package udav_net_client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.IT;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.net.AbsNetRsp;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;

////https://www.baeldung.com/httpclient-post-http-request
@RequiredArgsConstructor
public class ARsp extends AbsNetRsp<HttpEntity, String> {

	@Override
	public <T> T toType(Class<T> type, T... defRq) {
		Object any = null;
		try {
			if (type == null) {
				return (T) this;
			} else if (HttpResponse.class.isAssignableFrom(type)) {
				return (T) rsp;
			}
			if (String.class.isAssignableFrom(type)) {
				String responseBody = EntityUtils.toString(rsp.getEntity());
				return (T) responseBody;
			} else if (InputStream.class.isAssignableFrom(type)) {
				return (T) rsp.getEntity().getContent();
			} else if (byte[].class.isAssignableFrom(type)) {
				return (T) EntityUtils.toByteArray(rsp.getEntity());
			}
			T t = IT.isJsonType(rsp.getEntity().getContent(), type, true);
			return t;
		} catch (Exception ex) {
			Object finalAny = any;
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Illegal response '%s' to convert type '%s'", finalAny, type), defRq);
		}
	}

	public static ARsp of(HttpResponse rsp) {
		return new ARsp(rsp);
	}

	public final HttpResponse rsp;

	@Override
	public int code() {
		return rsp.getStatusLine().getStatusCode();
	}

	@Override
	public HttpEntity body() {
		return rsp.getEntity();
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
			return errBytes = IOUtils.toByteArray(rsp.getEntity().getContent());
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Init apCli error-body bytes is null"), defRq);
		}
	}

	@Override
	public String msg() {
		return rsp.getStatusLine().getReasonPhrase();
	}
}
