package mpc.str.sym;

import mpu.Sys;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import mpc.exception.WhatIsTypeException;
import mpu.str.Sb;
import mpu.str.STR;

//Separator
public enum SEP {
	STAR, DOLLAR, SPACE, UNDER, DOT, COMMA, DASH, COLON, SCOLON, EQ, DQ, SQ, RQ, DOG, HOUSE, GT, LT, EXMARK, QSTMARK;


	//	public static void main(String[] args) {
	//		//---------------------------------------------------------------
	//		//---------------------------------------------------------------
	//		//---------------------------------------------------------------
	//
	//		DOG._p_("BORDER 1");
	//		COLON.p_("LAST 1");
	//		SCOLON._p("FIRST 1");
	//		HOUSE.__p("BEFORE 1");
	//		HOUSE.p__("AFTER 1");
	//		STAR.__p__("BETWEEN 1");
	//
	//		DOG._p2_("BORDER 2");
	//		COLON.p2_("LAST 2");
	//		SCOLON._p2("FIRST 2");
	//		HOUSE.__p2("BEFORE 2");
	//		HOUSE.p2__("AFTER 2");
	//		STAR.__p1__("BETWEEN 1");
	//
	//		DOG._p3_("BORDER 3");
	//		COLON.p3_("LAST 3");
	//		SCOLON._p3("FIRST 3");
	//		HOUSE.__p3("BEFORE 3");
	//		HOUSE.p3__("AFTER 3");
	//		STAR.__p2__("BETWEEN 2");
	//
	//	}

	public static final String NEWLINE = SYM.NEWLINE;

	public static final int DEF_LENGTH_HEAD_LINE = 60;
	public static final int DEF_LENGTH_HEAD_LINE_HALF = DEF_LENGTH_HEAD_LINE / 2;

	public static String toString(String defIfNull, Object title, Object... args) {
		if (title == null) {
			return defIfNull;
		}
		String vl = title.toString();
		if (X.empty(args)) {
			return vl;
		}
		return String.format(vl, args);
	}

	public static StringBuilder _LINE_(String info, boolean... print) {
		StringBuilder sb = new StringBuilder();
		String repeat = STR.repeat("^", 30);
		sb.append(repeat).append(" > ").append(info).append(" < ").append(repeat);
		if (ARG.isDefEqTrue(print)) {
			Sys.p(sb);
		}
		return sb;
	}

	public String toLineSize(int length) {
		char val = SYM.getCharByFieldName(name(), SYMJ.SKULL.charAt(0));
		return toLineSize(val, length);
	}

	public static String toLineSize(char char_, int... len) {
		return STR.repeat(char_, len.length == 0 ? DEF_LENGTH_HEAD_LINE : len[0]);
	}

	/**
	 * *************************************************************
	 * ------------------- generators -----------------
	 * *************************************************************
	 */

	private CharSequence toSepL(int headFactor, SimplePosition position, boolean firstNewLine, Object title, Object... args) {
		String titleStr = toString("", title, args);
		IT.isNumber(headFactor, 10, IT.EQ.LE, "Max head type=7");
		headFactor = headFactor <= 0 ? 1 : headFactor;
		String line = toLineSize(DEF_LENGTH_HEAD_LINE / headFactor / (position == SimplePosition.BORDER ? 2 : 1));
		switch (position) {
			case FIRST:
				return Sb.init(firstNewLine ? NEWLINE : "", line, " ", titleStr);
			case LAST:
				return Sb.init(firstNewLine ? NEWLINE : "", titleStr, " ", line);
			case BEFORE:
				return Sb.init(firstNewLine ? NEWLINE : "", line, SYM.NEWLINE, titleStr);
			case AFTER:
				return Sb.init(firstNewLine ? NEWLINE : "", titleStr, SYM.NEWLINE, line);
			case BORDER:
				if (titleStr.isEmpty()) {
					return Sb.init(firstNewLine ? NEWLINE : "", line, line);
				}
				return Sb.init(firstNewLine ? NEWLINE : "", line, " ", titleStr, " ", line);
			default:
				throw new WhatIsTypeException(position);
		}
	}

	private CharSequence toSepB(int countLine, boolean firstNewLine, Object title, Object... args) {
		String titleStr = toString("", title, args);
		char ch = SYM.getCharByFieldName(name(), SYMJ.SKULL.charAt(0));
		String mlineHalf = STR.repeat(ch, DEF_LENGTH_HEAD_LINE_HALF);
		Object bord = titleStr.isEmpty() ? "" : ' ';
		String mline = Sb.init(mlineHalf).append(bord).append(titleStr).append(bord).append(mlineHalf).toString();
		if (countLine <= 0) {
			return mline;
		}
		String line = STR.repeat(ch, mline.length());
		StringBuilder body = new StringBuilder((firstNewLine ? 2 : 0) + (DEF_LENGTH_HEAD_LINE * countLine) + DEF_LENGTH_HEAD_LINE + titleStr.length() + 2 + (countLine * 2));
		if (firstNewLine) {
			body.append(NEWLINE);
		}
		for (int c = 0; c < countLine; c++) {
			body.append(line).append(NEWLINE);
		}
		body.append(mline).append(NEWLINE);
		for (int c = 0; c < countLine; c++) {
			body.append(line).append(NEWLINE);
		}
		return body;
	}

	/**
	 * *************************************************************
	 * ------------------------------ p ----------------------------
	 * *************************************************************
	 */

	public void _p_(Object title, Object... args) {
		Sys.p(_str_(title, false, args));
	}

	public String _str_(Object title, Object... args) {
		return _str_(title, true, args).toString();
	}

	public String _str_(Object title, boolean firstNewLine, Object... args) {
		return toSepL(1, SimplePosition.BORDER, firstNewLine, title, args).toString();
	}

	public void _p(Object title, Object... args) {
		Sys.p(_str(title, false, args));
	}

	public String _str(Object title, Object... args) {
		return _str(title, true, args);
	}

	public String _str(Object title, boolean firstNewLine, Object... args) {
		return toSepL(1, SimplePosition.FIRST, firstNewLine, title, args).toString();
	}

	public void p_(Object title, Object... args) {
		Sys.p(str_(title, false, args));
	}

	public String str_(Object title, Object... args) {
		return str_(title, true, args);
	}

	public String str_(Object title, boolean firstNewLine, Object... args) {
		return toSepL(1, SimplePosition.LAST, firstNewLine, title, args).toString();
	}


	public void __p(Object title, Object... args) {
		Sys.p(__str(title, false, args));
	}

	public String __str(Object title, Object... args) {
		return __str(title, true, args);
	}

	public String __str(Object title, boolean firstNewLine, Object... args) {
		return toSepL(1, SimplePosition.BEFORE, firstNewLine, title, args).toString();
	}


	public void p__(Object title, Object... args) {
		Sys.p(str__(title, false, args));
	}

	public String str__(Object title, Object... args) {
		return str__(title, true, args);
	}

	public String str__(Object title, boolean firstNewLine, Object... args) {
		return toSepL(1, SimplePosition.AFTER, firstNewLine, title, args).toString();
	}

	/**
	 * *************************************************************
	 * ----------------------------- p2 ----------------------------
	 * *************************************************************
	 */

	public void _p2_(Object title, Object... args) {
		Sys.p(_str2_(title, false, args));
	}

	public String _str2_(Object title, Object... args) {
		return _str2_(title, true, args);
	}

	public String _str2_(Object title, boolean firstNewLine, Object... args) {
		return toSepL(2, SimplePosition.BORDER, firstNewLine, title, args).toString();
	}

	public void _p2(Object title, Object... args) {
		Sys.p(_str2(title, false, args));
	}

	public String _str2(Object title, Object... args) {
		return _str2(title, true, args);
	}

	public String _str2(Object title, boolean firstNewLine, Object... args) {
		return toSepL(2, SimplePosition.FIRST, firstNewLine, title, args).toString();
	}

	public void p2_(Object title, Object... args) {
		Sys.p(str2_(title, false, args));
	}

	public String str2_(Object title, Object... args) {
		return str2_(title, true, args);
	}

	public String str2_(Object title, boolean firstNewLine, Object... args) {
		return toSepL(2, SimplePosition.LAST, firstNewLine, title, args).toString();
	}

	public void __p2(Object title, Object... args) {
		Sys.p(__str2(title, false, args));
	}

	public String __str2(Object title, Object... args) {
		return __str2(title, true, args);
	}

	public String __str2(Object title, boolean firstNewLine, Object... args) {
		return toSepL(2, SimplePosition.BEFORE, firstNewLine, title, args).toString();
	}

	public void p2__(Object title, Object... args) {
		Sys.p(str2__(title, false, args));
	}

	public String str2__(Object title, Object... args) {
		return str2__(title, true, args);
	}

	public String str2__(Object title, boolean firstNewLine, Object... args) {
		return toSepL(2, SimplePosition.AFTER, firstNewLine, title, args).toString();
	}

	/**
	 * *************************************************************
	 * -------------------------------- p3 -------------------------
	 * *************************************************************
	 */

	public void _p3_(Object title, Object... args) {
		Sys.p(_str3_(title, false, args));
	}

	public String _str3_(Object title, Object... args) {
		return _str3_(title, true, args);
	}

	public String _str3_(Object title, boolean firstNewLine, Object... args) {
		return toSepL(3, SimplePosition.BORDER, firstNewLine, title, args).toString();
	}

	public void _p3(Object title, Object... args) {
		Sys.p(_str3(title, false, args));
	}

	public String _str3(Object title, Object... args) {
		return _str3(title, true, args);
	}

	public String _str3(Object title, boolean firstNewLine, Object... args) {
		return toSepL(3, SimplePosition.FIRST, firstNewLine, title, args).toString();
	}

	public void p3_(Object title, Object... args) {
		Sys.p(str3_(title, false, args));
	}

	public String str3_(Object title, Object... args) {
		return str3_(title, true, args);
	}

	public String str3_(Object title, boolean firstNewLine, Object... args) {
		return toSepL(3, SimplePosition.LAST, firstNewLine, title, args).toString();
	}

	public void __p3(Object title, Object... args) {
		Sys.p(__str3(title, false, args));
	}

	public String __str3(Object title, Object... args) {
		return __str3(title, true, args);
	}

	public String __str3(Object title, boolean firstNewLine, Object... args) {
		return toSepL(3, SimplePosition.BEFORE, firstNewLine, title, args).toString();
	}

	public void p3__(Object title, Object... args) {
		Sys.p(str3__(title, false, args));
	}

	public String str3__(Object title, Object... args) {
		return str3__(title, true, args);
	}

	public String str3__(Object title, boolean firstNewLine, Object... args) {
		return toSepL(3, SimplePosition.AFTER, firstNewLine, title, args).toString();
	}

	public String __str3__(Object title) {
		return __str3__(title, false);
	}

	public String __str3__(Object title, boolean firstNewLine, Object... args) {
		return toSepB(3, firstNewLine, title, args).toString();
	}

	public void __p3__(Object title, Object... args) {
		Sys.p(__str3__(title, false, args));
	}

	/**
	 * *************************************************************
	 * ------------------------------ __p__ --------------------------
	 * *************************************************************
	 */

	public void __p__(Object title, Object... args) {
		Sys.p(__str__(title, false, args));
	}

	public String __str__(Object title, boolean firstNewLine, Object... args) {
		return toSepB(0, firstNewLine, title, args).toString();
	}

	public String __str__(Object title, Object... args) {
		return __str__(title, true, args);
	}

	public void __p1__(Object title, Object... args) {
		Sys.p(__str1__(title, false, args));
	}

	public String __str1__(Object title, boolean firstNewLine, Object... args) {
		return toSepB(1, firstNewLine, title, args).toString();
	}

	public String __str1__(Object title) {
		return __str1__(title, false);
	}

	public void __p2__(Object title, Object... args) {
		Sys.p(__str2__(title, false, args));
	}

	public String __str2__(Object title) {
		return __str2__(title, false);
	}

	public String __str2__(Object title, boolean firstNewLine, Object... args) {
		return toSepB(2, firstNewLine, title, args).toString();
	}

	/**
	 * *************************************************************
	 * ------------------------- TEMPLATES -------------------------
	 * *************************************************************
	 */

	public static void __STAR__(Object headmessage, Object... args) {
		STAR.__p__(headmessage, args);
	}

	public static void _EQ(Object headmessage, Object... args) {
		EQ._p(headmessage, args);
	}

	public static void _EQ2(Object headmessage, Object... args) {
		EQ._p2(headmessage, args);
	}

	public static void EQ__(Object headmessage, Object... args) {
		EQ.p__(headmessage, args);
	}

	/**
	 * *************************************************************
	 * ------------------------------ PAGE -------------------------
	 * *************************************************************
	 */
	public static void printPage(String pageTitle, String pageSource) {
		Sys.p(getPage(pageTitle, pageSource));
	}

	public static String getPage(String title, String pageSource) {
		return Sb.init(title, pageSource.length() + (2 * DEF_LENGTH_HEAD_LINE) + (3 * 2)).append(SYM.NEWLINE).
				append(DASH.toLineSize(DEF_LENGTH_HEAD_LINE)).append(SYM.NEWLINE).
				append(pageSource).append(SYM.NEWLINE).
				append(HOUSE.toLineSize(DEF_LENGTH_HEAD_LINE)).toString();
	}

	/**
	 * *************************************************************
	 * ------------------------------ STUFF -------------------------
	 * *************************************************************
	 */

	//
	//	USep
	//	public void invoke(USep sep, String methodName) {
	//		USep proxyInstance = (USep) Proxy.newProxyInstance(
	//				USep.class.getClassLoader(),
	//				new Class[]{USep.class},
	//				(proxy, method, methodArgs) -> {
	//					if (method.getName().equals("p")) {
	//						return 42;
	//					} else {
	//						throw new UnsupportedOperationException(
	//								"Unsupported method: " + method.getName());
	//					}
	//				});
	//
	//		return proxyInstance.p1("hello");
	//	}

	public enum SimplePosition {
		FIRST, LAST, BEFORE, AFTER, BORDER
	}

}
