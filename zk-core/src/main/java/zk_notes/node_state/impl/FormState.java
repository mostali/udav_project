package zk_notes.node_state.impl;

import mpe.call_msg.core.INodeID;
import mpu.pare.Pare;
import zk_notes.node_state.ObjState;

public class FormState<P> extends ObjState<P> implements INodeID {

	public FormState(Pare sdn, String pathComStr) {
		super(sdn, pathComStr, true);
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
		return spaceName() + "/" + pageName() + "/" + nodeName();
	}
}
