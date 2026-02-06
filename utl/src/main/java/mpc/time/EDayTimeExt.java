package mpc.time;

import mpc.exception.FIllegalArgumentException;
import mpu.core.QDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EDayTimeExt implements ITimeMode {
	//	MORNING(5, 10), DAY(11, 17), EVNING(18, 23), NIGHT(0, 4);
	M1(5, 6), M2(7, 8), M3(9, 10),
	D1(11, 12), D2(13, 14), D3(15, 16),
	E1(17, 18), E2(19, 20), E3(21, 22),
	N1(23, 0), N2(1, 2), N3(3, 4);
	public final int start, end;

	EDayTimeExt(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public static EDayTimeExt valueOf() {
		return valueOf(QDate.now().hour);
	}

	public static EDayTimeExt valueOfStartWith(String letter) {
		for (EDayTimeExt val : values()) {
			if (val.name().startsWith(letter)) {
				return val;
			}
		}
		return null;
	}

	public static EDayTimeExt valueOf(int hour) {
		for (EDayTimeExt qts : values()) {
			if (qts.isTime(hour)) {
				return qts;
			}
		}
		throw new FIllegalArgumentException("Incorrect hour value [%s]", hour);
	}

	public static List<Integer> fillRange(Integer begin, Integer end) {
		if (begin < 0 || end > 23) {
			throw new FIllegalArgumentException("Hour out of range 0-23. Begin(%s) End(%s)", begin, end);
		}
		List<Integer> hours = new ArrayList<>();
		for (Integer hour = begin; hour < 24; hour++) {
			hours.add(hour);
			if (hour == end) {
				break;
			} else if (hour == 23) {
				hour = -1;
			}
		}
		return hours;
	}

	@Override
	public boolean isTime(int hours) {
		if (start < end) {
			return start <= hours && hours <= end;
		} else {
			return start <= hours || hours <= end;
		}
	}

	public boolean isTimeOut(int hours) {
		return start <= hours && hours <= end;
	}

	@Override
	public List<ITimeMode> modeValues() {
		return Arrays.asList(values());
	}

	@Override
	public String modeName() {
		return name();
	}

}
