package mpc.net;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.IT;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;

//https://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests
//https://www.baeldung.com/httpurlconnection-post
@RequiredArgsConstructor
public class JRsp<IS extends InputStream> extends AbsNetRsp<IS, String> {

	public static JRsp of(HttpURLConnection rsp) {
		return new JRsp(rsp);
	}

	public final HttpURLConnection con;

	int code = -2;

	@SneakyThrows
	@Override
	public int code() {
		if (code > -2) {
			return code;
		}
		return code = con.getResponseCode();
	}

	@SneakyThrows
	@Override
	public IS body() {
		return (IS) (isErrorStatus() ? con.getErrorStream() : con.getInputStream());
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
		IS body = body();
		try {
			return errBytes = IOUtils.toByteArray(body);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Init jCli error-body bytes is null"), defRq);
		}
	}

	@SneakyThrows
	@Override
	public String msg() {
		return con.getResponseMessage();
	}

}
