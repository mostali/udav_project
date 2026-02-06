package udav_net_client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.net.AbsNetRsp;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

@RequiredArgsConstructor
public class OkRsp extends AbsNetRsp<ResponseBody, String> {

	@Override
	public <T> T toType(Class<T> type, T... defRq) {
		Object any = null;
		try {
			if (type == null) {
				return (T) this;
			} else if (Response.class.isAssignableFrom(type)) {
				return (T) rsp;
			} else if (String.class.isAssignableFrom(type)) {
				String responseBody = rsp.body().string();
				return (T) responseBody;
			} else if (InputStream.class.isAssignableFrom(type)) {
				InputStream byteStream = rsp.body().byteStream();
				return (T) byteStream;
			} else if (byte[].class.isAssignableFrom(type)) {
				return (T) rsp.body().bytes();
			}
			Class<T> type1 = type;
			T t = IT.isJsonType(rsp.body().byteStream(), type1, true);
			return t;
		} catch (Exception ex) {
			Object finalAny = any;
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Illegal response '%s' to convert type '%s'", finalAny, type), defRq);
		}
	}

	@Override
	public <T> T bodyAs(Class<T> type, T... defRq) {
		if (type == String.class) {
			X.nothing();
		}
		T t = super.bodyAs(type, defRq);
		return t;

	}

	public static OkRsp of(Response rsp) {
		return new OkRsp(rsp);
	}

	public final Response rsp;

	@Override
	public int code() {
		return rsp.code();
	}

	@Override
	public ResponseBody body() {
		return rsp.body();
	}

	private byte[] bodyBytes;

	@SneakyThrows
	@Override
	public String bodyStr(String... defRq) {
		return bodyBytes != null ? new String(bodyBytes) : new String(bodyBytes = body().string().getBytes());
	}

	@SneakyThrows
	@Override
	public String err() {
		IT.state(isErrorStatus(), "except error http status(4*,5*) '%s'", code());
		return IOUtils.toString(errBytes());
	}

	private byte[] errBytes;

	@SneakyThrows
	public byte[] errBytes(byte[]... defRq) {
		if (errBytes != null) {
			return errBytes;
		}
		try {
			return errBytes = body().bytes();
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Init okCli error-body bytes is null"), defRq);
		}
	}

	@Override
	public String msg() {
		return rsp.message();
	}
}
