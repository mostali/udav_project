package zk_notes.control;

import mpe.str.CN;
import mpu.X;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.MouseEvent;
import zk_form.notify.ZKI;
import zk_notes.factory.NFOpen;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.core.NodeActionIO;
import zk_page.ZKC;
import zk_page.ZKJS;
import zk_page.ZKME;
import zk_page.ZKR;
import zk_page.events.ECtrl;

public class NodeLnAction {

	static void doNodeLnAction(Event e, NodeDir nodeDir) {

		MouseEvent mouseEvent = (MouseEvent) e;

		int keys = mouseEvent.getKeys();

		ECtrl eCtrl = ECtrl.of(e);

		String href = nodeDir.stateCom().get(CN.HREF, null);
		if (href != null) {
			switch (eCtrl) {
				case CTRL:
				case ALT:
				case DEFAULT:
					switch (eCtrl) {
						case ALT:
							ZKR.openWindow800_1200(href);
							break;
						case CTRL:
						default:
							ZKR.redirectToLocation(href, eCtrl == ECtrl.CTRL);
					}

					return; //ok - it finish

				case SHIFT:
				case SHIFT_ALT:
				case CTRL_SHIFT:
				case CTRL_ALT:
				case CTRL_ALT_SHIFT:
				default:
					//ok - go specific action

			}
		}

		ZKJS.setAction_ShakeEffect(e.getTarget());

		switch (eCtrl) {
			case CTRL_SHIFT:
			case CTRL_ALT:
			case CTRL:
				NodeActionIO.doEventAction_ActiveWeb(nodeDir, keys);
				break;

			case ALT:
				String rslt = nodeDir.state().readFcDataOk(null);
				if (X.empty(rslt)) {
					ZKI.infoAfterPointer("Empty OK data", ZKI.Level.WARN);
				} else {
					ZKME.textReadonly("OK data", rslt, true);
				}
				break;

			case SHIFT:
				String rsltErr = nodeDir.state().readFcDataErr(null);
				if (X.empty(rsltErr)) {
					ZKI.infoAfterPointer("Empty ERR data", ZKI.Level.WARN);
				} else {
					ZKME.textReadonly("ERR data", rsltErr, true);
				}
				break;

			case SHIFT_ALT:
			case CTRL_ALT_SHIFT:
			case DEFAULT:
			default:
				NFOpen.openOrCloseToggle(nodeDir, ZKC.getFirstWindow());
				break;
		}
	}
}
