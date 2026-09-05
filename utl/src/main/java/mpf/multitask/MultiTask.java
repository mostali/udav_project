package mpf.multitask;

import com.google.common.base.Stopwatch;
import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpu.IT;
import mpc.rfl.RFL;

import java.util.*;
import java.util.concurrent.*;

public class MultiTask {

	public static final Logger L = LoggerFactory.getLogger(MultiTask.class);

	public static <T, R> List<R> runMultiThread(String executorName, Class handlerClass, Collection<T> entitys) {
		return runMultiThread(executorName, handlerClass, entitys, null);
	}

	public static <T, R> List<R> runMultiThread(String executorName, Class handlerClass, Collection<T> entitys, Map context) {
		return runMultiThread(executorName, handlerClass, null, entitys, context);
	}

	public static <T, R> List<R> runMultiThread(String executorName, FutureHandler handler, Collection<T> entitys) {
		return runMultiThread(executorName, null, handler, entitys, null);
	}

	public static <T, R> List<R> runMultiThread(String executorName, FutureHandler handler, Collection<T> entitys, Map context) {
		return runMultiThread(executorName, null, handler, entitys, context);
	}

	private static <T, R> List<R> runMultiThread(String executorName, Class<FutureHandler<T, R>> handlerClass, FutureHandler<T, R> handler, Collection<T> entitys, Map context) {

		if (!(entitys instanceof List)) {
			entitys = new ArrayList(entitys);
		}
		context = context == null ? new HashMap() : context;

		String MT_EXECUTOR_NAME = X.notEmpty(executorName) ? executorName : context.containsKey("MT_EXECUTOR_NAME") ? (String) context.get("RMC_EXECUTOR_NAME") : "MT_" + UUID.randomUUID().toString().substring(10);
		int MT_PART = context.containsKey("MT_PART") ? (Integer) context.get("MT_PART") : 10;
		int MT_KEEP_ALIVE_TIME = context.containsKey("MT_KEEP_ALIVE_TIME") ? (Integer) context.get("MT_KEEP_ALIVE_TIME") : 5;

		IT.notEmpty(entitys, "multi entitys empty");

		Stopwatch stopwatch = Stopwatch.createStarted();

		if (L.isInfoEnabled()) {
			String name = handlerClass != null ? handlerClass.getSimpleName() : handler.getClass().getSimpleName();
			L.info("Мультипоточная операция '{}', объектов '{}'", name, entitys.size());
		}

		//передаем в многопоточное выполнение Authentication,мб NPE (login must be not empty)
		//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//		context.put("auth", auth);

		double portion = Math.ceil((1.0d * entitys.size()) / MT_PART);
		int ceil = (int) Math.ceil((1.0d * entitys.size()) / portion);
		LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(ceil);
		ExecutorService executorService = MonitoredThreadPoolExecutorFactory.newPool(
				MT_EXECUTOR_NAME,
				ceil,
				MT_PART,
				MT_KEEP_ALIVE_TIME,
				TimeUnit.MINUTES,
				taskQueue,
				null,
				new ThreadPoolExecutor.CallerRunsPolicy()
		);

		List<Future<R>> futures = new ArrayList<>();
		for (int i = 0; i < ceil; i++) {
			int count = Math.min((i + 1) * (int) portion, entitys.size());
			List<T> edDocPortion = ((List<T>) entitys).subList(i * (int) portion, count);
			Future<R> future = executorService.submit(new FutureTask(handlerClass, handler, edDocPortion, context));
			futures.add(future);
		}

		List result = new ArrayList();
		// Получаем результаты выполнения
		for (Future<R> future : futures) {
			try {
				result.add(future.get());
			} catch (Exception e) {
				L.error(e.getMessage(), e);
			}
		}

		executorService.shutdown();
		stopwatch.stop();

		if (L.isInfoEnabled()) {
			String name = handlerClass != null ? handlerClass.getSimpleName() : handler.getClass().getSimpleName();
			L.info("Мультипоточная операция '{}' завершена за {}mm", name, stopwatch.elapsed(TimeUnit.MINUTES));
		}
		return result;
	}

	public interface FutureHandler<T, R> {
		R handle(List<T> entitys, Map context);
	}

	public static class FutureTask<T, R> implements Callable<R> {
		private final List<T> entytys;
		private final Map context;
		private final Class<FutureHandler<T, R>> handlerClass;
		private FutureHandler<T, R> handler;

		public FutureTask(FutureHandler<T, R> handler, List<T> entyties, Map context) {
			this(null, handler, entyties, context);
		}

		public FutureTask(Class<FutureHandler<T, R>> handlerClass, List<T> entyties, Map context) {
			this(handlerClass, null, entyties, context);
		}

		private FutureTask(Class<FutureHandler<T, R>> handlerClass, FutureHandler handler, List<T> entyties, Map context) {
			this.entytys = entyties;
			this.context = context;
			this.handlerClass = handlerClass;
			this.handler = handler;
		}

		@Override
		public R call() {
			if (handler == null) {
				handler = RFL.instEmptyConstructor(handlerClass);
			}
			return handler.handle(entytys, context);
		}
	}
}
