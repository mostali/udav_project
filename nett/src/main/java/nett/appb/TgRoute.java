package nett.appb;

import botcore.msg.IBotMsg;
import lombok.SneakyThrows;
import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import mpu.IT;
import mpc.exception.ICleanMessage;
import mpc.exception.StackTraceRuntimeException;
import mpc.rfl.RFL;
import mpu.str.JOIN;
import mp.utl_odb.netapp.INetApp;
import mpe.NT;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpu.X;
import nett.AppAts;
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
		getUsrId();//check user or create
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

	public Message sendMsgHtml(String message) {
		return sendMsg(message, ParseMode.HTML, false, true, null);
	}


	@Override
	public Message sendMsgMdQk(String message) {
		try {
			return sendMsgMd(message, true);
		} catch (AtsException ex) {
			if (!AtsException.isMsgHasWrongSymbols(ex)) {
				throw ex;
			}
			try {
				return sendMsgHtml(SYMJ.FILE_TXT + message);
			} catch (Exception ex2) {
				if (!AtsException.isMsgHasWrongSymbols(ex2)) {
					throw ex2;
				}
				return sendMsgHtml(SYMJ.FILE_TXT + AppAts.MSG_ERR_ILLEGAL_MSG);
			}
		}
	}

	@Override
	public Message sendMsgHtmlQk(String message) {
		try {
			return sendMsgHtml(message);
		} catch (AtsException ex) {
			if (!AtsException.isMsgHasWrongSymbols(ex)) {
				throw ex;
			}
			try {
				return sendMsgMd(SYMJ.FILE_TXT + message);
			} catch (Exception ex2) {
				if (!AtsException.isMsgHasWrongSymbols(ex2)) {
					throw ex2;
				}
				return sendMsgHtml(SYMJ.FILE_TXT + AppAts.MSG_ERR_ILLEGAL_MSG);
			}
		}
	}


	public Message sendMsgMd(String message, boolean... wrapMd) {
		return sendMsg(ARG.isDefEqTrue(wrapMd) ? IBotMsg.wrapPreMd(message) : message, ParseMode.MARKDOWN, false, true, null);
	}

	public Message sendMsgHtml(List<String> messages, boolean... single) {
		if (ARG.isDefEqTrue(single)) {
			Message m = null;
			for (String msg : messages) {
				m = sendMsgHtml(msg);
			}
			return m;
		}
		return sendMsgHtml(JOIN.allByNL(messages).toString());
	}

	public Message sendMsg(String message, String isHtml, boolean enablePreview, boolean enableNotification, List<List<TgInlineKeyboardButton>> keyboard) {
		SendMessage msg = Tgc.newSendMessage(Tgc.getChatIDAny(this.getUpdate().update), message, isHtml, enablePreview, enableNotification, keyboard);
		return sendMsg(msg);
	}

	public Serializable sendEditMessage(Integer msg_id, String message, String isHtml, boolean preview, List<List<TgInlineKeyboardButton>> lines) {
		EditMessageText sm = Tgc.newSendEditMessage(msg_id, getChatIdAny(), message, isHtml, preview, lines);
		return sendMsgHtml(sm);
	}

	public Message sendMsg(SendMessage msg) {
		return ATS.sendMessage(getRootRoute().getBotId(), null, msg, false, false, getRootRoute());
	}

	public int sendAndGetMsgId(TgMsg tgSend) {
		Message m = (Message) sendMsgHtml(tgSend);
		return m.getMessageId();
	}

	@Override
	public Serializable sendMsgHtml(IBotMsg message) {
		return sendMsgHtml((TgMsg) message);
	}

	public Serializable sendMsgHtml(TgMsg tgMsg) {
		IT.notNull(tgMsg);
		if (tgMsg.getChatId() == null) {
			tgMsg.setChatId(getChatIdAnyStr());
		}
		if (tgMsg.isError()) {
			String txt = tgMsg.getText();
			tgMsg.html().setText(Tgh.b(PFX_MSG_ERROR) + " " + txt);
		}
		return DefTgApp.send(getRootRoute().getBotId(), null, tgMsg, false, getRootRoute());
	}

	public Serializable sendMsgHtml(EditMessageText msg) {
		return DefTgApp.sendMessage(getRootRoute().getBotId(), null, msg, false, getRootRoute());
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
		return sendMsg(errMsg, ParseMode.HTML, true, true, null);
	}

	public Integer getEmsgId() {
		return ((Message) getEmsgId(this)).getMessageId();
	}

}
