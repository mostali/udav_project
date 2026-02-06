package utl_jack;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.*;
import com.google.gson.*;
import lombok.SneakyThrows;
import mpc.net.JHttp;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.core.EQ;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class UJack {

	@SneakyThrows
	public static void main(String[] args) {
		JsonNode deserialize = deserialize("{\"k\":{\"k\":\"v\"}}");
		JsonNode k = deserialize.get("k");
		X.exit(k);
		X.exit(toStringGScientific(JHttp.GET_BODY("", null, String.class, 200)));
	}

	public static String toString(Object json, boolean... pretty) {
		return serialize(json, pretty);
	}

	@SneakyThrows
	public static String serialize(Object obj, boolean... pretty) {
		ObjectMapper mapper = mapper();
		mapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		if (ARG.isDefEqTrue(pretty)) {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		}
		return mapper.writeValueAsString(obj);
	}

	@SneakyThrows
	public static <T> T deserialize(String json, Class<T> type) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return mapper.readValue(json, type);
	}

	@SneakyThrows
	public static JsonNode deserialize(String json) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return mapper.readTree(json);
	}


	@SneakyThrows
	public static boolean eq(String json, String json2) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(json);
		JsonNode tree2 = mapper.readTree(json2);
		return tree1.equals(tree2);
	}

	public static String toStringPretty(String json) {
		JsonNode deserialize = UJack.deserialize(json);
		return deserialize.toPrettyString();
	}

	public static void exit(String json) {
		Sys.exit(UJack.toStringPretty(json));
	}

	public static void p(String json) {
		Sys.p(UJack.toStringPretty(json));
	}

	@SneakyThrows
	public static Map<String, Object> toMapFromString(String json) {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
		};
		LinkedHashMap<String, Object> map = mapper.readValue(json, typeRef);
		//		Map<String, String> map = mapper.readValue(json, Map.class);
		return map;
	}

	public static <T> T toObjectFromMap(Map map, Class<T> asType, T... defRq) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.convertValue(map, asType);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Error parse type '%s' from map '%s'", asType, map), defRq);
		}
	}

	@SneakyThrows
	public static String toStringFromMap(Map<String, Object> rsp) {
		return mapper().writeValueAsString(rsp);
	}

	@SneakyThrows
	public static JsonNode toNodeFromMap(Map<String, Object> map) {
		return toNodeFromObject(map, JsonNode.class);
	}

	@SneakyThrows
	public static JsonNode toNodeFromString(String value) {
		return toNodeFromObject(value, TextNode.class);
	}

	@SneakyThrows
	public static <T extends JsonNode> T toNodeFromObjectAuto(Object value) {
		if (value instanceof CharSequence) {
			return (T) toNodeFromObject(value, TextNode.class);
		} else if (value instanceof Number) {
			return (T) toNodeFromObject(value, ValueNode.class);
		} else if (value instanceof Map) {
			return (T) toNodeFromObject(value, JsonNode.class);
		} else if (value instanceof Iterable || value.getClass().isArray()) {
			return (T) toNodeFromObject(value, ArrayNode.class);
		}
		throw new WhatIsTypeException(value.getClass());
	}

	@SneakyThrows
	public static <T extends JsonNode> T toNodeFromObject(Object value, Class<T> typeNode) {
		return mapper().convertValue(value, typeNode);
	}

	private static ObjectMapper mapper() {
		return new ObjectMapper();
	}

	public static ArrayNode addToArray(Map<String, Object> src, String fieldname, Map json) {
		return addToArray(src, fieldname, toNodeFromMap(json));
	}

	public static ArrayNode addToArray(Map<String, Object> src, String fieldname, JsonNode node) {
		Object jo = src.get(fieldname);
		if (jo == null) {
			src.put(fieldname, jo = mapper().createArrayNode());
		} else if (!(jo instanceof ArrayNode)) {
			throw new FIllegalStateException(" Except JA, but it:" + jo);
		}
		return ((ArrayNode) jo).add(node);
	}

	public static JsonNode addToObject(Map<String, Object> src, String fieldname, String childFieldName, Map json) {
		return addToObject(src, fieldname, childFieldName, toNodeFromMap(json));
	}

	public static JsonNode addToObject(Map<String, Object> src, String fieldname, String childFieldName, JsonNode node) {
		Object jo = src.get(fieldname);
		if (jo == null) {
			src.put(fieldname, jo = mapper().createObjectNode());
		} else if (!(jo instanceof ObjectNode)) {
			throw new FIllegalStateException(" Except JO, but it:" + jo);
		}
		return ((ObjectNode) jo).put(childFieldName, node);
	}

	public static ArrayNode JA(Map<String, Object> json, String fieldname, ArrayNode... defRq) {
		ArrayNode valueJson = getNode(toNodeFromMap(json), fieldname, ArrayNode.class, null);
		if (valueJson != null) {
			return valueJson;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("JA '%s' is required", fieldname), defRq);
	}

	public static ObjectNode JO(Map<String, Object> json, String fieldname, ObjectNode... defRq) {
		ObjectNode valueJson = getNode(toNodeFromMap(json), fieldname, ObjectNode.class, null);
		if (valueJson != null) {
			return valueJson;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("JA '%s' is required", fieldname), defRq);
	}

	//
	//
	//

	public static ValueNode getNodeSimple(JsonNode je, String key, ValueNode... defRq) {
		return getNode(je, key, ValueNode.class, defRq);
	}

	public static ArrayNode getNodeArray(JsonNode je, String key, ArrayNode... defRq) {
		return getNode(je, key, ArrayNode.class, defRq);
	}

	public static ObjectNode getNodeObject(JsonNode je, String key, ObjectNode... defRq) {
		return getNode(je, key, ObjectNode.class, defRq);
	}

	public static <T extends JsonNode> T getNode(JsonNode je, String key, Class<T> type, T... defRq) {
		JsonNode jeByKey = null;
		if (je != null && je.isObject()) {
			jeByKey = je.get("key");
			if (jeByKey != null) {
				if (jeByKey.isObject() && type == ObjectNode.class) {
					return (T) jeByKey;
				} else if (jeByKey.isArray() && type == ArrayNode.class) {
					return (T) jeByKey;
				} else if (jeByKey.isValueNode() && type == ValueNode.class) {
					return (T) jeByKey;
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
		throw new RequiredRuntimeException("JE except '%s', but it '%s'", type.getSimpleName(), JsType.of(jeByKey));
	}

	public static boolean hasSimpleValue(ArrayNode arr, String item) {
		for (JsonNode jsonNode : arr) {
			switch (JsType.of(jsonNode)) {
				case PRIMITIVE:
					if (EQ.equals(jsonNode.asText(), item)) {
						return true;
					}
				default:
					continue;
			}
		}
		return false;
	}

	@SneakyThrows
	public static String toStringScientific(String jsonValue, boolean... pretty) {
		ObjectMapper mapper = new ObjectMapper()
				.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)//
				.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true))//
				.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);//
		return ARG.isDefEqTrue(pretty) ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(jsonValue)) : mapper.writeValueAsString(mapper.readTree(jsonValue));
	}

	public static String toStringGScientific(String jsonValue, boolean... pretty) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
			BigDecimal value = BigDecimal.valueOf(src);
			return new JsonPrimitive(value);
		});
		Gson gson = gsonBuilder.create();
		return gson.toJson(jsonValue);
	}

	//
	//
	//
	public enum JsType {
		NULL, PRIMITIVE, ARRAY, OBJECT;

		public static JsType of(JsonNode el, JsType... defRq) {
			if (el != null) {
				if (el.isValueNode()) {
					return PRIMITIVE;
				} else if (el.isObject()) {
					return OBJECT;
				} else if (el.isArray()) {
					return ARRAY;
				} else if (el.isNull()) {
					return NULL;
				}
			}
			return ARG.toDefThrowMsg(() -> X.f("JsonElement is undefined:" + (el == null ? null : el.getClass().getSimpleName())), defRq);
		}

	}
}
