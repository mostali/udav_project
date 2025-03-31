package mpc.map;

import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.QDate;
import mpu.core.TimeMark;
import mpu.str.UST;

import java.nio.file.Path;

public interface IGetterAs {

	<T> T getAs(String key, Class<T> asType, T... defRq);

	default QDate getAsQDate(String key, String[] formats, QDate... defRq) {
		for (String format : formats) {
			QDate getAsQDate = getAsQDate(key, format, null);
			if (getAsQDate != null) {
				return getAsQDate;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Date not found by key '%s' and formats %s", key, ARR.of(formats)), defRq);
	}

	default QDate getAsQDate(String key, String format, QDate... defRq) {
		String datePattern = getAsString(key, null);
		if (datePattern != null) {
			return UST.QDATE(datePattern, format, defRq);
		}
		return ARG.toDefThrowMsg(() -> X.f("Value not found by key '%s'", key), defRq);
	}

	default Long getAsTimeMarkMs(String key, Long... defRq) {
		String firstAsString = getAsString(key, null);
		if (firstAsString != null) {
			return TimeMark.convertToMs(firstAsString, defRq);
		}
		return ARG.toDefThrowMsg(() -> X.f("TimeMark not found by key '%s'", key), defRq);
	}

	default String getAsString(String key, String... defRq) {
		return getAs(key, String.class, defRq);
	}

	default Boolean getAsBoolean(String key, Boolean... defRq) {
		return getAs(key, Boolean.class, defRq);
	}

	default Integer getAsInt(String key, Integer... defRq) {
		return getAs(key, Integer.class, defRq);
	}

	default Long getAsLong(String key, Long... defRq) {
		return getAs(key, Long.class, defRq);
	}

	default Path getAsPath(String key, Path... defRq) {
		return getAs(key, Path.class, defRq);
	}
}
