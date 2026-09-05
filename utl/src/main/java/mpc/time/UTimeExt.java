package mpc.time;

import com.google.common.base.Stopwatch;
import mpu.Sys;
import mpu.X;
import mpe.rt.SLEEP;
import mpu.core.ARR;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//https://habrahabr.ru/post/274905/
public class UTimeExt {

//	public static void main(String[] args) throws IOException {
////		timerVoiceQuickly(1_000 * 600, 1000L);
//		timerVoiceQuickly(TimeUnit.MINUTES.toMillis(3), 1000L);
//	}

	public final static String[] ruMonths = {"января", "февраля", "марта",
			"апреля", "мая", "июня", "июля", "августа", "сентября", "октября",
			"ноября", "декабря"};

	public final static Locale localeRU = new Locale("ru");

	public final static DateFormatSymbols dfsRU = DateFormatSymbols
			.getInstance(localeRU);

	static {
		UTimeExt.dfsRU.setMonths(UTimeExt.ruMonths);
	}

	public static void timerVoiceQuickly(long msec, Long... refresh_ms) {
		long dms = msec / msec <= 1000 ? 100 : (msec <= 10_000 ? 10 : (msec <= 100_000 ? 100 : (msec <= 1_000_000 ? 1000 : 60 * 1000)));
		Long refresh_ms_ = ARR.defIfNull(dms, refresh_ms);
		Process rt = null;
		try {
			rt = Runtime.getRuntime().exec("spd-say start");
			Stopwatch sw = Stopwatch.createStarted();
			do {
				long ms_current = sw.elapsed(TimeUnit.MILLISECONDS);
				Sys.p(ms_current);
				if (ms_current >= msec) {
					break;
				}
				SLEEP.ms(refresh_ms_);
			} while (true);
			rt = Runtime.getRuntime().exec(X.f("spd-say 'proshlo %s minut'", msec));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String toLogTimeMs(long ms) {
		return ms >= 1000 ? TimeUnit.MILLISECONDS.toSeconds(ms) + "s" : ms + "ms";
	}

	public static Date toDate(LocalDateTime dateTime) {
		return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static String currentRuDay() {
		return currentRuDay(new Date());
	}

	public static String currentRuDay(Date date) {
		DateFormat df = new SimpleDateFormat("dMMMM", localeRU);
		SimpleDateFormat sdf = (SimpleDateFormat) df;
		sdf.setDateFormatSymbols(dfsRU);
		return sdf.format(date);
	}

}
