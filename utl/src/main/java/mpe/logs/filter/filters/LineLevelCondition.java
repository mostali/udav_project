package mpe.logs.filter.filters;

import lombok.RequiredArgsConstructor;
import mpc.log.Lev;
import mpc.log.LogLine;
import mpc.str.condition.StringCondition;
import mpe.logs.filter.ILogFilter;

import java.util.Map;

@RequiredArgsConstructor
public class LineLevelCondition extends StringCondition implements ILogFilter {

	private final Map<Lev, Boolean> state;
	private boolean isNullThat = true;

	@Override
	public boolean isEqualsLine(String line) {
		LogLine logLine = LogLine.of(line, false);
		if (logLine == null) {
			return isNullThat;
		}
		Boolean bool = state.get(logLine.levelType());
		return bool == null ? isNullThat : bool;
	}

	@Override
	public StringCondition toFilter() {
		return this;
	}

	@Override
	public String toStringFnPart() {
		return ILogFilter.toStringFnPart(state, isNullThat);
	}
}
