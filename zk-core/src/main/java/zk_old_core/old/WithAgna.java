package zk_old_core.old;

import zk_old_core.control_old.AgnaCom;

public interface WithAgna {

	default AgnaCom getAgnaOrCreate() {
		AgnaCom first = AgnaCom.findFirst(null);
		return first != null ? first : new AgnaCom();
	}


}
