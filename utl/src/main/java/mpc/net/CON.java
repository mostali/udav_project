package mpc.net;

import lombok.SneakyThrows;
import mpc.ERR;
import mpc.str.JOIN;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Stream;

//Connection
public class CON {

	public static final Logger L = LoggerFactory.getLogger(CON.class);

	public enum Method {
		GET, POST, PUT, DELETE;
	}

	public static final String AUTHORIZATION = "Authorization";
	public static final String X_CSRFTOKEN = "X-CSRFToken";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String COOKIE = "Cookie";
	public static final String[] HCT_APPLICATION_JSON = {"Content-Type", APPLICATION_JSON};
	public static final String[] HCT_FORM_URLENCODED = {"Content-Type", "application/x-www-form-urlencoded"};
	public static final String[] HACCEPT_ANY_XML = {"Accept", "text/html,application/xhtml+xml,application/xml"};
	public static final String[] HACCEPT_APPLICATION_JSON = {"Accept", APPLICATION_JSON};

	public static String[][] HEADERS(String[]... headers) {
		return Stream.of(headers).filter(h -> h != null).toArray(String[][]::new);
	}

	@NotNull
	public static String[] HEADER_BEARER(String token) {
		return new String[]{AUTHORIZATION, token.startsWith("Bearer ") ? token : "Bearer " + ERR.notEmpty(token)};
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
		return new String[]{COOKIE, JOIN.SC(ERR.NE(cookies))};
	}

	@NotNull
	public static String[] HEADER_CSRF(String token) {
		return new String[]{X_CSRFTOKEN, ERR.notEmpty(token)};
	}

}