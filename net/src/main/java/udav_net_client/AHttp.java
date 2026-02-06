package udav_net_client;

import mpc.exception.FIllegalStateException;
import mpu.IT;
import mpu.X;
import mpc.exception.WrongLogicRuntimeException;
import mpc.net.CON;
import mpc.net.INetRsp;
import mpu.str.STR;
import mpc.str.sym.SYMJ;
import mpe.rt_exec.old.ExecOld;
import mpz_deprecated.EER;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;


public class AHttp extends CON {


	public static void main(String[] args) {
		String url = "http://s.zznote.ru/_api/p/r?ska=go";

		// Create HttpClient
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			// Create HttpDelete request
			HttpDelete request = new HttpDelete(url);

			// Execute the request
			try (CloseableHttpResponse response = httpClient.execute(request)) {

				// Get HttpResponse Status
//				System.out.println("Response Code: " + response.getCode());

				// Get HttpResponse Content
				String content = EntityUtils.toString(response.getEntity());
				System.out.println("Response Content: \n" + content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//
//	public static void main(String[] args) throws Exception {
//		//		String get = GET_JSON_BEARER(currency, token);
//		//		String post = POST_JSON_BEARER(currency_search + "?page=0", token, "{\"or\": []}", String.class);
//		//		Object rslt2 = UOk.GET_JSON("http://URL/api/v1/", new String[][]{new String[]{AUTHORIZATION, "Bearer " + UGson.getValueSimple(rslt, "access_token")}}, null);
//	}

	public static final Logger L = LoggerFactory.getLogger(AHttp.class);

	public static String getFinalURL(String url) throws IOException {
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setInstanceFollowRedirects(false);
		con.connect();
		con.getInputStream();
		if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
			String redirectUrl = con.getHeaderField("Location");
			return getFinalURL(redirectUrl);
		}
		return url;
	}

	private static final String UA_NATIVE = "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/63.0.3239.84 Chrome/63.0.3239.84 Safari/537.36";

	public static String getHtmlFromUrlNew(String url) throws IOException {
		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13";
		userAgent = UA_NATIVE;
		Document doc = Jsoup.connect(url).userAgent(userAgent).timeout(30000).get();
		return doc.html();
	}

	@Deprecated
	public static String getHtmlFromUrl(String url) throws IOException {
		URL url_ = null;
		StringBuffer buffer = null;
		url_ = new URL(url);
		InputStream is = url_.openStream();
		int ptr = 0;
		buffer = new StringBuffer();
		while ((ptr = is.read()) != -1) {
			buffer.append((char) ptr);
		}
		return buffer.toString();
	}

	public static boolean isHttpCode(Exception ex, int code) {
		if (!(ex instanceof IOException)) {
			return false;
		}
		return ex.getMessage().startsWith("Server returned HTTP response code: " + code);
	}

	public static void checkConnection(String url, int msec) {
		if (!AHttp.hasConnection(url, msec)) {
			throw EER.IS("Check connection to ::: " + url);
		}
	}

	public static boolean hasConnection(String url, int msec) {
		try {
			URLConnection conn = new URL(url).openConnection();
			conn.setConnectTimeout(msec);
			conn.setReadTimeout(msec);
			conn.connect();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	static class CmdCurlSimple {
		static final String[] cmd = new String[]{"curl", "-m", null, "--socks5", null, null};

		public static String[] getCmd(int timeoutSec, String ip_port, String testUrl) {
			cmd[2] = String.valueOf(timeoutSec);
			cmd[4] = ip_port;
			cmd[5] = testUrl;
			return cmd;
		}

	}

	public static boolean hasConnectionProxySock5(String ip_port, String testUrl, int timeoutMaxTimeSec) {
		try {
			List<String>[] response = ExecOld.execAlternative(CmdCurlSimple.getCmd(timeoutMaxTimeSec, ip_port, testUrl));
			return response[0].size() != 0;
		} catch (IOException | InterruptedException e) {
			L.error("hasConnectionProxySock5::IOException::" + e.getMessage());
			return false;
		}
	}

	public static StringEntity createXmlPostEntity(String xml, Charset... charset) {
		StringEntity entity_ = new StringEntity(xml, charset.length == 0 ? Charset.defaultCharset() : charset[0]);
		entity_.setContentType(CON.APPLICATION_XML);
		return entity_;
	}

	public static <T> T POST_WITH_COOKIE(String url, String coockie_value, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL_COOKIE(false, url, coockie_value, post_json, type, successHttpCode);
	}

	public static <T> T POST_FORM(String url, String[][] headers, String formdata, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL(false, url, headers, formdata, null, type, successHttpCode);
	}

	public static <T> T POST(String url, String[][] headers, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL(false, url, headers, null, post_json, type, successHttpCode);
	}

	public static <T> T GET(String url) throws IOException {
		return CALL(true, url, (String[][]) null, null, null, null, null);
	}

	public static <T> T GET(String url, String coockie_value, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL_COOKIE(true, url, coockie_value, null, type, successHttpCode);
	}

	public static <T> T GET(String url, String[][] headers, Class<T> type, Integer... successHttpCode) throws IOException {
		return CALL(true, url, headers, null, null, type, successHttpCode);
	}

	/**
	 * NEED TEST
	 * https://stackoverflow.com/questions/10859766/adding-a-cookie-to-a-http-request/10863512
	 */
	public static <T> T CALL_COOKIE(boolean getOrPost, String url, String coockie_value, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {
		String[][] headers = coockie_value == null ? null : new String[][]{new String[]{"Cookie", coockie_value}};
		return CALL(getOrPost, url, headers, null, post_json, type, successHttpCode);
	}

	public static <T> T CALL(boolean getOrPost, String url, String[][] headers, String formdata, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {
		String logMark = (getOrPost ? "GET" : "POST") + " >> " + url + " H:" + X.sizeOf0(headers) + " D:" + STR.toStrLine(post_json);
		if (L.isDebugEnabled()) {
			L.debug(SYMJ.ARROW_RIGHT2 + logMark);
		}
		Exception ex = null;
		T rsp = null;
		try {
			rsp = CALL_IMPL(getOrPost, url, headers, formdata, post_json, type, successHttpCode);
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
		return ex == null ? rsp : X.throwException(ex);
	}

	private static <T> T CALL_IMPL(boolean getOrPost, String url, String[][] headers, String formdata, String post_json, Class<T> type, Integer... successHttpCode) throws IOException {

		String errMsg = "set only single data [formdata|json]";
		if (X.nullOnlyOne(formdata, post_json)) {
//				switch (method) {
//					case GET:
//					case POST:
//					case PUT:
//						break;
//					default:
//						throw new FIllegalStateException(errMsg);
//				}
			if (!getOrPost) {
				throw new FIllegalStateException(errMsg);
			}
		}

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpRequestBase httpPost = getOrPost ? new HttpGet(url) : new HttpPost(url);


		StringEntity requestEntity;
		if (X.notEmpty(post_json)) {
			requestEntity = new StringEntity(post_json, ContentType.APPLICATION_JSON);
		} else if (X.notEmpty(formdata)) {
			requestEntity = new StringEntity(formdata, ContentType.APPLICATION_FORM_URLENCODED);
		} else if (!getOrPost) {
			throw new WrongLogicRuntimeException(errMsg);
		} else {
			requestEntity = null;
		}

		((HttpPost) httpPost).setEntity(requestEntity);

		//set cookie
		if (X.notEmpty(headers)) {
			for (String[] header : headers) {
				httpPost.setHeader(IT.notEmpty(header[0]), IT.notEmpty(header[1]));
			}
		}

		CloseableHttpResponse response = httpClient.execute(httpPost);

		ARsp netRsp = ARsp.of(response);

		INetRsp.checkLeggalHttpStatus(successHttpCode, netRsp);

		return netRsp.toType(type);

	}


}
