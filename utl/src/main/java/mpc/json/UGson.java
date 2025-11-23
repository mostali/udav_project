package mpc.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpc.json.expt.CustomizedObjectTypeAdapter;
import mpc.num.UNum;
import mpe.core.P;
import mpu.core.ARG;
import mpu.core.ARR;
import mpc.arr.NaturalOrderComparator;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.*;
import mpc.fs.fd.EFT;
import mpc.fs.fd.RES;
import mpc.map.MAP;
import mpu.str.STR;
import mpu.str.UST;
import mpu.Sys;
import mpu.X;
import mpu.core.RW;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UGson {

	public static final String EMPTY = "{}";

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {

		Map<String, Map<String, Map<Integer, String>>> json1 = MAP.of("1", Map.of("body", Map.of(10, "-10-")), "2", Map.of("body", "123"));
		String json = toStringJson(json1);
		X.exit(json);

//		JsonObject body = getValueObject(json2, "body", null);
		String body = getValueSimple(json, "body", null);

		X.exit(body);

//		List<String> authors = JsonPath.read(new File("/home/dav/pjm/tmp/bottom-history-panel-data/20241109_201256.tmp.rsp"), "$.*.10");
//		Object vl = JsonPath.read(new File("log.log"), "$.payload.['org.unidata.mdm.rest.v2.data'].recordKeys.etalonKey.id");
		String vl = JsonPath.read(new File("log.json"), "$.payload.['org.unidata.mdm.rest.v2.data'].recordKeys.jp");
		Object guid = JsonPath.read(new File("log.json"), vl);

		P.exit(guid);
	}

//	static class JsonPath {
//		final String jsonPath;
//		Path path;
//
//		public JsonPath(String jsonPath) {
//			this.jsonPath = jsonPath;
//			path = Path.of(jsonPath);
//			String next = UPath.item(path, 0).toString();
//
//		}
//
//		public static JsonElement getAs(String json, String path) {
//			JsonPath jsonPath = new JsonPath(path);
//			GsonMap<String> gm = GsonMap.of(json);
//

	/// /			gm.entrySet().forEach(e -> {
	/// /				String key = e.getKey();
	/// /			});
//
//			List<Rslt> rsps = new LinkedList<>();
//			for (int i = 0; i < jsonPath.path.getNameCount(); i++) {
//				String next = jsonPath.path.getName(i).toString();
//				Rslt mapResult = getMapResult(gm, next);
//				if (!mapResult.isEmpty()) {
//					rsps.add(mapResult);
//				}
//			}
//			return toJsonArrayFromAnyObject(rsps.stream().map(s -> s.result).collect(Collectors.toList()));
//		}
//
//		@RequiredArgsConstructor
//		static class Rslt {
//			final String key;
//			final Map result;
//
//			public boolean isEmpty() {
//				return X.empty(result);
//			}
//		}
//
//		private static Rslt getMapResult(GsonMap<String> gm, String nextPath) {
//			Map rsp = new LinkedHashMap();
//			for (Map.Entry<String, ?> o : gm.entrySet()) {
//				String key = o.getKey();
//				if ("*".equals(nextPath) || nextPath.equals(key)) {
//					rsp.put(key, o.getValue());
//				}
//			}
//			return new Rslt(nextPath, rsp);
//		}
//	}
	public static void exit(Object data) {

		Sys.p(data);
		Sys.exit();
	}

	public static void p(Object data) {
		Sys.p(data == null ? null : toStringPretty(data.toString()));
	}

	public static JsonObject toJO(InputStream jsonData) {
		return getGson().fromJson(new InputStreamReader(jsonData), JsonObject.class);
	}

	public static JsonObject toJO(byte[] jsonData) {
		return getGson().fromJson(new InputStreamReader(new ByteArrayInputStream(jsonData)), JsonObject.class);
	}

	public static JsonObject toJO(Map map) {
		return JO(toStringJson(map));
	}

	public static JsonObject toJO(String key, List list) {
		JsonObject jo = new JsonObject();
		JsonArray arr = new JsonArray();
		list.stream().forEach(o -> arr.add(JO(String.valueOf(o))));
		jo.add(key, arr);
		return jo;
	}

	public static LinkedTreeMap<String, ?> toMapFromJO(JsonObject json, LinkedTreeMap... defRq) {
		try {
			return (LinkedTreeMap<String, ?>) getGson().fromJson(json, Map.class);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static Map toMapFromObject(Object json, Map... defRq) {
		try {
			Map map = getGson().fromJson(toStringJson_FromObject(json), Map.class);
			if (map != null) {
				return map;
			}
			throw new FIllegalArgumentException("Illegal object '%s' for deserialize to map ", json);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	@Deprecated
	public static LinkedHashMap<String, JsonElement> toNativeMap(JsonObject jo) {
		LinkedHashMap<String, JsonElement> map = new LinkedHashMap<>();
		jo.entrySet().forEach(e -> map.put(e.getKey(), e.getValue()));
		return map;
	}

	public static Map toMapFromString(Path pathJson, Map... defRq) {
		return toMapFromString(RW.readString(pathJson), defRq);
	}

	public static Map toMapFromString(CharSequence json, Map... defRq) {
		Exception err;
		try {
			Map map = getGson().fromJson(json.toString(), Map.class);
			if (map != null) {
				return map;
			}
			err = new FIllegalArgumentException("Map is null after deserialize");
		} catch (Exception ex) {
			err = ex;
		}
		Exception finalErr = err;
		return ARG.toDefThrow(() -> new RequiredRuntimeException(finalErr, "Illegal json '%s'", json), defRq);
	}

	public static Map toMapFromJO(JsonObject json, Map... defRq) {
		Exception err;
		try {
			Map map = getGson().fromJson(json, Map.class);
			if (map != null) {
				return map;
			}
			err = new FIllegalArgumentException("Map is null after deserialize ");
		} catch (Exception ex) {
			err = ex;
		}
		Exception finalErr = err;
		return ARG.toDefThrow(() -> new RequiredRuntimeException(finalErr, "Illegal json '%s'", json), defRq);
	}

	public static String toStringJsonFromKeyValues(Object... keyValues) {
		return toStringJson(MAP.of(keyValues));
	}

	public static String toStringJson(Map json, boolean... serializeNulls) {
		return getGson(ARG.isDefEqTrue(serializeNulls)).toJson(json);
	}

	public static String toStringJson(JsonObject json) {
		return json.getAsString();
	}

	public static JsonObject JO(Map json) {
		return JO(toStringJson(json));
	}

	public static <T> T as(String json, Class<T> asType) {
		return getGson().fromJson(json, asType);
	}

	public static String toStringJson_FromObject(Object object) {
		return getGson().toJson(object);
	}

	public static JsonArray JA(String response, JsonArray... defRq) {
		try {
			return getGson().fromJson(response, JsonArray.class);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Error parse JsonArray from json '%s' (%s)", STR.substrKeepStartEndAndInsertBetween(response, 10, 10, "...", response)), defRq);
		}
	}

	public static <T> T toObjectFromMap(Map map, Class<T> asType, T... defRq) {
		try {
			return getGson().fromJson(toStringJson(map), asType);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Error parse type '%s' from map '%s'", asType, map), defRq);
		}
	}

	public static boolean isEmpty(JsonElement element, boolean strictNN) {
		if (element == null) {
			if (ARG.isDefEqTrue(strictNN)) {
				throw new NullPointerException("Json is null");
			}
			return true;
		} else if (element.isJsonObject()) {
			return element.getAsJsonObject().entrySet().isEmpty();
		} else if (element.isJsonArray()) {
			JsonArray asJsonArray = element.getAsJsonArray();
			return asJsonArray.isEmpty();
		} else if (element.isJsonPrimitive()) {
			return X.empty(element.getAsJsonPrimitive().getAsString());
		}
		return element.isJsonNull();
	}

	public static boolean isGson(String json) {
		return UGson.JO(json, null) != null;
	}

	public static boolean isGsonArray(String json) {
		return UGson.JA(json, null) != null;
	}

	public static boolean isGsonLinent(String json) {
		try {
			String stringPrettyLinent = toStringPrettyLinent(json);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isGsonContent(Path file) {
		return isGson(RW.readString(file));
	}

	public static void createEmptyJsonFile(Path file) {
		UFS.MKFILE.createEmptyFileMkdirsIfNotExist(file, true);
	}

	public static String toStringPretty_FromListAnyObject_Numered(Collection<Object> items) {
		return toStringPrettyFromObject(toMapJson_fromListAnyObject(items));
	}

	public static Map<Integer, JsonObject> toMapJson_fromListAnyObject(Collection<Object> items) {
		AtomicInteger ctr = new AtomicInteger(0);
		return STREAM.mapToSet(items, UGson::toJsonObjectFromAnyObject).stream().collect(Collectors.toMap(js -> ctr.getAndIncrement(), js -> js));
	}

	public static JsonObject toJsonObjectFromAnyObject(Object jsonObject) {
		return of(toStringJson_FromObject(jsonObject), JsonObject.class);
	}

	public static JsonArray toJsonArrayFromAnyObject(Object jsonObject) {
		return of(toStringJson_FromObject(jsonObject), JsonArray.class);
	}

	public static <T> T toObject(JsonPrimitive obj, Class<T> clazz, T... defRq) {
		if (clazz.isAssignableFrom(obj.getClass())) {
			return clazz.cast(obj);
		} else if (Number.class.isAssignableFrom(clazz)) {
			if (obj.isNumber()) {
				return UNum.toNumber(obj.getAsNumber(), clazz, defRq);
			} else if (obj.isString()) {
				return UST.strTo(obj.getAsString(), clazz);
			}
		} else if (clazz.isPrimitive()) {
			throw NI.stop("ni impl:" + clazz + ":" + obj);
		} else if (CharSequence.class.isAssignableFrom(clazz)) {
			return (T) obj.toString();
		} else if (Boolean.class.isAssignableFrom(clazz)) {
			return obj.isBoolean() ? (T) (Boolean) obj.getAsBoolean() : (T) UST.BOOL(obj.toString(), (Boolean[]) defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Wrong Value [" + obj + "] for type [" + clazz + "]"), defRq);
	}

	public static String toStringPretty_orEmpty(String json) {
		return X.empty(json) ? EMPTY : toStringPretty(json);
	}

	public static String toStringScientific(String json) {
		GsonBuilder gsonBuilder = new GsonBuilder();

		gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, Object>>() {
		}.getType(), new MapDeserializerDoubleAsIntFix());

		Gson gson = gsonBuilder.create();
//			List<Map<String, Object>> l = gson.fromJson(json, new TypeToken<List<Map<String, Object>>>() {
//			}.getType());
		Map<String, Object> l = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
		}.getType());

//			for (Map<String, Object> item : l) {
//				System.out.println(item);
//			}

		String serialized = gson.toJson(l);
//			System.out.println(serialized);
		return serialized;
	}

	public static String toStringPrettyArrayWrap(String key, String array) {
		return GsonMap.ofKV(key, UGson.JA(array)).toStringPrettyJson();
	}

	public enum TypeGson {
		NULL, PRIMITIVE, ARRAY, OBJECT;

		public static TypeGson of(JsonElement el, TypeGson... defRq) {
			if (el.isJsonPrimitive()) {
				return PRIMITIVE;
			} else if (el.isJsonObject()) {
				return OBJECT;
			} else if (el.isJsonArray()) {
				return ARRAY;
			} else if (el.isJsonNull()) {
				return NULL;
			}
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw el == null ? new RequiredRuntimeException("JsonElement is null") : new RequiredRuntimeException("JsonElement is undefined:" + el.getClass().getSimpleName());
		}

		public static <T> T to(JsonElement je, Class<T> type, T... defRq) {
			if (je != null) {
				if (type.isAssignableFrom(je.getClass())) {
					if (type == JsonPrimitive.class) {
						return type.cast(je.getAsJsonPrimitive());
					} else if (type == JsonObject.class) {
						return type.cast(je.getAsJsonObject());
					} else if (type == JsonArray.class) {
						return type.cast(je.getAsJsonArray());
					} else if (type == JsonNull.class) {
						return type.cast(je.getAsJsonNull());
					} else if (type == JsonElement.class) {
						throw NI.stop("wth");
					}
				}
			}
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			} else if (je == null) {
				throw new RequiredRuntimeException("JE is null");
			}
			throw new RequiredRuntimeException("What is type [%s], except [%s]?" + je.getClass().getSimpleName(), type);
		}
	}

	@RequiredArgsConstructor
	private static class JsonObjectComparator<T> implements Comparator<JsonObject> {
		public final String key;

		public static Comparator<? super JsonElement> of(String sort_key) {
			return new JsonObjectComparator(sort_key);
		}

		@Override
		public int compare(JsonObject o1, JsonObject o2) {
			JsonPrimitive asJsonPrimitive1 = getJP(o1, key);
			JsonPrimitive asJsonPrimitive2 = getJP(o2, key);
			return NaturalOrderComparator.NUMERICAL_ORDER.compare(asJsonPrimitive1.getAsString(), asJsonPrimitive2.getAsString());
		}

	}

	public static void addProp(JsonObject jo, String key, String val) {
		jo.addProperty(key, val);
	}

	public static JsonObject addObj(JsonObject jo, String key, String json) {
		JsonObject pretty = pretty(json);
		jo.add(key, pretty);
		return pretty;
	}

	public static boolean eq(String json, String json2) {
		JsonElement o1 = parse(json);
		JsonElement o2 = parse(json2);
		return o1.equals(o2);
	}

	public static JsonElement parse(String json) {
		return JsonParser.parseString(json);
	}

	public static List<JsonElement> toList(JsonArray jo) {
		List<JsonElement> all = new ArrayList<>();
		jo.forEach(e -> all.add(e));
		return all;
	}

	public static LinkedHashMap<String, JsonElement> toMapSyntetic(JsonObject jo) {
		//LinkedHashMap<String, JsonElement> map = jo.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
		LinkedHashMap<String, JsonElement> map = new LinkedHashMap<>();
		jo.entrySet().forEach(e -> map.put(e.getKey(), e.getValue()));
		return map;
	}

	/**
	 * *************************************************************
	 * ---------------------------- GET --------------------------
	 * *************************************************************
	 */

	public static JsonPrimitive getJP(JsonObject jsonObject, String key, JsonPrimitive... defRq) {
		JsonPrimitive asJsonPrimitive = jsonObject.getAsJsonPrimitive(key);
		if (asJsonPrimitive != null) {
			return asJsonPrimitive;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("NPE by key '%s' from object '%s'", key, jsonObject);
	}

	/**
	 * *************************************************************
	 * ---------------------------- OBJECT --------------------------
	 * *************************************************************
	 */

	public static Map getValueObjectAsMap(String jo, String[] keys, Map... defRq) {
		return getValueObjectAsMap(JO(jo), keys, defRq);
	}

	public static Map getValueObjectAsMap(JsonObject jo, String[] keys, Map... defRq) {
		JsonObject joDst;
		if (ARG.isDef(defRq)) {
			joDst = getValueObject(jo, keys, null);
			if (joDst == null) {
				return ARG.toDef(defRq);
			}
		} else {
			joDst = getValueObject(jo, keys);
		}
		return toMapSyntetic(joDst);
	}

	public static JsonObject getValueObject(String jo, String[] keys, JsonObject... defRq) {
		return getValueObject(JO(jo), keys, defRq);
	}

	public static JsonObject getValueObject(String jo, String key, JsonObject... defRq) {
		return getValueObject(JO(jo), key, defRq);
	}

	public static JsonObject getValueObject(JsonElement jo, String[] keys, JsonObject... defRq) {
		return getNode(jo, keys, JsonObject.class, defRq);
	}

	public static JsonObject getValueObject(JsonElement jo, String key, JsonObject... defRq) {
		return getNode(jo, key, JsonObject.class, defRq);
	}

	/**
	 * *************************************************************
	 * ---------------------------- ARRAY --------------------------
	 * *************************************************************
	 */

	public static List<JsonElement> getValueArrayAsList(String jo, String[] keys, List<JsonElement>... defRq) {
		return getValueArrayAsList(JO(jo), keys, defRq);
	}

	public static List<JsonElement> getValueArrayAsList(JsonObject jo, String[] keys, List<JsonElement>... defRq) {
		JsonArray ja;
		if (ARG.isDef(defRq)) {
			ja = getValueArray(jo, keys, null);
			if (ja == null) {
				return ARG.toDef(defRq);
			}
		} else {
			ja = getValueArray(jo, keys);
		}
		return toList(ja);
	}

	public static JsonArray getValueArray(String jo, String key, JsonArray... defRq) {
		return getValueArray(JO(jo), key, defRq);
	}

	public static JsonArray getValueArray(String jo, String[] keys, JsonArray... defRq) {
		return getValueArray(JO(jo), keys, defRq);
	}

	public static JsonArray getValueArray(JsonObject jo, String[] keys, JsonArray... defRq) {
		return getNode(jo, keys, JsonArray.class, defRq);
	}

	public static JsonArray getValueArray(JsonObject jo, String key, JsonArray... defRq) {
		return getNode(jo, key, JsonArray.class, defRq);
	}

	public static <T extends JsonElement> T getNode(JsonElement jo0, String[] keys, Class<T> type, T... defRq) {
		int keyI = 0;
		if (jo0.isJsonObject()) {
			JsonObject jo = jo0.getAsJsonObject();
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				keyI = i;
				if (i == keys.length - 1) {
					return getNode(jo, key, type, defRq);
				}
				jo = jo.getAsJsonObject(key);
				if (jo == null) {
					break;
				}
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		} else if (jo0 == null) {
			throw new RequiredRuntimeException("JE is null by key %s#%s", keys[keyI], keyI);
		}
		throw new RequiredRuntimeException("JE except %s by key %s#%s", type.getSimpleName(), keys[keyI], keyI);
	}

	public static <T extends JsonElement> T getNode(JsonElement je, String key, Class<T> type, T... defRq) {
		JsonElement jeByKey = null;
		if (je != null && je.isJsonObject()) {
			jeByKey = je.getAsJsonObject().get(key);
			if (jeByKey != null) {
				if (jeByKey.isJsonObject() && type == JsonObject.class) {
					return (T) jeByKey.getAsJsonObject();
				} else if (jeByKey.isJsonArray() && type == JsonArray.class) {
					return (T) jeByKey.getAsJsonArray();
				} else if (jeByKey.isJsonPrimitive() && type == JsonPrimitive.class) {
					return (T) jeByKey.getAsJsonPrimitive();
				}
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		} else if (je == null) {
			throw new RequiredRuntimeException("JE is null");
		} else if (jeByKey == null) {
			throw new RequiredRuntimeException("JE not found by key '%s'", key);
		}
		throw new RequiredRuntimeException("JE except '%s', but it '%s'", type.getSimpleName(), TypeGson.of(jeByKey));
	}

	/**
	 * *************************************************************
	 * ---------------------------- PRIMITIVE --------------------------
	 * *************************************************************
	 */

	public static String getValueSimple(String json, String[] keys, String... defRq) {
		return getValueSimple(JO(json), keys, defRq);
	}

	public static String getValueSimple(JsonObject jo, String[] keys, String... defRq) {
		int keyI = 0;
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			keyI = i;
			if (i == keys.length - 1) {
				return getValueSimple(jo, key, defRq);
			}
			jo = jo.getAsJsonObject(key);
			if (jo == null) {
				break;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		} else if (jo == null) {
			throw new RequiredRuntimeException("JO is null by key %s#%s", keys[keyI], keyI);
		}
		throw new RequiredRuntimeException("JO except JP by key %s#%s", keys[keyI], keyI);
	}

	public static String getValueSimple(String json, String key, String... defRq) {
		return getValueSimple(JO(json), key, defRq);
	}

	public static <T> T getValueSimpleAs(JsonElement je, String key, Class<T> asType, T... defRq) {
		try {
			String val = getValueSimple(je, key);
			return UST.strTo(val, asType, defRq);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "JP '%s' as type '%s' not found", key, asType), defRq);
		}
	}

	public static String getValueSimpleOr(JsonElement je, String[] keys, String... defRq) {
		if (je != null) {
			for (String key : keys) {
				String vl = getValueSimple(je, key, null);
				if (vl != null) {
					return vl;
				}
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("JP '%s' not found by keys", ARR.as(keys)), defRq);
	}

	//TODO - use getNode
	public static String getValueSimple(JsonElement je, String key, String... defRq) {
		JsonPrimitive node = getNode(je, key, JsonPrimitive.class, null);
		if (node != null) {
			return node.getAsString();
		}
		if (true) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("JP '%s' not found by key", key), defRq);
		}
		JsonElement jeByKey = null;
		if (je != null && je.isJsonObject()) {
			jeByKey = je.getAsJsonObject().get(key);
			if (jeByKey != null && jeByKey.isJsonPrimitive()) {
				return jeByKey.getAsString();
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		} else if (je == null) {
			throw new RequiredRuntimeException("JE is null");
		} else if (jeByKey == null) {
			throw new RequiredRuntimeException("JE not found by key '%s'", key);
		}
		throw new RequiredRuntimeException("JE except JP, but it '%s'", TypeGson.of(jeByKey));
	}

	/**
	 * *************************************************************
	 * ---------------------------- PRETTY --------------------------
	 * *************************************************************
	 */

//	private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
//			.registerTypeAdapter(CqlSearchResult.class, new CqlSearchResultDeserializer()).create();
	private final Gson gsonPretty = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//			.registerTypeAdapter(CqlSearchResult.class, new CqlSearchResultDeserializer()).create();

	public static JsonReader toLinentJsonReader(InputStream inputStream) throws UnsupportedEncodingException {
		Validate.notNull(inputStream);
		InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
		JsonReader jsonReader = new JsonReader(reader);
		jsonReader.setLenient(true);
		return jsonReader;
	}

	@Deprecated
	public static JsonObject pretty() {
		return pretty("{}");
	}

	@Deprecated
	public static JsonObject pretty(JsonObject jo) {
		return pretty(jo.toString());
	}

	@Deprecated
	public static JsonObject pretty(String json) {
		JsonElement jsonElement = JsonParser.parseString(json);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.fromJson(jsonElement, JsonObject.class);
	}

	public static String toStringPrettyFromObject(Object jsonObj) {
		return toStringPretty(toStringJson_FromObject(jsonObj));
	}

	public static String toStringPretty(String jsonString) {
		return toStringPretty(jsonString, false, true);
	}

	public static String toStringPretty(String jsonString, boolean serializeNulls, boolean scientific) {
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		return getGsonWith(true, serializeNulls, scientific).toJson(json);
	}

	public static String toStringJson(String jsonString, boolean pretty, boolean serializeNulls, boolean scientific) {
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		return getGsonWith(pretty, serializeNulls, scientific).toJson(json);
	}

	public static @NotNull Gson getGson(boolean serializeNulls) {
		return ARG.isDefEqTrue(serializeNulls) ? getGsonWith(false, ARG.isDefEqTrue(serializeNulls), false) : new Gson();
	}

	public static @NotNull Gson getGson() {
		return getGsonWith(false, false, false);
	}

	public static Gson getGsonWith(boolean pretty, boolean serializeNulls, boolean scintific) {
//		IT.stateNot(pretty && scintific, "not work this combination pretty && scintific");
		GsonBuilder gsonBuilder = new GsonBuilder();
		if (serializeNulls) {
			gsonBuilder = gsonBuilder.serializeNulls();
		}
		if (pretty) {
			gsonBuilder = gsonBuilder.setPrettyPrinting();
		}
		if (scintific) {
			gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, Object>>() {
			}.getType(), new MapDeserializerDoubleAsIntFix());
//			}.getType(), new MapSerializerDoubleAsDouble());
		}
		Gson gson = gsonBuilder.create();
		return gson;
	}

	public static String toStringScientificOld(String jsonString) {

		NI.stop("see jackson Scientific");
		if (true) {
			GsonBuilder gsonBuilder = getGsonBuilderNum();

			Gson gson0 = gsonBuilder.create();

			Map json = gson0.fromJson(jsonString, Map.class);
//			return toStringJson(json);
			String json1 = gsonBuilder.create().toJson(json);
			return json1;

		}
		CustomizedObjectTypeAdapter adapter = new CustomizedObjectTypeAdapter();
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Map.class, adapter)
				.create();

		List<Map<String, Object>> l = gson.fromJson(jsonString, new TypeToken<List<Map<String, Object>>>() {
		}.getType());
		String serialized = gson.toJson(l);

		if (true) {
			return serialized;
		}

		GsonBuilder gsonBuilder = new GsonBuilder();

		gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, Object>>() {
		}.getType(), new MapDeserializerDoubleAsIntFix());

		gson = gsonBuilder.create();
		l = gson.fromJson(jsonString, new TypeToken<List<Map<String, Object>>>() {
		}.getType());

//		for (Map<String, Object> item : l) {
//			System.out.println(item);
//		}

		serialized = gson.toJson(l);

		if (true) {
			return serialized;
		}

		if (false) {
			Map<String, Object> jsonMap = new Gson().fromJson(jsonString, new TypeToken<Map<String, Object>>() {
			}.getType());
			return new Gson().toJson(jsonMap);
		}

		gson = new GsonBuilder()
				.setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
				.create();

		return gson.toJson(jsonString);
	}

	private static @NotNull GsonBuilder getGsonBuilderNum() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
			@Override
			public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
				BigDecimal value = BigDecimal.valueOf(src);
				return new JsonPrimitive(value);
			}
		});
		gsonBuilder.registerTypeAdapter(Number.class, new JsonSerializer<Number>() {
			@Override
			public JsonElement serialize(final Number src, final Type typeOfSrc, final JsonSerializationContext context) {
				return new JsonPrimitive(src.longValue());
			}
		});
		return gsonBuilder;
	}

	@SneakyThrows
	public static String toStringPrettyLinent(String jsonString) {
		InputStream jsonIS = IOUtils.toInputStream(jsonString, Charset.defaultCharset());
		JsonObject json = JsonParser.parseReader(toLinentJsonReader(jsonIS)).getAsJsonObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(json);
		return prettyJson;
	}

	/**
	 * *************************************************************
	 * ---------------------------- IMPLODE --------------------------
	 * *************************************************************
	 */

	public static JsonArray implodeFilesFromDirLevel1(Path dir, LS_SORT ls_sort, List<String> onlyKeys, String sort_key) throws FileNotFoundException {
		if (sort_key == null && ls_sort == null) {
			ls_sort = LS_SORT.NATURAL;
		}
		List<Path> files = UDIR.ls(dir, EFT.FILE, ls_sort);
		return implodeFiles(files, onlyKeys, sort_key);
	}

	public static JsonArray implodeFiles(List<Path> files, List<String> onlyKeys, String sort_key) throws FileNotFoundException {
		List l = new LinkedList();
		for (Path allEntytysPath : files) {
			JsonElement json = getGson().fromJson(new FileReader(allEntytysPath.toFile()), JsonElement.class);
			l.add(json);
		}
		return implodeElements(l, onlyKeys, sort_key);
	}

	public static JsonArray implodeElements(List<JsonObject> jsons, List<String> onlyKeys, String sort_key) throws FileNotFoundException {
		JsonArray jsonArray = new JsonArray();
		if (X.notEmpty(onlyKeys)) {
			jsons = jsons.stream().map(jo -> json2json(jo, onlyKeys)).collect(Collectors.toList());
		}
		if (X.empty(sort_key)) {
			jsons.stream().forEach(jsonArray::add);
		} else {
			jsons.stream().sorted(JsonObjectComparator.of(sort_key)).forEach(jsonArray::add);
		}
		return jsonArray;
	}

	public static JsonObject json2json(JsonObject src, List<String> keys) {
		JsonObject jo = new JsonObject();
		keys.stream().forEach(k -> jo.add(k, src.get(k)));
		return jo;
	}

	/**
	 * *************************************************************
	 * ---------------------------- OF --------------------------
	 * *************************************************************
	 */

	public static JsonObject ofStr(Object objRepresentJson, JsonObject... defRq) {
		return of(objRepresentJson.toString(), JsonObject.class, defRq);
	}


	public static JsonObject ofFile(String file, JsonObject... defRq) {
		return of(RW.readString(Paths.get(file)), JsonObject.class, defRq);
	}

	public static JsonObject ofFile(String file, Object[] args, JsonObject... defRq) {
		return of(X.f(RW.readString(Paths.get(file)), args), JsonObject.class, defRq);
	}

	public static JsonObject ofRsrc(String file, Object[] args, JsonObject... defRq) {
		return of(X.f(RES.readString(file), args), JsonObject.class, defRq);
	}

	public static JsonObject ofRsrc(String file, JsonObject... defRq) {

		return of(RES.readString(file), JsonObject.class, defRq);

	}

	public static JsonElement JE(String json, JsonObject... defRq) {
		try {
			return of(json, JsonElement.class, defRq);
		} catch (JsonSyntaxException ex) {
			throw new IllegalStateException("JE:" + json, ex);
		}
	}

	public static JsonObject JO_byKeyValues(Object... keyValue) {
		return JO(MAP.mapOf(keyValue));
	}

	public static JsonObject JO(String json, JsonObject... defRq) {
		try {
			return of(json, JsonObject.class, defRq);
		} catch (JsonSyntaxException ex) {
			throw new IllegalStateException("JO:" + json, ex);
		}
	}

	public static JsonObject JO_(String json, JsonObject... defRq) {
		return of(json, JsonObject.class, defRq);
	}

	//
	//
	public static <T> T of(String json, Class<T> type, T... defRq) {
		try {
			return getGson().fromJson(json, type);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static <T> T of(Map json, Class<T> type, T... defRq) {
		return of(toJO(json), type);
	}

	public static <T> T of(JsonObject json, Class<T> type, T... defRq) {
		try {
			return getGson().fromJson(json, type);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	//
	//
	public static Function<Map.Entry<?, ?>, Comparable> createSortFuncByJsonPojo(String key, Class castTo) {
		return entry -> {
			if (entry == null || entry.getValue() == null || !(entry.getValue() instanceof JsonObject)) {
				return 0;
			}
			JsonPrimitive vl1 = ((JsonObject) entry.getValue()).getAsJsonPrimitive(key);
			if (vl1 == null) {
				return 0;
			}
			return (Comparable) objTo(vl1, castTo);
		};
	}

	public static <T> T objTo(JsonPrimitive json, Class<T> castTo, T... defRq) {
		if (CharSequence.class.isAssignableFrom(castTo)) {
			return (T) json.getAsString();
		} else if (Number.class.isAssignableFrom(castTo)) {
			if (Integer.class == castTo) {
				return (T) (Integer) json.getAsInt();
			} else if (Long.class == castTo) {
				return (T) (Long) json.getAsLong();
			} else if (BigDecimal.class == castTo) {
				return (T) json.getAsBigDecimal();
			} else if (BigInteger.class == castTo) {
				return (T) json.getAsBigInteger();
			} else if (Byte.class == castTo) {
				return (T) (Byte) json.getAsByte();
			} else if (Byte.class == castTo) {
				return (T) (Double) json.getAsDouble();
			} else if (Byte.class == castTo) {
				return (T) (Float) json.getAsFloat();
			} else if (Byte.class == castTo) {
				return (T) (Short) json.getAsShort();
			}
			return (T) json.getAsNumber();
		} else if (Boolean.class == castTo) {
			return (T) (Boolean) json.getAsBoolean();
		} else if (Character.class == castTo) {
			return (T) (Character) json.getAsCharacter();
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Type '%s' not found for casting '%s'", castTo, json), defRq);
	}

	@RequiredArgsConstructor
	public static class ComparatorEntryBy implements Comparator<Map.Entry<?, ?>> {
		final Function<Map.Entry<?, ?>, Comparable> func;

		@Override
		public int compare(Map.Entry<?, ?> o1, Map.Entry<?, ?> o2) {
			Comparable c1 = func.apply(o1);
			if (c1 == null) {
				return 0;
			}
			Comparable c2 = func.apply(o2);
			if (c2 == null) {
				return 0;
			}
			return c1.compareTo(c2);
		}
	}


	public static class MapSerializerDoubleAsDouble implements JsonSerializer<Map<String, Object>> {
		@Override
		public JsonElement serialize(Map<String, Object> src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			for (Map.Entry<String, Object> entry : src.entrySet()) {
				jsonObject.add(entry.getKey(), toJsonElement(entry.getValue(), context));
			}
			return jsonObject;
		}

		private JsonElement toJsonElement(Object value, JsonSerializationContext context) {
			if (value == null) {
				return JsonNull.INSTANCE;
			} else if (value instanceof Number) {
				if (value instanceof Double || value instanceof Float) {
					// Всегда сериализуем как double, даже если целое
					return new JsonPrimitive(((Number) value).doubleValue());
				} else {
					return new JsonPrimitive((Number) value);
				}
			} else if (value instanceof String) {
				return new JsonPrimitive((String) value);
			} else if (value instanceof Boolean) {
				return new JsonPrimitive((Boolean) value);
			} else if (value instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) value;
				return serialize(map, null, context);
			} else if (value instanceof List) {
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) value;
				JsonArray jsonArray = new JsonArray();
				for (Object item : list) {
					jsonArray.add(toJsonElement(item, context));
				}
				return jsonArray;
			} else {
				return context.serialize(value);
			}
		}
	}

	private static class MapDeserializerDoubleAsIntFix implements JsonDeserializer<Map<String, Object>> {

		@Override
		@SuppressWarnings("unchecked")
		public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return (Map<String, Object>) read(json);
		}

		public Object read(JsonElement in) {

			if (in.isJsonArray()) {
				List<Object> list = new ArrayList<Object>();
				JsonArray arr = in.getAsJsonArray();
				for (JsonElement anArr : arr) {
					list.add(read(anArr));
				}
				return list;
			} else if (in.isJsonObject()) {
				Map<String, Object> map = new LinkedTreeMap<String, Object>();
				JsonObject obj = in.getAsJsonObject();
				Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
				for (Map.Entry<String, JsonElement> entry : entitySet) {
					map.put(entry.getKey(), read(entry.getValue()));
				}
				return map;
			} else if (in.isJsonPrimitive()) {
				JsonPrimitive prim = in.getAsJsonPrimitive();
				if (prim.isBoolean()) {
					return prim.getAsBoolean();
				} else if (prim.isString()) {
					return prim.getAsString();
				} else if (prim.isNumber()) {

					Number num = prim.getAsNumber();
					// here you can handle double int/long values
					// and return any type you want
					// this solution will transform 3.0 float to long values
					if (Math.ceil(num.doubleValue()) == num.longValue()) {
						return num.longValue();
					} else {
						return num.doubleValue();
					}
				}
			}
			return null;
		}
	}

}
