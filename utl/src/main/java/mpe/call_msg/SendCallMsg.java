package mpe.call_msg;

import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.str.STR;

import java.nio.file.Path;
import java.util.Map;

public class SendCallMsg extends CallMsg {

	public static final String KEY = "sendmsg";

	public static boolean isValidKeyFirstLine(String msg) {
		return STR.startsWith(msg, KEY + ":", true);
	}

//	public Object doSendMsg() {
//		TYPE type = type();
//		switch (type) {
//			case EMAIL:
//				return RFL.JCT.MAIL.newJarCall().invokeArgs(getHeaders_SEQARGS());
//			case TG:
//				throw new UnsupportedOperationException("except up impl");

	/// /				String[] headersSeqargs = getHeaders_SEQARGS();
	/// /
	/// /				return RFL.JCT.TG.newJarCall().invokeArgs(headersSeqargs);
//			default:
//				throw new WhatIsTypeException(type);
//		}
//	}
	public String getMsgOrBody() {
		Map headersMap = getHeaders_MAP();
		Object o = headersMap.get("msg");
		String string = o.toString();
		if (o != null) {
			return string;
		}
		String msg = getBody_STRING();
		return msg;
	}

	public void updateMsgWithBodyIfEmpty() {
		Map headersMap = getHeaders_MAP();
		if (headersMap.containsKey("msg")) {
			return;
		}
		String msg = getBody_STRING();
		updateHeaderValueByKey("msg", msg, true);
	}

	public String getToTg(String... defRq) {
		return getHeaderValueByKey("toTg", defRq);
	}

	public enum TYPE {
		EMAIL, TG
	}

//	public TYPE type0(TYPE... defRq) {
//		try {
//			return TYPE.valueOf(type());
//		} catch (Exception ex) {
//			return ARG.toDefThrow(ex, defRq);
//		}
//		return (TYPE) type((Object[]) defRq);
//	}

	@Override
	public TYPE type(Object... defRq) {
		Map head = getHeaders_MAP();
		if (head.containsKey("mail.smtp.host")) {
			return TYPE.EMAIL;
		} else if (head.containsKey("toTg")) {
			return TYPE.TG;
		}
		return (TYPE) ARG.toDefThrowMsg(() -> X.f("Not found SendCallMsg"), defRq);
	}


	public SendCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(linesMsgHeadersAndBody())) {
			addError("Empty qz msg");
			return;
		}

		if (!STR.startsWith(line0, true, KEY + ":")) {
			addError("Except first line with starts %s", KEY + ":");
		}


	}

	@Override
	public String toString() {
		return "SendCallMsg{" +
				"msg='" + fileData + '\'' +
				", line='" + line0 + '\'' +
				", state=" + state +
				", errs=" + X.sizeOf0(getErrors()) +
				'}';
	}

	public static SendCallMsg of(Path file) {
		return of(RW.readString(file));
	}

	public static SendCallMsg of(String msg) {
		return (SendCallMsg) ofQk(msg).throwIsErr();
	}

	public static SendCallMsg ofQk(String msg) {
		return new SendCallMsg(msg);
	}

	public static boolean isValid(String data) {
		return SendCallMsg.of(data).isValid();
	}

}
