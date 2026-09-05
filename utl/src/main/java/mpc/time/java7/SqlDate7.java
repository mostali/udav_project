package mpc.time.java7;

import mpu.core.QDate;
import mpu.Sys;

import java.util.Calendar;
import java.sql.Date;

//for java7
public class SqlDate7 extends Date {

	@Deprecated //slow
	public SqlDate7() {
		this(new Date(System.currentTimeMillis()));
	}

	@Deprecated //slow
	public SqlDate7(long ms) {
		this(new Date(ms));
	}

	public SqlDate7(Date7 date7) {
		this(date7.year, date7.month, date7.day, date7.hour, date7.minut, date7.second);
	}

	public final Integer year;
	public final Integer day;
	public final Integer month;
	public final Integer hour;
	public final Integer minut;
	public final Integer second;

	@Deprecated //slow
	public SqlDate7(Date date) {
		this(Integer.parseInt(QDate.CURRENT_YEAR.format(date)), Integer.parseInt(
				QDate.CURRENT_MONTH.format(date)), Integer.parseInt(
				QDate.CURRENT_DAY.format(date)), Integer.parseInt(
				QDate.CURRENT_HOUR.format(date)), Integer.parseInt(
				QDate.CURRENT_MINUTES.format(date)), Integer.parseInt(
				QDate.CURRENT_SECONDS.format(date)));
	}

	public SqlDate7(Integer year, Integer month, Integer day, Integer hour,
					Integer minutes, Integer seconds) {
		super(toDateWithManlyMonth(year, month, day, hour,
				minutes, seconds).getTime());
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minut = minutes;
		this.second = seconds;
	}

	public SqlDate7(Calendar cal) {
		this(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
	}

	public static Date toDateWithManlyMonth(Integer year, Integer month, Integer day, Integer hour, Integer minutes, Integer seconds) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day, hour, minutes, seconds);
		return new SqlDate7(Date7.of(cal.getTime()));
	}

//	public static SqlDate7 of(long ms) {
//		return new SqlDate7(ms);
//	}

	public static SqlDate7 of(Date date) {
		return toDate7(date);
	}

//	public static SqlDate7 nowAsDate() {
//		return new Date();
//	}

	public static SqlDate7 now() {
		return toDate7(new java.util.Date());
	}

	public Calendar toCalendar() {
		return toCalendar(this);
	}

	public static Calendar toCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static SqlDate7 toDate7(java.util.Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return new SqlDate7(cal);
	}

	public SqlDate7 add(int calUnit, int amount) {
		Calendar cal = toCalendar();
		cal.add(calUnit, amount);
		return new SqlDate7(cal);
	}

	public static void main(String[] args) {
		Sys.exit(SqlDate7.now());
	}
}
