package nett.msg;

import botcore.msg.BotMessages;

public class TgMessages extends BotMessages<TgMsg> {

	public static TgMsg of(String msg) {
		TgMsg tgMsg = new TgMsg();
		tgMsg.setText(msg);
		return tgMsg;
	}
}
