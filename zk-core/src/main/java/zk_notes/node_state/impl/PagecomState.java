package zk_notes.node_state.impl;

import mpu.pare.Pare;
import zk_notes.node_state.ObjState;

public class PagecomState<P> extends ObjState<P> {

	public PagecomState(Pare sdn, String pathComStr) {
		super(sdn, pathComStr, false);
	}

	@Override
	public String spaceName() {
		return sdn.key();
	}

	@Override
	public String toObjId() {
		return sdn.key() + "/" + sdn.val() + "/" + objName();
	}
}
