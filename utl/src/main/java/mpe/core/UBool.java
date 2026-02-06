package mpe.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class UBool {

	public static boolean isTrue_Bool_12_YesNo_PlusMinus(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Boolean) {
			return (Boolean) obj;
		} else if (obj instanceof Number) {
			return ((Number) obj).intValue() == 1;
		} else if (obj instanceof CharSequence) {
			switch (obj.toString().toLowerCase().trim()) {
				case "true":
				case "yes":
				case "y":
				case "1":
				case "+":
					return true;
			}
		}
		return false;
	}

	public static boolean isFalse_Bool_10_YesNo_PlusMinus(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Boolean) {
			return (Boolean) obj == false;
		} else if (obj instanceof Number) {
			return ((Number) obj).intValue() == 0;
		} else if (obj instanceof CharSequence) {
			switch (obj.toString().toLowerCase().trim()) {
				case "false":
				case "no":
				case "n":
				case "0":
				case "-":
					return true;
			}
		}
		return false;
	}

	public static String isTrue_Bool_12_YesNo_PlusMinus_SWAP10(String currentProp) {
		return UBool.isTrue_Bool_12_YesNo_PlusMinus(currentProp) ? "0" : "1";
	}

	public static boolean isFalseSafe(Boolean bool) {
		return bool == null ? false : !bool;
	}

	public static boolean isTrueSafe(Boolean bool) {
		return bool == null ? false : bool;
	}

	public static boolean isTrue(Object obj) {
		return obj != null && obj instanceof Boolean && ((Boolean) obj);
	}

	public static boolean isTrueStringly(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return "true".equals(obj.toString());
	}

	public static boolean isFalseStringly(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return "false".equals(obj.toString());
	}

	public static boolean isFalse(Object obj) {
		return obj != null && obj instanceof Boolean && !((Boolean) obj);
	}

	public static Boolean int2bool(int bool) {
		return bool == 0 ? null : bool > 0;
	}

	public static int bool2int(Boolean bool) {
		return bool == null ? 0 : (bool ? 1 : -1);
	}

	public static int bool2posint(Boolean bool) {
		return bool == null ? 0 : (bool ? 1 : 2);
	}

	public static Boolean swapBoolean(AtomicBoolean bool) {
		return bool.getAndSet(!bool.get());
	}

	public static boolean isTrueAny(String s) {
		if (s == null) {
			return false;
		}
		s = s.trim();
		if (s.isEmpty()) {
			return false;
		}
		s = s.toLowerCase();
		if (s.equals("true") || s.equals("enable") || s.equals("on")
				|| s.equals("yes") || s.equals("y") || s.equals("1")) {
			return true;
		}
		return false;
	}

	public static boolean isFalseAny(String s) {
		if (s == null) {
			return false;
		}
		s = s.trim();
		if (s.isEmpty()) {
			return false;
		}
		s = s.toLowerCase();
		if (s.equals("false") || s.equals("disable") || s.equals("off")
				|| s.equals("no") || s.equals("n") || s.equals("0")) {
			return true;
		}
		return false;
	}
}
