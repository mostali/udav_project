package mpe.logs.filter.filters;

import mpu.IT;
import mpc.exception.FIllegalStateException;
import mpu.core.ARR;
import mpu.core.RW;
import mpc.fs.UF;
import mpc.str.condition.StringCondition;
import mpe.logs.filter.ILogFilter;

import java.util.HashSet;
import java.util.Set;

public class KeyLineCondition extends StringCondition implements ILogFilter {
	private final String file;
	private Set<String> stopPhrase;
	private final boolean includeExclude;
	private final boolean ignoreCase;

	@Override
	public boolean exclude() {
		return !includeExclude;
	}

	public KeyLineCondition(Set<String> stopPhrase, boolean includeExclude, boolean ignoreCase) {
		this.stopPhrase = (Set<String>) IT.NE(stopPhrase, "set StopPhrase's");
		this.includeExclude = includeExclude;
		this.ignoreCase = ignoreCase;
		this.file = null;
	}

	public KeyLineCondition(String file, boolean includeExclude, boolean ignoreCase) {
		this.file = IT.isFileExist(file);
		this.includeExclude = includeExclude;
		this.ignoreCase = ignoreCase;
	}

	public static KeyLineCondition of(boolean includeExclude, boolean ignoreCase, Set<String> stopPhrases, String file) {
		if (stopPhrases != null) {
			return new KeyLineCondition(stopPhrases, includeExclude, ignoreCase);
		} else if (file != null) {
			return new KeyLineCondition(file, includeExclude, ignoreCase);
		}
		throw new FIllegalStateException("set stopPhrases or file");
	}

	@Override
	public boolean isEqualsLine(String line) {
		boolean hasStopLine = getStopPhrase().stream().filter(p -> ARR.contains(line, p, ignoreCase)).findAny().isPresent();
		return hasStopLine;
	}

	public Set<String> getStopPhrase() {
		return stopPhrase != null ? stopPhrase : (stopPhrase = new HashSet<>(RW.readLines(file)));
	}

	@Override
	public StringCondition toFilter() {
		return this;
	}

	@Override
	public String toStringFnPart() {
		String part = "";
		if (stopPhrase != null) {
			part = stopPhrase.hashCode() + "";
		} else {
			part = UF.f(file).lastModified() / 1000 + "";
		}
		return "sl-" + part + "" + (ignoreCase ? "ic" : "IC");
	}

}
