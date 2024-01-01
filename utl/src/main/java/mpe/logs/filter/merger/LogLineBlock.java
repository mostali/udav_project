package mpe.logs.filter.merger;

import mpc.X;
import mpc.args.ARG;
import mpc.arr.Arr;
import mpc.log.L;
import mpc.log.LogLine;
import mpc.str.JOIN;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class LogLineBlock implements Comparable<LogLineBlock> {
	final String srcFile;
	final Date date;
	String[] lines;

	public LogLineBlock(String srcFile, Date date, String[] lines) {
		this.srcFile = srcFile;
		this.date = date;
		this.lines = lines;
	}

	private String linesStr;

	public String toStringLines(boolean... fresh) {
		return linesStr == null || ARG.isDefEqTrue(fresh) ? (linesStr = JOIN.NL(lines)) : linesStr;
	}

	public String toStringLinesAs(boolean explodeMultiline) {
		return explodeMultiline ? JOIN.NL(lines) : JOIN.TAB(lines);
	}

	public String toString() {
		return "(" + X.sizeOf0(lines) + ")" + JOIN.NL(lines);
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, lines);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof LogLineBlock)) {
			return false;
		}
		LogLineBlock logLine = (LogLineBlock) o;
		return Objects.equals(date, logLine.date) && Arrays.equals(lines, logLine.lines);
	}

	@Override
	public int compareTo(@NotNull LogLineBlock o) {
		if (o == this) {
			return 0;
		}
		int i = date.compareTo(o.date);
		if (i != 0) {
			return i;
		}
		i = srcFile.compareTo(o.srcFile);
		if (i != 0) {
			return i;
		}
		i = toStringLines().compareTo(o.toStringLines(false));
		if (i != 0) {
			return i;
		}
		if (L.isWarnEnabled()) {
			L.warn("How to compare two lines:\n{}\n{}", this, o);
		}
		return 0;//wth
	}

	public void addLine(String line) {
		lines = Arr.addArrayElement(lines, line);
	}

	public LogLine toLogLine() {
		return LogLine.of(JOIN.NL(lines));
	}
}
