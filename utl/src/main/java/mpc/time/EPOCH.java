package mpc.time;

import mpc.exception.WhatIsTypeException;
import mpu.core.UTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class EPOCH {
	public static long epoch() {
		return epoch(System.currentTimeMillis());
	}

	public static long epoch(long ms) {
		return ms / 1000L;
	}

	public static Date epochToDate(int ms) {
		return new Date(ms * 1000L);
	}

	public static LocalDateTime epochToLocalDateTime(int ms, ZoneId... zoneId) {
		return epochToDate(ms, LocalDateTime.class, zoneId);
	}

	public static <T> T epochToDate(int ms, Class<T> asType, ZoneId... zoneId) {
		if (asType == Date.class) {
			return (T) UTime.toDateFromEpoch(ms, zoneId);
		} else if (asType == LocalDateTime.class) {
			return (T) UTime.toLocalDateTime(ms, zoneId);
		}
		throw new WhatIsTypeException(asType);
	}
}
