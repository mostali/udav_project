package nett.msg;

import botcore.clb.IBotButton;
import botcore.msg.IBotMsg;
import botcore.msgcore.ClbMap;
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
import mpu.str.TKN;
import mpu.str.UST;
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

	public static String createLinkMd(String name, String link) {
		return "[" + name + "](" + link + ")";
	}

	public static String createLinkHtml(String name, String link) {
		return "<a href=\"" + link + "\">" + name + "</a>";
	}

	public TgMsg asType(MsgType asType) {
		this.asType = asType;
		return this;
	}

	private String _caption;

	public TgMsg set_caption(String caption) {
		this._caption = caption;
		return this;
	}

	public boolean anywayFormat;

	public TgMsg anywayFormat() {
		this.anywayFormat = true;
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

		public static void checkKeyboardClbDataMap(PartialBotApiMethod sendObject) {
			MsgType msgType = of(sendObject);
			switch (msgType) {
				case MSG: {
					checkKeyboard((((SendMessage) sendObject).getReplyMarkup()));
					return;
				}
				case EMSG: {
					checkKeyboard((((EditMessageText) sendObject).getReplyMarkup()));
					return;
				}
				default:
					return;

			}
		}

		private static void checkKeyboard(ReplyKeyboard replyMarkup) {
			if (replyMarkup instanceof InlineKeyboardMarkup) {
				InlineKeyboardMarkup keys = (InlineKeyboardMarkup) replyMarkup;
				keys.getKeyboard().stream().flatMap(k -> k.stream()).forEach(k -> ClbMap.checValidCallbackData(k.getCallbackData()));
			}
		}
	}

	@Override
	public String toString() {
		return "TgMsg{" +
				"parseMode='" + parseModeType + '\'' +
				", disableNotification=" + disableNotification +
				", disableWebPagePreview=" + disableWebPagePreview +
				", allowSendingWithoutReply=" + allowSendingWithoutReply +
				", message='" + get_text() + '\'' +
				", emsgId=" + emsgId +
				", fileDocument=" + fileDocument +
				", chatId='" + chatId + '\'' +
				", keyboard=" + keyboard +
				'}';
	}

//	@Setter
//	@Getter
//	private String parseMode;

	private @Getter ParseMode parseModeType;

//	public ParseMode getParseModeType() {
//		return ParseMode.valueOfMode(parseMode);
//	}

	@Setter
	@Getter
	private Boolean disableNotification, disableWebPagePreview, allowSendingWithoutReply;

	public TgMsg setDisableWebPagePreview(Boolean disableWebPagePreview) {
		this.disableWebPagePreview = disableWebPagePreview;
		return this;
	}

	@Setter
	@Getter
	private String _text;

	public TgMsg set_text_addline(String line, Object... args) {
		line = X.f_(line, args);
		if (_text == null) {
			_text = line;
			return this;
		}
		_text += STR.NL + line;
		return this;
	}

	@Getter
	private Integer emsgId;

	public TgMsg set_emsgId(Integer emsgId) {
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
		this._text = X.f_(text, args);
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
		tgMsg.setParseMode(ParseMode.NONE);
		return tgMsg.setText(msg, args);
	}

	public static TgMsg of(String msg, List<List<IBotButton>> buttons) {
		return of(null, msg, buttons);
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
		tgMsg.set_emsgId(emsg);
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

	@Deprecated
	public TgMsg setParseMode(String parseMode, boolean... ifMd2_Escape) {
		return setParseMode(ParseMode.valueOfMode(parseMode), ifMd2_Escape);
	}

	private boolean ifMd2_Escape = false;

	public TgMsg setParseMode(ParseMode parseMode, boolean... ifMd2_Escape) {
		this.parseModeType = parseMode;
		this.ifMd2_Escape = ARG.isDefEqTrue(ifMd2_Escape);
		return this;
	}

	interface ITmpFile {
		void removeTmpFile();
	}

	public static class SendMessageAsTmpFile extends SendDocument implements ITmpFile {

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

		initSend();

		String msgText = get_text();

		if (ifMd2_Escape && getParseModeType() == ParseMode.MD2) {
			msgText = escapeMarkdownV2(msgText);
		}

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

				IT.NE(msgText, "set text or file for sending [Document]");

				SendMessageAsTmpFile so = new SendMessageAsTmpFile();

				_SEND_OBJECT = so;

				Path tmpLocalFile = UFS.createTmpLocalFile(UUID.randomUUID() + ".tgmsg.txt", msgText, true);

				so.setDocument(new InputFile(tmpLocalFile.toFile()));

				so.setTmpFile(tmpLocalFile);

			}

		} else if (fileDocument != null) {

			SendDocument so = new SendDocument();
			_SEND_OBJECT = so;
			so.setDocument(new InputFile(fileDocument));

		}


		if (_SEND_OBJECT == null) {
			if (X.notEmpty(msgText)) {
				_SEND_OBJECT = new SendMessage();
			}
		}

		IT.NN(_SEND_OBJECT, "need send obj");

		//
		//

		MsgType type = MsgType.of(_SEND_OBJECT);

		if (_caption != null) {
			type.apply_setCaption(_SEND_OBJECT, _caption);
		}

		setString(_SEND_OBJECT, "setChatId", IT.notNull(getChatId()));

		if (msgText != null) {
			switch (type) {
				case DOC:
					//skip it - it use as tmp file
					break;
				default:
					setString(_SEND_OBJECT, "setText", msgText);

			}
		}
		if (parseModeType != null) {
			setString(_SEND_OBJECT, "setParseMode", parseModeType.mode);
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

	public int sendObjectAndGetMsgId(TgPollingBot bot) {
		Message msg = (Message) sendObject(bot);
		return msg.getMessageId();
	}

	public Serializable sendObject(TgPollingBot bot) {
		PartialBotApiMethod sendObject = toSendObject();

		MsgType.checkKeyboardClbDataMap(sendObject);

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
					L.error("Error sending:" + get_text());
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

	public static String escapeMarkdownV2(String text) {
		return text.replaceAll("([_*\\[\\]()~`>#+\\-=|{}.!])", "\\\\$1");
	}

	public static class TgCmd {
		public final String org;
		public final String body;
		public final boolean withSpace;
		public final boolean isSlashStart;

		public TgCmd(String org) {
			this.org = org;
			this.isSlashStart = org.startsWith("/");
			String body = org.substring(isSlashStart ? 3 : 2);
			this.withSpace = body.startsWith(" ");
			this.body = body.trim();
		}

		public boolean isOnlyKey() {
			return body.isEmpty();
		}

		public Integer getBodyAsSingleNum(Integer... defRq) {
			return UST.INT(body, defRq);
		}

		public Integer[] getBodyAsTwoNum(Integer[]... defRq) {
			return TKN.twoAs(body, " ", Integer.class, defRq);
		}

		public boolean isIndexCmd() {
			return isOnlyKey() && isSlashStart;
		}
	}
}
