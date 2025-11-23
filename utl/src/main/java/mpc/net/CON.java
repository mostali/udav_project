package mpc.net;

import lombok.SneakyThrows;
import mpu.IT;
import mpu.core.ENUM;
import mpu.str.JOIN;
import mpu.str.TKN;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Stream;

//Connection
public class CON {

	public static final Logger L = LoggerFactory.getLogger(CON.class);

	public static final String AUTHORIZATION = "Authorization";
	public static final String X_CSRFTOKEN = "X-CSRFToken";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String COOKIE = "Cookie";
	public static final String[] HCT_APPLICATION_JSON = {"Content-Type", APPLICATION_JSON};
//	public static final String[] HDR_CT_APP_JSON = {"Content-Type", "application/json"};
	public static final String[] HCT_FORM_URLENCODED = {"Content-Type", "application/x-www-form-urlencoded"};
	public static final String[] HACCEPT_ANY_XML = {"Accept", "text/html,application/xhtml+xml,application/xml"};
	public static final String[] HACCEPT_APPLICATION_JSON = {"Accept", APPLICATION_JSON};


	public static final String HTTP = "http://";
	public static final String HTTPS = "https://";

	public static String HTTPS(boolean enable) {
		return enable ? HTTPS : HTTP;
	}

	public enum Method {
		GET, POST, PUT, DELETE, UNDEFINED;

		public static Method valueOf(String name, Method... defRq) {
			return ENUM.valueOf(name, Method.class, true, defRq);
		}
	}

	//System.setProperty("com.sun.net.ssl.checkRevocation", "false");
	//https://docs.oracle.com/javase/8/docs/technotes/guides/security/certpath/CertPathProgGuide.html#AIA
	//https://stackoverflow.com/questions/9619030/resolving-javax-net-ssl-sslhandshakeexception-sun-security-validator-validatore
	//https://stackoverflow.com/questions/4663147/is-there-a-java-setting-for-disabling-certificate-validation
	public static Boolean onOffSsl = true;

	@SneakyThrows
	public static void onOffSSl(boolean onOff) {
		if (onOffSsl.equals(onOff)) {
			return;
		}
		onOffSsl = onOff;
		if (onOff) {
			HttpsURLConnection.setDefaultSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
			return;
		}
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509ExtendedTrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					}

					@Override
					public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
					}

					@Override
					public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
					}

					@Override
					public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
					}

					@Override
					public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
					}
				}
		};

		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	}

	public static String[][] HEADERS_ARGS_BY_SEMICOLON(String... headers) {
		return Stream.of(headers).map(h -> TKN.two(h, ":")).toArray(String[][]::new);

	}

	public static String[][] HEADERS(String[]... headers) {
		return Stream.of(headers).filter(h -> h != null).toArray(String[][]::new);
	}

	@NotNull
	public static String[] HEADER_BEARER(String token) {
		return new String[]{AUTHORIZATION, token.startsWith("Bearer ") ? token : "Bearer " + IT.notEmpty(token)};
	}

	public static String[] HEADER_AUTH_BASIC(String[] lp) {
		return new String[]{AUTHORIZATION, "Basic " + toBasicAuthString(lp)};
	}

	@SneakyThrows
	public static String toBasicAuthString(String[] loginPassword) {
		return Base64.getEncoder().encodeToString((loginPassword[0] + ":" + loginPassword[1]).getBytes("Utf8"));
	}


	@NotNull
	public static String[] HEADER_COOKIE(Collection<String> cookies) {
		return new String[]{COOKIE, JOIN.allBy(";", IT.NE(cookies))};
	}

	@NotNull
	public static String[] HEADER_CSRF(String token) {
		return new String[]{X_CSRFTOKEN, IT.notEmpty(token)};
	}

}