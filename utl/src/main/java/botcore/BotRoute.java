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
import mpu.str.USToken;
import mpc.str.condition.StringConditionType;

import mpu.str.ToString;
import mpe.str.URx;
import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BotRoute extends BotBaseRoute {

	public static final Logger L = LoggerFactory.getLogger(BotRoute.class);
	public static final String PFX_MSG_ERROR = "ERROR:";

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
				name = USToken.first(routeClass.getSimpleName(), "Route").toLowerCase();
				IT.NE(name, "illegal route class name (keyRoute is empty)", routeClass);
			}
			this.keyRoute = name.isEmpty() ? "" : BotCallback.PATH_DEL + name;
			this.keyEq = StringConditionType.EQ;
			keyAliasRoute = null;
			this.z = 0;
			this.rx_grp = 1;
		} else {
			if (X.empty(ano.key())) {
				String name = USToken.first(routeClass.getSimpleName(), "Route").toLowerCase();
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

			Object response;

			switch (updateType) {
				case QUEST:
					response = doUpdateQuest(getQuestState(), messageOrCallBackData);
					break;
				case MSG:
					response = doUpdateMessage(messageOrCallBackData);
					break;
				case CLB:
					response = doUpdateCallback(messageOrCallBackData);
					break;
				default:
					throw new WhatIsTypeException(updateType);
			}

			afterUpdate(response);

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
		if (response == null || response instanceof Number) {
			return;
		} else if (response instanceof CharSequence) {
			sendMsgHtmlQk(response.toString());
		} else if (response instanceof IBotMsg) {
			sendMsgHtml((IBotMsg) response);
		} else if (response instanceof IClb) {
			sendMsgHtml(ARR.asHSet((IClb) response));
		} else if (response instanceof Throwable) {
			sendError((Throwable) response);
		} else if (response instanceof ICleanMessage) {
			sendMsgHtml(((ICleanMessage) response).getCleanMessage());
		} else if (response instanceof QuestState) {
			QuestState questState = (QuestState) response;
			sendMsgHtml(questState.msg());
			setQuestState((QuestState) response);
		} else if (response instanceof INetRsp) {
			sendMsgHtml((INetRsp) response);
		} else if (response instanceof Collection) {
			Collection collection = IT.NE((Collection) response);
			collection.forEach(i -> {
				if (i instanceof CharSequence) {
					sendMsgHtml(i.toString());
				} else if (i instanceof IBotMsg) {
					sendMsgHtml((IBotMsg) i);
				}
			});
		} else {
			if (L.isErrorEnabled()) {
				L.error("Bot NOT handle response:" + response);
			}
		}
	}

	private Serializable sendMsgHtml(INetRsp response) {
		INetRsp<?, ?> rsp = (INetRsp<?, ?>) response;
		if (rsp.isSuccess2__()) {
			String msg = response.getErrorJsonValue(ARR.of(CN.CMSG, CN.MSG), null);
			if (msg != null) {
				return sendMsgHtml(msg);
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
		return sendMsgHtml(SYMJ.FAIL_RED_THINK + X.f(msg, args));
	}

	public Serializable sendMsgWarn(String msg, Object... args) {
		return sendMsgHtml(SYMJ.WARN + X.f(msg, args));
	}

	public Serializable sendMsgFail(String msg) {
		return sendMsgHtml(SYMJ.WARN3 + msg);
	}

	public Object doUpdateError(Exception ex) {
		return sendError(ex);
	}

	public Object doUpdateMessage(String msg) {
		msg = updateType + ":" + msg;
		if (APP.IS_DEBUG_ENABLE) {
			return sendMsgHtml(cn() + ":" + msg);
		}
		String err = cn() + ":doUpdateMessage:" + msg;
		throw new UnsupportedOperationException(err);
	}

	protected Object doUpdateQuest(QuestState questState, String questAnswer) {
		String msg = X.f(" {} QuestAnswer[%s][%s]", cn(), questAnswer, questState);
		if (APP.IS_DEBUG_ENABLE) {
			return sendMsgHtml(msg);
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
			return sendMsgHtml(err);
		}
		throw new UnsupportedOperationException(err);
	}

	/**
	 * *************************************************************
	 * ---------------------------- SEND --------------------------
	 * *************************************************************
	 */

	public Object sendError(Throwable ex) {

		if (ex instanceof CleanMessageRuntimeException) {
			if (L.isErrorEnabled()) {
				L.error("Happens surprise bot error", ex);
			}
			return sendMsgHtml(SYMJ.WARN + ((CleanMessageRuntimeException) ex).getCleanMessage());
		} else if (ex instanceof NotifyMessageRtException) {
			NotifyMessageRtException nmsg = (NotifyMessageRtException) ex;
			return sendMsgHtml(nmsg.getCleanMessageOrMessage());
		} else if (ex instanceof INetRsp.NetResponseException) {
			return sendMsgHtml(((INetRsp.NetResponseException) ex).rsp);
		} else if (ex instanceof IllegalHttpStatusException) {
			IllegalHttpStatusException e = (IllegalHttpStatusException) ex;
			String message;
			if (APP.IS_PROM_ENABLE) {
				message = e.code() + "";
			} else if (APP.IS_DEBUG_ENABLE) {
				message = IBotMsg.wrapPreMd(ex.getMessage() + "\n" + ERR.getStackTrace(ex));
			} else {
				message = ex.getMessage();
			}
			return sendMsgMdQk(message);
		} else if (ex instanceof BotMsgException) {
			return sendMsgHtml(((BotMsgException) ex).level(), ex.getMessage(), ex);
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
		L.warn("Note found base tg error for : " + ERR.getAllMessages(ex));
		return sendMsgMd(IBotMsg.wrapPreMd(ERR.toStringRoleMode(ex)));
	}

	protected Serializable sendMsgHtml(BotMsgLevel level, String message, Object context) {
		switch (level) {
			case INFO:
				return sendMsgHtml(message);
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

	public Serializable sendError(String message) {
		String err = X.fl("{}:SendError, chatId ({}), route({}), msg|clb({})({})", updateType, getChatIdAny(), cn(), getIsCallbackOrMessage(), getMessageOrCallBackData());
		if (L.isErrorEnabled()) {
			L.error(err);
		}
		if (APP.IS_DEBUG_ENABLE) {
			return sendMsgHtml(err + "\n" + message);
		}
		return null;
	}

	public Object sendMsgF(String message, Object... args) {
		return sendMsgHtml(X.f(message, args));
	}


	public Serializable sendMsgHtml(String message) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + message);
	}

	public Serializable sendMsgMdQk(String message) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + message);
	}

	public Serializable sendMsgHtmlQk(String message) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + message);
	}

	public Serializable sendMsgMd(String message, boolean... wrapMd) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + message);
	}

	public Serializable sendMsgHtml(IBotMsg message) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + message);
	}

	public Serializable sendMsgHtml(Long toChatId, IBotMsg message) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + toChatId + ":" + message);
	}

	public Object sendMsgHtml(Collection<IClb> clb) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + clb);
	}


	public Object sendMsgHtml(String message, List<List<?>> keys) {
		throw new UnsupportedOperationException(cn() + ":sendMessage:" + message + ":" + keys);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Editable Message ----------------------------
	 * *************************************************************
	 */

	public static Serializable getEmsgId(BotRoute botRoute) {
		return botRoute.sendMsgHtml(SYMJ.TIME_DONE + " Loading..");
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
