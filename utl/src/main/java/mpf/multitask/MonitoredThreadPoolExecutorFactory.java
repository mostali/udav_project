package mpf.multitask;

import mpc.exception.NI;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class MonitoredThreadPoolExecutorFactory {
	protected static final Logger log = LoggerFactory.getLogger(MonitoredThreadPoolExecutorFactory.class);
	public static final String MONITORING_GROUP = "ThreadPools";
	private static boolean monitoringEnabled = false;
	@Nullable
	private static final ExecutorService FJP;
	private static List<WeakReference<ExecutorService>> trackedPools = Collections.synchronizedList(new LinkedList());

	public MonitoredThreadPoolExecutorFactory() {
	}

	@Nullable
	private static ExecutorService createFJP() {
		log.info("Creating ForkJoinPool...");
		throw new NI();
	}

	public static void deleteFJP() {
		if (FJP != null) {
			try {
				ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName("sufd:type=ForkJoinPool"));
			} catch (Exception var1) {
				log.error("Ошибка удаления ForkJoinPool-а", var1);
			}
		}

	}

	public static boolean isFJPEnabled() {
		return FJP != null;
	}

	public static ExecutorService getFJP() {
		if (FJP != null) {
			return FJP;
		} else {
			throw new IllegalStateException("Fork join pool is disabled");
		}
	}

	public static ExecutorWithCallback newPool(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, @Nullable ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
		if (monitoringEnabled) {
			MonitoredThreadPoolExecutorFactory.MonitoredThreadPoolExecutor poolExecutor = new MonitoredThreadPoolExecutorFactory.MonitoredThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, (ThreadFactory) (threadFactory != null ? threadFactory : new CustomizableThreadFactory(name)), rejectedExecutionHandler);

			try {
				ManagementFactory.getPlatformMBeanServer().registerMBean(poolExecutor, new ObjectName("sufd:type=ThreadPool" + name));
			} catch (Exception var11) {
			}

			poolExecutor.registerMetrics(name);
			trackedPools.add(new WeakReference(poolExecutor));
			return poolExecutor;
		} else {
			ExecutorWithCallback poolExecutor = new MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new CustomizableThreadFactory(name), rejectedExecutionHandler);
			trackedPools.add(new WeakReference(poolExecutor));
			return poolExecutor;
		}
	}

	public static void setMonitoringEnabled(boolean monitoringEnabled) {
		MonitoredThreadPoolExecutorFactory.monitoringEnabled = monitoringEnabled;
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Iterator i$ = MonitoredThreadPoolExecutorFactory.trackedPools.iterator();

				while (i$.hasNext()) {
					WeakReference<ExecutorService> poolRef = (WeakReference) i$.next();
					ExecutorService pool = (ExecutorService) poolRef.get();
					if (pool != null) {
						pool.shutdownNow();
					}
				}

				if (MonitoredThreadPoolExecutorFactory.FJP != null) {
					MonitoredThreadPoolExecutorFactory.FJP.shutdownNow();
				}

			}
		});
		if ("true".equals(System.getProperty("disableFJP", "true"))) {
			FJP = null;
		} else {
			FJP = createFJP();
		}

	}

	private static class FutureTaskWrapper extends FutureTask {
		private final Callable callable;

		FutureTaskWrapper(Callable callable) {
			super(callable);
			this.callable = callable;
		}
	}

	public static class MonitoredThreadPoolExecutor extends MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback implements RejectedExecutionHandler {
		private volatile long submitedTasksCount = 0L;
		private volatile long rejectedTasksCount = 0L;
		private volatile int maxOverflow = 0;
		private volatile int currentOverflow = 0;
		private RejectedExecutionHandler rejectionHandler;

		public long getSubmitedTasksCount() {
			return this.submitedTasksCount;
		}

		public long getRejectedTasksCount() {
			return this.rejectedTasksCount;
		}

		public int getMaxOverflow() {
			return this.maxOverflow;
		}

		public double getMaxUtilization() {
			return (double) this.getLargestPoolSize() / (double) this.getMaximumPoolSize();
		}

		public int getMaximumPoolSize() {
			return super.getMaximumPoolSize();
		}

		public int getPoolSize() {
			return super.getPoolSize();
		}

		public int getActiveCount() {
			return super.getActiveCount();
		}

		public int getLargestPoolSize() {
			return super.getLargestPoolSize();
		}

		public int getQueueSize() {
			return super.getQueue().size();
		}

		public MonitoredThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

		public MonitoredThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		}

		public MonitoredThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		}

		public MonitoredThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		}

		public void execute(Runnable command) {
			super.execute(command);
			++this.submitedTasksCount;
		}

		public void execute(Runnable runnable, ThreadPoolExecutorCallback callback) {
			super.execute(runnable, callback);
			++this.submitedTasksCount;
		}

		public <T> Future<T> submit(Callable<T> task, ThreadPoolExecutorCallback callback) {
			Future<T> future = super.submit(task, callback);
			++this.submitedTasksCount;
			return future;
		}

		public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
			super.setRejectedExecutionHandler(this);
			this.rejectionHandler = handler;
		}

		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			this.currentOverflow = 0;
		}

		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			if (++this.currentOverflow > this.maxOverflow) {
				this.maxOverflow = this.currentOverflow;
			}

			++this.rejectedTasksCount;
			if (this.rejectionHandler != null) {
				this.rejectionHandler.rejectedExecution(r, executor);
			}

		}

		public void registerMetrics(String name) {
			throw new NI();
//			MetricRegistry.createMetric("ThreadPools", name, "queueSize", new AbstractOneValueMetric() {
//				public Number getValue() {
//					return (double)MonitoredThreadPoolExecutor.this.getQueueSize();
//				}
//			});
//			MetricRegistry.createMetric("ThreadPools", name, "queueRemainigCapacity", new AbstractOneValueMetric() {
//				public Number getValue() {
//					return (double)MonitoredThreadPoolExecutor.this.getQueue().remainingCapacity();
//				}
//			});
//			MetricRegistry.createMetric("ThreadPools", name, "activeThreads", new AbstractOneValueMetric() {
//				public Number getValue() {
//					return (double)MonitoredThreadPoolExecutor.this.getActiveCount();
//				}
//			});
//			MetricRegistry.createMetric("ThreadPools", name, "maxThreads", new AbstractOneValueMetric() {
//				public Number getValue() {
//					return (double)MonitoredThreadPoolExecutor.this.getMaximumPoolSize();
//				}
//			});
//			MetricRegistry.createMetric("ThreadPools", name, "rejections", new AbstractOneValueMetric() {
//				public Number getValue() {
//					return (double)MonitoredThreadPoolExecutor.this.getRejectedTasksCount();
//				}
//			});
//			MetricRegistry.createMetric("ThreadPools", name, "submitedTasks", new AbstractOneValueMetric() {
//				public Number getValue() {
//					return (double)MonitoredThreadPoolExecutor.this.getSubmitedTasksCount();
//				}
//			});
//			MetricRegistry.createMetric("ThreadPools", name, "maxUtilization", new AbstractOneValueMetric() {
//				public Number getValue() {
//					return MonitoredThreadPoolExecutor.this.getMaxUtilization();
//				}
//			});
//			MetricRegistry.createMetric("ThreadPools", name, "rejectionsSinceLastSubmission", new AbstractOneValueMetric() {
//				public Number getValue() {
//					return (double)MonitoredThreadPoolExecutor.this.getMaxOverflow();
//				}
//			});
		}
	}

	public static class ThreadPoolExecutorWithCallback extends ThreadPoolExecutor implements ExecutorWithCallback {
		public ThreadPoolExecutorWithCallback(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		}

		public ThreadPoolExecutorWithCallback(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

		public ThreadPoolExecutorWithCallback(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		}

		public ThreadPoolExecutorWithCallback(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		}

		public void execute(Runnable runnable, ThreadPoolExecutorCallback callback) {
			super.execute(new MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.RunnableWrapper(runnable, callback));
		}

		public <T> Future<T> submit(Callable<T> task, ThreadPoolExecutorCallback callback) {
			return super.submit(new MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.CallableWrapper(task, callback));
		}

		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			if (r instanceof MonitoredThreadPoolExecutorFactory.FutureTaskWrapper && ((MonitoredThreadPoolExecutorFactory.FutureTaskWrapper) r).callable instanceof MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.CallableWrapper) {
				MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.CallableWrapper wrapper = (MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.CallableWrapper) ((MonitoredThreadPoolExecutorFactory.FutureTaskWrapper) r).callable;
				if (wrapper.callback != null) {
					wrapper.callback.afterExecute(wrapper.callable, t);
				}
			}

			if (r instanceof MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.RunnableWrapper && ((MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.RunnableWrapper) r).callback != null) {
			}

		}

		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			if (r instanceof MonitoredThreadPoolExecutorFactory.FutureTaskWrapper && ((MonitoredThreadPoolExecutorFactory.FutureTaskWrapper) r).callable instanceof MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.CallableWrapper) {
				MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.CallableWrapper wrapper = (MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.CallableWrapper) ((MonitoredThreadPoolExecutorFactory.FutureTaskWrapper) r).callable;
				if (wrapper.callback != null) {
					wrapper.callback.beforeExecute(t, wrapper.callable);
				}
			}

			if (r instanceof MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.RunnableWrapper && ((MonitoredThreadPoolExecutorFactory.ThreadPoolExecutorWithCallback.RunnableWrapper) r).callback != null) {
			}

		}

		protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
			return new MonitoredThreadPoolExecutorFactory.FutureTaskWrapper(callable);
		}

		private static class RunnableWrapper implements Runnable {
			private final Runnable runnable;
			private final ThreadPoolExecutorCallback callback;

			RunnableWrapper(Runnable runnable, ThreadPoolExecutorCallback callback) {
				this.runnable = runnable;
				this.callback = callback;
			}

			public void run() {
				this.runnable.run();
			}
		}

		private static class CallableWrapper implements Callable {
			private final Callable callable;
			private final ThreadPoolExecutorCallback callback;

			CallableWrapper(Callable callable, ThreadPoolExecutorCallback callback) {
				this.callable = callable;
				this.callback = callback;
			}

			public Object call() throws Exception {
				return this.callable.call();
			}
		}
	}
}