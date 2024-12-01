package zk_page.node_state;

import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import utl_rest.StatusException;
import zk_os.db.net.WebUsr;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;

import java.util.List;

public class SecFileState<P> extends FileState<P> implements ISecState {

	public SecFileState(Pare sdn, String pathComStr, boolean isForm) {
		super(sdn, pathComStr, isForm);
	}

}
