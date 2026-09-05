package botcore;

import botcore.clb.BotCallback;
import botcore.clb.QuestState;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.IT;
import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpu.pare.Pare;
import mpc.rfl.RFL;
import mpc.rfl.UReflExt;
import mpc.rfl.UReflScanner;
import mpu.str.Rt;
import mpu.str.UST;
import mpu.core.QDate;

import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class RootRoute {

	public static final Logger L = LoggerFactory.getLogger(RootRoute.class);

	@Getter
	@Setter
	private String botId;

	public Long getBotIdAsLong(Long... defRq) {
		return UST.LONG(getBotId(), defRq);
	}

	private BotRoute defaultRoute;

	public BotRoute getDefRoute() {
		return defaultRoute;
	}

	public void setDefaultRoute(BotRoute defaultRoute) {
		this.defaultRoute = defaultRoute.setRootRoute(this);
	}

	@Getter
	private final ConcurrentHashMap<String, Collection<BotRoute>> mapRoutes = new ConcurrentHashMap<>();

	public ConcurrentHashMap<String, Collection<BotRoute>> getMapRoutes() {
		return mapRoutes;
	}

	public static final ConcurrentHashMap<String, List<Class>> _BOTAPP_ROUTES = new ConcurrentHashMap<>();

	public static void regAppRoutes(String btype, String... routePackages) {
		List<Class> routes = findAllRouteClasses(routePackages);
		_BOTAPP_ROUTES.put(btype, (List) IT.NE(routes, "Reg App routes is empty", btype));
		if (L.isInfoEnabled()) {
			String head = X.fl("BotType '{}' regAppRoutes  >>> {}", btype, ARR.as(routePackages));
			Rt.buildReport(routes, head, L);
		}
	}

	@SneakyThrows
	public RootRoute(String botId, List<Class<BotRoute>> routes) {
		this(botId, null, routes);
	}

	@SneakyThrows
	protected RootRoute(String botId, String[] bot_types, List<Class<BotRoute>> routes) {
		this.botId = botId;
		IT.notNullAny(bot_types, routes);
		if (bot_types == null) {
			mapRoutes.put(botId, initRoutesAndSort(this, routes, true));
		} else {
			List<BotRoute> inited = null;
			for (String btype : bot_types) {
				List<Class<BotRoute>> classRoutes = (List) IT.NE(_BOTAPP_ROUTES.get(btype), "App routes is empty", btype);
				List<BotRoute> values = initRoutesAndSort(this, classRoutes, false);
				inited = (List) mapRoutes.get(botId);
				if (inited == null) {
					mapRoutes.put(botId, inited = values);
				}
				inited.addAll(values);
			}
			inited = new ArrayList<>(new HashSet(inited));
			RootRoute.sortRoutes(inited);
			mapRoutes.put(botId, inited);
		}
		initDefaultRoute();
	}

	protected void initDefaultRoute() {
	}


	private final Map<Long, BotRoute> _QUESTS = new HashMap<>();

	public void setQuestState(BotRoute route) {
		_QUESTS.put(route.getChatIdAny(), route);
	}

	public <R extends BotRoute> R findRoute(Class<R> tgRouteClass, R... defRq) {
		for (Collection<BotRoute> allRoutes : getMapRoutes().values()) {
			for (BotRoute tgRoute : allRoutes) {
				if (tgRoute.getClass().isAssignableFrom(tgRouteClass)) {
					return (R) tgRoute;
				}
			}
		}
		return ARG.toDefRq(defRq);
	}

	public BotRoute findRoute(IBotUpdate update) {
		List<Pare> specialTypes = update.getSpecialTypes();
		if (X.notEmpty(specialTypes)) {
			for (Collection<BotRoute> allRoutes : getMapRoutes().values()) {
				for (BotRoute botRoute : allRoutes) {
					if (botRoute.isCanHandleSpecilTypes(specialTypes)) {
						return botRoute;
					}
				}
			}
			return null;
		}
		String msg = update.getMessageText();
		for (Collection<BotRoute> allRoutes : getMapRoutes().values()) {
			for (BotRoute botRoute : allRoutes) {
				if (botRoute.isRouteMatchesMessageBody(msg)) {
					return botRoute;
				}
			}
		}

		String data = update.getCallbackData();
		if (data == null) {
			return null;
		}
		BotRoute routeByCallback = findRouteByCallback(update);
		if (routeByCallback == null) {
			if (L.isWarnEnabled()) {
				L.warn("RootRoute '{}' not found by Callback by data:{}", getClass().getSimpleName(), data);
			}
		}
		return routeByCallback;

	}

	protected BotRoute findRouteByCallback(IBotUpdate update) {
		return null;
	}

	public BotRoute findTargetRoute(BotRoute aliasRoute, BotRoute... defRq) {
		for (Collection<BotRoute> allRoutes : mapRoutes.values()) {
			for (BotRoute tgRoute : allRoutes) {
				if (tgRoute.isOwnAliasRoute(aliasRoute)) {
					return tgRoute;
				}
			}
		}
		return ARG.toDefRq(defRq);
	}

	public void onUpdatesReceived(List<? extends IBotUpdate> updates) {
		for (IBotUpdate update : updates) {
			onSingleUpdate(update);
		}
	}

	public void onSingleUpdate(IBotUpdate botUpdate) {
		onSingleUpdate(botUpdate, true);
	}

	public boolean onSingleUpdate(IBotUpdate botUpdate, boolean handleWithDefaultRouteIfNotFound) {

		CHECK_AND_RUN_NATIVE_SRV();

		long chatId = botUpdate.getChatIdAny();
		BotRoute questRoute = _QUESTS.get(chatId);
		if (questRoute != null) {
			_QUESTS.remove(chatId);
			if (botUpdate.getCallbackData() == null) {
				if (QuestState.isExpired(questRoute.getQuestState())) {
					if (L.isDebugEnabled()) {
						L.debug("QuestState({}) is expired ({})", chatId, questRoute.getQuestState().getDateExpired().f(QDate.F.MONO17NF));
					}
				} else {
					questRoute.doUpdate(botUpdate);
					return true;
				}
			} else {
				//we wait answer, but user click callback
			}
		}

		BotRoute findedRoute = findRoute(botUpdate);

		BotRoute aliasRoute = null;
		BotRoute targetRoute;
		if (findedRoute != null && findedRoute.isAlias()) {
			aliasRoute = findedRoute;
			targetRoute = findTargetRoute(aliasRoute);
			pathUpdateMessage(aliasRoute, targetRoute, botUpdate);
			if (L.isDebugEnabled()) {
				L.debug("AliasRoute found & apply: [ {} >>> {} ] >>> {}", BotRoute.toStringLog(findedRoute), BotRoute.toStringLog(targetRoute), botUpdate);
			}
		} else {
			targetRoute = findedRoute;
			if (L.isDebugEnabled()) {
				L.debug("RequestRoute found: [ {} ] >>> {}", BotRoute.toStringLog(targetRoute), botUpdate);
			}
		}

		if (targetRoute == null) {
			if (handleWithDefaultRouteIfNotFound) {
				targetRoute = defaultRoute.newInstance();
			} else {
				return false;
			}
		} else {
			List<BotCallback> callbacks = targetRoute.getCallbacks();
			BotRoute uniqTargetRoute = targetRoute.newInstance();
			if (aliasRoute != null) {
				uniqTargetRoute.setFromKeyAliasRoute(aliasRoute.getKeyRoute());
			}

			if (X.notEmpty(callbacks)) {
				if (targetRoute.hasNoStaticCallbacks) {
					BotRoute.initCallbacks(uniqTargetRoute, false);
				}
			}
			targetRoute = uniqTargetRoute;
		}

		if (L.isDebugEnabled()) {
			String msg = "RequestRoute [doUpdate] / cn[{}] / key:[{}] / alias:[{}] / hc:[{}]";
			L.debug(msg, targetRoute.cn(), targetRoute.getKeyRoute(), targetRoute.getKeyAliasRoute(), targetRoute.hashCode());
		}

		boolean singleThread = false;
		if (singleThread) {
			targetRoute.doUpdate(botUpdate);
		} else {
			BotRoute finalTargetRoute = targetRoute;

			if (L.isDebugEnabled()) {
				L.debug("Run update from route [{}]", targetRoute);
			}
			CompletableFuture<Void> runBotTask = CompletableFuture.runAsync(() -> finalTargetRoute.doUpdate(botUpdate));

			runBotTask.exceptionally(e -> {
				L.error("Error on send BotRoute " + finalTargetRoute, e);
				return null;
			});

//			new Thread(, RFL.scn(targetRoute)).start();
		}

		return true;
	}

	protected void pathDecodeUpdate(IBotUpdate botUpdate) {
		throw new UnsupportedOperationException("need child impl in:" + getClass() + ":" + botUpdate);
	}

	protected void pathUpdateMessage(BotRoute aliasRoute, BotRoute tgRoute, IBotUpdate botUpdate) {
		throw new UnsupportedOperationException("need child impl in:" + getClass() + ":" + botUpdate);
	}

	protected String getNewMessageFromAlias(BotRoute aliasRoute, BotRoute targetRoute, String orgMsg) {
		String lastPart = orgMsg.substring(aliasRoute.getKeyRoute().length());
		switch (targetRoute.getKeyEq()) {
			case EQ:
				return targetRoute.getKeyRoute();
			case STARTS:
			case STARTSIC:
				return targetRoute.getKeyRoute() + "" + lastPart;
			default:
				throw new WhatIsTypeException(targetRoute.getKeyEq());
		}
	}

	private long timeNextRun = -1;
	private static int DEALY_RUN_NS_MIN = 30;

	private void CHECK_AND_RUN_NATIVE_SRV() {
		long runTime = System.currentTimeMillis();
		if (runTime < timeNextRun) {
			return;
		} else if (timeNextRun == -1) {
			timeNextRun = runTime + TimeUnit.MINUTES.toMillis(DEALY_RUN_NS_MIN);
			return;
		}
		if (!_QUESTS.isEmpty()) {
			if (L.isDebugEnabled()) {
				L.debug("NativeRootRoute Services Quest ({}) run INIT", _QUESTS.size());
			}
			Iterator<Map.Entry<Long, BotRoute>> it = _QUESTS.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Long, BotRoute> quest = it.next();
				if (QuestState.isExpired(quest.getValue().getQuestState())) {
					it.remove();
				}
			}
			if (L.isDebugEnabled()) {
				L.debug("NativeRootRoute Services Quest ({}) running FINAL", _QUESTS.size());
			}
		}
		timeNextRun = runTime + TimeUnit.MINUTES.toMillis(DEALY_RUN_NS_MIN);
		if (L.isDebugEnabled()) {
			L.debug("NativeRootRoute Services was runned successfully, next run after:" + QDate.of(timeNextRun).f(QDate.F.MONO17NF));
		}
	}

	public void onClosing() {
		if (L.isDebugEnabled()) {
			L.debug("RR:onClosing");
		}
	}

	public static RouteAno getRouteAno(Class routeClass) {
		if (routeClass.isAnnotationPresent(RouteAno.class)) {
			RouteAno ano = (RouteAno) routeClass.getAnnotation(RouteAno.class);
			return ano;
		}
		return null;
	}

	public static List<BotRoute> initRoutesAndSort(RootRoute rootRoute, List<Class<BotRoute>> routeClasses, boolean sort) throws ClassNotFoundException, IOException {
		List<BotRoute> routes = initRoutes(rootRoute, routeClasses, APP.IS_DEBUG_ENABLE);
		if (sort) {
			sortRoutes(routes);
		}
		return routes;
	}

	public static void sortRoutes(List<BotRoute> routes) {
		Collections.sort(routes, (o1, o2) -> o1.z > o2.z ? 1 : -1);
	}

	public static List<BotRoute> initRoutes(RootRoute rootRoute, List<Class<BotRoute>> routeClasses, boolean debugMode) throws ClassNotFoundException, IOException {

		List<BotRoute> botRoutes = new ArrayList<>();

		List<Class<BotRoute>> targetRoutes = filterMode(routeClasses, debugMode);

		for (Class<BotRoute> routeClass : targetRoutes) {

			BotRoute inst = RFL.instEmptyConstructor(routeClass);

			inst.setRootRoute(rootRoute);

			BotRoute.initCallbacks(inst, true);
			BotRoute.initCallbacks(inst, false);

			if (L.isInfoEnabled()) {
				L.info("Route '{}' is CREATED\n{}", routeClass.getSimpleName(), inst);
			}

			botRoutes.add(inst);

		}
		return botRoutes;
	}


	private static List<Class<BotRoute>> filterMode(List<Class<BotRoute>> routeClasses, boolean appDebugMode) {
		List<Class<BotRoute>> targetRoutes = new ArrayList();
		//check DEBUG
		for (Class<BotRoute> routeClass : routeClasses) {

			if (RDM.isMode(routeClass, RDM.OFF)) {
				if (L.isInfoEnabled()) {
					L.info("Route '{}' OFF", routeClass.getSimpleName());
				}
				continue;
			}

			boolean isRouteInOnlyDebugMode = RDM.isMode(routeClass, RDM.DEBUG);
			if (!isRouteInOnlyDebugMode) {
				targetRoutes.add(routeClass);
				continue;
			}
			//route is InOnlyDebugMode
			if (appDebugMode) {
				targetRoutes.add(routeClass);
				if (L.isInfoEnabled()) {
					L.info("Route '{}' only in DEBUG-MODE", routeClass.getSimpleName());
				}
			}

		}
		return targetRoutes;
	}

	public static List<Class> findAllRouteClasses(String[] routePackages) {
		List<Class> route_classes;
		if (true) {
			route_classes = UReflScanner.getAllPackageClassViaClassgraph(routePackages, RouteAno.class);
		} else {
			route_classes = new LinkedList();
			route_classes.addAll(UReflExt.getAllPackageClassess_viaDoubleSearch(true, RouteAno.class, RouteAno.class.getClassLoader(), routePackages));
		}
		if (L.isInfoEnabled()) {
			String head = X.fl("findAllRouteClasses '{}'\n", ARR.as(route_classes));
			Rt.buildReport(route_classes, head, L);
		}
		return route_classes;
	}


}
