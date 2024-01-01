package mpc.net;

import lombok.Getter;
import mpc.X;
import mpc.arr.Arr;

@Getter
public class IllegalHttpStatusException extends RuntimeException {
	private final Integer[] legalCode;
	private final INetRsp response;

	public int code() {
		return response.code();
	}

	public IllegalHttpStatusException(Integer[] legalCode, INetRsp response) {
		super(X.f("Response Code [%s] is illegal, Except %s, Msg [%s]\n%s", response.code(), Arr.as(legalCode), response.msg(), response.any()));
		this.legalCode = legalCode;
		this.response = response;
	}

}
