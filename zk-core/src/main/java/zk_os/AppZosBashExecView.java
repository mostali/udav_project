package zk_os;

import mpc.exception.WhatIsTypeException;
import mpc.map.BootContext;
import mpc.str.sym.SEP;
import mpu.Sys;
import mpu.SysExec;
import mpu.SysExecAsync;
import mpu.SysExecV3_RMM;
import mpu.pare.Pare3;
import mpu.str.SPLIT;
import mpu.str.Sb;
import zk_form.notify.ZKI;

import java.util.function.Function;

public class AppZosBashExecView {

	public static Function<String, Object> funcExecCmdAndShowResult = (bashData) -> {
//		ExecRq execRq = Sys.exec(true, SPLIT.argsBySpace(s));
//		Supplier<String> lines_Err_Out = () -> ARR.mergeToList(ARR.as(s, "-----------------------"), execRq.getOut(false), execRq.getOut(true));
//		List<String> lines_Err_Out = ARR.mergeToList(ARR.as(s, "-----------------------"), execRq.getOut(false), execRq.getOut(true));


		Integer bashMode = BootContext.get().getAs(SysExec.APK_BASH_MODE, Integer.class, 0);

		Pare3<Integer, String, String> execRq;

		switch (bashMode) {
			case 0:
				execRq = SysExecV3_RMM.execWaitMs(30_000, Sys.ExecDestroyMode.HYBRID_NOWAIT_DESTROY, SPLIT.argsBySpace(bashData));
				break;
			case 1:
				execRq = SysExecAsync.execSync_ShData(null, bashData, null);
				break;

			default:
				throw new WhatIsTypeException(bashMode);

		}


		Function<Pare3<Integer, String, String>, String> showResultMsg = (r) -> {
			Sb sb = new Sb();
			sb.NL(bashData);
			sb.NL(SEP.DASH.__str1__("OUT"));
			sb.NL(execRq.val());
			sb.NL(SEP.DASH.__str1__("ERR"));
			sb.NL(execRq.ext());
			return sb.toString();
		};

		if (execRq.key() == null) {
			ZKI.errorSingleLine("timeout");
		}
//		if (execRq.key() == 0) {
//			ZKI.infoEditorBw(execRq.val());
//		} else
		String rslt = showResultMsg.apply(execRq);
		ZKI.infoEditorDark(rslt);
		return rslt;
	};

}
