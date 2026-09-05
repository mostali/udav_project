package mpe.logs.filter.merger;

import mpc.log.Lev;
import mpc.log.LogLine;
import mpc.str.condition.LogGetterDate;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.pare.Pare;
import mpu.str.STR;
import mpu.str.Sb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LogStatAnalyze_ThreadHistory {

	private static Logger L = LoggerFactory.getLogger(LogStatAnalyze_ThreadHistory.class);

	public static void main(String[] args) throws IOException {

		X.exit(collectHistoryByThread_REPORT("/home/dav/pjbf_tasks/16/0611/ai-last-day.log", LogGetterDate.buildByFormat("dd.MM.yyyy;HH:mm:ss.SSS"), LogLine.DEF_MAP_AI));
//		LogLine logLine = LogLine.of("05.06.2024;12:51:42.422 DEBUG [d017fae4-0655-47c0-8f46-e9b8ef519610 NO_SESSION] [qtp1687087217-27028][r.o.a.c.d.ObjectDaoImpl]: getObjectsByCodes вернул [76] строк для codes [[0503074, 0503125, 0503127, 0503128-НП, 0503129, 0503160, 0503164, 0503169, 0503173, 0503178, 0503184, 0501096, 0503725, 0503738-НП, 0503760, 0503773, GRBS_VNK1, GRBS_VNK2]] за [0] ms\n", LogLine.DEF_MAP_AI);
//		X.p(logLine)
		;

//		Map<String, ThreadInfo> collectStats = collectStatsByGroup("/home/dav/pjbf_tasks/62/4/mr.server.LL-EFIWDT_S20231109115630-E20231109115715_sl-1700210574IC.log", LogGetterDate.buildDefault());
		Map<String, ThreadInfo> collectStats = collectHistoryByThread("/home/dav/pjbf_tasks/16/0611/ai-last-day.log", LogGetterDate.buildByFormat("dd.MM.yyyy;HH:mm:ss.SSS"), LogLine.DEF_MAP_AI);
//
//		P.p(Rt.buildReport(collectStats, "collectStats"));

		for (Map.Entry<String, ThreadInfo> thread : collectStats.entrySet()) {

			X.p(">>>" + thread.getKey());
			thread.getValue().history.forEach(p -> X.p(1, p.val()));
//			X.p(STR.TAB(1) + thread.getValue().history.stream().map(p -> p.val()).collect(Collectors.joining(STR.NL)));
		}

	}

	public static String collectHistoryByThread_REPORT(String file, LogGetterDate logGetterDate, int[] mappingStrategyLL) {
		Map<String, ThreadInfo> collectStats = collectHistoryByThread(file, logGetterDate, mappingStrategyLL);
		Sb sb = new Sb();
		for (Map.Entry<String, ThreadInfo> thread : collectStats.entrySet()) {
			sb.p(">>>" + thread.getKey());
			Collections.reverse(thread.getValue().history);
			thread.getValue().history.forEach(p -> sb.p(1, p.key() + ":" + p.val()));
		}
		return sb.toString();
	}

	public static Map<String, ThreadInfo> collectHistoryByThread(String file, LogGetterDate logGetterDate, int[] mappingStrategyLL) {
		LogLineBlock[] logLineBlocks = LogFile.parseLogLinesBlock(file, logGetterDate);
		Map<String, ThreadInfo> threadMap = new TreeMap<>();
		for (LogLineBlock logLineBlock : logLineBlocks) {
			LogLine logLine = LogLine.of(logLineBlock.getLines()[0], mappingStrategyLL);
			String date = logLine.date();
			String thread = logLine.thread();
			String group = logLine.group();
			String content = logLine.content();

			ThreadInfo threadInfo = threadMap.computeIfAbsent(thread, (t) -> new ThreadInfoByThread(thread));

			threadInfo.levelAdd(logLine.levelType());
//			threadInfo.historyAdd(Pare3.of(date, group, content));
			threadInfo.historyAdd(Pare.of(date, content));
		}
		return threadMap;
	}


	private static class ThreadInfoByThread extends ThreadInfo {
		public ThreadInfoByThread(String threadName) {
			super(threadName);
		}

		@Override
		public String toString() {
			String sources = history.stream().map(p -> p.val()).collect(Collectors.toSet()).stream().collect(Collectors.joining(" " + SYMJ.GALAXY + " "));
			return super.toString() + X.f(" >>> >>> %s %s" + STR.NL, SYMJ.GALAXY, sources);
		}

	}


	private abstract static class ThreadInfo {
		final String key;
		final List<Pare<String, String>> history;
		final Map<Lev, Integer> levelUsage;

		@Override
		public String toString() {
			return X.f_("(%s) - %s:", levelUsage, history);
		}

		public ThreadInfo(String key) {
			this.key = key;
			history = new LinkedList<>();
			levelUsage = new HashMap<>();
		}

		ThreadInfo historyAdd(Pare line) {
			history.add(line);
			return this;
		}

		ThreadInfo levelAdd(Lev lev) {
			Integer i = levelUsage.get(lev);
			levelUsage.put(lev, i == null ? 1 : ++i);
			return this;
		}

	}


}
