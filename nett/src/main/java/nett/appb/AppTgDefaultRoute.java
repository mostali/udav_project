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
	public Object doUpdateMessage(String msgIn) {
		if ("/".equals(msgIn) || "/start".equals(msgIn) || "/help".equals(msgIn)) {
			String defMsg = DefMsg.readDefMain();
			return defMsg;
		}
		return "not found:" + msgIn;
	}
}
