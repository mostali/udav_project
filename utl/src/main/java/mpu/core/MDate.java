package mpu.core;

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
public class MDate extends QDate {

//	public static void of(String[] args) {
//
//	}
//
//	public AlphaDate(Date date) {
//		super(date);
//	}


	//
	//
	private static final int BASE_YEAR = 2025;
//	private static final char START_LETTER = 'm'; // 'm' -> 2025

	/**
	 * Парсит строку вида "ma01", "ne11" в java.util.Date
	 *
	 * @param input строка формата [a-z][a-z][0-9]{2}
	 * @return Date или null при ошибке
	 */
	public static Date parseStringToDate(String input) {
		if (input == null || input.length() != 4) {
			return null;
		}

		char yearChar = input.charAt(0);
		char monthChar = input.charAt(1);
		String dayStr = input.substring(2);

		// Проверка, что последние два символа — цифры
		if (!dayStr.matches("\\d{2}")) {
			return null;
		}

		int day = Integer.parseInt(dayStr);
		if (day < 1 || day > 31) {
			return null;
		}

		// Определяем год по первой букве
		if (yearChar < 'm' || yearChar > 'z') {
			return null;
		}
		int year = BASE_YEAR + (yearChar - 'm');

		// Определяем месяц по второй букве: 'a' = 1, 'b' = 2, ..., 'l' = 12
		if (monthChar < 'a' || monthChar > 'l') {
			return null;
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
			return cal.getTime();
		} catch (Exception e) {
			return null;
		}
	}

	public static String formatDateToString(long ms) {
		return formatDateToString(new Date(ms));
	}

	/**
	 * Преобразует Date в строку вида "ma01"
	 *
	 * @param date входная дата
	 * @return строка или null, если дата вне диапазона
	 */
	public static String formatDateToString(Date date) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

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
		System.out.println(parseStringToDate("ma01")); // Sun Jan 01 00:00:00 ... 2025
		System.out.println(parseStringToDate("mb03")); // Tue Feb 03 00:00:00 ... 2025
		System.out.println(parseStringToDate("ne11")); // Mon May 11 00:00:00 ... 2026
		System.out.println(parseStringToDate("mj23")); // Mon May 11 00:00:00 ... 2026

		Date date = parseStringToDate("ma01");
		System.out.println(formatDateToString(date)); // ma01

		// Проверка ошибок
		System.out.println(parseStringToDate("ma32")); // null (некорректный день)
		System.out.println(parseStringToDate("mz01")); // null (месяц 'z' — недопустим)
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

