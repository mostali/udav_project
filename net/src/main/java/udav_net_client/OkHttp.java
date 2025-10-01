package udav_net_client;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpc.exception.FIllegalStateException;
import mpc.exception.NI;
import mpc.net.INetRsp;
import mpc.net.CON;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.str.Hu;
import mpu.str.STR;
import mpu.str.TKN;
import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OkHttp extends CON {

	public static void main(String[] args) throws Exception {
		//		String get = GET_JSON_BEARER(currency, token);
		//		String post = POST_JSON_BEARER(currency_search + "?page=0", token, "{\"or\": []}", String.class);
		//		Object rslt2 = UOk.GET_JSON("http://URL/api/v1/", new String[][]{new String[]{AUTHORIZATION, "Bearer " + UGson.getValueSimple(rslt, "access_token")}}, null);
	}


	public static Logger L = LoggerFactory.getLogger(OkHttp.class);

	//	static {
	//		L = mpc.log.L.L;
	//	}

	@NotNull
	public static RequestBody buildPayloadJson(String post_json) {
		return RequestBody.create(MediaType.parse(HCT_APPLICATION_JSON[1]), post_json);
	}

	@NotNull
	public static RequestBody buildPayloadForm(String post_form) {
		return RequestBody.create(MediaType.parse(HCT_FORM_URLENCODED[1]), post_form);
	}

	@NotNull
	public static Request.Builder buildRq(String url) {
		return new Request.Builder().url(url);
	}

	@NotNull
	public static OkHttpClient buildClient(int timeout_sec, int write_timeout, int read_timeout) {
		//old version
		//		return new OkHttpClient.Builder()
		//				.connectTimeout(10, TimeUnit.SECONDS)
		//				.writeTimeout(10, TimeUnit.SECONDS)
		//				.readTimeout(30, TimeUnit.SECONDS)
		//				;
		OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(DEAFULT_LOG_INTERCEPTOR);
		if (timeout_sec > 0) {
			builder.connectTimeout(timeout_sec, TimeUnit.SECONDS);
		}
		if (write_timeout > 0) {
			builder.writeTimeout(write_timeout, TimeUnit.SECONDS);
		}
		if (read_timeout > 0) {
			builder.readTimeout(read_timeout, TimeUnit.SECONDS);
		}
		return builder.build();
	}

	public static List<String> getCookies(Response rsp) {
		return rsp.headers().values("Set-Cookie");
	}

	public static final Interceptor DEAFULT_LOG_INTERCEPTOR = new Interceptor() {
		//		public static final int LOG_MODE = 0;
		//https://gist.github.com/erickok/e371a9e0b9e702ed441d
		@Override
		public okhttp3.Response intercept(Chain chain) throws IOException {

			Request request = chain.request();

			String mark = request.method() + ":" + STR.randAlpha(3);
			long start_ms = System.currentTimeMillis();

			if (L.isDebugEnabled()) {

				String head = SYMJ.ARROW_RIGHT2 + SYMJ.ARROW_RIGHT2 + mark;
				String url = " >> " + request.url() + "";
				String body = " | " + body2str(request.body(), "NOBODY");
				String headers = " | " + headers2str(request.headers());
				L.debug(head + url + body + headers);
				//				Sb sb = new Sb();
				//				sb.TABNL(0, SYMJ.ARROW_RIGHT2 + SYMJ.ARROW_RIGHT2 + mark + " >> " + request.url());
				//				sb.TABNL(1, "payload");
				//				if (request.body() != null) {
				//					String str = body2str(request.body());
				//					sb.TABNL(2, str);
				//				} else {
				//					sb.TABNL(2, "NULL");
				//				}
				//				sb.TABNL(1, "|");
				//				sb.TABNL(2, request.headers() + "");
				//				switch (LOG_MODE) {
				//					case 0:
				//						L.debug(sb.toStringLine(";;"));
				//						break;
				//					default:
				//						L.debug(sb.toString());
				//						break;
				//				}
			}

			okhttp3.Response response = chain.proceed(request);

			if (L.isDebugEnabled()) {
				String head = SYMJ.ARROW_LEFT2 + mark;
				String headers = " | " + headers2str(response.headers());
				String body = "C" + response.code() + "; " + response.message() + headers;
				String ms = Hu.MS(System.currentTimeMillis() - start_ms);
				L.debug(head + " << " + ms + " << " + body);
			}

			return response;
		}
	};

	//	public static final Interceptor BEARER_INTERCEPTOR = new Interceptor() {
	//		@Override
	//		public okhttp3.Response intercept(Chain chain) throws IOException {
	//			Request originalRequest = chain.request();
	//
	//			Request.Builder builder = originalRequest.newBuilder().header("Authorization", "Baerer " + SsClient.getAccesToken());
	////				Credentials.basic("aUsername", "aPassword")
	//
	//			Request newRequest = builder.build();
	//			return chain.proceed(newRequest);
	//		}
	//	};

	private static String headers2str(Headers headers, String... returnIfNull) {
		if (headers == null) {
			return ARG.toDefOr(null, returnIfNull);
		}
		return STR.noNL(String.valueOf(headers), ", ");
	}

	@SneakyThrows
	public static String body2str(RequestBody body, String... returnIfNull) {
		if (body == null) {
			return ARG.toDefOr(null, returnIfNull);
		}
		final Buffer buffer = new Buffer();
		body.writeTo(buffer);
		return buffer.readUtf8();
	}


	public static <T> T POST_JSON_WITH_COOKIE(String url, String coockie_value, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL_COOKIE(CON.Method.POST, url, coockie_value, post_json, type, successHttpCode);
	}


	public static String GET(String url, Integer... status) throws IOException {
		return CALL_LOG(CON.Method.GET, url, (String[][]) null, null, null, String.class, status);
	}

	public static String GET(String url, String[][] headers, Integer... status) throws IOException {
		return CALL_LOG(CON.Method.GET, url, headers, null, null, String.class, status);
	}

	public static <T> T GET(String url, String[][] headers, Class<T> asType, Integer... status) throws IOException {
		return CALL_LOG(CON.Method.GET, url, headers, null, null, asType, status);
	}

	public static <T> T GET_JSON_BEARER(String url, String token, Class<T> asType) throws IOException {
		token = token.startsWith("Bearer ") ? token : "Bearer " + IT.notEmpty(token);
		String[][] headers = {HEADER_BEARER(token)};
		return CALL_LOG(CON.Method.GET, url, headers, null, null, asType);
	}

	public static <T> T POST_JSON_BEARER(String url, String token, String body_json, Class<T> type, Integer... success_codes) throws IOException {
		return CALL_LOG(CON.Method.POST, url, HEADERS(HEADER_BEARER(token)), body_json, null, type, success_codes);
	}

	public static <T> T PUT_JSON_BEARER(String url, String token, String body_json, Class<T> type, Integer... success_codes) throws IOException {
		return CALL_LOG(Method.PUT, url, HEADERS(HEADER_BEARER(token)), body_json, null, type, success_codes);
	}

	public static <T> T GET_JSON(String url, String coockie_value, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL_COOKIE(CON.Method.GET, url, coockie_value, null, type, successHttpCode);
	}

	public static <T> T GET_JSON(String url, String[][] headers, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL_LOG(CON.Method.GET, url, headers, null, null, type, successHttpCode);
	}

	public static String GET_JSON_BEARER(String url, String token) throws IOException {
		return CALL_JSON_BEARER(Method.GET, url, token);
	}

	public static String CALL_JSON_BEARER(Method method, String url, String token) throws IOException {
		return CALL_LOG(method, url, HEADERS(HEADER_BEARER(token)), null, null, String.class);
	}

	/**
	 * NEED TEST
	 * https://stackoverflow.com/questions/10859766/adding-a-cookie-to-a-http-request/10863512
	 */
	public static <T> T CALL_COOKIE(CON.Method getOrPost, String url, String coockie_value, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {
		String[][] headers = coockie_value == null ? null : new String[][]{new String[]{"Cookie", coockie_value}};
		return CALL_LOG(getOrPost, url, headers, post_json, null, type, successHttpCode);
	}


	public static <T> T GET_JSON(String url, String[][] headers, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL_LOG(CON.Method.GET, url, headers, post_json, null, type, successHttpCode);
	}

	public static <T> T POST_JSON(String url, String[][] headers, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL_LOG(CON.Method.POST, url, headers, post_json, null, type, successHttpCode);
	}

	public static <T> T POST_FORM(String url, String[][] headers, String post_form, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL_LOG(CON.Method.POST, url, headers, null, post_form, type, successHttpCode);
	}

	private static <T> T CALL_LOG(CON.Method post, String url, String[][] headers, String post_json, String post_form, Class<T> type, Integer... successHttpCode) throws IOException {
		T rsp = CALL_IMPL(post, url, headers, post_json, post_form, type, successHttpCode);
		return rsp;
	}

	private static <T> T CALL_IMPL(CON.Method method, String url, String[][] headers, String post_json, String post_form, Class<T> type, Integer... successHttpCode) throws IOException {

		if (X.notNullAll(post_json, post_form)) {
			throw new FIllegalStateException("Set single type payload JSON[%s] FORM[%s]", post_json, post_form);
		}

		OkHttpClient client = buildClient(10, 10, 30);

		Request.Builder reqBuilder = buildRq(url);

		switch (method) {
			case DELETE:
				reqBuilder = reqBuilder.delete();
				break;
			case GET:
				reqBuilder = reqBuilder.get();
				break;
			case PUT:
			case POST:
				if (post_json != null) {
					reqBuilder.method(method.name(), buildPayloadJson(post_json));
				} else if (post_form != null) {
					reqBuilder.method(method.name(), buildPayloadForm(post_form));
				} else {
					throw new FIllegalStateException("Request '%s' need body", method);
				}
				break;
			default:
				throw NI.stop(method);
		}

		reqBuilder.addHeader(HCT_APPLICATION_JSON[0], HCT_APPLICATION_JSON[1]);

		if (headers != null) {
			for (String[] header : headers) {
				if (!HCT_APPLICATION_JSON[0].equals(header[0])) {
					if (header.length == 1) {
						header = TKN.two(header[0], ":");
						header[0] = header[0].trim();
						header[1] = header[1].trim();
					}
					reqBuilder.addHeader(header[0], header[1]);
				}
			}
		}
		Call call = client.newCall(reqBuilder.build());
		Response response = call.execute();

		OkRsp netRsp = OkRsp.of(response);

		INetRsp.checkLeggalHttpStatus(successHttpCode, netRsp);

//		checkResponseCode(response, successHttpCode);

		if (type == null || Response.class.isAssignableFrom(type)) {
			return (T) response;
		} else if (type.isAssignableFrom(netRsp.getClass())) {
			return (T) netRsp;
		}

		IT.state(response.isSuccessful(), response);

//		if (String.class.isAssignableFrom(type)) {
//			String responseBody = response.body().string();
//			return (T) responseBody;
//		} else if (InputStream.class.isAssignableFrom(type)) {
//			InputStream byteStream = response.body().byteStream();
//			return (T) byteStream;
//		} else if (byte[].class.isAssignableFrom(type)) {//сработает?
//			return (T) response.body().bytes();
//		}
//		T t = IT.isTypeGson0(response.body().string(), type, true);
//		return t;

		INetRsp.checkLeggalHttpStatus(successHttpCode, netRsp);

		T type1 = netRsp.toType(type, null);

		return type1;


	}


}
