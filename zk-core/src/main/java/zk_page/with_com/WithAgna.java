package zk_page.with_com;

import zk_rmm.AgnaCom;

public interface WithAgna {

	default AgnaCom getAgnaOrCreate() {
		AgnaCom first = AgnaCom.findFirst(null);
		return first != null ? first : new AgnaCom();
	}


}
