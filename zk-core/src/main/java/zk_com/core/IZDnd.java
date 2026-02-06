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
import zk_notes.control.NodeLn;
import zk_os.AppZosProps;
import zk_page.ZKS;
import zk_page.core.IPageCom;
import zk_notes.node_state.ObjState;

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
//				if (target instanceof IZDrop) {
//					TopPanel.findFirst().appendChild(com);
//					NotesSpace.findFirst().appendChild(target);
//					IZCom.L.info("Store [DROPABLE] com by event Drop&Drag state " + target.getClass().getSimpleName() + " <<< " + target);
//					ZKC.getFirstWindow().appendChild(com);
//				} else {
				IZCom.L.info("Store event Drop&Drag state not supported for class without IZState >>> " + target.getClass().getSimpleName() + " <<< " + target);
//				}
				return;
			}

			IZState izState = (IZState) target;

			ObjState storeState;
			boolean isNodeln = izState instanceof NodeLn;
			if (isNodeln) {
				storeState = izState.getComState(true);
			} else if (IPageCom.isPagecom(izState)) {
				IPageCom pageCom;
				if (izState instanceof DoubleLn) {
					pageCom = (IPageCom) ((DoubleLn) izState).getComs().get(0);
				} else {
					pageCom = (IPageCom) izState;
				}
				String name = pageCom.getPageComName();
				storeState = izState.getPagecomState(name, true);
			} else {
				storeState = izState.getFormState(true);
			}

			int step = UST.INT(AppZosProps.AUTO_GRID_PX.getValueSplited(",")[0], 20);
			//List<Component> allNotes = ZKNFinder.findAllNodeCom(nodeDir, isForm, isForm);
			int x = de.getX();
			int y = de.getY();
			if (isNodeln) {
				y += 30;
				x += 8;
			}
			Integer newLeftPx = UNum.round(x, step);
			Integer newTopPx = UNum.round(y, step);
			storeState.fields().set_TOP_LEFT(newTopPx, newLeftPx);
			HtmlBasedComponent targetCom = (HtmlBasedComponent) target;
			targetCom.setTop(ZKS.px(newTopPx));
			targetCom.setLeft(ZKS.px(newLeftPx));

			if (ARG.isDef(afterUpdateClb)) {
				ARG.toDef(afterUpdateClb).apply(de);
			}
		});

	}

}
