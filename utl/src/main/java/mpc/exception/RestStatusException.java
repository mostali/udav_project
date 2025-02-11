package mpc.exception;

import mpe.core.U;
import mpe.restapp.Rsp;
import mpu.X;
import mpu.IT;

public class RestStatusException extends RuntimeException implements ICleanMessage, IResponseStatusException {
	public final int code;

	public RestStatusException() {
		this(200, null);
	}

	public RestStatusException(String message, Object... args) {
		this(200, message, args);
	}

	public RestStatusException(int code, String message, Object... args) {
		super(X.f(IT.NN(message), args));
		this.code = code;
	}

	public RestStatusException(int code, Throwable throwable, String message, Object... args) {
		super(X.f(IT.NN(message), args), throwable);
		this.code = code;
	}

	public static RestStatusException ofSafeNull(String dataRsp) {
		return new RestStatusException(dataRsp == null ? U.__NULL__ : dataRsp);
	}

	@Override
	public String getCleanMessage() {
		return getMessage();
	}

	public String getCleanData() {
		return getMessage();
	}

	public static RestStatusException OK(String msg, Object... args) {
		return new RestStatusException(200, msg, args);
	}

	public static RestStatusException C400(String msg, Object... args) {
		return new RestStatusException(400, msg, args);
	}

	@Override
	public int code() {
		return code;
	}

	public boolean isOk() {
		return code >= 200 && code < 300;
	}

	public Rsp.STATE codeType() {
		return Rsp.STATE.ofCode(code());
	}

}
