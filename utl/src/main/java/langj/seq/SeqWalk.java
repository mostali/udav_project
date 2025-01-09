package langj.seq;

import langj.*;
import lombok.Getter;
import mpu.core.ARG;
import mpu.IT;
import mpc.exception.WhatIsTypeException;

public abstract class SeqWalk {

	@Getter
	public final String code;
	@Getter
	public final int fromIndex;

	public SeqWalk(String code, int fromIndex) {
		this.code = IT.notBlank(code.trim());
		this.fromIndex = fromIndex;
	}

	private final StringBuilder seq = new StringBuilder();

	public StringBuilder seq() {
		return seq;
	}

	protected Boolean isFirst = null;
	protected Boolean isLast = null;
	protected int index_current = 0;

	public String first(boolean... cut) {
		int offset = ARG.isDefEqTrue(cut) ? 0 : 1;
		return code.substring(0, index_current + offset);
	}


	public StringBuilder walk() {
stopCycle:
		for (int i = fromIndex; i < code.length(); i++) {
			isFirst = fromIndex == i;
			isLast = code.length() - 1 == i;
			index_current = i;

			char CHAR = code.charAt(i);
			CH cht = CH.of(CHAR);

			State state = next(CHAR, cht);
			switch (state) {
				case APPEND:
					seq.append(CHAR);
				case NEXT:
					continue;
				case STOP:
					break stopCycle;
				default:
					throw new WhatIsTypeException(state);
			}
		}
		return seq;
	}

	public abstract State next(char CHAR, CH TYPE);

	public enum State {
		STOP, APPEND, NEXT
	}

	public interface IObj {
		default ECode type() {
			return pt().ecode();
		}

		ObjPt pt();

		Host host();

		Ctx ctx();
	}

	public static abstract class SeqPt extends SeqWalk implements SeqWalk.IObj {
		private final Host host;
		private final Ctx ctx;

		protected ObjPt objPt = null;
		protected ECode ecode = null;

		@Override
		public Host host() {
			return host;
		}

		@Override
		public Ctx ctx() {
			return ctx;
		}

		@Override
		public ObjPt pt() {
			if (this.objPt != null) {
				return objPt;
			}
			walk();
			IT.NN(this.objPt);
			return this.objPt;
		}

		public SeqPt(String code) {
			this(code, 0, Host.NULL, new Ctx());
		}

		public SeqPt(String code, int from, Host host, Ctx ctx) {
			super(code, from);
			this.host = host;
			this.ctx = ctx;
		}

	}


//	public class SeqWalk_LINK extends SeqPt {
//
//		public SeqWalk_LINK(String code) {
//			super(code);
//		}
//
//		@Override
//		public State next(char CHAR, CH TYPE) {
//			CH cht = CH.of(CHAR);
//			switch (cht) {
//				case DIGIT:
//				case LETTER:
//				case DOLLAR:
//				case _:
//					ECode.checkSymbol(code, index_current, fromIndex == index_current);
//					return State.APPEND;
//				case SPACE:
//				case COMMA:
//				case POINT:
//				case LT:
//				case GT:
//				case EQ:
//				case STAR:
//				case PLUS:
//				case MINUS:
//				case SEMICOLON:
//				case RSLASH:
//				case BQO:
//					this.objPt = new ObjPt(code, ECode.LINK, HostType.NULL, ctx()).link(seq().toString(), true);
//					return State.STOP;
//				default:
//					throw new PtException(code, "Illegal char not found, index:" + i);
//			}
//		}
//	}
}