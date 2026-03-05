package nett.appb;

import botcore.BotUpdate;
import lombok.RequiredArgsConstructor;
import mpc.exception.FIllegalStateException;
import mpu.IT;
import mpu.core.ARR;
import mpu.pare.Pare;
import nett.Tgc;
import org.telegram.telegrambots.meta.api.objects.*;

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
		return ARR.as(Pare.of(doc.getClass().getSimpleName(), doc));
	}

	private boolean isDecoded = false;

	@Override
	public boolean isDecode(boolean... state) {
		if (state != null && state.length > 0) {
			return isDecoded = state[0];
		}
		return isDecoded;
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
		Chat chat = Tgc.getChat(update, getIsCallbackOrMessage());
		if (chat == null) {
			throw new FIllegalStateException("Chat not found from update:\n" + update);
		}
		return chat.getId();
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
