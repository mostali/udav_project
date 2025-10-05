package zk_os;

import mpc.str.sym.SEP;
import mpu.Sys;
import mpu.SysExec;
import mpu.pare.Pare3;
import mpu.str.SPLIT;
import mpu.str.Sb;
import zk_form.notify.ZKI;

import java.util.function.Function;

public class AppZosView {

	public static Function<String, Object> funcExecCmdAndShowResult = (s) -> {
//		ExecRq execRq = Sys.exec(true, SPLIT.argsBySpace(s));
//		Supplier<String> lines_Err_Out = () -> ARR.mergeToList(ARR.as(s, "-----------------------"), execRq.getOut(false), execRq.getOut(true));
//		List<String> lines_Err_Out = ARR.mergeToList(ARR.as(s, "-----------------------"), execRq.getOut(false), execRq.getOut(true));
		Pare3<Integer, String, String> execRq = SysExec.exec(30, Sys.ExecDestroyMode.HYBRID_NOWAIT_DESTROY, SPLIT.argsBySpace(s));
		Function<Pare3<Integer, String, String>, String> showResultMsg = (r) -> {
			Sb sb = new Sb();
			sb.NL(s);
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
