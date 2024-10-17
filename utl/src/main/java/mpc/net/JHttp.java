package mpc.net;

import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpu.IT;
import mpu.X;
import mpc.exception.WrongLogicRuntimeException;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.STR;
import mpc.str.sym.SYMJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


//https://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests
public class JHttp extends CON {

	public static final Logger L = LoggerFactory.getLogger(JHttp.class);

	@SneakyThrows
	public static void main(String[] args) throws IOException {

//		 DELETE_BODY(urlApi, null, String.class, 200);


		if (true) {
			return;
		}
//		INetRsp rsp = POST_JSON(url, HEADERS(HCT_APPLICATION_JSON, HACCEPT_APPLICATION_JSON), "{\"executed\":\"true\"}", null, 200);
//		INetRsp rsp = POST_FORM(url, HEADERS(HCT_FORM_URLENCODED, HACCEPT_ANY_XML), "executed=true", null, 200);
//		P.exit(ERR.state(rsp != null));
	}


	public static <T> T GET_BODY(String url, String[][] headers, String body, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL(Method.GET, url, headers, null, body, type, successHttpCode);
	}

	public static <T> T DELETE_BODY(String url, String body, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL(Method.DELETE, url, null, null, body, type, successHttpCode);
	}

	public static <T> T GET_BODY(String url, String body, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL(Method.GET, url, null, null, body, type, successHttpCode);
	}

	public static <T> T POST_JSON(String url, String[][] headers, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL(Method.POST, url, headers, null, post_json, type, successHttpCode);
	}

	public static <T> T POST_FORM(String url, String[][] headers, String formdata, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL(Method.POST, url, headers, formdata, null, type, successHttpCode);
	}

//	public static <T, E> T CALL_TC(Enum method, String url, String[] loginPassword, StringEntity entity, Class<T> pojoJsonClass, Gson gson, AbsType<String>... headers) throws IOException {
//		return new WrapperCallNetErrorTc<T, IOException>("UNet::GET_POST_TC", WrapperCallNetErrorTc.DEF_TC) {
//			@Override
//			public T callImpl() throws IOException {
//				return CALL(method, url, loginPassword, entity, pojoJsonClass, gson, headers);
//			}
//		}.call_();
//	}

	private static <T> T CALL(Method method, String url, String[][] headers, String formdata, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {
		String logMark = method + " >> " + url + " H:" + X.sizeOf0(headers) + " D:" + STR.toStrLine(post_json);
		if (L.isDebugEnabled()) {
			L.debug(SYMJ.ARROW_RIGHT2 + logMark);
		}
		Exception ex = null;
		T rsp = null;
		try {
			rsp = CALL_IMPL(method, url, headers, formdata, post_json, type, successHttpCode);
		} catch (Exception e) {
			ex = e;
		}
		if (L.isErrorEnabled()) {
			if (ex != null) {
				L.error(SYMJ.ARROW_LEFT2 + logMark + "\n" + rsp, ex);
			} else {
				if (L.isDebugEnabled()) {
					L.debug(SYMJ.ARROW_LEFT2 + logMark + "\n" + rsp);
				}
			}
		}
		return X.throwErrorNN_OrReturn(ex, rsp);
	}

	private static <T> T CALL_IMPL(Method method, String url, String[][] headers, String formdata, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {

//		if (method == Method.DELETE) {//TODO WTF - RMM - MUST WORK
//			Pare<Integer, String> rsp = CALL_DELETE(url);
//			INetRsp.checkLeggalHttpStatus(successHttpCode, rsp.key());
//			if (String.class.isAssignableFrom(type)) {
//				return (T) rsp.val();
//			} else if (Pare.class.isAssignableFrom(type)) {
//				return (T) rsp;
//			}
//			throw new FIllegalStateException("Method DELETE work only with result type as String or Pare");
//		}

//		System.setProperty("http.keepAlive", "false");

		URL url0 = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) url0.openConnection();

		String errMsg = "set only single data [formdata|json]";
		if (X.nullOnlyOne(formdata, post_json)) {
			if (Method.GET != method) {
				throw new FIllegalStateException(errMsg);
			}
		}

		String data;
		if (X.notEmpty(post_json)) {
			data = post_json;
		} else if (X.notEmpty(formdata)) {
			data = formdata;
		} else {
			switch (method) {
				case GET:
				case DELETE:
					break;
				default:
					throw new WrongLogicRuntimeException(errMsg);
			}
			data = null;
		}

		conn.setRequestMethod(method.name());
		boolean dooutput = true;
		conn.setDoOutput(dooutput);

//		headers = new String[1][1];
//		headers[0] = new String[]{"Cookie", "0"};
//		headers[2] = new String[]{"User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36"};
		//set cookie
		if (X.notEmpty(headers)) {
			for (String[] header : headers) {
				conn.setRequestProperty(IT.notEmpty(header[0]), IT.notEmpty(header[1]));
			}
		}

		if (data != null) {
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = data.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
		}

		JRsp netRsp = JRsp.of(conn);

		INetRsp.checkLeggalHttpStatus(successHttpCode, netRsp);

		return (T) netRsp.toType(type);
	}

	@SneakyThrows
	public static Pare<Integer, String> CALL_DELETE(String url) {
		HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(url)).build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		return Pare.<Integer, String>of(response.statusCode(), response.body());

	}


}
