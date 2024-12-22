package nett.ats;

import lombok.NonNull;
import nett.appb.EncodeDecodeClb;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class SimpleSendMessage extends SendMessage {

	public SimpleSendMessage(@NonNull String chatId, @NonNull String text, ReplyKeyboard keyboard) {
		super(chatId, text);

		SimpleSendMessage sm = this;

		sm.setChatId(chatId);
		sm.setParseMode(ParseMode.HTML);
		sm.disableWebPagePreview();
		sm.disableNotification();
		if (keyboard != null) {
			sm.setReplyMarkup(keyboard);
		}
		EncodeDecodeClb.pathEncodeKeyboard(sm.getReplyMarkup());

	}
}
