package botcore.msg;

import mpc.exception.ICleanMessage;

public interface IBotMsg extends ICleanMessage {
	static String wrapPreMd(String text) {
		return "```\n" + text + "\n```";
	}
}
