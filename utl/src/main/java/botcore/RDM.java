package botcore;

//RouteDebugMode
public enum RDM {
	ON, OFF, DEBUG,
	;//DEBUG_SINGLE_ROUTE;

	public static boolean isOffMode(Class<BotRoute> routeClass) {
		RouteAno ano = RootRoute.getRouteAno(routeClass);
		return ano != null && ano.mode() == OFF;
	}

	public static boolean isMode(Class<BotRoute> routeClass, RDM mode) {
		RouteAno ano = RootRoute.getRouteAno(routeClass);
		return ano != null && ano.mode() == mode;
	}
}
