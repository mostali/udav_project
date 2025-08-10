package zk_notes.node_srv.jarcall;

import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.pare.Pare3;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treeitem;
import zk_com.base.Ln;
import zk_form.tree.ZJarViewTree;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.core.PlayContainer;
import zk_page.ZKM;

import java.util.ArrayList;
import java.util.List;

public class JarPlayContainer {

	public static PlayContainer toPlayContainer(PlayContainer.PlayLn playLn) {
		List<Component> listComs = new ArrayList();

		listComs.add(playLn);

//		listComs.add(new ShowZChoiceLn(playLn.node));
		listComs.add(new ThreeDd(playLn.node));

		return new PlayContainer(listComs.toArray(new Component[listComs.size()]));
	}

//	public static class ShowZChoiceLn extends Ln {
//		public ShowZChoiceLn(NodeDir node) {
//			super(" " + SYMJ.STAR_SHOTING);
//			title("Configure z-call");
//			addEventListener(e -> {
//				HtmlBasedComponent com = (HtmlBasedComponent) new ZJarViewTree(new String[]{"/opt/appVol/.bin/jira-mod.jar", "mp.jira"}) {
//					@Override
//					public void onHappensSelect(SelectEvent event, Pare3<TreeNode, Treeitem, Boolean> selected) {
////						super.onHappensSelect(event, selected);
//						X.p("Select::::" + selected.key().getModel().getRoot() + ":" + selected.ext());
//					}
//				}.inlineBlock().width("200px");
//				ZKM.showModal("Choice", com);
//			});
//		}
//	}

}
