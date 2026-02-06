package botcore;

import botcore.clb.BotCallback;
import botcore.clb.IClb;
import botcore.clb.QuestState;
import botcore.msg.BotMsgException;
import botcore.msg.BotMsgLevel;
import botcore.msg.IBotMsg;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import mpc.net.IllegalHttpStatusException;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.IT;
import mpu.core.EQ;
import mpe.core.ERR;
import mpc.env.APP;
import mpe.str.CN;
import mpc.env.boot.AppErrorJournal;
import mpc.exception.*;
import mpc.net.INetRsp;
import mpu.pare.Pare;
import mpc.rfl.RFL;
import mpc.str.sym.SYMJ;
import mpu.str.STR;
import mpu.str.TKN;
import mpc.str.condition.StringConditionType;

import mpu.str.ToString;
import mpe.str.URx;
import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BotRoute extends BotBaseRoute {

	public static final Logger L = LoggerFactory.getLogger(BotRoute.class);

	@Getter
	private final String keyRoute;
	public final Integer z;
	public final Integer rx_grp;

	private final Pattern compiled;

	public Pattern getKeyRouteAsRegex() {
		return compiled;
	}

	@Getter
	private final String keyAliasRoute;

	@Getter
	@Setter
	private String fromKeyAliasRoute;

	@Getter
	private final StringConditionType keyEq;

	@Getter
	private List<BotCallback> callbacks;

	protected boolean hasNoStaticCallbacks = false;

	@Getter
	private RootRoute rootRoute;

	public static String toStringLog(BotRoute botRoute) {
		return botRoute == null ? "BotRouteNULL" : botRoute.cn();
	}

	public RootRoute getRootRoute() {
		return rootRoute;
	}

	public BotRoute setRootRoute(RootRoute rootRoute) {
		this.rootRoute = rootRoute;
		return this;
	}


	public boolean isCanHandleSpecilTypes(List<Pare> specialTypes) {
		return false;
	}

	protected boolean isDefaultRoute() {
		return false;
	}

	@Getter
	private QuestState questState;

	public void setQuestStateKV(Object...lv) {
		setQuestState(QuestState.ofMap(lv));
	}
	public void setQuestState(QuestState questState) {
		this.questState = questState;
		getRootRoute().setQuestState(this);
	}

	public static void initCallbacks(BotRoute route, boolean isStatic) {
		List<Object> clbs;
		if (isStatic) {
			clbs = RFL.fieldValuesSt(route.getClass(), StringConditionType.STARTS.buildCondition(BotCallback.CALLBACK_), true, Collections.EMPTY_LIST);
		} else {
			clbs = RFL.fieldValues(route, StringConditionType.STARTS.buildCondition(BotCallback.CALLBACK_), true, Collections.EMPTY_LIST);
		}
		if (X.empty(clbs)) {
			return;
		} else if (!isStatic) {
			route.hasNoStaticCallbacks = true;
		}
		List<BotCallback> callbacks = clbs.stream().map(e -> (BotCallback) e).collect(Collectors.toList());
		for (BotCallback clb : callbacks) {
			clb.setRouteDesc(route.desc());
		}
		route.callbacks.addAll(callbacks);
	}


	public BotRoute newInstance(IBotUpdate update) {
		return newInstance().initUpdate(update);
	}

	@SneakyThrows
	public BotRoute newInstance() {
		BotRoute tgRoute = RFL.inst_(getClass(), BotRoute.class, this);
		return tgRoute;
	}

	public BotRoute(BotRoute route) {
		this.rootRoute = route.rootRoute;
		this.keyRoute = route.getKeyRoute();
		this.keyEq = route.getKeyEq();
		this.callbacks = route.getCallbacks();
		this.keyAliasRoute = null;
		this.fromKeyAliasRoute = route.getFromKeyAliasRoute();
		this.hasNoStaticCallbacks = route.hasNoStaticCallbacks;
		this.compiled = route.compiled;
		this.z = route.z;
		this.rx_grp = route.rx_grp;
	}

	public BotRoute() {
		Class routeClass = getClass();
		RouteAno ano = RootRoute.getRouteAno(routeClass);
		String keyAliasRoute;
		if (ano == null) {
			String name;
			if (isDefaultRoute()) {
				name = "";
			} else {
				name = TKN.first(routeClass.getSimpleName(), "Route").toLowerCase();
				IT.NE(name, "illegal route class name (keyRoute is empty)", routeClass);
			}
			this.keyRoute = name.isEmpty() ? "" : BotCallback.PATH_DEL + name;
			this.keyEq = StringConditionType.EQ;
			keyAliasRoute = null;
			this.z = 0;
			this.rx_grp = 1;
		} else {
			if (X.empty(ano.key())) {
				String name = TKN.first(routeClass.getSimpleName(), "Route").toLowerCase();
				this.keyRoute = BotCallback.PATH_DEL + name;
			} else {
				this.keyRoute = ano.key();
			}
			this.keyEq = ano.eq();

			keyAliasRoute = ano.alias().isEmpty() ? null : ano.alias();

			this.z = ano.z();
			this.rx_grp = ano.rx_grp();

		}
		this.keyAliasRoute = keyAliasRoute;

		boolean isRx = keyEq == StringConditionType.REGEX;
		IT.isFalse(keyAliasRoute != null && isRx, "Illegal route configuration (key & keqEq)", cn(), keyAliasRoute, keyEq);
		this.callbacks = new ArrayList<>();

		this.compiled = isRx ? Pattern.compile(getKeyRoute()) : null;
	}

	public boolean isOwnAliasRoute(BotRoute aliasRoute) {
		return EQ.equals(aliasRoute.getKeyAliasRoute(), getKeyRoute()) && EQ.equals(aliasRoute.getKeyEq(), getKeyEq());
	}

	public boolean isAlias() {
		return getKeyAliasRoute() != null;
	}

	public boolean isKeyRouteRegex() {
		return compiled != null;
	}

	public String desc() {
		return keyEq + ":" + keyRoute;
	}

	public String cn() {
		return getClass().getSimpleName();
	}

	public Long getChatIdAny() {
		return getUpdate().getChatIdAny();
	}

	public boolean isAdmin() {
		return false;
	}

	protected boolean isRouteMatchesMessageBody(String str) {
		return str == null ? false : keyEq.matchesSafe(str, keyRoute);
	}

	/**
	 * *************************************************************
	 * ----------------------------  Handle----------------------------
	 * *************************************************************
	 */

	public boolean isSelfKeyByRegex(String regex) {
		String rx = getKeyRoute() + regex;
		Pattern compile = getKeyEq().isIgnoreCase() ? Pattern.compile(rx, Pattern.CASE_INSENSITIVE) : Pattern.compile(rx);
		return compile.matcher(getMessageOrCallBackData()).matches();
	}

	public boolean isSelfKey(String... plus) {
		String val = getKeyRoute() + ARG.toDefOr("", plus);
		return EQ.equalsString(val, getMessageOrCallBackData(), getKeyEq().isIgnoreCase(), true);
	}

	public String getMessageWithoutRouteKey() {
		String keyRoute = getKeyRoute();
		if (isKeyRouteRegex()) {
			return URx.group(getKeyRouteAsRegex(), getMessageOrCallBackData(), rx_grp, null);
		}
		return getMessageOrCallBackData().substring(keyRoute.length()).trim();
	}

	private List<String> routeArgs;

	public List<String> getRouteArgs() {
		if (routeArgs != null) {
			return routeArgs;
		}
		String other = getMessageWithoutRouteKey();
		if (X.blank(other)) {
			return routeArgs = Collections.EMPTY_LIST;
		}
		return routeArgs = Arrays.asList(other.trim().split("\\s+"));
	}

	public boolean isCallbackRoute(IBotUpdate update, Boolean eqStartsContains) {
		String data = update.getCallbackData();
		if (data == null) {
			return false;
		}
		for (BotCallback clb : getCallbacks()) {
			if (clb.isCallback(data, eqStartsContains)) {
				return true;
			}
		}
		return false;
	}

	@Getter
	private IBotUpdate update;

	public BotRoute setUpdate(IBotUpdate update) {
		this.update = update;
		return this;
	}

	@Getter
	private Boolean isCallbackOrMessage = null;

	@Getter
	private String messageOrCallBackData = null;

	private UpdateType updateType;

	private BotRoute initUpdate(IBotUpdate update) {
		setUpdate(update);
		boolean isMessage = update.getCallbackData() == null;
		isCallbackOrMessage = !isMessage;
		messageOrCallBackData = update.getMessageOrCallbackData();
		updateType = UpdateType.getUpdateType(this, update);
		return this;
	}

	/**
	 * *************************************************************
	 * ---------------------------- doUpdate --------------------------
	 * *************************************************************
	 */

	public enum UpdateType {
		MSG, CLB, QUEST;

		public static UpdateType getUpdateType(BotRoute botRoute, IBotUpdate update) {
			if (botRoute.getQuestState() != null) {
				return UpdateType.QUEST;
			} else if (!botRoute.isCallbackOrMessage) {
				return UpdateType.MSG;
			}
			return UpdateType.CLB;
		}
	}


	public void doUpdate(IBotUpdate updateObj) {

		initUpdate(updateObj);

		try {

			beforeUpdate();

			Object routeResult2send;

			switch (updateType) {
				case QUEST:
					routeResult2send = doUpdateQuest(getQuestState(), messageOrCallBackData);
					break;
				case MSG:
					routeResult2send = doUpdateMessage(messageOrCallBackData);
					break;
				case CLB:
					routeResult2send = doUpdateCallback(messageOrCallBackData);
					break;
				default:
					throw new WhatIsTypeException(updateType);
			}

			afterUpdate(routeResult2send);

		} catch (Exception ex) {
			doUpdateError(ex);
		} finally {
			setUpdate(null);
			isCallbackOrMessage = null;
			messageOrCallBackData = null;
		}
	}

	protected void beforeUpdate() {
	}

	protected void afterUpdate(Object response) {
		if (response == null) {
			// ok. msg already sended
			return;
		} else if (response instanceof CharSequence) {
			sendMsgAnyway(IBotMsg.ParseMode.NONE, response.toString());
		} else if (response instanceof IBotMsg) {
			sendMsg((IBotMsg) response);
		} else if (response instanceof IClb) {
			sendMsgWithClb(ARR.asHSET((IClb) response));
		} else if (response instanceof Throwable) {
			sendError((Throwable) response);
		} else if (response instanceof ICleanMessage) {
			sendMsg_STRING(((ICleanMessage) response).getCleanMessage());
		} else if (response instanceof QuestState) {
			QuestState questState = (QuestState) response;
			sendMsg(questState.msg());
			setQuestState((QuestState) response);
		} else if (response instanceof INetRsp) {
			sendMsg_HTML((INetRsp) response);
		} else if (response instanceof Collection) {
			Collection collection = IT.NE((Collection) response);
			collection.forEach(i -> {
				if (i instanceof CharSequence) {
					sendMsg_STRING(i.toString());
				} else if (i instanceof IBotMsg) {
					sendMsg((IBotMsg) i);
				}
			});
		} else {
			if (L.isWarnEnabled()) {
				L.warn("Bot NOT handle response:" + response);
			}
			if (!APP.IS_PROM_ENABLE) {
				sendMsgError("Not found msg type [%s], route [%s], clb [%s], msg [%s],", RFL.scn(response), RFL.scn(this), isCallbackOrMessage, getMessageOrCallBackData());
			}
		}
	}

	private Serializable sendMsg_HTML(INetRsp response) {
		INetRsp<?, ?> rsp = (INetRsp<?, ?>) response;
		if (rsp.isSuccess2__()) {
			String msg = response.getErrorJsonValue(ARR.of(CN.CMSG, CN.MSG), null);
			if (msg != null) {
				return sendMsg_STRING(msg);
			} else {
				if (L.isWarnEnabled()) {
					L.warn("BotMsg INetResponse typeof {} skipped", response.getClass());
				}
				return null;
			}
		} else {
			String msg = response.getErrorJsonValue(ARR.of(CN.CMSG, CN.ECODE), null);
			if (X.notEmpty(msg)) {
				return sendMsgError(msg);
			} else {
				if (APP.IS_DEBUG_ENABLE) {
					String message = rsp.msgWithError();
					message = STR.substrTo(message, 600, message);
					return sendError(message);
				}
				return null;
			}
		}
	}

	public Serializable sendMsgErrorWithUUID(String msg, Object context) {
		UUID uuid = UUID.randomUUID();
		AppErrorJournal.ERROR(uuid, context);
		return sendMsgError(msg + " ( Id ошибки: " + uuid + ")");
	}

	public Serializable sendMsgError(String msg, Object... args) {
		return sendMsg_STRING(SYMJ.FAIL_RED_THINK + X.f(msg, args));
	}

	public Serializable sendMsgWarn(String msg, Object... args) {
		return sendMsg_STRING(SYMJ.WARN + X.f(msg, args));
	}

	public Serializable sendMsgFail(String msg) {
		return sendMsg_STRING(SYMJ.WARN3 + msg);
	}

	public Object doUpdateError(Exception ex) {
		return sendError(ex);
	}

	public Object doUpdateMessage(String msgIn) {
		msgIn = updateType + ":" + msgIn;
		if (APP.IS_DEBUG_ENABLE) {
			return sendMsg_STRING(cn() + ":" + msgIn);
		}
		String err = cn() + ":doUpdateMessage:" + msgIn;
		throw new UnsupportedOperationException(err);
	}

	protected Object doUpdateQuest(QuestState questState, String questAnswer) {
		String msg = X.f(" {} QuestAnswer[%s][%s]", cn(), questAnswer, questState);
		if (APP.IS_DEBUG_ENABLE) {
			return sendMsg_STRING(msg);
		}
		String err = cn() + ":doUpdateQuest:" + msg;
		throw new UnsupportedOperationException(err);
	}

	private Object doUpdateCallback(String callBackData) {
		for (BotCallback clb : getCallbacks()) {
			String data2 = clb.getCallbackData2From(callBackData);
			if (data2 != null) {
				return doUpdateCallback(clb, data2);
			}
		}
		throw new WrongLogicRuntimeException("Callback not found, with  clb-data:" + callBackData);
	}

	public Object doUpdateCallback(BotCallback clb, String data2) {
		String err = X.fl("{}:Need impl callback [{} > {}] / {}", updateType, cn(), BotCallback.toStringLog(clb), data2);
		if (APP.IS_DEBUG_ENABLE) {
			return sendMsg_STRING(err);
		}
		throw new UnsupportedOperationException(err);
	}

	/**
	 * *************************************************************
	 * ---------------------------- SEND MESSAGE --------------------------
	 * *************************************************************
	 */

	public Serializable sendMsg_STRING(String message, Object... args) {
		throw new UnsupportedOperationException(cn() + ":sendMsgSelf:" + message);
	}

	public Serializable sendMsg_HTML(String message, Object... args) {
		throw new UnsupportedOperationException(cn() + ":sendMsgHtml:" + message);
	}

	public Serializable sendMsg_MD(String message, Object... args) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + message);
	}

	public Serializable sendMsg_MD2(String message, Object... args) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + message);
	}

	public Serializable sendMsg_AUDIO(String caption, String file) {
		throw new UnsupportedOperationException(cn() + ":sendMsgAudio");
	}

	public Serializable sendMsg_VIDEO(File file) {
		throw new UnsupportedOperationException(cn() + ":sendMsgAudio");
	}

	//
	//

	public Object sendError(Throwable ex) {

		if (ex instanceof CleanMessageRuntimeException) {
			if (L.isErrorEnabled()) {
				L.error("Happens surprise bot error", ex);
			}
			return sendMsg_STRING(SYMJ.WARN + ((CleanMessageRuntimeException) ex).getCleanMessage());
		} else if (ex instanceof NotifyMessageRtException) {
			NotifyMessageRtException nmsg = (NotifyMessageRtException) ex;
			return sendMsg_STRING(nmsg.getCleanMessageOrMessage());
		} else if (ex instanceof INetRsp.NetResponseException) {
			return sendMsg_HTML(((INetRsp.NetResponseException) ex).rsp);
		} else if (ex instanceof IllegalHttpStatusException) {
			return sendMsgErrorAnyway(ex);
		} else if (ex instanceof BotMsgException) {
			return sendMsg(((BotMsgException) ex).level(), ex.getMessage(), ex);
		}
		String err = X.fl("{}:SendError, chatId ({}), route({}), msg|clb({})({})", updateType, getChatIdAny(), cn(), getIsCallbackOrMessage(), getMessageOrCallBackData());
		if (L.isErrorEnabled()) {
			L.error(err, ex);
		}
		if (ex instanceof SocketTimeoutException) {
			return sendMsgFail("Сервер долго думает..");
		} else if (ex instanceof SocketException) {
			return sendMsgFail("Сетевая ошибка");
		}
//		else if (ex.getMessage().equals("nett.ats.AtsException")) {
//			return sendMsgFail("Ошибка отправки");
//		}
		L.warn("Not found legal tg error for error:\n" + ERR.getAllMessages(ex));
		String errByRole = ERR.toStringOfPddmMode(ex);
		return sendMsg_MD(STR.wrapPreAsMd(errByRole));
	}


	@Deprecated
	protected Serializable sendMsg(BotMsgLevel level, String message, Object context) {
		switch (level) {
			case INFO:
				return sendMsg_STRING(message);
			case ERROR:
				return sendMsgError(message);
			case WARN:
				return sendMsgWarn(message);
			case FAIL:
				return sendMsgFail(message);
			case ERRUUID:
				return sendMsgErrorWithUUID(message, context);
			default:
				throw new WhatIsTypeException(level);
		}
	}

	@Deprecated
	public Serializable sendError(String message) {
		String err = X.fl("{}:SendError, chatId ({}), route({}), msg|clb({})({})", updateType, getChatIdAny(), cn(), getIsCallbackOrMessage(), getMessageOrCallBackData());
		if (L.isErrorEnabled()) {
			L.error(err);
		}
		if (APP.IS_DEBUG_ENABLE) {
			return sendMsg_STRING(err + "\n" + message);
		}
		return null;
	}

	//
	//

	public <M extends IBotMsg> Serializable sendMsg(M message) {
		throw new UnsupportedOperationException(cn() + ":sendMsg0:" + message);
	}

	public Serializable sendMsgErrorAnyway(Throwable err) {
		String message;
		APP.PDDM pddm = APP.PDDM.get();
		switch (pddm) {
			case DEBUG:
				message = ERR.getMessagesAsStringWithHead(err, err.getMessage());
				break;
			case DEV:
				message = err.getMessage();
				break;
			case PROM:
			default:
				message = DefMsg.MSG_ERR_ILLEGAL_MSG;
				break;
		}
		return sendMsgAnyway(IBotMsg.ParseMode.MD, message);
	}

	public Serializable sendMsgAnyway(IBotMsg.ParseMode mode, String message, Object... args) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + message);
	}

	public Serializable sendMsgTo(Long toChatId, IBotMsg message) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + toChatId + ":" + message);
	}

	public Object sendMsgWithClb(Collection<IClb> clb) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + clb);
	}


	public Object sendMsgWithKeys(String message, List<List<?>> keys) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + message + ":" + keys);
	}


	/**
	 * *************************************************************
	 * ---------------------------- Editable Message ----------------------------
	 * *************************************************************
	 */

	public static Serializable sendLoadingAndGetEmsgId(BotRoute botRoute) {
		return botRoute.sendMsg_STRING(SYMJ.TIME_SANDGLASS + " Loading..");
	}


	/**
	 * *************************************************************
	 * ---------------------------- toString --------------------------
	 * *************************************************************
	 */

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "keyRoute='" + keyRoute + '\'' + ", callbacks=" + callbacks.size() + "\n" + ToString.toNiceStringCompact(callbacks) + '}';
	}


}
