package mpv.string.patterns;


import mpu.str.STR;
import mpu.str.UST;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class PatternsUtils {

	public static boolean containsWebLink(String text) {
		return getWebLink(text) != null;
	}

	public static String getWebLink(String text) {
		return STR.getPatternString(text, Patterns.WEB_URL);
	}

	public static boolean containsIpAddress(String text) {
		return getIpAddress(text) != null;
	}

	public static String getIpAddress(String text) {
		return STR.getPatternString(text, Patterns.IP_ADDRESS);
	}

	private static final Pattern IP_ADDRESS_WITH_PORT = Pattern.compile("(\\d{1,3}(?:\\.\\d{1,3}){3}(?::\\d{1,5})?)");

	public static String getIpAddressWithPort(String text) {
		String ipMayHasErrors = STR.getPatternString(text, IP_ADDRESS_WITH_PORT);
		if (ipMayHasErrors == null) {
			return null;
		}
		String cleanIp = STR.getPatternString(text, Patterns.IP_ADDRESS);
		if (cleanIp == null) {
			return null;
		}
		if (ipMayHasErrors.indexOf(':') == -1) {
			return null;
		}
		String port = ipMayHasErrors.split(":", 2)[1];
		Integer portInt = UST.INT(port, null);
		if (portInt != null && portInt > 0 && portInt <= 65535) {
			return ipMayHasErrors;
		}
		return null;
	}

}
