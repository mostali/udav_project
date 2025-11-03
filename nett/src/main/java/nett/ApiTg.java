package nett;


import mpu.X;
import mpc.env.APP;
import mpc.exception.NI;
import nett.appb.TgRootRoute;
import nett.appb.TgUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import mpz_deprecated.EER;
import mpu.IT;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//https://javarush.ru/groups/posts/504-sozdanie-telegram-bota-na-java-ot-idei-do-deploja
//https://github.com/rubenlagus/TelegramBots/wiki/Using-Http-Proxy
//http://spys.one/socks/
//http://www.gatherproxy.com/sockslist
//https://www.rgagnon.com/javadetails/java-0085.html connect via proxy
//https://javarush.ru/groups/posts/504-sozdanie-telegram-bota-na-java-ot-idei-do-deploja
//http://qaru.site/questions/219115/using-telegram-api-for-java-desktop-app
//https://github.com/ex3ndr/telegram-api#rpc-calls
public class ApiTg {

	public static final Logger L = LoggerFactory.getLogger(ApiTg.class);

	public static boolean useFreeProxy = false;

	private final static ConcurrentHashMap<String, TgPollingBot> MAP_TG_BOTS = new ConcurrentHashMap<String, TgPollingBot>();
	private final static ConcurrentHashMap<String, String> MAP_TOKENS = new ConcurrentHashMap<String, String>();

	public static String getBotToken(String botDomain) {
		return MAP_TOKENS.get(botDomain);
	}

	public static String putBotToken(String botDomain, String botToken) {
		IT.notNull(botToken, "set bottoken");
		return MAP_TOKENS.put(botDomain, botToken);
	}

	public static TgPollingBot getOrBuildBot(String botDomain, String botToken, boolean create, boolean setShutdownHook, TgRootRoute handler) {
		return getOrBuildBot(botDomain, botToken, create, setShutdownHook, ApiTg.useFreeProxy, handler);
	}

	public static TgPollingBot getOrBuildBot(String botDomain, String botToken, boolean createIfNotExist, boolean setShutdownHook, boolean useFreeProxy, TgRootRoute rootHandler) {
		TgPollingBot bot = MAP_TG_BOTS.get(botDomain);
		if (bot != null) {
			return bot;
		} else if (!createIfNotExist) {
			throw new IllegalStateException("Bot [" + botDomain + "] not created or already removed");
		} else {
			try {
				return buildBot(botDomain, botToken, useFreeProxy, rootHandler);
			} catch (TelegramApiRequestException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public static void destroyInstanceBot(TgPollingBot bot) {
		L.warn("Bot [{}] DESTROYING...", bot.getBotUsername());
		try {
			if (bot.getBotSession().isRunning()) {
				bot.getBotSession().stop();
				L.warn("Bot [{}] session was STOPPED", bot.getBotUsername());
			} else {
				L.warn("Bot [{}] session was ALREADY STOPPED", bot.getBotUsername());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (MAP_TG_BOTS.containsKey(bot.getBotUsername())) {
				MAP_TG_BOTS.remove(bot.getBotUsername());
			}
		}
	}

	public static final IParserProxy parserProxy = null;

	private static TgPollingBot buildBot(String botDomain, String botToken, boolean useFreeProxySOCK5, TgRootRoute handler) throws TelegramApiRequestException {
		IT.notEmpty(botDomain, "botDomain");
		if (ApiTg.MAP_TG_BOTS.containsKey(botDomain)) {
			throw new IllegalStateException("Tg Bot [" + botDomain + "] already BUILDED/STARTED");
		}
		if (X.empty(botToken)) {
			botToken = MAP_TOKENS.get(botDomain);
		}
		IT.notEmpty(botToken, "set botToken");
		while (true) {
			String freeProxy = null;
			if (useFreeProxySOCK5) {
				try {
					freeProxy = parserProxy.getFreeProxy();
				} catch (TgException e) {
					throw EER.RT.I(e);
				}
			}
			L.info("Use free proxy :" + (useFreeProxySOCK5 ? freeProxy : "NO"));
			try {
				TgPollingBot bot = ApiTg.buildInstanceBot(botDomain, botToken, freeProxy, handler);
				ApiTg.MAP_TG_BOTS.put(botDomain, bot);

				if (!APP.IS_DEBUG_ENABLE) {
					Runtime.getRuntime().addShutdownHook(new Thread(() -> destroyInstanceBot(bot)));
				}

				return bot;
			} catch (Exception ex) {
				if (ex instanceof NullPointerException) {
					throw ex;
				} else {
					L.error("Use free proxy, SKIP&REMOVE::ERROR:", ex);
					parserProxy.removeProxy(freeProxy);
					continue;
				}
			}
		}
	}

	private static void setShutdownHook(TgPollingBot bot) {
	}

	private static TgPollingBot buildInstanceBot(String botDomain, String botToken, String proxy, TgRootRoute rootRoute) throws TelegramApiRequestException {
		//		ApiContextInitializer.init();
		//		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		TelegramBotsApi telegramBotsApi = null;
		try {
			telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		} catch (TelegramApiException e) {
			return X.throwException(e);
		}

		DefaultBotOptions botOptions = new DefaultBotOptions();
		//		DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
		boolean useProxy = !X.empty(proxy);
		if (useProxy) {
			throw new NI();
			//			String[] freeProxy = proxy.split(":");
			//			String PROXY_HOST = freeProxy[0];
			//			int PROXY_PORT = Integer.parseInt(freeProxy[1]);
			//			botOptions.setProxyHost(PROXY_HOST);
			//			botOptions.setProxyPort(PROXY_PORT);
			//			// Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
			//			botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
		}

		rootRoute.setBotId(botDomain);

		TgPollingBot INSTANCE;
		if (rootRoute == null) {
			INSTANCE = new WoRootRoute(botDomain, botToken, botOptions);
		} else {
			INSTANCE = new WithRootRoute(rootRoute, botDomain, botToken, botOptions);
		}

		{
			if (L.isInfoEnabled()) {
				L.info("starting tg api.." + (useProxy ? "[" + proxy + "]" : "[without proxy]"));
			}
			BotSession botSession = null;
			try {
				botSession = telegramBotsApi.registerBot(INSTANCE);
			} catch (TelegramApiException e) {
				return X.throwException(e);
			}
			INSTANCE.setBotSession(botSession);
			if (L.isInfoEnabled()) {
				L.info("Started tg api [{}] ok", botDomain);
			}
		}
		return INSTANCE;
	}

	public static class WithRootRoute extends TgPollingBot {
		final TgRootRoute rootRoute;

		public WithRootRoute(TgRootRoute rootRoute, String botUsername, String botToken, DefaultBotOptions botOptions) {
			super(botUsername, botToken, botOptions);
			this.rootRoute = rootRoute;
		}

		@Override
		public void onUpdateReceived(Update update) {
			rootRoute.onUpdatesReceived(Arrays.asList(TgUpdate.of(update)));
		}

		@Override
		public void onUpdatesReceived(List<Update> updates) {
			rootRoute.onUpdatesReceived(updates.stream().map(TgUpdate::of).collect(Collectors.toList()));
		}

		@Override
		public void onClosing() {
			rootRoute.onClosing();
			super.onClosing();
		}
	}

	public static class WoRootRoute extends TgPollingBot {

		public WoRootRoute(String botUsername, String botToken, DefaultBotOptions botOptions) {
			super(botUsername, botToken, botOptions);
		}
	}

	public static Message execute(TgPollingBot bot, BotApiMethod method, int tc) {
		return executeMethod(bot, method, tc);
	}

	public static Message execute(TgPollingBot bot, SendPhoto method, int tc) {
		return executeMethod(bot, method, tc);
	}

	private static Message executeMethod(TgPollingBot bot, Object method, int tc) {
		IT.notNull(bot, "tg-bot");
		IT.notNull(method, "tg-method");
		if (!bot.getBotSession().isRunning()) {
			throw new IllegalStateException("Telegram bot not running..");
		}
		while (true) {
			try {
				if (method instanceof BotApiMethod) {
					return (Message) bot.execute((BotApiMethod) method);
				} else if (method instanceof SendPhoto) {
					return bot.execute((SendPhoto) method);
				} else {
					throw new IllegalArgumentException("What is tg-method?" + method.getClass().getSimpleName());
				}
			} catch (TelegramApiException e) {
				if (TgException.isConnectTimeOut(e.getCause())) {
					TgException.printRecallMessage("Connect timed out", tc--);
				} else if (!TgException.isOldWebHook(e) || tc-- <= 0) {
					throw new IllegalStateException(e);
				} else {
					L.warn("Tg::Execute::ERROR::tc::" + ":" + tc, e);
				}
			}
		}
	}
}
