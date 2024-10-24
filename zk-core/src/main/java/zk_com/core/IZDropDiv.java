package zk_com.core;

import mpc.fs.UF;
import mpu.core.ARG;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Events;
import zk_com.base.Img;
import zk_com.base.Ln;
import zk_page.ZKS;
import zk_page.node_state.FormState;

public interface IZDropDiv {

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
			FormState comState = izState.getComState_JSON(true);

			comState.updatePropSingle("left", de.getX() + 10 + "px");
			comState.updatePropSingle("top", de.getY() + 10 + "px");

			if (ARG.isDef(afterUpdateClb)) {
				ARG.toDef(afterUpdateClb).apply(de);
			}
		});

	}

}
