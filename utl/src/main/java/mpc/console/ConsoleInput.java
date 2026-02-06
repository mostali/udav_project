package mpc.console;

import mpu.Sys;
import mpu.core.ARG;
import mpc.str.sym.SYMJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsoleInput {


	//	public static void main(String[] args) throws InterruptedException {
	//		//		U.p("wait::" + waitInput(30000, 2, 5000));
	//		//		waitInfinity(null, 30000, 5000, Integer.MAX_VALUE, Integer.MAX_VALUE);
	//		//		ConsoleInput.waitInfinityAsync(TEST_INPUT_TASK, 3_000, 3_000, 1, 1);
	//
	//		//		U.p(ConsoleInput.waitInputSync(3900, 3900, 1));
	//		P.p(ConsoleInput.waitInputOrStartSync(3900));
	//
	//		P.p("R:s" + SIMPLE_TRM);
	//		//		ConsoleInput.waitInfinity(null);
	//
	//		//		waitInfinitySimple(null, Integer.MAX_VALUE);
	//		//		waitInfinityBlock(TEST_INPUT_TASK, Integer.MAX_VALUE);
	//
	//	}


	public static final Logger L = LoggerFactory.getLogger(ConsoleInput.class);
	public static final int DEF_CHECK_INPUT_EVERY_MILLIS = 500;
	private final AtomicInteger tries;
	private final long timeout;
	private final TimeUnit unit;

	public interface ConsoleInputTask<T> {
		void doConsoleTask(String cmd);
	}

	private static final ConsoleInput.ConsoleInputTask SIMPLE_TRM = new SimpleTrm();

	public static class SimpleTrm implements ConsoleInput.ConsoleInputTask {
		private static Long CTR = 0L;

		public static String INPUT_CMD(String cmd) {
			return SYMJ.ARROW_RIGHT + " " + ++CTR + ":" + cmd;
		}

		public static String OUTPUT_CMD(String cmd) {
			return SYMJ.ARROW_LEFT + " " + CTR + ":" + cmd;
		}

		public static String INPUT_CMD(Object usr, String cmd) {
			return SYMJ.ARROW_RIGHT + " " + ++CTR + ":" + usr.getClass().getSimpleName() + ":" + cmd;
		}

		public static String OUTPUT_CMD(Object usr, String cmd) {
			return SYMJ.ARROW_LEFT + " " + ++CTR + ":" + usr.getClass().getSimpleName() + ":" + cmd;
		}

		@Override
		public void doConsoleTask(String cmd) {
			try {
				Sys.p(INPUT_CMD(cmd));
				doConsoleTaskImpl(cmd);
			} catch (Throwable err) {
				err.printStackTrace(System.out);
			} finally {
				Sys.p(OUTPUT_CMD(cmd));
			}
		}

		public void doConsoleTaskImpl(String cmd) {
			Sys.p(cmd);
		}
	}

	public static void waitInfinityBlock(ConsoleInputTask consoleInputTask, int tryInfinityLoop) {

		if (consoleInputTask == null) {
			consoleInputTask = new ConsoleInputTask() {
				@Override
				public void doConsoleTask(String cmd) {
					p("doConsoleTask::" + cmd);
				}
			};
		}
		while (tryInfinityLoop-- > 0) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String input;
			printHead(-1);
			try {
				// wait until we have data to complete a readLine()
				while (!br.ready()) {
					Thread.sleep(DEF_CHECK_INPUT_EVERY_MILLIS);
				}
				input = br.readLine();
				consoleInputTask.doConsoleTask(input);
			} catch (InterruptedException | IOException e) {
				System.out.println("e:" + e.getMessage());
			}
		}

	}

	private static boolean MAIN_TRM_RUNNED = false;

	public static void waitInfinityAsyncMain(ConsoleInputTask... consoleInputTask) {
		if (MAIN_TRM_RUNNED) {
			return;
		}
		ConsoleInputTask trm = ARG.toDefOr(SIMPLE_TRM, consoleInputTask);
		ConsoleInput.waitInfinityAsync(trm);
		MAIN_TRM_RUNNED = true;
	}

	public static void waitInfinityAsync(ConsoleInputTask consoleInputTask) {
		waitInfinityAsync(consoleInputTask, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public static void waitInfinityAsync(final ConsoleInputTask consoleInputTask, final int waitTotalMs, final int waitOneCommandMs, final int triesGetFutureTimeout, final int tryInfinityLoop) {
		final ConsoleInputTask consoleInputTask_;
		if (consoleInputTask == null) {
			consoleInputTask_ = new ConsoleInputTask() {
				@Override
				public void doConsoleTask(String cmd) {
					p("doConsoleTask::" + cmd);
				}
			};
		} else {
			consoleInputTask_ = consoleInputTask;
		}

		new Thread(ConsoleInput.class.getSimpleName() + "-Thread") {
			@Override
			public void run() {
				int tryInfinityLoop_ = tryInfinityLoop;
				while (tryInfinityLoop_-- > 0) {
					String cmd = waitInputSync(waitTotalMs, waitOneCommandMs, triesGetFutureTimeout);
					consoleInputTask_.doConsoleTask(cmd);
				}
			}
		}.start();
	}

	public static String waitInputOrStartSync(int waitTotalMs, String... headMessage) {
		if (ARG.isDefNotEmpty(headMessage)) {
			Sys.p(headMessage[0]);
		}
		return waitInputSync(waitTotalMs, waitTotalMs, 1);
	}

	public static String waitInputSync(int waitTotalMs, int waitSingleCommandMs, int tries) {

		ConsoleInput con = tries > 0 ? new ConsoleInput(tries, waitSingleCommandMs) : new ConsoleInput(waitSingleCommandMs);

		ThreadWaiter waiter = null;
		try {
			waiter = waitInputAsync(con, waitTotalMs);
			String inputString = con.getInputString();
			return inputString;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (waiter != null && !waiter.stop) {
				waiter.stop = true;
			}
		}
		// System.out.println("Done. Your input was: " + input);
	}

	private static ThreadWaiter waitInputAsync(final ConsoleInput con, final int waitMs) {
		ThreadWaiter waiter = new ThreadWaiter(waitMs) {
			public void run() {
				//P.p("Start:" + waitMs + ":" + Thread.currentThread().getName());
				super.doSleep();
				con.quit();
				//P.p("End:" + Thread.currentThread().getName());
			}
		};
		waiter.start();
		return waiter;
	}

	private static class ThreadWaiter extends Thread {
		public boolean stop = false;
		public int waitMs;
		public static long ctr = 0;

		public ThreadWaiter(final int waitMs) {
			super("WaiterInputAsync-" + (++ctr));
			this.waitMs = waitMs;
		}

		public void doSleep() {
			while (waitMs > 0) {
				try {
					Thread.sleep(1000);
					if (stop) {
						break;
					}
					waitMs -= 1000;
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					L.error("Sleep error", ex);
				}
			}
		}
	}

	public void quit() {
		tries.set(0);
	}

	public ConsoleInput() {
		this(1, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

	public ConsoleInput(long timeoutMs) {
		this(1, timeoutMs, TimeUnit.MILLISECONDS);
	}

	public ConsoleInput(int tries, long timeoutMs) {
		this(tries, timeoutMs, TimeUnit.MILLISECONDS);
	}

	public ConsoleInput(int tries, long timeout, TimeUnit unit) {
		this.tries = new AtomicInteger(tries);
		this.timeout = timeout;
		this.unit = unit;
	}

	public String getInputString() throws InterruptedException {
		ExecutorService ex = Executors.newSingleThreadExecutor();
		String input = null;
		try {
			// start working
			do {
				// next loop
				Future<String> result = ex.submit(new ConsoleInputReadTask(tries));
				try {
					input = result.get(timeout, unit);
					break;
				} catch (ExecutionException e) {
					e.getCause().printStackTrace();
				} catch (TimeoutException e) {
					//Cancelling reading task
					result.cancel(true);
					//Thread cancelled. Input is null
				}
			} while (tries.getAndDecrement() > 0);

		} finally {
			ex.shutdownNow();
		}
		return input;
	}

	public class ConsoleInputReadTask implements Callable<String> {
		final AtomicInteger tries;

		public ConsoleInputReadTask(AtomicInteger tries) {
			this.tries = tries;
		}

		public String call() throws IOException {

			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			//ConsoleInputReadTask run()
			String input;
			do {
				printHead(tries.get());
				try {
					// wait until we have data to complete a readLine()
					while (!br.ready()) {
						Thread.sleep(DEF_CHECK_INPUT_EVERY_MILLIS);
					}
					input = br.readLine();
				} catch (InterruptedException e) {
					p("...");
					return null;
				}
			} while ("".equals(input));
			return input;
		}

	}

	private static void printHead(int tries) {
		//System.out.println((tries > 0 ? tries + ":::" : ":::") + "WAIT INPUT:::");
		System.out.println("::::::::::::::::::::::::::::::::::::TERMINAL::::::::::::::::::::::::::::::::::::");
	}

	protected static void p(Object m) {
		System.out.println(m);
	}

}
