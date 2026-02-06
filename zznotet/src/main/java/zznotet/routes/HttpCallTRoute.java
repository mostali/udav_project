package zznotet.routes;


import botcore.RouteAno;
import botcore.clb.BotCallback;
import lombok.SneakyThrows;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpc.str.condition.StringConditionType;
import nett.appb.TgRoute;
import mpe.call_msg.HttpCallMsg;

@RouteAno(key = "??", eq = StringConditionType.STARTS)
public class HttpCallTRoute extends TgRoute {

	public HttpCallTRoute() {
		super();
	}

	public HttpCallTRoute(TgRoute route) {
		super(route);
	}

	@Override
	protected void beforeUpdate() {
	}

	@Override
	protected NetUsrId createDefaultUsrId() {
		return NetUsrId.def();
	}

	@SneakyThrows
	@Override
	public Object doUpdateCallback(BotCallback clb, String data2) {
		return null;
	}


	@SneakyThrows
	@Override
	public Object doUpdateMessage(String msgIn) {
		String woPfx = msgIn.substring(2);
		HttpCallMsg httpCallMsg = HttpCallMsg.of(woPfx.trim());
		httpCallMsg.throwIsErr();
		String rsp = httpCallMsg.sendHttpCall100_or_custom400_404(true);
		return rsp;
	}

}
