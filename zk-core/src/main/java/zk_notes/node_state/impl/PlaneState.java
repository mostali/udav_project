package zk_notes.node_state.impl;

import mpe.call_msg.core.NodeID;
import mpu.pare.Pare;
import zk_notes.node_state.ObjState;

public class PlaneState<P> extends ObjState<P> {

	public PlaneState(Pare sdn, String pathComStr) {
		super(sdn, pathComStr, false);
	}

	@Override
	public String spaceName() {
		return sdn.key();
	}

	@Override
	public String toObjId() {
		return sdn.key() + "/" + NodeID.PAGE_INDEX_ALIAS + "/";
	}
}
