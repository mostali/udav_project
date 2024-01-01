package udav_net_client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.ERR;
import mpc.args.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.net.AbsNetRsp;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.io.InputStreamReader;

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
			}
			if (String.class.isAssignableFrom(type)) {
				String responseBody = rsp.body().string();
				return (T) responseBody;
			} else if (InputStream.class.isAssignableFrom(type)) {
				InputStream byteStream = rsp.body().byteStream();
				return (T) byteStream;
			} else if (byte[].class.isAssignableFrom(type)) {
				return (T) rsp.body().bytes();
			}
			T t = ERR.isTypeGson0(rsp.body().byteStream(), type, true);
			return t;
		} catch (Exception ex) {
			Object finalAny = any;
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Illegal response '%s' to convert type '%s'", finalAny, type), defRq);
		}
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

	private String bodyErr;

	@SneakyThrows
	@Override
	public String err() {
		if (bodyErr != null) {
			return bodyErr;
		}
		ERR.state(isErrorStatus(), "except error http status(4*,5*) '%s'", code());
		return bodyErr = rsp.body().string();
	}

	@Override
	public String msg() {
		return rsp.message();
	}
}
