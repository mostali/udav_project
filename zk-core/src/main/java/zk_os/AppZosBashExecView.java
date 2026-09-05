package zk_os;

import mpc.map.BootContext;
import mpc.str.sym.SEP;
import mpu.SysExec;
import mpu.pare.Pare3;
import mpu.str.Sb;
import zk_form.notify.ZKI;

import java.util.function.Function;

public class AppZosBashExecView {

	public static Function<String, Object> funcExecCmdAndShowResult = (bashData) -> {
//		ExecRq execRq = Sys.exec(true, SPLIT.argsBySpace(s));
//		Supplier<String> lines_Err_Out = () -> ARR.mergeToList(ARR.as(s, "-----------------------"), execRq.getOut(false), execRq.getOut(true));
//		List<String> lines_Err_Out = ARR.mergeToList(ARR.as(s, "-----------------------"), execRq.getOut(false), execRq.getOut(true));


		Integer bashMode = BootContext.get().getAs(SysExec.APK_BASH_MODE, Integer.class, 0);

		Pare3<Integer, String, String> execRq = SysExec.execByMode(bashData, bashMode);

		if (execRq.key() == null) {
			ZKI.errorSingleLine("timeout");
		}

		String rslt = SysExec.showResultMsg.apply(bashData, execRq);

		ZKI.infoEditorDark(rslt);
		return rslt;
	};

}
