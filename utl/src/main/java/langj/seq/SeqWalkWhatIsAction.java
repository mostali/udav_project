package langj.seq;

import langj.CH;
import langj.ECode;
import langj.PtException;
import mpu.IT;

import java.util.Optional;

public class SeqWalkWhatIsAction extends SeqWalk.SeqPt {

	public SeqWalkWhatIsAction(String code) {
		super(code);
	}

	private Optional<CH> ch = null;

	public static SeqWalkWhatIsAction of(String code) {
		return new SeqWalkWhatIsAction(code);
	}

	public CH findActionChar() {
		if (ch != null) {
			return ch.get();
		}
		walk();
		return IT.NN(ch).orElse(null);
	}

	@Override
	public State next(char CHAR, CH TYPE) {
		if (isFirst) {//must be letter
			ECode.checkSymbol(code, index_current, isFirst);
		}
		switch (TYPE) {
			case DIGIT:
			case LETTER:
			case DOLLAR:
			case __:
				ECode.checkSymbol(code, index_current, isFirst);
				if (isLast) {
					ch = Optional.ofNullable(null);
				}
				return State.NEXT;

//			case SPACE:
			case BRO:
			case POINT:
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
				ch = Optional.ofNullable(TYPE);
				return State.STOP;

		}

		throw new PtException(code, "Illegal char '%s' from index [%s]", TYPE, index_current);
	}
}
