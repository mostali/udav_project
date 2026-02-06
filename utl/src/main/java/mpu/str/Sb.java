package mpu.str;

import lombok.SneakyThrows;
import mpc.rfl.RFL;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

//StrinBuilder
public class Sb implements CharSequence {

	public static final String NL = System.lineSeparator();
	private final StringBuilder data;

	public Sb() {
		data = new StringBuilder();
	}

	public Sb(CharSequence ok) {
		data = new StringBuilder(ok);
	}

	public static Sb join(Object... args) {
		Sb sb = new Sb();
		for (Object arg : args) {
			sb.append(arg);
		}
		return sb;
	}

	/**
	 * *************************************************************
	 * --------------------------- StringBuilder --------------------------
	 * *************************************************************
	 */

	public static StringBuilder init(CharSequence initialString, int length) {
		return new StringBuilder(length).append(initialString);
	}

	public static StringBuilder init(CharSequence... join) {
		return initWithDel(null, join);
	}

	public static StringBuilder initWithDel(String del, Object... join) {
		switch (join.length) {
			case 0:
				return new StringBuilder();
			case 1:
				return new StringBuilder(String.valueOf(join[0]));
			default:
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < join.length; i++) {
					sb.append(join[i]);
					if (del != null && i != join.length - 1) {
						sb.append(STR.NL);
					}
				}
				return sb;
		}
	}

	public static Sb of(CharSequence... args) {
		Sb sb = new Sb();
		Arrays.stream(args).forEach(sb::NL);
		return sb;
	}

	public StringBuilder sb() {
		return data;
	}

	public StringBuilder to() {
		return sb();
	}

	public Sb append(Object str) {
		if (str instanceof CharSequence) {
			data.append(((CharSequence) str));
		} else {
			data.append(str);
		}
		return this;
	}

	@SneakyThrows
	public Sb appendtl(Object str, int tabLevel, boolean... addNewLine) {
		if (ARG.isDefEqTrue(addNewLine)) {
			NL();
		}
		if (str == null) {
			data.append(str);
			return this;
		}
		Method toString = RFL.method(str.getClass(), "toString", new Class[]{int.class}, false, true, false, null);
		if (toString == null) {
			TAB(tabLevel, str);
			return this;
		}
		Object vl = toString.invoke(str, tabLevel);
		data.append(vl);
		return this;
	}

	public Sb TAB(int tablevel) {
		if (tablevel > 0) {
			data.append(STR.TAB(tablevel));
		}
		return this;
	}

	public Sb TABNL(int tablevel, String str, Object... args) {
		TAB(tablevel, str, args);
		NL();
		return this;
	}

	public Sb TABNL(int tablevel, Object any) {
		TAB(tablevel, any);
		NL();
		return this;
	}

	public Sb TAB(int tablevel, String str, Object... args) {
		TAB(tablevel);
		appendf(str, args);
		return this;
	}

	public Sb TAB(int tablevel, Object str) {
		TAB(tablevel);
		data.append(str);
		return this;
	}

	public Sb append(Collection collection, String[] head, int tabLevel) {
		data.append(Rt.buildReport(collection, head, tabLevel));
		return this;
	}

	public Sb append(Map str, String[] head, int tabLevel) {
		data.append(Rt.buildReport(str, head, tabLevel));
		return this;
	}

	public Sb appendAll(Object... objs) {
		for (Object o : objs) {
			data.append(o);
		}
		return this;
	}

	public Sb appendf(CharSequence str, Object... args) {
		data.append(X.f(str, args));
		return this;
	}

	public Sb appendfl(CharSequence str, Object... args) {
		data.append(X.fl(str, args));
		return this;
	}

	@Override
	public int length() {
		return data.length();
	}

	@Override
	public char charAt(int index) {
		return data.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return data.subSequence(start, end);
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public Sb NONL(Object... line) {
		return NL(STR.noNL(String.valueOf(ARG.toDef(line))));
	}

	public Sb NLF(CharSequence str, Object... args) {
		appendf(str, args);
		append(NL);
		return this;
	}

	public Sb NL() {
		append(NL);
		return this;
	}

	public Sb NL(Object line) {
		if (ARG.isDef(line)) {
			append(ARG.toDef(line));
		}
		append(NL);
		return this;
	}
	public Sb NL(Object... lines) {
		for (Object line : lines) {
			NL(line);
		}
		return this;
	}
	public Sb deleteLastChar() {
		data.deleteCharAt(data.length() - 1);
		return this;
	}

	public Sb deleteEndIf(String sfx) {
		delete(data, false, sfx);
		return this;
	}

	public Sb deleteFirstIf(String pfx) {
		delete(data, true, pfx);
		return this;
	}

	public static void main(String[] args) {
		Sb sb = Sb.of("12345");
		Sb.delete(sb.data, false, "45s");
		Sys.exit(sb);
	}

	public static Sb of(CharSequence s) {
		return new Sb(s);
	}

	public static boolean delete(StringBuilder sb, boolean firstLast, String sfxPfx) {
		if (firstLast) {
			if (!startsWith(sb, sfxPfx)) {
				return false;
			}
			sb.delete(0, sfxPfx.length());
		} else {
			if (!endsWith(sb, sfxPfx)) {
				return false;
			}
			sb.delete(sb.length() - sfxPfx.length(), sb.length());
		}
		return true;
	}

	public static boolean endsWith(StringBuilder sb, String suffix) {
		return startsWith(sb, suffix, sb.length() - suffix.length());
	}

	public static boolean startsWith(StringBuilder sb, String prefix) {
		return startsWith(sb, prefix, 0);
	}

	public static boolean startsWith(StringBuilder sb, String prefix, int offset) {
		if (offset < 0 || sb.length() - offset < prefix.length()) {
			return false;
		}
		int len = prefix.length();
		for (int i = 0; i < len; ++i) {
			if (sb.charAt(offset + i) != prefix.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public String toStringLine(String replaceNl) {
		return toStringLine(this, replaceNl);
	}

	public static String toStringLine(Sb buildReport, String replaceNL) {
		return STR.clean(buildReport.toString(), true, true, true, replaceNL);
	}

	public Sb NL_() {
		switch (getLastChar()) {
			case '\n':
			case '\r':
				return this;
			default:
				NL();
				return this;
		}
	}

	public Sb deleteNL() {
		switch (getLastChar()) {
			case '\r':
				deleteLastChar();
				return this;
			case '\n':
				deleteLastChar();
				if (getLastChar() == '\r') {
					deleteLastChar();
				}
				return this;
			default:
				return this;
		}
	}

	private char getLastChar() {
		return data.charAt(data.length() - 1);
	}

	public void p(int tabLevel, Object obj) {
		append(STR.TAB(tabLevel));
		NL(obj);
	}

	public void p(Object s) {
		NL(s);
	}

	public void ptl(int tabLevel, Object s) {
		TABNL(tabLevel, s);
	}

	public void ptl(int tabLevel, String s, Object... args) {
		TABNL(tabLevel, s, args);
	}

	public boolean empty() {
		return sb().length() == 0;
	}
}
