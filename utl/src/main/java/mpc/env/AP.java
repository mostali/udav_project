package mpc.env;

import com.google.common.primitives.Primitives;
import lombok.SneakyThrows;
import mpc.env.boot.AppBoot;
import mpu.core.ARG;
import mpu.core.ARR;
import mpe.core.U;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.fs.fd.FILE;
import mpc.fs.fd.RES;
import mpc.log.L;
import mpc.map.UMap;
import mpc.rfl.RFL;
import mpc.types.ruprops.URuProps;
import mpu.str.STR;
import mpu.str.UST;
import mpu.str.USToken;
import mpc.map.ManyMap;
import mpu.X;

import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

//application.properties
public class AP {

	public static Map<String, String> map0;
	public static Map<String, String> map_profile;


	static {
		X.p("RL:"+Paths.get("").toAbsolutePath());
		checkAndDeleteMe();
	}


	public static void deleteMe() {
		UFS_BASE.RM.removeFileQk(Env.FILE_APPLICATION_PROPERTIES);
	}

	public static void reinitCache(String... profiles) {
		clearCache();
		AppProfile profile = AppProfile.getFirstUseful(profiles, null);
		AppBoot.bootLog("AP Cache reinit with profile '{}'", profile);
	}

	public static void clearCache() {
		map0 = null;
		map_profile = null;
	}

	/**
	 * *************************************************************
	 * ---------------------------- Get Map  -----------------------
	 * *************************************************************
	 */

	public static Map<String, String> getMap() {
		AppProfile firstUseful = AppProfile.getFirstUseful();
		Map<String, String> mapPRIMARY = getMap(firstUseful);
		if (firstUseful == null) {
			return mapPRIMARY;
		}
		Map<String, String> map0 = getMap(null);
		return ManyMap.of(map0, mapPRIMARY);

	}

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
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Application.Properties not found from resources&run.location");
	}


	@SneakyThrows
	public static Map<String, String> getApplicationProperties_RL(AppProfile appProfile, Map... defRq) {
		FILE of = FILE.of(toFilenameWithProfile(appProfile));
		if (of.exist()) {
			return URuProps.getRuPropertiesClassic(of.readLines());
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Application.Properties not found from run.location");
	}

	@SneakyThrows
	public static Map<String, String> getApplicationProperties_RSRC(AppProfile appProfile, Map... defRq) {
		String cnt = RES.of(Env.class, "/" + toFilenameWithProfile(appProfile)).cat(null);
		if (cnt != null) {
			return URuProps.getRuPropertiesClassic(ARR.as(cnt.split("\\n")));
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Application.Properties not found from resources");
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
		UFS.COPY.CopyOpt copyOpt = replace ? UFS_BASE.COPY.CopyOpt.FD_REPLACE_IF_EXIST : UFS_BASE.COPY.CopyOpt.FD_SKIP_IF_EXIST;
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

	@SneakyThrows
	private static String getPropertyFromAppData(String key) {
		FILE apFile = FILE.of(APP.getAppDataDir().resolve(Env.FILE_APPLICATION_PROPERTIES));
		if (!apFile.exist()) {
			return null;
		}
		Map ruPropertiesClassic = URuProps.getRuPropertiesClassic(apFile.readLines());
		return (String) ruPropertiesClassic.get(key);
	}

	public static String get(AppProfile appProfile, String key, String... defRq) {
		Map<String, String> map = getMap(appProfile, null);
		if (map != null) {
			return UMap.get(map, key, defRq);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Property '%s' not found. Resource application.properties not found from resources | run.location", key);
	}

	public static <T> T getAs(String key, Class<T> asType, T... defRq) {
		String val = getPropertyFromAppData(key);
		if (val != null) {
			return UST.strTo(val, asType, defRq);
		}
		AppProfile appProfile = AppProfile.getFirstUseful();
		T t;
		if (appProfile == null) {
			return getAs(null, key, asType, defRq);
		}
		t = getAs(appProfile, key, asType, null);
		if (t != null) {
			return t;
		}
		return getAs(null, key, asType, defRq);
	}

	public static <T> T getAs(AppProfile appProfile, String key, Class<T> asType, T... defRq) {
		Map map = getMap(appProfile, null);
		if (map != null) {
			return (T) UMap.getAs(map, key, asType, defRq);
		}
		return ARG.toDefRq(defRq);
	}

	public static String getValueWoDef(String apPropValue) {
		if (apPropValue != null && apPropValue.startsWith("${") && apPropValue.endsWith("}")) {
			apPropValue = STR.cutFirstLast(apPropValue, "${", "}");
			apPropValue = USToken.last(apPropValue, ':');
		}
		return apPropValue;
	}

	public static class AutoInit {

		public static final Class<AVI> AVI_CLASS = AVI.class;

		@SneakyThrows
		public static void initClass(Class clazz) {
			List<Field> values = RFL.fields(clazz, ARR.as(AVI_CLASS));
			for (Field field : values) {
				field.setAccessible(true);
				AVI avi = field.getAnnotation(AVI_CLASS);
				setValueFromAP(field, avi);
			}
		}

		public static void setValueFromAP(Field field, AVI avi) throws IllegalAccessException {

			String prop = avi.prop();
			Class anoType = avi.type();

			if (anoType != field.getType()) {
				throw new FIllegalArgumentException("Type of field '%s' is defference from avi-annotation '%s'", field.getType(), anoType);
			}

			String strVl = AP.getAs(prop, String.class, null);
			strVl = getValueWoDef(strVl);

			setFieldValue(field, avi, strVl);
		}

		public static void setValue(Class clazz, String property, String value) throws IllegalAccessException {
			List<Field> values = RFL.fields(clazz, ARR.as(AVI_CLASS));
			for (Field field : values) {
				field.setAccessible(true);
				AVI avi = field.getAnnotation(AVI_CLASS);
				if (avi.prop().equals(property)) {
					setFieldValue(field, avi, value);
				}
			}
		}


		private static void setFieldValue(Field field, AVI avi, String newValue) throws IllegalAccessException {

			String prop = avi.prop();
			Class anoType = avi.type();
			String defVal = avi.def();

			if (newValue == null) {
				if (U.__NULL__.equals(defVal)) {
					//ok, set null
				} else if (X.empty(defVal)) {
					throw new FIllegalArgumentException("AP value '%s' is null & AVI 'defVal' is empty", prop);
				} else {
					newValue = defVal;
				}
			}

			Object objVl = newValue == null ? null : UST.strTo(newValue, anoType);

			boolean isWrapperType = Primitives.isWrapperType(field.getType());
			if (objVl == null && isWrapperType) {
				throw new FIllegalArgumentException("Field '%s' is primitive. But value is null", field);
			}

			if (objVl != null && objVl instanceof CharSequence) {
				objVl = AppPH.replaceAppPlaceholders(objVl.toString());
			}

			field.set(null, objVl);
		}


		public static class AppPH {
			public static final String RPA_PLACEHOLDER = "{{RPA}}";

			private static String replaceAppPlaceholders(String newValue) {
				if (X.empty(newValue)) {
					return newValue;
				}
				if (newValue.contains(RPA_PLACEHOLDER)) {
					newValue = newValue.replace(RPA_PLACEHOLDER, Env.RPA.toString());
				}
				return newValue;
			}
		}

	}
}
