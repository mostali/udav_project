package mpe.call_msg.core;

import mpu.pare.Pare;

public interface IPageID extends ISpaceID {

	default Pare<String, String> sdn() {
		return Pare.of(spaceName(), pageName());
	}

	String pageName();

}
