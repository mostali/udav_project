package nett.msg;

import botcore.clb.IBotButton;
import botcore.msg.IBotMsg;
import lombok.Getter;
import lombok.Setter;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import mpc.log.L;
import mpc.rfl.RFL;
import mpu.str.STR;
import nett.TgPollingBot;
import nett.Tgc;
import nett.appb.EncodeDecodeClb;
import nett.ats.AtsException;
import nett.keyboard.TgInlineKeyboardButton;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
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
import java.nio.file.Path;
import java.util.*;

public class TgMsg implements IBotMsg {

	public static final int MAX_MD_SIZE = 4096;

	public static boolean isOkLengthLimitOfMessageAsCode(String code) {
		return X.sizeOf(code) <= TgMsg.MAX_MD_SIZE - ("```".length() * 2);
	}

	public static TgMsg of(Map lines) {
		return TgMsgs.of(lines);
	}

	public static TgMsg of(Collection lines) {
		return TgMsgs.of(lines);
	}

	private MsgType asType;

	public TgMsg asType(MsgType asType) {
		this.asType = asType;
		return this;
	}

	private String caption;

	public TgMsg caption(String caption) {
		this.caption = caption;
		return this;
	}

	public boolean anyway;

	public TgMsg anyway() {
		this.anyway = true;
		return this;
	}

	public enum MsgType {
		MSG, DOC, EMSG, AUDIO, VIDEO;

		public static MsgType of(Object o, MsgType... defRq) {
			if (o instanceof PartialBotApiMethod) {
				if (o instanceof SendDocument) {
					return DOC;
				} else if (o instanceof SendAudio) {
					return AUDIO;
				} else if (o instanceof SendVideo) {
					return VIDEO;
				} else if (o instanceof SendMessage) {
					return MSG;
				} else if (o instanceof EditMessageText) {
					return EMSG;
				}
			}
			return ARG.toDefThrowMsg(() -> X.f("Undefined TG MsgType [%s]", RFL.scn(o, null), defRq));
		}

		public void apply_setCaption(PartialBotApiMethod sendObject, String caption) {
			MsgType type = MsgType.of(sendObject);
			switch (type) {
				case DOC:
					((SendDocument) sendObject).setCaption(caption);
					break;
				case AUDIO:
					((SendAudio) sendObject).setCaption(caption);
					break;
				case VIDEO:
					((SendVideo) sendObject).setCaption(caption);
					break;
				default:
					throw new WhatIsTypeException("Except legal object for set caption (Doc,Audio,Video), but it [%s]", sendObject.getClass());

			}
		}
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

	public ParseMode getParseModeType() {
		return ParseMode.valueOfMode(parseMode);
	}

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
		return addKeyboardButtonRows(Arrays.asList(Arrays.asList(bt)));
	}

	public TgMsg addKeyboardButtonRows(List<List<TgInlineKeyboardButton>> rows) {
		setKeyboard(Tgc.newKeyboard(rows));
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

	public static TgMsg ofRoot(String code) {
		String data = STR.wrapTag(code, "root");
		return TgMsg.of(data);
	}

//	public enum SendMode {
//		MD_or_TEXT,
//	}

	public static TgMsg ofMdPreOrText(String code) {
		String msg = STR.wrapPreAsMd(code);
		return isOkLengthLimitOfMessageAsCode(code) ? ofMd(msg) : ofText(code);
	}

	public static TgMsg ofMdPre(String code) {
		String msg = STR.wrapPreAsMd(code);
		IT.state(isOkLengthLimitOfMessageAsCode(code), "increase message length (<=4096)");
		return ofMd(msg);
	}

	public static TgMsg ofText(String msg, Object... args) {
		return new TgMsg().setParseMode(ParseMode.NONE.mode).setText(msg, args);
	}

	public static TgMsg ofMd(String msg, Object... args) {
		return new TgMsg().setParseMode(ParseMode.MD.mode).setText(msg, args);
	}

	public static TgMsg ofMd2(String msg, Object... args) {
		return new TgMsg().setParseMode(ParseMode.MD2.mode).setText(msg, args);
	}

	public static TgMsg ofHtml(String msg, Object... args) {
		return new TgMsg().setParseMode(ParseMode.HTML.mode).setText(msg, args);
	}

	public static TgMsg of(String msg, String... args) {
		TgMsg tgMsg = new TgMsg();
		tgMsg.setParseMode(null);
		return tgMsg.setText(msg, args);
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

	@Override
	public String getMessage() {
		return getText();
	}

	public String getText() {
		return text;
	}

	public TgMsg setParseMode(String parseMode) {
		this.parseMode = parseMode;
		return this;
	}

	interface ITmpFile {
		void removeTmpFile();
	}

	public static class SendDocumentExt extends SendDocument implements ITmpFile {

		Path tmpLocalFile;

		public void setTmpFile(Path tmpLocalFile) {
			this.tmpLocalFile = tmpLocalFile;
		}

		@Override
		public void removeTmpFile() {
			if (tmpLocalFile != null) {
				return;
			}
			UFS.RM.fileQk(tmpLocalFile);
		}


	}

	public PartialBotApiMethod toSendObject() {
		PartialBotApiMethod _SEND_OBJECT = null;

		if (emsgId != null) {

			EditMessageText so = new EditMessageText();
			_SEND_OBJECT = so;
			so.setMessageId(emsgId);

		} else if (asType == MsgType.DOC) {

			if (fileDocument != null) {

				SendDocument so = new SendDocument();
				_SEND_OBJECT = so;
				so.setDocument(new InputFile(fileDocument));

			} else {

				SendDocumentExt so = new SendDocumentExt();

				_SEND_OBJECT = so;

				IT.NE(text, "set text or file for sending [Document]");

				Path tmpLocalFile = UFS.createTmpLocalFile(UUID.randomUUID() + ".tgmsg.txt", text, true);

				so.setDocument(new InputFile(tmpLocalFile.toFile()));

				so.setTmpFile(tmpLocalFile);

			}
		} else if (fileDocument != null) {
			SendDocument so = new SendDocument();
			_SEND_OBJECT = so;

			so.setDocument(new InputFile(fileDocument));
		} else if (text != null) {
			SendMessage so = new SendMessage();
			_SEND_OBJECT = so;

		}

		IT.NN(_SEND_OBJECT, "need send obj");

		//
		//

		MsgType type = MsgType.of(_SEND_OBJECT);

		if (caption != null) {
			type.apply_setCaption(_SEND_OBJECT, caption);
		}

		setString(_SEND_OBJECT, "setChatId", IT.notNull(getChatId()));

		if (text != null) {
			switch (type) {
				case DOC:
					//skip it - it use as tmp file
					break;
				default:
					setString(_SEND_OBJECT, "setText", text);

			}
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

		return _SEND_OBJECT;
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
//			if (sendObject instanceof SendDocumentExt) {
//				responseObject = bot.execute((SendDocument) sendObject);
//			} else
			if (sendObject instanceof SendDocument) {
				responseObject = bot.execute((SendDocument) sendObject);
			} else if (sendObject instanceof SendAudio) {
				responseObject = bot.execute((SendAudio) sendObject);
			} else if (sendObject instanceof SendVideo) {
				responseObject = bot.execute((SendVideo) sendObject);
			} else {
				responseObject = (Serializable) RFL.invoke_(bot, "execute", new Class[]{BotApiMethod.class}, new Object[]{sendObject});
//				RFL.invoke_(bot, "execute", new Class[]{TgPollingBot.class, BotApiMethod.class, int.class}, new Object[]{bot, sendObject, 3});
//				responseObject = (Serializable) RFL.invoke_(bot, "execute", new Class[]{BotApiMethod.class}, new Object[]{sendObject});

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
		} finally {
			if (sendObject instanceof ITmpFile) {
				((ITmpFile) sendObject).removeTmpFile();
			}
		}
		return responseObject;
	}

	public TgMsg html() {
		setParseMode(ParseMode.HTML.mode);
		return this;
	}
}
