package mpc.net;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.IT;
import mpu.core.ARR;
import mpu.core.ARG;
import mpc.exception.FIllegalStateException;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpc.json.UGson;
import mpe.rt.ValueOutStream;
import mpc.str.ObjTo;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public interface INetRsp<B, E> {

	@SneakyThrows
	default <T> T toType(Class<T> type, T... defRq) {
		if (type == null || INetRsp.class.isAssignableFrom(type)) {
			return (T) this;
		}
		Object any = any();
		if (type.isAssignableFrom(any.getClass())) {
			return (T) any;
		} else if (CharSequence.class.isAssignableFrom(type)) {
			return (T) anyStr();
		} else if (InputStream.class.isAssignableFrom(type)) {
			return (T) toInputStream();
		} else if (byte[].class.isAssignableFrom(type)) {
			return (T) IOUtils.toByteArray(toInputStream());
//		throw NI.stop("Who need bytes? May be use toInputStream()?");
		}
		if (any instanceof InputStream) {
			T typeGson0 = IT.isJsonType(new InputStreamReader((InputStream) any), type);
			return typeGson0;
		} else if (any instanceof CharSequence) {
			T typeGson0 = IT.isJsonType((CharSequence) any, type);
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
		if (ARG.isDef(successHttpCode) && !ARR.contains(successHttpCode, netJRsp.code())) {
			throw new IllegalHttpStatusException(successHttpCode, netJRsp);
		}
	}

	static void checkLeggalHttpStatus(Integer[] successHttpCode, int code) {
		if (ARG.isDef(successHttpCode) && !ARR.contains(successHttpCode, code)) {
			throw new IllegalHttpStatusException(successHttpCode, code);
		}
	}

	default String anyStr(String... defRq) {
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

	@SneakyThrows
	default <T> T errAs(Class<T> type, T... defRq) {
		Object errBody = err();
		if (errBody != null) {
			if (type.isAssignableFrom(errBody.getClass())) {
				return (T) errBody;
			} else if (CharSequence.class.isAssignableFrom(type)) {
				return (T) new String(errBytes(), Charset.defaultCharset());
			} else if (InputStream.class == type) {
				return (T) new ByteArrayInputStream(errBytes());
			} else if (byte[].class == type || byte.class == type) {
				return (T) errBytes();
			} else if (JsonObject.class == type) {
				return (T) UGson.toJO(errBytes());
			}
//			else if (GsonMap.class == type) {
//				return GsonMap.of()
//			}
			NI.stop("Who?" + type + ":" + errBody);
		}
		//TODO WTF?
		return ObjTo.objTo(errBody, type, defRq);
	}

	byte[] errBytes(byte[]... defRq);

	default InputStream errIs(byte[]... defRq) {
		return new ByteArrayInputStream(errBytes(defRq));
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
			throw new FIllegalStateException("Illegal getting values [%s] from successfully NetResponse\n%s", ARR.of(anyKey), toString(this));
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Json values [%s] not found from error", anyKey), defRq);
		}
	}

	default Object any() {
//		try {
			return isErrorStatus() ? err() : body();
//		} catch (Exception ex) {
//			return body();
//		}
	}


	@RequiredArgsConstructor
	public static class NetResponseException extends RuntimeException {
		public final INetRsp rsp;
	}

	public static String toString(INetRsp rsp) {
		String s = rsp.getClass().getSimpleName() + ":" + rsp.code() + ":" + rsp.msg() + "\n";
		return rsp.isErrorStatus() ? s + "err:" + rsp.err() : s + "body:" + rsp.bodyStr();
	}
}
