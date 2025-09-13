package nett.appb;

import botcore.BotRoute;
import botcore.RootRoute;
import mp.utl_odb.netapp.DefNetApp;
import mp.utl_odb.netapp.INetApp;
import mp.utl_odb.netapp.NetApp;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.env.AP;
import mpc.env.APP;
import mpu.X;
import nett.ApiTg;
import nett.TgPollingBot;
import nett.ats.AtsException;
import nett.msg.TgMsg;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class DefTgApp extends TgApp {

	public static @NotNull ICtxDb gncTree() {
		return UTree.tree(APP.TREE_GNC());
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		DefTgApp tgApp = DefTgApp.startTgApp(EchoRoute.class.getPackage().getName());
	}

	public static final boolean USE_FREE_PROXY = false;
//	private static DefTgApp instance;

	public static DefTgApp startTgApp(String... route_packages) throws IOException, ClassNotFoundException {
		return startTgApp(DefNetApp.get(), route_packages);
	}

	public static DefTgApp startTgApp(NetApp netApp, String... route_packages) throws IOException, ClassNotFoundException {

		String botId = getTgBotId();
		String botToken = getTgBotToken();
		String botAdmin = getTgBotAdminAPP();

		DefTgApp tgApp = startTgApp(netApp, botId, botToken, botAdmin, route_packages);

		return tgApp;
	}

	//

	public static String getTgBotAdminAPP() {
		ICtxDb tree = gncTree();
		String value = tree.getValue(APP.APK_TG_BT_OWNER_ID, null);
		return X.notEmpty(value) ? value : getTgBotAdminAP();
	}

	public static String getTgBotAdminAP() {
		return AP.get(APP.APK_TG_BT_OWNER_ID, null);
	}

	//

	public static String getTgBotToken() {
		String value = gncTree().getValue(APP.APK_TG_BT_TK, null);
		return X.notEmpty(value) ? value : getTgBotTokenAP();
	}


	public static String getTgBotTokenAP() {
		return AP.get(APP.APK_TG_BT_TK);
	}

	//

	public static String getTgBotId() {
		ICtxDb tree = gncTree();
		String value = tree.getValue(APP.APK_TG_BT_ID, null);
		return X.notEmpty(value) ? value : getTgBotIdAP();
	}

	public static String getTgBotIdAP() {
		return AP.get(APP.APK_TG_BT_ID);
	}

	//

	public static DefTgApp startTgApp(INetApp netApp, String botId, String botToken, String botAdmin, String... route_packages) throws IOException, ClassNotFoundException {
		AppTgDefaultRoute defaultRoute = new AppTgDefaultRoute();
		List route_classes = RootRoute.findAllRouteClasses(route_packages);
		return startTgApp(netApp, botId, botToken, botAdmin, route_classes, defaultRoute);
	}

	public static DefTgApp startTgApp(INetApp netApp, String botId, String botToken, String botAdmin, List<Class<BotRoute>> route_classes) throws IOException, ClassNotFoundException {
		AppTgDefaultRoute defaultRoute = new AppTgDefaultRoute();
		return startTgApp(netApp, botId, botToken, botAdmin, route_classes, defaultRoute);
	}

	public static DefTgApp startTgApp(INetApp netApp, String botId, String botToken, String botAdmin, List<Class<BotRoute>> route_classes, TgDefaultRoute defaultRoute) throws IOException, ClassNotFoundException {
//		DefTgApp localInstance = instance;
//		if (localInstance == null) {
//			synchronized (DefTgApp.class) {
//				localInstance = instance;
//				if (localInstance == null) {
//					instance = localInstance = new DefTgApp(netApp, botId, botToken, botAdmin, route_classes, defaultRoute);
		return new DefTgApp(netApp, botId, botToken, botAdmin, route_classes, defaultRoute);
//				}
//			}
//		}
//		return instance;
	}

	public DefTgApp(INetApp netApp, String botId, String botToken, String botAdmin, List<Class<BotRoute>> route_classes) throws IOException, ClassNotFoundException {
		super(netApp, botId, botToken, botAdmin, route_classes);
	}

	public DefTgApp(INetApp netApp, String botId, String botToken, String botAdmin, List<Class<BotRoute>> route_classes, TgDefaultRoute defaultRoute) throws IOException, ClassNotFoundException {
		super(netApp, botId, botToken, botAdmin, route_classes, defaultRoute);
	}

	/**
	 * *************************************************************
	 * ----------------------------- API --------------------------
	 * *************************************************************
	 */

	public static Serializable send(String botDomain, String botToken, TgMsg tgMsg, boolean useFreeProxy, TgRootRoute handler) {
		TgPollingBot bot = ApiTg.getOrBuildBot(botDomain, botToken, true, useFreeProxy, handler);
		return tgMsg.sendObject(bot);
	}

	public static Serializable sendMessage(String botDomain, String botToken, EditMessageText msg, boolean useFreeProxy, TgRootRoute handler) {
		TgPollingBot bot = ApiTg.getOrBuildBot(botDomain, botToken, true, useFreeProxy, handler);
		try {
			EncodeDecodeClb.pathEncodeKeyboard(msg.getReplyMarkup());
			return bot.execute(msg);
		} catch (TelegramApiException e) {
			throw AtsException.of(e);
		}
	}
}
