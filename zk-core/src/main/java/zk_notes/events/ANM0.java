package zk_notes.events;

import mpc.env.Env;
import mpu.Sys;
import zk_com.base_ctr.Menupopup0;
import zk_notes.ANI;
import zk_notes.node.NodeDir;

public class ANM0 {

	public static void applyMenu_FormFileItem(Menupopup0 menu, NodeDir node) {
		{

			menu.add_______();

			ANM.applyMenu_OpenAs(menu, node);

			menu.add_______();

			ANM.applyMenu_Mark(menu, node);

			menu.add_______();

			if (Env.isLocalDevMashine()) {

				AppEventsFD.applyEvent_OPENDIR_OS(menu, node.toPath());

				if (!node.state().emptyData()) {
					menu.addMenuItem(ANI.OS_OPEN + "Open in Code - Data", e -> Sys.open_Code(node.state().pathFc()));
				}

				if (!node.state().emptyDataProps()) {
					menu.addMenuItem(ANI.OS_OPEN + "Open in Code - Props", e -> Sys.open_Code(node.state().pathProps()));
				}

			}
			menu.add_______();

			AppEventsFD.applyEvent_OPENDIR(menu, node.toPath());
//			menu.addMenuitem(ANI.OS_OPEN + "Open Dir", SimpleDirView.getEventOpenDirViewWithSimpleMenu(node.toPath()));

		}
	}

}
