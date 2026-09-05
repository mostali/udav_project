package nett.appb;

import mpu.IT;
import mpc.rfl.RFL;
import botcore.IBotUpdate;
import botcore.clb.EncodeDecodeCacheClb;
import mpu.core.ARG;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class EncodeDecodeClb {

	public static void pathDecodeUpdate(IBotUpdate update, boolean required) {
		CallbackQuery query = ((TgUpdate) update).update.getCallbackQuery();
		String data = query.getData();
		String dataDecoded = EncodeDecodeCacheClb.decode(data);
		if (required) {
			IT.notNull(dataDecoded);
		} else if (dataDecoded == null) {
			return;
		}
		RFL.write(query, "data", dataDecoded, false, true);
		update.isDecode(true);
	}


	public static void pathEncodeKeyboard(ReplyKeyboard keyboard) {
		if (keyboard instanceof InlineKeyboardMarkup) {
			List<List<InlineKeyboardButton>> lines = (List<List<InlineKeyboardButton>>) RFL.read(keyboard, "keyboard", false, true);
			for (List<InlineKeyboardButton> row : lines) {
				for (InlineKeyboardButton bt : row) {
					pathBt(bt);
				}
			}
		}
	}

	private static void pathBt(InlineKeyboardButton bt) {
		String callbackData = (String) RFL.read(bt, "callbackData", true, true);
		if (callbackData != null && callbackData.length() > 30) {
			callbackData = EncodeDecodeCacheClb.encode(callbackData);
			RFL.write(bt, "callbackData", callbackData, true, true);
		}
	}


}
