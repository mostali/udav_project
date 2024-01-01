package mpc.json;

import lombok.SneakyThrows;
import mpc.*;
import mpc.args.ARG;
import mpc.ERR;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.RW;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.fs.fd.DIR;
import mpc.fs.fd.RES;
import mpc.rfl.IRfl;
import mpc.str.ObjTo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GsonMap<V> implements Map<String, V>, Serializable, IRfl {
	public static final Logger L = LoggerFactory.getLogger(GsonMap.class);
	public static final String EMPTYFILE = "{}";

	protected final String key;
	protected final Map<String, V> map;

	public GsonMap() {
		this(newEmptyMap());
	}

	public GsonMap(Map<String, V> map) {
		this(null, map);
	}

	public GsonMap(String key, Map<String, V> map) {
		this.key = key;
		this.map = map;
	}

	public static String toStringFromMap(Map<String, String> json) {
		return UGson.toStringJson(json);
	}

	@SneakyThrows
	public static GsonMap read(Path file, boolean... createIfNotExist) {
		if (ARG.isDefEqTrue(createIfNotExist)) {
			UFS_BASE.MKFILE.createFileIfNotExistWithContent_(file, UGson.EMPTY_PATTERN);
		}
		return of(RW.readContent(file));
	}

	@SneakyThrows
	public static void write(Path file, GsonMap gmap) {
		write(file, gmap, false, false);
	}

	@SneakyThrows
	public static void write(Path file, GsonMap gmap, boolean createIfNotExist) {
		write(file, gmap, false, createIfNotExist);
	}

	@SneakyThrows
	public static void write(Path file, GsonMap gmap, boolean pretty, boolean createIfNotExist) {
		if (ARG.isDefEqTrue(createIfNotExist)) {
			UFS_BASE.MKFILE.createFileIfNotExist_(file);
		}
		CharSequence string = gmap.toStringJson();
		RW.write(file, pretty ? UGson.toStringPretty(string.toString()) : string);
		if (L.isDebugEnabled()) {
			L.debug("Write file '{}' with content\n{}", file, string);
		}

	}

	public static boolean isEmpty(GsonMap gsonMap) {
		return gsonMap == null || gsonMap.isEmpty();
	}

	public static GsonMap of(Path file, boolean createFile) {
		if (createFile) {
			UGson.createEmptyJsonFile(file);
		}
		return of(file);
	}

	public CharSequence toStringJson() {
		return UGson.toStringJson(map);
	}

	public Map<String, V> map() {
		return map;
	}

	public static Map toMapFromString(CharSequence json, Map... defRq) {
		return UGson.toMapFromString(json, defRq);
	}

	public static GsonMap of(Map map) {
		return new GsonMap(map);
	}

	public static GsonMap of(Path fileJson, GsonMap... defRq) {
		try {
			if (UFS.isExistFileWithContent(fileJson)) {
				return RW.readGsonMap(fileJson);
			}
			throw new FileNotFoundException(fileJson.toString());
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Parse GsonMap error from file '%s'", fileJson), defRq);
		}
	}

	public static GsonMap of(String json, GsonMap... defRq) {
		try {
			return new GsonMap(UGson.toMapFromString(json));
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw ex;
		}
	}

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
		return ARG.toDefThrow(() -> new RequiredRuntimeException("GsonMap Value by key '%s' not found", key), defRq);
	}

	//
	//
	//

	public String getAsStr(Object key, String... defRq) {
		Object vl = get(key, null);
		if (vl != null) {
			return vl.toString();
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("GsonMap Value by key '%s' not found", key), defRq);
	}

	public Boolean getAsBoolean(Object key, Boolean... defRq) {
		return getAs(key, Boolean.class, defRq);
	}

	public GsonMap getAsGsonMapOrCreate(String key) {
		GsonMap child = getAsGsonMap(key, null);
		if (child != null) {
			return child;
		}
		child = newEmpty(true);
		put(key, (V) child.map);
		return child;
	}

	public GsonMap getAsGsonMap(String key, GsonMap... defRq) {
		GsonMap child = getAs(key, GsonMap.class, null);
		if (child != null) {
			return child;
		}
		return child != null ? newEmpty(true) : ARG.toDefThrow(() -> new RequiredRuntimeException("GsonMap Value typeof '%s' by key '%s' not found", GsonMap.class, key), defRq);
	}

	public static GsonMap of(String key, Map json) {
		return new GsonMap(key, json);
	}

	public static GsonMap of(Class rsrsClass, String rsrcPath) {
		String json = RES.of(rsrsClass, rsrcPath, DIR.class).cat();
		return new GsonMap(UGson.toMapFromString(json));
	}

	public <T> T getAs(Object key, Class<T> asType, T... defRq) {
		try {
			V v = get(key);
			return ObjTo.objTo(v, asType);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("GsonMap Value typeof '%s' by key '%s' not found", asType, key), defRq);
		}
	}

	@Override
	public V put(String key, V value) {
		boolean isGM = value instanceof GsonMap;
		ERR.state(!isGM, "fuse gm");
		if (value instanceof Integer || value instanceof Long) {
			Number n = (Number) value;
			value = (V) (Double) n.doubleValue();
		}
		return map().put(key, value);
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

	@Override
	public String toString() {
		return map().toString();
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

	public GsonMap child(String key, GsonMap... defRq) {
		GsonMap childJson = getAsGsonMap(key, null);
		return childJson != null ? childJson : ARG.toDefThrow(() -> new RequiredRuntimeException("ChildGson '%s' not found", key), defRq);
	}

	public GsonMap childOrCreate(String key) {
		return getAsGsonMapOrCreate(key);
	}

	protected GsonMap newChild() {
		return new ChildGsonMap(this, key, GsonMap.newEmptyMap());
	}

	public static Map newEmptyMap() {
		return new LinkedHashMap();
	}

	protected GsonMap newEmpty(boolean... withParent) {
		GsonMap parent = this;
		return ARG.isDefEqTrue(withParent) ? new GsonMap() {
			@Override
			protected GsonMap parent() {
				return parent;
			}
		} : null;
	}

	protected GsonMap parent() {
		return null;
	}

	public static class ChildGsonMap extends GsonMap {
		private final GsonMap parent;

		public ChildGsonMap(GsonMap parent, String key, Map childJson) {
			super(key, childJson);
			this.parent = parent;
		}

		@Override
		protected GsonMap parent() {
			return parent;
		}
	}
}
