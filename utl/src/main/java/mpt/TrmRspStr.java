package mpt;

import mpu.core.ARG;
import mpu.X;
import mpc.exception.NotifyMessageRtException;
import mpc.exception.WhatIsTypeException;
import mpu.str.STR;
import mpe.core.ERR;
import org.apache.commons.io.output.StringBuilderWriter;

import java.io.PrintWriter;

import static mpu.str.STR.NL;

public class TrmRspStr {


	public static String toFull_Simple(TrmRsp rsp) {
		switch (rsp.status()) {
			case ERR:
			case OK:
				String msgWithCode = toMessageWithCode(rsp, null);
				Object result = rsp.getResult(null);
				if (X.nullAll(msgWithCode, result)) {
					return rsp.status().name();
				} else if (X.notNullAll(msgWithCode, result)) {
					return msgWithCode + "\n" + result;
				} else if (msgWithCode == null) {
					return "R:" + result;
				}
				return msgWithCode;
			case FAIL:
				return ERR.getStackTrace(rsp.getCause());
			default:
				throw new WhatIsTypeException(rsp.status());
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- To Message With Code -------------------------
	 * *************************************************************
	 */

	public static String toMessageWithCode(TrmRsp rsp, String... returnIfDef) {
		String msg = null;
		Integer status = null;
		if (TrmRsp.isNotDefMsg(rsp.msg())) {
			msg = rsp.msg();
		}
		if (!TrmRsp.isDefStatus(rsp.exitcode())) {
			status = rsp.exitcode();
		}
		if (X.nullAll(msg, status)) {
			if (ARG.isDef(returnIfDef)) {
				return ARG.toDef(returnIfDef);
			}
			return rsp.status().name();
		} else if (X.notNullAll(msg, status)) {
			return "[" + status + "]" + msg;
		} else if (X.empty(msg)) {
			return rsp.msg() + "[" + status + "]";
		}
		return rsp.isOk() ? msg : msg + "[" + rsp.exitcode() + "]";
	}


	/**
	 * *************************************************************
	 * ---------------------------- To Report -------------------------
	 * *************************************************************
	 */

	public static StringBuilder toFull_ReportWithCauses(TrmRsp rsp, int tabLevel) {
		return toFull_Report(rsp, tabLevel, true);
	}

	public static StringBuilder toFull_Report(TrmRsp rsp, int tabLevel) {
		return toFull_Report(rsp, tabLevel, false);
	}

	public static StringBuilder toFull_Report(TrmRsp rsp, int tabLevel, boolean withCause) {
		if (rsp == null) {
			return new StringBuilder("TrmRsp(null)");
		}
		String TAB = STR.TAB(tabLevel);
		String TAB2 = STR.TAB(tabLevel + 1);
		String TAB3 = STR.TAB(tabLevel + 2);

		StringBuilderWriter sw = new StringBuilderWriter();
		StringBuilder rpr = sw.getBuilder();
		PrintWriter pw = new PrintWriter(sw);

		Object rslt = (rsp.getResult(null));
		String st = rsp.statusWithCodeWithMessage();
		if (rslt == null) {
			pw.print(st);
		} else {
			if (!TrmRsp.Status.OK.name().equals(st)) {
				pw.print(st);
			}
			pw.print(TAB + "R:" + rsp.getResult());
		}
		if (rsp.getView(null) != null) {
			pw.print(NL);
			pw.print(TAB + "V:" + rsp.getView());
		}
		if (rsp.isFail()) {
			pw.print(TAB + "" + (withCause ? NL : ": "));
			Throwable src = rsp.getCause() == null ? rsp : rsp.getCause();
			if (withCause) {
				src.printStackTrace(pw);
			} else {
				ERR.getAllMessages(src).forEach(pw::println);
			}
		} else if (rsp.getCause() != null) {
			pw.print(withCause ? NL : ": ");
			String msg = withCause ? rsp.getCause().toString() : ERR.getAllMessages(rsp.getCause()).toString();
			pw.print(TAB + "E:" + msg);
		}
		return rpr;
	}

	/**
	 * *************************************************************
	 * ---------------------------- To NotifyMessage -------------------------
	 * *************************************************************
	 */
	public static NotifyMessageRtException toNotifyMessage(TrmRsp rsp) {
		String msg = rsp.getMessageDetails(rsp.getMessage());
		switch (rsp.status()) {
			case OK:
				return NotifyMessageRtException.LEVEL.GREEN.I(msg);
			case ERR:
				return NotifyMessageRtException.LEVEL.BLUE.I(msg);
			case FAIL:
				return NotifyMessageRtException.LEVEL.RED.I(msg);
			default:
				throw new WhatIsTypeException(rsp.status());
		}
	}

	public static String toStatusWithCode(TrmRsp rsp) {
		return rsp == null ? "TrmRsp::null" : rsp.statusWithCode();
	}

	/**
	 * *************************************************************
	 * ---------------------------- Types -------------------------
	 * *************************************************************
	 */
	public enum MessageType {
		NONE, DEFAULT, MULTILINE, SHORT, LONG;

		public static MessageType of(String msg) {
			if (X.empty(msg)) {
				return MessageType.NONE;
			} else if (TrmRsp.isNotDefMsg(msg)) {
				return DEFAULT;
			} else if (msg.indexOf('\n') > 0) {
				return MULTILINE;
			} else if (msg.length() <= 50) {
				return SHORT;
			} else {
				return LONG;
			}
		}

	}

	public enum ResultType {
		NONE, MULTILINE, SHORT, LONG, OBJECT;

		public static ResultType of(Object rslt) {
			if (rslt == null) {
				return ResultType.NONE;
			} else if (rslt instanceof CharSequence) {
				String msg = rslt.toString();
				if (msg.indexOf('\n') > 0) {
					return MULTILINE;
				} else if (msg.length() <= 50) {
					return SHORT;
				} else {
					return LONG;
				}
			}
			return OBJECT;
		}
	}

}
