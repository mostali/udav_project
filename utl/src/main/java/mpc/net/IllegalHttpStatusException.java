package mpc.net;

import lombok.Getter;
import mpu.X;
import mpu.core.ARR;

@Getter
public class IllegalHttpStatusException extends RuntimeException {
	private final Integer[] legalCode;
	private final INetRsp response;

	public static boolean isCode(Exception ex, int code) {
		return ex instanceof IllegalHttpStatusException && ((IllegalHttpStatusException) ex).code() == code;
	}

	public int code() {
		return response.code();
	}

	public IllegalHttpStatusException(Integer[] legalCode, INetRsp response) {
		super(X.f("Response Code [%s] is illegal, Except %s, Msg [%s]\n%s", response.code(), ARR.as(legalCode), response.msg(), response.any()));
		this.legalCode = legalCode;
		this.response = response;
	}

}
