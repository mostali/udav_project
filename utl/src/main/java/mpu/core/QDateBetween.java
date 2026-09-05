package mpu.core;

import mpc.exception.NI;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Stream;

public class QDateBetween {

    public static LocalDateTime[] rangeBetween_YearLDT(QDate qdate) {
        return rangeBetween(qdate.toLocalDateTime(), Calendar.YEAR);
    }

    public static QDate[] rangeBetween_YearAsQDate(QDate qdate) {
        return convertPare(rangeBetween_YearLDT(qdate));
    }

    public static LocalDateTime[] rangeBetween_MonthLDT(QDate qdate) {
        return rangeBetween(qdate.toLocalDateTime(), Calendar.MONTH);
    }

    public static QDate[] rangeBetween_MonthAsQDate(QDate qdate) {
        return convertPare(rangeBetween_MonthLDT(qdate));
    }

    public static LocalDateTime[] rangeBetween_WeekLDT(QDate qdate) {
        return rangeBetween(qdate.toLocalDateTime(), Calendar.WEEK_OF_MONTH);
    }

    public static QDate[] rangeBetween_WeekAsQDate(QDate qdate) {
        return convertPare(rangeBetween_WeekLDT(qdate));
    }

    public static LocalDateTime[] rangeBetween_SingleDayLDT(QDate qdate) {
        return rangeBetween(qdate.toLocalDateTime(), Calendar.DAY_OF_MONTH);
    }

    public static QDate[] rangeBetween_SingleDayAsQDate(QDate qdate) {
        return convertPare(rangeBetween_SingleDayLDT(qdate));
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
        LocalDateTime temporal = date instanceof QDate ? ((QDate) date).toLocalDateTime() : UTime.toLdt(date);
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
                return (T[]) Stream.of(dates).map(d -> UTime.toDateFromLdt(d, zone)).toArray(Date[]::new);
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

    public static QDate[] rangeBetween_NowAndDiffMs(long addDiffMs) {
        QDate now = QDate.now();
        if (addDiffMs == 0L) {
            return new QDate[]{now, now};
        }
        QDate bordDate = now.addMilliSeconds((int) addDiffMs);
        return addDiffMs > 0 ? new QDate[]{now, bordDate} : new QDate[]{bordDate, now};
    }

    //
    //
    private static QDate[] convertPare(LocalDateTime[] ldts) {
        return new QDate[]{QDate.of(ldts[0]), QDate.of(ldts[1])};
    }
}
