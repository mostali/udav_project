package mpu.str;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class RANDOM {

	public static boolean randomTrueFalse() {
		return System.currentTimeMillis() % 2 == 0;
	}

	public static int range(int min, int max) {
		final Random RAND = new Random();
		if (min == max) {
			return RAND.nextInt(min);
		} else if (min > max) {
			return RAND.nextInt(min - max) + max;
		}

		return RAND.nextInt(max - min) + min;
	}

	public static String alpha_num(int length) {
		return varios(length, RandomStringMode.ALPHANUMERIC);
	}

	public static String uuid(int len) {
		return UUID.randomUUID().toString().substring(0, len);
	}

	public static <T> T array_item(T[] color) {
		return color[range(0, color.length - 1)];
	}

	public static boolean bool() {
		return System.currentTimeMillis() % 2 == 0;
	}

	public enum RandomStringMode {
		ALPHA, ALPHANUMERIC, NUMERIC
	}

	private RANDOM() {
	}

	public static Integer integer(int length) {
		return Integer.valueOf(num(length));
	}

	public static String num(int length) {
		return varios(length, RandomStringMode.NUMERIC);
	}

	public static int num(int length, int max) {
		int pat = integer(length);
		return (int) (pat >= max ? 0.7 * pat : pat);
	}

	/**
	 * SIMPLE (ARG_INTEGER)<br>
	 * VARIOS (ARG_INTEGER , RandomStringVarious.MODE)<br>
	 * otherwise return session_id length=32
	 *
	 * @param length
	 * @param mode
	 * @return
	 */
	public static String varios(int length, RandomStringMode mode) {

		StringBuffer buffer = new StringBuffer();
		String characters = "";

		switch (mode) {

			case ALPHA:
				characters = STR.ALPHABETIC_FULL;
				break;

			case ALPHANUMERIC:
				characters = STR.ALPHABETIC_FULL__NUM;
				break;

			case NUMERIC:
				characters = STR.ALPHABETIC_NUM;
				break;
		}

		int charactersLength = characters.length();

		for (int i = 0; i < length; i++) {
			double index = Math.random() * charactersLength;
			buffer.append(characters.charAt((int) index));
		}
		return buffer.toString();
	}

	// =================================================================SessionID
	private static SecureRandom random_sec = new SecureRandom();

	public static String session() {
		return new BigInteger(130, random_sec).toString(32);
	}

	// ======================================================SIMPLE-MODE
	private static final char[] symbols = new char[36];

	static {
		for (int idx = 0; idx < 10; ++idx) {
			symbols[idx] = (char) ('0' + idx);
		}
		for (int idx = 10; idx < 36; ++idx) {
			symbols[idx] = (char) ('a' + idx - 10);
		}
	}

	private final static Random random = new Random();

	public static String alpha(CharSequence prefix, int length) {
		if (prefix == null) {
			prefix = "";
		}
		final char[] buf = new char[prefix.length() + length];

		for (int idx = 0; idx < prefix.length(); ++idx) {
			buf[idx] = prefix.charAt(idx);
		}

		for (int idx = prefix.length(); idx < buf.length; ++idx) {
			buf[idx] = symbols[random.nextInt(symbols.length)];
		}

		return new String(buf);
	}

	public static String alpha(int length) {
		return alpha("", length);
	}

	public static String logmark(int length) {
		return "/" + alpha(length) + "/";
	}

}
