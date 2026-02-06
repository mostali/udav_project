package mpc.types.enums;

import org.apache.commons.lang3.StringUtils;

public enum EnDis {
	ENABLE, DISABLE, AUTO;

	public static EnDis valueOf(Boolean from) {
		return from == null ? AUTO : (from ? ENABLE : DISABLE);
	}

	public String nameCap() {
		return StringUtils.capitalize(name().toLowerCase());
	}
}
