package mpe.logs.filter.merger;

import mpe.ftypes.core.FDate;
import mpu.X;
import mpu.core.ARR;
import mpe.core.P;
import mpc.exception.RequiredRuntimeException;
import mpu.core.RW;
import mpu.core.ARG;
import mpu.IT;
import mpc.fs.UDIR;
import mpc.fs.UFS;
import mpc.fs.fd.EFT;
import mpc.log.Lev;
import mpc.map.MAP;
import mpu.str.Rt;
import mpc.str.condition.LogGetterDate;
import mpc.str.condition.StringCondition;
import mpu.core.QDate;
import mpe.logs.filter.ILogFilterProcessor;
import mpe.logs.filter.LogProc;
import mpe.logs.filter.filters.LineLevelCondition;
import mpe.logs.filter.filters.KeyLineCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LogMerger {

	private static Logger L = LoggerFactory.getLogger(LogMerger.class);

	public static void main(String[] args) throws IOException {

		String logFile = "/home/dav/pjnsi_tasks/30/catalina.out";

		LogGetterDate logGetterDate = LogGetterDate.buildByFormat(FDate.APP_SLDF_MDM);
		LogProc logLinesProcessor = new LogProc(logGetterDate);
		QDate from = QDate.ofWithYear("20250121-162300", "yyyyMMdd-HHmmss");
		QDate to = from.addMinutes(22);
		logLinesProcessor.addFilter(new StringCondition.BwDateStringCondition(from, to, logGetterDate));
		ArrayList<String> lines = logLinesProcessor.processFile(logFile, logGetterDate, false);
		X.exit(lines);

//		logLinesProcessor.addFilter(StringCondition.BwKeysStringCondition.build("123123123", "123123123"));
		logLinesProcessor.addFilter(StringCondition.BwKeysStringCondition.build("qtp1261031890-105257", "qtp1261031890-105257"));
		ArrayList<String> strings = logLinesProcessor.processFile("/home/dav/pjbf_tasks/72/content/test.log", logGetterDate, false);

//		logLinesProcessor.addFilter(StringCondition.BwKeysStringCondition.build("qtp1261031890-105257", "qtp1261031890-105257"));
//		ArrayList<String> strings = logLinesProcessor.processFile("/home/dav/pjbf_tasks/72/content/server.log.2023-12-01-544", logGetterDate, false);
		P.exit(strings);
		logLinesProcessor.addFilter(new LineLevelCondition(MAP.of(Lev.INFO, true)));
//		Date start = FDate.parse("20231109115820", FDate.YYYYMMDDmmhhss);
//		Date end = FDate.parse("20231109120015", FDate.YYYYMMDDmmhhss);
		Date start = QDate.MIN_DATE;
		Date end = QDate.MAX_DATE;
		logLinesProcessor.addFilter(new StringCondition.BwDateStringCondition(start, end, logGetterDate));
		logLinesProcessor.addFilter(new KeyLineCondition("/home/dav/pjbf_tasks/64/3/fr/_/merged/mr.server.LL-FWEITD_S20231109115020-E20231109115820+.log", false, false));

		List<Path> twins = UFS.TWINS.findTwins(Paths.get("/home/dav/pjbf_tasks/67/server.log"), true);
		P.exit(twins);

//		ArrayList<String> strings = logLinesProcessor.processFile("/home/dav/pjbf_tasks/64/3/fr/_/server (6).log", "/home/dav/pjbf_tasks/64/3/fr/_/merged/server (6).log", logGetterDate, false);
		strings = logLinesProcessor.processFile("/home/dav/pjbf_tasks/64/3/fr/_/server (66).log", "/home/dav/pjbf_tasks/64/3/fr/_/merged/server (6).log", logGetterDate, false);
		P.exit(strings.size());

//		mergeLogFromDir("/home/dav/pjbf_tasks/64/3/fr", "/home/dav/pjbf_tasks/64/3/fr/0/merged.log");
//		mergeLogFromDir(LogGetterDate.buildDefault(), "/home/dav/pjbf_tasks/64/3/WS", "/home/dav/pjbf_tasks/64/3/WS/0/merged.log");
//		P.exit("ok");
//
//		mergeLogFromDir(LogGetterDate.buildDefault(), "/home/dav/pjbf_tasks/0_merge/0", "/home/dav/pjbf_tasks/0_merge/merged.log");
//		P.exit("ok");
//
//		mergeLogFromDir(LogGetterDate.buildDefault(), "/home/dav/Загрузки/акт ПФ/", "/home/dav/Загрузки/акт ПФ/merged/merged.log");
//		P.exit("ok");

		{
			Path dirWS = Paths.get("/home/dav/pjbf_tasks/64/3/fr");
			Path dirFR = Paths.get("/home/dav/pjbf_tasks/64/3/WS");

			P.exit(UFS.TWINS.findTwinsReport(dirWS, dirFR, true));
		}

		{
			Path dirWS = Paths.get("/home/dav/pjbf_tasks/64/0");
			Path dirFR = Paths.get("/home/dav/pjbf_tasks/64/0_fr");

			P.exit(UFS.TWINS.findTwinsReport(dirWS, dirFR, true));
		}


		LogLineBlock[] logLines = LogFile.parseLogLinesBlock("/home/dav/pjbf_tasks/62/2/test/dlc.log", LogGetterDate.buildByDefault());
		LogLineBlock[] logLines2 = LogFile.parseLogLinesBlock("/home/dav/pjbf_tasks/62/2/test/server.log", LogGetterDate.buildByDefault());

		LogLineBlock[] merged = merge(logLines, logLines2);
		LogFile.writeFile("/home/dav/pjbf_tasks/62/2/test/dlc-merged.log", merged, true);

		P.exit();


		P.exit();
//		P.exit(UFS.findNotTwins(dirWS, dirFR, false));
//		P.exit(UFS.findTwins(Paths.get("/home/dav/pjbf_tasks/64/0"), Paths.get("/home/dav/pjbf_tasks/64/0_fr"), true));
//		P.exit(UFS.findNotTwins(Paths.get("/home/dav/pjbf_tasks/64/0_fr"), Paths.get("/home/dav/pjbf_tasks/64/0"), true));


//		LogFile.writeFile("/home/dav/pjbf_tasks/62/2/test/dlc.log2", logLines);
//		ERR.isEqFileContent("/home/dav/pjbf_tasks/62/2/test/dlc.log", "/home/dav/pjbf_tasks/62/2/test/dlc.log2");

//		P.exit();
//		merge("tmp/fromfile1", "tmp/tofile2");
//		LogGetterDate logGetterDate = LogGetterDate.of(LogProcArgs.FORMAT_DEFAULT, Date7.now().year);
//		merge("/home/dav/pjbf_tasks/8/part1/test", "merge.log", LogGetterDate.buildDefault());
//		merge("/home/dav/pjbf_tasks/62/2/test/", "/home/dav/pjbf_tasks/62/2/test/merge.log", LogGetterDate.buildDefault());
	}

	public static void mergeLogFromDir(LogGetterDate logGetterDate, String dir, String dstFile, boolean collapseMultilineToSingleLine) {
		List<Path> ls = UDIR.ls(Paths.get(dir), EFT.FILE, Collections.EMPTY_LIST);
		mergeLog(logGetterDate, ls, dstFile, null, collapseMultilineToSingleLine);
	}

	public static void mergeLog(LogGetterDate logGetterDate, List<Path> mergeFiles, String dstFile, ILogFilterProcessor processor, boolean collapseMultilineToSingleLine, Logger... logger) {
		Logger log = ARG.toDefOr(L, logger);
		log.info(Rt.buildReport(mergeFiles, "Begin merge files:").toString());
		List<LogLineBlock[]> total = new LinkedList();
		for (Path mergeFile : mergeFiles) {
			LogLineBlock[] logLineBlocks = LogFile.parseLogLinesBlock(mergeFile.toString(), logGetterDate);
			if (processor != null) {
				logLineBlocks = processor.processBlocksToBlocks(logGetterDate, mergeFile.toString(), ARR.as(logLineBlocks), collapseMultilineToSingleLine);
			}
			total.add(logLineBlocks);
		}
		LogLineBlock[] merged = merge(total.toArray(new LogLineBlock[0][0]));
		boolean explodeMultiline = !collapseMultilineToSingleLine;
		LogFile.writeFile(dstFile, merged, explodeMultiline);
		log.info("Merged successfully to {}", dstFile);

	}

	private static LogLineBlock[] merge(LogLineBlock[]... logLines) {
		if (L.isInfoEnabled()) {
			L.info("Start merge:" + ARR.as(logLines));
		}
		IT.notEmpty(logLines);
//		TreeMultiset<LogLine> treeMultiset = TreeMultiset.create();
//		int all = 0;
		TreeSet<LogLineBlock> treeMultiset = new TreeSet<>();
//		LinkedHashMap<Long, LogLineBlock> storeMap = new LinkedHashMap<>();
		for (int i = 0; i < logLines.length; i++) {
			LogLineBlock[] file0 = logLines[i];
			for (int n = 0; n < file0.length; n++) {
				LogLineBlock line0 = file0[n];
//				++all;
				boolean add = treeMultiset.add(line0);
//				storeMap.put(line0.date.getTime(),li)
//				ERR.isFalse(treeMultiset.contains(line0), "exists:" + line0);
			}
		}
//		ERR.state(all == treeMultiset.size());//skip check - before compare LogLine return 0
		return treeMultiset.toArray(new LogLineBlock[0]);
	}

//	private static void merge(String dirWithLogFiles, String targetFile, LogGetterDate logGetterDate) throws IOException {
//		Path dir = ERR.isDirExist(Paths.get(dirWithLogFiles));
//		File[] logFiles = dir.toFile().listFiles(new FileFilter() {
//			@Override
//			public boolean accept(File pathname) {
//				return pathname.isFile() &&
//						(StringUtils.endsWithIgnoreCase(pathname.getName(), ".log")
//								|| StringUtils.containsIgnoreCase(pathname.getName(), ".log."));
//			}
//		});
//		merge(dirWithLogFiles, targetFile, logGetterDate, logFiles);
//	}

//	private static void merge(String dirWithLogFiles, String targetFile, LogGetterDate logGetterDate, File... logFiles) throws IOException {
//
//		ERR.notEmpty(logFiles, "Log file not found from dir", dirWithLogFiles);
//		L.info("Found log files:{}\n{}", logFiles.length, Arr.toNiceStringCompact(logFiles));
//
//		Path target = Paths.get(targetFile);
//		FileUtils.deleteQuietly(target.toFile());
//
//		TreeMap<Date, File> tree = new TreeMap<>();
//		for (File log : logFiles) {
//			Date[] fl = getFirstLastDate(log, logGetterDate);
//			tree.put(fl[1], log);
//		}
//		L.info("Tree was builded :\n{}", tree);
//		write(tree, target);
//
//		L.info("Writed successfully. File ' {}", targetFile);
//	}

//	private static void write(TreeMap<Date, File> tree, Path targetFile) throws IOException {
//		int ctr = 0;
//		for (Map.Entry<Date, File> treeFile : tree.entrySet()) {
//			RW.write_Append_(targetFile, treeFile.getValue().toPath(), true);
//			RW.write_AppendOrCreateNew_(targetFile, PAGE_DELIMITER + " " + (++ctr) + " " + treeFile.getValue().getName());
//		}
//	}

	public static Date[] getFirstLastDate(File log, LogGetterDate logGetterDate) throws IOException {
		List<String> lines = RW.readLines_(log.toPath());
		return new Date[]{getFirstLastDate(lines, true, logGetterDate), getFirstLastDate(lines, false, logGetterDate)};
	}

	public static Date getFirstLastDate(List<String> lines, boolean firstOrLast, LogGetterDate logGetterDate, Date... defRq) {
		Date firstLast = null;
		if (firstOrLast) {
			for (String line : lines) {
				Date date = logGetterDate.getDateFrom(line, null);
				if (date != null) {
					firstLast = date;
					break;
				}
			}
		} else {
			for (int i = lines.size() - 1; i >= 0; i--) {
				String line = lines.get(i);
				Date date = logGetterDate.getDateFrom(line, null);
				if (date != null) {
					firstLast = date;
					break;
				}
			}
		}
		if (firstLast != null) {
			return firstLast;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Date not found from log lines({})\n{}", lines.size(), lines);
	}

}
