package mpu.core;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.IT;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.num.UNum;
import mpu.str.UST;
import mpe.ftypes.core.FDate;
import mpu.Sys;
import mpu.X;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

//QuickDate
public class QDate extends Date {

	public static final Date MIN_DATE = new Date(0);
	public static final Date MAX_DATE = new Date(Long.MAX_VALUE);
	public static final long SEC_MS = 1000;
	public static final long MIN_SEC = 60;
	public static final long HOUR_SEC = 3600;
	public static final long DAY_SEC = HOUR_SEC * 24;
	//
	//
	public static final SimpleDateFormat CURRENT_YEAR = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat CURRENT_MONTH = new SimpleDateFormat("MM");
	public static final SimpleDateFormat CURRENT_DAY = new SimpleDateFormat("dd");
	public static final SimpleDateFormat CURRENT_HOUR = new SimpleDateFormat("HH");
	public static final SimpleDateFormat CURRENT_MINUTES = new SimpleDateFormat("mm");
	public static final SimpleDateFormat CURRENT_SECONDS = new SimpleDateFormat("ss");
	public static final SimpleDateFormat CURRENT_MILLIS = new SimpleDateFormat("SSS");
	public static final SimpleDateFormat CURRENT_YYMMDD = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat CURRENT_HHMMSS = new SimpleDateFormat("HH:mm:ss");

	public QDate() {
		this(new Date());
	}

	public final Integer year;
	public final Integer day;
	public final Integer month;
	public final Integer hour;
	public final Integer minutes;
	public final Integer seconds;
	public final Integer millis;

	public static void main(String[] args) {
		QDate qDate = QDate.now().addDays(-3);
		QDate now = QDate.now();
//		X.exit(qDate.diff_WRONG_MINUS(now) + "|" + qDate.diff(now));
	}

	public static QDate[] nowNewPeriod(long ms) {
		QDate now = QDate.now();
		if (ms == 0L) {
			return new QDate[]{now, now};
		} else if (ms > 0) {
			return new QDate[]{now, now.addMilliSeconds((int) ms)};
		}
		return new QDate[]{now.addMilliSeconds((int) ms), now};
	}

	public static LocalDateTime nowLdt(ZoneId... zoneId) {
		return QDate.now().toLocalDateTime(zoneId);
	}

	private static Date toDateWithManlyMonth(Integer year, Integer month, Integer day, Integer hour, Integer minutes, Integer seconds, Integer millis) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day, hour, minutes, seconds);
		if (millis != null) {
			cal.set(Calendar.MILLISECOND, millis);
		}
		return cal.getTime();
	}

	public QDate as(ZoneId zoneId) {
		return of(ZonedDateTime.of(toLocalDateTime(), zoneId).toLocalDateTime());
	}

	private QDate(QDate date) {
		this(date.year, date.month, date.day, date.hour, date.minutes, date.seconds);
	}

	public QDate(Date date) {
		this(Integer.parseInt(CURRENT_YEAR.format(date)),//
				Integer.parseInt(CURRENT_MONTH.format(date)),//
				Integer.parseInt(CURRENT_DAY.format(date)),//
				Integer.parseInt(CURRENT_HOUR.format(date)),//
				Integer.parseInt(CURRENT_MINUTES.format(date)),//
				Integer.parseInt(CURRENT_SECONDS.format(date)),
				Integer.parseInt(CURRENT_SECONDS.format(date))
		);
	}

	public QDate(Integer year, Integer month, Integer day, Integer hour, Integer minutes, Integer seconds) {
		this(year, month, day, hour, minutes, seconds, null);
	}

	public QDate(Integer year, Integer month, Integer day, Integer hour, Integer minutes, Integer seconds, Integer millis) {
		super(toDateWithManlyMonth(year, month, day, hour, minutes, seconds, millis).getTime());
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minutes = minutes;
		this.seconds = seconds;
		this.millis = millis;
	}

	public QDate(String dateTime) throws ParseException {
		this(dateTime, false);
	}

	public QDate(String dateTime, boolean isMono14or8) throws ParseException {
		this(dateTime, isMono14or8 ? "yyyyMMddhhmmss" : "yyyyMMdd");
	}

	public QDate(String dateTime, String format) throws ParseException {
		this(new SimpleDateFormat(format).parse(dateTime));
	}

	@Override
	public String toString() {
//		return String.format("%s-%s-%s %s:%s:%s", year, m2month(), m2day(),
//				m2hour(), m2minutes(), m2seconds());
//		return f(F.MONO24NF_MS);
		return f(FDate.UTC_MS);
	}


	public QDate minusYears(int countYears) {
		return new QDate(year - countYears, month, day, hour, minutes, seconds, millis);
	}

	//QDate.of(Date.from(Instant.ofEpochMilli(ms())))
	public LocalDateTime toLocalDateTime(ZoneId... zoneId) {
		LocalDateTime localDateTime;
		if (millis == null) {
			localDateTime = LocalDateTime.of(year, month, day, hour, minutes, seconds);
		} else {
			localDateTime = LocalDateTime.of(year, month, day, hour, minutes, seconds, (int) TimeUnit.MILLISECONDS.toNanos(millis));
		}
		return ARG.isDef(zoneId) ? localDateTime.atZone(ARG.toDef(zoneId)).toLocalDateTime() : localDateTime;
	}

	public String toSqlString(ZoneId zoneId) {
		return SqlDate.toSqlDateOrNow(toDate(zoneId));
	}

	public String toSqlString() {
		return SqlDate.toSqlDateOrNow(toDate());
	}

	public Date toDate() {
		return toDate(null);
	}

	public Date toDate(ZoneId zoneId) {
		zoneId = zoneId == null ? ZoneId.systemDefault() : zoneId;
		Date d = Date.from(toLocalDateTime().atZone(zoneId).toInstant());
		return d;
	}

	public static int nowEpoch() {
		return now().epoch();
	}

	public static QDate now() {
		return QDate.of(new Date());
	}

	public static QDate now(ZoneId zoneMsk) {
		Clock clock = Clock.system(zoneMsk);
		LocalDateTime ldt = LocalDateTime.now(clock);
		return QDate.of(ldt);
	}

	public static QDate of(int year, int month, int day) {
		return of(LocalDateTime.of(year, month, day, 0, 0, 0));
	}

	public static QDate of(int year, int month, int day, int hour, int mintutes, int second) {
		return of(LocalDateTime.of(year, month, day, hour, mintutes, second));
	}

	public static QDate of(LocalDateTime ldt, ZoneId... zoneId) {
		ZoneId zid = ARG.isDef(zoneId) ? ARG.toDef(zoneId) : ZoneId.systemDefault();
		return of(ldt.atZone(zid).toInstant().toEpochMilli());
	}

	public static QDate of(File file, ZoneId... zoneId) {
		QDate d = QDate.of(file.lastModified());
		ZoneId zid = ARG.isDef(zoneId) ? ARG.toDef(zoneId) : ZoneId.systemDefault();
		return ARG.isDef(zoneId) ? d.as(zid) : d;
	}

	/**
	 * 990519
	 */
//	public static QDate ofMonodate6(String monodate6) {
//		QDate qdate;
//		try {
//			Integer y = Integer.parseInt(monodate6.substring(0, 2));
//			Integer m = Integer.parseInt(monodate6.substring(2, 4));
//			Integer d = Integer.parseInt(monodate6.substring(4, 6));
//			qdate = new QDate(Integer.parseInt(20 + "" + y), m, d, 0, 0, 0);
//		} finally {
//		}
//		return qdate;
//	}

	/**
	 * 20190519
	 */
	public static QDate ofMonodate8(String monodate8) {
		QDate qdate = null;
		try {
			monodate8 = monodate8.trim();
			Integer y = Integer.parseInt(monodate8.substring(0, 4));
			Integer m = Integer.parseInt(monodate8.substring(4, 6));
			Integer d = Integer.parseInt(monodate8.substring(6, 8));
			// U.exit("y:" + y + "," + "m:" + m + "," + "d:" + d);
			qdate = new QDate(y, m, d, 0, 0, 0);
			return qdate;
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid monodate8:" + monodate8);
		}
	}

//	public static QDate ofMn14Safe(String monodate14) {
//		try {
//			return ofMono14(monodate14);
//		} catch (Exception ex) {
//			return null;
//		}
//	}

	/**
	 * 2022-01-02 13:55:59
	 *
	 * @param monodate14
	 * @return
	 */
	@SneakyThrows
	public static QDate ofIso(String monodate14, QDate... defRq) {
		try {
			return of(FDate.DB_ISO_STANDART(monodate14));
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> ex, defRq);
		}
	}

	/**
	 * 20190519235502
	 */
	@SneakyThrows
	public static QDate ofMono14(String monodate14, QDate... defRq) {
		try {
			monodate14 = monodate14.trim();
			Integer y = Integer.parseInt(monodate14.substring(0, 4));
			Integer m = Integer.parseInt(monodate14.substring(4, 6));
			Integer d = Integer.parseInt(monodate14.substring(6, 8));

			Integer H = Integer.parseInt(monodate14.substring(8, 10));
			Integer M = Integer.parseInt(monodate14.substring(10, 12));
			Integer S = Integer.parseInt(monodate14.substring(12, 14));

			return new QDate(y, m, d, H, M, S);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> ex, defRq);
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- Mono ---------------------------
	 * *************************************************************
	 */

	/**
	 * 991201
	 */
	public String mono6_y2d2() {
		return m2year() + mono4_m2d2();
	}


	/**
	 * 99120123
	 */
	public String mono8_y2m2d2h2() {
		return m2year() + m2month() + m2day() + m2hour();
	}

	/**
	 * 199901
	 */
	public String mono6_y4m2() {
		return year + m2month();
	}

	/**
	 * 1208
	 */
	public String mono4_m2d2() {
		return m2month() + m2day();
	}

	/**
	 * 2359
	 */
	public String mono4_h2m2() {
		return m2hour() + m2minutes();
	}


	/**
	 * 991231005959
	 */
	public String mono12_y2s2() {
		return mono6_y2d2() + mono6_h2s2();
	}

	/**
	 * 005959
	 */
	public String mono6_h2s2() {
		return m2hour() + m2minutes() + m2seconds();
	}

	/**
	 * 19990108
	 */
	public String mono8_y4m2d2() {
		return String.valueOf(year) + m2month() + m2day();
	}

	/**
	 * 19991231005959
	 */
	public String mono14_y4s2() {
		return f(QDate.F.YYYYMMDD) + mono6_h2s2();
	}


	/**
	 * 19991231 0059
	 */
	public String mono13W_y4m2() {
		return f(QDate.F.YYYYMMDD) + "-" + m2hour() + m2minutes();
	}


	/**
	 * MONO17NF 1999-12-31 00:59
	 */
	public String f(F format) {
		return format.build(this);
	}

	public String f(String format, Integer... ymd_hms_sss) {
		return new SimpleDateFormat(format).format(ymd_hms_sss.length == 0 ? this : UTime.updateDate(this, ymd_hms_sss));
	}

	public boolean equals(QDate date, int calendar_period) {
		switch (calendar_period) {
			case Calendar.YEAR:
				return year == (int) date.year;
			case Calendar.MONTH:
				return (year == (int) date.year) && (month == (int) date.month);
			case Calendar.DAY_OF_WEEK:
			case Calendar.DAY_OF_WEEK_IN_MONTH:
			case Calendar.DAY_OF_MONTH:
			case Calendar.DAY_OF_YEAR:
				return (year == (int) date.year) && (month == (int) date.month) && (day == (int) date.day);
			case Calendar.HOUR:
			case Calendar.HOUR_OF_DAY:
				return (hour == (int) date.hour) && (year == (int) date.year) && (month == (int) date.month) && (day == (int) date.day);
			case Calendar.MINUTE:
				return (minutes == (int) date.minutes) && (hour == (int) date.hour) && (year == (int) date.year) && (month == (int) date.month) && (day == (int) date.day);
			case Calendar.SECOND:
				return (seconds == (int) date.seconds) && (minutes == (int) date.minutes) && (hour == (int) date.hour) && (year == (int) date.year) && (month == (int) date.month) && (day == (int) date.day);
			default:
				throw new WhatIsTypeException(calendar_period);
		}
	}

	public QDate[] border(int dayOfMonth, ZoneId... zoneId) {
		if (true) {
			switch (dayOfMonth) {
				case Calendar.DAY_OF_MONTH:
				case Calendar.DAY_OF_WEEK:
				case Calendar.MONTH:
				case Calendar.YEAR:
					return UTime.rangeBetween(this, dayOfMonth, ARG.toDefOrNull(zoneId), QDate.class);
				default:
					throw new WhatIsTypeException(dayOfMonth);
			}
		}
		switch (dayOfMonth) {
			case Calendar.DAY_OF_MONTH:
				return UTime.rangeBetween(this, dayOfMonth, ARG.toDefOrNull(zoneId), QDate.class);
			case Calendar.DAY_OF_WEEK:
				return UTime.rangeBetweenWeekAsQDate(this);
			case Calendar.MONTH:
				return UTime.rangeBetweenMonthAsQDate(this);
			case Calendar.YEAR:
				return UTime.rangeBetweenYearAsQDate(this);
			default:
				throw new WhatIsTypeException(dayOfMonth);
		}
	}

	public int getDayNum(boolean... ruNums) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		if (ARG.isDefNotEqTrue(ruNums)) {
			return day;
		}
		//calendar.getFirstDayOfWeek() она вернет число соответствующее первому дню недели для той страны с локалью которой был создан календарь (по умолчанию это региональные установки системы, но это можно изменить). Произведя вычитание и взяв по модулю, можно получить порядковый номер дня недели в неделе.
		//		int offset = cal.getFirstDayOfWeek();
		//		int day = cal.get(Calendar.DAY_OF_WEEK);
		//		return ((day - offset + 7) % 7 + 1);
		return --day == 0 ? 7 : day;
	}

	@RequiredArgsConstructor
	public enum F {
		/**
		 * 2022-02-01 22:19
		 */
		MONO17NF("%s-%s-%s %s:%s", "yyyy-MM-dd HH:mm"),
		YYYYMMDD("%s%s%s", "yyyyMMdd"),
		YYYY_MM_DD("%s-%s-%s", "yyyy-MM-dd"),

		/**
		 * 2022-02-01 22:19:55
		 */
		MONO20NF("%s-%s-%s %s:%s:%s", "yyyy-MM-dd HH:mm:ss"),
		MONO24NF_MS("%s-%s-%s %s:%s:%s.%s", "yyyy-MM-dd HH:mm:ss.SSS"),
		/**
		 * 19991231_135959
		 */
		MONO15_SEC("%s%s%s%s%s%s", "yyyyMMddHHmmss"),
		MONO15_FILE_SEC("%s%s%s_%s%s%s", "yyyyMMdd_HHmmss"),
		MONOTWICE("%s%s%s:%s%s%s", "yyyyMMdd:HHmmss"),
		HH_mm("%s-%s", "HH-mm"),
		HH__mm("%s.%s", "HH.mm"),
		DD__MM("%s.%s", "dd.MM"),
		HH_mm_ss("%s-%s-%s", "HH-mm-ss"),
		mm_ss("%s-%s", "mm-ss");

		private final String formatToString;
		public final String format;


		public String build(LocalDateTime q, ZoneId... zoneId) {
			return build(of(q, zoneId));
		}

		public String build(QDate q) {
			switch (this) {
				case HH__mm:
				case HH_mm:
					return X.f(formatToString, q.m2hour(), q.m2minutes());
				case DD__MM:
					return X.f(formatToString, q.m2day(), q.m2month());
				case mm_ss:
					return X.f(formatToString, q.m2minutes(), q.m2seconds());
				case HH_mm_ss:
					return X.f(formatToString, q.m2hour(), q.m2minutes(), q.m2seconds());
				case YYYY_MM_DD:
				case YYYYMMDD:
					return String.format(formatToString, q.year, q.m2month(), q.m2day());

				case MONO17NF:
					return String.format(formatToString, q.year, q.m2month(), q.m2day(), q.m2hour(), q.m2minutes());

				case MONOTWICE:
				case MONO15_SEC:
				case MONO20NF:
					return String.format(formatToString, q.year, q.m2month(), q.m2day(), q.m2hour(), q.m2minutes(), q.m2seconds());
				case MONO24NF_MS:
					return String.format(formatToString, q.year, q.m2month(), q.m2day(), q.m2hour(), q.m2minutes(), q.m2seconds(), q.m3millis());
				case MONO15_FILE_SEC:
					return String.format(formatToString, q.year, q.m2month(), q.m2day(), q.m2hour(), q.m2minutes(), q.m2seconds());

				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	/**
	 * *************************************************************
	 * ----------------------------- M2 ----------------------------
	 * *************************************************************
	 */

	public String m2year() {
		return String.valueOf(year).substring(2);
	}

	//
	//
	public String m2month() {
		return l2(month);
	}

	public String m2day() {
		return l2(day);
	}

	public String m2hour() {
		return l2(hour);
	}

	public String m2minutes() {
		return l2(minutes);
	}

	public String m2seconds() {
		return l2(seconds);
	}

	public String m3millis() {
		return millis == null ? "000" : l3(millis);
	}

	public static String l2(int v) {
		String m = String.valueOf(v);
		return m.length() == 2 ? m : "0" + m;
	}

	public static String l3(int v, boolean... allowCutEnd) {
		String m = String.valueOf(v);
		switch (m.length()) {
			case 1:
				return "00" + m;
			case 2:
				return "0" + m;
			case 3:
				return m + "";
			default:
				if (ARG.isDefEqTrue(allowCutEnd)) {
					return (v + "").substring(0, 3);
				}
				throw new FIllegalArgumentException("Except length <= 3, but cam %s", v);
		}
	}

	//
	//
	public static QDate of(Date date) {
		return date instanceof QDate ? (QDate) date : of(date, (TimeZone) null);
	}

	public static QDate of(Date date, ZoneId zoneId) {
		TimeZone tz = TimeZone.getTimeZone(zoneId);
		return of(date, tz);
	}

	public static QDate ofEpoch(String epochTime, QDate... defRq) {
		Integer epoch = UST.INT(epochTime, null);
		if (epoch != null && epoch > 0) {
			return ofEpoch(epoch);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Illegal epoch pattern '%s'", epochTime);
	}

	public static QDate ofEpoch(Integer epochTime) {
		Date d = new Date(epochTime * 1000L);
		return QDate.of(d);
	}

	public static QDate of(Date date, TimeZone zoneId) {
		Calendar cal = zoneId == null ? Calendar.getInstance() : Calendar.getInstance(zoneId);
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);
		int millis = cal.get(Calendar.MILLISECOND);
		QDate qdate = new QDate(year, month, day, hour, minutes, seconds, millis);
		return qdate;
	}

	public QDate add(int calendarUnit, int count) {
		if (count == 0) {
			return this;
		}
		Date d = UTime.add(toDate(), calendarUnit, count);
		return of(d);
	}

	public Integer epoch() {
		return epoch(null);
	}

	public Integer epoch(ZoneId zone) {
		Long l = toDate(zone).getTime() / 1000L;
		return l.intValue();
	}

	public static long now_ms() {
		return now().ms();
	}

	public long ms() {
		return ms(null);
	}

	public long ms(ZoneId zone) {
		return toDate(zone).getTime();
	}

	public QDate addDays(int countDays) {
		return add(Calendar.DATE, countDays);
	}

	public QDate addMonth(int countMonth) {
		return add(Calendar.MONTH, countMonth);
	}

	public QDate addYears(int countYears) {
		return add(Calendar.YEAR, countYears);
	}

	public QDate addHours(int countHours) {
		return add(Calendar.HOUR_OF_DAY, countHours);
	}

	public QDate addMinutes(int countMins) {
		return add(Calendar.MINUTE, countMins);
	}

	public QDate addSeconds(int countSecs) {
		return add(Calendar.SECOND, countSecs);
	}

	public QDate addMilliSeconds(int countMs) {
		return add(Calendar.MILLISECOND, countMs);
	}

	public boolean isAfter(QDate day) {
		return toDate().after(day.toDate());
	}

	public boolean isAfterOrEqauls(QDate day) {
		long time = toDate().getTime();
		return time >= day.ms();
	}

	public boolean isBefore(QDate day) {
		return toDate().before(day.toDate());
	}

	public boolean isBeforeOrEqauls(QDate day) {
		long time = toDate().getTime();
		return time <= day.ms();
	}

	/**
	 * *************************************************************
	 * ----------------------------  DIFF ----------------------------
	 * *************************************************************
	 */

	public Number diff(TimeUnit timeUnit) {
		return diff(now(), timeUnit);
	}

	public long diff() {
		return diff(now());
	}

	public long diff(QDate with) {
		return ms() - with.ms();
	}

	public Number diff(QDate with, TimeUnit timeUnit) {
		return QDate.toTime(timeUnit, ms() - with.ms());
	}

	//
	//

	public long diffabs() {
		return diffabs(QDate.now());
	}

	public Number diffabs(TimeUnit timeUnit) {
		return diffabs(QDate.now(), timeUnit);
	}

	public long diffabs(QDate with) {
		return Math.abs(diff(with));
	}

	public Number diffabs(QDate with, TimeUnit timeUnit) {
		return QDate.toTime(timeUnit, Math.abs(diff(with)));
	}

	public static Number toTime(TimeUnit timeUnit, long ms) {
		switch (timeUnit) {
			case NANOSECONDS:
				return ms * 1000 * 1000;
			case MICROSECONDS:
				return ms * 1000;
			case MILLISECONDS:
				return ms;
			case SECONDS:
				return ms / 1000.0;
			case MINUTES:
				return ms / 1000.0 / 60.0;
			case HOURS:
				return ms / 1000.0 / 60.0 / 60.0;
			case DAYS:
				return ms / 1000.0 / 60.0 / 60.0 / 24.0;
			default:
				throw new WhatIsTypeException(timeUnit);
		}
	}

	//
	//

	public static QDate of(long ms) {
		return of(new Date(ms));
	}

	public static QDate of(Long ms, QDate... defRq) {
		return ms != null ? of((long) ms) : ARG.toDefThrowMsg(() -> X.f("Date value is illegal '%s'", ms), defRq);
	}

	public static QDate of(String date, String[] format, QDate... defRq) {
		for (String formatTry : format) {
			QDate qDate = QDate.of(date, formatTry, null);
			if (qDate != null) {
				return qDate;
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Parse string '%s' with format's '%s'", date, ARR.of(format)), defRq);
	}

	public static QDate of(String date, String format, QDate... defRq) {
		try {
//			return of(new SimpleDateFormat(format).parse(date));
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date parsedDate = sdf.parse(date);

			// Проверяем, содержит ли формат только день и месяц (без года)
			if (!format.contains("y") && !format.contains("Y")) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(parsedDate);

				Calendar currentCal = Calendar.getInstance();
				int currentYear = currentCal.get(Calendar.YEAR);
				cal.set(Calendar.YEAR, currentYear);

				parsedDate = cal.getTime();
			}

			return of(parsedDate);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Parse string '%s' with format '%s'", date, format), defRq);
		}
	}

	public static QDate ofWithYear(String date, String format, Integer... year_month_day_hour_min_sec) {
		return of(UTime.parseDateCustom(date, format, year_month_day_hour_min_sec));
	}

	public EDay getDayName(boolean... shortName) {
		return EDay.toDayName(toDate());
	}

	public QDate addDaysAgo(int dayAgo) {
		return addDays(UNum.neg(dayAgo));
	}

	public java.sql.Date toSqlDate() {
		return new java.sql.Date(toDate().getTime());
	}

	public QDate truncate(int calendarUnit) {
		return of(DateUtils.truncate(this, calendarUnit));
//		return of(UTime7.getStartDatePeriodOf(toDate(), calendarUnit));
	}

	public enum EDay {
		SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

		// TODO
		public static void main(String[] args) {
			// String dayOfWeek = new SimpleDateFormat("EEEE",
			// Locale.ENGLISH).format(QDate.now().toDate());
			for (int i = 1; i <= 10; i++) {
				Sys.p(getDayNameCapitalize(QDate.ofMonodate8("20180422").addDays(-1 * i).toDate()));
			}
		}

		public static boolean isToday(Date date, EDay eday) {
			return eday.equals(toDayName(date));
		}

		public static EDay toDayName(Date date) {
			String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
			return valueOf(dayOfWeek.toUpperCase());
		}

		public static EDay getDayShortName(Date date) {
			String dayOfWeek = new SimpleDateFormat("EEE", Locale.ENGLISH).format(date);
			return valueOf(dayOfWeek.toUpperCase());
		}

		public static String getDayNameCapitalize(Date date) {
			return WordUtils.capitalize(toDayName(date).name().toLowerCase());
		}

		public static EDay ofIndex(int indexDay) {
			return ENUM.getEnum(indexDay, EDay.class);
		}

		public int index() {
			return ENUM.indexOf(this);
		}
	}

	public static class SqlDate {
		public final static SimpleDateFormat FORMAT_SQL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		public final static DateTimeFormatter DTF_SQL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		public static void main(String[] args) {
			Sys.exit(toQDate("2019-01-24 13:58:17"));
		}

		public static QDate toQDate(String sqlDate) {
			return QDate.of(toDate(sqlDate));
		}

		public static Date toDate(String sqlDate) {
			try {
				return FORMAT_SQL.parse(sqlDate);
			} catch (ParseException e) {
				return X.throwException(e);
			}
		}

		public static String now() {
			return now(ZoneId.systemDefault());
		}

		public static String now(ZoneId zoneId) {
			Clock clock = Clock.system(zoneId);
			LocalDateTime ldt = LocalDateTime.now(clock);
			return ldt.format(DTF_SQL);
		}

		public static String toSqlDate(QDate now) {
			return toSqlDateOrNow(now.toDate());
		}

		@Deprecated
		public static String toSqlDateOrNow(Date now) {
			if (now == null) {
				now = new Date();
			}
			return FORMAT_SQL.format(now);
		}

		public static String toSqlDate(Date now) {
			IT.notNull(now);
			return FORMAT_SQL.format(now);
		}

		public static String toSqlDate(LocalDateTime localDateTime) {
			return localDateTime.format(DTF_SQL);
		}

		public static String ofEpoch(Integer timestamp) {
			return toSqlDate(QDate.ofEpoch(timestamp));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !QDate.class.isAssignableFrom(o.getClass())) {
			return false;
		}
		QDate qDate = (QDate) o;
		return EQ.equalsUnsafe(year, qDate.year) && EQ.equalsUnsafe(day, qDate.day) && EQ.equalsUnsafe(month, qDate.month) && EQ.equalsUnsafe(hour, qDate.hour) && EQ.equalsUnsafe(minutes, qDate.minutes) && EQ.equalsUnsafe(seconds, qDate.seconds) && EQ.equalsUnsafe(millis, qDate.millis);
	}


}
