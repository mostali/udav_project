package utl_rest;

import mpc.map.MAP;
import mpe.str.CN;
import mpe.str.CV;
import mpu.core.ARG;
import mpu.IT;
import mpe.core.ERR;
import mpu.str.JOIN;
import mpu.X;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import utl_jack.MapJacksonModel;

import java.util.Map;
import java.util.Optional;

public class SrcResponseEntity<SRC, BODY> extends ResponseEntity<BODY> {

	public static final String C404_ENTITY_NOT_FOUND = "Entity not found";

	final SRC source;

	public static Object toSrcObject(ResponseEntity rspEntity, Object... defRq) {
		if (rspEntity instanceof SrcResponseEntity) {
			Object src = ((SrcResponseEntity) rspEntity).src(null);
			if (src != null) {
				return src;
			}
		}
		return ARG.toDef(defRq);
	}

	public static boolean isStatusError(ResponseEntity rspEntity) {
		return rspEntity != null && rspEntity.getStatusCode().isError();
	}

	public static <S, T> SrcResponseEntity<S, T> OK_or_404(Optional<T> body) {
		return body == null || !body.isPresent() ? SrcResponseEntity.C404(C404_ENTITY_NOT_FOUND) : SrcResponseEntity.OK_BODY(body.get());
	}

	public boolean hasSrc() {
		return src(null) != null;
	}

	public SRC src(SRC... defRq) {
		if (source != null) {
			return source;
		}
		return ARG.toDefRq(defRq);
	}

	public SrcResponseEntity(SRC source, HttpStatus status) {
		super(status);
		this.source = source;
	}

	public SrcResponseEntity(SRC source, BODY body, HttpStatus status) {
		super(body, status);
		this.source = source;
	}

	public SrcResponseEntity(SRC source, MultiValueMap<String, String> headers, HttpStatus status) {
		super(headers, status);
		this.source = source;
	}

	public SrcResponseEntity(SRC source, BODY body, MultiValueMap<String, String> headers, HttpStatus status) {
		super(body, headers, status);
		this.source = source;
	}

	/**
	 * *************************************************************
	 * ---------------------------- BUS --------------------------
	 * *************************************************************
	 */

	public static SrcResponseEntity C_JSM(HttpStatus status, String type, CharSequence msg, Object... args) {
		return toResponseEntity(status, MAP.of(CN.TYPE, type, CN.STATUS, status.isError() ? CV.ERROR : CV.OK, CN.CMSG, X.f(msg, args)));
	}

	/**
	 * *************************************************************
	 * ---------------------------- OK --------------------------
	 * *************************************************************
	 */

	public static ResponseEntity<String> ok(String body, Object... args) {
		return ok(X.f(body, args));
	}

	public static SrcResponseEntity OK_JSM(String type, String cmsg) {
		return C_JSM(HttpStatus.OK, type, cmsg);
	}

	public static SrcResponseEntity OK_JS_KV(Object... keyValues) {
		MapJacksonModel of = MapJacksonModel.of(keyValues);
		return OK_JS(of);
	}

	public static SrcResponseEntity OK_JS(Map json) {
		return toResponseEntity(HttpStatus.OK, json);
	}

	public static SrcResponseEntity OK_JS(MapJacksonModel json) {
		return toResponseEntity(HttpStatus.OK, json);
	}

	public static SrcResponseEntity FAILJS(MapJacksonModel json) {
		return toResponseEntity(HttpStatus.OK, json);
	}

	public static SrcResponseEntity OK_BODY(Object body) {
		return new SrcResponseEntity<>(null, body, HttpStatus.OK);
	}

	public static SrcResponseEntity OK(String message, Object... args) {
		return toResponseEntity(HttpStatus.OK, null, message, args);
	}

	public static SrcResponseEntity OK(HttpStatus status, String message, Object... args) {
		return toResponseEntity(status, null, message, args);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Response 400 --------------------------
	 * *************************************************************
	 */
	public static SrcResponseEntity C400_JSM(String type, CharSequence msg, Object... args) {
		return C_JSM(HttpStatus.BAD_REQUEST, type, msg, args);
	}

	public static SrcResponseEntity C400_JS(Map json) {
		return toResponseEntity(HttpStatus.BAD_REQUEST, json);
	}

	public static SrcResponseEntity C400(Exception ex) {
		return CODE(HttpStatus.BAD_REQUEST, ex, null);
	}

	public static SrcResponseEntity C400(String msg, Object... args) {
		return CODE(HttpStatus.BAD_REQUEST, null, msg, args);
	}

	public static SrcResponseEntity C400(Exception ex, String msg, Object... args) {
		return CODE(HttpStatus.BAD_REQUEST, ex, msg, args);
	}

	//
	public static SrcResponseEntity C404(String msg, Object... args) {
		return CODE(HttpStatus.NOT_FOUND, null, msg, args);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Response 500 --------------------------
	 * *************************************************************
	 */

	public static SrcResponseEntity C500_JSM(String rkRepostin, String cmsg) {
		return C_JSM(HttpStatus.INTERNAL_SERVER_ERROR, rkRepostin, cmsg);

	}

	public static SrcResponseEntity C500_JS(Map json) {
		return toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, json);
	}

	public static SrcResponseEntity C500(Throwable ex) {
		return CODE(HttpStatus.INTERNAL_SERVER_ERROR, ex, null);
	}

	public static SrcResponseEntity C500(String msg, Object... args) {
		return CODE(HttpStatus.INTERNAL_SERVER_ERROR, null, msg, args);
	}

	public static SrcResponseEntity C500(Throwable ex, String msg, Object... args) {
		return CODE(HttpStatus.INTERNAL_SERVER_ERROR, ex, msg, args);
	}

	public static SrcResponseEntity CODE(HttpStatus httpStatus, Throwable ex, String msg, Object... args) {
		return toResponseEntity(httpStatus, ex, msg, args);
	}

	static SrcResponseEntity toResponseEntity(HttpStatus httpStatus, Object cause_or_source) {
		return toResponseEntity(httpStatus, cause_or_source, null);
	}

	static SrcResponseEntity toResponseEntity(HttpStatus httpStatus, Object cause_or_source, CharSequence msg, Object... args) {
		if (X.empty(msg)) {
			IT.isEmpty(args);
		} else {
			msg = X.f(msg, args);
		}
		if (X.nullAll(msg, cause_or_source)) {
			return new SrcResponseEntity<>(null, HttpHeaders.EMPTY, httpStatus);
		} else if (cause_or_source == null) {
			return new SrcResponseEntity<>(null, msg, httpStatus);
		}
		if (cause_or_source instanceof MapJacksonModel) {
			Map<String, Object> src = ((MapJacksonModel) cause_or_source).map();
			Object body = msg != null ? msg : src;
			return new SrcResponseEntity(src, body, HttpHeaders.EMPTY, httpStatus);
		} else if (cause_or_source instanceof Throwable) {
			String reason = JOIN.allBy(" ", ERR.getAllMessages((Throwable) cause_or_source));
			if (msg == null) {
				return new SrcResponseEntity(cause_or_source, reason, HttpHeaders.EMPTY, httpStatus);
			}
			return new SrcResponseEntity(cause_or_source, msg + " " + reason, httpStatus);
		} else {
			return new SrcResponseEntity(cause_or_source, cause_or_source, httpStatus);
		}
	}
}
