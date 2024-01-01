package mpc.types.tks.anypt;

import lombok.Builder;
import mpc.*;
import mpc.args.ARG;
import mpc.ERR;
import mpc.exception.FIllegalStateException;
import mpc.str.JOIN;
import mpc.types.tks.cmt.Cmd;
import mpc.types.tks.cmt.Cmd7;

import java.util.List;

@Builder
//@RequiredArgsConstructor
public class AnyPat {

	final String original;

	public String original() {
		return original;
	}

	private Cmd7 cmd;

	public Cmd7 cmd(String... del) {
//		Cmd7 cmd = null;
		if (cmd != null) {
			return cmd;
		} else if (ARG.isNotDef(del)) {
			throw new FIllegalStateException("Lazy init cmd7 need delimiter");
		}
		String[] c7 = Cmd.toArgs(original, ERR.NN(ARG.toDef(del)), false);
		return cmd != null ? cmd : (cmd = Cmd7.of7(original, c7, null, null, null, null, null, null, null));
	}

	public final List<String> errors;

	public AnyPat throwIsNoOk() {
		if (isValid()) {
			return this;
		}
		throw new ERR.CheckException(JOIN.NL(errors));
	}

	@Override
	public String toString() {
		return isValid() ? toValidString() : original();
	}

	public String toValidString() {
		return original();
	}

	public boolean isValid() {
		return X.empty(errors);
	}

}
