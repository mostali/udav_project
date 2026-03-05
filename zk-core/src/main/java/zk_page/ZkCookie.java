package zk_page;

import utl_web.UCookie;

import javax.servlet.http.Cookie;

public class ZkCookie {
	public static String getCookieValue(String key, String... defRq) {
		return UCookie.getCookieValue(ZKR.getRequest(), key, defRq);
	}

	public static <T> T getCookieValueAs(String key, Class<T> asType, T... defRq) {
		return UCookie.getCookieValueAs(ZKR.getRequest(), key, asType, defRq);
	}

	public static Cookie getCookie(String key, Cookie... defRq) {
		return UCookie.getCookie(ZKR.getRequest(), key, defRq);
	}

	public static void setCookie(String key, Object value, boolean onlyWithHttps) {
		UCookie.setCookie(ZKR.getResponse(), key, value, onlyWithHttps);
	}

	public static void deleteCookie(String key) {
		UCookie.deleteCookie(ZKR.getResponse(), key);
	}
}
