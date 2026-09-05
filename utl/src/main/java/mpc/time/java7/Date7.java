package mpc.time.java7;

import mpu.core.QDate;
import mpu.Sys;

import java.util.Calendar;
import java.util.Date;

//Date for java7
public class Date7 extends Date {

	@Deprecated //slow?
	public Date7() {
		this(new Date());
	}

	public final Integer year;
	public final Integer day;
	public final Integer month;
	public final Integer hour;
	public final Integer minut;
	public final Integer second;

	@Deprecated //slow
	public Date7(Date date) {
		this(Integer.parseInt(QDate.CURRENT_YEAR.format(date)), Integer.parseInt(
				QDate.CURRENT_MONTH.format(date)), Integer.parseInt(
				QDate.CURRENT_DAY.format(date)), Integer.parseInt(
				QDate.CURRENT_HOUR.format(date)), Integer.parseInt(
				QDate.CURRENT_MINUTES.format(date)), Integer.parseInt(
				QDate.CURRENT_SECONDS.format(date)));
	}

	public Date7(Integer year, Integer month, Integer day, Integer hour,
				 Integer minutes, Integer seconds) {
		super(toDateWithManlyMonth(year, month, day, hour, minutes, seconds).getTime());
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minut = minutes;
		this.second = seconds;
	}

	public Date7(Calendar cal) {
		this(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
	}

	public static Date toDateWithManlyMonth(Integer year, Integer month, Integer day, Integer hour, Integer minutes, Integer seconds) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day, hour, minutes, seconds);
		return cal.getTime();
	}

	public static Date7 of(Date date) {
		return toDate7(date);
	}

	public static Date nowAsDate() {
		return new Date();
	}

	public static Date7 now() {
		return toDate7(new Date());
	}

	public Calendar toCalendar() {
		return toCalendar(this);
	}

	public static Calendar toCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static Date7 toDate7(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return new Date7(cal);
	}

	public Date7 add(int calUnit, int amount) {
		Calendar cal = toCalendar();
		cal.add(calUnit, amount);
		return new Date7(cal);
	}

	public static void main(String[] args) {
		Sys.exit(Date7.now());
	}
}
