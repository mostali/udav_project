package mpc.env;

import mpc.time.EPOCH;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class AppTime {
	public static final ZoneId ZONE_MSK = ZoneId.of("Europe/Moscow");

	public static LocalDateTime now() {
		return LocalDateTime.now(ZONE_MSK);
	}

	public static LocalDateTime ldt(Integer epoch) {
		return EPOCH.epochToLocalDateTime(epoch, ZONE_MSK);
	}
}
