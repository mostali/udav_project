package mpc.time;

import java.util.concurrent.TimeUnit;

//TimeUnit Alias
public class TU {
	public static final TimeUnit D = TimeUnit.DAYS;
	public static final TimeUnit H = TimeUnit.HOURS;
	public static final TimeUnit M = TimeUnit.MINUTES;
	public static final TimeUnit S = TimeUnit.SECONDS;
	public static final TimeUnit MLS = TimeUnit.MILLISECONDS;
	public static final TimeUnit MCS = TimeUnit.MICROSECONDS;

	public static final long SEC_MS = 1000;
	public static final long MIN_SEC = 60;
	public static final long HOUR_SEC = 3600;
	public static final long DAY_SEC = HOUR_SEC * 24;
	public static final long MONTH_SEC = DAY_SEC * 30;
}
