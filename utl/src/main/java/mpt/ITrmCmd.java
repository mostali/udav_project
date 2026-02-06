package mpt;

import mpu.IT;

public interface ITrmCmd<U extends IaUser> {

	default void checkKey(TrmRq cmd, String key) {
		IT.state(cmd.cmd7().key().equals(key));
	}


	default TrmRsp exe_(U usr, String cmd) throws Throwable {
		return exe_(usr, TrmRq.fromTrm(cmd));
	}

	TrmRsp exe_(U usr, TrmRq cmd) throws Throwable;

}
