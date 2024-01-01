package nett.appb;

import botcore.BotUpdate;
import lombok.RequiredArgsConstructor;
import mpc.arr.Arr;
import mpc.types.pare.Pare;
import nett.Tgc;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@RequiredArgsConstructor
public class TgUpdate extends BotUpdate {
	public final Update update;

	public static TgUpdate of(Update update) {
		return new TgUpdate(update);
	}

	@Override
	public List<Pare> getSpecialTypes() {
		Document doc = Tgc.getDocument(update);
		if (doc == null) {
			return null;
		}
		return Arr.as(Pare.of(doc.getClass().getSimpleName(), doc));
	}

	@Override
	public String getCallbackData() {
		CallbackQuery callbackQuery = update.getCallbackQuery();
		return callbackQuery == null ? null : callbackQuery.getData();
	}

	@Override
	public String getMessageOrCallbackData() {
		return Tgc.getMessageOrCallbackData(update, getIsCallbackOrMessage());
	}

	@Override
	public long getChatIdAny() {
		return Tgc.getChat(update, getIsCallbackOrMessage()).getId();
	}

	@Override
	public String getMessageText() {
		Message message = update.getMessage();
		return message == null ? null : message.getText();
	}

	@Override
	public String toString() {
		return "TgUpdate >>> " + update;
	}
}
