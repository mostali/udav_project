package zk_notes;

import mpc.env.Env;
import mpc.str.sym.SYMJ;
import mpu.Sys;
import mpu.X;
import zk_com.base_ctr.Menupopup0;
import zk_notes.apiv1.client.NoteApi;
import zk_old_core.dirview.SimpleDirView;
import zk_page.ZKJS;
import zk_page.node.NodeDir;

public class ANM0 {

	public static void applyMenu_FormFileItem(Menupopup0 menu, NodeDir node) {
		{

			menu.add_______();

			ANM.applyMenu_OpenAs(menu, node);

			menu.add_______();

			ANM.applyMenu_Mark(menu, node);

			menu.add_______();

			if (Env.isLocalDevMashine()) {

				menu.addMenuitem(ANI.OS_OPEN + "Open Dir OS", e -> Sys.open_Nautilus(node.fPath().toString()));

				if (!node.state().emptyData()) {
					menu.addMenuitem(ANI.OS_OPEN + "Open in Code - Data", e -> Sys.open_Code(node.state().pathFc()));
				}

				if (!node.state().emptyDataProps()) {
					menu.addMenuitem(ANI.OS_OPEN + "Open in Code - Props", e -> Sys.open_Code(node.state().pathProps()));
				}

			}
			menu.add_______();

			menu.addMenuitem(ANI.OS_OPEN + "Open Dir", e -> SimpleDirView.openWithSimpleMenu(node.fPath())._modal(true));

		}
	}

}
