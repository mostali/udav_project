package mpf.multitask;

import java.io.Serializable;

//Copy from Spring
public class CustomizableThreadCreator implements Serializable {
	private String threadNamePrefix;
	private int threadPriority = 5;
	private boolean daemon = false;
	private ThreadGroup threadGroup;
	private int threadCount = 0;
	private final Object threadCountMonitor = new CustomizableThreadCreator.SerializableMonitor();

	public CustomizableThreadCreator() {
		this.threadNamePrefix = this.getDefaultThreadNamePrefix();
	}

	public CustomizableThreadCreator(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix != null ? threadNamePrefix : this.getDefaultThreadNamePrefix();
	}

	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix != null ? threadNamePrefix : this.getDefaultThreadNamePrefix();
	}

	public String getThreadNamePrefix() {
		return this.threadNamePrefix;
	}

	public void setThreadPriority(int threadPriority) {
		this.threadPriority = threadPriority;
	}

	public int getThreadPriority() {
		return this.threadPriority;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public boolean isDaemon() {
		return this.daemon;
	}

	public void setThreadGroupName(String name) {
		this.threadGroup = new ThreadGroup(name);
	}

	public void setThreadGroup(ThreadGroup threadGroup) {
		this.threadGroup = threadGroup;
	}

	public ThreadGroup getThreadGroup() {
		return this.threadGroup;
	}

	public Thread createThread(Runnable runnable) {
		Thread thread = new Thread(this.getThreadGroup(), runnable, this.nextThreadName());
		thread.setPriority(this.getThreadPriority());
		thread.setDaemon(this.isDaemon());
		return thread;
	}

	protected String nextThreadName() {
		int threadNumber = 0;
		synchronized (this.threadCountMonitor) {
			++this.threadCount;
			threadNumber = this.threadCount;
		}

		return this.getThreadNamePrefix() + threadNumber;
	}

	protected String getDefaultThreadNamePrefix() {
		return getShortName(this.getClass()) + "-";
	}

	private static class SerializableMonitor implements Serializable {
		private SerializableMonitor() {
		}
	}

	public static String getShortName(Class<?> clazz) {
		return getShortName(getQualifiedName(clazz));
	}

	public static String getShortName(String className) {
		int lastDotIndex = className.lastIndexOf(46);
		int nameEndIndex = className.indexOf("$$");
		if (nameEndIndex == -1) {
			nameEndIndex = className.length();
		}

		String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
		shortName = shortName.replace('$', '.');
		return shortName;
	}

	public static String getQualifiedName(Class<?> clazz) {
		return clazz.isArray() ? getQualifiedNameForArray(clazz) : clazz.getName();
	}

	private static String getQualifiedNameForArray(Class<?> clazz) {
		StringBuilder result = new StringBuilder();

		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			result.append("[]");
		}

		result.insert(0, clazz.getName());
		return result.toString();
	}
}
