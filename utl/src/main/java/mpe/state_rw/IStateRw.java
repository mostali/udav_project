package mpe.state_rw;

import mpc.types.ruprops.RuProps;

import java.io.Serializable;
import java.util.Map;

public interface IStateRw<S> extends Serializable {
	void write(S state);

	S read(boolean... fresh);

	default boolean isEmpty() {
		S state = read();
		if (state == null) {
			return true;
		} else if (state instanceof RuProps) {
			return ((RuProps) state).readMap().isEmpty();
		} else if (state instanceof Map) {
			return ((Map) state).isEmpty();
		} else if (state instanceof CharSequence) {
			return ((CharSequence) state).length() == 0 || state.toString().equals("{}");
		}
		return false;
	}

	default void reset() {
		S state = read();
		if (state instanceof RuProps) {
			((RuProps) state).reset();
			return;
		}
		throw new UnsupportedOperationException("Override in " + getClass());
	}

	;
}
