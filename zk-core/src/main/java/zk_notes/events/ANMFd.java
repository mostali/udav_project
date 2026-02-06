package zk_notes.events;

import mpc.env.Env;
import zk_com.base_ctr.Menupopup0;
import zk_notes.ANI;
import zk_notes.node.NodeDir;
import zk_os.coms.AFC;

public class ANMFd {

	public static void applyMenu_FdOperations(Menupopup0 menu, NodeDir node) {
		AppEventsFD.applyEvent_OPENDIR_VIEW(menu, node.toPath());
		AppEventsFD.applyEvent_OPENTREE(menu, AFC.EVENTS.getStatePath(node));
		applyMenu_FdOperationsOS(menu, node);
	}

	public static void applyMenu_FdOperationsOS(Menupopup0 menu, NodeDir node) {
		if (Env.isLocalDevMashine()) {
			Menupopup0 menuOs = menu.addInnerMenu(ANI.OS_OPEN + " Open via OS..");
			AppEventsFD.applyEvent_OPENDIR_OS(menuOs, node.toPath());
			AppEventsFD.applyEvent_OPENDIR_TERMINAL(menuOs, node.toPath());

			if (!node.state().emptyData()) {
				AppEventsFD.applyEvent_OPEN_IN_CODE(menuOs,  ANI.OS_OPEN + " Open In Code - with Data", node.state().pathFc());
//				menuOs.addMI(ANI.OS_OPEN + " Open Code - with Data", e -> Sys.open_Code(node.state().pathFc()));
			}

			if (!node.state().emptyDataProps()) {
				AppEventsFD.applyEvent_OPEN_IN_CODE(menuOs, ANI.OS_OPEN + " Open in Code - with Props", node.state().pathProps());
//				menuOs.addMI(ANI.OS_OPEN + " Open in Code - with Props", e -> Sys.open_Code(node.state().pathProps()));
			}

		}
	}

}
