package mpc.net;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.IT;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Optional;

//https://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests
//https://www.baeldung.com/httpurlconnection-post
public class JRsp<IS extends InputStream> extends AbsNetRsp<IS, String> {

	public JRsp(HttpURLConnection con) {
		this.con = con;
	}

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

	private byte[] bodyBytes;

	@SneakyThrows
	@Override
	public String bodyStr(String... defRq) {
		return bodyBytes != null ? new String(bodyBytes) : new String(bodyBytes = IOUtils.toByteArray(body()));
	}

	@SneakyThrows
	@Override
	public IS body() {
//		try{
//		InputStream errorStream = con.getErrorStream();
//		if (!isErrorStatus()) {
//			return (IS) con.getInputStream();
//		}
////			if(errorStream==null){
//		return (IS) (errorStream != null ? errorStream : con.getInputStream());
//			}
//			return (IS) (isErrorStatus() ? errorStream : con.getInputStream());
//		}catch ()

		return (IS) (isErrorStatus() ? con.getErrorStream() : con.getInputStream());
	}

	@SneakyThrows
	@Override
	public String err() {
		IT.state(isErrorStatus(), "except error http status(4*,5*) '%s'", code());
		return IOUtils.toString(errBytes());
	}

//	private byte[] errBytes;
//
//	@SneakyThrows
//	public byte[] errBytes(byte[]... defRq) {
//		if (errBytes != null) {
//			return errBytes;
//		}
//		IS body = body();
//		try {
//			return errBytes = IOUtils.toByteArray(body);
//		} catch (Exception ex) {
//			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Init jCli error-body bytes is null"), defRq);
//		}
//	}

	private Optional<byte[]> errBytesOpt;

	@SneakyThrows
	public byte[] errBytes(byte[]... defRq) {
		if (errBytesOpt != null) {
			return errBytesOpt.get();
		}
		IS body = body();
		try {
			return (errBytesOpt = body == null ? Optional.empty() : Optional.of(IOUtils.toByteArray(body))).orElse(null);
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
