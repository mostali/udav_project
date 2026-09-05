package nett.ats;

import lombok.NonNull;
import nett.appb.EncodeDecodeClb;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class SendMessage0 extends SendMessage {

	public SendMessage0(@NonNull String chatId, @NonNull String text, ReplyKeyboard keyboard) {
		super(chatId, text);

		SendMessage0 sm = this;

		sm.setChatId(chatId);
		sm.setParseMode(ParseMode.HTML);
		sm.disableWebPagePreview();
		sm.disableNotification();
		if (keyboard != null) {
			sm.setReplyMarkup(keyboard);
		}
		EncodeDecodeClb.pathEncodeKeyboard(sm.getReplyMarkup());

	}

	public SendMessage0(String chatId, String text1) {
		super(chatId, text1);
	}

	public static SendMessage0 of(String chatId, String text, String parseMode) {
		SendMessage0 tgMsg = new SendMessage0(chatId, text);
		if (parseMode == null) {
			return tgMsg;
		}
		tgMsg.setParseMode(parseMode);
		return tgMsg;
	}
}
