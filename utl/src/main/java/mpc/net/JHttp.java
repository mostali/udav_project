package mpc.net;

import mpc.ERR;
import mpc.X;
import mpc.exception.WrongLogicRuntimeException;
import mpc.str.STR;
import mpc.str.sym.SYMJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


//https://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests
public class JHttp extends CON {

	public static final Logger L = LoggerFactory.getLogger(JHttp.class);

//	@SneakyThrows
//	public static void main(String[] args) {
//		INetRsp rsp = POST_JSON(url, HEADERS(HCT_APPLICATION_JSON, HACCEPT_APPLICATION_JSON), "{\"executed\":\"true\"}", null, 200);
//		INetRsp rsp = POST_FORM(url, HEADERS(HCT_FORM_URLENCODED, HACCEPT_ANY_XML), "executed=true", null, 200);
//		P.exit(ERR.state(rsp != null));
//	}

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

//		System.setProperty("http.keepAlive", "false");

		URL url0 = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) url0.openConnection();

		String errMsg = "set only single data [formdata|json]";
		ERR.state(X.notNullOnlyOne(formdata, post_json), errMsg);
		String data;
		if (X.notEmpty(post_json)) {
			data = post_json;
		} else if (X.notEmpty(formdata)) {
			data = formdata;
		} else {
			throw new WrongLogicRuntimeException(errMsg);
		}

		conn.setRequestMethod(method.name());
		conn.setDoOutput(true);

		//set cookie
		if (X.notEmpty(headers)) {
			for (String[] header : headers) {
				conn.setRequestProperty(ERR.notEmpty(header[0]), ERR.notEmpty(header[1]));
			}
		}

		try (OutputStream os = conn.getOutputStream()) {
			byte[] input = data.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		JRsp netRsp = JRsp.of(conn);

		INetRsp.checkLeggalHttpStatus(successHttpCode, netRsp);

		return (T) netRsp.toType(type);
	}

}
