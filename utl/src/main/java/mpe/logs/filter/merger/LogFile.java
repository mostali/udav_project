package mpe.logs.filter.merger;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpu.X;
import mpu.core.ARR;
import mpe.core.ERR;
import mpu.core.RW;
import mpc.log.L;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpc.str.condition.LogGetterDate;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LogFile implements Comparable<LogFile> {

	public static boolean REVERSE_VIEW_LOG = true;

	final String file;
	final LogGetterDate logGetterDate;
	private Optional<LogLineBlock[]> lines;

	@SneakyThrows
	public static void writeFile(String dstFile, LogLineBlock[] logLines, boolean explodeMultiline) {
		Path dstPath = Paths.get(dstFile);
		if (false) {
			List<String> lines = Arrays.stream(logLines).flatMap(l -> Stream.of(l.getLines())).collect(Collectors.toList());
			RW.writeLines(dstPath, lines);
		} else {
			UFS.MKFILE.createFileRmIfExist(dstPath, true);
			for (LogLineBlock logLine : logLines) {
				RW.write_Append_(dstPath, logLine.toStringLinesAs(explodeMultiline) + "\n");
			}
		}
	}

	public static LogLineBlock[] convertToLinesBlock(String file, LogGetterDate logGetterDate, List<String> lines, boolean explodeMultiline) {
		return lines.stream().map(l -> {
			String[] linesBlock = explodeMultiline ? SPLIT.argsBy(l, STR.NL) : ARR.of(l.replace(STR.NL, STR.TB));
			LogLineBlock logLineBlock = new LogLineBlock(file, logGetterDate.getDateFrom(l), linesBlock);
			return logLineBlock;
		}).toArray(LogLineBlock[]::new);
	}

	public static ArrayList<String> explodeMultiline(ArrayList<String> linesOut) {
		return (ArrayList<String>) SPLIT.allByInnerNL(linesOut);
	}

	public LogLineBlock[] getLines() {
		if (lines == null) {
			lines = Optional.ofNullable(parseLogLinesBlock(file, logGetterDate));
		}
		return lines.orElse(new LogLineBlock[0]);
	}

	@SneakyThrows
	public static List<String> parseLogLinesBlockAsString(String file, LogGetterDate logGetterDate) {
		LogLineBlock[] logLineBlocks = LogFile.parseLogLinesBlock(file, logGetterDate);
		List<String> lines = Arrays.stream(logLineBlocks).map(lb -> lb.toStringLines()).collect(Collectors.toList());
		return lines;
	}

	@SneakyThrows
	public static LogLineBlock[] parseLogLinesBlock(String file, LogGetterDate logGetterDate) {
		try {
			if (L.isInfoEnabled()) {
				L.info("Start parseLogLinesBlock:" + file);
			}
			LogLineBlock[] logLineBlocks = parseLogLinesBlock0(file, logGetterDate);
			if (L.isInfoEnabled()) {
				L.info("End parseLogLinesBlock OK:" + X.sizeOf(logLineBlocks) + ":" + file);
			}
			return logLineBlocks;
		} catch (Throwable err) {
			if (L.isInfoEnabled()) {
				L.info("End parseLogLinesBlock FAIL:" + file + ":" + ERR.getMessageWithType(err));
			}
			throw err;
		}
	}

	private static LogLineBlock[] parseLogLinesBlock0(String file, LogGetterDate logGetterDate) throws FileNotFoundException {

		List<String> lines = RW.readLines(new FileReader(file));
//		if (REVERSE_VIEW_LOG) {
//			Collections.reverse(lines);
//		}
		Date[] firstLastDateFULL = LogGetterDate.findFirstLastDate(lines, logGetterDate, null);
		if (firstLastDateFULL == null) {
			return new LogLineBlock[0];
		}
		List<LogLineBlock> lines0 = new LinkedList<>();
		LogLineBlock newLine = null;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			Date dateFrom = logGetterDate.getDateFrom(line, null);
			if (dateFrom == null) {//it simple text
				if (newLine == null) { //first lines without date mark - that get first date - 1ms
					newLine = new LogLineBlock(file, new Date(firstLastDateFULL[0].getTime() - 1), new String[]{line});
				} else {
					newLine.addLine(line);
				}
				continue;
			}
			//found mark date
			if (newLine == null) {//is new
				newLine = new LogLineBlock(file, dateFrom, new String[]{line});
				continue;
			}
			//it new lineBlock, store prev
			lines0.add(newLine);
			newLine = new LogLineBlock(file, dateFrom, new String[]{line});

		}
		if (newLine != null) {//add last
			lines0.add(newLine);
		}
		LogLineBlock[] array = lines0.toArray(new LogLineBlock[lines0.size()]);
		return array;
	}

	@Override
	public int compareTo(@NotNull LogFile o) {
		return lines.get()[0].compareTo(o.lines.get()[0]);
	}
}
