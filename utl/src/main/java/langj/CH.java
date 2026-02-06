package langj;

import mpu.core.ARG;
import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;

public enum CH {
	LETTER, DIGIT, __('_'),
	EQ('='), MINUS('-'), PLUS('+'), STAR('*'), SPACE(' '),
	SQ('\''), DQ('"'), RQ('`'),
	POINT('.'), COMMA(','),
	SEMICOLON(';'),
	DOLLAR('$'),
	BRO('('), BRC(')'), BQO('['), BQC(']'), BFO('{'), BFC('}'), LT('<'), GT('>'),
	RSLASH('/'), LSLASH('\\'),
	;

	public final Character ch;

	CH() {
		this(null);
	}

	CH(Character ch) {
		this.ch = ch;

	}

	public static CH of(char ch, CH... defRq) {
		for (CH jch : values()) {
			if (jch.matches(ch)) {
				return jch;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Undefined CH type <" + ch + ">");
	}

	public static boolean isJavaSymbol(char ch, boolean... isStart) {
		return ARG.isDefEqTrue(isStart) ? isJavaSymbolStart(ch) : Character.isJavaIdentifierPart(ch);
	}

	public static boolean isJavaSymbolStart(char ch) {
		return Character.isJavaIdentifierStart(ch);
	}


	public boolean matches(char ch) {
		switch (this) {
			case DIGIT:
				return Character.isDigit(ch);
			case LETTER:
				return Character.isLetter(ch);
			case SPACE:
				return Character.isWhitespace(ch);
			default:
				return this.ch == ch;
		}
	}

	public boolean matches(String str, int i) {
		return matches(str.charAt(i));
	}

	public static boolean matchesAny(String str, CH ch, int i) {
		return ch.matches(str.charAt(i));
	}

	public static boolean matchesAnd(String str, CH ch, int i, CH ch2, int i2) {
		return ch.matches(str.charAt(i)) && ch2.matches(str.charAt(i2));
	}

	public boolean matchesTypeSE(String str) {
		switch (this) {
			case SQ:
			case DQ:
			case RQ:
//				return US.matchesSE(str, ch, true);
			case BRO:
			case BQO:
			case BFO:
//				return US.matchesSE(str, ch, pare(), true);
			case DIGIT:
				switch (str.length()) {
					case 0:
						return false;
					default:
						CH of = CH.of(str.charAt(0), null);
						if (of == null) {
							return false;
						}
						switch (of) {
							case MINUS:
							case PLUS:
							case DIGIT:
								//ok
								break;
							default:
								return false;
						}
						return matches(str.charAt(str.length() - 1));
				}
		}
		throw new NI(this + ":" + str);
	}

	public Character pare(Character... defRq) {
		switch (this) {
			case SQ:
			case DQ:
			case RQ:
				return ch;
			case BRO:
			case BFO:
			case BQO:
				return BRC.ch;
			case LT:
				return GT.ch;
			default:
				return ARG.toDefRq(defRq);
		}
	}
}
