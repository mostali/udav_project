package zk_com.core;

import mpc.num.UNum;
import mpu.core.ARG;
import mpu.func.FunctionV1;
import mpu.str.UST;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Events;
import zk_com.base_ext.DoubleLn;
import zk_notes.AppNotesProps;
import zk_notes.control.NodeLn;
import zk_os.AppZosConfig;
import zk_os.AppZosProps;
import zk_page.ZKS;
import zk_page.core.IPageCom;
import zk_page.node_state.FormState;

public interface IZDnd {

	static void initDND(HtmlBasedComponent com, FunctionV1... afterUpdateClb) {

		/**
		 * Drag&Drop Behaviour
		 */

		ZKS.DRAG_DROP(com, false, true);

		com.addEventListener(Events.ON_DROP, e ->

		{

			DropEvent de = (DropEvent) e;

			Component target = de.getDragged();

			if (!(target instanceof IZState)) {
				IZCom.L.info("Store event Drop&Drag state not supported for class without IZState >>> " + target.getClass().getSimpleName() + " <<< " + target);
				return;
			}

			IZState izState = (IZState) target;

			FormState storeState;
			if (izState instanceof NodeLn) {
				storeState = izState.getComState_JSON(true);
			} else if (IPageCom.isPageCom(izState)) {
				IPageCom pageCom;
				if (izState instanceof DoubleLn) {
					pageCom = (IPageCom) ((DoubleLn) izState).getComs().get(0);
				} else {
					pageCom = (IPageCom) izState;
				}
				String name = pageCom.getPageComName();
				storeState = izState.getPageComState_JSON(name, true);
			} else {
				storeState = izState.getFormState_PROPS(true);
			}
			int step = UST.INT(AppZosProps.AUTO_GRID_PX.getValueSplited(",")[0], 20);
			storeState.update("left", UNum.round(de.getX(), step) + "px");
			storeState.update("top", UNum.round(de.getY(), step) + "px");
//			storeState.update("left", de.getX() + 10 + "px");
//			storeState.update("top", de.getY() + 10 + "px");

			if (ARG.isDef(afterUpdateClb)) {
				ARG.toDef(afterUpdateClb).apply(de);
			}
		});

	}

}
