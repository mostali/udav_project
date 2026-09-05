package nett.appb;


import botcore.BotRoute;
import botcore.DefMsg;
import lombok.Getter;
import mpc.env.boot.AppBoot;
import mpu.core.EQ;
import mpu.X;
import mpu.IT;
import mpc.env.Env;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.netapp.*;
import mp.utl_odb.netapp.mdl.NetActivityModel;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpe.NT;
import mpu.str.STR;
import nett.ats.ATS;
import nett.Tgc;
import botcore.IBotUpdate;
import nett.msg.TgMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class TgApp {

	public static final Logger L = LoggerFactory.getLogger(TgApp.class);

	@Getter
	private final TgRootRoute rootRoute;

	private final String botAdmin;

	private AppMode app_mode = AppMode.ON;

	public void setAppMode(AppMode app_mode) {
		this.app_mode = IT.notNull(app_mode);
	}

	@Deprecated //APP
	public boolean isProm() {
		return app_mode == AppMode.ON;
	}

	public boolean isActivityAllow() {
		return true;
	}

	public Path getStoreRoot() {
		return Env.RPA;
	}

	public boolean isAdmin(String chatID) {
		if (EQ.equalsString(chatID, botAdmin, false, true)) {
			return true;
		}
		return false;
	}

	public TypeDb getActivityDb() {
		return NetActivityModel.DB(getStoreRoot());
	}

	public Message sendMessage(NetUsrId usrId, String message) throws TelegramApiException {
		IT.isTrue(usrId.getNetType() == NT.TG);
		SendMessage sm = Tgc.newSendMessage(usrId.getUserNID(), message);
		return getRootRoute().getTgBot().execute(sm);
	}

	public enum AppMode {
		ON, DEBUG, BLOCK, ADMIN
	}

	public boolean isAllowedSystemMessages(String chatID) {
		return !isProm() || isAdmin(chatID);
	}

//	public TgApp(String botId, String botToken, String botAdmin, List<Class> route_classes, DefaultRoute defaultRoute) throws IOException, ClassNotFoundException {
//		this(new DefNetApp(), botId, botToken, botAdmin, route_classes, defaultRoute);
//	}

	protected TgApp(INetApp netApp, String botId, String botToken, String botAdmin, List<Class<BotRoute>> route_classes) throws IOException, ClassNotFoundException {
		this(netApp, botId, botToken, botAdmin, route_classes, null);
	}

	protected TgApp(INetApp netApp, String botId, String botToken, String botAdmin, List<Class<BotRoute>> route_classes, TgDefaultRoute defaultRoute) throws IOException, ClassNotFoundException {

		List<String> routes = route_classes.stream().map(Class::getSimpleName).collect(Collectors.toList());
//		String bot = STR.toStringSE(botId + "", 3);
		String tk = STR.toStringSE(botToken, 3);
		AppBoot.bootLog("Running TG Bot https://t.me/{} [{}] {} with admin [{}], routes {}", botId, getClass().getSimpleName(), tk, botAdmin, routes);

		this.botAdmin = botAdmin;

		defaultRoute = defaultRoute == null ? new AppDefaultRoute() : defaultRoute;
		TgDefaultRoute finalDefaultRoute = defaultRoute;

		TgRootRoute rootRoute = new TgRootRoute(botId, route_classes) {

			@Override
			public TgApp getTgApp() {
				return TgApp.this;
			}

			@Override
			public INetApp getNetApp() {
				return netApp;
			}

			@Override
			protected void initDefaultRoute() {
				setDefaultRoute(finalDefaultRoute);
			}

			@Override
			public void onSingleUpdate(IBotUpdate botUpdate) {
				Update update = ((TgUpdate) botUpdate).update;
				go:
				switch (app_mode) {
					case BLOCK:
						sendMessage(update, "Sorry, bot is update, try again later..");
						return;
					case ADMIN:
						String chatID = Tgc.getChatIdAnyStr(update);
						boolean isAdmin = chatID.equals(botAdmin) || isAdmin(chatID);
						if (!isAdmin) {
							sendMessage(update, "Sorry, bot is temporary unreachable, try again later..");
							return;
						}
						boolean isOn = EQ.equalsSafe(Tgc.getMessage(update), "@@" + AppMode.ON);
						if (isOn) {
							setAppMode(AppMode.ON);
							sendMessage(update, "Bot is ON");
							return;
						}
						break go;
				}

				super.onSingleUpdate(botUpdate);
			}
		};
		if (X.notEmpty(botAdmin)) {
			ReplyKeyboardMarkup keyboard = defaultRoute.getMainMenu(botAdmin);
			ATS.sendMessage(botId, botToken, botAdmin, DefMsg.buildOwnerStartMessage(), keyboard, false, DefTgApp.USE_FREE_PROXY, rootRoute);
		} else {
			ATS.createNewTgBot(botId, botToken, rootRoute);
		}

		this.rootRoute = rootRoute;
	}


//	public String loadStartContent(Class resourceClass, String fileFromRunLoctionOrResources) {
//		return RES.loadFileFromRunLocationOrResources(resourceClass, fileFromRunLoctionOrResources, copy ? !(copy = false) : false, null);
//	}

//	@NotNull
//	public ReplyKeyboardMarkup getMainMenu(String chatId) {
//		KeyboardRow row0 = new KeyboardRow();
//		row0.add("Help..");
//		List<KeyboardRow> lines = Arrays.asList(row0);
//		TgReplyKeyboardMarkup keyboard = new TgReplyKeyboardMarkup();
//		keyboard.setKeyboard(lines);
//		keyboard.setResizeKeyboard(true);
//		//keyboard.setOneTimeKeyboard(true);
//		return keyboard;
//	}


	public class AppDefaultRoute extends TgDefaultRoute {

		public AppDefaultRoute() {
			super();
		}

		@Override
		public TgMsg getTgMsg_ROUTES() {
			return isAllowedSystemMessages(getChatIdAnyStr()) ? super.getTgMsg_ROUTES() : super.getTgMsg_START();
		}

//		@Override
//		public TgMsg getTgMsg_RouteNotFound(String route) {
//			return isAllowedSystemMessages(getChatIdAnyStr()) ? super.getTgMsg_RouteNotFound(route) : TgMsg.of("Sorry, not undastand..");
//		}

		@Override
		public TgMsg getTgMsg_CallbackNotFound(String clb) {
			return isAllowedSystemMessages(getChatIdAnyStr()) ? super.getTgMsg_CallbackNotFound(clb) : TgMsg.of("Unknown action..");
		}

//		@Override
//		public ReplyKeyboardMarkup getMainMenu(String chatId) {
//			return TgApp.this.getMainMenu(chatId);
//		}

	}
}
