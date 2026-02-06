package mpc.json;

import com.google.gson.*;
import mpu.X;
import mpu.core.ARG;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.FIllegalStateException;
import mpc.exception.WhatIsTypeException;
import mpu.core.RW;
import mpc.fs.fd.RES;
import mpu.pare.Pare;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Deprecated
public class JSoon {

//	public static void main(String[] args) throws IOException {
//
//		JSoon j = JSoon.ofFile("test.json");
//
//		P.p("first" + j.first("id"));
//		P.p("values" + j.values());
//		P.p("VALUES is " + j.first("tests").values());
//		P.p("toString" + j);
//		P.p("tests : " + j.first("tests"));
//		P.p("commands : " + j.first("tests").first("commands"));
//		P.p("commands : " + j.find("tests", "commands"));
//
//		P.p("targets : " + j.first("tests").first("commands").first("targets").first());
//		P.p("targets : " + j.find("tests", "commands", "targets"));
//		P.p("targets : " + j.find("tests", "commands", "targets").size());
//
//		U.exit(j.first("tests").values());
//
//	}

	public static final JSoon EMPTY_JSON = new JSoon();

	public final String _name;
	public final JsonElement _entity;

	public JsonElement self() {
		return _entity;
	}

	public final UGson.TypeGson _typeJson;

	public JSoon() {
		_typeJson = null;
		_entity = null;
		_name = null;

	}

	public JSoon(JsonElement inputJson, String name) {
		_typeJson = UGson.TypeGson.of(inputJson);
		_entity = inputJson;
		this._name = name;
	}

	public List<String> keys() {
		JsonObject obj = self().getAsJsonObject(); //since you know it's a JsonObject
		Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();//will return members of your object
		for (Map.Entry<String, JsonElement> entry : entries) {
			System.out.println(entry.getKey());
		}
		return obj.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toList());
	}

	public JSoon deserialize() {
		return JSoon.of(value().toString());
	}

	public Stream<Pare<Object, JsonElement>> stream() {
		switch (_typeJson) {
			case ARRAY: {
				List<Pare<Object, JsonElement>> l = new ArrayList();
				JsonArray arr = self().getAsJsonArray();
				for (int i = 0; i < arr.size(); i++) {
					l.add(Pare.of(i, arr.get(i)));
				}
				return l.stream();
			}
			case OBJECT: {
				List<Pare<Object, JsonElement>> l = new ArrayList();
				Set<Map.Entry<String, JsonElement>> arr = self().getAsJsonObject().entrySet();
				return arr.stream().map(e -> Pare.of(e.getKey(), e.getValue()));
			}
			default:
				throw new UnsupportedOperationException(_typeJson.name());
		}
	}

	private void checkNotNull() {
		if (_typeJson == null) {
			throw new IllegalStateException("Json is null");
		}
	}

	public static JSoon of(JsonElement inputJson, String name) {
		if (inputJson == null) {
			return EMPTY_JSON;
		}
		JSoon json = new JSoon(inputJson, name);
		return json;
	}

	public static JSoon ofStr(Object objRepresentJson, JSoon... defRq) {
		return of(objRepresentJson.toString(), defRq);
	}

	public static JSoon ofRsrc(String file, String[] args, JSoon... defRq) {
		return of(X.f(RES.readString(file), args), defRq);
	}

	public static JSoon of(String inputJson, String[] args, JSoon... defRq) {
		return of(X.f(inputJson, args), defRq);
	}

	public static JSoon of(String inputJson, JSoon... defRq) {
		try {
			return of(toJsonElement(inputJson), null);
		} catch (JsonSyntaxException ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static JSoon ofFile(String file, String[] args, JSoon... defRq) throws FileNotFoundException {
		return of(X.f(RW.readString(Paths.get(file)), args), defRq);
	}

	public static JSoon ofFile(String file) throws FileNotFoundException {
		JsonElement json = new Gson().fromJson(new FileReader(file), JsonElement.class);
		return JSoon.of(json, null);
	}

	public static String toJsonString(Object obj) {
		String json = new Gson().toJson(obj);
		return json;
	}

	public static <T> T toObject(String json, Class<T> type) {
		T t = new Gson().fromJson(json, type);
		return t;
	}

	public String toStringJson() {
		checkNotNull();
		switch (_typeJson) {
			case PRIMITIVE:
				return this._entity.getAsString();
			case ARRAY:
			case OBJECT:
				return this._entity.toString();
			default:
				throw new WhatIsTypeException(_typeJson);
		}
	}

	public int size() {
		checkNotNull();
		switch (_typeJson) {
			case PRIMITIVE:
				return 1;
			case ARRAY:
				return this._entity.getAsJsonArray().size();
			case OBJECT:
				return this._entity.getAsJsonObject().size();
			default:
				throw new WhatIsTypeException(_typeJson);
		}
	}

	public <T> T toPrimitive(Class<T> toClass) {
		return castTo(toClass);
	}

	public <T> T castTo(Class<T> toClass) {
		switch (_typeJson) {
			case OBJECT:
				return new Gson().fromJson(this._entity.toString(), toClass);
			case PRIMITIVE:
				return castToIqJavaType(this._entity, toClass);
			case ARRAY:
				break;
		}
		throw new FIllegalArgumentException("Wrong cast json-entity [%s] to type [%s] ", this._typeJson, toClass);
	}

	public static <T> T castToIqJavaType(JsonElement jsonElement, Class<T> to) {
		try {
			return castToPrimitiveJavaTypeRq(jsonElement, to);
		} catch (WhatIsTypeException ex) {
			if (to == UUID.class) {
				try {
					String typ = castToPrimitiveJavaTypeRq(jsonElement, String.class);
					UUID uuid = UUID.fromString(typ);
					return (T) uuid;
				} catch (WhatIsTypeException exString) {
					throw new WhatIsTypeException(exString, "Error get UUID type");
				}
			}
			throw ex;
		}
	}

	public static <T> T castToPrimitiveJavaTypeRq(JsonElement jsonElement, Class<T> to) {
		if (to == String.class) {
			return (T) jsonElement.getAsString();
		} else if (to == Integer.class) {
			return (T) (Integer) jsonElement.getAsInt();
		} else if (to == Long.class) {
			return (T) (Long) jsonElement.getAsLong();
		} else if (to == Boolean.class) {
			return (T) (Boolean) jsonElement.getAsBoolean();
		} else if (to == Double.class) {
			return (T) (Double) jsonElement.getAsDouble();
		} else if (to == Float.class) {
			return (T) (Float) jsonElement.getAsFloat();
		} else if (to == Short.class) {
			return (T) (Short) jsonElement.getAsShort();
		} else if (to == Byte.class) {
			return (T) (Byte) jsonElement.getAsByte();
		} else if (to == Number.class) {
			return (T) jsonElement.getAsNumber();
		} else if (to == BigDecimal.class) {
			return (T) jsonElement.getAsBigDecimal();
		} else if (to == BigInteger.class) {
			return (T) jsonElement.getAsBigInteger();
		}
		throw new WhatIsTypeException("Wrong primitive java type [%s], json-element [%s]", to.getName(), jsonElement);
	}

	public JSoon find(String... keys) {
		return findRq(false, keys);
	}

	public JSoon get(String... keys) {
		return findRq(true, keys);
	}

	public JSoon findRq(boolean throwErrorIfObjectByKeyNotFound, String... keys) {
		if (_typeJson == null) {
			throw new IllegalStateException("Json is null");
		}
		switch (keys.length) {
			case 0:
				throw new IllegalStateException("Set keys");
			case 1:
				JSoon f = first(keys[0]);
				if (f.isNull()) {
					if (throwErrorIfObjectByKeyNotFound) {
						throw new IllegalStateException("Key not found ::: " + keys[0]);
					}
					return null;
				}
				return f;
		}

		JSoon next = first(keys[0]);
		if (next.isNull()) {
			if (throwErrorIfObjectByKeyNotFound) {
				throw new IllegalStateException("Key not found ::: " + keys[0]);
			}
			return null;
		}
		String[] nkeys = new String[keys.length - 1];
		System.arraycopy(keys, 1, nkeys, 0, nkeys.length);
		return next.findRq(throwErrorIfObjectByKeyNotFound, nkeys);
	}


	public List<JSoon> values() {
		checkNotNull();
		List<JSoon> values = new ArrayList();
		switch (_typeJson) {
			case PRIMITIVE:
				values.add(this);
				return values;
			case ARRAY:
				JsonArray arr = this._entity.getAsJsonArray();
				for (int i = 0; i < arr.size(); i++) {
					values.add(of(arr.get(i), this._name));
				}
				return values;
			case OBJECT:
				JsonObject obj = this._entity.getAsJsonObject();
				if (obj.size() == 0) {
					return values;
				}
				for (Map.Entry<String, JsonElement> key : obj.entrySet()) {
					values.add(of(key.getValue(), key.getKey()));
				}
				return values;
			default:
				throw new WhatIsTypeException(_typeJson);
		}
	}


	public JSoon first() {
		checkNotNull();

		switch (_typeJson) {
			case PRIMITIVE:
				return this;
			case ARRAY:
				JsonArray arr = this._entity.getAsJsonArray();
				if (arr.size() == 0) {
					return EMPTY_JSON;
				}
				return of(arr.get(0), this._name);
			case OBJECT:
				JsonObject obj = this._entity.getAsJsonObject();
				if (obj.size() == 0) {
					return EMPTY_JSON;
				}
				Map.Entry<String, JsonElement> first = obj.entrySet().stream().findFirst().get();
				return of(first.getValue(), first.getKey());
			default:
				throw new WhatIsTypeException(_typeJson);
		}
	}


	public boolean isNull() {
		return _typeJson == null;
	}

	public JSoon first(String key) {
		return first(key, EMPTY_JSON);
	}

	public JSoon first(String key, JSoon DEFAULT) {
		checkNotNull();
		switch (_typeJson) {
			case PRIMITIVE:
				return new JSoon();
			case ARRAY:
				JsonArray arr = this._entity.getAsJsonArray();
				if (arr.size() == 0) {
					return DEFAULT;
				}
				for (JsonElement e : arr) {
					JSoon tmp = JSoon.of(e, this._name);
					JSoon f = tmp.first(key);
					if (!f.isNull()) {
						return f;
					}
				}
				return DEFAULT;
			case OBJECT:
				JsonObject obj = this._entity.getAsJsonObject();
				if (obj.size() == 0) {
					return DEFAULT;
				}
				JsonElement ent = obj.get(key);
				if (ent == null) {
					return DEFAULT;
				}
				return of(ent, key);
			default:
				throw new WhatIsTypeException(_typeJson);
		}
	}

	public String toStringDebug() {
		return " /JSON{" +
				":::" + _typeJson +
				", " + this._name + "=" + _entity +
				'}';
	}

	public static Boolean TO_STRING_DEBUG = false;

	@Override
	public String toString() {
		return TO_STRING_DEBUG ? toStringDebug() : self().toString();
	}

	public static JsonElement toJsonElement(String inputJson) {
		return new Gson().fromJson(inputJson, JsonElement.class);
	}

	public JSoon add(String property, Object value) {
		checkNotNull();
		switch (_typeJson) {
			case PRIMITIVE:
			case ARRAY:
				throw new FIllegalStateException("Unreachable store in '%s' with key '%s', value ::: " + value, _typeJson, property);
			case OBJECT:
				store(((JsonObject) _entity), property, value);
				return this;
			default:
				throw new WhatIsTypeException(_typeJson);
		}
	}

	public static void store(JsonObject jsonObject, String property, Object value) {
		if (value == null) {
			jsonObject.addProperty(property, "");
		} else if (value instanceof JsonElement) {
			jsonObject.add(property, (JsonElement) value);
		} else if (value instanceof String) {
			jsonObject.addProperty(property, (String) value);
		} else if (value instanceof CharSequence) {
			jsonObject.addProperty(property, value.toString());
		} else if (value instanceof Number) {
			jsonObject.addProperty(property, (Number) value);
		} else if (value instanceof Boolean) {
			jsonObject.addProperty(property, (Boolean) value);
		} else if (value instanceof Character) {
			jsonObject.addProperty(property, (Character) value);
		} else {
			throw new IllegalArgumentException("What is type of object? ::: " + value.getClass() + " ::: " + value);
		}
	}

	/**
	 * return first value
	 */
	public Object value() {
		JSoon first = first();
		if (first == null) {
			return null;
		}
		return toValueObject(first);
	}

	public Object value(String key) {
		JSoon first = first(key, null);
		if (first == null) {
			return null;
		}
		return toValueObject(first);
	}

	public static Object toValueObject(JSoon json) {
		switch (json._typeJson) {
			case PRIMITIVE:
				return toValueObject(json.getAsJsonPrimitive());
			case ARRAY:
				return json.getAsJsonArray().getAsJsonArray();
			case OBJECT:
				return json.getAsJsonObject();
			default:
				throw new WhatIsTypeException(json._typeJson);
		}
	}

	public static Object toValueObject(JsonPrimitive json) {
		if (json.isString()) {
			return json.getAsString();
		} else if (json.isNumber()) {
			return json.getAsNumber();
		} else if (json.isBoolean()) {
			return json.getAsBoolean();
		} else {
			throw new WhatIsTypeException("What is type of JsonPrimitive? ::: " + json);
		}
	}

	private JsonPrimitive getAsJsonPrimitive() {
		if (_entity.isJsonPrimitive()) {
			return _entity.getAsJsonPrimitive();
		}
		throw new IllegalArgumentException("It is not json primitive ::: " + toString());
	}

	private JsonObject getAsJsonObject() {
		if (_entity.isJsonPrimitive()) {
			return _entity.getAsJsonObject();
		}
		throw new IllegalArgumentException("It is not json object ::: " + toString());
	}

	private JsonArray getAsJsonArray() {
		if (_entity.isJsonPrimitive()) {
			return _entity.getAsJsonArray();
		}
		throw new IllegalArgumentException("It is not json array ::: " + toString());
	}

}
