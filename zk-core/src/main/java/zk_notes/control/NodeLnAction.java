package zk_notes.control;

import mpe.str.CN;
import mpu.X;
import mpu.core.ENUM;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.MouseEvent;
import zk_form.notify.ZKI;
import zk_notes.factory.NFForm;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVM;
import zk_notes.node_state.EntityState;
import zk_notes.node_state.ObjState;
import zk_notes.std_actions.WebNodeAction;
import zk_os.sec.UO;
import zk_page.ZKC;
import zk_page.ZKJS;
import zk_page.ZKME;
import zk_page.ZKR;
import zk_page.events.ECtrl;
import zk_page.index.RSPath;

public class NodeLnAction {

	public enum HrefTarget {
		BLANK, WINDOW, _BLANK
	}

	static void doNodeLnAction(Event e, NodeDir nodeDir) {

		MouseEvent mouseEvent = (MouseEvent) e;

		int keys = mouseEvent.getKeys();

		ECtrl eCtrl = ECtrl.ofAsCtrl(e);

		if (applyHrefBeahviours(nodeDir, eCtrl)) {
			return; //ok - it finish
		}

//		if (applyNvmBehviours(nodeDir, eCtrl)) {
//			return;
//		}

		ZKJS.setAction_ShakeEffect(e.getTarget());

		switch (eCtrl) {
			case CTRL_SHIFT:
			case CTRL_ALT:
			case CTRL:
				if (UO.RUN.isAllowed(nodeDir)) {
					WebNodeAction.of(nodeDir).doAction(keys);
				}
				break;

			case ALT:
				if (UO.EDIT.isAllowed(nodeDir)) {
					String rslt = nodeDir.state().readFcDataOk(null);
					if (X.empty(rslt)) {
						ZKI.infoAfterPointer("Empty OK data", ZKI.Level.WARN);
					} else {
						ZKME.textReadonly("OK data", rslt, true);
					}
				}
				break;

			case SHIFT:
				if (UO.EDIT.isAllowed(nodeDir)) {
					String rsltErr = nodeDir.state().readFcDataErr(null);
					if (X.empty(rsltErr)) {
						ZKI.infoAfterPointer("Empty ERR data", ZKI.Level.WARN);
					} else {
						ZKME.textReadonly("ERR data", rsltErr, true);
					}
				}
				break;

			case SHIFT_ALT:
			case CTRL_ALT_SHIFT:
			case DEFAULT:
			default:

				NFForm.openFormOrCloseToggle(nodeDir, ZKC.getFirstWindow());

				break;
		}
	}

	private static boolean applyNvmBehviours(NodeDir nodeDir, ECtrl eCtrl) {
		NVM nvm = nodeDir.nvm_first_auto_cached();
		if (nvm != null) {
			return true;
		}
		switch (eCtrl) {
			case CTRL:
				RSPath.toPageItem_OWin(nodeDir.nodeID());
				return true;

//			case ALT:
//			case DEFAULT:
//			case SHIFT:
//			case SHIFT_ALT:
//			case CTRL_SHIFT:
//			case CTRL_ALT:
//			case CTRL_ALT_SHIFT:
			default:
				return false;
		}

	}

	private static boolean applyHrefBeahviours(NodeDir nodeDir, ECtrl eCtrl) {
		ObjState comState = nodeDir.stateCom();
		String href = comState.get(CN.HREF, null);
		if (href == null) {
			return false;
		}

		HrefTarget hrefTarget = ENUM.valueOf(comState.get(EntityState.HREF_TARGET, null), HrefTarget.class, true, null);
		switch (eCtrl) {
			case CTRL:
			case ALT:
			case DEFAULT:

				if (hrefTarget == HrefTarget.WINDOW || eCtrl == ECtrl.ALT) {

					ZKR.openWindow800_1200(href);

				} else {

					boolean needBlank = hrefTarget == HrefTarget.BLANK || eCtrl == ECtrl.CTRL || hrefTarget == HrefTarget._BLANK;
					ZKR.redirectToLocation(href, needBlank);

				}
				return true;

			case SHIFT:
			case SHIFT_ALT:
			case CTRL_SHIFT:
			case CTRL_ALT:
			case CTRL_ALT_SHIFT:
			default:
				//ok - go specific action

				return false;

		}
	}
}
