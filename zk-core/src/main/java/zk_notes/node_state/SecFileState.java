package zk_notes.node_state;

import mpu.pare.Pare;

public abstract class SecFileState<P> extends FileState<P> implements ISecState {

	public SecFileState(Pare sdn, String pathComStr, boolean isForm) {
		super(sdn, pathComStr, isForm);
	}

}
