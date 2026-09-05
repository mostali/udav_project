package mpc.types.enums;

import org.apache.commons.lang3.StringUtils;

public enum EOnOff {
	ON, OFF, AUTO;

	public static EOnOff valueOf(Boolean from) {
		return from == null ? AUTO : (from ? ON : OFF);
	}

	public String nameCap() {
		return StringUtils.capitalize(name().toLowerCase());
	}
}
