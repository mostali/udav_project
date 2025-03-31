package mpu.core;

import mpc.exception.RequiredRuntimeException;

import java.util.concurrent.TimeUnit;

//ChatGpt
public class TimeMark {
	public static Long convertToMs(String timeString, Long... defRq) {
		return convert(timeString, TimeUnit.MILLISECONDS, defRq);
	}

	public static Long convert(String timeString, TimeUnit timeUnit, Long... defRq) {
		try {
			return convert(timeString, timeUnit);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Illegal MarkPeriodTime pattern '%s' for convert to '%s'", timeString, timeUnit), defRq);
		}
	}

	public static long convert(String timeString, TimeUnit timeUnit) {
		// Удаляем пробелы и приводим к нижнему регистру
		timeString = timeString.trim();

		// Определяем количество и единицу времени
		int number;
		char unit = ' ';

		// Извлекаем число и единицу из строки
		StringBuilder numberBuilder = new StringBuilder();
		for (char c : timeString.toCharArray()) {
			if (Character.isDigit(c)) {
				numberBuilder.append(c);
			} else {
				unit = c;
				break;
			}
		}

		// Преобразуем строку с числом в целое число
		if (numberBuilder.length() == 0) {
			throw new IllegalArgumentException("Неверный формат строки: " + timeString);
		}
		number = Integer.parseInt(numberBuilder.toString());

		// Конвертируем в миллисекунды
		long milliseconds;
		switch (unit) {
			case 'y': // годы
				milliseconds = TimeUnit.DAYS.toMillis(number * 365); // Упрощенное преобразование, не учитывает високосные годы
				break;
			case 'M': // месяцы
				milliseconds = TimeUnit.DAYS.toMillis(number * 30); // Упрощенное преобразование, не учитывает разное количество дней в месяцах
				break;
			case 'd': // дни
				milliseconds = TimeUnit.DAYS.toMillis(number);
				break;
			case 'h': // часы
				milliseconds = TimeUnit.HOURS.toMillis(number);
				break;
			case 'm': // минуты
				milliseconds = TimeUnit.MINUTES.toMillis(number);
				break;
			case 's': // секунды
				milliseconds = TimeUnit.SECONDS.toMillis(number);
				break;
			case 'S': // миллисекунды
				milliseconds = number; // уже в миллисекундах
				break;
			case 'n': // наносекунды
				milliseconds = number / 1_000_000; // переводим в миллисекунды
				break;
			default:
				throw new IllegalArgumentException("Неверный формат строки: " + timeString);
		}

		// Преобразуем миллисекунды в нужный TimeUnit и возвращаем результат
		return timeUnit.convert(milliseconds, TimeUnit.MILLISECONDS);
	}

	public static void main(String[] args) {
		System.out.println(convert("1y", TimeUnit.DAYS)); // Пример: 365 дней (примерное значение для года)
		System.out.println(convert("5d", TimeUnit.HOURS)); // 120 часов
		System.out.println(convert("10h", TimeUnit.MINUTES)); // 600 минут
		System.out.println(convert("1s", TimeUnit.MILLISECONDS)); // 1000 миллисекунд
		System.out.println(convert("500n", TimeUnit.MILLISECONDS)); // 0.5 миллисекунд
	}
}
