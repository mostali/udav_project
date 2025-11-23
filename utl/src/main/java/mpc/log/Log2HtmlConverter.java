package mpc.log;

import mpu.X;
import mpc.exception.FIllegalStateException;
import mpu.str.JOIN;
import mpu.core.RW;
import mpc.html.EHtml5;
import mpu.str.TKN;
import mpc.str.condition.LogGetterDate;
import mpv.byteunit.ByteUnit;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Log2HtmlConverter {

	public static String handleLine(String line, LogGetterDate logGetterDate) {
		Date date = logGetterDate.getDateFrom(line, null);
		if (date == null) {
			return line;
		}
		List<String> lineTokens = new ArrayList<>();

		/**
		 * DATE
		 */
		String[] two = TKN.two(line, " ");
		String dateToken = EHtml5.b.with(two[0]);
		lineTokens.add(dateToken);

		/**
		 * LEVEL
		 */
		two = TKN.two(two[1], " ", null);
		if (two == null) {
			lineTokens.add(two[1]);
			return JOIN.allBySpace(lineTokens);
		}

		String token1 = getTagTokenLevel(two[0]);
		lineTokens.add(token1);

		/**
		 * OTHER PART
		 */
		lineTokens.add(two[1]);
		return JOIN.allBySpace(lineTokens);
	}

	private static String getTagTokenLevel(String token1) {
		String color = null;
		switch (token1) {
			case "DEBUG":
				color = "green";
				break;
			case "TRACE":
				color = "gray";
				break;
			case "INFO":
//				color = "red";
				break;
			case "FATAL":
			case "ERROR":
				color = "red";
				break;
			case "WARN":
				color = "blue";
				break;
			default:
				color = null;
		}
		if (color == null) {
			return token1;
		}
		return EHtml5.b.withTag(token1, "style='color:" + color + "'");
	}

	public static String fromFile(Path logFile, int count, boolean reverse, int maxMb, LogGetterDate logGetterDate, Object... tagAttrs) {

		if (X.sizeOf(logFile, ByteUnit.MB) >= maxMb) {
			throw new MaxHtmlLogException("File more that %sMb", maxMb);
		}

		List<String> lines = RW.readLines(logFile, count);

		if (reverse) {
			Collections.reverse(lines);
		}

		String html = fromLines(logGetterDate, lines, tagAttrs);
		return html;
	}

	public static String fromLines(LogGetterDate logGetterDate, List<String> lines, Object... tagAttrs) {
		String html = lines.stream().map(l -> EHtml5.div.withTag(Log2HtmlConverter.handleLine(l, logGetterDate), tagAttrs)).collect(Collectors.joining());
		return html;
	}

	public static class MaxHtmlLogException extends FIllegalStateException {
		public MaxHtmlLogException(String message, Object... args) {
			super(message, args);
		}
	}
}
