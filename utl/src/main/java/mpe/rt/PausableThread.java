package mpe.rt;

import mpc.exception.FIllegalStateException;
import mpc.exception.WrongLogicRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.str.STR;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//https://stackoverflow.com/questions/16758346/how-pause-and-then-resume-a-thread
public class PausableThread extends Thread {

	public static final Logger L = LoggerFactory.getLogger(PausableThread.class);

	public enum State {
		_NULL, INIT, RUN, WAIT, EXIT, _ERROR;
	}

	private volatile int duration_ms_after_work = 0;

	public PausableThread duration_ms_after_work(int duration) {
		this.duration_ms_after_work = duration;
		return this;
	}

	private volatile State _CURRENT_STATE = State.INIT;

	private final Object pauseLock = new Object();

	public PausableThread(String name, int duration_ms_after_work, State state) {
		super(name);
		this.duration_ms_after_work = duration_ms_after_work;
		if (state != State.INIT) {
			applyState(state);
		}
	}

	@Override
	public synchronized void start() {
		throw new IllegalStateException("Run via state method");
	}

	protected void doWork() {
		if (L.isDebugEnabled()) {
			L.debug("DO_WORK:" + QDate.now());
		}
	}

	public State state() {
		return _CURRENT_STATE;
	}

	public void state_run() {
		applyState(PausableThread.State.RUN);
	}

	public void state_wait() {
		applyState(PausableThread.State.WAIT);
	}

	public void state_stop() {
		applyState(State.EXIT);
	}

	public PausableThread applyState(State action) {
		boolean needStart = false;
		ok:
		switch (_CURRENT_STATE) {
			case _ERROR:
				throw new FIllegalStateException(cause, "Thread stopped with error");
			case EXIT:
				throw new FIllegalStateException("Thread already stopped:" + this._CURRENT_STATE);
			case INIT:
				switch (action) {
					case RUN:
					case WAIT:
						needStart = true;
					case EXIT:
						break ok;
					default:
						throw ILLEGAL_INPUT_STATE(action);
				}
			case RUN:
			case WAIT:
				switch (action) {
					case WAIT:
						break ok;
					case EXIT:
					case RUN:
						synchronized (pauseLock) {
							pauseLock.notifyAll();
						}
						break ok;
					default:
						throw ILLEGAL_INPUT_STATE(action);
				}
			default:
				throw new WhatIsTypeException(_CURRENT_STATE);
		}
		_CURRENT_STATE = action;
		if (needStart) {
			super.start();
		}
		return this;
	}

	private FIllegalStateException ILLEGAL_INPUT_STATE(State action) {
		throw new FIllegalStateException("Illegal input state %s. Current-state %s", action, _CURRENT_STATE);
	}

	private Throwable cause;

	@Override
	public void run() {
		next_or_exit:
		while (true) {
			String rnd = STR.randAlpha(3) + "-";
			if (L.isTraceEnabled()) {
				L.trace(rnd + "NextCycle:" + _CURRENT_STATE + ":" + QDate.now());
			}
			switch (_CURRENT_STATE) {
				case _ERROR:
				case EXIT:
					break next_or_exit;
				case INIT:
					throw new WrongLogicRuntimeException(_CURRENT_STATE);
				case WAIT: {
					if (L.isDebugEnabled()) {
						L.debug(rnd + "WAIT-START");
					}
					synchronized (pauseLock) {
						try {
							pauseLock.wait();
							if (L.isDebugEnabled()) {
								L.debug(rnd + "WAIT-END");
							}
							continue next_or_exit;
						} catch (InterruptedException ex) {
							cause = ex;
							_CURRENT_STATE = State._ERROR;
							break next_or_exit;
						}
					}
				}
				case RUN:
					try {
						doWork();
						if (duration_ms_after_work > 0) {
							SLEEP.ms(duration_ms_after_work);
						}
					} catch (Throwable ex) {
						cause = ex;
						_CURRENT_STATE = State._ERROR;
						break next_or_exit;
					}
					continue next_or_exit;
				default:
					throw new WhatIsTypeException(_CURRENT_STATE);
			}

		}
		if (L.isWarnEnabled()) {
			String msg = "Bye:" + _CURRENT_STATE;
			if (cause == null) {
				L.warn(msg);
			} else {
				L.warn(msg, cause);
			}
		}
	}

	@Override
	public String toString() {
		return super.toString() + "@" + state();
	}

}
