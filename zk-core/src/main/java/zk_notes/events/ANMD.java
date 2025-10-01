package zk_notes.events;

import mpc.fs.ext.EXT;
import mpu.X;
import mpu.func.FunctionV1;
import zk_com.base_ctr.Menupopup0;
import zk_notes.ANI;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.libs.PlaneState;
import zk_os.coms.AFC;
import zk_os.core.Sdn;
import zk_os.sec.Sec;
import zk_page.ZKR;
import zk_page.index.RSPath;

import java.nio.file.Path;

public class ANMD {
	public static void applyPlaneLink(Menupopup0 menu, String sd3) {

//		SdTree sdTree = SdTree.of(sdn);
//		TreeState state = sdTree.state();
//		String curVal = state.get(ISecState.SECE, null);
//		boolean userEqualsByLoginOrAlias = SecMan.isUserEqualsByLoginOrAlias(sdn.key());

		if (Sec.isAnonim()) {
			return;
		}

		PlaneState formState = AppStateFactory.ofPlaneName_orCreate(sd3);
		boolean allowedEdit = formState.isAllowedAccess_EDIT();
		if (!allowedEdit) {
			return;
		}

		menu.addMI_EDITOR("Edit plane state", formState.pathFc(), true, EXT.JSON);


		if (!X.emptyFile_NotExist(formState.pathFc())) {
			menu.add_______();
		}

		ANM.ANM_Mark.applySecurityItems(menu, formState);

		menu.add_______();

		Path rpaSd3 = AFC.PLANES.getPlaneDir(sd3);

		menu.addMI_DeleteFile_WithSec(ANI.DELETE_ENTITY + " Delete plane", rpaSd3);

		FunctionV1<String> func = (newPlaneName) -> {
			NodeFileTransferMan.movePlane(sd3, newPlaneName);
			if (Sdn.isEmptySd3()) {
				ZKR.restartPage();
			} else {
				RSPath.toPlane_Redirect(newPlaneName);
			}
		};

		menu.addMI_RenameFile_Cfrm(rpaSd3.toString(), func);


	}
}
