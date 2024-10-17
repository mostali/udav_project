package botcore.msg;

import mpc.exception.ICleanMessage;

public interface IBotMsg extends ICleanMessage {
	static String wrap2(String text) {
		return "```\n" + text + "\n```";
	}
}
