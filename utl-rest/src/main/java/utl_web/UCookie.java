package utl_web;

import mpu.X;
import mpu.core.ARG;
import mpu.str.UST;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UCookie {
	public static Cookie getCookie(HttpServletRequest request, String name, Cookie... defRq) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(name)) {
					return c;
				}
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("except cookie [%s]", name), defRq);
	}

	public static <T> T getCookieValueAs(HttpServletRequest request, String key, Class<T> asType, T... defRq) {
		String cookie = getCookieValue(request, key, null);
		if (cookie != null) {
			return UST.strTo(cookie, asType, defRq);
		}
		return ARG.toDefThrowMsg(() -> X.f("except cookie [%s] as type [%s]", key, asType), defRq);
	}

	public static String getCookieValue(HttpServletRequest request, String key, String... defRq) {
		Cookie cookie = getCookie(request, key, null);
		if (cookie != null) {
			return cookie.getValue();
		}
		return ARG.toDefThrowMsg(() -> X.f("except cookie [%s]", key), defRq);
	}

	public static Cookie deleteCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, "");
//		cookie.setValue("");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return cookie;
	}

	public static Cookie setCookie(HttpServletResponse response, String name, Object value, boolean onlyWithHttps) {
		Cookie userCookie = new Cookie(name, value == null ? null : value.toString());
		userCookie.setSecure(onlyWithHttps);
		response.addCookie(userCookie);
		return userCookie;
	}
}
