package zk_notes.node_state.impl;

import mpe.cmsg.ns.INodeID;
import mpu.pare.Pare;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.ObjState;
import zk_os.core.Sdn;

public class FormState<P> extends ObjState<P> implements INodeID {

	public FormState(Pare sdn, String pathComStr) {
		super(sdn, pathComStr, true);
	}

	public static FormState ofName(Sdn sdn, String name) {
		return AppStateFactory.forForm(sdn, name);
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
