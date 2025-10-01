package zk_notes.node_state.libs;

import mpu.pare.Pare;
import zk_notes.node_state.FormState;

public class ComState<P> extends FormState<P> {

	public ComState(Pare sdn, String pathComStr) {
		super(sdn, pathComStr, false);
	}
}
