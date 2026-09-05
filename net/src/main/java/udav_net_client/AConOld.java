package udav_net_client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mpc.exception.FIllegalStateException;
import mpc.net.CON;
import mpc.types.abstype.AbsType;
import mpu.str.STRA;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import udav_net.wrappercall.WrapperCallNetErrorTc;

import java.io.IOException;
import java.nio.charset.Charset;


public class AConOld extends CON {

	public static <T, E> T CALL_TC(Enum method, String url, String[] loginPassword, StringEntity entity, Class<T> pojoJsonClass, Gson gson, AbsType<String>... headers) throws IOException {
		return new WrapperCallNetErrorTc<T, IOException>("UNet::GET_POST_TC", WrapperCallNetErrorTc.DEF_TC) {
			@Override
			public T callImpl() throws IOException {
				return CALL(method, url, loginPassword, entity, pojoJsonClass, gson, headers);
			}
		}.call_();
	}

	public static <T> T CALL(Enum method, String url, String[] loginPassword, StringEntity entity, Class<T> pojoJsonClass, Gson gson, AbsType<String>... headers) throws IOException {
		String response = AConOld.CALL(method, url, loginPassword, entity, headers);
		if (L.isDebugEnabled()) {
			L.debug("Call to [{}]. Response [{}]", url, response == null ? "NULL" : response.length());
		}
//		response= UGson.getValueArray(response,"data").toString();
		gson = gson == null ? new GsonBuilder().create() : gson;
		final T pojo = gson.fromJson(response, pojoJsonClass);
		return pojo;
	}

	public static String CALL(Enum method, String url, String[] loginPassword, StringEntity post_entity, AbsType<String>... headers) throws IOException {
		HttpRequestBase httpRequest = null;
		switch (Method.valueOf(method.name())) {
			case GET:
				httpRequest = new HttpGet(url);
				break;
			case POST:
				HttpPost httpPost = new HttpPost(url);
				httpRequest = httpPost;
				if (post_entity != null) {

					httpPost.setEntity(post_entity);
				}
				break;
			default:
				throw new IllegalStateException("need impl:" + method);
		}

		if (loginPassword != null) {
			String encoding = AHttp.toBasicAuthString(loginPassword);
			httpRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
		}

		for (AbsType<String> header : headers) {
			if (header != null) {
				httpRequest.setHeader(header.name(), header.val());
			}
		}

		RequestConfig.Builder requestConfig = RequestConfig.custom();
		requestConfig.setConnectTimeout(30 * 1000);
		requestConfig.setConnectionRequestTimeout(30 * 1000);
		requestConfig.setSocketTimeout(30 * 1000);

		httpRequest.setConfig(requestConfig.build());

		CloseableHttpClient httpclient0 = HttpClientBuilder.create().build();

		HttpResponse response = httpclient0.execute(httpRequest);
		HttpEntity entity = response.getEntity();

		StatusLine status = response.getStatusLine();
		if (STRA.firstCharAsNum(status.getStatusCode()) > 2) {
			throw new FIllegalStateException("Response code [%s]/[%s]", status.getStatusCode(), status.getReasonPhrase());
		}
		return IOUtils.toString(entity.getContent(), Charset.defaultCharset());
	}

	public static String CALL(Enum method, String url, String[] loginPassword, AbsType<String>... headers) throws IOException {
		return CALL(method, url, loginPassword, null, headers);
	}

	public enum HttpHeader {
		ACCEPT, CONTENT_TYPE;

		public static AbsType HEADER_ACCEPT_APPLICATION_JSON = HttpHeader.ACCEPT.toHeader(APPLICATION_JSON);
		public static AbsType HEADER_ACCEPT_APPLICATION_XML = HttpHeader.ACCEPT.toHeader(APPLICATION_XML);

		public static AbsType HEADER_CONTENTTYPE_APPLICATION_JSON = HttpHeader.CONTENT_TYPE.toHeader(APPLICATION_JSON);
		public static AbsType HEADER_CONTENTTYPE_APPLICATION_XML = HttpHeader.CONTENT_TYPE.toHeader(APPLICATION_XML);

		public AbsType<String> toHeader(String val) {
			return new AbsType(name(), val);
		}
	}
}
