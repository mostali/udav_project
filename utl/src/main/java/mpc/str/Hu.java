package mpc.str;

import lombok.SneakyThrows;
import mpc.args.ARG;
import mpc.core.P;
import mpc.num.UDbl;
import mpc.time.UTime;
import mpv.byteunit.ByteUnit;

import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

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

	public static String NUMk(Number number) {
		return K(number, 0, "k");
	}

	public static String K(Number vw, int scale, String... sfx) {
		Double v = vw.doubleValue();
		Number n;
		if (v % 1000 == 0) {
			n = vw.longValue() / 1000;
		} else {
			n = UDbl.scale(v / 1000.0, scale, RoundingMode.DOWN);
		}
		return ARG.isDef(sfx) ? n + ARG.toDef(sfx) : n.toString();
	}

	public static String NUM(Number number) {
		String str = String.format("%,d", number);
		return str;
	}


}
