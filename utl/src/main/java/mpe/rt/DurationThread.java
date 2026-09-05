//package mpe.rt;
//
//import mpu.core.ARG;
//import mpu.IT;
//import mpc.exception.TimeoutRuntimeException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public abstract class DurationThread<R> extends ObjThread<R> {
//
//	public static final Logger L = LoggerFactory.getLogger(DurationThread.class);
//
//	final long every_ms, init_ms, max_ms;
//
//	private Long started_ms, expired_ms;
//
//	protected int ctr, max_ctr = 0;
//
//	private volatile boolean stop = false;
//
//	private boolean throw_timeout = false;
//
//	private boolean logic_garant_timeout = true;
//
//	protected abstract R doWork() throws Throwable;
//
//	public DurationThread<R> garant_timeout(boolean logic_garant_timeout) {
//		this.logic_garant_timeout = logic_garant_timeout;
//		return this;
//	}
//
//	public DurationThread<R> throw_timeout(boolean throw_timeout) {
//		this.throw_timeout = throw_timeout;
//		return this;
//	}
//
//	public DurationThread(String name, long every_ms, boolean state_start) {
//		this(name, every_ms, -1, -1, state_start);
//	}
//
//	public DurationThread(String name, long every_ms, long init_ms, long max_ms, boolean state_start) {
//		super(name);
//		this.every_ms = IT.isPosNotZero(every_ms);
//		this.init_ms = init_ms;
//		this.max_ms = max_ms;
//		if (state_start) {
//			state_start();
//		}
//	}
//
//	public synchronized void state_stop() {
//		state_stop(true);
//	}
//
//	public synchronized void state_stop(boolean throw_interrupt) {
//		stop = true;
//		//if (getState() == Thread.State.TIMED_WAITING) {
//		if (ARG.isDefEqTrue(throw_interrupt)) {
//			super.interrupt();
//		}
//		//}
//	}
//
//	public synchronized void state_start() {
//		IT.isNull(started_ms);
//		started_ms = System.currentTimeMillis();
//		super.start();
//		this.expired_ms = max_ms > 0 ? started_ms + max_ms : -1;
//	}
//
//	@Override
//	public synchronized void start() {
//		throw new UnsupportedOperationException("use state_start");
//	}
//
//	@Override
//	public void run() {
//
//		if (init_ms > 0) {
//			SLEEP.sleep_(init_ms);
//		}
//
//		stop:
//		while (true) {
//
//			ctr++;
//
//			try {
//
//				if (hasStopState()) {
//					break stop;
//				}
//
//				{ // WORK
//					R r = doWork();
//					set_result_object(r);
//				}
//
//				if (hasStopState()) {
//					break stop;
//				}
//
//				SLEEP.sleep_(every_ms);
//
//			} catch (Throwable t) {
//				set_result_error(t);
//				break stop;
//			}
//		}
//	}
//
//	private boolean hasStopState() throws TimeoutRuntimeException {
//		if (stop || ctr == max_ctr) {
//			return true;
//		}
//		if (this.expired_ms <= 0) {
//			return false;
//		}
//		boolean hasTimeout = hasTimeoutByExpired(logic_garant_timeout, this.expired_ms, every_ms);
//		if (hasTimeout && throw_timeout) {
//			throw new TimeoutRuntimeException();
//		}
//		return hasTimeout;
//	}
//
//	public static boolean hasTimeoutByExpired(boolean logic_garant_timeout, long expired_ms, long every_ms) {
//		return !hasTimeByExpired(logic_garant_timeout, expired_ms, every_ms);
//	}
//
//	public static boolean hasTimeByExpired(boolean logic_garant_timeout, long expired_ms, long every_ms) {
//		long now_ms = System.currentTimeMillis();
//		if (logic_garant_timeout) {
//			return expired_ms + every_ms > now_ms;
//		}
//		return expired_ms > now_ms;
//	}
//
//}
