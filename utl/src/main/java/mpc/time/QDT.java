package mpc.time;

import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.core.ARG;

import java.util.concurrent.TimeUnit;

public enum QDT {
	YEAR, MONTH, DAY, HOUR, MIN, SEC, MS, NANO;

	public static QDT valueOfStr(String pattern, QDT... defRq) {

		if (pattern == null || pattern.isEmpty()) {
			return ARG.toDefThrowMsg(() -> "Format string must not be empty", defRq);
		}

		// Выделение числовой части (поддержка отрицательных чисел)
		int i = 0;
		if (pattern.charAt(0) == '-') {
			i = 1;
		}

		while (i < pattern.length() && Character.isDigit(pattern.charAt(i))) {
			i++;
		}

		if (i == 0 || (i == 1 && pattern.charAt(0) == '-') || i == pattern.length()) {
			return ARG.toDefThrowMsg(() -> "Invalid format sequence: " + pattern, defRq);
		}

		String unitChar = pattern.substring(i);

		switch (unitChar) {
			case "n":
				return NANO;
			case "S":
				return MS;
			case "s":
				return SEC;
			case "M":
				return MIN;
			case "h":
				return HOUR;
			case "d":
				return DAY;
			case "m":
				return MONTH;
			case "y":
				return YEAR;
			default:
				return ARG.toDefThrowMsg(() -> "Invalid unit char in pattern '" + pattern + "'", defRq);

		}

	}

	public static void main(String[] args) {

		X.exit(valueOfStr("-1h"));
		X.exit(toLong("-1h", TimeUnit.MILLISECONDS));
	}

	/**
	 * Преобразует строковый формат в числовое значение в указанных единицах измерения.
	 *
	 * @param pattern Строка вида "2h", "-1s", "5M" и т.д.
	 * @param tu     Целевая единица измерения (TimeUnit)
	 * @return Значение в единицах tu
	 */
	public static Long toLong(String pattern, TimeUnit tu, Long... defRq) {
		if (pattern == null || pattern.isEmpty()) {
			return ARG.toDefThrowMsg(() -> "Format string must not be empty", defRq);
		}

		// Выделение числовой части (поддержка отрицательных чисел)
		int i = 0;
		if (pattern.charAt(0) == '-') {
			i = 1;
		}

		while (i < pattern.length() && Character.isDigit(pattern.charAt(i))) {
			i++;
		}

		if (i == 0 || (i == 1 && pattern.charAt(0) == '-') || i == pattern.length()) {
			return ARG.toDefThrowMsg(() -> "Invalid format sequence: " + pattern, defRq);
		}

		String unitChar = pattern.substring(i);

		long value = Long.parseLong(pattern.substring(0, i));

		long secondsInMonth = 30L * 24 * 60 * 60;
		long secondsInYear = 365L * 24 * 60 * 60;

		switch (unitChar) {
			case "n":
				return tu.convert(value, TimeUnit.NANOSECONDS);
			case "S":
				return tu.convert(value, TimeUnit.MILLISECONDS);
			case "s":
				return tu.convert(value, TimeUnit.SECONDS);
			case "M":
				return tu.convert(value, TimeUnit.MINUTES);
			case "h":
				return tu.convert(value, TimeUnit.HOURS);
			case "d":
				return tu.convert(value, TimeUnit.DAYS);
			case "m":
				return tu.convert(value * secondsInMonth, TimeUnit.SECONDS);
			case "y":
				return tu.convert(value * secondsInYear, TimeUnit.SECONDS);
			default:
				return ARG.toDefThrowMsg(() -> "Invalid unit char in pattern '" + pattern + "'", defRq);

		}
	}
}