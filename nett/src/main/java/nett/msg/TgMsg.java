package nett.msg;

import botcore.clb.IBotButton;
import botcore.msg.IBotMsg;
import lombok.Getter;
import lombok.Setter;
import mpc.args.ARG;
import mpc.ERR;
import mpc.X;
import mpc.exception.NI;
import mpc.log.L;
import mpc.rfl.RFL;
import nett.TgPollingBot;
import nett.Tgc;
import nett.appb.EncodeDecodeClb;
import nett.ats.AtsException;
import nett.keyboard.TgInlineKeyboardButton;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TgMsg implements IBotMsg {

	public static TgMsg of(Map lines) {
		return TgMsgs.of(lines);
	}

	public static TgMsg of(Collection lines) {
		return TgMsgs.of(lines);
	}

	@Override
	public String toString() {
		return "TgMsg{" +
				"parseMode='" + parseMode + '\'' +
				", disableNotification=" + disableNotification +
				", disableWebPagePreview=" + disableWebPagePreview +
				", allowSendingWithoutReply=" + allowSendingWithoutReply +
				", message='" + text + '\'' +
				", emsgId=" + emsgId +
				", fileDocument=" + fileDocument +
				", chatId='" + chatId + '\'' +
				", keyboard=" + keyboard +
				'}';
	}

	@Setter
	@Getter
	private String parseMode;

	@Setter
	@Getter
	private Boolean disableNotification, disableWebPagePreview, allowSendingWithoutReply;

	public TgMsg setDisableWebPagePreview(Boolean disableWebPagePreview) {
		this.disableWebPagePreview = disableWebPagePreview;
		return this;
	}


	@Override
	public String getCleanMessage() {
		return text;
	}

	@Setter
	@Getter
	private String text;
	@Getter
	private Integer emsgId;

	public TgMsg emsgId(Integer emsgId) {
		this.emsgId = emsgId;
		return this;
	}

	@Setter
	@Getter
	private File fileDocument;

	@Setter
	@Getter
	private String chatId;
	@Getter
	@Setter
	private ReplyKeyboard keyboard;

	@Getter
	private boolean isError;

	public TgMsg addKeyboardButton(TgInlineKeyboardButton... bt) {
		setKeyboard(Tgc.newKeyboard(Arrays.asList(Arrays.asList(bt))));
		return this;
	}

	public TgMsg setText(String text, Object... args) {
		this.text = args.length == 0 ? text : String.format(text, args);
		return this;
	}

	public TgMsg setFileDocument(File fileDocument) {
		this.fileDocument = fileDocument;
		return this;
	}

	public static TgMsg ofh(String msg, Object... args) {
		return new TgMsg().setParseMode(ParseMode.HTML).setText(msg, args);
	}

	public static TgMsg of(String msg, String... args) {
		return new TgMsg().setText(msg, args);
	}

	public static TgMsg of(Integer emsg, String msg, List<List<IBotButton>> buttons) {
		ReplyKeyboard keyboard = null;
		if (X.notEmpty(buttons)) {
			boolean global = !(buttons.get(0).get(0) instanceof KeyboardButton);
			if (ARG.isDefEqTrue(global)) {
				keyboard = Tgc.newKeyboard((List) buttons);
			} else {
				keyboard = Tgc.newKeyboardReply(buttons);
			}
		}
		TgMsg tgMsg = new TgMsg();
		tgMsg.emsgId(emsg);
		tgMsg.setKeyboard(keyboard);
		tgMsg.setText(msg);
		return tgMsg;
	}

	public static TgMsg ERROR(String msg, String... args) {
		return of(msg, args).error(true);
	}

	public boolean error() {
		return this.isError;
	}

	public TgMsg error(boolean isError) {
		this.isError = isError;
		return this;
	}

	public static TgMsg of(File document) {
		return new TgMsg().setFileDocument(document);
	}

	public String getText() {
		return text;
	}

	public TgMsg setParseMode(String parseMode) {
		this.parseMode = parseMode;
		return this;
	}

	public PartialBotApiMethod toSendObject() {
		PartialBotApiMethod _SEND_OBJECT = null;
		if (emsgId != null) {
			EditMessageText so = new EditMessageText();
			_SEND_OBJECT = so;
			so.setMessageId(emsgId);
		} else if (fileDocument != null) {
			SendDocument so = new SendDocument();
			_SEND_OBJECT = so;

			so.setDocument(new InputFile(fileDocument));
		} else if (text != null) {
			SendMessage so = new SendMessage();
			_SEND_OBJECT = so;

		}
		//
		//

		setString(_SEND_OBJECT, "setChatId", ERR.notNull(getChatId()));

		if (text != null) {
			setString(_SEND_OBJECT, "setText", text);
		}
		if (parseMode != null) {
			setString(_SEND_OBJECT, "setParseMode", parseMode);
		}
		if (disableNotification != null) {
			setBoolean(_SEND_OBJECT, "setDisableNotification", disableNotification);
		}
		if (disableWebPagePreview != null) {
			setBoolean(_SEND_OBJECT, "setDisableWebPagePreview", disableWebPagePreview);
		}
		if (allowSendingWithoutReply != null) {
			setBoolean(_SEND_OBJECT, "setAllowSendingWithoutReply", allowSendingWithoutReply);
		}
		if (keyboard != null) {
			setKeyboard(_SEND_OBJECT, keyboard);
		}

		if (_SEND_OBJECT != null) {
			return _SEND_OBJECT;
		}
		throw new NI();
	}

	public void setString(PartialBotApiMethod sendObject, String method, String object) {
		RFL.invoke(sendObject, method, new Class[]{String.class}, new Object[]{object});
	}

	public void setBoolean(PartialBotApiMethod sendObject, String method, boolean object) {
		RFL.invoke(sendObject, method, new Class[]{Boolean.class}, new Object[]{object});
	}

	public static void setKeyboard(PartialBotApiMethod sendObject, ReplyKeyboard keyboard) {
		EncodeDecodeClb.pathEncodeKeyboard(keyboard);
		Class type = sendObject instanceof EditMessageText ? InlineKeyboardMarkup.class : ReplyKeyboard.class;
		RFL.invoke(sendObject, "setReplyMarkup", new Class[]{type}, new Object[]{keyboard});
	}

	private Serializable responseObject;

	public int sendObjectGetMsgId(TgPollingBot bot) {
		Message msg = (Message) sendObject(bot);
		return msg.getMessageId();
	}

	public Serializable sendObject(TgPollingBot bot) {
		PartialBotApiMethod sendObject = toSendObject();
		try {
			if (sendObject instanceof SendDocument) {
				responseObject = bot.execute((SendDocument) sendObject);
			} else {
				responseObject = (Serializable) RFL.invoke_(bot, "execute", new Class[]{BotApiMethod.class}, new Object[]{sendObject});
			}
		} catch (InvocationTargetException e) {
			Throwable te = e.getTargetException();
			if (te instanceof TelegramApiException) {
				if (L.isErrorEnabled()) {
					L.error("Error sending:" + text);
				}
				throw AtsException.of((TelegramApiException) te);
			}
			return X.throwException(te);
		} catch (Exception ex) {
			return X.throwException(ex);
		}
		return responseObject;
	}

	public TgMsg html() {
		setParseMode(ParseMode.HTML);
		return this;
	}
}
