package mpu.str;

import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import mpe.core.P;
import mpu.core.ARG;
import mpu.core.TimeMark;
import mpu.core.UDbl;
import mpu.core.UTime;
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
		P.exit(K(5000, 2, "k"));

		P.exit(5000.00 % 1000 == 0);
		P.exit(K(1000, 2));
		P.exit(K(1029, 2));
	}

	public static String MB1(double bytes) {
		return MB(bytes, 1);
	}

	public static String MB(double bytes, int scale) {
		double vl = ByteUnit.BYTE.toMB(bytes);
		return UDbl.scale(vl, scale) + "Mb";
	}

	public static String SEC(long sec) {
		return UTime.toStringTimeHumanlySec(sec);
	}

	public static String MS(Stopwatch started) {
		return MS(started.elapsed(TimeUnit.MILLISECONDS));
	}

	public static String MS(long ms) {
		return UTime.toStringTimeHumanlyMs(ms);
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
		return K(number, ARG.toDefOr(0, scale), "k");
	}

	public static String NUMm(Number number, Integer... scale) {
		return M(number, ARG.toDefOr(0, scale), "M");
	}

	public static String K(Number num, int scale, String... sfx) {
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


}
