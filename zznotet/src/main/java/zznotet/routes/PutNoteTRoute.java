package zznotet.routes;


import botcore.RouteAno;
import botcore.clb.BotCallback;
import lombok.SneakyThrows;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpc.net.IllegalHttpStatusException;
import mpc.str.condition.StringConditionType;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARR;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import nett.appb.TgRoute;
import zk_os.AppZosCore;
import udav_net.apis.zznote.ItemPath;
import zk_os.core.Sdn;

import java.util.List;

@RouteAno(key = "!", eq = StringConditionType.STARTS)
public class PutNoteTRoute extends TgRoute {

	public PutNoteTRoute() {
		super();
	}

	public PutNoteTRoute(TgRoute route) {
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

		List<String> lines = SPLIT.allByNL(msgIn);
		String line0 = lines.get(0);

		boolean isSinglyChar = line0.equals("!");
		if (isSinglyChar) {
			return "set item";
		}

		lines = ARR.asAL(lines);
		lines.remove(0);

		line0 = line0.substring(1).trim();

		ItemPath itemPath = ItemPath.of(line0);

		ItemPath itemPathDst = Sdn.getPathViaWebContext(itemPath);

		try {
//			String bodyLines = JOIN.allBy(STR.NL_HTML, lines);
			String body = JOIN.allByNL(lines);
			String rsp = AppZosCore.createLocalNoteApi().PUT_item(itemPathDst, body, true);
			return X.empty(rsp) ? "-" : rsp;
		} catch (IllegalHttpStatusException ex) {
			switch (ex.code()) {
				case 400:
					return ex.getMessage();
				case 404:
					return SYMJ.MOAI;
				default:
					throw ex;
			}
		}
	}

}
