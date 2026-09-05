package mpc.exception;

import mpe.restapp.Rsp;

public interface IResponseStatusException {

	int code();

	default boolean isOk() {
		int code = code();
		return code >= 200 && code < 300;
	}

	String getMessage();

	default Rsp.STATE codeHttpType() {
		return Rsp.checkCodeHttpType(Rsp.STATE.ofCode(code()));
	}

}
