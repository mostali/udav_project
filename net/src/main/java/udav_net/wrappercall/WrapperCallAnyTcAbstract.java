package udav_net.wrappercall;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import mpu.core.ARG;
import mpe.core.P;
import mpe.rt.SLEEP;
import mpc.exception.RecallRuntimeException;
import org.slf4j.Logger;
import mpc.log.LoggerToSystemOut;

import java.util.ArrayList;
import java.util.List;

public abstract class WrapperCallAnyTcAbstract<R, E extends Exception> {

	public static final int DEF_TC = 3;

	@Getter
	private Logger logger = new LoggerToSystemOut();

	public WrapperCallAnyTcAbstract<R, E> setLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

	public final int tcMax;

	private int totalTc = 0;

	public int getTotalTcNext(boolean... noDecrement) {
		return ARG.isDefEqFalse(noDecrement) ? totalTc : ++totalTc;
	}

	public int getTotalTc(boolean... doDecrement) {
		return ARG.isDefEqTrue(doDecrement) ? ++totalTc : totalTc;
	}

	public final String callName;

	public WrapperCallAnyTcAbstract() {
		this(null, DEF_TC);
	}

	public WrapperCallAnyTcAbstract(String callName, int tc) {
		this.tcMax = tc == -1 ? DEF_TC : tc;
		this.totalTc = tcMax;
		this.callName = callName == null ? getClass().getSimpleName() : callName;
	}

	public abstract R callImpl() throws E;

	@SneakyThrows
	public R call() {
		return call_();
	}

	public R call_() throws E {
		next:
		while (true) {
			try {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("DoCall::{}::Current={}", callName, getTotalTc());
				}
				return callImpl();
			} catch (RecallRuntimeException ex) {
				if (getLogger().isErrorEnabled()) {
					getLogger().error("Happens RecallRuntimeException:{}", ex.getMessage());
				}
				List<RecallStrategy> strategys = getRecallStrategies();
				for (RecallStrategy recallStrategy : strategys) {
					if (recallStrategy.isRecallException(ex)) {
						int tcNext = getTotalTc(true);
						if (tcNext <= tcMax) {
							recallStrategy.doSleep(tcNext, tcMax);
							break;
						}
					}
				}
				throw ex;
			}
		}
	}

	@Getter
	private List<RecallStrategy> recallStrategies = new ArrayList<>();

	{
		recallStrategies.add(new DefaultRecallStrategy());
	}


	protected void doSleep() {
		int sleep = calcNextSleep(-1, calcFactor(totalTc));
		getLogger().error("CallTc:{}/{}:wait:{}ms", tcMax, totalTc, sleep);
		SLEEP.ms(sleep);
	}

	protected boolean isRecallException(Throwable context) {
		return context instanceof RecallRuntimeException;
	}

	public static class DefaultRecallStrategy extends RecallStrategy {

		@Override
		public boolean isRecallException(Throwable error) {
			return error instanceof RecallRuntimeException;
		}

		@Override
		public void doSleep(int currentTC, int totalTC) {
			getSleepStrategy().doSleep(currentTC, totalTC);
		}
	}

	public static abstract class RecallStrategy {
		@Getter
		@Setter
		private SleepStrategy sleepStrategy = new DefaultSleepStrategy();

		public abstract boolean isRecallException(Throwable error);

		public abstract void doSleep(int currentTC, int totalTC);
	}

	public static abstract class SleepStrategy {
		public abstract void doSleep(int currentTC, int totalTC);
	}

	public static class DefaultSleepStrategy extends SleepStrategy {
		private static int defSleepPeriod = 10000;

		@Override
		public void doSleep(int currentTC, int totalTC) {
			P.p(getClass().getSimpleName() + "/doSleep/%s/%s(%s)", defSleepPeriod, currentTC, totalTC);
			SLEEP.ms(defSleepPeriod);
		}
	}


	public static double calcFactor(int totalTc) {
		return totalTc <= DEF_TC ? 3.14 : (totalTc < 5 ? 2.5 : (totalTc < 15 ? 1.3 : 1));
	}

	public static int calcNextSleep(int sleep, double factor) {
		return sleep == -1 ? (int) 1000L : ((int) (sleep * factor));
	}
}
