package zk_notes.events;

import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import org.zkoss.zul.Menuitem;
import zk_com.base_ctr.Menupopup0;
import zk_notes.ANI;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.ObjState;
import zk_os.coms.AFC;
import zk_os.sec.SecMan;
import zk_page.index.RSPath;
import zk_page.index.control.TopSpacePanel;

import java.nio.file.Path;
import java.util.function.Supplier;

public class ANMP {

	public static void applyPageLink(Menupopup0 menu, Pare<String, String> sdn, boolean... showLinkToPage) {

//		SdTree sdTree = SdTree.of(sdn);
//		TreeState state = sdTree.state();
//		String curVal = state.get(ISecState.SECE, null);
//		boolean userEqualsByLoginOrAlias = SecMan.isUserEqualsByLoginOrAlias(sdn.key());

		if (ARG.isDefEqTrue(showLinkToPage)) {
			menu.addMI_Href_v1(SYMJ.ARROW_WN + "Open Page", RSPath.PAGE.toPageLink(sdn), true);
		}

		if (SecMan.isAnonimUnsafe()) {
			return;
		}

		ObjState pageState = AppStateFactory.forPage(sdn);
		boolean allowedEdit = pageState.isAllowedAccess_EDIT();
		if (!allowedEdit) {
			return;
		}

		ObjState.Fields pageFields = pageState.fields();

		Path pageStatePath = pageState.pathFc();

		AppEventsPage.applyEvent_EditPageProps(menu, pageStatePath);

		menu.add_______();

		Path pageDir = AFC.PAGES.getDir(sdn);

		{//FIX
			Supplier<String> get = () -> SYMJ.FIX + (pageFields.get_FIXED(false) ? SYMJ.OK_GREEN : "") + " Fix on top";
			menu.addMI(get.get(), (e) -> {
				pageFields.set_FIXED(!pageFields.get_FIXED(false));
				TopSpacePanel.replaceFirst(new TopSpacePanel());
				Menuitem key = (Menuitem) e.getTarget();
				key.setLabel(get.get());
			});
		}

		menu.add_______();


		{//RENAME
			FunctionV1 functionV1 = (newPagename) -> {
				RSPath.toPage_Redirect(sdn.keyStr(), newPagename.toString());
//				if (Sdn.isEmptyPlane_WoCheckIndex()) {
//				} else {
//					RSPath.toPlane_Redirect(sdn.keyStr());
//				}
			};
			menu.addMI_RenameFileDirect(pageDir, functionV1);
		}

		{//DELETE
			menu.addMI_DeleteFile_WithSec(ANI.DELETE_ENTITY + " Delete page", pageDir, false);
		}

		menu.add_______();

		AppEventsPage.applyEvent_MovePageToOtherPlane(menu, sdn);

		menu.add_______();

		ANM.ANM_Mark.applySecurityItems(menu, pageState);

		menu.add_______(true);

		AppEventsZip.applyEvent_ZipPage(menu, sdn);
		AppEventsZip.applyEvent_DownloadZipPage(menu, sdn);
		AppEventsZip.applyEvent_RecoveryPageFromArchive(menu, sdn);

		menu.add_______(true);

		{
			AppEventsFD.applyEvent_OPENDIR_VIEW(menu, pageDir);
		}

		{
			AppEventsFD.applyEvent_OPENDIR_OS(menu, pageDir);
			AppEventsFD.applyEvent_OPENDIR_TERMINAL(menu, pageDir);
		}

	}
}
