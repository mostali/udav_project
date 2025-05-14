package zk_notes.events;

import mpc.env.Env;
import mpu.Sys;
import zk_com.base_ctr.Menupopup0;
import zk_notes.ANI;
import zk_notes.node.NodeDir;
import zk_os.AFC;

public class ANM0 {

	public static void applyMenu_FormFileItem(Menupopup0 menu, NodeDir node) {
		{

			menu.add_______();

			AppEventsFD.applyEvent_OPENTREE(menu, AFC.EVENTS.getRpaEventsStatePath(node));

			ANM.applyMenu_Mark(menu, node);

			menu.add_______();

			if (Env.isLocalDevMashine()) {

				AppEventsFD.applyEvent_OPENDIR_OS(menu, node.toPath());

				if (!node.state().emptyData()) {
					menu.addMI(ANI.OS_OPEN + " Open in Code - Data", e -> Sys.open_Code(node.state().pathFc()));
				}

				if (!node.state().emptyDataProps()) {
					menu.addMI(ANI.OS_OPEN + " Open in Code - Props", e -> Sys.open_Code(node.state().pathProps()));
				}

			}

		}
	}

}
