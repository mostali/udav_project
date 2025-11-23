package mpe.rt;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.X;
import mpu.core.ARGn;
import mpc.str.sym.SYMJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@RequiredArgsConstructor
public abstract class TryCall<T, E extends Throwable> {
	public static final Logger L = LoggerFactory.getLogger(TryCall.class);

	public static final int DEF_TC = 3;

	final String callName;
	final int _tc;

	public TryCall(String callName, int... tryCount) {
		this.callName = SYMJ.LIGHTING + callName;
		this._tc = ARGn.toDefOr(DEF_TC, tryCount);
	}

	protected abstract T callImpl() throws E;

	@SneakyThrows
	public T callSneaky() {
		return call();
	}

	@SneakyThrows
	public T call() throws E {
		int tc = _tc;
		while (true) {
			try {

				if (L.isInfoEnabled()) {
					L.info(callName + ":" + tc + "tc");
				}
				beforeCall();
				T t = callImpl();
				if (L.isInfoEnabled()) {
					logResult(t);
				}
				return t;

			} catch (Throwable ex) {

				afterCallWithException(ex, tc);

				ex = checkException(ex, tc--);

				if (ex != null) {
					return X.throwException(ex);
				}

				if (L.isWarnEnabled()) {
					String msg = callName + ":ERROR:" + tc + "tc";
					L.warn(msg, ex);
				}

			}
		}

	}

	@SneakyThrows
	protected Throwable checkException(Throwable ex, int tc) {
		if (tc < 0) {
			throw ex;
		}
		return null;
	}

	protected void afterCallWithException(Throwable ex, int tc) {
	}

	protected void beforeCall() {
	}

	private void logResult(T t) {
		String count = null;
		if (t == null) {
			count = "null";
		} else if (t instanceof Collection) {
			Collection c = (Collection) t;
			count = c.size() + "size";
		} else if (t instanceof CharSequence) {
			CharSequence c = (CharSequence) t;
			count = c.length() + "len";
		} else {
			count = t.getClass().getSimpleName() + "?";
		}
		if (L.isInfoEnabled()) {
			L.info(SYMJ.OK_GREEN + callName + ":" + count);
		}
	}

}
