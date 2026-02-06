package mpf.multitask;

public interface MonitoredForkJoinPoolMXBean {
	int getPoolSize();

	int getRunningThreadCount();

	int getActiveThreadCount();

	long getStealCount();

	long getQueuedTaskCount();

	int getQueuedSubmissionCount();
}
