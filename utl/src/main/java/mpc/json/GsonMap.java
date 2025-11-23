package mpc.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import mpu.pare.Tuple;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GsonMap<V> implements Map<String, V>, Serializable, IRfl, IGetterAs {

//	public static void main(String[] args) {
//		String stringPrettyJson = GsonMap.ofKV("1", "2", "3", null).toStringPrettyJson(true);
//		X.exit(UGson.parse(stringPrettyJson));
//	}

	public static final Logger L = LoggerFactory.getLogger(GsonMap.class);
	public static final String EMPTYFILE = "{}";
	public static final GsonMap EMPTYMAP = new GsonMap();

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

	public static Object checkAndConvertIfMap(Object data) {
		if (!(data instanceof Map)) {
			return data;
		} else if (data instanceof GsonMap) {
			return data;
		} else {
			return GsonMap.of((Map) data);
		}
	}

	public static GsonMap toMapFromObj(Object jsonObject) {
		Map mapFromObject = UGson.toMapFromObject(jsonObject);
		return GsonMap.of(mapFromObject);
	}

	public static GsonMap ofObj(Object any) {
		return GsonMap.of(UGson.toStringJson_FromObject(any));
	}

	public static List<GsonMap> ofLinentJson(JsonElement s) {
		if (s instanceof JsonArray) {
			return ARR.toList(((JsonArray) s)).stream().map(js -> GsonMap.of(js.toString())).collect(Collectors.toList());
		}
		return ARR.as(GsonMap.of(s.toString()));
	}

	@Override
	public String toString() {
		//return toStringJson(); // why not it?
		return UGson.toStringJson(this);
	}

	@SneakyThrows
	public static GsonMap read(Path file, boolean... createIfNotExist) {
		if (ARG.isDefEqTrue(createIfNotExist)) {
			UFS.MKFILE.createFileIfNotExistWithContentMkdirs(file, UGson.EMPTY);
		}
		return of(RW.readString(file));
	}

	@SneakyThrows
	public static GsonMap read(Path file, Charset charset, boolean... createIfNotExist) {
		if (ARG.isDefEqTrue(createIfNotExist)) {
			UFS.MKFILE.createFileIfNotExistWithContentMkdirs(file, UGson.EMPTY);
		}
		return of(RW.readContent_(file, charset));
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
		write(file, gmap, pretty, true, createIfNotExist);
	}

	@SneakyThrows
	public static void write(Path file, GsonMap gmap, boolean pretty, boolean scientific, boolean createIfNotExist) {
		if (ARG.isDefEqTrue(createIfNotExist)) {
			UFS.MKFILE.createFileIfNotExist_(file);
		}
		String json = gmap.toStringJson();
		String content = UGson.toStringJson(json.toString(), pretty, true, scientific);
//		String content = pretty ? UGson.toStringPretty(json) : json;
//		content = scientific ? UGson.toStringScientific(content) : json;
		RW.write(file, content);
		if (L.isDebugEnabled()) {
			L.debug("Write file://{} with content*{}\n{}", file, X.sizeOf(content), content);
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

	public String toStringJson(boolean... serializeNulls) {
		if (key != null) {
			return UGson.toStringJson(MAP.of(key, map), serializeNulls);
		}
		return UGson.toStringJson(map, serializeNulls);
	}

	public String toStringPrettyJson() {
		return UGson.toStringPretty(toStringJson(true), false, false);
	}

	public String toStringPrettyJson(boolean serializeNulls, boolean scintific) {
		return UGson.toStringPretty(toStringJson(serializeNulls), ARG.isDefEqTrue(serializeNulls), scintific);
	}

//	public CharSequence toStringScientific() {
//		return UGson.toStringScientific(toStringJson().toString());
//	}

	public Map<String, V> map() {
		return map;
	}

	public static Map toMapFromString(CharSequence json, Map... defRq) {
		return UGson.toMapFromString(json, defRq);
	}

	public static GsonMap ofKV(Object... keyValues) {
		return new GsonMap(MAP.of(keyValues));
	}

	public static GsonMap of(Map map) {
		return new GsonMap(map);
	}

	public static GsonMap of(Path fileJson, GsonMap... defRq) {
		try {
			if (UFS.isFileWithContent(fileJson)) {
				return RW.readGsonMap(fileJson);
			}
			throw new FileNotFoundException(fileJson.toString());
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> ex instanceof RequiredRuntimeException ? (RequiredRuntimeException) ex : new RequiredRuntimeException(ex, "Error parse file with json: %s", fileJson), defRq);
		}
	}

	public static GsonMap of(String json, GsonMap... defRq) {
		try {
			return new GsonMap(UGson.toMapFromString(json));
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> ex instanceof RequiredRuntimeException ? (RequiredRuntimeException) ex : new RequiredRuntimeException(ex, "Error parse json string: %s", json), defRq);
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


	public <T> T getAs(String key, Class<T> asType, T... defRq) {
		try {
			V v = get(key);
			return ObjTo.objTo(v, asType);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("GsonMap Value typeof '%s' by key '%s' not found", asType, key), defRq);
		}
	}

	public String getAsString(Object key, String... defRq) {
		Object vl = get(key, null);
		if (vl != null) {
			return vl.toString();
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("GsonMap Value by key '%s' not found", key), defRq);
	}

//	public Long getAsLong(String key, Long... defRq) {
//		return getAs(key, Long.class, defRq);
//	}

//	public Integer getAsInt(String key, Integer... defRq) {
//		return getAs(key, Integer.class, defRq);
//	}

//	public Boolean getAsBoolean(String key, Boolean... defRq) {
//		return getAs(key, Boolean.class, defRq);
//	}


	public GsonMap getAsGsonMapOrCreate(String key) {
		GsonMap child = getAsGsonMap(key, null);
		if (child != null) {
			return child;
		}
		child = newEmpty(true);
		put(key, (V) child.map);
		return child;
	}

	public List<GsonMap> getAsArrayGsonMap(String key, List<GsonMap>... defRq) {
		List child = (List) get(key, null);
		if (child != null) {
			return (List<GsonMap>) child.stream().map(m -> GsonMap.of((Map) m)).collect(Collectors.toList());
		}
		return child != null ? child : ARG.toDefThrow(() -> new RequiredRuntimeException("getAsArrayGsonMap by key '%s' not found", key), defRq);
	}

	public List getAsArray(String key, List... defRq) {
		List child = (List) get(key, null);
		if (child != null) {
			return child;
		}
		return child != null ? child : ARG.toDefThrow(() -> new RequiredRuntimeException("getAsArrayGsonMap by key '%s' not found", key), defRq);
	}

	public GsonMap getAsGsonMap(String key, GsonMap... defRq) {
		GsonMap child = getAs(key, GsonMap.class, null);
		if (child != null) {
			return child;
		}
		return child != null ? newEmpty(true) : ARG.toDefThrowMsg(() -> X.f("GsonMap not found value typeof '%s' by key '%s' not found", GsonMap.class, key), defRq);
	}

	public static GsonMap of(String key, Map json) {
		return new GsonMap(key, json);
	}

	public static GsonMap of(Class rsrsClass, String rsrcPath) {
		String json = RES.of(rsrsClass, rsrcPath, DIR.class).cat();
		return new GsonMap(UGson.toMapFromString(json));
	}

	@Override
	public V put(String key, V value) {
		boolean isGM = value instanceof GsonMap;
		IT.state(!isGM, "fuse gm");
		if (value instanceof Integer || value instanceof Long) {
			Number n = (Number) value;
			value = (V) (Double) n.doubleValue();
		}
		V put = map().put(key, value);
//		if (L.isInfoEnabled()) {
//			L.info("G");
//		}
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

	public Tuple getTuple(String... props) {
		return Tuple.ofMap(this, props);
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
