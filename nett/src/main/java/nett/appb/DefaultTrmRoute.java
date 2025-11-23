package nett.appb;

import mpc.exception.CleanMessageRuntimeException;
import mpu.X;
import mpt.TRM;
import mpt.TrmRq;
import mpt.TrmRsp;
import botcore.RouteAno;
import nett.msg.TgMsg;

@RouteAno
public class DefaultTrmRoute extends TgDefaultRoute {

	@Override
	protected TgMsg afterUpdateMessage(String msg) {
		String cmd = msg;
		if (!cmd.startsWith("#")) {
			return null;
		}
		cmd = cmd.substring(1).trim();
		String answer;

		TrmRsp rsp = TRM.executeCmd(getUsrId(), TrmRq.fromTg(cmd));
		Object rslt = rsp.throwIsNoOk().getResultOrMessage();
		if (rslt == null) {
			String msgNull = "Return NULL";
			if (L.isErrorEnabled()) {
				L.error(msgNull);
			}
			throw new CleanMessageRuntimeException(rsp, msgNull);
		}
		if (rslt instanceof TgMsg) {
			return (TgMsg) rslt;
		}
		answer = X.toString(rslt);

		return TgMsg.of(answer);

	}

}
