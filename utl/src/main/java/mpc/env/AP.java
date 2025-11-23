package mpc.env;

import lombok.SneakyThrows;
import mpc.env.boot.AppBoot;
import mpu.core.ARG;
import mpu.core.ARR;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpc.fs.fd.FILE;
import mpc.fs.fd.RES;
import mpc.log.L;
import mpc.map.MAP;
import mpc.types.ruprops.URuProps;
import mpu.str.STR;
import mpu.str.UST;
import mpu.str.TKN;
import mpu.X;

import java.nio.file.Paths;
import java.util.Map;

//application.properties
public class AP {

	public static Map<String, String> appAP;
	public static Map<String, String> map0;
	public static Map<String, String> map_profile;


	static {
		X.p("RL:" + Paths.get("").toAbsolutePath());
		checkAndDeleteMe();
	}


	public static void deleteMe() {
		UFS.RM.fileQk(Env.FILE_APPLICATION_PROPERTIES);
	}

	public static void reinitCache(String... profiles) {
		clearCache();
		AppProfile profile = AppProfile.getFirstUseful(profiles, null);
		AppBoot.bootLog("AP Cache reinit with profile '{}'", profile);
	}

	public static void clearCache() {
		appAP = null;
		map0 = null;
		map_profile = null;
	}

	/**
	 * *************************************************************
	 * ---------------------------- Get Map  -----------------------
	 * *************************************************************
	 */

//	public static Map<String, String> getMap() {
//		AppProfile firstUseful = AppProfile.getFirstUseful();
//		Map<String, String> mapPRIMARY = getMap(firstUseful);
//		if (firstUseful == null) {
//			return mapPRIMARY;
//		}
//		Map<String, String> map0 = getMap(null);
//		return ManyMap.of(map0, mapPRIMARY);
//	}
	public static Map<String, String> getMap(AppProfile appProfile, Map... defRq) {
		if (appProfile == null) {
			if (map0 != null) {
				return map0;
			}
		} else if (map_profile != null) {
			return map_profile;
		}
		//try get default AP from RL
		Map<String, String> map = getApplicationProperties_RL(null, null);
		if (map != null) {
			return appProfile == null ? AP.map0 = map : (AP.map_profile = map);
		}
		//try get profile AP* from RL
		map = getApplicationProperties_RL(appProfile, null);
		if (map != null) {
			return appProfile == null ? AP.map0 = map : (AP.map_profile = map);
		}
		//try get profile AP* from RSRC
		map = getApplicationProperties_RSRC(appProfile, null);
		if (map != null) {
			return appProfile == null ? AP.map0 = map : (AP.map_profile = map);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Application.Properties not found from resources&run.location"), defRq);
	}


	@SneakyThrows
	public static Map<String, String> getApplicationProperties_RL(AppProfile appProfile, Map... defRq) {
		FILE of = FILE.of(toFilenameWithProfile(appProfile));
		if (of.fdExist()) {
			return URuProps.getRuPropertiesClassic(of.readLines());
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Application.Properties not found from run.location"), defRq);
	}

	@SneakyThrows
	public static Map<String, String> getApplicationProperties_RSRC(AppProfile appProfile, Map... defRq) {
		String cnt = RES.of(Env.class, "/" + toFilenameWithProfile(appProfile)).cat(null);
		if (cnt != null) {
			return URuProps.getRuPropertiesClassic(ARR.as(cnt.split("\\n")));
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Application.Properties not found from resources"), defRq);
	}

	public static String toFilenameWithProfile(AppProfile appProfile) {
		return appProfile == null ? Env.FILE_APPLICATION_PROPERTIES : "application-" + appProfile.name().replace("_", "-") + ".properties";
	}

	public static Boolean isRmOnStart() {
		return getAs("app.start.rm.ap", Boolean.class, false);
	}

	@SneakyThrows
	public static void copyToRunLocation(Class src) {
		Boolean replace = AP.isRmOnStart();
		UFS.COPY.CopyOpt copyOpt = replace ? UFS.COPY.CopyOpt.FD_REPLACE_IF_EXIST : UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST;
		RES.of(src, "/" + Env.FILE_APPLICATION_PROPERTIES, true).copyToRunLocation_(copyOpt);
	}

	@SneakyThrows
	public static void checkAndDeleteMe() {
		Boolean rmOnStart = isRmOnStart();
		if (rmOnStart) {
			deleteMe();
		}
		if (L.isInfoEnabled()) {
			L.info("AP {} rmOnStart:{}", Paths.get(Env.FILE_APPLICATION_PROPERTIES).toAbsolutePath(), rmOnStart);
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- Get -----------------------
	 * *************************************************************
	 */
	public static String[] getMany(String... keys) {
		return getMany(keys, new String[0]);
	}

	public static String[] getMany(String[] keys, String[]... defRq) {
		AppProfile appProfile = AppProfile.getFirstUseful();
		String[] t;
		if (appProfile == null) {
			return getMany(null, keys, defRq);
		}
		t = getMany(appProfile, keys, null);
		if (t != null) {
			return t;
		}
		return getMany(null, keys, defRq);
	}

	public static String[] getMany(AppProfile appProfile, String[] keys, String[]... defRq) {
		String[] vls = new String[keys.length];
		Exception ex0 = null;
		for (int i = 0; i < keys.length; i++) {
			try {
				vls[i] = get(appProfile, keys[i]);
			} catch (Exception ex) {
				ex0 = ex;
				break;
			}
		}
		if (ex0 == null) {
			return vls;
		}
		return ARG.toDefThrow(ex0, defRq);
	}

	public static <T> T getAs(String key, Class<T> asType, T... defRq) {
		String vl = get(key, null);
		if (vl != null) {
			return UST.strTo(vl, asType, defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("App Property '%s' not found.", key), defRq);
	}

	public static String get(String key, String... defRq) {
		String val = getPropertyFromAppData(key);
		if (val != null) {
			return val;
		}
		AppProfile appProfile = AppProfile.getFirstUseful();
		String t;
		if (appProfile == null) {
			return get(null, key, defRq);
		}
		t = get(appProfile, key, null);
		if (t != null) {
			return t;
		}
		return get(null, key, defRq);
	}


	public static String get(AppProfile appProfile, String key, String... defRq) {
		return get(appProfile, key, null, defRq);
	}

	public static <T> T getAs(AppProfile appProfile, String key, Class<T> asType, T... defRq) {
		return get(appProfile, key, asType, defRq);
	}

	private static <T> T get(AppProfile appProfile, String key, Class<T> asType, T... defRq) {
		Map<String, String> map = getMap(appProfile, null);
		if (map != null) {
			return asType == null ? (T) MAP.get((Map) map, key, defRq) : (T) MAP.getAs(map, key, asType, defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("App Property '%s' not found.", key), defRq);
	}

	//
	//

	@SneakyThrows
	public static Map<String, String> getApplicationProperties_APP(boolean fresh, Map<String, String> defIfNotExist_setNNForCache) {
		if (appAP == null || ARG.isDefEqTrue(fresh)) {
			FILE apFile = FILE.of(APP.LOCATION.getAppDataDirOrDef().resolve(Env.FILE_APPLICATION_PROPERTIES));
			if (!apFile.fdExist()) {
				return appAP = defIfNotExist_setNNForCache;
			}
			appAP = URuProps.getRuPropertiesClassic(apFile.readLines());
		}
		return appAP;
	}

	@SneakyThrows
	private static String getPropertyFromAppData(String key) {
		return (String) getApplicationProperties_APP(false, ARR.EMPTY_MAP).get(key);
	}

	public static String getValueWoDef(String apPropValue) {
		if (apPropValue != null && apPropValue.startsWith("${") && apPropValue.endsWith("}")) {
			apPropValue = STR.substrBetweenStartEnd(apPropValue, "${", "}");
			apPropValue = TKN.last(apPropValue, ':');
		}
		return apPropValue;
	}

}
