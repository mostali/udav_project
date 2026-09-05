package mpu.core;

import mpc.exception.RequiredRuntimeException;
import mpu.X;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class UTime {

    public static Date toDateFromLdt(LocalDateTime dateTime, ZoneId... zoneId) {
        return Date.from(dateTime.atZone(zoneId.length == 0 ? ZoneId.systemDefault() : zoneId[0]).toInstant());
    }

    public static LocalDateTime toLdt(Date dateToConvert, ZoneId... zoneId) {
        return toZdt(dateToConvert, zoneId).toLocalDateTime();
    }

    public static LocalDate toLocalDate(Date dateToConvert, ZoneId... zoneId) {
        return toZdt(dateToConvert, zoneId).toLocalDate();
    }

    public static ZonedDateTime toZdt(Date dateToConvert, ZoneId... zoneId) {
        return dateToConvert.toInstant().atZone(zoneId.length == 0 ? ZoneId.systemDefault() : zoneId[0]);
    }

    public static Date updateDate(Date date, Integer... year_month_day_hour_min_sec) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        updateDate(instance, year_month_day_hour_min_sec);
        return instance.getTime();
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
            return ARG.throwErr(() -> new RequiredRuntimeException(e, "Parse date error from value '%s' format '%s',", string, format), defRq);
        }
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


}
