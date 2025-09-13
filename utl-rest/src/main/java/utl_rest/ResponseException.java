package utl_rest;

import mpu.X;
import mpc.exception.FIllegalArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Deprecated
public class ResponseException extends RuntimeException {

	final HttpStatus httpStatus;

	public boolean isOk() {
		return httpStatus.is2xxSuccessful();
	}

	public ResponseException() {
		httpStatus = HttpStatus.OK;
	}

	public ResponseException(String msg, Object... args) {
		super(X.f(msg, args));
		httpStatus = HttpStatus.OK;
	}

	public ResponseException(HttpStatus httpStatus, String msg, Object... args) {
		super(X.f(msg, args));
		this.httpStatus = httpStatus;
	}

	public ResponseException(HttpStatus httpStatus, Throwable cause) {
		super(cause);
		this.httpStatus = httpStatus;
	}

	public ResponseException(HttpStatus httpStatus, Throwable cause, String msg, Object... args) {
		super(X.f(msg, args), cause);
		this.httpStatus = httpStatus;
	}

	public ResponseEntity toResponseEntity() {
		return SrcResponseEntity.toResponseEntity(httpStatus, getCause(), getMessage());
	}

	public static ResponseException ANY(int status, String msg, Object... args) {
		HttpStatus httpStatus = HttpStatus.resolve(status);
		if (httpStatus != null) {
			switch (httpStatus.series()) {
				case INFORMATIONAL:
				case SUCCESSFUL:
				case REDIRECTION:
					throw ResponseException.OK(httpStatus, msg, args);
				default:
					throw ResponseException.CODE(httpStatus, msg, args);
			}

		}
		throw new FIllegalArgumentException("Unknown Code '%s':%s", status, msg);
	}

	/**
	 * *************************************************************
	 * ---------------------------- OK --------------------------
	 * *************************************************************
	 */

	public static ResponseException OK(String message, Object... args) {
		return new ResponseException(message, args);
	}

	public static ResponseException OK(HttpStatus status, String message, Object... args) {
		return new ResponseException(status, message, args);
	}

	/**
	 * *************************************************************
	 * ---------------------------- CODE --------------------------
	 * *************************************************************
	 */

	public static ResponseException CODE(HttpStatus status, Throwable ex, String error, Object... args) {
		return new ResponseException(status, ex, error, args);
	}

	public static ResponseException CODE(HttpStatus status, String error, Object... args) {
		return new ResponseException(status, error, args);
	}

	public static ResponseException CODE(HttpStatus status, Throwable ex) {
		return new ResponseException(status, ex);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Code 400 --------------------------
	 * *************************************************************
	 */
	public static ResponseException C400(Throwable ex, String error, Object... args) {
		return new ResponseException(HttpStatus.BAD_REQUEST, ex, error, args);
	}

	public static ResponseException C400(String error, Object... args) {
		return new ResponseException(HttpStatus.BAD_REQUEST, error, args);
	}

	public static ResponseException C400(Throwable ex) {
		return new ResponseException(HttpStatus.BAD_REQUEST, ex);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Code 500 --------------------------
	 * *************************************************************
	 */
	public static ResponseException C500(Throwable ex, String error, Object... args) {
		return new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ex, error, args);
	}

	public static ResponseException C500(String error, Object... args) {
		return new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR, error, args);
	}

	public static ResponseException C500(Throwable ex) {
		return new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ex);
	}

}
