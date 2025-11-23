package zk_notes.events;

import mpc.fs.ext.EXT;
import mpu.X;
import mpu.func.FunctionV1;
import zk_com.base_ctr.Menupopup0;
import zk_notes.ANI;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.impl.PlaneState;
import zk_os.coms.AFC;
import zk_os.core.Sdn;
import zk_os.sec.SecMan;
import zk_page.ZKR;
import zk_page.index.RSPath;

import java.nio.file.Path;

public class ANMD {

	public static void applyPlaneLink(Menupopup0 menu, String sd3) {

//		SdTree sdTree = SdTree.of(sdn);
//		TreeState state = sdTree.state();
//		String curVal = state.get(ISecState.SECE, null);
//		boolean userEqualsByLoginOrAlias = SecMan.isUserEqualsByLoginOrAlias(sdn.key());

		if (SecMan.isAnonimUnsafe()) {
			return;
		}

		PlaneState formState = AppStateFactory.forPlane(sd3);
		boolean allowedEdit = formState.isAllowedAccess_EDIT();
		if (!allowedEdit) {
			return;
		}

		Path pathFc = formState.pathFc();

		menu.addMI_EDITOR(ANI.EDIT_MODE + " Edit plane props", pathFc, true, EXT.JSON);

		if (!X.emptyFile_NotExist(pathFc)) {
			menu.add_______(true);
		}

		ANM.ANM_Mark.applySecurityItems(menu, formState);

		menu.add_______();

		Path rpaSd3 = AFC.PLANES.getPlaneDir(sd3);

		menu.addMI_DeleteFile_WithSec(ANI.DELETE_ENTITY + " Delete plane", rpaSd3);

		FunctionV1<String> func = (newPlaneName) -> {
			NodeFileTransferMan.movePlane(sd3, newPlaneName);
			if (Sdn.isEmptyPlane_WoCheckIndex()) {
				ZKR.restartPage();
			} else {
				RSPath.toPlane_Redirect(newPlaneName);
			}
		};

		menu.addMI_RenameFile_Cfrm(rpaSd3.toString(), func);


	}
}
