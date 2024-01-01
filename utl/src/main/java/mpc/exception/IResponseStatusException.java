package mpc.exception;

public interface IResponseStatusException {

	int code();

	default boolean isOk() {
		int code = code();
		return code >= 200 && code < 300;
	}

	String getMessage();

}
