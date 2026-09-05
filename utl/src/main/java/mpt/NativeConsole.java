package mpt;

import lombok.RequiredArgsConstructor;
import mpc.console.ConsoleInput;
import mpc.exception.WhatIsTypeException;

@RequiredArgsConstructor
public class NativeConsole implements ConsoleInput.ConsoleInputTask<TrmRsp> {

	final IaUser usr_system0;

	@Override
	public void doConsoleTask(String cmd) {

		IaUser usrSystem0 = this.usr_system0;

		if (TRM.L.isInfoEnabled()) {
			TRM.L.info(ConsoleInput.SimpleTrm.INPUT_CMD(usrSystem0, cmd));
		}

		//		if (cmd.startsWith("=")) {
		//			cmd = cmd.substring(1);
		//			Host host = Host.eval(cmd);
		//			return TrmRsp.OKR(host);
		//		}

		TrmRsp rsp = TRM.executeCmd(usrSystem0, TrmRq.fromTrm(cmd));

		switch (rsp.status()) {
			case OK:
				System.out.println(TrmRspStr.toFull_Simple(rsp));
				break;
			case ERR:
				System.err.println(TrmRspStr.toFull_Simple(rsp));
				break;
			case FAIL:
				rsp.getError().printStackTrace(System.out);
				break;
			default:
				throw new WhatIsTypeException(rsp.status());
		}

		//			if (TRM.L.isInfoEnabled()) {
		//				TRM.L.info(ConsoleInput.SimpleTrm.OUTPUT_CMD(cmd));
		//			}

	}

}
