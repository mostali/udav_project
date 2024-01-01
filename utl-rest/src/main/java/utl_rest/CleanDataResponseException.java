package utl_rest;

import mpc.args.ARG;
import mpc.core.U;
import mpc.X;
import mpc.ERR;
import mpc.exception.FNullPointerException;
import mpc.exception.ICleanMessage;

public class CleanDataResponseException extends RuntimeException implements ICleanMessage {
	public final int status;

	public CleanDataResponseException() {
		this(200, null);
	}

	public CleanDataResponseException(String message, Object... args) {
		this(200, message, args);
	}

	public CleanDataResponseException(int status, String message, Object... args) {
		super(X.f(ERR.NN(message), args));
		this.status = status;
	}

	public CleanDataResponseException(int status, Throwable throwable, String message, Object... args) {
		super(X.f(ERR.NN(message), args), throwable);
		this.status = status;
	}

	public static CleanDataResponseException of(String dataRsp, boolean... nullable) {
		return new CleanDataResponseException(dataRsp != null ? dataRsp : (ARG.isDefEqTrue(nullable) ? U.__NULL__ : X.throwErrorNN_OrReturn(new FNullPointerException("Data Response is null (may be set flag nullable?)"), null)));
	}


	@Override
	public String getCleanMessage() {
		return getMessage();
	}

	public String getCleanData() {
		return getMessage();
	}

	public static CleanDataResponseException OK(String msg, Object... args) {
		return new CleanDataResponseException(200, msg, args);
	}

	public static CleanDataResponseException C400(String msg, Object... args) {
		return new CleanDataResponseException(400, msg, args);
	}
}
