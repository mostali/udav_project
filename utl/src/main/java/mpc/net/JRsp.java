package mpc.net;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.ERR;
import mpc.rt.ValueOutStream;

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

	private String bodyErr;

	@SneakyThrows
	@Override
	public String err() {
		if (bodyErr != null) {
			return bodyErr;
		}
		ERR.state(isErrorStatus(), "except error http status(4*,5*) '%s'", code());
		IS body = body();
		return bodyErr = (String) ValueOutStream.of(body).getValue();
	}

	@SneakyThrows
	@Override
	public String msg() {
		return con.getResponseMessage();
	}

}
