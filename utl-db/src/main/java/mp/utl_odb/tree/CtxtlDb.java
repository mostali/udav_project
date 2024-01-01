package mp.utl_odb.tree;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mp.utl_odb.query_core.QP;
import mpc.X;
import mpc.args.ARG;
import mpc.types.pare.Pare4;
import mpz_deprecated.EER;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.str.UST;
import mpc.time.QDate;
import mpc.time.UTimeExt;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CtxtlDb extends CtxtDb {

	public static final Logger L = LoggerFactory.getLogger(CtxtlDb.class);

	public CtxtlDb(Class clas) {
		super(clas);
	}

	public CtxtlDb(Class clas, String key) {
		super(clas, key);
	}

	public CtxtlDb(String key) {
		super(key);
	}

	public CtxtlDb(String parentDir, String key) {
		super(parentDir, key);
	}

	public CtxtlDb(String rootParentDir, String parentDir, String key, boolean isFileOrName) {
		super(rootParentDir, parentDir, key, isFileOrName);
	}

	public CtxtlDb(Path path) {
		super(path);
	}

	public String getTA(String key, TimeAccess... timeAccess) throws UtreeDelayException {
		return getTA(key, -1, timeAccess);
	}

	public String getTC(String key, long lifetimeValueMs, String... defRq) throws UtreeDelayException {
		CtxTimeModel val = getTC_MODEL(key, lifetimeValueMs);
		if (val == null) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("Key '%s' not found", key);
		}
		return val.getValue();
	}

	public <T> T getTC(String key, long delayCacheMs, Class<T> type, T... defRq) throws UtreeDelayException {
		Exception err = null;
		try {
			String vl = getTC(key, delayCacheMs);
			if (vl != null) {
				return UST.strTo(vl, type);
			}
		} catch (Exception ex) {
			err = ex;
		}

		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		} else if (err == null) {
			throw new RequiredRuntimeException("Value by key '%s' not found", key);
		}
		return X.throwException(err);
	}

	@RequiredArgsConstructor
	public static class Key {
		final String val;

		public static Key of(String value) {
			return new Key(value);
		}

		public CtxFieldType type() {
			return CtxFieldType.typeOf(this);
		}

		public String colName() {
			return type().name();
		}

		public QP pEQ() {
			return QP.pEQ(colName(), val);
		}

		public static class Val extends Key {
			public Val(String val) {
				super(val);
			}

			public static Val of(String value) {
				return new Val(value);
			}
		}

		public static class Time extends Key {
			public Time(String val) {
				super(val);
			}

			public static Time of(String value) {
				return new Time(value);
			}
		}

		public static class Ext extends Key {
			public Ext(String val) {
				super(val);
			}

			public static Ext of(String value) {
				return new Ext(value);
			}
		}
	}

	public CtxTimeModel getTC_MODEL(String key, long lifetimeValueMs, TimeAccess... timeAccess) throws UtreeDelayException {
		return getTC_MODEL(Key.of(key), lifetimeValueMs, timeAccess);
	}

	public CtxTimeModel getTC_MODEL(Key key, long lifetimeValueMs, TimeAccess... timeAccess) throws UtreeDelayException {
		return getTC_MODEL(key, lifetimeValueMs, false, timeAccess);
	}

	public CtxTimeModel getTC_MODEL(Key key, long lifetimeValueMs, boolean lastModel, TimeAccess... timeAccess) throws UtreeDelayException {
		CtxTimeModel mod = super.getCtxTimeModel(key, lastModel);
		if (mod == null) {
			return null;
		}
		if (lifetimeValueMs != -1) {
			QDate dateUdpated = mod.getTimeAsQDate();
			if (System.currentTimeMillis() - dateUdpated.ms() <= lifetimeValueMs) {
				return mod;
			} else {
				throw new UtreeDelayException(mod, ETA.CACHE_MS, -1, lifetimeValueMs);
			}
		}
		QDate now = QDate.now();
		if (timeAccess != null) {
			for (TimeAccess access : timeAccess) {
				if (!access.isAllowedAccess(mod, now)) {
					throw new UtreeDelayException(mod, access);
				}
			}
		}

		updateTime(mod);
		return mod;
	}

	public static void check_TC(CtxTimeModel mod, long delayLockMs) throws UtreeDelayException {
		if (delayLockMs != -1) {
			QDate dateUdpated = mod.getTimeAsQDate();
			if (System.currentTimeMillis() - dateUdpated.ms() < delayLockMs) {
				throw new UtreeDelayException(mod, ETA.DELAY_MS, delayLockMs, -1);
			}
		}
	}

	public static void check_TA(CtxTimeModel mod, TimeAccess... timeAccess) throws UtreeDelayException {
		if (timeAccess != null) {
			QDate now = QDate.now();
			for (TimeAccess access : timeAccess) {
				if (!access.isAllowedAccess(mod, now)) {
					throw new UtreeDelayException(mod, access);
				}
			}
		}
	}

	public String getTA(String key, long delayLockMs, TimeAccess... timeAccess) throws UtreeDelayException {
		CtxTimeModel ta = getTA(key, null, delayLockMs, timeAccess);
		if (ta == null) {
			return null;
		}
		return ta.getValue();
	}

	public static final Semaphore LOCK = new Semaphore(1);

	@SneakyThrows
	public Pare4<Boolean, Long, Long, CtxTimeModel> getTLSSync(String actionCacheKey, long limit_sec, boolean regAction) {
		try {
			LOCK.acquire();
			return getTLS(actionCacheKey, limit_sec, regAction);
		} finally {
			LOCK.release();
		}
	}

	/**
	 * @return Long[] { ALLOWED, WAIT_MS , NEXT_ACTION_DATE_MS, TIME_MODEL }
	 */

	public Pare4<Boolean, Long, Long, CtxTimeModel> getTLS(String actionCacheKey, long limit_sec, boolean regAction, Pare4<Boolean, Long, Long, CtxTimeModel>... defRq) {
		long delayLockMs = TimeUnit.SECONDS.toMillis(limit_sec);
		try {
			String putValueIfNewOrNothing = regAction ? "" : null;
			CtxtDb.CtxTimeModel tm = this.getTA(actionCacheKey, putValueIfNewOrNothing, delayLockMs);
			if (tm != null) {
				return Pare4.of(true, -1l, tm.getTime() + delayLockMs, tm);
			} else {
				return ARG.toDefThrow(new RequiredRuntimeException("Key '%s' not found", actionCacheKey), defRq);
			}
		} catch (CtxtlDb.UtreeDelayException e) {
			return Pare4.of(false, CtxtDb.CtxTimeModel.getWaitingToNextActionMs(e.timeModel, delayLockMs), e.timeModel.getTime(), e.timeModel);
		}
	}

	public CtxTimeModel getTA(String key, String updateValue_or_nothing, long delayLockMs, TimeAccess... timeAccess) throws UtreeDelayException {
		CtxTimeModel mod = super.getCtxTimeModelByKey(key);
		boolean needUpdate = updateValue_or_nothing != null;
		if (mod == null) {
			if (needUpdate) {
				return put(key, updateValue_or_nothing);
			}
			return null;
		}

		check_TC(mod, delayLockMs);

		check_TA(mod, timeAccess);

		updateTime(mod);

		if (needUpdate) {
			saveModelAsUpdate(mod);
		}

		return mod;
	}

	public static class UtreeDelayException extends Exception {

		private static final long serialVersionUID = 1L;
		public final CtxTimeModel timeModel;
		final long delayLock;
		final long cacheLock;
		final TimeAccess timeAccess;

		public long getWaitingToNextActionSec(long limitSec) {
			return limitSec - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeModel.getTime());
		}

		public long getWaitingToNextActionMs(long limitMs) {
			return limitMs - (System.currentTimeMillis() - timeModel.getTime());
		}

		public long getLastUpdateMs() {
			return timeModel.getTime();
		}

		public ETA getTypeDelay() {
			if (cacheLock != -1) {
				return ETA.CACHE_MS;
			} else if (delayLock != -1) {
				return ETA.DELAY_MS;
			} else if (timeAccess != null) {
				return ETA.valueOf(timeAccess.tsType);
			}
			throw new WhatIsTypeException(ReflectionToStringBuilder.toString(this));
		}

		public UtreeDelayException(CtxTimeModel mod, ETA type, long delayLock, long cacheLock) {
			this(toErrorMessage(mod, type, delayLock, cacheLock, null), mod, delayLock, cacheLock, null);
		}

		public UtreeDelayException(CtxTimeModel mod, TimeAccess timeAccess) {
			this(timeAccess.toString(), mod, -1, -1, timeAccess);
		}

		private UtreeDelayException(String updateDateErrorMessage, CtxTimeModel mod, long delayLock, long cacheLock, TimeAccess timeAccess) {
			super(updateDateErrorMessage);
			this.timeModel = mod;
			this.delayLock = delayLock;
			this.cacheLock = cacheLock;
			this.timeAccess = timeAccess;
		}

		private static String toErrorMessage(CtxTimeModel mod, ETA type, long delayLockMs, long cacheLock, TimeAccess timeAccess) {
			switch (type) {
				case ALLOWED_HOUR:
				case EXCEPT_HOUR:
				case ALLOWED_DAYWEEK:
				case EXCEPT_DAYWEEK:
					return timeAccess.toString();
				case DELAY_MS:
				case CACHE_MS:
					QDate date = mod.getTimeAsQDate();
					return X.f(type + ":" + UTimeExt.toLogTimeMs(delayLockMs) + "; date:" + date + "; diffabs=" + UTimeExt.toLogTimeMs(date.diffabs()));
				default:
					throw new UnsupportedOperationException("What is ? " + type);
			}
		}

	}

	public enum ETA {
		ALLOWED_HOUR, EXCEPT_HOUR, ALLOWED_DAYWEEK, EXCEPT_DAYWEEK, DELAY_MS, CACHE_MS;

		public boolean isType(TimeAccess access) {
			return this == ETA.valueOf(access.tsType);
		}

		public TimeAccess paramHoursOrDays(int... values) {
			switch (this) {
				case ALLOWED_HOUR:
					return new TimeAccess(values, null, null, null, null);
				case EXCEPT_HOUR:
					return new TimeAccess(null, values, null, null, null);
				case ALLOWED_DAYWEEK:
					return new TimeAccess(null, null, values, null, null);
				case EXCEPT_DAYWEEK:
					return new TimeAccess(null, null, null, values, null);
				default:
					throw EER.IS("Method not use with TAT:[" + name() + "]");
			}
		}

		public TimeAccess paramDelay(long delayMs) {
			switch (this) {
				case DELAY_MS:
					return new TimeAccess(null, null, null, null, new DelayAccess(delayMs));
				default:
					throw EER.IS("Method not use with TAT:[" + name() + "]");

			}
		}
	}

	public static class DelayAccess {
		public final long delayMs;

		public DelayAccess(long delayMs) {
			this.delayMs = delayMs;
		}

		public boolean isAllowedAccess(CtxTimeModel mod) {
			return delayMs < 0 ? true : mod.getTimeAsQDate().diff(QDate.now()) > delayMs;
		}
	}

	public static class TimeAccess {
		public final int[] values;
		public final String tsType;

		public final DelayAccess accessDelay;

		@Override
		public String toString() {
			switch (ETA.valueOf(tsType)) {
				case DELAY_MS:
					return tsType + ":" + accessDelay;
				default:
					return tsType + ":" + Arrays.stream(values).boxed().map(String::valueOf).collect(Collectors.joining(":"));

			}

		}

		private TimeAccess(int[] allowedHours, int[] exceptHours, int[] allowedDayOfWeek, int[] exceptDayOfWeek, DelayAccess delayAccess) {
			if (delayAccess != null) {
				this.values = null;
				this.accessDelay = delayAccess;
				this.tsType = ETA.DELAY_MS.name();
			} else if (delayAccess != null) {
				this.values = null;
				this.accessDelay = delayAccess;
				this.tsType = ETA.DELAY_MS.name();
			} else {
				this.accessDelay = null;

				if (allowedHours != null) {
					this.values = allowedHours;
					this.tsType = ETA.ALLOWED_HOUR.name();
				} else if (exceptHours != null) {
					this.values = exceptHours;
					this.tsType = ETA.EXCEPT_HOUR.name();
				} else if (allowedDayOfWeek != null) {
					this.values = allowedDayOfWeek;
					this.tsType = ETA.ALLOWED_DAYWEEK.name();
				} else if (exceptDayOfWeek != null) {
					this.values = exceptDayOfWeek;
					this.tsType = ETA.EXCEPT_DAYWEEK.name();
				} else {
					throw EER.IS("Invalid TimeAccess Param");
				}
			}

		}

		public boolean isAllowedAccess(CtxTimeModel mod, QDate currentDate) {

			switch (ETA.valueOf(tsType)) {

				case ALLOWED_HOUR:
					for (int v : values) {
						if (currentDate.hour == v) {
							return true;
						}
					}
					return false;

				case EXCEPT_HOUR:
					for (int v : values) {
						if (currentDate.hour == v) {
							return false;
						}
					}
					return true;

				case ALLOWED_DAYWEEK:
					for (int v : values) {
						if (currentDate.getDayName() == QDate.EDay.ofIndex(v)) {
							return true;
						}
					}
					return false;

				case EXCEPT_DAYWEEK:
					for (int v : values) {
						if (currentDate.getDayName() == QDate.EDay.ofIndex(v)) {
							return false;
						}
					}
					return true;

				case DELAY_MS:
					return accessDelay.isAllowedAccess(mod);

				default:
					throw EER.NEEDIMPL();

			}
		}

	}

}
