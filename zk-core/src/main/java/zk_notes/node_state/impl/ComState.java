package zk_notes.node_state.impl;

import mpe.call_msg.core.INodeID;
import mpu.pare.Pare;
import zk_notes.node_state.ObjState;

public class ComState<P> extends ObjState<P> implements INodeID {

	public ComState(Pare sdn, String pathComStr) {
		super(sdn, pathComStr, false);
	}

	@Override
	public String pageName() {
		return sdn.val();
	}

	@Override
	public String spaceName() {
		return sdn.key();
	}

	@Override
	public String nodeName() {
		return objName();
	}

	@Override
	public String toObjId() {
		return sdn.key() + "/" + sdn.val() + "/" + objName();
	}
}
