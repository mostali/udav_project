package mpc.log;


import mpu.str.Sb;

import java.util.Map;

// copy of import org.slf4j.event.Level;
public enum Lev {
	FATAL(50, "FATAL"),
	ERROR(40, "ERROR"),
	WARN(30, "WARN"),
	INFO(20, "INFO"),
	DEBUG(10, "DEBUG"),
	TRACE(0, "TRACE");

	private final int levelInt;
	private final String levelStr;

	private Lev(int i, String s) {
		this.levelInt = i;
		this.levelStr = s;
	}

	public static String toShortFilenameString(Map<Lev, Boolean> states, boolean isNullThat) {
		Sb sb = new Sb();
		for (Map.Entry<Lev, Boolean> levEntry : states.entrySet()) {
//			sb.append(Character.toLowerCase(levEntry.getKey().name().charAt(0)) + "" + UBool.bool2int(levEntry.getValue()));
			char ch = levEntry.getKey().name().charAt(0);
			if (levEntry.getValue() == null) {
				sb.append(isNullThat ? ch : Character.toLowerCase(ch));
			} else {
				sb.append(levEntry.getValue() ? ch : Character.toLowerCase(ch));
			}
		}
		return sb.toString();
	}

	public int toInt() {
		return this.levelInt;
	}

	public String toString() {
		return this.levelStr;
	}
}
