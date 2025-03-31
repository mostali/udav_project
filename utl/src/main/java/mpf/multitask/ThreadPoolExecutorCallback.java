package mpf.multitask;

import java.util.concurrent.Callable;

public interface ThreadPoolExecutorCallback {
	void beforeExecute(Thread var1, Callable var2);

	void afterExecute(Callable var1, Throwable var2);
}
