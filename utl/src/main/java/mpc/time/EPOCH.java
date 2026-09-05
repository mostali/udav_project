package mpc.time;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARG;

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
			return (T) toDateFromEpoch(ms, zoneId);
		} else if (asType == LocalDateTime.class) {
			return (T) toLocalDateTime(ms, zoneId);
		}
		throw new WhatIsTypeException(asType);
	}

	public static LocalDateTime toLocalDateTime(int epoch, ZoneId... zoneId) {
		return LocalDateTime.ofInstant(new Date(epoch * 1000L).toInstant(), zoneId.length == 0 ? ZoneId.systemDefault() : zoneId[0]);
	}

	public static Date toDateFromEpoch(int sec, ZoneId... zoneId) {
		return Date.from(toLocalDateTime(sec, zoneId).atZone(ARG.isDef(zoneId) ? zoneId[0] : ZoneId.systemDefault()).toInstant());
	}
}
