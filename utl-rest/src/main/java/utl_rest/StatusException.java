package utl_rest;

import mpc.env.APP;
import mpe.core.ERR;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.IResponseStatusException;
import mpu.str.JOIN;
import mpu.X;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import utl_jack.MapJacksonModel;

import java.util.Map;

public class StatusException extends org.springframework.web.server.ResponseStatusException implements IResponseStatusException {

	public static boolean isOk(Throwable endRequestFinish) {
		return endRequestFinish instanceof StatusException && ((StatusException) endRequestFinish).isOk();
	}

	public static void C403_or_404() {
		if (APP.IS_DEBUG_ENABLE) {
			throw StatusException.C403("access denied");
		}
		throw StatusException.C404();
	}

	@Override
	public int code() {
		return getStatus().value();
	}

	public boolean isOk() {
		return getStatus().is2xxSuccessful();
	}

	public StatusException() {
		this(HttpStatus.OK);
	}

	public StatusException(HttpStatus status) {
		super(status);
	}

	public StatusException(String msg, Object... args) {
		this(HttpStatus.OK, msg, args);
	}

	private Map json;

	public StatusException(HttpStatus httpStatus, Map json) {
		super(httpStatus);
		this.json = json;

	}

	public StatusException(HttpStatus httpStatus, String msg, Object... args) {
		super(httpStatus, X.f(msg, args));
	}

	public StatusException(HttpStatus httpStatus, Throwable cause, String msg, Object... args) {
		super(httpStatus, X.f(msg, args), cause);
	}

	public ResponseEntity toResponseEntity() {
		if (json != null) {
			HttpHeaders hh = new HttpHeaders();
			hh.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity(json, hh, getStatus());
		}
		String reasonMsg = getReason();
		Throwable causeEx = getCause();
		HttpStatus httpStatus = getStatus();
		if (X.nullAll(reasonMsg, causeEx)) {
			return new ResponseEntity<>(HttpHeaders.EMPTY, httpStatus);
		} else if (causeEx == null) {
			return new ResponseEntity<>(reasonMsg, httpStatus);
		}
		String causeReason = JOIN.allBy(" ", ERR.getAllMessages(causeEx));
		if (reasonMsg == null) {
			return new ResponseEntity<>(causeReason, httpStatus);
		}
		return new ResponseEntity<>(reasonMsg + " " + causeReason, httpStatus);
	}

	public static StatusException ANY(int status, String msg, Object... args) {
		HttpStatus httpStatus = HttpStatus.resolve(status);
		if (httpStatus != null) {
			switch (httpStatus.series()) {
				case INFORMATIONAL:
				case SUCCESSFUL:
				case REDIRECTION:
					throw StatusException.OK(httpStatus, msg, args);
				default:
					throw StatusException.CODE(httpStatus, msg, args);
			}
		}
		throw new FIllegalArgumentException("Unknown Code '%s':%s", status, msg);
	}

	/**
	 * *************************************************************
	 * ---------------------------- OK --------------------------
	 * *************************************************************
	 */

	public static StatusException OK(String message, Object... args) {
		return new StatusException(message, args);
	}

	public static StatusException OK(HttpStatus status, String message, Object... args) {
		return new StatusException(status, message, args);
	}

	public static StatusException OK204(String message, Object... args) {
		return CODE(HttpStatus.NO_CONTENT, message, args);
	}

	/**
	 * *************************************************************
	 * ---------------------------- ERR --------------------------
	 * *************************************************************
	 */

	public static StatusException CODE(HttpStatus status, Throwable ex, String error, Object... args) {
		return new StatusException(status, ex, error, args);
	}

	public static StatusException CODE(HttpStatus status, String msg, Object... args) {
		return new StatusException(status, msg, args);
	}

	public static StatusException CODE(HttpStatus status) {
		return new StatusException(status);
	}

	public static StatusException JSON(HttpStatus status, MapJacksonModel json) {
		return new StatusException(status, json.toJson());
	}

	public static StatusException C401() {
		return new StatusException(HttpStatus.UNAUTHORIZED);
	}

	public static StatusException C401(String msg, Object... args) {
		return new StatusException(HttpStatus.UNAUTHORIZED, msg, args);
	}

	public static StatusException C404() {
		return new StatusException(HttpStatus.NOT_FOUND);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Code 404 --------------------------
	 * *************************************************************
	 */

	public static StatusException C404(String msg, Object... args) {
		return new StatusException(HttpStatus.NOT_FOUND, msg, args);
	}

	public static StatusException C404(Throwable err, String reason, Object... args) {
		return new StatusException(HttpStatus.NOT_FOUND, err, reason, args);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Code 403 --------------------------
	 * *************************************************************
	 */

	public static StatusException C403() {
		return new StatusException(HttpStatus.FORBIDDEN);
	}

	public static StatusException C403(String msg, Object... args) {
		return new StatusException(HttpStatus.FORBIDDEN, msg, args);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Code 400 --------------------------
	 * *************************************************************
	 */
	public static StatusException C400(Throwable ex, String error, Object... args) {
		return new StatusException(HttpStatus.BAD_REQUEST, ex, error, args);
	}

	public static StatusException C400(String error, Object... args) {
		return new StatusException(HttpStatus.BAD_REQUEST, error, args);
	}

	public static StatusException C400(Map json) {
		return new StatusException(HttpStatus.BAD_REQUEST, json);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Code 400 --------------------------
	 * *************************************************************
	 */
	public static StatusException C409(String error, Object... args) {
		return new StatusException(HttpStatus.CONFLICT, error, args);
	}

	public static StatusException C409(Throwable ex, String error, Object... args) {
		return new StatusException(HttpStatus.CONFLICT, ex, error, args);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Code 500 --------------------------
	 * *************************************************************
	 */
	public static StatusException C500(Throwable ex, String error, Object... args) {
		return new StatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex, error, args);
	}

	public static StatusException C500(String error, Object... args) {
		return new StatusException(HttpStatus.INTERNAL_SERVER_ERROR, error, args);
	}

	@Override
	public String getMessage() {
		return super.getMessage() + (json == null ? "" : ":" + json);
	}
}
