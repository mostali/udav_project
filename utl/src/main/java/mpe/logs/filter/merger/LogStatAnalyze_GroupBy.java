package mpe.logs.filter.merger;

import mpu.X;
import mpe.core.P;
import mpc.log.Lev;
import mpc.log.LogLine;
import mpu.str.Rt;
import mpc.str.condition.LogGetterDate;
import mpc.str.sym.SYMJ;
import mpu.pare.Pare3;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LogStatAnalyze_GroupBy {

	private static Logger L = LoggerFactory.getLogger(LogStatAnalyze_GroupBy.class);

	public static void main(String[] args) throws IOException {


//		Map<String, ThreadInfo> collectStats = collectStatsByGroup("/home/dav/pjbf_tasks/62/4/mr.server.LL-EFIWDT_S20231109115630-E20231109115715_sl-1700210574IC.log", LogGetterDate.buildDefault());
		Map<String, ThreadInfo> collectStats = collectStatsByThread("/home/dav/pjbf_tasks/62/4/mr.server.LL-EFIWDT_S20231109115630-E20231109115715_sl-1700210574IC.log", LogGetterDate.buildByDefault());

		P.p(Rt.buildReport(collectStats, "collectStats"));

	}

	public static Map<String, ThreadInfo> collectStatsByThread(String file, LogGetterDate logGetterDate) {
		LogLineBlock[] logLineBlocks = LogFile.parseLogLinesBlock(file, logGetterDate);
		Map<String, ThreadInfo> threadMap = new HashMap<>();
		for (LogLineBlock logLineBlock : logLineBlocks) {
			LogLine logLine = LogLine.of(logLineBlock.getLines()[0]);
			String date = logLine.date();
			String thread = logLine.thread();
			String group = logLine.group();
			String content = logLine.content();

			ThreadInfo threadInfo = threadMap.get(thread);
			if (threadInfo == null) {
				threadMap.put(thread, threadInfo = new ThreadInfoByThread(thread));
			}
			threadInfo.levelAdd(logLine.levelType());
			threadInfo.historyAdd(Pare3.of(date, group, content));
		}
		return threadMap;
	}

	public static Map<String, ThreadInfo> collectStatsByGroup(String file, LogGetterDate logGetterDate) {
		LogLineBlock[] logLineBlocks = LogFile.parseLogLinesBlock(file, logGetterDate);
		Map<String, ThreadInfo> threadMap = new HashMap<>();
		for (LogLineBlock logLineBlock : logLineBlocks) {
			LogLine logLine = LogLine.of(logLineBlock.getLines()[0]);
			String date = logLine.date();
			String thread = logLine.thread();
			String group = logLine.group();
			String content = logLine.content();

			ThreadInfo threadInfo = threadMap.get(group);
			if (threadInfo == null) {
				threadMap.put(group, threadInfo = new ThreadInfoByGroup(group));
			}
			threadInfo.levelAdd(logLine.levelType());
			threadInfo.historyAdd(Pare3.of(date, thread, content));
		}
		threadMap = threadMap.entrySet().stream().sorted((e1, e2) -> ((Integer) e2.getValue().history.size()).compareTo(e1.getValue().history.size())).collect(Collectors.toMap(
				Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return threadMap;
	}

	public static class ThreadInfoByThread extends ThreadInfo {
		public ThreadInfoByThread(String threadName) {
			super(threadName);
		}

		@Override
		public String toString() {
			String sources = history.stream().map(p -> p.val()).collect(Collectors.toSet()).stream().collect(Collectors.joining(" " + SYMJ.GALAXY + " "));
			return super.toString() + X.f(" >>> >>> %s %s" + STR.NL, SYMJ.GALAXY, sources);
		}

	}

	public static class ThreadInfoByGroup extends ThreadInfo {

		public ThreadInfoByGroup(String groupName) {
			super(groupName);
		}

		@Override
		public String toString() {
			String sources = history.stream().map(p -> p.val()).collect(Collectors.toSet()).stream().collect(Collectors.joining(" " + SYMJ.ARROW_MOVABLE + " "));
			return super.toString() + X.f(" >>> >>> %s %s" + STR.NL, SYMJ.ARROW_MOVABLE, sources);
		}
	}

	public abstract static class ThreadInfo {
		final String key;
		final List<Pare3<String, String, String>> history;
		final Map<Lev, Integer> levelUsage;

		@Override
		public String toString() {
			return X.f_("(%s) - %s:", history.size(), levelUsage);
		}

		public ThreadInfo(String key) {
			this.key = key;
			history = new LinkedList<>();
			levelUsage = new HashMap<>();
		}

		ThreadInfo historyAdd(Pare3 line) {
			history.add(line);
			return this;
		}

		ThreadInfo levelAdd(Lev lev) {
			Integer i = levelUsage.get(lev);
			levelUsage.put(lev, i == null ? 1 : ++i);
			return this;
		}

//		public static <K, V extends Comparable<? super V>> Map<K, V> sortByHistorySize(Map<K, V> map) {
//			List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
//			list.sort(Map.Entry.comparingByValue());
//
//			Map<K, V> result = new LinkedHashMap<>();
//			for (Map.Entry<K, V> entry : list) {
//				result.put(entry.getKey(), entry.getValue());
//			}
//
//			return result;
//		}
//		public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByHistorySize() {
//			return (Comparator<Map.Entry<K, V>> & Serializable) (c1, c2) -> c1.getValue().compareTo(c2.getValue());
//		}
//
//		public static int sortByHistorySize(Map.Entry<String, ThreadInfo> stringThreadInfoEntry, Map.Entry<String, ThreadInfo> stringThreadInfoEntry1) {
//			return ((Integer) stringThreadInfoEntry.getValue().history.size()).compareTo(stringThreadInfoEntry1.getValue().history.size());
//		}
	}


}
