package mpe.rt;

import lombok.SneakyThrows;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.str.RANDOM;
import mpu.str.STR;
import org.slf4j.Logger;

public class SLEEP {
	public static void sec(long sec, String... message) {
		ms(sec * 1000, message);
	}

	public static void ms(long ms, String... message) {
		if (ARG.isDef(message)) {
			String msg = "Sleep:" + ms;
			Sys.p(X.sizeOf(message) == 0 ? msg : STR.formatAllOr(msg, message));
		}
		ms(ms);
	}

	@SneakyThrows
	public static void ms0(long ms) {
		Thread.sleep(ms);
	}

	public static void ms(long ms) {
		ms(ms, (Logger) null);
	}

	public static void ms(long ms, Logger logger) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			if (logger == null) {
				e.printStackTrace();
			} else if (logger.isWarnEnabled()) {
				logger.warn(Thread.currentThread().getName() + " Interrupt sleep", e);
			}
		}
	}

	public static void msRange(int minMs, int maxMs) {
		try {
			Thread.sleep(RANDOM.range(minMs, maxMs));
		} catch (InterruptedException e) {
			X.throwException(e);
		}
	}

	public static void msRandPlus500(int ms) {
		msRange(ms, ms + 500);
	}
}
