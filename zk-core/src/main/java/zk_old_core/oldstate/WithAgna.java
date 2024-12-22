package zk_old_core.oldstate;

import zk_old_core.coms.AgnaCom;

public interface WithAgna {

	default AgnaCom getAgnaOrCreate() {
		AgnaCom first = AgnaCom.findFirst(null);
		return first != null ? first : new AgnaCom();
	}


}
