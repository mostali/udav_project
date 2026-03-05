package mpe.logs.filter.merger;

import mpc.exception.FIllegalStateException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.log.L;
import mpc.map.BootContext;
import mpc.map.MAP;
import mpc.str.condition.LogGetterDate;
import mpc.str.condition.StringCondition;
import mpc.str.sym.SYMJ;
import mpe.ftypes.core.FDate;
import mpe.logs.filter.LogProc;
import mpu.X;
import mpu.core.ARR;
import mpu.core.QDate;
import mpu.core.TimeMark;
import mpu.func.FunctionV;
import mpu.str.TablePrint;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <pre>
 *
 * Install:
 * /bin/bash ~/pjm/utl/install-log-merger.sh
 *
 * Run:
 * java -jar ~/.data/.bin/logm-mod.jar -from 20250121:201300 -to 20250126:230059 -period 5m -format "yyyy-MM-dd HH:mm:ss,SSS" -merge true -dir "./workDir" -files "*.{log,out}" -cleanDir true
 *
 * </pre>
 */
public class LogMergerMod {

//	private static Logger L = LoggerFactory.getLogger(LogMergerMod.class);

	public static final String FORMAT_INPUT_DATE = FDate.APP_MONOTWICE;


	private static boolean checkMan(String[] args) {
		boolean contains = ARR.contains(args, "--man");
		if (contains) {
			String msg = X.fl_("\nJar Args[default]=Example Value" +
							"\nfrom={}\nto={}\nperiod=1s|1m|1h|1d|1M\nlogFormat(required)={}\nmerge[false]=true\ndir[.]=./workDir\nfiles[*]=*.{log,out}\ncleanDir[false]=true\n", //
					QDate.now().f(QDate.F.MONO20NF), QDate.now().addMinutes(1).f(QDate.F.MONO20NF), FDate.APP_SLDF_MDM);
			TablePrint tableList = TablePrint.toStringFromMap(MAP.mapOfLines(msg), false);
			tableList.addRow("Example", "java -jar ./logm-mod.jar -from 20250121:201300 -to 20250126:230059 -period 5m -format \"yyyy-MM-dd HH:mm:ss,SSS\" -merge true -dir \"./workDir\" -files \"*.{log,out}\" -cleanDir true");
			tableList.printConsole();
			return true;
		}
		return false;
	}

	public static void main(String[] args) throws IOException {
		if (checkMan(args)) {
			return;
		}
//		SLEEP.sec(5,"wait debug");
//		args = new String[]{"-from1", "20250121:201300", "-to1", "20250121:162800", "-period", "1m", "-format", "yyyy-MM-dd HH:mm:ss,SSS",
//				"-merge", "true", "-dir", "/home/dav/pjnsi/0HNR_LOG", "-files", "*.{log,out}", "-cleanDir", "true"};

		BootContext bootContext = BootContext.ofAll(args);

		String logFormatDate = bootContext.getAsString("format");
		String period = bootContext.getAsString("period", null);
		Path workDir = bootContext.getAsPath("dir", Paths.get("."));
		String files = bootContext.getAsString("files", "*");
		Boolean merge = bootContext.getAsBoolean("merge", false);
		Boolean cleanDir = bootContext.getAsBoolean("cleanDir", false);

		String spfx = "__";
		if (cleanDir) {
			UFS.ls(workDir).forEach(p -> {
				if (UF.fn(p).startsWith(spfx)) {
					UFS.RM.deleteDir(p);
				}
			});
		}

		Collection<Path> srcFilesAll = UFS.SEARCH.searchFilesWithWc(workDir, files);

		Collection<Path> srcFiles = srcFilesAll.stream().filter(f -> !f.getFileName().toString().startsWith(spfx)).collect(Collectors.toList());

		LogGetterDate iGetLogDate = LogGetterDate.buildByFormat(logFormatDate);

		Supplier<Date[]> finderFirstLastDateInFiles = () -> LogGetterDate.findFirstLastDateFromFiles(srcFiles, iGetLogDate, null);

		QDate[] startEnd = parseStartEnd(finderFirstLastDateInFiles);
		QDate start = startEnd[0];
		QDate end = startEnd[1];

		FunctionV printConfg = () -> {
			String msg = X.fl("\nLogMerger Config=Used value\nfrom={}\nto={}\nperiod={}\nlogFormat={}\nmerge={}\ndir={}\nfiles={}", start.f(QDate.F.MONO20NF), end.f(QDate.F.MONO20NF), period, logFormatDate, merge, UF.ln(workDir), files);
//			L.info(msg);
//			L.info("\nRun LogMerger Config:\nfrom={}\nto={}\nperiod={}\nlogFormat={}\nmerge={}\ndir={}\nfiles={}", start.f(QDate.F.MONO20NF), end.f(QDate.F.MONO20NF), period, logFormatDate, merge, workDir, files);
			TablePrint.toStringFromMap(MAP.mapOfLines(msg), true);
		};
		printConfg.apply();

		LogProc logLinesProcessor = new LogProc(iGetLogDate);
		List<Path> handledFiles = ARR.asAL();
		String pfx = start.f(QDate.F.HH_mm) + "__" + end.f(QDate.F.HH_mm);

		Map<Path[], Integer> stat = new HashMap<>();

		for (Path path : srcFiles) {
			logLinesProcessor.addFilter(new StringCondition.BwDateStringCondition(start, end, iGetLogDate));
			Path dst = UF.newRenameName(path, spfx + pfx + "." + path.getFileName().toString());
			ArrayList<String> lines = logLinesProcessor.processFile(path.toString(), dst.toString(), iGetLogDate, false);
			L.info("Handle {} lines to file file://{}", X.sizeOf(lines), dst);
			stat.put(new Path[]{path, dst}, X.sizeOf(lines));
			handledFiles.add(dst);
		}


		Path dstMergedPath = null;
		if (merge) {
			String dstMergedFile = spfx + "MERGED__" + pfx + ".log";
			dstMergedPath = workDir.resolve(dstMergedFile);
			LogMerger.mergeLog(iGetLogDate, handledFiles, dstMergedPath.toString(), null, false);
		}

		{ // INFO

			printConfg.apply();

			TablePrint rsltTable = TablePrint.of("Src", "Size", "Dst", "Size");

			rsltTable.addRowAs(SYMJ.DIR_OPEN + " Work Directory ", "", UF.ln(workDir), "");

//			L.info("Finish. Work Directory {} file://{}", SYMJ.DIR_OPEN, workDir);

			stat.entrySet().forEach(p -> {
//				L.info("Finish. Src file://{} *{}   >>>   Dst file://{} *{}", p.getKey()[0], X.sizeOfLines(p.getKey()[0]), p.getKey()[1], X.sizeOfLines(p.getKey()[1]));
				rsltTable.addRowAs(UF.ln(p.getKey()[0]), X.sizeOfLines(p.getKey()[0]), UF.ln(p.getKey()[1]), X.sizeOfLines(p.getKey()[1]));
			});
			if (dstMergedPath != null) {
//				L.info("Finish. Merged file://" + dstMergedPath + " *" + X.sizeOfLines(dstMergedPath));
				rsltTable.addRowAs(SYMJ.FILE4 + " Merged", "", UF.ln(dstMergedPath), X.sizeOfLines(dstMergedPath));
			}
			rsltTable.printConsole();
		}


	}

	//-p - last period
	//-start - all from start
	//-start&p - all from start+period
	//-end&p - all from end-period
	//-start&p - all from start to period
	//-start&end - all from start to end

	private static QDate[] parseStartEnd(Supplier<Date[]> finderFirstLastDateInFiles) {

		Date[] firstLastDateFromFiles = finderFirstLastDateInFiles.get();

		BootContext bootContext = BootContext.get();
		QDate startDate = bootContext.getAsQDate("from", FORMAT_INPUT_DATE, null);
		QDate endDate = bootContext.getAsQDate("to", FORMAT_INPUT_DATE, null);

		if (startDate != null && endDate != null) {
			return new QDate[]{startDate, endDate};
		}

		String markPeriodTime = bootContext.getAsString("period", null);

		if (startDate != null) {
			if (markPeriodTime != null) {
				Long periodMs = TimeMark.convertToMs(markPeriodTime);
				endDate = startDate.addMilliSeconds(periodMs.intValue());
			} else {
				endDate = QDate.now();
			}
			return new QDate[]{startDate, endDate};
		}

		//startDate is null

		if (endDate != null) {
			if (markPeriodTime != null) {
				Long periodMs = TimeMark.convertToMs(markPeriodTime);
				startDate = endDate.addMilliSeconds(-periodMs.intValue());
			} else {
				startDate = QDate.now().minusYears(3);
			}
			return new QDate[]{startDate, endDate};
		}

		//startDate is null
		//endDate is null

		if (markPeriodTime != null) {
			Long periodMs = TimeMark.convertToMs(markPeriodTime);
//			endDate = QDate.now();
			endDate = QDate.of(firstLastDateFromFiles[1]);
			startDate = endDate.addMilliSeconds(-periodMs.intValue());
			return new QDate[]{startDate, endDate};
		}

		throw new FIllegalStateException("\nConfigure parse period used any combination of arg [ '-from date' , '-to date' , '-period 3m' ]" + //
				"\nInput date parse with format '%s'", //
				FORMAT_INPUT_DATE //
		);
	}

}
