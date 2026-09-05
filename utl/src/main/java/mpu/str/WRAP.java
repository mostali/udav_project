package mpu.str;

import mpc.exception.FIllegalStateException;
import mpe.sql.SeqBuilder;
import mpu.X;
import mpu.core.ARG;

import java.util.Collection;

public class WRAP {

	public static String byPreAsMd(String text) {
		return "```\n" + text + "\n```";
	}

	public static String byPreAsHtml(String text) {
		return "<pre>\n" + text + "\n</pre>";
	}

	/**
	 * *************************************************************
	 * ---------------------------- WRAP --------------------------
	 * *************************************************************
	 */

	public static String unwrap(String src, String pfxSfx, String... defRq) {
		boolean hasAll = src.startsWith(pfxSfx) && src.endsWith(pfxSfx);
		return hasAll ? STR.removeStartEndQk(src, pfxSfx) : ARG.throwMsg(() -> X.f("Except pfxSfx '%s' in string: %s", pfxSfx, src), defRq);
	}

	public static String unwrapBody(String src, String rmmLeft, String rmmRight, String... defRq) {
		boolean hasAll = src.startsWith(rmmLeft) && src.endsWith(rmmRight);
		if (hasAll) {
			String val = STR.removeStart(src, rmmLeft, false);
			val = STR.removeEnd(val, rmmRight, false);
			return val;
		}
		return ARG.throwMsg(() -> X.f("Except pfx '%s' & sfx '%s' in string: %s", rmmLeft, rmmRight, src), defRq);
	}

	/**
	 * *************************************************************
	 * ---------------------------- WRAP --------------------------
	 * *************************************************************
	 */

	public static String byTag(Object data, String tag) {
		return "<" + tag + ">" + data + "</" + tag + ">";
	}

	public static String byTag(Object data, String tag, Object... tagAttrs) {
		if (tagAttrs.length == 0) {
			return byTag(data, tag);
		}
		return "<" + tag + " " + JOIN.argsBySpace(tagAttrs) + ">" + data + "</" + tag + ">";
	}

	public static String byJsonArray(Collection array, boolean wrapQuoteOnValues) {
		return "[" + SeqBuilder.generateSequence(array, wrapQuoteOnValues, false) + "]";
	}

	public static String byBracket(Object str) {
		return "[" + str + "]";
	}

	public static String byBracketRound(Object str) {
		return "(" + str + ")";
	}

	public static String byBracketFig(Object str) {
		return "{" + str + "}";
	}

	public static String wrap(Object str, String pt) {
		return pt + str + pt;
	}

	public static String wrapIfNot(CharSequence str, String pt, boolean... strict) {
		String vl = str.toString();
		boolean startsWith = vl.startsWith(pt);
		boolean endsWith = vl.endsWith(pt);
		if (startsWith && endsWith) {
			return vl;
		} else if (!startsWith && !endsWith) {
			return pt + vl + pt;
		}
		if (ARG.isDefEqTrue(strict)) {
			throw new FIllegalStateException("Except String with start&end or not part '%s', string:\n", pt, vl.length() < 200 ? vl : ToString.toStringSE(vl, 10));
		}
		return startsWith ? vl + pt : pt + vl;
	}

	public static String wrap(Object str, String start, String end) {
		return start + str + end;
	}

	public static String unwrapQuote(String val, String... defRq) {
		return unwrap(val, "\"", defRq);
	}
}
