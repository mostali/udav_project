package zk_notes.node_state.libs;

import mpu.pare.Pare;
import zk_notes.node_state.FormState;

public class PlaneState<P> extends FormState<P> {

	public PlaneState(Pare sdn, String pathComStr, boolean isForm) {
		super(sdn, pathComStr, isForm);
	}
}
