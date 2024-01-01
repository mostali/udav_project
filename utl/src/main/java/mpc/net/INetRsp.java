package mpc.net;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import mpc.ERR;
import mpc.arr.Arr;
import mpc.args.ARG;
import mpc.exception.FIllegalStateException;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpc.json.UGson;
import mpc.rt.ValueOutStream;
import mpc.str.ObjTo;

import java.io.InputStream;
import java.io.InputStreamReader;

public interface INetRsp<B, E> {

	default <T> T toType(Class<T> type, T... defRq) {
		if (type == null) {
			return (T) this;
		}
		Object any = any();
		if (type.isAssignableFrom(any.getClass())) {
			return (T) any;
		} else if (CharSequence.class.isAssignableFrom(type)) {
			return (T) strAny();
		} else if (InputStream.class.isAssignableFrom(type)) {
			return (T) toInputStream();
		} else if (byte[].class.isAssignableFrom(type)) {
			throw NI.stop("Who need bytes? May be use toInputStream()?");
		}
		if (any instanceof InputStream) {
			T typeGson0 = ERR.isTypeGson0(new InputStreamReader((InputStream) any), type);
			return typeGson0;
		} else if (any instanceof CharSequence) {
			T typeGson0 = ERR.isTypeGson0((CharSequence) any, type, true);
			return typeGson0;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Illegal response '%s' to convert type '%s'", any, type), defRq);
	}

	default InputStream toInputStream() {
		Object bodyAny = any();
		if (bodyAny instanceof InputStream) {
			return (InputStream) bodyAny;
		}
		throw new NI("need impl toInputStream()");
	}

	static void checkLeggalHttpStatus(Integer[] successHttpCode, INetRsp netJRsp) {
		if (ARG.isDef(successHttpCode) && !Arr.contains(successHttpCode, netJRsp.code())) {
			throw new IllegalHttpStatusException(successHttpCode, netJRsp);
		}
	}

	default String strAny(String... defRq) {
		return isErrorStatus() ? errStr(defRq) : bodyStr(defRq);
	}


	int code();

	B body();

	default String errStr(String... defRq) {
		return errAs(String.class, defRq);
	}

	default String bodyStr(String... defRq) {
		return bodyAs(String.class, defRq);
	}

	default <T> T bodyAs(Class<T> type, T... defRq) {
		B data = body();
		if (data != null) {
			if (type.isAssignableFrom(data.getClass())) {
				return (T) data;
			} else if (CharSequence.class.isAssignableFrom(type)) {
				if (InputStream.class.isAssignableFrom(data.getClass())) {
					return (T) ValueOutStream.of((InputStream) data).getValue();
				}
				return (T) data.toString();
			}
		}
		return ObjTo.objTo(data, type, defRq);
	}

	default <T> T errAs(Class<T> type, T... defRq) {
		Object errBody = err();
		if (errBody != null) {
			if (type.isAssignableFrom(errBody.getClass())) {
				return (T) errBody;
			}
			String errData = errBody.toString();
			if (CharSequence.class.isAssignableFrom(type)) {
				return (T) errData;
			}
		}
		return ObjTo.objTo(errBody, type, defRq);
	}

	E err();

	default String msgWithError() {
		return msg() + ":" + err();
	}

	default boolean isErrorStatus() {
		return INetRsp.isErrorStatus(code());
	}

	String msg();

	default boolean isSuccess2__() {
		return isSuccessStatus(code());
	}

	public static boolean isErrorStatus(int code) {
		return code >= 400 && code <= 599;
	}

	public static boolean isSuccessStatus(int code) {
		return code >= 200 && code <= 299;
	}

	default INetRsp throwIsNoOk() {
		if (isSuccess2__()) {
			return this;
		}
		throw new NetResponseException(this);
	}

	default NetResponseException throwError() {
		NetResponseException netResponseException = new NetResponseException(this);
		if (true) {
			throw netResponseException;
		}
		return netResponseException;
	}

	default String getErrorJsonValue(String[] anyKey, String... defRq) {
		try {
			if (isErrorStatus()) {
				JsonObject jo = errAs(JsonObject.class);
				return UGson.getValueSimpleOr(jo, anyKey);
			}
			throw new FIllegalStateException("Illegal getting values [%s] from successfully NetResponse\n%s", Arr.of(anyKey), toString(this));
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Json values [%s] not found from error", anyKey), defRq);
		}
	}

	default Object any() {
		return isErrorStatus() ? err() : body();
	}

	@RequiredArgsConstructor
	public static class NetResponseException extends RuntimeException {
		public final INetRsp rsp;
	}

	public static String toString(INetRsp rsp) {
		String s = rsp.getClass().getSimpleName() + ":" + rsp.code() + ":" + rsp.msg() + "\n";
		return rsp.isErrorStatus() ? s + "err:" + rsp.err() : s + "body:" + rsp.body();
	}
}
