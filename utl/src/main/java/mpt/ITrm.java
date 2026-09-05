package mpt;

import mpu.IT;

import java.util.Map;

public interface ITrm<U extends IaUser> {

	default void checkKey(TrmRq cmd) {
		IT.state(cmd.cmd7().key().equals(key()));
	}

	default String key() {
		return getClass().getAnnotation(TrmEntity.class).value()[0];
	}
	default Map<String, ITrmCmd> cmds(Map<String, ITrmCmd>... defRq) {
		return TRM.cmds(key(), defRq);
	}


	default ITrmCmd trmcmd(String cmd, ITrmCmd... defRq) {
		return TRM.trmCmd(key(), cmd, defRq);
	}


	default ITrm trm(ITrm... defRq) {
		return TRM.trm(key(), defRq);
	}

	default TrmRsp exe(U usr, String cmd) {
		try {
			return exe_(usr, cmd);
		} catch (TrmRsp throwable) {
			throw throwable;
		} catch (Throwable throwable) {
			return TrmRsp.FAIL(throwable);
		}
	}

	default TrmRsp exe(U usr, TrmRq cmd) {
		try {
			return exe_(usr, cmd);
		} catch (TrmRsp throwable) {
			throw throwable;
		} catch (Throwable throwable) {
			return TrmRsp.FAIL(throwable);
		}
	}

	default TrmRsp exe_(U usr, String cmd) throws Throwable {
		return exe_(usr, TrmRq.fromTrm(cmd));
	}

	default TrmRsp exe_(U usr, TrmRq trmRequest) throws Throwable {
		return TRM.execute0(this, usr, trmRequest);
	}

}
