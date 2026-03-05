package mpe.logs.filter;

import lombok.RequiredArgsConstructor;
import mpu.X;
import mpu.core.ARR;
import mpu.str.Hu;
import mpc.str.condition.LogGetterDate;
import mpu.core.UTime;
import mpc.str.condition.StringConditionType;
import mpc.str.condition.StringCondition;
import mpu.core.RW;
import mpe.logs.filter.merger.LogFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpu.IT;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class LogProc {

	public final List<StringCondition> lineConditions = new ArrayList<>();

	private final StringCondition.IGetterDate<String> iGetLogDate;

	public LogProc() {
		this.iGetLogDate = LogGetterDate.buildByDefault();
	}

	private static final Logger L = LoggerFactory.getLogger(LogProc.class);

	public static void main(String[] args) throws IOException {

//		StringCondition.BwKeysStringCondition
//		P.exit();
//		{
//			QDate qDate = QDate.of("06-13;09:35:26.674", FDate.APP_STANDART_LOG_DATE_FORMAT, 2023);
////			P.exit(qDate);
//			QDate qDate1 = QDate.of("06-13;09:37:26.678", FDate.APP_STANDART_LOG_DATE_FORMAT, 2023);
//			LogLinesProcessor byBetweenDate = createByBetweenDate(qDate, qDate1);
//			ArrayList<String> strings = byBetweenDate.processFile("/home/dav/pjbf_tasks/12/server.log", "/home/dav/pjbf_tasks/12/server.bw.log");
//			P.exit();
//		}
//		{
//			QDate qDate = QDate.of(2023, 05, 31, 10, 00, 00);
//			QDate qDate1 = QDate.of(2023, 05, 31, 10, 20, 00);
//			LogProc byBetweenDate = createByBetweenDate(qDate, qDate1);
//			ArrayList<String> strings = byBetweenDate.processFile("/home/dav/pjbf_tasks/8/part1/WS/logs/merged.log", "/home/dav/pjbf_tasks/8/part1/WS/logs/merged2.log");
//		}
	}

	public static LogProc of() {
		return of(LogGetterDate.buildByDefault());
	}

	public static LogProc of(LogGetterDate logGetterDate) {
		return create(logGetterDate);
	}

	public static LogProc create(LogGetterDate logGetterDate) {
		return new LogProc(logGetterDate);
	}

	public static LogProc createByDay(LogGetterDate logGetterDate, Date day) {
		Date[] dates = UTime.rangeBetween(day, Calendar.DAY_OF_MONTH, Date.class);
		return createByBetweenDate(logGetterDate, dates[0], dates[1]);
	}

	public static LogProc createByBetweenDate(LogGetterDate logGetterDate, Date start, Date end) {
		return new LogProcArgs(logGetterDate, start, end).buildLogLinesProcessor();
	}

	public static LogProc createByBetweenKeys(LogGetterDate logGetterDate, String start, String end) {
		return new LogProcArgs(logGetterDate, start, end).buildLogLinesProcessor();
	}

	public static LogProc createByBetweenKeys(LogGetterDate logGetterDate, Date start, Date end) {
		return new LogProcArgs(logGetterDate, start, end).buildLogLinesProcessor();
	}

	public static LogProc createByKey(LogGetterDate logGetterDate, String key) {
		return new LogProcArgs(logGetterDate, key).buildLogLinesProcessor();
	}

	public static ArrayList<String> processLines(LogGetterDate logGetterDate, List<String> linesIn, List<ILogFilter> filters, boolean explodeMultiline) {
		LogProc logProc = of(logGetterDate);
		for (ILogFilter logFilterCom : filters) {
			StringCondition filter = logFilterCom.toFilter();
			if (filter != null) {
				logProc.addFilter(filter);
			}
		}
		ArrayList<String> linesOut = logProc.process(linesIn);

		if (explodeMultiline) {
			linesOut = LogFile.explodeMultiline(linesOut);
		}
		return linesOut;
	}


	public LogProc addFilter(StringCondition stringCondition) {
		if (L.isDebugEnabled()) {
			L.debug("Added StringCondition::{}", stringCondition);
		}
		lineConditions.add(stringCondition);
		return this;
	}

	public LogProc addLineCondition_ByKey(String key) {
		return addFilter(StringCondition.KeyContainsStringCondition.build(key, StringConditionType.CONTAINS));
	}

	public LogProc addLineCondition_ByRegex(String key) {
		return addFilter(StringCondition.KeyContainsStringCondition.build(key, StringConditionType.REGEX));
	}

	public LogProc addLineCondition_ByLastMinutesAgo(int lastMinutesAgo) {
		return addFilter(StringCondition.MsAgoStringCondition.build(new Date(), TimeUnit.MINUTES.toMillis(lastMinutesAgo), iGetLogDate));
	}

	public LogProc addLineCondition_BySingleDay(Date singleDay) {
		return addFilter(StringCondition.SingleDayStringCondition.build(singleDay, iGetLogDate));
	}

	public LogProc addLineCondition_ByBetweenDay(Date start, Date end) {
		return addFilter(StringCondition.BwDateStringCondition.build(start, end, iGetLogDate));
	}

	public LogProc addLineCondition_ByBetweenKeys(String _byKeyStart, String _byKeyEnd) {
		return addFilter(StringCondition.BwKeysStringCondition.build(_byKeyStart, _byKeyEnd));
	}

	//
	//
	public ArrayList<String> processFile(String file, String fileOut, LogGetterDate logGetterDate, boolean explodeMultiline) throws IOException {
		ArrayList<String> lines = processFile(Paths.get(file), logGetterDate, explodeMultiline);
		RW.write_(Paths.get(fileOut), lines, StandardOpenOption.CREATE);
		return lines;
	}

	public ArrayList<String> processFile(String file, LogGetterDate logGetterDate, boolean explodeMultiline) throws IOException {
		return processFile(Paths.get(file), logGetterDate, explodeMultiline);
	}

	public ArrayList<String> processFile(Path file, LogGetterDate logGetterDate, boolean explodeMultiline) throws IOException {
		List<String> lines = LogFile.parseLogLinesBlockAsString(file.toString(), logGetterDate);
		ArrayList<String> processedLines = process(lines);
		if (explodeMultiline) {
			processedLines = LogFile.explodeMultiline(processedLines);
		}
		return processedLines;
	}

	public ArrayList<String> process(String... lines) {
		return process(ARR.as(lines));
	}

	public ArrayList<String> process(List<String> lines) {
		if (X.empty(lineConditions)) {
			return new ArrayList<>(lines);
		}
		ArrayList<String> array = lines instanceof ArrayList ? (ArrayList) lines : new ArrayList<>(lines);
		return process(array, lineConditions);
	}

	public static ArrayList<String> process(ArrayList<String> linesIn, List<StringCondition> lineConditions) {
		if (L.isDebugEnabled()) {
			L.debug("Start Log Processor:" + Hu.NUMk(linesIn.size()) + ":" + lineConditions);
		}
		ArrayList<String> linesOut = new ArrayList<>();
		ArrayList<StringCondition> conditionsSingleLine = new ArrayList<>();
		ArrayList<StringCondition.IBetweenCondition> conditionsBetween = new ArrayList<>();
		for (int i = 0; i < lineConditions.size(); i++) {
			StringCondition stringCondition = lineConditions.get(i);
			if (stringCondition.isBetweenCondition()) {
				conditionsBetween.add((StringCondition.IBetweenCondition) stringCondition);
			} else {
				conditionsSingleLine.add(stringCondition);
			}
		}
		BetweenConditionState betweenConditionState = conditionsBetween.isEmpty() ? null : new BetweenConditionState(conditionsBetween);

		//go
		process(linesIn, linesOut, betweenConditionState, conditionsSingleLine);

		if (L.isTraceEnabled()) {
			L.trace("Start Log Processor finish");
		}

		return linesOut;
	}

	public static void process(ArrayList<String> linesIn, ArrayList<String> linesOut, BetweenConditionState betweenConditionState, List<StringCondition> lineConditionsSingly) {
		process(linesIn, linesOut, betweenConditionState, lineConditionsSingly, 0, 0);
	}

	public static void process(ArrayList<String> linesIn, ArrayList<String> linesOut, BetweenConditionState betweenConditionState, List<StringCondition> lineConditionsSingly_TOTAL, int countLineBefore, int countLineAfter) {

		List<StringCondition>[] two = StringCondition.splitIncludeExclude(lineConditionsSingly_TOTAL);
		List<StringCondition> lineConditionsSingly_INCLUDE = two[0];
		List<StringCondition> lineConditionsSingly_EXCLUDE = two[1];

		int lastLineIndexBwConditionEnd = -1;
		NEXT_LINE:
		for (int i = 0; i < linesIn.size(); i++) {
			String line = linesIn.get(i);
			if (line == null) {
				continue NEXT_LINE;
			} else if (X.blank(line)) {
				linesOut.add(line);
				continue NEXT_LINE;
			}
			if (i % 100_000 == 0) {
				L.info("lines:" + Hu.NUMk(i));
			}
			if (betweenConditionState != null) {
				boolean hasStart = betweenConditionState.hasStart(line);
				if (!hasStart) {
					continue NEXT_LINE;
				} else if (betweenConditionState.hasEnd(line)) {
					if (betweenConditionState.hasBwKeys()) {
						lastLineIndexBwConditionEnd = i;
						betweenConditionState.resetEnd();
					} else {
						break NEXT_LINE;
					}
				}
				if (lineConditionsSingly_TOTAL.isEmpty()) {
					linesOut.add(line);
				}
			}
			if (lineConditionsSingly_TOTAL.isEmpty()) {
				continue NEXT_LINE;
			}
			if (StringCondition.hasSomeOneCondition(lineConditionsSingly_EXCLUDE, line)) {
				//ok skip exlude condition
			} else if (StringCondition.hasSomeOneCondition(lineConditionsSingly_INCLUDE, line)) {
				//				if (false) {//wth?
				//					String linePrev = ArrItem.item(linesIn, i - 1, "") + SYM.NEWLINE;
				//					linePrev = "";
				//					String lineNext = SYM.NEWLINE + ArrItem.item(linesIn, i + 1, "");
				//					line = linePrev + line + lineNext;
				//				}
				linesOut.add(line);
			}
		}//END

		if (lastLineIndexBwConditionEnd > 0) {
			for (int i = linesOut.size() - 1; i >= lastLineIndexBwConditionEnd; i--) {
				linesOut.remove(i);
			}
		}
	}

	public static class BetweenConditionState {
		final List<StringCondition.IBetweenCondition> lineConditionsBetween;

		@Override
		public String toString() {
			return "BetweenConditionState{" +
					"...=" + lineConditionsBetween +
					", hasStart=" + hasStart +
					", hasEnd=" + hasEnd +
					'}';
		}

		public BetweenConditionState(List<StringCondition.IBetweenCondition> lineConditionsBetween) {
			IT.notEmpty(lineConditionsBetween);
			this.lineConditionsBetween = lineConditionsBetween;
		}

		boolean hasStart = false;
		boolean hasEnd = false;

		public boolean hasStart(String line) {
			if (hasStart) {
				return true;
			}
			//			else if (hasBwKeys_AllFirstLines()) {
			//				return hasStart = true;
			//			}
			return hasStart = StringCondition.hasSomeOneStartCondition(lineConditionsBetween, line);
		}

		private Boolean hasBwKeys, allFirstLines = null;

		public Boolean hasBwKeys() {
			return hasBwKeys != null && hasBwKeys;
		}

//		public Boolean hasBwKeys_AllFirstLines() {
//			return allFirstLines != null && allFirstLines;
//		}

		public boolean hasEnd(String line) {
			if (!hasStart) {
				return false;
			}
			if (hasEnd) {
				return true;
			}
			if (hasBwKeys == null) {
				StringCondition.BwKeysStringCondition bwKeysConditionWithKeys = StringCondition.BwKeysStringCondition.findBwKeysConditionWithKeys(lineConditionsBetween);
				hasBwKeys = bwKeysConditionWithKeys != null;
				//				if (hasBwKeys) {
				//					allFirstLines = !bwKeysConditionWithKeys.hasStartKey;
				//				}
			}
			return hasEnd = StringCondition.hasSomeOneEndCondition(lineConditionsBetween, line);
		}

		public void resetEnd() {
			hasEnd = false;
		}
	}

}
