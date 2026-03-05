package nett.ats;

import mpc.log.L;
import mpu.X;
import mpu.core.ARG;
import nett.ApiTg;
import nett.ITokenFinder;
import nett.TgPollingBot;
import nett.appb.DefTgApp;
import nett.appb.EncodeDecodeClb;
import nett.appb.TgRootRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import mpu.core.ARR;
import mpu.IT;
import mpc.env.Env;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

//AppTgSender
public class ATS extends ATS_Base {

	public static final Logger L = LoggerFactory.getLogger(ATS.class);


	public static void main(String[] args) throws MalformedURLException {
		String id = Env.EDIR.FILEVAR_TLP.readStrRq("nett/bt/gts/i");
		String token = Env.EDIR.FILEVAR_TLP.readStrRq("nett/bt/gts/t");
//		sendMessage(id, token, 1L, "hey4", true, USE_FREE_PROXY, null);//

	}

	private static class SingletonHolder {
		public static final ATS HOLDER_INSTANCE = new ATS();
	}

	public static ATS get() {
		return SingletonHolder.HOLDER_INSTANCE;
	}

	public static TgPollingBot createNewTgBot(String botDomain, String botToken, TgRootRoute rootHandler) {
		TgPollingBot bot = ApiTg.getOrBuildBot(botDomain, botToken, true, false, DefTgApp.USE_FREE_PROXY, rootHandler);
		return bot;
	}

	public static TgPollingBot getTgBot(String botDomain) {
		TgPollingBot bot = ApiTg.getOrBuildBot(botDomain, null, false, false, DefTgApp.USE_FREE_PROXY, null);
		return bot;
	}

	private ITokenFinder[] tokenLoaders = new ITokenFinder[0];

	public ATS addTokenFinder(ITokenFinder tokenLoader) {
		this.tokenLoaders = ARR.incrementArray(tokenLoaders);
		this.tokenLoaders[tokenLoaders.length - 1] = tokenLoader;
		return this;
	}

	public String getOrFindBotToken(String botDomain) {
		IT.notEmpty(botDomain, "botDomain");
		String botToken = ApiTg.getBotToken(botDomain);
		if (botToken != null) {
			return botToken;
		}
		botToken = findToken(botDomain);
		if (botToken != null) {
			ApiTg.putBotToken(botDomain, botToken);
		}
		return botToken;
	}

	private String findToken(String botDomain) {
		if (this.tokenLoaders.length > 0) {
			for (ITokenFinder iTokenFinder : tokenLoaders) {
				String token = iTokenFinder.findToken(botDomain);
				if (token != null) {
					return token;
				}
			}
		}
		return null;
	}


	/**
	 * *************************************************************
	 * ---------------------------send*** --------------------------
	 * *************************************************************
	 */

	public static Message sendMessage(String botDomain, String botToken, SendMessage msg, boolean shutdown, boolean useFreeProxy, TgRootRoute handler) {
		if (L.isDebugEnabled()) {
			L.debug("sendMessage:\n" + msg);
		}
		TgPollingBot bot = ApiTg.getOrBuildBot(botDomain, botToken, true, shutdown, useFreeProxy, handler);
		try {
			EncodeDecodeClb.pathEncodeKeyboard(msg.getReplyMarkup());
			return bot.execute(msg);
		} catch (TelegramApiException e) {
			throw AtsException.of(e);
		} finally {
			if (ARG.isDefEqTrue(shutdown)) {
				ApiTg.destroyInstanceBot(bot);
			}
		}
	}

	public static Message sendMessage(String botDomain, String botToken, long reciverId, String message, boolean shutdown, boolean useFreeProxy, TgRootRoute rootHandler) {
		return sendMessage(botDomain, botToken, String.valueOf(reciverId), message, null, shutdown, useFreeProxy, rootHandler);
	}

	public static Message sendMessage(String botDomain, String botToken, String reciverId, String message, ReplyKeyboard keyboard, boolean shutdown, boolean useFreeProxy, TgRootRoute rootHandler) {
		TgPollingBot bot = ApiTg.getOrBuildBot(botDomain, botToken, true, shutdown, useFreeProxy, rootHandler);
		try {
			return doSendMessage(bot, reciverId, message, keyboard);
		} catch (TelegramApiException e) {
			throw AtsException.of(e);
		} finally {
			if (ARG.isDefEqTrue(shutdown)) {
				ApiTg.destroyInstanceBot(bot);
			}
		}
	}

	public static Message sendPost(String botDomain, String botToken, long reciverId, String message, URL urlPhoto, boolean shutdown, TgRootRoute handler) {
		TgPollingBot bot = ApiTg.getOrBuildBot(botDomain, botToken, true, shutdown, handler);
		try {
			return doSendPhoto(bot, reciverId, message, urlPhoto);
		} catch (TelegramApiException e) {
			throw AtsException.of(e);
		} catch (IOException e) {
			return X.throwException(e);
		} finally {
			if (ARG.isDefEqTrue(shutdown)) {
				ApiTg.destroyInstanceBot(bot);
			}
		}
	}

	public static Message sendPost(String botDomain, String botToken, long reciverId, ISimplePostModel post, boolean shutdown, TgRootRoute handler) {
		TgPollingBot bot = ApiTg.getOrBuildBot(botDomain, botToken, true, shutdown, handler);
		try {
			return doSend(bot, reciverId, post);
		} catch (TelegramApiException e) {
			throw AtsException.of(e);
		} finally {
			if (ARG.isDefEqTrue(shutdown)) {
				ApiTg.destroyInstanceBot(bot);
			}
		}
	}

}
