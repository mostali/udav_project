package mpc.types.ruprops;

import lombok.SneakyThrows;
import mpc.fs.UF;
import mpu.core.RW;
import mpc.fs.UFS;
import mpc.fs.fd.RES;
import mpc.fs.path.IPath;
import mpc.map.IMap;
import mpc.map.MAP;
import mpu.core.ARG;
import mpu.IT;
import mpu.str.STR;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RuProps implements Serializable, IMap<String, String>, IPath {
	private Map<String, String> map;
	private transient Path path;
	private final String pathStr;
	private boolean syncWrite = false;

	public String pathStr() {
		return pathStr;
	}

	public void writeMap_(Path... path) throws IOException {
		Path _path = IT.notNull(ARG.toDefOr(this.path, path), "set path");
		writeMap(_path, (Map) map, syncWrite ? true : null);
	}

	@SneakyThrows
	public static void writeMap(Path path, Map<String, Object> map, Boolean mkdirs_mkdir_orNot) {
		String cnt = toStringFromMap(map);
		RW.write_(path, cnt, mkdirs_mkdir_orNot);
	}

	public static String toStringFromMap(Map<String, Object> map) {
		return URuProps.toRuPropertiesClassic(map);
	}

	public RuProps syncWrite(boolean syncWrite) {
		this.syncWrite = syncWrite;
		return this;
	}

	@Override
	public String toString() {
		String pfile = pathStr == null ? null : UF.ln(pathStr);
		return "RuProps{" + "path=" + pfile + " ,map=" + readMap() + '}';
	}

	public RuProps(Path path) {
		this.path = path;
		pathStr = path == null ? null : path.toString();
	}

	public Path toPath() {
		IT.notEmpty(pathStr, "RuProps is not file");
		return path == null ? path = Paths.get(pathStr) : path;
	}

	public static RuProps ofRunLocationOrResource(Class resourceClass, String fileFromRunLocationOrResource, boolean copyToRunLocation) {
		String app_props = RES.loadFileFromRunLocationOrResources(resourceClass, fileFromRunLocationOrResource, copyToRunLocation);
		return of(app_props);
	}

	public static RuProps of(String cnt) {
		RuProps props = of((Path) null);
		props.map = toMapClassic(cnt);
		return props;
	}

	public static RuProps of(Path path) {
		return new RuProps(path);
	}

	public String toStringClassic() {
		return toStringFromMap((Map) map);
	}

	public static String toStringClassic(Map map) {
		return URuProps.toRuPropertiesClassic(map);
	}

	public Map<String, String> toMapClassic() {
		return readMap();
	}

	public static Map toMapClassic(String map) {
		return URuProps.getRuPropertiesClassic(map);
	}

	public static Map toMapClassic(Path path) throws IOException {
		return URuProps.getRuPropertiesClassic(path);
	}

	@SneakyThrows
	public Map<String, String> readMap(Path... path) {
		return readMap_(path);
	}

	public Map<String, String> readMap_(Path... path) throws IOException {
		return readMap_(false, path);
	}

	public Map<String, String> readMap_(boolean fresh, Path... path) throws IOException {
		return readMap_(false, fresh, path);
	}

	public void reset() {
		map = null;
	}

	public Map<String, String> readMap_(boolean createFileIfNotExist, boolean fresh, Path... path) throws IOException {
		if (!fresh && map != null) {
			return map;
		}
		Path _path = ARG.toDefOr(this.path, path);
		if (_path != null) {
			if (UFS.existFile(_path)) {
				return map = toMapClassic(_path);
			}
			if (createFileIfNotExist) {
				UFS.MKFILE.createFileIfNotExist_(_path);
			}
		}
		return map = new LinkedHashMap<>();
	}

	/**
	 * *************************************************************
	 * ------------------------- SET --------------------------
	 * *************************************************************
	 */
	public RuProps setBool(String key, Boolean val, boolean... ifNullThat_Null_Blank) {
		return setType(key, val, ifNullThat_Null_Blank);
	}

	public RuProps setString(String key, String val, boolean... ifNullThat_Null_Blank) {
		return setType(key, val, ifNullThat_Null_Blank);
	}

	public RuProps setInt(String key, Integer val, boolean... ifNullThat_Null_Blank) {
		return setType(key, val, ifNullThat_Null_Blank);
	}

	public RuProps setLong(String key, Long val, boolean... ifNullThat_Null_Blank) {
		return setType(key, val, ifNullThat_Null_Blank);
	}

	@SneakyThrows
	public RuProps setType(String key, Object val, boolean... ifNullThat_Null_Blank) {
		String value = val != null ? val.toString() : (ARG.isDefEqTrue(ifNullThat_Null_Blank) ? null : "");
		readMap_().put(key, value);
		if (syncWrite) {
			writeMap_();
		}
		return this;
	}


	/**
	 * *************************************************************
	 * ------------------------- GET --------------------------
	 * *************************************************************
	 */
	@SneakyThrows
	public String getString(String key, String... defRq) {
		return MAP.get(readMap_(), key, defRq);
	}

	public Integer getInt(String key, Integer... defRq) {
		return getAsType(key, Integer.class, defRq);
	}

	public Long getLong(String key, Long... defRq) {
		return getAsType(key, Long.class, defRq);
	}

	public Boolean getBool(String key, Boolean... defRq) {
		return getAsType(key, Boolean.class, defRq);
	}

	@SneakyThrows
	public <T> T getAsType(String key, Class<T> type, T... defRq) {
		return getAs(key, type, defRq);
	}

	@Override
	public String get(String key) {
		return map().get(key);
	}

	@Override
	public void put(String key, String value) {
		map().put(key, value);
	}

	@Override
	public Map<String, String> toMap() {
		return map();
	}

	@SneakyThrows
	public <T> T getAs(String key, Class<T> type, T... defRq) {
		return MAP.getAs(readMap_(), key, type, defRq);
	}


	@SneakyThrows
	public void delete() {
		UFS.RM.deleteDir(toPath());
	}

	public boolean containsKey(String key) {
		return readMap().containsKey(key);
	}

	public boolean existsFile() {
		return pathStr == null ? false : UFS.existFile(toPath());
	}

	public Map<String, String> map() {
		return readMap();
	}

	public Set<String> keys() {
		return map().keySet();
	}

	public void mkfileIfNotExist(Boolean mkDir_mkDirs_Not) {
		if (!existsFile()) {
			UFS.MKFILE.createFileIfNotExist(toPath(), mkDir_mkDirs_Not);
		}
	}

}
