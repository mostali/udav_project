package zznotet.routes;


import botcore.RouteAno;
import botcore.clb.BotCallback;
import lombok.SneakyThrows;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpc.net.IllegalHttpStatusException;
import mpc.str.condition.StringConditionType;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.str.SPLIT;
import mpu.str.STR;
import nett.appb.TgRoute;
import zk_os.AppZosCore;
import udav_net.apis.zznote.NoteApi;
import udav_net.apis.zznote.ItemPath;
import zk_os.core.Sdn;

import java.util.List;

@RouteAno(key = "*", eq = StringConditionType.STARTS)
public class GetNoteTRoute extends TgRoute {

	public GetNoteTRoute() {
		super();
	}

	public GetNoteTRoute(TgRoute route) {
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
//		DataClbMap dq = new DataClbMap(data2);
		return null;
	}

	@SneakyThrows
	@Override
	public Object doUpdateMessage(String msgIn) {

		List<String> lines = SPLIT.allByNL(msgIn);
		String line0 = lines.get(0);

		line0 = line0.substring(1).trim();

		ItemPath itemPath = ItemPath.of(line0);

		ItemPath itemPathDst = Sdn.getPathViaWebContext(itemPath);

		NoteApi noteApi = AppZosCore.createLocalNoteApi();

		boolean isSinglyChar = line0.equals("*");

		String rsp;
		if (isSinglyChar) {
			try {
				rsp = noteApi.GET_items(itemPathDst.sdn());
				rsp = X.empty(rsp) ? "-" : rsp;
			} catch (IllegalHttpStatusException ex) {
				switch (ex.code()) {
					case 404:
						return SYMJ.MOAI;
					case 400:
						return SYMJ.WHAT;
					default:
						throw ex;
				}
			}
		} else {
			try {
				rsp = noteApi.GET_item(itemPathDst);
				rsp = X.empty(rsp) ? "-" : rsp;
			} catch (IllegalHttpStatusException ex) {
				switch (ex.code()) {
					case 404:
						rsp = SYMJ.MOAI;
						break;
					case 400:
						rsp = SYMJ.WHAT;
						break;
					default:
						throw ex;
				}
			}
		}

		return STR.wrapPreAsMd(rsp);
	}

}
