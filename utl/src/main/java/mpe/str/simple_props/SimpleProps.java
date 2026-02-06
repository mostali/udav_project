package mpe.str.simple_props;

import mpu.str.JOIN;
import mpu.X;
import mpe.str.ARGS;
import mpc.str.sym.SYM;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpu.IT;

import java.util.*;
import java.util.stream.Collectors;

@Deprecated
public class SimpleProps extends SplitCommand implements IIProps {
	public static final Logger L = LoggerFactory.getLogger(SimpleProps.class);

	public final List<Prop> props = new ArrayList<>();
	public final String delimeterProp;
	public final String delimeterMain;

	@Override
	public List<String> keys() {
		return props.stream().map(e -> e.key()).filter(e -> e != null).collect(Collectors.toList());
	}

	public SimpleProps(Map map) {
		this(toStringProps(map, SYM.SCOLON, SYM.EQ), SYM.EQ);
	}

	@Override
	public String getValue(String key) {
		return getProp(key);
	}

	@Override
	public List<String> getValues(String key) {
		return getPropsSafe(key);

	}

	public Map<String, String> toMap() {
		Map<String, String> properties = new LinkedHashMap<>();
		for (Prop prop : props) {
			String key = prop.key();
			String value = prop.value();
			properties.put(key, value);
		}
		return properties;
	}

	public Properties toJavaProperties() {
		Properties properties = new Properties();
		for (Prop prop : props) {
			String key = prop.key();
			String value = prop.value();
			if (value == null) {
				if (X.empty(key)) {
					L.warn("Key&value is empty in pattern, because skip:" + super.original);
					continue;
				}
			}
			properties.setProperty(key, value);
		}
		return properties;
	}

	private static String toStringProps(Map<String, Object> map, String delimetrLine, String delimterProp) {
		StringBuilder sb = new StringBuilder();
		for (String k : map.keySet()) {
			Object v = map.get(k);
			if (v != null && v instanceof List) {
				for (Object o : (List) v) {
					sb.append(k).append(delimterProp).append(o == null ? "" : o).append(delimetrLine);
				}
			} else {
				sb.append(k).append(delimterProp).append(v == null ? SYM.EMPTY : v).append(delimetrLine);
			}
		}
		return sb.toString();
	}

	public SimpleProps(SimpleProps props) {
		this(props.props, props.delimeterMain, props.delimeterProp);
	}

	public SimpleProps(List<Prop> props, String delimterMain, String delimterProp) {
		this(toStringProps(props, delimterMain, delimterProp), delimterMain, delimterProp);
	}

	public SimpleProps(String gstring, String delimterProp) {
		this(gstring, SYM.SCOLON, delimterProp);
	}

	public SimpleProps(String gstring, String delimterMain, String delimterProp) {
		super(gstring, delimterMain, true);
		this.delimeterProp = delimterProp;
		this.delimeterMain = delimterMain;
		for (String prop : command()) {
			props.add(new Prop(prop, delimterProp));
		}
	}

	public SimpleProps(String gstring) {
		this(gstring, SYM.EQ);
	}

	public int asint(String name) throws SimplePropNotFoundException {
		try {
			return get_propint(name, null);
		} catch (Exception ex) {
			throw new SimplePropNotFoundException(name);

		}
	}

	public long aslong(String name) throws SimplePropNotFoundException {
		try {
			return get_proplong(name, null);
		} catch (Exception ex) {
			throw new SimplePropNotFoundException(name);
		}
	}

	public QDate asqdate(String prop) throws SimplePropNotFoundException {
		QDate safe = asqdateSafe(prop);
		if (safe == null) {
			throw new SimplePropNotFoundException(prop);
		}
		return safe;
	}

	public QDate asqdateSafe(String monodate14) throws SimplePropNotFoundException {
		try {
			return QDate.ofMono14(asstr(monodate14));
		} catch (Exception ex) {
			L.error(ex.getMessage());
			return null;
		}
	}

	public String asstr(String name) throws SimplePropNotFoundException {
		String src = getProp(name);
		if (src == null) {
			throw new SimplePropNotFoundException(name);
		}
		return src;
	}

	public String getPropRequired(String name) {
		String src = getProp(name);
		if (src == null) {
			throw new IllegalArgumentException("Key not found ::: " + name);
		}
		return src;
	}


	public int get_propint(String key, Integer def) {
		try {
			return Integer.parseInt(getProp(key));
		} catch (NumberFormatException ex) {
			return def;
		}
	}

	public long get_proplong(String key, Integer def) {
		try {
			return Long.parseLong(getProp(key));
		} catch (NumberFormatException ex) {
			return def;
		}
	}

	public float get_propfloat(String key, float def) {
		try {
			return Float.parseFloat(getProp(key));
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * return null, if not exist
	 */
	public List<String> getPropsSafe(String key) {
		List<String> args = null;
		for (Prop prop : props) {
			String[] two = prop.two();
			try {
				if (two[0].equals(key)) {
					if (args == null) {
						args = new ArrayList<>();
					}
					args.add(two[1]);
				}
			} catch (Exception ex) {
			}
		}
		return args;
	}

	public String getProp(String key) {
		return getProp(key, null);
	}

	public Integer getPropAsInt(String key, Integer def) {
		try {
			return Integer.parseInt(getProp(key));
		} catch (Exception ex) {
			return def;
		}
	}

	public String getProp(String key, String def) {
		for (Prop prop : props) {
			String[] two = prop.two();
			try {
				if (two[0].equals(key)) {
					return two[1];
				}
			} catch (Exception ex) {
			}
		}
		return def;
	}

	public SimpleProps set_prop(String key, String propValue) {
		if (key.contains(delimeterProp)) {
			throw new IllegalArgumentException("Key contains prop delimetr :" + key + " :" + delimeterProp);
		}
		for (Iterator iterator = props.iterator(); iterator.hasNext(); ) {
			Prop prop = (Prop) iterator.next();
			if (key.equals(prop.two()[0])) {
				iterator.remove();
			}
		}
		props.add(new Prop(key + delimeterProp + propValue, delimeterProp));
		return this;
	}

	public Integer paIntegerramProps(int index, Integer def) {
		return ARGS.argsAsInt(command(), index, def);
	}

	public static class Prop extends SplitCommand {

		public Prop(String gstring, String dkey) {
			super(gstring, dkey, true);
		}

		public String key() {
			try {
				return command()[0];
			} catch (Exception ex) {
				return null;
			}
		}

		public String value() {
			try {
				return command()[1];
			} catch (Exception ex) {
				return null;
			}
		}

		@Override
		public String toString() {
			return toStringProp();
		}

		public String toStringProp() {
			return super.original;
		}

		public String toStringProp(String del) {
			return SimpleProps.toStringProp(key(), value(), del);
		}
	}

	public static class SimplePropNotFoundException extends Exception {
		private static final long serialVersionUID = 1L;

		public SimplePropNotFoundException(String prop) {
			super("Error, prop :" + prop + " not found");
		}
	}

	public static SimpleProps ofSimpleJoin(String[] args) {
		return of(JOIN.argsBy(" ", args));
	}

	public static SimpleProps ofSafe(String string) {
		try {
			return new SimpleProps(string);
		} catch (Exception ex) {
			return null;
		}
	}

	public static SimpleProps of(String string) {
		return new SimpleProps(string);
	}

	public static SimpleProps ofJavaProp(String content) {
		return new SimpleProps(IT.notNull(content), SYM.NEWLINE, SYM.EQ);
	}

	public static String toStringProp(String key, String val, String del) {
		key = key == null ? "" : key;
		val = val == null ? "" : val;
		return key.concat(IT.notEmpty(del)).concat(val);
	}

	@Override
	public String toStringProps() {
		return toStringProps(props, delimeterMain, delimeterProp);
	}

	public static String toStringProps(List<Prop> props, String delimetrMain, String delimetrProp) {
		return props.stream().map(e -> e.toStringProp(delimetrProp)).collect(Collectors.joining(delimetrMain)); //+ " "
	}

	@Override
	public String toString() {
		return toStringProps();
	}

}
