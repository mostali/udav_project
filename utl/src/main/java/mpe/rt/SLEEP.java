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
		sleep(sec * 1000, message);
	}

	public static void sleep(long ms, String... message) {
		if (ARG.isDef(message)) {
			String msg = "Sleep:" + ms;
			Sys.p(X.sizeOf(message) == 0 ? msg : STR.formatAllOr(msg, message));
		}
		sleep(ms);
	}

	@SneakyThrows
	public static void sleep_(long ms) {
		Thread.sleep(ms);
	}

	public static void sleep(long ms) {
		sleep(ms, (Logger) null);
	}

	public static void sleep(long ms, Logger logger) {
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

	public static void sleepRandom500ms(int ms) {
		sleepRandom(ms, ms + 500);
	}

	public static void sleepRandom(int min, int max) {
		try {
			Thread.sleep(RANDOM.RANGE(min, max));
		} catch (InterruptedException e) {
			X.throwException(e);
		}
	}

	public static void sleepR(int ms) {
		sleepRandom(ms, ms + 500);
	}
}
