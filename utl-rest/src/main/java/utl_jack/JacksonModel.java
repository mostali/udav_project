package utl_jack;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.SneakyThrows;
import mpu.IT;

import java.io.Serializable;
import java.util.Map;

public interface JacksonModel<M extends JacksonModel> extends Serializable {

	default String toJson() {
		return UJack.serialize(this, true);
	}

	default M fromJson(CharSequence json) {
		return (M) UJack.deserialize(json.toString(), getClass());
	}

	@SneakyThrows
	default JsonNode toJsonNode() {
		ObjectMapper mapper = new ObjectMapper();
		String content = toJson();
		JsonNode jn = mapper.readTree(content);
		return jn;
	}

	Map<String, Object> getJson(boolean... createIfNull);

	default ArrayNode addToArray(String fieldname, Object json, boolean... createSrcIfNull) {
		Map<String, Object> src = getJson(createSrcIfNull);
		IT.NN(src, "array is null by field", fieldname);
		return UJack.addToArray(src, fieldname, (JsonNode) UJack.toNodeFromObjectAuto(json));

	}

	default JsonNode addToObject(String fieldname, String childFieldName, Object json, boolean... createSrcIfNull) {
		Map<String, Object> src = getJson(createSrcIfNull);
		IT.NN(src, "object is null by field & childFieldName", fieldname, childFieldName);
		return UJack.addToObject(src, fieldname, childFieldName, (JsonNode)UJack.toNodeFromObjectAuto(json));
	}

}
