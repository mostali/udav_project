package utl_jack;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

public class RspJacksonModel extends MapJacksonModel {

	public RspJacksonModel(Map map) {
		super(map);
	}

	public static MapJacksonModel CODE(HttpStatus status, String cause) {
		return of("code", status.value(), "cause", cause);
	}

	public static ResponseStatusException toStatusException(HttpStatus httpStatus, String cause) {
		return new ResponseStatusException(httpStatus, getStandartResponseJson(httpStatus, cause).toJson());
	}

	public static MapJacksonModel getStandartResponseJson(HttpStatus status, String cause) {
		return MapJacksonModel.of("code", status.value(), "cause", cause);
	}
}
