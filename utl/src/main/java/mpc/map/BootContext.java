package mpc.map;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import mpc.arr.STREAM;
import mpc.env.AP;
import mpc.env.APP;
import mpc.env.AppProfile;
import mpc.env.Env;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.fd.RES;
import mpc.types.opts.SeqOptions;
import mpc.types.ruprops.RuProps;
import mpc.types.ruprops.URuProps;
import mpf.contract.IContractBuilder;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.core.EQ;
import mpu.func.Function2;
import mpu.func.Function3;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.SPL;
import mpu.str.SPLIT;
import mpu.str.UST;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public class BootContext implements IContractBuilder, IGetterAs {

	private static final String APK_RC_FOPTS = "rc_fopts";
	private static final String APK_RC_FMAP = "rc_fmap";

	public static Function2<String, String[], String> bootValue = (value, defRq) -> {
		if (!value.startsWith("${")) {
			return value;
		}

		Pare<String, Optional<String>> unwrap = SPL.unwrap(value);

		//if placeholder wo default value
		if (!unwrap.hasVal()) {
			return get().get(unwrap.key(), defRq);
		}

		boolean isInited = get() != null;
		if (!isInited) {
			init(ARR.EMPTY_ARGS);
		}

		String bootval = get().get(unwrap.key(), null);
		if (bootval != null) {
			return bootval;
		}
		//value not found, check default value
//		if (ARG.isDef(defRq)) {
//			return ARG.toDef(defRq);
//		}
		return unwrap.val().get();
	};

	public final String[] runAppArgs;

	private final List<Pare3<ApType, String, Object>> initAppLocEntitys;

	public static BootContext INSTANCE = null;

	public static BootContext get() {
		return INSTANCE;
	}

	public BootContext(String[] runAppArgs, List<Pare3<ApType, String, Object>> initAppLocEntitys) {
		this.runAppArgs = runAppArgs;
		this.initAppLocEntitys = initAppLocEntitys;
		INSTANCE = this;
	}

	private static int localMapIndex = 0;

	public static String getKey(String key, String... defRq) {
		return INSTANCE != null ? INSTANCE.get(key, defRq) : ARG.toDefThrowMsg(() -> X.f("BC Key '%s' not found", key), defRq);
	}


	public void addMapLocal(Map localMap, Integer... index) {
		addApLoc(Pare3.of(ApType.LOCAL_MAP, localMapIndex++ + "", localMap), index);
		reset();
	}

	public void addMap(Path fileWithMap, Integer... index) {
		IT.isFileExist(fileWithMap);
		addApLoc(Pare3.of(ApType.FILE_MAP, fileWithMap.toString(), null), index);
		reset();
	}

	public void addOpts(Path fileWithOpts, Integer... index) {
		IT.isFileExist(fileWithOpts);
		addApLoc(Pare3.of(ApType.FILE_OPTS, fileWithOpts.toString(), null), index);
		reset();
	}

	private void addApLoc(Pare3<ApType, String, Object> apLoc, Integer... index) {
		IT.stateNot(initAppLocEntitys.contains(apLoc));
		if (ARG.isDef(index)) {
			initAppLocEntitys.add(ARG.toDef(index), apLoc);
		} else {
			initAppLocEntitys.add(apLoc);
		}
	}

	public BootContext reset() {
		_bootMap = null;
		_fullMap = null;
		_cacheAP = null;
		return this;
	}


	@Override
	public Map getContractDataMap() {
		return asMap();
	}

	public static void init(String[] args, Map<String, String>... withMaps) {
		ofAll(args, withMaps);
	}

	public static BootContext ofAll(String[] args, Map<String, String>... withMaps) {
		return of(args, true, true, true, true, withMaps);
	}

	public static BootContext of(String[] args, boolean includeSys, boolean includeEnv, boolean includeApAllPlaces, boolean checkProfile, Map<String, String>[] withMaps) {
		String[] profiles = null;
		out:
		if (checkProfile) {
			String profilesStr = System.getProperty(AppProfile.PROFILES_ACTIVE);
			if (profilesStr != null) {
				profiles = SPLIT.argsByComma(profilesStr);
			}
		}
		return of(args, includeSys, includeEnv, includeApAllPlaces, profiles, withMaps);
	}

	public static BootContext of(String[] args, boolean includeSys, boolean includeEnv, boolean includeApAllPlaces, String[] withProfiles, Map<String, String>[] localMaps) {
		List<Pare3<ApType, String, Object>> apTypes = new ArrayList<>();
		{
			apTypes.add(Pare3.of(ApType.RUN_OPTS, null, false));
		}
		if (includeSys) {
			apTypes.add(Pare3.of(ApType.SYS, null, false));
		}
		if (includeEnv) {
			apTypes.add(Pare3.of(ApType.ENV, null, false));
		}
		if (X.notEmpty(localMaps)) {
			for (Map<String, String> withMap : localMaps) {
				apTypes.add(Pare3.of(ApType.LOCAL_MAP, localMapIndex++ + "", withMap));
			}
		}
		if (includeApAllPlaces) {
			apTypes.add(Pare3.of(ApType.APPDIR, Env.FILE_APPLICATION_PROPERTIES, false));
			apTypes.add(Pare3.of(ApType.RL, Env.FILE_APPLICATION_PROPERTIES, false));
			apTypes.add(Pare3.of(ApType.RSRC, Env.FILE_APPLICATION_PROPERTIES, false));
		}
		if (X.notEmpty(withProfiles)) {
			AppProfile[] appProfiles = AppProfile.valuesOf(withProfiles, true);
			//		for (AppProfile appProfile : appProfiles) {
			//			apTypes.add(Pare3.of(ApType.APPDIR, AP.toFilenameWithProfile(appProfile), false));
			//			apTypes.add(Pare3.of(ApType.RL, AP.toFilenameWithProfile(appProfile), false));
			//			apTypes.add(Pare3.of(ApType.RSRC, AP.toFilenameWithProfile(appProfile), false));
			//		}
			for (AppProfile appProfile : appProfiles) {
				apTypes.add(Pare3.of(ApType.APPDIR, AP.toFilenameWithProfile(appProfile), false));
			}
			for (AppProfile appProfile : appProfiles) {
				apTypes.add(Pare3.of(ApType.RL, AP.toFilenameWithProfile(appProfile), false));
			}
			for (AppProfile appProfile : appProfiles) {
				apTypes.add(Pare3.of(ApType.RSRC, AP.toFilenameWithProfile(appProfile), false));
			}
		}
		Pare3[] array = apTypes.toArray(new Pare3[apTypes.size()]);
		return of(args, array);
	}

	public static BootContext of(String[] args, Pare3<ApType, String, Object>... files) {
		return new BootContext(args, ARR.asAL(files));
	}

	//
	//
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// -------------------------- GET VALUE --------------------------------
	//
	//

	@Override
	public <T> T getAs(String key, Class<T> asType, T... defRq) {
		List<String> values = getAll(key, null);
		if (X.notEmpty(values)) {
			return UST.strTo(values.get(0), asType);
		}
		return ARG.toDefThrowMsg(() -> X.f("Not found value by key '%s'", key), defRq);
	}

	public <T> List<T> getAllAs(String key, Class<T> asType, List<T>... defRq) {
		List<String> values = getAll(key);
		if (X.notEmpty(values)) {
			return STREAM.mapToList(values, vl -> UST.strTo(vl, asType));
		}
		return ARG.toDefThrowMsg(() -> X.f("Not found values by key '%s' for type '%s'", key, asType), defRq);
	}

	public String get(String key, String... defRq) {
//		if (key == null) {
//			return null;
//		}
		IT.NN(key);
		List<String> all = ARG.isDef(defRq) ? getAll(key, null) : getAll(key);
		if (all != null) {
			return ARRi.first(all, defRq);
		}
		return ARG.toDefThrowMsg(() -> X.f("Not found values by key '%s'", key), defRq);
	}

	public List<String> getAll(String key, List<String>... defRq) {
		return getAll(key, false, defRq);
	}

	private List<String> getAll(String key, boolean ignoreCase_NOCACHE, List<String>... defRq) {
		if (ignoreCase_NOCACHE) {
			List<String> values = findValues(key, ignoreCase_NOCACHE);
			return X.notEmpty(values) ? values : ARG.toDefThrowMsg(() -> X.f("Not found values by key '%s' (ignore case)", key), defRq);
		}
		Map<String, List<String>> cacheAP = getCacheMap();
		if (cacheAP.containsKey(key)) {
			List<String> cachedVls = cacheAP.get(key);
			return X.notEmpty(cachedVls) ? cachedVls : ARG.toDefThrowMsg(() -> X.f("Not found values by key '%s'", key), defRq);
		}
		List<String> values = findValues(key);
		cacheAP.put(key, values);
		return X.notEmpty(values) ? values : ARG.toDefThrowMsg(() -> X.f("Not found values by key '%s'", key), defRq);
	}

	// ------------------------------------- CACHE MAP-------------------------------------

	private Map<String, List<String>> _cacheAP = null;

	public Map<String, List<String>> getCacheMap() {
		return _cacheAP != null ? _cacheAP : (_cacheAP = new HashMap<>());
	}

	//
	//
	// --------------------------------------------------------------------
	// --------------------------------------------------------------------
	// -------------------------- FIND ALL --------------------------------
	//
	//

	public <T> T findValueAs(String key, Class<T> asType) {
		String value = findValue(key);
		return value != null ? UST.strTo(value, asType, null) : null;
	}


	public String findValue(String key) {
		List<String> values = findValues(key);
		return X.empty(values) ? null : ARRi.first(values);
	}

	public List<String> findValues(String key, boolean... ignoreCase) {
		Map<Pare<ApType, String>, List<String>> values = findValuesMap(key, true, ignoreCase);
		return X.empty(values) ? null : ARRi.first(values).getValue();
	}

	public Map<Pare<ApType, String>, List<String>> findValuesMap(String key, boolean onlyFirst, boolean... ignoreCase) {
		return findValuesMap(key, null, onlyFirst, ignoreCase);
	}

	public Map<Pare<ApType, String>, Pare<String, List<String>>> findValuesMapByKey(Predicate<String> keyPredicate, boolean onlyFirst, boolean... ignoreCase) {
		return findValuesMapByKeyOrValue(null, keyPredicate, false, onlyFirst, ignoreCase);
	}

	public Map<Pare<ApType, String>, Pare<String, List<String>>> findValuesMapByVal(Predicate<String> keyPredicate, boolean onlyFirst, boolean... ignoreCase) {
		return findValuesMapByKeyOrValue(null, keyPredicate, true, onlyFirst, ignoreCase);
	}

	public Map<Pare<ApType, String>, List<String>> findValuesMap(String key, Predicate<String> keyPredicate, boolean onlyFirst, boolean... ignoreCase) {
		IT.state(X.nullOnlyOne(key, keyPredicate));
		boolean ic = ARG.isDefEqTrue(ignoreCase);
		Map<Pare<ApType, String>, List<String>> allFounded = new LinkedHashMap<>();
		stop:
		for (Map.Entry<Pare<ApType, String>, Map<String, String>> candidateMapEntry : getBootMap().entrySet()) {
			List<String> vls = new LinkedList();
			String keySrc = null;
			for (Map.Entry<String, String> candidateEntryValue : candidateMapEntry.getValue().entrySet()) {

				if (keyPredicate != null) {
					if (!keyPredicate.test(candidateEntryValue.getKey())) {
						continue;
					}
					keySrc = candidateEntryValue.getKey();
				} else { //use key
					if (!(ic ? candidateEntryValue.getKey().equalsIgnoreCase(key) : candidateEntryValue.getKey().equals(key))) {
						continue;
					}
					keySrc = key;
				}

				Object value = IT.NN((Object) candidateEntryValue.getValue());
				if (value instanceof String) {
					vls.add((String) value);
				} else {
					Set<String> set = (Set<String>) value;
					set.forEach(v -> vls.add(v));
				}

			}
			if (X.empty(vls)) {
				continue;
			}
			allFounded.put(candidateMapEntry.getKey(), vls);
			if (onlyFirst) {
				break stop;
			}
		}
		return allFounded.isEmpty() ? null : allFounded;
	}

	// -----------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------
	// ------------------------------------- FULL MAP (with key-------------------------------------

	public Map<Pare<ApType, String>, Pare<String, List<String>>> findValuesMapByKeyOrValue(String key, Predicate<String> keyPredicate, boolean byValue, boolean onlyFirst, boolean... ignoreCase) {
		IT.state(X.nullOnlyOne(key, keyPredicate), "set key or predicate");
		boolean ic = ARG.isDefEqTrue(ignoreCase);
		Map<Pare<ApType, String>, Pare<String, List<String>>> allFounded = new LinkedHashMap<>();
		stop:
		for (Map.Entry<Pare<ApType, String>, Map<String, String>> candidateMapEntry : getBootMap().entrySet()) {
			List<String> vls = new LinkedList();
			String keySrc = null;
			for (Map.Entry<String, String> candidateEntryValue : candidateMapEntry.getValue().entrySet()) {

				String entryKey = candidateEntryValue.getKey();
				String entryValue = candidateEntryValue.getValue();

				if (keyPredicate != null) {
					if (!keyPredicate.test(byValue ? entryValue : entryKey)) {
						continue;
					}
					keySrc = entryKey;
				} else { //use key
//					if (!(ic ? entryKey.equalsIgnoreCase(key) : entryKey.equals(key))) {
					if (byValue) {
						if (!EQ.equalsString(entryValue, key, ic)) {
							continue;
						}
					} else {
						if (!EQ.equalsString(entryKey, key, ic)) {
							continue;
						}
					}
//					keySrc = key;
					keySrc = entryKey;
				}

				Object value = IT.NN((Object) entryValue);
				if (value instanceof String) {
					vls.add((String) value);
				} else {
					Set<String> set = (Set<String>) value;
					set.forEach(v -> vls.add(v));
				}

			}
			if (X.empty(vls)) {
				continue;
			}
			allFounded.put(candidateMapEntry.getKey(), Pare.of(keySrc, vls));
			if (onlyFirst) {
				break stop;
			}
		}
		return allFounded.isEmpty() ? null : allFounded;
	}

	// -----------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------
	// ------------------------------------- FULL MAP-------------------------------------

	public BootMap asMap(boolean... checkAndReplaceSpringPlaceholderWithFind) {
		return BootMap.of(this).checkAndReplaceSpringPlaceholderWithFind(checkAndReplaceSpringPlaceholderWithFind);
	}

	private Multimap<String, String> _fullMap;

	@Deprecated //why?
	public Multimap<String, String> asFullMap(boolean... all) {
		if (_fullMap != null) {
			return _fullMap;
		}
		Multimap<String, String> fullMap = LinkedHashMultimap.create();
		for (Map.Entry<Pare<ApType, String>, Map<String, String>> candidateMapEntry : getBootMap().entrySet()) {
			for (Map.Entry<String, String> candidateEntryValue : candidateMapEntry.getValue().entrySet()) {
				String key = candidateEntryValue.getKey();
				Object value = candidateEntryValue.getValue();
				if (ARG.isDefNotEqTrue(all) && fullMap.containsKey(key)) {
					continue;
				}
				if (value instanceof String) {
					fullMap.put(key, (String) value);
				} else {
					Set<String> set = (Set<String>) value;
					set.forEach(v -> fullMap.put(key, v));
				}
			}
		}
		return _fullMap = fullMap;
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------- INIT-------------------------------------
	// -------------------------------------------------------------------------------

	private Map<Pare<ApType, String>, Map<String, String>> _bootMap;

	public Map<Pare<ApType, String>, Map<String, String>> getBootMap() {
		if (_bootMap != null) {
			return _bootMap;
		}
		_bootMap = init(runAppArgs, initAppLocEntitys);

		boolean reInit = false;
		String value = findValue(APK_RC_FMAP);
		if (value != null) {
			addMap(IT.isFileExist(Paths.get(value)), 1);
			reInit = true;
		}

		value = findValue(APK_RC_FOPTS);
		if (value != null) {
			addOpts(IT.isFileExist(Paths.get(value)), 1);
			reInit = true;
		}

		return reInit ? (_bootMap = init(runAppArgs, initAppLocEntitys)) : _bootMap;
	}

	private static Map<Pare<ApType, String>, Map<String, String>> init(String[] args, List<Pare3<ApType, String, Object>> files) {
		Map<Pare<ApType, String>, Map<String, String>> bootMap = new LinkedHashMap();
		for (Pare3<ApType, String, Object> apTypeFile : files) {
			ApType AP_TYPE = apTypeFile.key();
			String apLocName = apTypeFile.val();
			switch (AP_TYPE) {
				case RUN_OPTS:
					if (X.notEmpty(args)) {
						Map map = SeqOptions.ofStrictKeys(args).asMultiMap().asMap();
						bootMap.put(Pare.of(AP_TYPE, apLocName), map);
					}
					continue;
				case FILE_OPTS: {
					Map map = SeqOptions.ofPath(Paths.get(apLocName)).asMultiMap().asMap();
					bootMap.put(Pare.of(AP_TYPE, apLocName), map);
					continue;
				}
				case FILE_MAP: {
					Map map = RuProps.of(Paths.get(apLocName)).toMap();
					bootMap.put(Pare.of(AP_TYPE, null), map);
					continue;
				}
				case SYS:
					bootMap.put(Pare.of(AP_TYPE, null), (Map) System.getProperties());
					continue;
				case ENV:
					bootMap.put(Pare.of(AP_TYPE, null), System.getenv());
					continue;
				case LOCAL_MAP:
					Map<String, String> map = (Map<String, String>) apTypeFile.ext();
					bootMap.put(Pare.of(AP_TYPE, apLocName), map);
					continue;
				case APPDIR:
				case RL:
				case RSRC:
					AP_TYPE.initBootMap_AP(bootMap, apLocName, (Boolean) apTypeFile.ext());
					break;
				default:
					throw new WhatIsTypeException(AP_TYPE);
			}
		}
		return bootMap;
	}

	//application.properties location
	public enum ApType {
		RUN_OPTS, //cmd args
		SYS,
		ENV,
		LOCAL_MAP, // custom local map
		FILE_OPTS,
		FILE_MAP,
		APPDIR, // appVol dir
		RL, //ap from run dir
		RSRC //  package resources
		;

		Void initBootMap_AP(Map<Pare<ApType, String>, Map<String, String>> bootMap, String filename, boolean required) {
			switch (this) {
				case SYS:
					bootMap.put(Pare.of(this, filename), (Map) System.getProperties());
					return null;
				case ENV:
					bootMap.put(Pare.of(this, filename), System.getenv());
					return null;
				case APPDIR: {
					Path appDataDir = APP.LOCATION.getAppDataDirOrNull();
					if (appDataDir == null) {
						return !required ? null : X.throwException("Not found appDir");
					}
					return loadMapFromDir(bootMap, appDataDir, filename, required);
				}
				case RL:
					return loadMapFromDir(bootMap, Env.RUN_LOCATION, filename, required);
				case RSRC:
					String cnt = RES.of(Env.class, "/" + UF.normFileStart(filename)).cat(null);
					if (cnt != null) {
						Map rsrcMap = URuProps.getRuPropertiesClassic(ARR.as(cnt.split("\\n")));
						bootMap.put(Pare.of(this, filename), rsrcMap);
						return null;
					}
					return throwRequired(filename, required);
				default:
					throw new WhatIsTypeException(this);
			}
		}

		private Void loadMapFromDir(Map<Pare<ApType, String>, Map<String, String>> bootMap, Path fromDir, String filename, boolean required) {
			Path fileMap = fromDir.resolve(filename);
			if (UFS.existFile(fileMap)) {
				bootMap.put(Pare.of(this, filename), RuProps.of(fileMap).toMap());
				return null;
			}
			return throwRequired(filename, required);
		}

		private Void throwRequired(String filename, boolean required) {
			return !required ? null : X.throwException("Boot Resource '%s' not exists from %s", filename, this);
		}

	}

	@Override
	public String toString() {
		return "BootContext{" + "runAppArgs=" + Arrays.toString(runAppArgs) + ", initAppLocEntitys=" + initAppLocEntitys + '}';
	}
}
