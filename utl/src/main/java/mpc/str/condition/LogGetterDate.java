package mpc.str.condition;

import mpu.X;
import mpu.core.*;
import mpc.exception.RequiredRuntimeException;
import mpc.log.LogLine;
import mpe.ftypes.core.FDate;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class LogGetterDate extends StringCondition.IGetterDate<String> {

	public final Integer year;
	public final String format;
	public static final String FORMAT_DEFAULT = FDate.APP_SLDF_UFOS;

	public static LogGetterDate of(String format, Integer... year) {
		return new LogGetterDate(format, year);
	}

	public static LogGetterDate buildByFormat_UFOS() {
		return of(FORMAT_DEFAULT, QDate.now().year);
	}

	public static LogGetterDate buildByDefault() {
		return buildByFormat_UFOS();
	}

	public static LogGetterDate buildByFormat(String format) {
		boolean withYear = format.contains("YY");
		return withYear ? of(format) : of(format, QDate.now().year);
	}


	public LogGetterDate(String format, Integer... year) {
		this.format = format;
		this.year = ARG.toDefOrNull(year);
	}

	public static Date[] findFirstLastDateViaFullScan(Collection<String> lines, LogGetterDate logGetterDate) {
		Set<Date> collect = lines.stream().map(line -> LogLine.of(line, false)).filter(X::NN).map(ll -> ll.date()).map(ds -> logGetterDate.getDateFrom(ds, null)).filter(X::NN).collect(Collectors.toSet());
		return new Date[]{ARRi.last(collect, null), ARRi.first(collect, null)};
	}

//	public static Date findLastDateFromFiles(Collection<Path> files, LogGetterDate logGetterDate, Date... defRq) {
//		Set<Date> dates = new HashSet<>();
//		for (Path file : files) {
//			Date lastDate = findLastDate(RW.readLines(file), logGetterDate, null);
//			if (lastDate != null) {
//				dates.add(lastDate);
//			}
//		}
//		return ARRi.first(dates, defRq);
//	}

	public static Date[] findFirstLastDateFromFiles(Collection<Path> files, LogGetterDate logGetterDate, Date[]... defRq) {
		Set<Date> firstDates = new HashSet<>();
		Set<Date> endDates = new HashSet<>();
		for (Path file : files) {
			Date firstDate = findFirstDate(RW.readLines(file), logGetterDate, null);
			if (firstDate != null) {
				firstDates.add(firstDate);
			}
			Date lastDate = findLastDate(RW.readLines(file), logGetterDate, null);
			if (lastDate != null) {
				endDates.add(lastDate);
			}
		}
		if (X.notEmpty(firstDates) && X.notEmpty(endDates)) {
			return new Date[]{ARRi.first(firstDates), ARRi.last(endDates)};
		}
		return ARG.toDefThrowMsg(() -> X.f("not found first&last date from files %s", files), defRq);
	}

	public static Date[] findFirstLastDate(List<String> lines, LogGetterDate logGetterDate, Date[]... defRq) {
		Date first = findFirstDate(lines, logGetterDate, null);
		if (first != null) {
			Date last = findLastDate(lines, logGetterDate);
			return new Date[]{first, last};
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except First&Last date's "), defRq);

	}

	public static Date findFirstDate(List<String> lines, LogGetterDate logGetterDate, Date... defRq) {
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			Date dateFrom = logGetterDate.getDateFrom(line, null);
			if (dateFrom != null) {
				return dateFrom;
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("First date not found"), defRq);
	}

	public static Date findLastDate(List<String> lines, LogGetterDate logGetterDate, Date... defRq) {
		for (int i = lines.size() - 1; i >= 0; i--) {
			String line = lines.get(i);
			Date dateFrom = logGetterDate.getDateFrom(line, null);
			if (dateFrom != null) {
				return dateFrom;
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Last date not found"), defRq);
	}

	@Override
	public Date getDateFrom(String from, Date... defRq) {
		try {
			return UTime.parseDateCustom(from, format, new Integer[]{year});
//			return QDate.of() getDateFromString(from, format, year);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	@Override
	public Date toDate(Date date) {
		return UTime.updateDate(date, year);
	}

	@Override
	public String toString(Date date, String... defRq) {
		try {
			if (year != null) {
				date = UTime.updateDate(date, year);
			}
			return new SimpleDateFormat(format).format(date);
		} catch (Exception ex) {
			Date finalDate = date;
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error format date->string '%s', format '%s'", finalDate, format), defRq);
		}
	}

//	public static Date getDateFromString(String from, int year, Date... defRq) {
//		return getDateFromString(from, FDate.APP_STANDART_LOG_DATE_FORMAT, year, defRq);
//	}

//	public static Date getDateFromString(String from, Date... defRq) {
//		return getDateFromString(from, FDate.APP_STANDART_LOG_DATE_FORMAT, Date7.now().year, defRq);
//	}
//
//	public static Date getDateFromString(String from, String format, int year, Date... defRq) {
//		try {
//			return getDateFromStringImpl(from, format, year);
//		} catch (Exception ex) {
//			return ARG.toDefThrow(ex, defRq);
//		}
//	}
//
//	private static Date getDateFromStringImpl(String from, String format, int year) {
//		String first = USToken.first(from, ' ');
//		SimpleDateFormat dateInputFormat = new SimpleDateFormat(format);
//		Calendar parsedCal = Calendar.getInstance();
//		try {
//			parsedCal.setTime(dateInputFormat.parse(first));
//		} catch (ParseException e) {
//			throw new IllegalArgumentException(e);
//		}
//		parsedCal.set(Calendar.YEAR, year);
//		return parsedCal.getTime();
//	}
}
