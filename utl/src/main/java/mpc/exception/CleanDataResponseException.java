package mpc.exception;

import mpc.env.APP;
import mpc.json.GsonMap;
import mpc.net.ContentType;
import mpu.core.ARG;
import mpu.X;
import mpu.IT;
import mpu.core.RW;
import mpu.pare.Pare;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public class CleanDataResponseException extends RuntimeException implements ICleanMessage, IResponseStatusException {
	public final int status;

	public static Exception NOTHING(String msg) {
		return new CleanDataResponseException(msg).nothing();
	}

//	public static CleanDataResponseException downloadFile(String path) {
//
//	}

	@Override
	public int code() {
		return status;
	}

	public CleanDataResponseException() {
		this(200, null);
	}

	public CleanDataResponseException(String message, Object... args) {
		this(200, message, args);
	}

	public CleanDataResponseException(Path data) {
		this(200, RW.readString(data));
	}

	public CleanDataResponseException(int status, String message, Object... args) {
		super(X.f(IT.NN(message), args));
		this.status = status;
	}

	public CleanDataResponseException(int status, Throwable throwable, String message, Object... args) {
		super(X.f(IT.NN(message), args), throwable);
		this.status = status;
	}

	public static CleanDataResponseException ofJson(int status, Map json) {
		GsonMap gsonMap = GsonMap.of(json);
		String rsltJson;
		if (APP.IS_DEBUG_ENABLE) {
			rsltJson = gsonMap.toStringPrettyJson(true, true);
		} else {
			rsltJson = gsonMap.toStringJson(true);
		}
		return new CleanDataResponseException(status, rsltJson);
	}

	public static CleanDataResponseException of(int status, String dataRsp, Object... args) {
		return new CleanDataResponseException(status, dataRsp, args);
	}

	public static CleanDataResponseException ofNotEmptyRsp_or400(String rsp, String name) {
		Supplier<String> eval400Msg = () -> "Except data for '" + name + "'";
		if (X.blank(rsp)) {
//			throw RestStatusException.C400(UGson.toStringJson(MAP.of("error", msg400.get())));
			throw RestStatusException.C400(eval400Msg.get());
		}
		return new CleanDataResponseException(rsp);
	}

	public static CleanDataResponseException ofRspPare(Pare<Integer, String> rspPare) {
//		Supplier<String> eval400Msg = () -> "Except data for '" + name + "'";
//		if (X.blank(rsp.val())) {
//			throw RestStatusException.C400(eval400Msg.get());
//		}
		return new CleanDataResponseException(rspPare.key(), rspPare.val());
	}
//	public static CleanDataResponseException of(String dataRsp, boolean... nullable) {
//		return new CleanDataResponseException(dataRsp != null ? dataRsp : (ARG.isDefEqTrue(nullable) ? U.__NULL__ : X.throwErrorNN_OrReturn(new FNullPointerException("Data Response is null (may be set flag nullable?)"), null)));
//	}

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

	public static CleanDataResponseException C404(String msg, Object... args) {
		return new CleanDataResponseException(404, msg, args);
	}

	private boolean nothing = false;

	public boolean isNothing() {
		return nothing;
	}

	public CleanDataResponseException nothing(boolean... nothing) {
		this.nothing = ARG.isDefNotEqFalse(nothing);
		return this;
	}

	private Pare<ContentType, Path> content;

	public boolean hasContentFile() {
		return !Pare.isEmpty(content);
	}

	public Pare<ContentType, Path> getContentFile() {
		return content;
	}

	public CleanDataResponseException setContentFile(ContentType contentType, Path File) {
		content = Pare.of(contentType, File);
		return this;
	}

}
