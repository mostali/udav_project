package mpu.core;

import mpu.X;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class UTime {

	public static void main(String[] args) {

	}

	public static Date toDateFromEpoch(int sec, ZoneId... zoneId) {
		return Date.from(toLocalDateTime(sec, zoneId).atZone(ARG.isDef(zoneId) ? zoneId[0] : ZoneId.systemDefault()).toInstant());
	}

	public static Date toDateFromEpoch(int sec) {
		return new Date(sec * 1000L);
	}

	public static LocalDateTime toLocalDateTime(int epoch, ZoneId... zoneId) {
		return LocalDateTime.ofInstant(new Date(epoch * 1000L).toInstant(), zoneId.length == 0 ? ZoneId.systemDefault() : zoneId[0]);
	}

	public static Date toDate(LocalDateTime dateTime, ZoneId... zoneId) {
		return Date.from(dateTime.atZone(zoneId.length == 0 ? ZoneId.systemDefault() : zoneId[0]).toInstant());
	}

	public static LocalDateTime toLocalDateTime(Date dateToConvert, ZoneId... zoneId) {
		return toZonedDateTime(dateToConvert, zoneId).toLocalDateTime();
	}

	public static LocalDate toLocalDate(Date dateToConvert, ZoneId... zoneId) {
		return toZonedDateTime(dateToConvert, zoneId).toLocalDate();
	}

	public static ZonedDateTime toZonedDateTime(Date dateToConvert, ZoneId... zoneId) {
		return dateToConvert.toInstant().atZone(zoneId.length == 0 ? ZoneId.systemDefault() : zoneId[0]);
	}

	public static LocalDateTime[] rangeBetweenYearLDT(QDate qdate) {
		return rangeBetween(qdate.toLocalDateTime(), Calendar.YEAR);
	}

	public static QDate[] rangeBetweenYearAsQDate(QDate qdate) {
		return convertPare(rangeBetweenYearLDT(qdate));
	}

	public static LocalDateTime[] rangeBetweenMonthLDT(QDate qdate) {
		return rangeBetween(qdate.toLocalDateTime(), Calendar.MONTH);
	}

	public static QDate[] rangeBetweenMonthAsQDate(QDate qdate) {
		return convertPare(rangeBetweenMonthLDT(qdate));
	}

	public static LocalDateTime[] rangeBetweenWeekLDT(QDate qdate) {
		return rangeBetween(qdate.toLocalDateTime(), Calendar.WEEK_OF_MONTH);
	}

	public static QDate[] rangeBetweenWeekAsQDate(QDate qdate) {
		return convertPare(rangeBetweenWeekLDT(qdate));
	}

	public static LocalDateTime[] rangeBetweenSingleDayLDT(QDate qdate) {
		return rangeBetween(qdate.toLocalDateTime(), Calendar.DAY_OF_MONTH);
	}

	public static QDate[] rangeBetweenSingleDayAsQDate(QDate qdate) {
		return convertPare(rangeBetweenSingleDayLDT(qdate));
	}

	private static QDate[] convertPare(LocalDateTime[] ldts) {
		return new QDate[]{QDate.of(ldts[0]), QDate.of(ldts[1])};
	}

	public static <T> T[] rangeBetween(Date qdate, int calendarUnit, Class<T>... clazz) {
		return rangeBetween(qdate, calendarUnit, null, clazz);
	}

	/**
	 * QDate
	 * java.sql.Date
	 * Long.class
	 * Integer.class
	 */
	public static <T> T[] rangeBetween(Date date, int calendarUnit, ZoneId zoneId, Class<T>... clazz) {
		LocalDateTime temporal = date instanceof QDate ? ((QDate) date).toLocalDateTime() : UTime.toLocalDateTime(date);
		LocalDateTime[] dates = rangeBetween(temporal, calendarUnit);
		if (ARG.isNotDef(clazz)) {
			return (T[]) dates;
		}
		Class _class = ARG.toDef(clazz);
		if (_class == QDate.class) {
			if (zoneId == null) {
				return (T[]) Stream.of(dates).map(QDate::of).toArray(QDate[]::new);
			} else {
				return (T[]) Stream.of(dates).map(d -> QDate.of(d, zoneId)).toArray(QDate[]::new);
			}
		} else if (_class == java.sql.Date.class) {
			if (zoneId == null) {
				return (T[]) Stream.of(dates).map(d -> QDate.of(d).toSqlDate()).toArray(java.sql.Date[]::new);
			} else {
				return (T[]) Stream.of(dates).map(d -> new java.sql.Date(d.atZone(zoneId).toInstant().toEpochMilli())).toArray(java.sql.Date[]::new);
			}
		} else if (_class == Date.class) {
			if (zoneId == null) {
				ZoneId zone = zoneId == null ? ZoneId.systemDefault() : zoneId;
				return (T[]) Stream.of(dates).map(d -> toDate(d, zone)).toArray(Date[]::new);
			} else {
				return (T[]) Stream.of(dates).map(d -> new Date(d.atZone(zoneId).toInstant().toEpochMilli())).toArray(Date[]::new);
			}
		} else if (_class == Long.class) {
			ZoneId zone = zoneId == null ? ZoneId.systemDefault() : zoneId;
			return (T[]) Stream.of(dates).map(d -> d.atZone(zone).toInstant().toEpochMilli()).toArray(Long[]::new);
		} else if (_class == Integer.class) {
			ZoneId zone = zoneId == null ? ZoneId.systemDefault() : zoneId;
			return (T[]) Stream.of(dates).map(d -> d.atZone(zone).toInstant().getEpochSecond()).toArray(Long[]::new);
		}
		throw new NI("clazz ni:" + _class);

	}

	public static LocalDateTime[] rangeBetween(LocalDateTime time, int calendarUnit) {
		LocalDateTime d1 = null;
		LocalDateTime d2 = null;
		switch (calendarUnit) {
			case Calendar.DAY_OF_WEEK:
			case Calendar.DAY_OF_WEEK_IN_MONTH:
			case Calendar.DAY_OF_MONTH:
			case Calendar.DAY_OF_YEAR:
				d1 = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), 0, 0, 0);// .minusDays(countUnit)
				d2 = LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), 23, 59, 59);
				break;
			case Calendar.MONTH: {
				Calendar gCal = new GregorianCalendar(time.getYear(), time.getMonthValue(), time.getDayOfMonth());
				d1 = LocalDateTime.of(time.getYear(), time.getMonth(), 1, 0, 0, 0);
				d2 = LocalDateTime.of(time.getYear(), time.getMonth(), gCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
				break;
			}
			case Calendar.YEAR: {
				d1 = LocalDateTime.of(time.getYear(), 1, 1, 0, 0, 0);
				d2 = LocalDateTime.of(time.getYear(), 12, 31, 23, 59, 59);
				break;
			}
			default:
				throw new IllegalArgumentException("Not implemented calendar unit UTime:" + calendarUnit);
		}

		return new LocalDateTime[]{d1, d2};

	}

	public static BigDecimal toTime(long duration, TimeUnit timeUnitData, TimeUnit timeUnitTarget, Integer... scale) {
		Double val = null;
		switch (timeUnitTarget) {
			case DAYS:
				val = (double) timeUnitData.toHours(duration) / 24;
				break;
			case HOURS:
				val = (double) timeUnitData.toMinutes(duration) / 60;
				break;
			case MINUTES:
				val = (double) timeUnitData.toSeconds(duration) / 60;
				break;
			case SECONDS:
				val = (double) timeUnitData.toMillis(duration) / (1000);
				break;
			default:
				throw new WhatIsTypeException(timeUnitData);

		}
		BigDecimal bd = new BigDecimal(val);
		return ARG.isDefNNF(scale) ? bd.setScale(ARG.toDef(scale), RoundingMode.HALF_DOWN) : bd;
	}

	public static String toStringTimeHumanlySec(long seconds) {
		if (seconds < QDate.MIN_SEC) {
			return seconds + " seconds";
		} else if (seconds < QDate.HOUR_SEC) {
			return toTime(seconds, TimeUnit.SECONDS, TimeUnit.MINUTES, 0) + " minutes";
		} else if (seconds < QDate.DAY_SEC) {
			return toTime(seconds, TimeUnit.SECONDS, TimeUnit.HOURS, 0) + " hours";
		} else {
			return toTime(seconds, TimeUnit.SECONDS, TimeUnit.DAYS, 0) + " days";
		}
	}

	public static String toStringTimeHumanlyMs(long ms) {
		if (ms < QDate.SEC_MS) {
			return ms + "ms";
		} else if (ms < QDate.MIN_SEC * 1000) {
			return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 1) + "s";
		} else if (ms < QDate.DAY_SEC * 1000) {
			return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.MINUTES, 1) + "m";
		} else {
			return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.HOURS, 1) + "h";
		}
	}

	public static String toStringTimeHumanlyMsRuShort(long ms) {
		if (ms < QDate.SEC_MS) {
			return ms + "мс";
		} else if (ms < QDate.MIN_SEC * 1000) {
			return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 1) + "с";
		} else if (ms < QDate.DAY_SEC * 1000) {
			return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.MINUTES, 1) + "м";
		} else {
			return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.HOURS, 1) + "ч";
		}
	}

	public static Date addDays(Date date, int days) {
		return add(date, Calendar.DAY_OF_MONTH, days);
	}

	public static Date addHours(Date date, int count) {
		return add(date, Calendar.HOUR, count);
	}

	public static Date addMinutes(Date date, int count) {
		return add(date, Calendar.MINUTE, count);
	}

	public static Date add(Date date, int time_unit, int count) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(time_unit, count); // number of days to add
		Date newDate = cal.getTime();
		return newDate;
	}

	public static QDate setLastDayOfMonth(QDate qDate) {
		int m = qDate.month;
		do {
			qDate = qDate.addDays(1);
		} while (qDate.month.equals(m));
		return qDate.addDays(-1);
	}

	public static boolean isExpired(long expired_ms) {
		return System.currentTimeMillis() >= expired_ms;
	}

	public static Date parseDateCustom(String string, String format, Integer[] year_month_day_hour_min_sec, Date... defRq) {
		try {
			SimpleDateFormat dateInputFormat = new SimpleDateFormat(format);
			Calendar parsedCal = Calendar.getInstance();
			Date parse = dateInputFormat.parse(string);
			parsedCal.setTime(parse);
			updateDate(parsedCal, year_month_day_hour_min_sec);
			return parsedCal.getTime();
		} catch (ParseException e) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(e, "Parse date error from value '%s' format '%s',", string, format), defRq);
		}
	}

	public static Date updateDate(Date date, Integer... year_month_day_hour_min_sec) {
		Calendar instance = Calendar.getInstance();
		instance.setTime(date);
		updateDate(instance, year_month_day_hour_min_sec);
		return instance.getTime();
	}

	public static void updateDate(Calendar date, Integer... year_month_day_hour_min_sec) {
		if (X.empty(year_month_day_hour_min_sec)) {
			return;
		}
		Integer year = ARRi.itemAs(year_month_day_hour_min_sec, 0, Integer.class, null);
		if (year != null) {
			date.set(Calendar.YEAR, year);
		}
		Integer month = ARRi.itemAs(year_month_day_hour_min_sec, 1, Integer.class, null);
		if (month != null) {
			date.set(Calendar.MONTH, month);
		}
		Integer day = ARRi.itemAs(year_month_day_hour_min_sec, 2, Integer.class, null);
		if (day != null) {
			date.set(Calendar.DAY_OF_MONTH, day);
		}
		Integer hours = ARRi.itemAs(year_month_day_hour_min_sec, 3, Integer.class, null);
		if (hours != null) {
			date.set(Calendar.HOUR_OF_DAY, hours);
		}
		Integer minutes = ARRi.itemAs(year_month_day_hour_min_sec, 4, Integer.class, null);
		if (minutes != null) {
			date.set(Calendar.MINUTE, minutes);
		}
		Integer seconds = ARRi.itemAs(year_month_day_hour_min_sec, 5, Integer.class, null);
		if (seconds != null) {
			date.set(Calendar.SECOND, seconds);
		}
		Integer ms = ARRi.itemAs(year_month_day_hour_min_sec, 6, Integer.class, null);
		if (ms != null) {
			date.set(Calendar.MILLISECOND, ms);
		}
	}

	public static String toString(Date date, String formatDate) {
		return new SimpleDateFormat(formatDate).format(date);
	}

	public static Boolean equals(Date date1, Date date2) {
		return date1 == null || date2 == null ? false : date1.compareTo(date2) == 0;
	}

	public static Date toDateOrNow(Date value) {
		return value == null ? new Date() : value;
	}

	public static Date toDate(Date value, Date def) {
		return value == null ? def : value;
	}

}
