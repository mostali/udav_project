package mpc.num;

import com.google.common.base.Stopwatch;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpe.core.P;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import mpc.str.ObjTo;
import mpu.core.UTime;
import mpu.str.STR;
import mpu.str.TKN;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UNum {

	public static void main(String[] args) {
		List<BigDecimal> bigDecimals =
				Arrays.asList(new BigDecimal("100.01"),
						new BigDecimal("100.44"),
						new BigDecimal("100.50"),
						new BigDecimal("100.75"));
		for (final BigDecimal bd : bigDecimals) {
			System.out.println(bd.setScale(0, RoundingMode.CEILING));
		}
		X.exit();


		P.p(toStringBigNumber("123"));
		P.p(toStringBigNumber("123.0"));
		P.p(toStringBigNumber("123."));
		P.p(toStringBigNumber("-123.12"));
		P.p(toStringBigNumber("-4123.13"));
		P.p(toStringBigNumber("-25123.13"));
		P.p(toStringBigNumber("-125123.13"));
		P.p(toStringBigNumber("-7127123.13"));
	}

	public static String toStringBigNumber(BigDecimal bigNum) {
		if (true) {
			return new DecimalFormat("# ###,00").format(bigNum);
		}
		return toStringBigNumber(bigNum.toString());
	}

	public static String toStringBigNumber(String bigNumStr) {

		Character point = bigNumStr.indexOf(".") == -1 ? null : '.';
		point = point != null ? point : (bigNumStr.indexOf(",") == -1 ? null : ',');

		bigNumStr = bigNumStr.replace(",", ".");

		String first = TKN.first(bigNumStr, ".", bigNumStr);
		String second = TKN.last(bigNumStr, ".", "");

		char[] chars = first.toCharArray();
		StringBuilder sb = new StringBuilder();
		String three = "";
		for (int i = chars.length - 1; i >= 0; i--) {
			if (three.length() == 3) {
				sb.append(three).append(" ");
				three = "";
			}
			three += ((Character) chars[i]);
		}
		if (three.length() > 0) {
			sb.append(three);
		}
		String str = StringUtils.reverse(sb.toString()) + (point == null ? "" : point + second);
		if (str.length() > 1 && !Character.isDigit(str.charAt(0)) && Character.isWhitespace(str.charAt(1))) {
			str = new StringBuilder(str).deleteCharAt(1).toString();
		}
		return str.trim();
	}

	public static String KBhu(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[]{"", "kb", "mb", "gb", "tb"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + "" + units[digitGroups];
	}

	public static Integer PCT(Number num1, Number num2) {
		return double2procInt(num1.doubleValue() / num2.doubleValue());
	}

	public static Integer PCT(Double value) {
		return double2procInt(value);
	}

	public static Integer double2procInt(Double value) {
		value = value * 100;
		return value.intValue();
	}

//	public static String TIMEhu(Stopwatch timer) {
//		return MShu(timer.elapsed(TimeUnit.MILLISECONDS));
//	}

//	public static String MShu(Stopwatch timer) {
//		return TIMEhu(timer);
//	}

//	public static String MShu(long ms) {
//		return UTime.toStringTimeHumanlyMs(ms);
//	}

	public static void add(int[] part, int[]... parts) {
		if (parts.length == 0) {
			return;
		}
		for (int[] part0 : parts) {
			IT.state(part.length == part0.length);
			for (int i = 0; i < part.length; i++) {
				part[i] += part0[i];
			}
		}
	}

	public static boolean hasMinMax(double x, Double[] mnx, boolean... requiredInMinMaxPreiod) {
		if (X.empty(mnx)) {
			return ARG.isDefEqTrue(requiredInMinMaxPreiod) ? false : true;
		}
		boolean rslt = x >= mnx[0] && (mnx.length == 1 || x <= mnx[1]);
		return rslt;
	}

	public static <N extends Number> N round10(N num) {
		return round(num, 10);
	}

	public static <N extends Number> N round100(N num) {
		return round(num, 100);
	}

	public static <N extends Number> N round(N num, int step) {
		if (num == null || num.intValue() == 0) {
			return num;
		}
		long l = Math.round(num.doubleValue() / step) * step;
		return (N) ObjTo.objTo(l, num.getClass());
	}

	public static int neg(int num) {
		return num < 0 ? num : (num == 0 ? num : -num);
	}

	public static long neg(long num) {
		return num < 0 ? num : (num == 0 ? num : -num);
	}

	public static double neg(double num) {
		return num < 0 ? num : (num == 0 ? num : -num);
	}

	public static int pos(int num) {
		return Math.abs(num);
	}

	public static long pos(long num) {
		return Math.abs(num);
	}

	public static long N2Z(Long val) {
		return val == null ? 0 : val;
	}

	public static int N2Z(Integer val) {
		return val == null ? 0 : val;
	}

	public static Integer defIfNull(Integer n, Integer def) {
		return n == null ? def : n;
	}

	public static Integer defIfNullNegZero(Integer n, Integer def) {
		return n == null || n <= 0 ? def : n;
	}

	public static boolean isPos(Integer integer) {
		return integer != null && integer > 0;
	}

	public static boolean isPosOrZero(Integer integer) {
		return integer != null && integer >= 0;
	}

	public static boolean isPos(Long integer) {
		return integer != null && integer > 0;
	}

	public static boolean isPosOrZero(Long integer) {
		return integer != null && integer >= 0;
	}

	public static int maxGE(int i, int max) {
		return i >= max ? max : i;
	}

	public static int maxGT(int i, int max) {
		return i > max ? max : i;
	}

	public static int minLE(int i, int min) {
		return i <= min ? min : i;
	}

	public static int minLT(int i, int min) {
		return i < min ? min : i;
	}

	public static boolean isSeries(int status, int... i) {
		int num = Integer.parseInt((status + "").charAt(0) + "");
		for (int i0 : i) {
			if (i0 == num) {
				return true;
			}
		}
		return false;
	}

	public static <T> T toNumber(Number obj, Class<T> clazz, T... defRq) {
		if (clazz == Integer.class) {
			return obj instanceof Integer ? (T) obj : (T) (Integer) obj.intValue();
		} else if (clazz == Long.class) {
			return obj instanceof Long ? (T) obj : (T) (Long) obj.longValue();
		} else if (clazz == Double.class) {
			return obj instanceof Double ? (T) obj : (T) (Double) obj.doubleValue();
		} else if (clazz == Float.class) {
			return obj instanceof Float ? (T) obj : (T) (Float) obj.floatValue();
		} else if (clazz == Short.class) {
			return obj instanceof Short ? (T) obj : (T) (Short) obj.shortValue();
		} else if (clazz == Byte.class) {
			return obj instanceof Byte ? (T) obj : (T) (Byte) obj.byteValue();
		} else if (clazz == BigDecimal.class) {
			return obj instanceof BigDecimal ? (T) obj : (T) BigDecimal.valueOf(obj.doubleValue());
		} else if (clazz == BigInteger.class) {
			return obj instanceof BigInteger ? (T) obj : (T) BigInteger.valueOf(obj.longValue());
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Wrong Number Value [" + obj + "] for type [" + clazz + "]"), defRq);

	}

	public static <T> T toNumberDefaultValue(Class<T> clazz) {
		if (clazz == Integer.class) {
			return (T) (Integer) 0;
		} else if (clazz == Long.class) {
			return (T) (Long) 0L;
		} else if (clazz == Double.class) {
			return (T) (Double) 0.0;
		} else if (clazz == Float.class) {
			return (T) (Double) 0.0;
		} else if (clazz == Short.class) {
			Short zero = 0;
			return (T) zero;
		} else if (clazz == Byte.class) {
			Byte zero = 0;
			return (T) zero;
		} else if (clazz == BigDecimal.class) {
			return (T) BigDecimal.valueOf(0);
		} else if (clazz == BigInteger.class) {
			return (T) BigInteger.valueOf(0);
		}
		throw new WhatIsTypeException("What is number type? " + clazz);
	}

	public static String withPfx(Long num, String pfx, int len) {
		int curLen = num.toString().length();
		if (curLen >= len) {
			return pfx + "" + num;
		}
		return STR.repeat(pfx, len - curLen) + num;
	}
}
