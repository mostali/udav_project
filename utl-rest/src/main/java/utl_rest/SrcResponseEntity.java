package utl_rest;

import mpc.*;
import mpc.args.ARG;
import mpc.ERR;
import mpc.core.UErr;
import mpc.str.JOIN;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import utl_jack.MapJacksonModel;

import java.util.Map;

public class SrcResponseEntity<SRC, BODY> extends ResponseEntity<BODY> {

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
	 * ---------------------------- OK --------------------------
	 * *************************************************************
	 */

	public static ResponseEntity<String> ok(String body, Object... args) {
		return ok(X.f(body, args));
	}

	public static SrcResponseEntity OKJS(Object... keyValues) {
		MapJacksonModel of = MapJacksonModel.of(keyValues);
		return OKJS(of);
	}

	public static SrcResponseEntity OKJS(Map json) {
		return toResponseEntity(HttpStatus.OK, json);
	}

	public static SrcResponseEntity OKJS(MapJacksonModel json) {
		return toResponseEntity(HttpStatus.OK, json);
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
	public static SrcResponseEntity C400(Exception ex) {
		return CODE(HttpStatus.BAD_REQUEST, ex, null);
	}

	public static SrcResponseEntity C400(String msg, Object... args) {
		return CODE(HttpStatus.BAD_REQUEST, null, msg, args);
	}

	public static SrcResponseEntity C400(Exception ex, String msg, Object... args) {
		return CODE(HttpStatus.BAD_REQUEST, ex, msg, args);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Response 500 --------------------------
	 * *************************************************************
	 */
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

	static SrcResponseEntity toResponseEntity(HttpStatus httpStatus, Object cause_or_source, String msg, Object... args) {
		if (X.empty(msg)) {
			ERR.isEmpty(args);
		} else {
			msg = X.f(msg, args);
		}
		if (X.isNullAll(msg, cause_or_source)) {
			return new SrcResponseEntity<>(null, HttpHeaders.EMPTY, httpStatus);
		} else if (cause_or_source == null) {
			return new SrcResponseEntity<>(null, msg, httpStatus);
		}
		if (cause_or_source instanceof MapJacksonModel) {
			Map<String, Object> src = ((MapJacksonModel) cause_or_source).map();
			Object body = msg != null ? msg : src;
			return new SrcResponseEntity(src, body, HttpHeaders.EMPTY, httpStatus);
		} else if (cause_or_source instanceof Throwable) {
			String reason = JOIN.allBy(" ", UErr.getAllMessages((Throwable) cause_or_source));
			if (msg == null) {
				return new SrcResponseEntity(cause_or_source, reason, HttpHeaders.EMPTY, httpStatus);
			}
			return new SrcResponseEntity(cause_or_source, msg + " " + reason, httpStatus);
		} else {
			return new SrcResponseEntity(cause_or_source, cause_or_source, httpStatus);
		}
	}
}
