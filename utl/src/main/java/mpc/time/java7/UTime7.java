package mpc.time.java7;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//for java7
public class UTime7 {

//	public static void main(String[] args) throws ParseException {
//		String STANDART_LOG_FORMAT = "MM-dd;HH:mm:ss.SSS";
//		SimpleDateFormat dateInputFormat = new SimpleDateFormat(STANDART_LOG_FORMAT);
//		String data = "07-15;17:41:21.955";
//		Calendar parsedCal = Calendar.getInstance();
//		parsedCal.setTime(dateInputFormat.parse(data));
//		parsedCal.set(Calendar.YEAR, 2021);
//		U.exit(parsedCal.getTime());
//		//		U.exit(getStartEndDatePeriodOf(LocalDateTime.now(), Calendar.MONTH));
//		//		U.exit(new Date7(new Date()).add(Calendar.MONTH, -1));
//		//		U.exit(getStartEndDatePeriodOf(Date7.now(), Calendar.DAY_OF_MONTH));
//		//		U.exit(getStartEndDatePeriodOf(Date7.now(), Calendar.MONTH));
//		//		U.exit(getStartEndDatePeriodOf(Date7.now(), Calendar.YEAR));
//
//	}

	public static Date[] getStartEndDatePeriodOf(Date time, int calendarUnit) {
		return new Date[]{getStartDatePeriodOf(time, calendarUnit), getEndDatePeriodOf(time, calendarUnit)};
	}

	//see getStartEndDatePeriodOf
	public static Date[] getStartEndDatePeriodOfOLD(Date time, int calendarUnit) {
		Date d1 = null;
		Date d2 = null;
		switch (calendarUnit) {
			case Calendar.MINUTE: {
				Date7 date7 = Date7.of(time);
				//				d1 = new Date7(date7.year, date7.month, date7.day, date7.hour, date7.minut, 0);
				//				d2 = new Date7(date7.year, date7.month, date7.day, date7.hour, date7.minut, 59);
				break;
			}
			case Calendar.HOUR: {
				Date7 date7 = Date7.of(time);
				d1 = new Date7(date7.year, date7.month, date7.day, date7.hour, 0, 0);
				d2 = new Date7(date7.year, date7.month, date7.day, date7.hour, 59, 59);
				break;
			}
			case Calendar.DAY_OF_MONTH: {
				Date7 date7 = Date7.of(time);
				d1 = new Date7(date7.year, date7.month, date7.day, 0, 0, 0);
				d2 = new Date7(date7.year, date7.month, date7.day, 23, 59, 59);
				break;
			}
			case Calendar.MONTH: {
				Date7 date7 = Date7.of(time);
				d1 = new Date7(date7.year, date7.month, 1, 0, 0, 0);
				d2 = new Date7(date7.year, date7.month, date7.toCalendar().getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
				break;
			}
			case Calendar.YEAR: {
				Date7 date7 = Date7.of(time);
				d1 = new Date7(date7.year, 0, 1, 0, 0, 0);
				d2 = new Date7(date7.year, 11, 31, 23, 59, 59);
				break;
			}
			default:
				throw new IllegalArgumentException("Not implemented calendar unit UTime7:" + calendarUnit);
		}

		return new Date[]{d1, d2};
	}

	public static Date getStartDatePeriodOf(Date time, int calendarUnit) {
		Date d1 = null;
//		Date d2 = null;
		switch (calendarUnit) {
			case Calendar.MINUTE: {
				Date7 date7 = Date7.of(time);
				d1 = new Date7(date7.year, date7.month, date7.day, date7.hour, date7.minut, 0);
//				d2 = new Date7(date7.year, date7.month, date7.day, date7.hour, date7.minut, 59);
				break;
			}
			case Calendar.HOUR: {
				Date7 date7 = Date7.of(time);
				d1 = new Date7(date7.year, date7.month, date7.day, date7.hour, 0, 0);
//				d2 = new Date7(date7.year, date7.month, date7.day, date7.hour, 59, 59);
				break;
			}
			case Calendar.DAY_OF_MONTH: {
				Date7 date7 = Date7.of(time);
				d1 = new Date7(date7.year, date7.month, date7.day, 0, 0, 0);
//				d2 = new Date7(date7.year, date7.month, date7.day, 23, 59, 59);
				break;
			}
			case Calendar.MONTH: {
				Date7 date7 = Date7.of(time);
				d1 = new Date7(date7.year, date7.month, 1, 0, 0, 0);
//				d2 = new Date7(date7.year, date7.month, date7.toCalendar().getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
				break;
			}
			case Calendar.YEAR: {
				Date7 date7 = Date7.of(time);
				d1 = new Date7(date7.year, 0, 1, 0, 0, 0);
//				d2 = new Date7(date7.year, 11, 31, 23, 59, 59);
				break;
			}
			default:
				throw new IllegalArgumentException("Not implemented calendar unit UTime7:" + calendarUnit);
		}
		return d1;
	}

	public static Date getEndDatePeriodOf(Date time, int calendarUnit) {
//		Date d1 = null;
		Date d2 = null;
		switch (calendarUnit) {
			case Calendar.MINUTE: {
				Date7 date7 = Date7.of(time);
//				d1 = new Date7(date7.year, date7.month, date7.day, date7.hour, date7.minut, 0);
				d2 = new Date7(date7.year, date7.month, date7.day, date7.hour, date7.minut, 59);
				break;
			}
			case Calendar.HOUR: {
				Date7 date7 = Date7.of(time);
//				d1 = new Date7(date7.year, date7.month, date7.day, date7.hour, 0, 0);
				d2 = new Date7(date7.year, date7.month, date7.day, date7.hour, 59, 59);
				break;
			}
			case Calendar.DAY_OF_MONTH: {
				Date7 date7 = Date7.of(time);
//				d1 = new Date7(date7.year, date7.month, date7.day, 0, 0, 0);
				d2 = new Date7(date7.year, date7.month, date7.day, 23, 59, 59);
				break;
			}
			case Calendar.MONTH: {
				Date7 date7 = Date7.of(time);
//				d1 = new Date7(date7.year, date7.month, 1, 0, 0, 0);
				d2 = new Date7(date7.year, date7.month, date7.toCalendar().getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
				break;
			}
			case Calendar.YEAR: {
				Date7 date7 = Date7.of(time);
//				d1 = new Date7(date7.year, 0, 1, 0, 0, 0);
				d2 = new Date7(date7.year, 11, 31, 23, 59, 59);
				break;
			}
			default:
				throw new IllegalArgumentException("Not implemented calendar unit UTime7:" + calendarUnit);
		}
		return d2;
	}

	public static Date toDateBySDF(String str, String format) {
		return toDateRq(str, new SimpleDateFormat(format));
	}

	public static Date toDateRq(String str, SimpleDateFormat simpleDateFormat) {
		try {
			return toDate(str, simpleDateFormat);
		} catch (ParseException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public static Date toDate(String str, SimpleDateFormat simpleDateFormat) throws ParseException {
		return simpleDateFormat.parse(str);
	}

}
