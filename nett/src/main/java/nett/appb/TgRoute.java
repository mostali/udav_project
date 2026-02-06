package nett.appb;

import botcore.DefMsg;
import botcore.msg.IBotMsg;
import lombok.SneakyThrows;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpu.IT;
import mpc.exception.ICleanMessage;
import mpc.exception.StackTraceRuntimeException;
import mpc.rfl.RFL;
import mp.utl_odb.netapp.INetApp;
import mpe.NT;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpu.X;
import mpu.str.STR;
import nett.ats.ATS;
import nett.Tgc;
import nett.Tgh;
import botcore.BotRoute;
import nett.ats.AtsException;
import nett.keyboard.TgInlineKeyboardButton;
import nett.msg.TgMsg;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import mpe.core.ERR;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public abstract class TgRoute extends BotRoute {

	@Override
	public TgRootRoute getRootRoute() {
		return (TgRootRoute) super.getRootRoute();
	}

	public TgApp getTgApp() {
		return getRootRoute().getTgApp();
	}

	public INetApp getNetApp() {
		return getRootRoute().getNetApp();
	}

	public boolean isAdmin() {
		return getTgApp().isAdmin(getChatIdAnyStr());
	}

	protected NetUsrId createDefaultUsrId() {
		Chat chat = getChatAny();
		NetUsrId defUsr = getNetApp().createBlankUser(NT.TG, chat.getId().toString());
		defUsr.setUserName(chat.getFirstName() == null ? chat.getTitle() : chat.getFirstName());
		return defUsr;
	}

	private NetUsrId cachedUsrId;

	@SneakyThrows
	public NetUsrId getUsrId() {
		if (cachedUsrId != null) {
			return cachedUsrId;
		}
		INetApp netApp = getNetApp();
		NetUsrId defaultUsrId = createDefaultUsrId();
		try {
			cachedUsrId = netApp.getOrCreateNewUser(defaultUsrId);
		} catch (Exception ex) {
			L.error("wth", ex);
		}

		return cachedUsrId;
	}

	protected void beforeUpdate() {
		//TODO throw err DefUsr.class need
//		getUsrId();//check user or create
	}

	public TgRoute(TgRoute route) {
		super(route);
	}

	public TgRoute() {
		super();
	}

	@SneakyThrows
	public TgRoute newInstance() {
		TgRoute tgRoute = RFL.inst_(getClass(), TgRoute.class, this);
		return tgRoute;
	}

	/**
	 * *************************************************************
	 * ----------------------------  GET CHAT----------------------------
	 * *************************************************************
	 */

	public String getChatIdAnyStr() {
		return String.valueOf(getChatIdAny());
	}

	public Long getChatIdAny() {
		return getChatAny().getId();//Tgc.getChatIDAny(getUpdate());
	}

	public Chat getChatAny() {
		return Tgc.getChat(getUpdate().update, getIsCallbackOrMessage());
	}


	@Override
	public TgUpdate getUpdate() {
		return (TgUpdate) super.getUpdate();
	}

	/**
	 * *************************************************************
	 * ----------------------------  MESSAGE----------------------------
	 * *************************************************************
	 */

	public String getAppCleanMessage(Throwable ex) {
		return null;
	}

	public TgMsg getTgMsg_RouteNotFound() {
		return getTgMsg_RouteNotFound(getMessageOrCallBackData());
	}

	public static TgMsg getTgMsg_RouteNotFound(String route) {
		return TgMsg.ERROR("route '" + route + "' not found ");
	}

	/**
	 * *************************************************************
	 * ---------------------------- SEND MESSAGE----------------------------
	 * *************************************************************
	 */

	@Override
	public Message sendMsgAnyway(IBotMsg.ParseMode mode, String message, Object... args) {
		String msg = X.f_(message, args);
		if (L.isDebugEnabled()) {
			L.debug("sendMsgAnyway[ParseMode={}]{}:{}", mode, STR.NL, msg);
		}
		try {
			return (Message) mode.sendMessage(this, msg, args);
		} catch (Exception ex) {

			if (mode == IBotMsg.ParseMode.NONE || !AtsException.isMsgWrong400(ex, true)) {
				if (L.isErrorEnabled()) {
					L.error("sendMsgAnyway happens error, ParseMode [" + mode + "] with message:\n" + msg, ex);
				}
				return (Message) sendMsg_STRING(DefMsg.MSG_ERR_ILLEGAL_MSG);
			}

			//illegal msg
			//try send message as html
			mpc.log.L.debugOrWarnError(L, ex, "sendMsgAnyway has illegal msg, will be try [STRING]");
			return (Message) sendMsg_STRING(msg);

		}

	}

	//
	//

	public Message sendMsg_STRING(String message, Object... args) {
		return sendMsgImpl(X.f_(message, args), null, false, true, null);
	}

	public Message sendMsg_HTML(String message, Object... args) {
		return sendMsgImpl(X.f_(message, args), org.telegram.telegrambots.meta.api.methods.ParseMode.HTML, false, true, null);
	}

	public Serializable sendMsgEditable(EditMessageText msg) {
		return DefTgApp.sendMessage(getRootRoute().getBotId(), null, msg, false, getRootRoute());
	}

	@Override
	public Serializable sendMsg_AUDIO(String caption, String file) {
		return getRootRoute().sendMessageAudio(getChatIdAnyStr(), caption, file);
	}

	//	@Override
//	public Serializable sendMsg(IBotMsg message) {
//		return sendMsg0((TgMsg) message);
//	}

	//	public <M extends IBotMsg> Serializable sendMsg(PartialBotApiMethod message) {
//		return DefTgApp.send(getRootRoute().getBotId(), null, message, false, getRootRoute());
//	}
	@Override
	public <M extends IBotMsg> Serializable sendMsg(M message) {
		TgMsg tgMsg = (TgMsg) message;
		if (tgMsg.anywayFormat) {
			return sendMsgAnyway(tgMsg.getParseModeType(), tgMsg.get_text());
		}
		IT.notNull(tgMsg);
		if (tgMsg.getChatId() == null) {
			tgMsg.setChatId(getChatIdAnyStr());
		}
		if (tgMsg.isError()) {
			String txt = tgMsg.get_text();
			tgMsg.setParseMode(IBotMsg.ParseMode.HTML);
			tgMsg.setText(Tgh.b(DefMsg.PFX_MSG_ERROR) + " " + txt);
		}
		return DefTgApp.send(getRootRoute().getBotId(), null, tgMsg, false, getRootRoute());
	}

	public Message sendMsg_MD(String message, Object... args) {
		return sendMsgImpl(X.f_(message, args), org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN, false, true, null);
	}

	public Message sendMsg_MD2(String message, Object... args) {
		return sendMsgImpl(X.f_(message, args), ParseMode.MARKDOWNV2, false, true, null);
	}

	public Message sendMsgImpl(String message, String parseMode, boolean enablePreview, boolean enableNotification, List<List<TgInlineKeyboardButton>> keyboard) {
		long chatIDAny = Tgc.getChatIDAny(this.getUpdate().update);
		SendMessage msg = Tgc.newSendMessage(chatIDAny, message, parseMode, enablePreview, enableNotification, keyboard);
		return sendMsgImpl(msg);
	}

	public Serializable sendEditMessage(Integer msg_id, String message, String isHtml, boolean preview, List<List<TgInlineKeyboardButton>> lines) {
		EditMessageText sm = Tgc.newSendEditMessage(msg_id, getChatIdAny(), message, isHtml, preview, lines);
		return sendMsgEditable(sm);
	}

	public Message sendMsgImpl(SendMessage msg) {
		return ATS.sendMessage(getRootRoute().getBotId(), null, msg, false, false, getRootRoute());
	}

	public int sendMsgIMplAndGetMsgId(TgMsg tgSend) {
		Message m = (Message) sendMsg(tgSend);
		return m.getMessageId();
	}

	/**
	 * *************************************************************
	 * ---------------------------- SEND ERROR ----------------------------
	 * *************************************************************
	 */

	public Object sendError(Throwable error) {

		ICleanMessage clnMsg = ERR.getCleanMessageType(error);
		if (clnMsg != null) {
			return sendError(null, error, clnMsg);
		}

		String cleanMsg = getAppCleanMessage(error);
		if (cleanMsg == null) {
			return super.sendError(error);
		}
		return cleanMsg;
	}

	public Message sendErrorFmt(String msg, Object... args) {
		return sendError(X.f(msg, args));
	}

	public Message sendError(String msg) {
		return sendError(msg, new StackTraceRuntimeException(msg), null);
	}

	public Message sendError(String error, Throwable stacktrace, ICleanMessage cleanMessage) {
		String msgErr = X.fl("Telegram Send ErrorMessage '{}' / '{}'", error, cleanMessage == null ? null : cleanMessage.getCleanMessage());
		L.error(msgErr, stacktrace);
		String pfxErr = Tgh.b("ERROR: ");
		String errMsg = cleanMessage != null ? cleanMessage.getCleanMessage() : Tgh.code(error);
		errMsg = pfxErr + errMsg;
		return sendMsgImpl(errMsg, org.telegram.telegrambots.meta.api.methods.ParseMode.HTML, true, true, null);
	}

	public Integer sendLoadingAndGetEmsgId() {
		return ((Message) sendLoadingAndGetEmsgId(this)).getMessageId();
	}

	public ICtxDb.CtxModel<Ctx3Db.CtxModelCtr> getTgChatContext() {
		return Users.getUserModelOrCreate(getChatIdAnyStr());
	}

	public static class Users {
		public static final String STORE_DIR = "tgbot-store";
		public static final UTree CHATS_CTX_DB = UTree.treeApp(STORE_DIR, "chats-ctx");

		static {
			CHATS_CTX_DB.createDbIfNotExists();
		}

		public static ICtxDb.CtxModel<Ctx3Db.CtxModelCtr> getUserModelOrCreate(String key) {
			ICtxDb.CtxModel<Ctx3Db.CtxModelCtr> userModel = getUserModel(key);
			return userModel != null ? userModel : CHATS_CTX_DB.put(key);
		}

		public static ICtxDb.CtxModel<Ctx3Db.CtxModelCtr> getUserModel(String key) {
			return CHATS_CTX_DB.getModelByKey(key);
		}

	}
}
