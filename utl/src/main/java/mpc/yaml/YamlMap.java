package mpc.yaml;

import lombok.SneakyThrows;
import mpc.map.IGetterAs;
import mpu.core.ARG;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpu.core.ARR;
import mpu.core.RW;
import mpc.fs.UFS;
import mpc.fs.fd.DIR;
import mpc.fs.fd.RES;
import mpc.map.MAP;
import mpc.rfl.IRfl;
import mpc.str.ObjTo;
import mpu.X;
import mpu.pare.Pare;
import mpu.pare.Tuple;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class YamlMap<V> implements Map<String, V>, Serializable, IRfl, IGetterAs {

	public static void main(String[] args) {
//		YamlMap yamlMap = YamlMap.of("key: val"); // from yaml string
//		X.exit(yamlMap.toStringPrettyYaml());
		YamlMap yamlMap1 = YamlMap.of(Map.of("k1", "v1", "k2", Map.of("k3", Arrays.asList(1, 2, 3)))); // from Map
		X.exit(yamlMap1.toStringPrettyYaml());
		YamlMap yamlMap2 = YamlMap.of(Paths.get("file.yaml"), true); // from new file
		YamlMap yamlMap3 = YamlMap.ofObj(new Pare("key", "val")); // from pojo

		String prettyYaml = yamlMap1.toStringPrettyYaml();
	}

	public static final Logger L = LoggerFactory.getLogger(YamlMap.class);
	public static final String EMPTYFILE = "{}";
	public static final YamlMap EMPTYMAP = new YamlMap();

	protected final String key;
	protected final Map<String, V> map;

	public YamlMap() {
		this(newEmptyMap());
	}

	public YamlMap(Map<String, V> map) {
		this(null, map);
	}

	public YamlMap(String key, Map<String, V> map) {
		this.key = key;
		this.map = map;
	}

	// ==========================================
	// YAML Utils (Замена UGson для SnakeYAML 2.2)
	// ==========================================
	private static class YamlUtils {
		private static Yaml getYaml(boolean pretty) {
			LoaderOptions loaderOptions = new LoaderOptions();
			DumperOptions dumperOptions = new DumperOptions();

			dumperOptions.setDefaultFlowStyle(pretty ? DumperOptions.FlowStyle.BLOCK : DumperOptions.FlowStyle.FLOW);
			dumperOptions.setPrettyFlow(pretty);

			SafeConstructor constructor = new SafeConstructor(loaderOptions);
			Representer representer = new Representer(dumperOptions);
			representer.setDefaultFlowStyle(dumperOptions.getDefaultFlowStyle());

			return new Yaml(constructor, representer, dumperOptions);
		}

		public static String toStringYaml(Map<?, ?> map, boolean pretty) {
			return getYaml(pretty).dumpAsMap(map);
		}

		public static Map<String, Object> toMapFromString(String yamlStr) {
			if (yamlStr == null || yamlStr.trim().isEmpty()) {
				return new LinkedHashMap<>();
			}
			Object loaded = getYaml(false).load(yamlStr);
			if (loaded instanceof Map) {
				return (Map<String, Object>) loaded;
			}
			return new LinkedHashMap<>();
		}

		public static String toStringYaml_FromObject(Object obj, boolean pretty) {
			return getYaml(pretty).dumpAsMap(obj);
		}

		public static void createEmptyYamlFile(Path file) {
			try {
				UFS.MKFILE.createFileIfNotExistWithContentMkdirs(file, EMPTYFILE);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	// ==========================================
	// Static Factory & Conversion Methods
	// ==========================================

	public static String toStringFromMap(Map<String, String> yaml) {
		return YamlUtils.toStringYaml(yaml, true);
	}

	public static Object checkAndConvertIfMap(Object data) {
		if (!(data instanceof Map)) {
			return data;
		} else if (data instanceof YamlMap) {
			return data;
		} else {
			return YamlMap.of((Map) data);
		}
	}

	public static YamlMap toMapFromObj(Object yamlObject) {
		Map mapFromObject = YamlUtils.toMapFromString(YamlUtils.toStringYaml_FromObject(yamlObject, false));
		return YamlMap.of(mapFromObject);
	}

	public static YamlMap ofObj(Object any) {
		return YamlMap.of(YamlUtils.toStringYaml_FromObject(any, true));
	}

	@Override
	public String toString() {
		return YamlUtils.toStringYaml(this, true);
	}

	@SneakyThrows
	public static YamlMap read(Path file, boolean... createIfNotExist) {
		if (ARG.isDefEqTrue(createIfNotExist)) {
			UFS.MKFILE.createFileIfNotExistWithContentMkdirs(file, EMPTYFILE);
		}
		return of(RW.readString(file));
	}

	@SneakyThrows
	public static YamlMap read(Path file, Charset charset, boolean... createIfNotExist) {
		if (ARG.isDefEqTrue(createIfNotExist)) {
			UFS.MKFILE.createFileIfNotExistWithContentMkdirs(file, EMPTYFILE);
		}
		return of(RW.readContent_(file, charset));
	}

	@SneakyThrows
	public static void write(Path file, YamlMap ymap) {
		write(file, ymap, false, false);
	}

	@SneakyThrows
	public static void write(Path file, YamlMap ymap, boolean createIfNotExist) {
		write(file, ymap, false, createIfNotExist);
	}

	@SneakyThrows
	public static void write(Path file, YamlMap ymap, boolean pretty, boolean createIfNotExist) {
		if (ARG.isDefEqTrue(createIfNotExist)) {
			UFS.MKFILE.createFileIfNotExist_(file);
		}
		String content = YamlUtils.toStringYaml(ymap.map(), pretty);
		RW.write(file, content);
		if (L.isDebugEnabled()) {
			L.debug("Write file://{} with content*{}\n{}", file, X.sizeOf(content), content);
		}
	}

	public static boolean isEmpty(YamlMap yamlMap) {
		return yamlMap == null || yamlMap.isEmpty();
	}

	public static YamlMap of(Path file, boolean createFile) {
		if (createFile) {
			YamlUtils.createEmptyYamlFile(file);
		}
		return of(file);
	}

	public String toStringYaml(boolean... pretty) {
		boolean isPretty = ARG.isDefEqTrue(pretty);
		if (key != null) {
			return YamlUtils.toStringYaml(MAP.of(key, map), isPretty);
		}
		return YamlUtils.toStringYaml(map, isPretty);
	}

	public String toStringPrettyYaml() {
		String stringYaml = toStringYaml(true);
		Map stringYaml2 = YamlUtils.toMapFromString(stringYaml);
		return YamlUtils.toStringYaml(stringYaml2, true);
	}

	public Map<String, V> map() {
		return map;
	}

	public static Map toMapFromString(CharSequence yaml, Map... defRq) {
		return YamlUtils.toMapFromString(yaml.toString());
	}

	public static YamlMap ofKV(Object... keyValues) {
		return new YamlMap(MAP.of(keyValues));
	}

	public static YamlMap of(Map map) {
		return new YamlMap(map);
	}

	@SneakyThrows
	public static YamlMap of(Path fileYaml, YamlMap... defRq) {
		try {
			if (UFS.isFileWithContent(fileYaml)) {
				String content = RW.readString(fileYaml);
				return new YamlMap(YamlUtils.toMapFromString(content));
			}
			throw new FileNotFoundException(fileYaml.toString());
		} catch (Exception ex) {
			return ARG.throwErr(() -> ex instanceof RequiredRuntimeException ? (RequiredRuntimeException) ex : new RequiredRuntimeException(ex, "Error parse file with yaml: %s", fileYaml), defRq);
		}
	}

	public static YamlMap of(String yaml, YamlMap... defRq) {
		try {
			return new YamlMap(YamlUtils.toMapFromString(yaml));
		} catch (Exception ex) {
			return ARG.throwErr(() -> ex instanceof RequiredRuntimeException ? (RequiredRuntimeException) ex : new RequiredRuntimeException(ex, "Error parse yaml string: %s", yaml), defRq);
		}
	}

	// ==========================================
	// Map Interface Implementation
	// ==========================================

	@Override
	public int size() {
		return map().size();
	}

	public boolean isEmpty() {
		return X.empty(map());
	}

	@Override
	public boolean containsKey(Object key) {
		return map().containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map().containsValue(value);
	}

	@Override
	public V get(Object key) {
		return get(key, null);
	}

	public V get(Object key, V... defRq) {
		V v = map.get(key);
		if (v != null || containsKey(key)) {
			return v;
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("YamlMap Value by key '%s' not found", key), defRq);
	}

	public <T> T getAs(String key, Class<T> asType, T... defRq) {
		try {
			V v = get(key);
			return ObjTo.objTo(v, asType);
		} catch (Exception ex) {
			return ARG.throwErr(() -> new RequiredRuntimeException("YamlMap Value typeof '%s' by key '%s' not found", asType, key), defRq);
		}
	}

	public String getAsString(Object key, String... defRq) {
		Object vl = get(key, null);
		if (vl != null) {
			return vl.toString();
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("YamlMap Value by key '%s' not found", key), defRq);
	}

	public YamlMap getAsYamlMapOrCreate(String key) {
		YamlMap child = getAsYamlMap(key, null);
		if (child != null) {
			return child;
		}
		child = newEmpty(true);
		put(key, (V) child.map);
		return child;
	}

	public List<YamlMap> getAsArrayYamlMap(String key, List<YamlMap>... defRq) {
		List child = (List) get(key, null);
		if (child != null) {
			return (List<YamlMap>) child.stream().map(m -> YamlMap.of((Map) m)).collect(Collectors.toList());
		}
		return child != null ? child : ARG.throwErr(() -> new RequiredRuntimeException("getAsArrayYamlMap by key '%s' not found", key), defRq);
	}

	public List getAsArray(String key, List... defRq) {
		List child = (List) get(key, null);
		if (child != null) {
			return child;
		}
		return child != null ? child : ARG.throwErr(() -> new RequiredRuntimeException("getAsArrayYamlMap by key '%s' not found", key), defRq);
	}

	public YamlMap getAsYamlMap(String key, YamlMap... defRq) {
		YamlMap child = getAs(key, YamlMap.class, null);
		if (child != null) {
			return child;
		}
		return child != null ? newEmpty(true) : ARG.throwMsg(() -> X.f("YamlMap not found value typeof '%s' by key '%s' not found", YamlMap.class, key), defRq);
	}

	public static YamlMap of(String key, Map yaml) {
		return new YamlMap(key, yaml);
	}

	public static YamlMap of(Class rsrsClass, String rsrcPath) {
		String yamlStr = RES.of(rsrsClass, rsrcPath, DIR.class).cat();
		return new YamlMap(YamlUtils.toMapFromString(yamlStr));
	}

	@Override
	public V put(String key, V value) {
		boolean isYM = value instanceof YamlMap;
		IT.state(!isYM, "fuse ym");
		if (value instanceof Integer || value instanceof Long) {
			Number n = (Number) value;
			value = (V) (Double) n.doubleValue();
		}
		V put = map().put(key, value);
		return put;
	}

	@Override
	public V remove(Object key) {
		return map().remove(key);
	}

	@Override
	public void putAll(@NotNull Map m) {
		map().putAll(m);
	}

	@Override
	public void clear() {
		map().clear();
	}

	@Override
	public Set keySet() {
		return map().keySet();
	}

	@NotNull
	@Override
	public Collection<V> values() {
		return map().values();
	}

	@NotNull
	@Override
	public Set<Entry<String, V>> entrySet() {
		return map().entrySet();
	}

	public boolean hasAny(Object... keys) {
		for (Object key : keys) {
			if (map().containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasAll(Object... keys) {
		if (X.empty(keys)) {
			return false;
		}
		for (Object key : keys) {
			if (!map().containsKey(key)) {
				return false;
			}
		}
		return true;
	}

	public YamlMap child(String key, YamlMap... defRq) {
		YamlMap childYaml = getAsYamlMap(key, null);
		return childYaml != null ? childYaml : ARG.throwErr(() -> new RequiredRuntimeException("ChildYaml '%s' not found", key), defRq);
	}

	public YamlMap childOrCreate(String key) {
		return getAsYamlMapOrCreate(key);
	}

	protected YamlMap newChild() {
		return new ChildYamlMap(this, key, YamlMap.newEmptyMap());
	}

	public static Map newEmptyMap() {
		return new LinkedHashMap();
	}

	protected YamlMap newEmpty(boolean... withParent) {
		YamlMap parent = this;
		return ARG.isDefEqTrue(withParent) ? new YamlMap() {
			@Override
			protected YamlMap parent() {
				return parent;
			}
		} : null;
	}

	protected YamlMap parent() {
		return null;
	}

	public Tuple getTuple(String... props) {
		return Tuple.ofMap(this, props);
	}

	public static class ChildYamlMap extends YamlMap {
		private final YamlMap parent;

		public ChildYamlMap(YamlMap parent, String key, Map childYaml) {
			super(key, childYaml);
			this.parent = parent;
		}

		@Override
		protected YamlMap parent() {
			return parent;
		}
	}
}