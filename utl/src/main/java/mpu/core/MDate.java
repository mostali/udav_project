package mpu.core;

import lombok.SneakyThrows;
import mpc.log.L;
import mpu.X;
import mpu.func.Function2;
import mpu.pare.Pare;
import mpu.str.UST;

import java.util.Calendar;
import java.util.Date;

/*
java class который парсит строку в дату, и дату в строку
Шаблон строки, например
ma01 возвращает Date 01.01.2025
mb03 возвращает Date 03.02.2025
ne11 возвращает Date 11.05.2026
т.е. с буквы  m до z - это года от 2025(m) 2026(n) и т.д.
 */
public class MDate {


	//
	//
	private static final int BASE_YEAR = 2025;

	/**
	 * Парсит строку вида "ma01", "ne11" в java.util.Date
	 *
	 * @param input строка формата [a-z][a-z][0-9]{2}
	 * @return Date или null при ошибке
	 */

	public static String now() {
		return formatDateToString(System.currentTimeMillis());
	}

	@SneakyThrows
	public static QDate parseStringToQDate(String input, boolean strictPrimaryZero, QDate... defRq) {
		Pare<Date, String> dateStringPare = parseStringToDate0(input, strictPrimaryZero);
		return dateStringPare.hasKey() ? QDate.of(dateStringPare.key()) : ARG.toDefThrowMsg(() -> dateStringPare.val(), defRq);
	}

	@SneakyThrows
	public static Pare<Date, String> parseStringToDate0(String input, boolean strictPrimaryZero) {

		int maxLen = strictPrimaryZero ? 4 : 3;
		String strictMsg = strictPrimaryZero ? "STRICT" : "SIMPLE";

		Function2<String, Boolean, String> lenVailidator = (in, strict) -> {
			int len = in.length();
			if (in == null || (strict ? len != 4 : !(len == 3 || len == 4))) {
				return X.f_("MDate '%s' %s illegal length [%s] ", in, strictMsg, maxLen);
			}
			return null;
		};

		String err = lenVailidator.apply(input, strictPrimaryZero);
		if (err != null) {
			return Pare.of(null, err);
		}

		char yearChar = input.charAt(0);
		char monthChar = input.charAt(1);
		String dayStr = input.substring(2);

		// Проверка, что последние два символа — цифры
		Function2<String, Boolean, String> dayVailidator = (dayStr0, strict) -> {
			int maxDayLen = strict ? 2 : 1;
			if (strictPrimaryZero ? maxDayLen != 2 : !(maxDayLen != 2 || maxDayLen != 1)) {
				return X.f_("MDate '%s' %s illegal DAY, except length [%s] ", input, strictMsg, maxDayLen);
			}
			Integer datInt = UST.INT(dayStr0, null);
			if (datInt == null) {
				return X.f_("MDate '%s' %s illegal DAY [%s] ", dayStr0, strictMsg, input);
			}
			return null;
		};

		err = dayVailidator.apply(dayStr, strictPrimaryZero);
		if (err != null) {
			return Pare.of(null, err);
		}

		int day = Integer.parseInt(dayStr);
		if (day < 1 || day > 31) {
			String msg = X.f_("MDate '%s' %s illegal DAY num [%s] ", input, strictMsg, day);
			return Pare.of(null, msg);
		}

		// Определяем год по первой букве
		if (yearChar < 'm' || yearChar > 'z') {
			return Pare.of(null, X.f_("MDate '%s' %s illegal YEAR char [%s] ", input, strictMsg, yearChar));
		}

		int year = BASE_YEAR + (yearChar - 'm');

		// Определяем месяц по второй букве: 'a' = 1, 'b' = 2, ..., 'l' = 12
		if (monthChar < 'a' || monthChar > 'l') {
			return Pare.of(null, X.f_("MDate '%s' %s illegal MONTH char [%s] ", input, strictMsg, monthChar));
		}
		int month = monthChar - 'a' + 1; // Calendar.MONTH is 0-based, но мы используем Calendar напрямую

		// Создаём дату через Calendar
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false); // строгая проверка даты (например, 31.04 будет ошибкой)
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1); // Calendar.MONTH is 0-based
		cal.set(Calendar.DAY_OF_MONTH, day);

		try {
			cal.getTime(); // вызовет IllegalArgumentException, если дата недопустима (например, 31.04)
			return Pare.of(cal.getTime(), null);
		} catch (Exception e) {
			String msg = X.f_("MDate '%s' %s parse time error [%s] ", input, strictMsg, e.getClass().getName() + " - " + e.getMessage());
			if (L.isDebugEnabled()) {
				L.debug(msg, e);
			}
			return Pare.of(null, msg);
		}
	}

	/**
	 * Преобразует Date в строку вида "ma01"
	 *
	 * @param date входная дата
	 * @return строка или null, если дата вне диапазона
	 */

	public static String formatDateToString(Date date) {
		return formatDateToString(date.getTime());
	}

	public static String formatDateToStringNow() {
		return formatDateToString(System.currentTimeMillis());
	}

	public static String formatDateToString(long ms) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ms);

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1; // 1-based
		int day = cal.get(Calendar.DAY_OF_MONTH);

		// Проверка диапазона лет: от 2025 ('m') до 2038 ('z')
		if (year < BASE_YEAR || year > BASE_YEAR + ('z' - 'm')) {
			return null; // вне поддерживаемого диапазона
		}
		if (month < 1 || month > 12) {
			return null;
		}
		if (day < 1 || day > 31) {
			return null;
		}

		char yearChar = (char) ('m' + (year - BASE_YEAR));
		char monthChar = (char) ('a' + (month - 1));

		return String.format("%c%c%02d", yearChar, monthChar, day);
	}

	// Пример использования
	public static void main(String[] args) {

		System.out.println(parseStringToDate0("ma01", false)); // Sun Jan 01 00:00:00 ... 2025
		System.out.println(parseStringToDate0("mb03", true)); // Tue Feb 03 00:00:00 ... 2025
		System.out.println(parseStringToDate0("ne11", true)); // Mon May 11 00:00:00 ... 2026
		System.out.println(parseStringToDate0("mj23", true)); // Mon May 11 00:00:00 ... 2026

		System.out.println(parseStringToDate0("ma01", true)); // ma01

		// Проверка ошибок
		System.out.println(parseStringToQDate("ma32", true)); // null (некорректный день)
		System.out.println(parseStringToQDate("mz01", true)); // null (месяц 'z' — недопустим)
	}

	public static String nowStr() {
		return formatDateToString(new Date());
	}

	//
	//

//	private static final int BASE_YEAR = 2025;
//
//	public static LocalDate parse(String input) {
//		if (input == null || input.length() != 4) return null;
//		if (!input.substring(2).matches("\\d{2}")) return null;
//
//		char y = input.charAt(0);
//		char m = input.charAt(1);
//		int day = Integer.parseInt(input.substring(2));
//
//		if (y < 'm' || y > 'z') return null;
//		if (m < 'a' || m > 'l') return null;
//
//		int year = BASE_YEAR + (y - 'm');
//		int month = m - 'a' + 1;
//
//		try {
//			return LocalDate.of(year, month, day);
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//	public static String format(LocalDate date) {
//		if (date == null) return null;
//		int year = date.getYear();
//		if (year < BASE_YEAR || year > BASE_YEAR + ('z' - 'm')) return null;
//
//		char y = (char) ('m' + (year - BASE_YEAR));
//		char m = (char) ('a' + (date.getMonthValue() - 1));
//		return String.format("%c%c%02d", y, m, date.getDayOfMonth());
//	}
//
//	public static void main(String[] args) {
//		System.out.println(parse("ma01")); // 2025-01-01
//		System.out.println(format(LocalDate.of(2025, 1, 1))); // ma01
//	}

}

