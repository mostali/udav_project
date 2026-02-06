package langj.seq;

import langj.CH;
import langj.ECode;
import langj.ObjPt;
import langj.PtException;
import mpu.str.STRA;

@Deprecated
public class SeqWalkWhatIsFirstType extends SeqWalk.SeqPt {

	public SeqWalkWhatIsFirstType(String code) {
		super(code);
	}

	@Override
	public ObjPt pt() {
		if (this.objPt != null) {
			return this.objPt;
		} else if (getCode().startsWith("new ")) {
			ObjPt classPt = ECode.cutClass(code);
			ObjPt bodyArgs = classPt.getBodyObjPt();
			classPt.index_last += bodyArgs.index_last + 1;
			this.objPt = classPt;
			return this.objPt;
		}
		return super.pt();
	}

	@Override
	public State next(char CHAR, CH TYPE) {
		if (isFirst) {
			if (TYPE == null) {
				throw new PtException(code, "Code start with unknown char '" + CHAR + "'");
			}
			switch (TYPE) {
				case MINUS:
				case PLUS:
				case DIGIT: {
					this.objPt = ECode.NUM.cutNum(code);
					return State.STOP;
				}
				case SQ: {
					Integer firstLastSE = STRA.findFirstSE(code, CH.SQ.ch, CH.SQ.ch, true);
					this.objPt = new ObjPt(code, ECode.CHAR).ch(code.substring(firstLastSE));
					return State.STOP;
				}
				case DQ: {
					Integer firstLastSE = STRA.findFirstSE(code, CH.DQ.ch, CH.DQ.ch, true);
					this.objPt = new ObjPt(code, ECode.STRING).ch(code.substring(firstLastSE));
					return State.STOP;
				}
				case BRO: {
					this.objPt = ECode.cutBody(getCode());
					return State.STOP;
				}
				case LETTER:
					//ok
					break;
				default:
					throw new PtException(code, "illegal code (by first char)");
			}
		}

		switch (TYPE) {
			case DIGIT:
			case LETTER:
			case DOLLAR:
			case __:
				ECode.checkSymbol(code, index_current, isFirst);
				return State.APPEND;
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
//					objPt = new ObjPt(code, ECode.LINK, HostType.NULL, ctx).link(sbLink.toString(), true);
				return State.STOP;
			default:
				throw new PtException(code, "Illegal char not found, index:" + index_current);
		}
	}
}
