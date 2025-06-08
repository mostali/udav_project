package mpe.rt.core;

import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.JOIN;
import mpu.str.Sb;
import mpu.str.STR;
import mpu.X;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static mpu.str.STR.NL;

public class ExecRq extends Exception {
	public final Integer status;
	private final String[] cmds;
	private final List<String> st_out;
	private final List<String> err_out;

	public static void main(String[] args) throws ExecRq {
//		exec(false, "chromium www.example.com");
//		exec(false, "google-chrome www.example.com");
		exec(false, new File("/home/dav/pjbf_stands/SAT_VIEW/"), "gedit", "ufos.sh");
//		exec(false,  "ls");
	}

	public ExecRq(Throwable cause) {
		super(cause);
		this.cmds = null;
		this.st_out = null;
		this.err_out = null;
		this.status = Integer.MIN_VALUE;
	}

	public ExecRq(String[] cmd, int status, List<String> st_out, List<String> err_out, Throwable error) {
		super(error);
		this.cmds = cmd;
		this.status = status;
		this.st_out = st_out;
		this.err_out = err_out;
	}

	public static ExecRq FAIL(Exception e) {
		return new ExecRq(e);
	}

	public String getMessage(boolean successOrError, String... separatorLine) {
		List<String> out = successOrError ? st_out : err_out;
		String msg = out != null ? JOIN.allBy(ARG.toDefOr(NL, separatorLine), out) : "";
		return msg;
	}

	@Override
	public String getMessage() {
		return getMessageReport(0);
	}

	public String getMessageReport(int tabLevel) {
		String TAB = STR.TAB(tabLevel);
		String TAB2 = STR.TAB(tabLevel + 1);
		String TAB3 = STR.TAB(tabLevel + 2);
		Sb sb = new Sb();
		sb.append(TAB).append("Status:").append(status == 0 ? "OK(0)" : "ERR(" + status + ")").append(":");
		String msg = super.getMessage();
		if (X.notEmpty(msg)) {
			sb.append(msg);
		} else if (emptyOut()) {
			sb.append("empty");
		}
		sb.NL();
		if (X.notEmpty(st_out)) {
			sb.append(st_out, ARR.of("OUT:", "<"), tabLevel + 1);
		}
		if (X.notEmpty(err_out)) {
			sb.append(err_out, ARR.of("ERR:", "<"), tabLevel + 1);
		}
		sb.deleteEndIf(Sb.NL);
		return sb.toString();
	}

	private boolean emptyOut() {
		return X.emptyAll(st_out, err_out);
	}

	public static ExecRq execCmd(boolean returnErrorOrThrow, String cmd, Object... args) throws ExecRq {
		return exec(returnErrorOrThrow, X.f_(cmd, args).split("\\s++"));
	}

	public static ExecRq exec(boolean returnErrorOrThrow, String... cmds) throws ExecRq {
		return exec(returnErrorOrThrow, null, cmds);
	}

	public static ExecRq exec(boolean returnErrorOrThrow, File dir, String... cmds) throws ExecRq {
		int _STATUS = 0;
		List<String> st_out = new CopyOnWriteArrayList();
		List<String> st_err = new CopyOnWriteArrayList();
		Throwable _ERR = null;
		try {
			_STATUS = dir == null ? ExecThread.execCommand(cmds, st_out, st_err) : ExecThread.execCommand(dir, cmds, st_out, st_err);
		} catch (Throwable err) {
			_STATUS = -1;
			_ERR = err;
		}
		ExecRq rst = new ExecRq(cmds, _STATUS, st_out, st_err, _ERR);
		if (returnErrorOrThrow || _STATUS == 0) {
			return rst;
		}
		throw rst;
	}

	public boolean isSuccess() {
		return status == 0;
	}

	public List<String> getOut(boolean st_or_err) {
		return st_or_err ? st_out : err_out;
	}

	public List getOutMerged() {
		return ARR.mergeToList(ARR.as("-------------ERR------------"), getOut(false), ARR.as("-------------OUT------------"), getOut(true));
	}
}
