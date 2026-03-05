package nett.ats;

import mpc.exception.NI;
import mpc.fs.UF;
import mpc.log.L;
import mpu.X;
import mpu.core.ARG;
import mpc.fs.ext.GEXT;
import mpe.img.UImg;
import mpv.byteunit.ByteUnit;
import nett.ApiTg;
import nett.TgPollingBot;
import nett.Tgc;
import nett.appb.EncodeDecodeClb;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ATS_Base {

	/**
	 * *************************************************************
	 * ----------------------------doSend --------------------------
	 * *************************************************************
	 */
	public static Message doSend(TgPollingBot bot, long reciverId, ISimplePostModel post, boolean... shutdown) throws TelegramApiException {
		try {
			return doSend_(bot, reciverId, post);
		} finally {
			if (ARG.isDefEqTrue(shutdown)) {
				ApiTg.destroyInstanceBot(bot);
			}
		}
	}

	private static Message doSend_(TgPollingBot tbot, long reciverId, ISimplePostModel post) throws TelegramApiException {

		List<String> urls = post.getPost_media_as_list();

		if (urls.isEmpty() && X.empty(post.getPost_text_newest())) {
			throw new IllegalStateException("text, text_new, medias is empty");
		}

		if (urls.isEmpty()) {
			return doSendMessage(tbot, reciverId, post.getPost_text_newest());
		} else {

			GEXT type = GEXT.getTypeFromUrl(urls.get(0));

			switch (type) {

				case GIF:
					return doSendGif(tbot, reciverId, post.getPost_text_newest(),
							urls.get(0));

				case IMG:
					return doSendPhoto(tbot, reciverId, post.getPost_text_newest(),
							urls.get(0));
				case VIDEO:
					return doSendVideo(tbot, reciverId, post.getPost_text_newest(),
							urls.get(0));
				default:
					throw new IllegalStateException("Unknown type doSend :" + type);
			}
		}

	}

	public static Message doSendMessage(DefaultAbsSender bot, long whoId, String message) throws TelegramApiException {
		return doSendMessage(bot, whoId, message, null);
	}

	public static Message doSendMessage(DefaultAbsSender bot, long whoId, String message, ReplyKeyboard keyboard) throws TelegramApiException {
		return doSendMessage(bot, String.valueOf(whoId), message, keyboard);
	}

	public static Message doSendMessage(DefaultAbsSender bot, String whoId, String message, ReplyKeyboard keyboard) throws TelegramApiException {
		SendMessage sm = new SendMessage0(whoId, message, keyboard);
		return bot.execute(sm);
	}

	public static Message doSendGif(DefaultAbsSender bot, long whoId, String caption, String file) throws TelegramApiException {
		return doSendGif(bot, String.valueOf(whoId), caption, file);
	}

	public static Message doSendGif(DefaultAbsSender bot, String whoId, String caption, String file) throws TelegramApiException {
		if (L.isInfoEnabled()) {
			L.info("TG/ [{}] -> doSendGif -> [{}] | {} / {}Mb", bot.getMe().getUserName(), whoId, UF.ln(file), X.sizeOf(Paths.get(file), ByteUnit.MB));
		}
		SendAnimation sp = new SendAnimation();
		sp.setChatId(whoId);
		sp.setCaption(caption);
		sp.setAnimation(new InputFile(file));
		EncodeDecodeClb.pathEncodeKeyboard(sp.getReplyMarkup());
		return bot.execute(sp);
	}

	public static Message doSendPhoto(DefaultAbsSender bot, long whoId, String caption, URL url) throws TelegramApiException, IOException {
		return doSendPhoto(bot, String.valueOf(whoId), caption, url);
	}

	public static Message doSendPhoto(DefaultAbsSender bot, String whoId, String caption, URL url) throws TelegramApiException, IOException {
		if (L.isInfoEnabled()) {
			L.info("TG/ [{}] -> doSendPhoto as Url -> [{}] | {}", bot.getMe().getUserName(), whoId, url);
		}
		SendPhoto sp = new SendPhoto();
		sp.setChatId(whoId);
		sp.setCaption(caption);
		String fileName = UImg.getNameFromUrlSafe(url.toString());
		sp.setPhoto(new InputFile(url.openConnection().getInputStream(), fileName));
		EncodeDecodeClb.pathEncodeKeyboard(sp.getReplyMarkup());
		return bot.execute(sp);
	}

	public static Message doSendPhoto(DefaultAbsSender bot, long whoId, String caption, String file) throws TelegramApiException {
		return doSendPhoto(bot, String.valueOf(whoId), caption, file);
	}

	public static Message doSendPhoto(DefaultAbsSender bot, String whoId, String caption, String file) throws TelegramApiException {
		if (L.isInfoEnabled()) {
			L.info("TG/ [{}] -> doSendPhoto -> [{}] | {} / {}Mb", bot.getMe().getUserName(), whoId, UF.ln(file), X.sizeOf(Paths.get(file), ByteUnit.MB));
		}
		SendPhoto sp = new SendPhoto();
		sp.setChatId(whoId);
		sp.setCaption(caption);
		sp.setPhoto(new InputFile(new File(file)));
		EncodeDecodeClb.pathEncodeKeyboard(sp.getReplyMarkup());
		return bot.execute(sp);
	}

	public static Message doSendVideo(DefaultAbsSender bot, long whoId, String caption, String file) throws TelegramApiException {
		return doSendVideo(bot, String.valueOf(whoId), caption, file);
	}

	public static Message doSendVideo(DefaultAbsSender bot, String whoId, String caption, String file) throws TelegramApiException {
		if (L.isInfoEnabled()) {
			L.info("TG/ [{}] -> doSendVideo -> [{}] | {} / {}Mb", bot.getMe().getUserName(), whoId, UF.ln(file), X.sizeOf(Paths.get(file), ByteUnit.MB));
		}
		SendVideo sv = new SendVideo();
		sv.setChatId(whoId);
		sv.setCaption(caption);
		sv.setVideo(new InputFile(new File(file)));
		EncodeDecodeClb.pathEncodeKeyboard(sv.getReplyMarkup());
		return bot.execute(sv);
	}

	//
	//
	public static Serializable doSend(DefaultAbsSender bot, PartialBotApiMethod msg) throws TelegramApiException {
		if (L.isInfoEnabled()) {
			L.info("TG/ [{}] -> doSend -> [{}] ", bot.getMe().getUserName(), msg);
		}
		if (msg instanceof SendAudio) {
			return bot.execute((SendAudio) msg);
		} else if (msg instanceof EditMessageMedia) {
			return bot.execute((EditMessageMedia) msg);
		}
		throw NI.stop(msg.getClass());
	}

	public static Message doSendAudio(DefaultAbsSender bot, String whoId, String caption, String file) throws TelegramApiException {
		return doSendAudio(bot, whoId, caption, file, null, null);
	}

	public static Message doSendAudio(DefaultAbsSender bot, String whoId, String caption, String file, ReplyKeyboardMarkup inlineKeyboard, Integer replyToMsg) throws TelegramApiException {
		if (L.isInfoEnabled()) {
			L.info("TG/ [{}] -> doSendAudio -> [{}] | {} / {}Mb", bot.getMe().getUserName(), whoId, UF.ln(file), X.sizeOf(Paths.get(file), ByteUnit.MB));
		}
		SendAudio sv = Tgc.newSendAudioMessage(whoId, new File(file));
		sv.setChatId(whoId);
		sv.setCaption(caption);
		if (replyToMsg != null) {
			sv.setReplyToMessageId(replyToMsg);
		}
		sv.setAudio(new InputFile(new File(file)));
		if (inlineKeyboard != null) {
			sv.setReplyMarkup(inlineKeyboard);
		}
		return doSendAudio(bot, sv);
	}

	public static Message doSendAudio(DefaultAbsSender bot, SendAudio sendAudioMsg) throws TelegramApiException {
		if (L.isInfoEnabled()) {
			File newMediaFile = sendAudioMsg.getAudio().getNewMediaFile();
			L.info("TG/ [{}] -> doSendAudio -> [{}] | {} / {}Mb", bot.getMe().getUserName(), sendAudioMsg.getChatId(), UF.ln(newMediaFile), X.sizeOf(newMediaFile.toPath(), ByteUnit.MB));
		}
		EncodeDecodeClb.pathEncodeKeyboard(sendAudioMsg.getReplyMarkup());
		return bot.execute(sendAudioMsg);
	}

	public static Message doSendDocument(DefaultAbsSender bot, long chatId, String caption, String file) throws TelegramApiException {
		return doSendDocument(bot, String.valueOf(chatId), caption, file);
	}

	public static Message doSendDocument(DefaultAbsSender bot, String chatId, String caption, String file) throws TelegramApiException {
		if (L.isInfoEnabled()) {
			L.info("TG/ [{}] -> doSendDocument -> [{}] | {} / {}Mb", bot.getMe().getUserName(), chatId, UF.ln(file), X.sizeOf(Paths.get(file), ByteUnit.MB));
		}
		SendDocument sd = new SendDocument();
		sd.setChatId(chatId);
		sd.setDocument(new InputFile(new File(file)));
		sd.setCaption(caption);
		EncodeDecodeClb.pathEncodeKeyboard(sd.getReplyMarkup());
		return bot.execute(sd);
	}

	public static List<Message> doSendMediaGroup(DefaultAbsSender bot, long chatId, String caption, List<String> files) throws TelegramApiException {
		return doSendMediaGroup(bot, String.valueOf(chatId), caption, files);
	}

	public static List<Message> doSendMediaGroup(DefaultAbsSender bot, String chatId, String caption, List<String> files)
			throws TelegramApiException {

		SendMediaGroup sm = new SendMediaGroup();
		sm.setChatId(chatId);
		List<InputMedia> media = new ArrayList<>();
		for (String file : files) {
			InputMediaPhoto im = new InputMediaPhoto();
			im.setMedia(new File(file), file);
			media.add(im);
		}
		sm.setMedias(media);
		//ED.pathEncodeKeyboard(sm.getReplyMarkup());
		return bot.execute(sm);
	}
}
