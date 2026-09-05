package mpc.time;

import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.UST;

import java.util.concurrent.TimeUnit;

public enum QDelayType {
    YEAR, MONTH, DAY, HOUR, MIN, SEC, MS, MC, NANO;

    public static Pare<QDelayType, Long> valueOfAsPare(String pattern, Pare<QDelayType, Long>... defRq) {
        QDelayType qDelayType = QDelayType.valueOfStr(pattern, null);
        if (qDelayType == null) {
            String finalPattern = pattern;
            return ARG.throwMsg(() -> X.f("Except valid qd pattern [%s]", finalPattern), defRq);
        }
        boolean minus = pattern.startsWith("-") ? true : false;
        if (minus) {
            pattern = pattern.substring(1);
        }
        return Pare.of(qDelayType, (minus ? -1 : 1) * UST.LONG(pattern.substring(1)));
    }

    public static QDelayType valueOfStr(String pattern, QDelayType... defRq) {

        if (pattern == null || pattern.isEmpty()) {
            return ARG.throwMsg(() -> "Format string must not be empty", defRq);
        }

        // Выделение числовой части (поддержка отрицательных чисел)
        int i = 0;
        if (pattern.charAt(0) == '-') {
            i = 1;
        }

        while (i < pattern.length() && Character.isDigit(pattern.charAt(i))) {
            i++;
        }

        if (i == 0 || (i == 1 && pattern.charAt(0) == '-') || i == pattern.length()) {
            return ARG.throwMsg(() -> "Invalid format sequence: " + pattern, defRq);
        }

        String unitChar = pattern.substring(i);

        switch (unitChar) {
            case "n":
            case "N":
                return NANO;
            case "c":
            case "C":
                return MC;
            case "S":
                return MS;
            case "s":
                return SEC;
            case "m":
                return MIN;
            case "h":
            case "H":
                return HOUR;
            case "d":
            case "D":
                return DAY;
            case "M":
                return MONTH;
            case "y":
            case "Y":
                return YEAR;
            default:
                return ARG.throwMsg(() -> "Invalid unit char in pattern '" + pattern + "'", defRq);

        }

    }

    public static void main(String[] args) {

        X.exit(valueOfStr("-1h"));
        X.exit(toLong("-1h", TimeUnit.MILLISECONDS));
    }

    /**
     * Преобразует строковый формат в числовое значение в указанных единицах измерения.
     *
     * @param pattern Строка вида "2h", "-1s", "5M" и т.д.
     * @param tu      Целевая единица измерения (TimeUnit)
     * @return Значение в единицах tu
     */
    public static Long toLong(String pattern, TimeUnit tu, Long... defRq) {
        if (pattern == null || pattern.isEmpty()) {
            return ARG.throwMsg(() -> "Format string must not be empty", defRq);
        }

        // Выделение числовой части (поддержка отрицательных чисел)
        int i = 0;
        if (pattern.charAt(0) == '-') {
            i = 1;
        }

        while (i < pattern.length() && Character.isDigit(pattern.charAt(i))) {
            i++;
        }

        if (i == 0 || (i == 1 && pattern.charAt(0) == '-') || i == pattern.length()) {
            return ARG.throwMsg(() -> "Invalid format sequence: " + pattern, defRq);
        }

        String unitChar = pattern.substring(i);

        long value = Long.parseLong(pattern.substring(0, i));

        long secondsInMonth = 30L * 24 * 60 * 60;
        long secondsInYear = 365L * 24 * 60 * 60;

        switch (unitChar) {
            case "n":
            case "N":
                return tu.convert(value, TimeUnit.NANOSECONDS);
            case "c":
            case "C":
                return tu.convert(value, TimeUnit.MICROSECONDS);
            case "S":
                return tu.convert(value, TimeUnit.MILLISECONDS);
            case "s":
                return tu.convert(value, TimeUnit.SECONDS);
            case "m":
                return tu.convert(value, TimeUnit.MINUTES);
            case "h":
            case "H":
                return tu.convert(value, TimeUnit.HOURS);
            case "d":
            case "D":
                return tu.convert(value, TimeUnit.DAYS);
            case "M":
                return tu.convert(value * secondsInMonth, TimeUnit.SECONDS);
            case "y":
            case "Y":
                return tu.convert(value * secondsInYear, TimeUnit.SECONDS);
            default:
                return ARG.throwMsg(() -> "Invalid unit char in pattern '" + pattern + "'", defRq);

        }
    }

    public Long getDelay(long val, TimeUnit timeUnit) {
        switch (this) {
            case MONTH:
                throw new UnsupportedOperationException(this + "");
            default:
                return toTimeUnit().convert(val, timeUnit);
//                throw new WhatIsTypeException(this);
        }
    }

    public TimeUnit toTimeUnit() {
        switch (this) {
            case NANO:
                return TimeUnit.NANOSECONDS;
            case MC:
                return TimeUnit.MICROSECONDS;
            case MS:
                return TimeUnit.MILLISECONDS;
            case SEC:
                return TimeUnit.SECONDS;
            case MIN:
                return TimeUnit.MINUTES;
            case HOUR:
                return TimeUnit.HOURS;
            case DAY:
                return TimeUnit.DAYS;
            case YEAR:
            case MONTH:
                throw new UnsupportedOperationException(this + "");
            default:
                throw new WhatIsTypeException(this);

        }
    }
}