package mpe.cmsg.std;

import mpe.cmsg.core.CallMsg;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.str.STR;

import java.nio.file.Path;
import java.util.Map;

public class SendCallMsg extends CallMsg {

	public static final String KEY = "sendmsg";
	public static final String LINE0 = "sendmsg:";

	public static boolean isValidKey(String msg) {
		return STR.startsWith(msg, LINE0, true);
	}

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


	@Override
	public TYPE subtype(Object... defRq) {
		Map head = getHeaders_MAP();
		if (head.containsKey("mail.smtp.host")) {
			return TYPE.EMAIL;
		} else if (head.containsKey("toTg")) {
			return TYPE.TG;
		}
		return (TYPE) ARG.throwMsg(() -> X.f("Not found SendCallMsg"), defRq);
	}

	public SendCallMsg(String fullMsg) {
		super(fullMsg, false);

		if (X.empty(getLinesMsg())) {
			addError("Empty msg");
			return;
		}

		if (!STR.startsWith(line0, true, LINE0)) {
			addError("Except first line with starts %s", LINE0);
		}


	}

	@Override
	public String toString() {
		return "SendCallMsg{" +
				"msg='" + msg + '\'' +
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
