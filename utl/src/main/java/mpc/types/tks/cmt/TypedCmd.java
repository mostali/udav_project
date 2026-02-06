package mpc.types.tks.cmt;

import mpu.core.ARG;
import mpu.core.ARR;
import mpu.IT;
import mpu.core.ENUM;
import mpu.core.EQ;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpu.str.TKN;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypedCmd<ECMD extends Enum> extends Cmd {

	private Map<ECMD, String> map;
	private List<String> skipPartIfNotExist;
	private String splitterRx;

	public static <E extends Enum> Map<E, String> explodeToMap(Class<E> type, String[] ms, String... skipPartIfNotExist) {
		Map<E, String> map = new LinkedHashMap<>();
//		List<String> skip = skipPartIfNotExist == null ? null : Stream.of(skipPartIfNotExist).map(e -> e.name()).collect(Collectors.toList());
		List<String> skip = skipPartIfNotExist == null ? null : ARR.as(skipPartIfNotExist);
nextPart:
		for (String part : ms) {
			String tk = TKN.first(part, " ", part);
			E val = (E) ENUM.valueOf(tk, type, true, null);
			if (val == null) {
				if (skip != null && skip.contains(tk)) {
					continue nextPart;
				}
				throw new FIllegalArgumentException("What is part '%s' ?", part);
			} else {
				map.put(val, part);
			}
		}
//		List<E> vls = EN.getValues(type);
		for (E key : type.getEnumConstants()) {
			if (!map.containsKey(key) && !skip.contains(key.name())) {
				throw new FIllegalStateException("Cmd illegal");
			}
		}
		return map;
	}

	public TypedCmd setSkipPartIfNotExist(List<String> skipPartIfNotExist) {
		this.skipPartIfNotExist = skipPartIfNotExist;
		return this;
	}

	public TypedCmd(String cmd, String splitter) {
		super(cmd);
		this.splitterRx = IT.NE(splitter);
	}

	public Map<ECMD, String> getMap(ECMD... ecmd) {
		return map != null ? map : (map = (Map<ECMD, String>) explodeToMap(ARG.toDefRq(ecmd).getClass(), super.original.split(splitterRx), this.skipPartIfNotExist.toArray(new String[0])));
	}

	public String getMapValue(ECMD eCmd) {
		return getMap(eCmd).get(eCmd);
	}

	//	public String getCmdNextStr(ECMD eCmd, boolean trim, boolean... ignoreCase) {
//		Cmd7 cmd7 = getPartAsCmd7(eCmd);
//		String cmd = USToken.next(cmd7.original, eCmd.name(), ARG.isPredicatEqTrue(ignoreCase));
//		if (trim) {
//			cmd = cmd.trim();
//		}
//		return cmd;
//	}
	public boolean hasKey(ECMD key, boolean... ignoreCase) {
		return hasKey(key.name(), ignoreCase);
	}

	public boolean hasKey(String key, boolean... ignoreCase) {
		try {
			getCmdStr(key, ARG.isDefEqTrue(ignoreCase), false, false);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getCmdStr(ECMD key, boolean ignoreCase, boolean next, boolean trim, String... defRq) {
		return getCmdStr(key.name(), ignoreCase, next, trim, defRq);
	}

	public void removeCmd(String key, boolean ignoreCase, boolean... skipErrorIfNotExist) {
		Map.Entry<ECMD, String> e = null;
		try {
			e = getCmdEntry(key, ignoreCase);
			getMap().remove(e.getKey());
		} catch (Exception ex) {
			if (ARG.isDefEqTrue(skipErrorIfNotExist)) {
				return;
			}
			throw ex;
		}
	}

	public String getCmdStr(String key, boolean ignoreCase, boolean next, boolean trim, String... defRq) {
		Map.Entry<ECMD, String> e = null;
		try {
			e = getCmdEntry(key, ignoreCase);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
		String vl = e.getValue();
		if (next) {
			vl = TKN.startWith(vl, key, ignoreCase);
		}
		if (trim) {
			vl = vl.trim();
		}
		return vl;
	}

	public Map.Entry<ECMD, String> getCmdEntry(String key, boolean ignoreCase, Map.Entry<ECMD, String>... defRq) {
		for (Map.Entry<ECMD, String> e : getMap().entrySet()) {
			if (EQ.equalsString(e.getKey().name(), key, ignoreCase, true)) {
				return e;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Entry '%s' (ic=%s) not found from typed-map", key, ignoreCase);
	}

	public Cmd7<String, String, String, String, String, String, String> getPartAsCmd7(ECMD eCmd) {
		return getPartAs(eCmd, Cmd7.class);
	}


	public <CMD extends Cmd> CMD getPartAs(ECMD eCmd, Class<CMD> toCmdClass) {
		return ofAs(getMapValue(eCmd), toCmdClass);
	}

	public static <ECMD extends Enum> String toStringFromMap(TypedCmd<ECMD> typedCmd, String splitter) {
		ECMD[] vls = (ECMD[]) typedCmd.getMap().keySet().toArray(new Enum[0]);
		ECMD[] full = (ECMD[]) vls[0].getClass().getEnumConstants();
//		String collect = Stream.of(vls).filter(e -> typedCmd.getMapValue(e) != null).map(e -> (full[0] == e ? "" : splitter) + typedCmd.getMapValue(e)).collect(Collectors.joining(" "));
		String collect = Stream.of(vls).map(e -> (full[0] == e ? "" : splitter) + typedCmd.getMapValue(e)).collect(Collectors.joining(" "));
		return collect;
	}
}
