package mpf.multitask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface ExecutorWithCallback extends ExecutorService {
	void execute(Runnable var1, ThreadPoolExecutorCallback var2);

	<T> Future<T> submit(Callable<T> var1, ThreadPoolExecutorCallback var2);
}
