package mpe.str;

import mpu.Sys;
import mpc.exception.WhatIsTypeException;
import mpu.str.Regexs;
import mpu.str.TKN;
import mpc.str.sym.SYMJ;
import org.apache.commons.lang3.StringUtils;

public class RemoverLineSeparator {
	public static class Token {
		final String line;
		final Token next;
		Token prev;

		public Token(String line, Token next) {
			this.line = line;
			this.next = next;
		}

		@Override
		public String toString() {
			return "Token{" +
				   SYMJ.ARROW_RIGHT_SPEC + line +
				   "\n" +
				   SYMJ.ARROW_RIGHT_SPEC + next.line
					;
		}

		public static Token build(String text) {
			if (text.isEmpty()) {
				return new Token(null, null);
			}
			String[] lines = text.split("\n");
			switch (lines.length) {
				case 1:
					return new Token(lines[0], null);
				case 2:
					Token next = new Token(lines[1], null);
					Token cur = new Token(lines[0], next);
					next.prev = cur;
					return cur;
			}
			Token last = null;
			for (int i = lines.length - 1; i >= 0; i--) {
				if (i == 0) {
					Token cur = new Token(lines[i], last);
					return cur;
				} else if (i == lines.length - 1) {
					last = new Token(lines[i], null);
					continue;
				}
				Token cur = new Token(lines[i], last);
				last.prev = cur;
				last = cur;
			}
			return last;
		}

		public boolean end() {
			return next == null;
		}

		public boolean NULL() {
			return line == null;
		}

		public boolean EMPTY() {
			return line != null && line.isEmpty();
		}

		public boolean BLANK() {
			return line != null && StringUtils.isBlank(line);
		}

		public boolean NO_EMPTY() {
			return line != null && !line.isEmpty();
		}

		public boolean WORD() {
			return line.matches(mpu.str.Regexs.WORD_RU);
		}

		public boolean NO_WORD() {
			return !WORD();
		}

		public boolean __SPACE() {
			return NO_EMPTY() && line.charAt(0) == ' ';
		}

		public boolean SPACE__() {
			return NO_EMPTY() && lastChar() == ' ';
		}

		private char lastChar() {
			return line.charAt(line.length() - 1);
		}

		private char firstChar() {
			return line.charAt(0);
		}

		public String trim() {
			return line.trim();
		}

		public boolean __P() {
			if (EMPTY()) {
				return false;
			}
			return Character.isUpperCase(line.charAt(0));
		}

		public boolean P__() {
			if (EMPTY()) {
				return false;
			}
			char lastChar = lastChar();
			switch (lastChar) {
				case '.':
				case '!':
				case '?':
					return true;
				default:
					return false;
			}
		}

		public boolean __LC() {
			if (EMPTY()) {
				return false;
			}
			return Character.isLowerCase(firstChar());
		}

		public boolean LC__() {
			if (EMPTY()) {
				return false;
			}
			return Character.isLowerCase(lastChar());
		}

		public boolean NUM() {
			return line.matches("\\s*\\d+\\s*");
		}

		public boolean __NW() {
			return !__W();
		}

		public boolean __W() {
			String first = firstWord();
			return first.matches(mpu.str.Regexs.WORD_RU);
		}

		public boolean Nw__() {
			String first = firstWord();
			return first.matches(mpu.str.Regexs.WORD_RU);
		}

		public boolean W__() {
			String first = firstWord();
			return first.matches(Regexs.WORD_RU);
		}

		public String firstWord() {
			return TKN.first(line, ' ', line);
		}

		public String lastWord() {
			return TKN.last(line, ' ', line);
		}

		public Token add(StringBuilder sb) {
			return add(sb, false, 0);
		}

		public Token add(StringBuilder sb, boolean trim) {
			return add(sb, trim, 0);
		}

		public Token add(StringBuilder sb, boolean trim, int nl) {
			if (nl > 0) {
				sb.append("\n");
			}
			sb.append((trim ? line.trim() : line));
			if (nl < 0) {
				sb.append("\n");
			}
			return this;
		}

		public TState state() {
			return TState.of(this);
		}
	}

	Token first = null;
	Token cur = null;
	Token pv = null;
	StringBuilder sb;

	public static String removeLineSeparatorNice(String text) {
		return new RemoverLineSeparator().go(text).sb.toString();
	}

	private void next() {
		pv = cur;
		cur = cur.next;
	}

	private void fill(Token pv, Token cur) {
		fill(sb, pv, cur);
		next();
	}

	public RemoverLineSeparator go(String text) {
		print(text);
		sb = new StringBuilder();
		first = Token.build(text);
		cur = first;
		pv = null;
		while (cur != null) {
			fill(pv, cur);
		}
//		if (cur != null) {
//			next(true);
//		}
		return this;
	}

	public RemoverLineSeparator print(String text) {
		if (!print) {
			return null;
		}
		Sys.p(text + "\n_______________________");
		first = Token.build(text);
		cur = first;
		pv = null;
		while (cur != null) {
			print(cur);
			next();
		}
		Sys.p("_______________________");

//		if (cur != null) {
//			next(true);
//		}
		return this;
	}

	public static final boolean print = false;

	private static void print(Token cur) {
		if (print) {
			return;
		}
		if (cur == null) {
			Sys.p("null");
		} else {
			Sys.p(cur.line + SYMJ.ARROW_RIGHT_SPEC + cur.state());

		}
	}

	enum TState {
		EMP, BLANK, NUM,
		SPACE__, P__, LC__, NW__,
		__LC, __P, __NW;

		static TState of(Token token) {
			for (TState st : values()) {
				if (st.is(token)) {
					return st;
				}
			}
			throw new WhatIsTypeException(token.line);
		}

		boolean is(Token token) {
nextState:
			switch (this) {
				case EMP:
					return token.EMPTY();
				case BLANK:
					return token.BLANK();
				case NUM:
					return token.NUM();
				case SPACE__:
					return token.SPACE__();
				case __P:
					return token.__P();
				case P__:
					return token.P__();
				case LC__:
					return token.LC__();
				case __LC:
					return token.__LC();
				case __NW:
					return token.__NW();
				case NW__:
					return token.Nw__();
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	private static Token fill(StringBuilder sb, Token prev, Token cur) {
		if (prev == cur || prev == null) {
			return cur.add(sb);
		}
		TState sCur = TState.of(cur);
		TState sPrev = TState.of(prev);
		switch (sCur) {
			case BLANK:
			case EMP:
				return null;
			case NUM:
				return null;
			default:
				if (sPrev == TState.NUM && prev.prev != null) {
					sPrev = TState.of(prev.prev);
				}
				switch (sPrev) {
					case __P:
						return cur.add(sb, false, 1);
					case SPACE__:
						return cur.add(sb, false);
					case P__:
						return cur.add(sb, false, 1);
					case LC__:
						sb.append(" ");
						return cur.add(sb, false);
					case NUM:
						return cur.add(sb);
					case __NW:
						return cur.add(sb, false, 1);
					case NW__:
						return cur.add(sb, false, TState.__NW.is(cur) ? 1 : 0);
					default:
//						print(prev, cur, sPrev, sCur);
						return cur.add(sb);
				}

		}
	}

}
