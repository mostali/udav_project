package botcore;

public enum RDT {
	ON, OFF, DEBUG;

	public static boolean hasDebugMode(Class<BotRoute> routeClass) {
		RouteAno ano = RootRoute.getRouteAno(routeClass);
		return ano != null && ano.mode() == DEBUG;
	}

	public static boolean isOffMode(Class<BotRoute> routeClass) {
		RouteAno ano = RootRoute.getRouteAno(routeClass);
		return ano != null && ano.mode() == OFF;
	}
}
