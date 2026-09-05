package mpe.cmsg.ns;

import mpu.pare.Pare;

public interface IPageID extends ISpaceID {

	default Pare<String, String> sdn() {
		return Pare.of(spaceName(), pageName());
	}

	String pageName();

}
