package zk_notes.node_srv.types.jarMsg;

import org.zkoss.zk.ui.Component;
import zk_notes.node_srv.PlayContainer;

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
