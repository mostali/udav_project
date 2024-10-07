package nett.appb;

import botcore.DefMsg;
import nett.appb.TgDefaultRoute;
import nett.appb.TgRoute;

public class AppTgDefaultRoute extends TgDefaultRoute {
	public AppTgDefaultRoute() {
	}

	public AppTgDefaultRoute(TgRoute route) {
		super(route);
	}

	@Override
	public Object doUpdateMessage(String msg) {
		if ("/".equals(msg) || "/start".equals(msg) || "/help".equals(msg)) {
			String defMsg = DefMsg.readDefMain();
			return defMsg;
		}
		return "not found:" + msg;
	}
}
