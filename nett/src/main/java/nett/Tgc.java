package nett;

import botcore.clb.IBotButton;
import mpc.exception.RequiredRuntimeException;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import nett.appb.TgCallback;
import nett.keyboard.TgInlineKeyboardButton;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//https://core.telegram.org/bots/api#replykeyboardmarkup
//https://rdrr.io/cran/telegram.bot/man/InlineKeyboardButton.html
//Component's
public class Tgc {

	public static InlineKeyboardMarkup newKeyboardInline(String... keysAndCallbacks) {
		return newKeyboardInlineStr(ARR.partition(keysAndCallbacks, 2, 2));
	}

	public static ReplyKeyboardMarkup newKeyboardReply(List<List<IBotButton>> keys) {
		ReplyKeyboardMarkup keyBoardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();
		for (List<IBotButton> singleRow : keys) {
			KeyboardRow row = new KeyboardRow();
			for (IBotButton iBotButton : singleRow) {
				row.add((KeyboardButton) iBotButton);
			}
			keyboard.add(row);
		}
		keyBoardMarkup.setKeyboard(keyboard);
		keyBoardMarkup.setResizeKeyboard(true);
		return keyBoardMarkup;
	}

	public static TgInlineKeyboardButton newSingleInlineKeyboardButton(TgCallback callback, Object data2) {
		return newSingleInlineKeyboardButton(callback, null, data2);
	}

	public static TgInlineKeyboardButton newSingleInlineKeyboardButton(TgCallback callback, String name, Object data2) {
		TgInlineKeyboardButton tgInlineKeyboardButton = new TgInlineKeyboardButton();
		tgInlineKeyboardButton.setText(X.empty(name) ? callback.label() : name);
		String callbackData = callback.pathWithData2(data2);
		tgInlineKeyboardButton.setCallbackData(callbackData);
		return tgInlineKeyboardButton;
	}

	//
	//
	//

	public static InlineKeyboardMarkup newKeyboardInline(List<TgCallback> callbacks) {
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
		for (TgCallback callback : callbacks) {
			rowsInline.add(newSingleInlineKeyboardButtonList(callback, null));
		}
		markupInline.setKeyboard(rowsInline);
		return markupInline;
	}

	public static List<InlineKeyboardButton> newSingleInlineKeyboardButtonList(TgCallback callback, Object data2) {
		List<InlineKeyboardButton> rowInline = newSingleInlineKeyboardButtonList(callback.label(), callback.pathWithData2(data2));
		return rowInline;
	}

	private static List<InlineKeyboardButton> newSingleInlineKeyboardButtonList(String message, String callback) {
		List<InlineKeyboardButton> rowInline = new ArrayList<>();
		InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
		inlineKeyboardButton.setText(message);
		inlineKeyboardButton.setCallbackData(callback);
		rowInline.add(inlineKeyboardButton);
		return rowInline;
	}

	@Deprecated
	public static InlineKeyboardMarkup newKeyboardInlineStr(List<List<String>> keysAndCallbacks) {
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

		List<List<String>> subSets = keysAndCallbacks;

		for (List<String> subset : subSets) {
			List<InlineKeyboardButton> rowInline = newSingleInlineKeyboardButtonList(subset.get(0), subset.get(1));
			rowsInline.add(rowInline);
		}
		// Add it to the message
		markupInline.setKeyboard(rowsInline);
		return markupInline;
	}

	public static SendMessage newSendMessageSimple(long whoId, String message) {
		return newSendMessage(whoId, message, null, false, true);
	}

	public static SendMessage newSendMessageHtml(long whoId, String message) {
		return newSendMessage(whoId, message, null, true, true);
	}

	public static SendMessage newSendMessage(long whoId, String message, String parseMode, boolean enablePreview, boolean enableNotification) {
		return newSendMessage(whoId, message, parseMode, enablePreview, enableNotification, (List) null);
	}

	public static SendMessage newSendMessage(long whoId, String message, String parseMode, boolean enablePreview, boolean enableNotification, List<List<TgInlineKeyboardButton>> keyboard) {
		InlineKeyboardMarkup keyboard_ = X.empty(keyboard) ? null : Tgc.newKeyboard(keyboard);
		return newSendMessage(whoId, message, parseMode, enablePreview, enableNotification, keyboard_);
	}

	public static SendMessage newSendMessage(long whoId, String message, String parseMode, boolean enablePreview, boolean enableNotification, ReplyKeyboard keyboard) {
		return newSendMessage(String.valueOf(whoId), message, parseMode, enablePreview, enableNotification, keyboard);
	}

	public static SendMessage newSendMessage(String whoId, String message, String parseMode, boolean enablePreview, boolean enableNotification, ReplyKeyboard keyboard) {
		SendMessage sm = new SendMessage(whoId, message);
		sm.setChatId(whoId);
		if (parseMode != null) {
			sm.setParseMode(parseMode);
		}
		if (!enablePreview) {
			sm.disableWebPagePreview();
		}
		if (!enableNotification) {
			sm.disableNotification();
		}
		if (keyboard != null) {
			sm.setReplyMarkup(keyboard);
		}
		return sm;
	}

	public static SendAudio newSendAudioMessage(String chatId, File file) {
		SendAudio sa = new SendAudio(chatId, new InputFile(file));
//		sm.setChatId(whoId);
//		if (parseMode != null) {
//			sm.setParseMode(parseMode);
//		}
//		if (!enablePreview) {
//			sm.disableWebPagePreview();
//		}
//		if (!enableNotification) {
//			sm.disableNotification();
//		}
//		if (keyboard != null) {
//			sm.setReplyMarkup(keyboard);
//		}
		return sa;
	}

	public static InlineKeyboardMarkup newKeyboard(List<List<TgInlineKeyboardButton>> keyboard) {
		if (X.notEmpty(keyboard)) {
			List<List<InlineKeyboardButton>> lines = new ArrayList<>();
			for (List<TgInlineKeyboardButton> keys : keyboard) {
				List<InlineKeyboardButton> row = new ArrayList<>();
				for (TgInlineKeyboardButton bt : keys) {
					if (bt.isVisible()) {
						row.add(bt);
					}
				}
				if (X.notEmpty(row)) {
					lines.add(row);
				}
			}
			if (X.notEmpty(lines)) {
				InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
				markupInline.setKeyboard(lines);
				return markupInline;
			}
		}
		return null;
	}

	public static EditMessageText newSendEditMessage(Integer msg_id, long whoId, String message, String parseMode, boolean enablePreview, List<List<TgInlineKeyboardButton>> keyboard) {
		return newSendEditMessage(msg_id, String.valueOf(whoId), message, parseMode, enablePreview, keyboard);
	}

	public static EditMessageText newSendEditMessage(Integer msg_id, String whoId, String message, String parseMode, boolean enablePreview, List<List<TgInlineKeyboardButton>> keyboard) {

		EditMessageText sm = new EditMessageText();
		sm.setChatId(whoId);
		sm.setMessageId(msg_id);
		sm.setText(message);

		if (parseMode != null) {
			sm.setParseMode(parseMode);
		}
		if (!enablePreview) {
			sm.disableWebPagePreview();
		}
		InlineKeyboardMarkup markupInline = Tgc.newKeyboard(keyboard);
		if (markupInline != null) {
			sm.setReplyMarkup(markupInline);
		}
		return sm;
	}

	public static SendMessage newSendMessage(String whoId, String message) {
		return newSendMessage(whoId, message, ParseMode.HTML, true, true, null);
	}

	public static SendMessage newSendMessage(long whoId, String message, String parseMode) {
		return newSendMessage(whoId, message, parseMode, true, true);
	}

	public static User getUser(Update update, boolean... isCallback) {
		if (ARG.isDefEqTrue(isCallback)) {
			return update.getCallbackQuery().getMessage().getFrom();
		} else {
			return update.getMessage().getFrom();
		}
	}

	public static Chat getChat(Update update, boolean... isCallback) {
		if (ARG.isDefEqTrue(isCallback)) {
			return update.getCallbackQuery().getMessage().getChat();
		} else if (update.getMessage() != null) {
			return update.getMessage().getChat();
		} else {
			return update.getEditedMessage().getChat();
		}
	}

	public static long getChatID(Update update, boolean... isCallback) {
		if (ARG.isDefEqTrue(isCallback)) {
			return update.getCallbackQuery().getMessage().getChatId();
		} else {
			return update.getMessage().getChatId();
		}
	}

	public static Chat getChatAny(Update update) {
		if (update.getMessage() != null) {
			return update.getMessage().getChat();
		} else if (update.getEditedMessage() != null) {
			return update.getEditedMessage().getChat();
		} else if (update.getCallbackQuery() != null) {
			return update.getCallbackQuery().getMessage().getChat();
		} else {
			throw new RequiredRuntimeException("need chat");
		}
	}

	public static String getChatIdAnyStr(Update update) {
		return String.valueOf(getChatIDAny(update));
	}

	public static long getChatIDAny(Update update) {
		return getChatAny(update).getId();
	}

	public static String[] getMessageAndCallback(Update update) {
		String[] two = new String[2];
		two[0] = getMessage(update);
		two[1] = getCallback(update);
		return two;
	}

	public static String getMessageOrCallbackData(Update update, boolean... isCallback) {
		return ARG.isDefEqTrue(isCallback) ? getCallback(update) : getMessage(update);
	}

	public static String getMessage(Update update) {
		return update.getMessage() == null ? null : update.getMessage().getText();
	}

	public static String getCallback(Update update) {
		return update.getCallbackQuery() == null ? null : update.getCallbackQuery().getData();
	}


	public static boolean isAllInvisible(TgInlineKeyboardButton... keys) {
		for (TgInlineKeyboardButton bt : keys) {
			if (bt.isVisible()) {
				return false;
			}
		}
		return true;
	}

	public static Document getDocument(Update update) {
		return update.getMessage() == null ? null : update.getMessage().getDocument();
	}
}
