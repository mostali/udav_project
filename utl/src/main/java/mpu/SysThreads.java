package mpu;

import mpu.core.ARG;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.stream.Collectors;

public class SysThreads {
	//	public static List<Thread> getThreads() {
//		List<Thread> all = Thread.getAllStackTraces().keySet().stream().filter(t -> t.getName().equals(threadName)).collect(Collectors.toList());
//		return X.notEmpty(all) ? all : ARG.toDefThrowMsg(() -> X.f("Thread '%s' not found", threadName), defRq);
//	}
	public static List<Thread> getThreads(String threadName, List<Thread>... defRq) {
		List<Thread> all = Thread.getAllStackTraces().keySet().stream().filter(t -> t.getName().equals(threadName)).collect(Collectors.toList());
		return X.notEmpty(all) ? all : ARG.toDefThrowMsg(() -> X.f("Thread '%s' not found", threadName), defRq);
	}

	public static boolean isThreadActive(String threadName) {
		Thread.State threadState = getThreadState(threadName, null);
		return threadState != null && threadState != Thread.State.TERMINATED;
	}

	public static Thread.State getThreadState(String threadName, Thread.State... defRq) {
		final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 10000);
		for (ThreadInfo threadInfo : threadInfos) {
			if (!threadName.equals(threadInfo.getThreadName())) {
				continue;
			}
			final Thread.State state = threadInfo.getThreadState();
			return state;
		}
		return ARG.toDefThrowMsg(() -> X.f("Thread '%s' not found", threadName), defRq);
	}

	public static Integer[] getAllThreadCount() {
		final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		return new Integer[]{threadMXBean.getAllThreadIds().length, threadMXBean.getDaemonThreadCount(), threadMXBean.getPeakThreadCount()};
	}
}
