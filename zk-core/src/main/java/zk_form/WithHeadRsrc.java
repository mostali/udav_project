package zk_form;

import zk_form.head.IHeadRsrc;

public interface WithHeadRsrc {

	default IHeadRsrc[] getHeadRsrcs() {
		return null;
	}


}
