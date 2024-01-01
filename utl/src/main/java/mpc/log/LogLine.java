package mpc.log;

import lombok.RequiredArgsConstructor;
import mpc.X;
import mpc.args.ARG;
import mpc.arr.Arr;
import mpc.arr.ArrItem;
import mpc.core.EN;
import mpc.exception.RequiredRuntimeException;
import mpc.str.USToken;

@RequiredArgsConstructor
public class LogLine {

	public final String line;
	private String[] _tokens;
	private Lev _level;

	public String[] tokens() {
//		return _tokens != null ? _tokens : (_tokens = SPLIT.splitFast(line, " "));
		return _tokens != null ? _tokens : (_tokens = parseTokens(line));
	}

	public String date() {
		return ArrItem.item(tokens(), 0, null);
	}

	public String level() {
		return ArrItem.item(tokens(), 1, null);
	}

	public static String[] parseTokens(String line, String[]... defRq) {
		try {
			return parseTokensImpl(line);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error parse line '%s' to log tokens", line), defRq);
		}
	}

	public static String[] parseTokensImpl(String line) {
		if (X.empty(line)) {
			return Arr.EMPTY_ARRAY;
		}
		String line0 = line;
		String date = USToken.first(line0, ' ');
		String lev = USToken.first(line0 = line0.substring(date.length() + 1), ' ');
		String thread = USToken.first(line0 = line0.substring(lev.length() + 1), ']');
		String clazz = USToken.first(line0 = line0.substring(thread.length() + 1), ']');
		String content = line0 = line0.substring(clazz.length() + 1);

		thread = thread.trim().substring(1).trim();
		clazz = clazz.trim().substring(1).trim();
		content = content.trim();

		return new String[]{date, lev, thread, clazz, content};
	}

	public String thread() {
		return ArrItem.item(tokens(), 2, null);
	}

	public String group() {
		return ArrItem.item(tokens(), 3, null);
	}

	public String content() {
		return ArrItem.item(tokens(), 4, null);
	}

	public Lev levelType() {
		return _level != null ? _level : (_level = EN.valueOf(level(), Lev.class, null));
	}


	public static LogLine of(String line, boolean... doValidWithThrow_returnNull_orNoCheckIfEmpty) {
		LogLine logLine = new LogLine(line);
		if (ARG.isNotDef(doValidWithThrow_returnNull_orNoCheckIfEmpty)) {
			return logLine;
		}
		try {
			logLine.tokens();
			return logLine;
		} catch (Exception ex) {
			if (ARG.isDefEqTrue(doValidWithThrow_returnNull_orNoCheckIfEmpty)) {
				throw ex;
			}
			return null;
		}
	}


}
