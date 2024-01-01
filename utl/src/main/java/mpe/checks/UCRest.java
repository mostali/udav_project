package mpe.checks;

import mpc.X;
import mpc.exception.RestStatusException;
import mpc.str.STR;

//Check Utility ( for REST )

public class UCRest {

	public static void notEmpty400(String value, Object... message) {
		if (X.empty(value)) {
			throw RestStatusException.C400(STR.formatAllOr("Value is empty", message));
		}
	}
}
