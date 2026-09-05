package langj;

import langj.seq.SeqWalkWhatIsAction;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.IT;
import mpc.types.abstype.AbsType;
import mpc.exception.WrongLogicRuntimeException;
import mpc.exception.NI;
import mpc.rfl.RFL;
import mpe.str.URx;
import mpu.Sys;
import mpu.X;
import mpu.str.STRA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public enum ECode {
	NEW,
	NUM(URx.NUM_START),
	CHAR, STRING, BODY_RB,
	ACTION,
	LINK, METHOD, FIELD, CLASS;

	public static final Logger L = LoggerFactory.getLogger(ECode.class);

	public static final String NEW_ = "new ";

	public boolean isValid(String pattern) {
		switch (this) {
			case NEW:
				return X.notEmpty(pattern) && pattern.startsWith(NEW_);
			default:
				throw NI.stop(this + ":" + pattern);

		}
	}

	private final Pattern regex;

	public Pattern regex() {
		return regex;
	}

	ECode() {
		this(null);
	}

	ECode(String regex) {
		this.regex = regex == null ? null : Pattern.compile(regex, Pattern.DOTALL);
	}

	public static void checkSymbol(String str, int index, boolean isFirst) {
		if (!ARR.isIndex(index, str)) {
			throw new PtException(str, "Symbol not found by index [%s] from String.len(%s)\n%s", index, str.length(), str);
		}
		char ch = str.charAt(index);
		boolean res = CH.isJavaSymbol(ch, isFirst);
		if (!res) {
			throw new PtException(str, "Symbol [%s] is illegal for java name (isFirst=%s)", ch, isFirst);
		}
	}

	public static void notNull(AbsType host, String method) {
		if (host == null || host.val() == null) {
			throw new PtException(method, "Host object is null:" + host);
		}
	}

	public static void notEmptyCode(String code) {
		if (X.blank(code)) {
			throw new PtException(code, "Code body is empty or blank '%s'", code);
		}
	}

	public static void main(String[] args) {
//		P.p(ECode.NUM.firstObj("777a"));
		ObjPt objPt = ECode.cutClass("new ath('1')", 0, false);
//		ECode obj = (ECode) objPt;
		Sys.exit(objPt.getBodyObjPt().getBodyUnwrapString());
		Sys.p(ECode.NEW.firstObj("new Host()"));
	}

	/**
	 * *************************************************************
	 * ----------------------------- TYPE --------------------------
	 * *************************************************************
	 */

	public static ECode firstType(String code) {
		return firstType(code, null, new Ctx());
	}

	public static ECode firstType(String code, Ctx ctx) {
		return firstType(code, null, ctx);
	}

	public static ECode firstType(String code, Host host, Ctx ctx) {
		IT.notEmpty(code);
		if (code.startsWith("new ")) {
			return NEW;
		}
		char fch = code.charAt(0);
		CH firstChar = CH.of(fch, null);
		if (firstChar == null) {
			throw new PtException(code, "Code start with unknown char '" + fch + "'");
		}
		switch (firstChar) {
			case MINUS:
			case PLUS:
			case DIGIT:
				return ECode.NUM;
			case SQ:
				return CHAR;
			case DQ:
				return STRING;
			case BRO:
				return BODY_RB;
			case LETTER:
				return ACTION;
			default:
				throw new PtException(code, "illegal code (by first char)");
		}
	}

	/**
	 * *************************************************************
	 * ----------------------------- OBJ ---------------------------
	 * *************************************************************
	 */

	public ObjPt firstObj(String code, ObjPt... defRq) {
		return firstObj(code, null, new Ctx(), defRq);
	}

	public ObjPt firstObj(String code, Ctx context, ObjPt... defRq) {
		return firstObj(code, Host.NULL, context, defRq);
	}

	public ObjPt firstObj(String code, Host host, Ctx ctx, ObjPt... defRq) {
		IT.notNull(host);
		IT.notEmpty(code);

//		switch (this) {
//			case ACTION:
//				host.checkNullValue();
//				break;
//		}
		switch (this) {
			case NUM:
				host.checkNeedNullValue();
				return cutNum(code);

			case CHAR: {
				Integer firstLastSE = STRA.findFirstSE(code, CH.SQ.ch, CH.SQ.ch, true);
				return new ObjPt(code, this).ch(code.substring(firstLastSE));
			}

			case STRING: {
				Integer firstLastSE = STRA.findFirstSE(code, CH.DQ.ch, CH.DQ.ch, true);
				return new ObjPt(code, this).string(code.substring(firstLastSE));
			}

			case BODY_RB:
				return cutBody(code);

			case NEW: {
				IT.isTrue(code.startsWith("new "));
				ObjPt classPt = cutClass(code);
				ObjPt bodyArgs = classPt.getBodyObjPt();//CHECK
				classPt.index_last += bodyArgs.index_last + 1;
				return classPt;
			}

			case ACTION: {
				SeqWalkWhatIsAction act = SeqWalkWhatIsAction.of(code);
				CH ch = act.findActionChar();
				if (ch == null) {//IS LAST
					if (host.isNull()) { //class or link
						if (ctx.isLink(code)) {
							return new ObjPt(code, ECode.LINK, host, ctx).link(code);
						}
						throw new PtException(code, "Code is final. Host is NULL. May be only link, but not found in ctx");
//						Class clazz = UNRefl.clazz_native(code, true, true, true, null);
//						return new ObjPt(code, ECode.CLASS, host, ctx).clazz(clazz);
					} else {
						return new ObjPt(code, ECode.FIELD, host, ctx).field(code);
					}
				}
				switch (ch) {
					case BRO://method
						ObjPt methodPt = cutMethodName(code, host, ctx);
						ObjPt bodyArgs = methodPt.getBodyObjPt();//CHECK
						methodPt.index_last += bodyArgs.index_last + 1;
						return methodPt;
					case POINT: {
						String first = act.first(true);
						if (host.isNull()) {
							if (ctx.isLink(first)) {
								return new ObjPt(code, ECode.LINK, host, ctx).link(first);
							}
							ObjPt classPt = cutClass(code, null);
							if (classPt != null) {
								return classPt;
							}
							throw new PtException(code, "Code typeof '%s' not found pattern.", this);
						}
						return new ObjPt(code, ECode.FIELD, host, ctx).field(first);
					}
					//case BRO:
					//case POINT:
					case COMMA:
					case LT:
					case GT:
					case EQ:
					case STAR:
					case PLUS:
					case MINUS:
					case RSLASH:
					case BQO:
					case SEMICOLON:
						throw new NI(this);
				}

			}
//			case METHOD_RMM: {
//				ObjPt methodPt = cutMethodName(code, host, ctx);
//				ObjPt bodyArgs = methodPt.getBodyObjPt();//CHECK
//				methodPt.index_last += bodyArgs.index_last + 1;
//				return methodPt;
//			}
//			case LINK: {
//				if (ctx == null) {
//					throw new PtException(code, "Code typeof '%s' required ctx.", this);
//				} else if (!ctx.containsKey(code)) {
//					throw new PtException(code, "Code typeof '%s' required link in ctx.", this);
//				} else {
////					return new ObjPt(code, this).link(code, true);
//					return cutLink(code, 0, ctx);
//				}
//			}
//			case LINK_or_SIMPLECLASS_FINAL:
//				if (host.isNull()) {
//					if (ctx == null) {
//						throw new PtException(code, "host & ctx is null. What is '%s' ?", this);
//					} else if (!ctx.containsKey(code)) {
//						throw new PtException(code, "host is null, but ctx contains't it '%s'", this);
//					} else {
//						return new ObjPt(code, this).link(code, true);
//					}
//				} else {
//					return new ObjPt(code, this, host, ctx).field(code, true);
//				}

//			case LINK_or_SIMPLECLASS_NOTFINAL:
//				int pointInd = code.indexOf('.');
//				String first = code.substring(0, pointInd);
//				if (ctx != null && ctx.containsKey(first)) {
//					return new ObjPt(code, this).link(first, true);
//				}
//				StringBuilder sbCode = null;
//				for (int i = pointInd; i < code.length(); i++) {
//					char CHAR = code.charAt(i);
//					CH cht = CH.of(CHAR);
//					switch (cht) {
//						case BRO:
//							if (sbCode == null) {
//								throw new PtException(code, "illegal postion BRO, from index [%s]. Not found first char ( must be java char )", i);
//							}
//							//TODOs
//						case DIGIT:
//						case LETTER:
//						case DOLLAR:
//							checkSymbol(code, i, i == pointInd + 1);
//							if (sbCode == null) {
//								sbCode = new StringBuilder(code.substring(0, pointInd - 1).trim());
//							}
//							sbCode.append(CHAR);
//							continue;
//						case POINT:
//							String pack_or_class = sbCode.toString();
//							Class clazz = UNRefl.clazz(pack_or_class, null);
//							if (clazz == null) {
//								sbCode.append(CHAR);
//								continue;
//							}
//							return new ObjPt(code, this).clazz(clazz).index(i - 1);
//						default:
//							throw new PtException(code, "Class not found, index:" + i);
//
//					}
//				}
		}

		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new PtException(code, "ECode '%s' not found", this);

	}

	public ObjPt cutNum(String code) {
		String num = URx.find(regex(), code, 1, null);
		if (num == null) {
			throw new PtException(code, "First object not found");
		}
		return new ObjPt(code, this).num(num);
	}

	/**
	 * *************************************************************
	 * ---------------------------- LINK -----------------------
	 * *************************************************************
	 */
	public static ObjPt cutLink(String code, int fromIndex, Ctx ctx, ObjPt... defRq) {
		try {
			return cutLinkRq(code, fromIndex, ctx);
		} catch (Exception e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			if (e instanceof PtException) {
				throw e;
			} else {
				throw new PtException(code, e);
			}
		}
	}

	private static ObjPt cutLinkRq(String code, int fromIndex, Ctx ctx) {
		notEmptyCode(code);
		StringBuilder sbLink = new StringBuilder();
		for (int i = fromIndex; i < code.length(); i++) {
			char CHAR = code.charAt(i);
			CH cht = CH.of(CHAR);
			switch (cht) {
				case DIGIT:
				case LETTER:
				case DOLLAR:
				case __:
					checkSymbol(code, i, fromIndex == i);
					sbLink.append(CHAR);
					continue;
				case SPACE:
				case COMMA:
				case POINT:
				case LT:
				case GT:
				case EQ:
				case STAR:
				case PLUS:
				case MINUS:
				case SEMICOLON:
				case RSLASH:
				case BQO:
					ObjPt linkPt = new ObjPt(code, ECode.LINK, Host.NULL, ctx).link(sbLink.toString());
					return linkPt;
				default:
					throw new PtException(code, "Illegal char not found, index:" + i);
			}
		}
		throw new PtException(code, "Link not found");
	}

	/**
	 * *************************************************************
	 * ---------------------------- METHOD -----------------------
	 * *************************************************************
	 */
	public static ObjPt cutMethodName(String code, Host host, Ctx ctx, ObjPt... defRq) {
		return cutMethodName(code, 0, host, ctx, defRq);
	}

	public static ObjPt cutMethodName(String code, int firstCharIndexName, Host _host, Ctx ctx, ObjPt... defRq) {
		try {
			return cutMethodNameRq(code, firstCharIndexName, _host, ctx);
		} catch (Exception e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			if (e instanceof PtException) {
				throw e;
			} else {
				throw new PtException(code, e);
			}
		}
	}

	private static ObjPt cutMethodNameRq(String code, int firstCharIndexName, Host _host, Ctx ctx) {
		notEmptyCode(code);
		StringBuilder sbMethod = new StringBuilder();
		for (int i = firstCharIndexName; i < code.length(); i++) {
			char CHAR = code.charAt(i);
			CH cht = CH.of(CHAR);
			switch (cht) {
				case DIGIT:
				case LETTER:
				case DOLLAR:
				case __:
					checkSymbol(code, i, firstCharIndexName == i);
					sbMethod.append(CHAR);
					continue;
				case BRO:
					ObjPt method = new ObjPt(code, ECode.METHOD, _host, ctx).method(sbMethod.toString());
					return method;
				default:
					throw new PtException(code, "Method not found, index:" + i);
			}
		}
		throw new PtException(code, "Method not found");
	}

	/**
	 * *************************************************************
	 * ---------------------------- CLASS -----------------------
	 * *************************************************************
	 */
	public static ObjPt cutClass(String code, ObjPt... defRq) {
		return cutClass(code, 0, defRq);
	}

	public static ObjPt cutClass(String code, int firstCharIndexName, ObjPt... defRq) {
		return cutClass(code, firstCharIndexName, true, defRq);
	}

	public static ObjPt cutClass(String code, int firstCharIndexName, boolean loadClass, ObjPt... defRq) {
		try {
			return cutClassRq(code, firstCharIndexName, loadClass);
		} catch (Exception e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			if (e instanceof PtException) {
				throw e;
			} else {
				throw new PtException(code, e);
			}
		}
	}

	private static ObjPt cutClassRq(String code, int firstCharIndexName, boolean loadClass) {
		notEmptyCode(code);
		boolean isNew = ECode.NEW.isValid(code);
		if (isNew) {
			if (firstCharIndexName != 0) {
				throw new PtException(code, "IllegalState for NEW and firstCharIndexName!=0");
			} else {
				firstCharIndexName = 4;
			}
		}
		StringBuilder sbClass = new StringBuilder();
		for (int i = firstCharIndexName; i < code.length(); i++) {
			char CHAR = code.charAt(i);
			CH cht = CH.of(CHAR);
			switch (cht) {
				case SPACE:
					continue;
				case DIGIT:
				case LETTER:
				case __:
				case DOLLAR:
					checkSymbol(code, i, firstCharIndexName == i);
					sbClass.append(CHAR);
					continue;
				case BRO: {
					if (!isNew) {
						throw new PtException(code, "Class not found, check '%s' from index [%s]. Found BRO", cht, i);
					}
					String className = sbClass.toString();
					ObjPt objPt = new ObjPt(code, ECode.NEW);
					if (!loadClass) {
						return objPt.clazz(className, i - 1);
					}
					Class clazz = RFL.clazz(className, null);
					if (clazz == null) {
						throw new PtException(code, "Class not found, check '%s' from index [%s]. Found BRO, but class not found '%s'", cht, i, className);
					}
					return objPt.clazz(clazz, i - 1);
				}
				//may be new, or static class
				case POINT: {
					if (isNew) {
						sbClass.append(CHAR);
						continue;
					}
					String pack_or_class = sbClass.toString();
					Class clazz;
					if (pack_or_class.indexOf('.') == -1) {
						clazz = RFL.clazz_native(pack_or_class, true, true, true, null);
					} else {
						clazz = RFL.clazz(pack_or_class, null);
					}
					if (clazz == null) {
						sbClass.append(CHAR);
						continue;
					}
					return new ObjPt(code, ECode.CLASS).clazz(clazz, i - 1);
				}
				default:
					throw new PtException(code, "Class not found, check '%s' from index [%s]", cht, i);
			}
		}
		throw new PtException(code, "Class not found");
	}

	/**
	 * *************************************************************
	 * ---------------------------- BODY -----------------------
	 * *************************************************************
	 */
	public static ObjPt cutBody(String code, ObjPt... defRq) {
		return cutBody(code, 0, defRq);
	}

	public static ObjPt cutBody(String code, int firstCharIndexName, ObjPt... defRq) {
		try {
			return cutBodyRq(code, firstCharIndexName);
		} catch (Exception e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			if (e instanceof PtException) {
				throw e;
			} else {
				throw new PtException(code, e);
			}
		}
	}

	public static ObjPt cutBodyRq(String code, int firstCharIndexName) {
		if (code.length() < 2) {
			throw new PtException(code, "Code body length must be more that 1");
		} else if (code.charAt(0) != '(') {
			throw new PtException(code, "Code body must be start with '('");
		}
		StringBuilder sbBody = null;
		for (int i = firstCharIndexName; i < code.length(); i++) {
			char CHAR = code.charAt(i);
			CH cht = CH.of(CHAR);
			switch (cht) {
				case BRO:
					if (sbBody == null) {
						sbBody = new StringBuilder();
					} else {
						CharSequence innerBody = cutBody(code.substring(i)).body;
						i += innerBody.length();
					}
					break;
				case BRC:
					if (sbBody == null) {
						throw new WrongLogicRuntimeException("Code body must be start with '(' , code:" + code);
					}
					return new ObjPt(code, ECode.BODY_RB).body(sbBody, i);
				case SQ:
				case DQ: {
					String startSymbol = code.substring(i);
					ObjPt sqCode = ECode.CHAR.firstObj(startSymbol);
					i += sqCode.index_last;
					continue;
				}
				//				case COMMA:
				//					if (isSingleBodyEntity) {
				//						throw new PtException(code, "Code body must be single body entity");
				//					}
				default:
					sbBody.append(CHAR);
					break;
			}
		}

		throw new PtException(code, "Code body must be out with close symbol '('");

	}


}
