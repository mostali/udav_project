package mpu.str;

import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import mpe.core.P;
import mpu.X;
import mpu.core.*;
import mpv.byteunit.ByteUnit;

import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//Конвертим объекты в человекочитаемый вид
//Humanly
public class Hu {


	public static void main(String[] args) {

		Number num = 5000;
		Double v = num.doubleValue();
		boolean is = v % 1000 == 0;
		Long n = is ? (Long) (num.longValue() / 1000) : (long) (double) UDbl.scale(v / 1000.0, 2, RoundingMode.DOWN);

		P.exit(n);
		P.exit(K_(5000, 2, "k"));

		P.exit(5000.00 % 1000 == 0);
		P.exit(K_(1000, 2));
		P.exit(K_(1029, 2));
	}

	public static String MB1(double bytes) {
		return MB(bytes, 1);
	}

	public static String MB(double bytes, int scale) {
		double vl = ByteUnit.BYTE.toMB(bytes);
		return UDbl.scale(vl, scale) + "Mb";
	}

	public static String MIN(long min) {
		return toStringTimeHumanlySec(TimeUnit.MINUTES.toSeconds(min));
	}

	public static String SEC(long sec) {
		return toStringTimeHumanlySec(sec);
	}

	public static String MS(Stopwatch started) {
		return MS(started.elapsed(TimeUnit.MILLISECONDS));
	}

	public static String MS(long ms) {
		return toStringTimeHumanlyMs(ms);
	}

	@SneakyThrows
	public static String KB_TB(Path file) {
		return KB_TB(Files.size(file));
	}

	public static String KB_TB(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[]{"", "kb", "mb", "gb", "tb"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + "" + units[digitGroups];
	}

	public static Integer PCT(Number num1, Number num2) {
		return UDbl.double2procInt(num1.doubleValue() / num2.doubleValue());
	}

	public static Integer PCT(Double value) {
		return UDbl.double2procInt(value);
	}

	public static String NUMk(Number number, Integer... scale) {
		return K_(number, ARG.toDefOr(0, scale), "k");
	}

	public static String NUMm(Number number, Integer... scale) {
		return M(number, ARG.toDefOr(0, scale), "M");
	}

	public static String K1(Number num) {
		return K(num, 1);
	}

	public static String K(Number num, int scale) {
		double v = num.longValue();
		if (v == 0) {
			return v + "";
		}
		String pfx = v < 0 ? "-" : "";
		if (v < 0) {
			v = -v;
		}
		if (v < 1000) {
			return pfx + num + "";
		} else if (v < 1_000_000) {
			return pfx + UDbl.scale(v / 1000, scale) + "k";
		} else {
			return pfx + UDbl.scale(v / 1_000_000, scale) + "k";
		}
	}

	@Deprecated
	public static String K_(Number num, int scale, String... sfx) {
		switch (scale) {
			case 0:
				return num.longValue() / 1000 + ARG.toDefOr("", sfx);
			default:
				return UDbl.scale(num.doubleValue() / 1000.0, scale, RoundingMode.DOWN) + ARG.toDefOr("", sfx);

		}
	}

	public static String M(Number num, int scale, String... sfx) {
		switch (scale) {
			case 0:
				return num.longValue() / 1_000_000 + ARG.toDefOr("", sfx);
			default:
				return UDbl.scale(num.doubleValue() / 1_000_000.0, scale, RoundingMode.DOWN) + ARG.toDefOr("", sfx);

		}
	}

	public static String NUM(Number number) {
		String str = String.format("%,d", number);
		return str;
	}


	public static String DATE(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	public static long MS(String huFormat) {
		return TimeMark.convertToMs(huFormat);
	}


	public static String toStringTimeHumanlyMsRuShort(long ms) {
		if (ms < QDate.SEC_MS) {
			return ms + "мс";
		} else if (ms < QDate.MIN_SEC * 1000) {
			return QDate.toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 1) + "с";
		} else if (ms < QDate.DAY_SEC * 1000) {
			return QDate.toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.MINUTES, 1) + "м";
		} else {
			return QDate.toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.HOURS, 1) + "ч";
		}
	}

	public static String toStringTimeHumanlyMs(long ms) {
		if (ms < QDate.SEC_MS) {
			return ms + "ms";
		} else if (ms < QDate.MIN_SEC * 1000) {
			return QDate.toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 1) + "s";
		} else if (ms < QDate.DAY_SEC * 1000) {
			return QDate.toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.MINUTES, 1) + "m";
		} else {
			return QDate.toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.HOURS, 1) + "h";
		}
	}

	public static String toStringTimeHumanlySec(long seconds) {
		if (seconds < QDate.MIN_SEC) {
			return seconds + " seconds";
		} else if (seconds < QDate.HOUR_SEC) {
			return QDate.toTime(seconds, TimeUnit.SECONDS, TimeUnit.MINUTES, 0) + "m";
		} else if (seconds < QDate.DAY_SEC) {
			return QDate.toTime(seconds, TimeUnit.SECONDS, TimeUnit.HOURS, 0) + "h";
		} else {
			return QDate.toTime(seconds, TimeUnit.SECONDS, TimeUnit.DAYS, 0) + "d";
		}
	}

	public static String toStringPareRangeDate(QDate[] pare, QDate.F dateFormat) {
		return pare[0].f(dateFormat) + STR.DEL + pare[1].f(dateFormat);
	}

	public static String sizeOfFile(long size) {
		if (size < 1024L) {
			return size + " B";
		}
		int exp = (int) (Math.log(size) / Math.log(1024));
		char pre = "KMGTPE".charAt(exp - 1);
		return String.format("%.1f %ciB", size / Math.pow(1024, exp), pre);
	}

	public static String posXY(Integer[] xy) {
		return xy[0] + "x" + xy[1];
	}

	public static String posXY_BR(Integer[] xy) {
		return "(" + xy[0] + "," + xy[1] + ")";
	}

	@SneakyThrows
	public static String sizeOfFile(Path file) {
		if (file == null) {
			return "-1bb";
		}
		long l = X.sizeOf(file);
		if (l <= 0) {
			return "0kb";
		} else if (l < 1000) {
			return l + "b";
		}
		Double convert = ByteUnit.MB.convert(l, ByteUnit.BYTE);
		return convert < 1.0 ? K_(l, 0) + "b" : convert.longValue() + "Mb";
	}
}
