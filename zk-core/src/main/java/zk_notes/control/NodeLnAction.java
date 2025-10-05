package zk_notes.control;

import mpc.types.tks.FID;
import mpc.types.tks.FIDP;
import mpe.str.CN;
import mpu.X;
import mpu.core.ENUM;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.MouseEvent;
import zk_form.notify.ZKI;
import zk_notes.factory.NFOpen;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.core.NodeActionIO;
import zk_notes.node_state.FormState;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.ZKJS;
import zk_page.ZKME;
import zk_page.ZKR;
import zk_page.events.ECtrl;

public class NodeLnAction {

	public enum HrefTarget {
		_BLANK, WINDOW
	}

	static void doNodeLnAction(Event e, NodeDir nodeDir) {

		MouseEvent mouseEvent = (MouseEvent) e;

		int keys = mouseEvent.getKeys();

		ECtrl eCtrl = ECtrl.of(e);

		FormState comState = nodeDir.stateCom();
		String href = comState.get(CN.HREF, null);
		if (href != null) {

			FIDP fidp = FIDP.of(CN.HREF, CN.TARGET);

			HrefTarget hrefTarget = ENUM.valueOf(comState.get(fidp.toString(), null), HrefTarget.class, true, null);
			switch (eCtrl) {
				case CTRL:
				case ALT:
				case DEFAULT:

					if (hrefTarget == HrefTarget.WINDOW || eCtrl == ECtrl.ALT) {

						ZKR.openWindow800_1200(href);

					} else {

						boolean needBlank = hrefTarget == HrefTarget._BLANK || eCtrl == ECtrl.CTRL;
						ZKR.redirectToLocation(href, needBlank);

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

		boolean allowedRunNode = SecMan.isAllowedRun(nodeDir);

		if (!allowedRunNode) {
			return;
		}

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
