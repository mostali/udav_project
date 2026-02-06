package zk_page.index.tabs;

import mp.utl_odb.tree.AppPropDef;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Tabs;
import mpe.call_msg.core.NodeID;
import zk_com.base.Lb;
import zk_com.base_ctr.Menupopup0;
import zk_com.tabs.Tab0;
import zk_com.tabs.Tabbox0;
import zk_com.tabs.Tabpanel0;
import zk_notes.events.ANMF;
import zk_os.walkers.NoteWalker;
import zk_notes.node.NodeDir;
import zk_page.ZKS;

import java.util.LinkedHashMap;
import java.util.Map;

public class FormsTabs extends MapTabs {

	final Pare<String, String> sdn;

	public FormsTabs(AppPropDef<Boolean> isVerticalAppPropDef, Pare<String, String> sdn) {
		super(isVerticalAppPropDef);
		this.sdn = sdn;
	}

	@Override
	protected void init() {
		super.init();

		Map map = new LinkedHashMap<>();

		new NoteWalker(sdn) {
			@Override
			protected Boolean walkForm(NodeDir nodeDir) {

				map.put(new FormTabpanel0(nodeDir), null);

				return true;
			}
		}.doWalk();

		Tabbox0 tabbox0 = initWithTabs(map, true);

		Tabs tabs = tabbox0.getTabs();

		for (Component child : tabs.getChildren()) {

			Tab0 tab0 = (Tab0) child;

			ZKS.DRAG_DROP(tab0, true, true);

			NodeID nodeID = NodeID.of(getNodeID(tab0));
			NodeDir nodeDir = NodeDir.ofNodeId(nodeID);
			Menupopup0 menu = tab0.getOrCreateMenupopup(this);
//			ANM.applyMenu_FormFileItem(menu, nodeDir);
			ANMF.applyForm(menu, nodeDir);

		}
	}

	public static String getNodeID(Tab0 tab0) {
		return (String) tab0.getChildren().get(0).getAttribute(NodeID.KEY);
	}

	public static class FormTabpanel0 extends Tabpanel0 {

		public final NodeDir nodeDir;

		public FormTabpanel0(NodeDir nodeDir) {
			super(nodeDir.nodeName());
			this.nodeDir = nodeDir;
			this.tab0 = Tab0.of(new Lb(nodeDir.nodeName()).attr(NodeID.KEY, nodeDir.nodeId()));
		}

		@Override
		public void init() {
			super.init();

			onEventSelect((e) -> {

				clearLazyTabContent();

				appendChildLazyTabContent(new FormTabView(getTab0(), nodeDir));

				getParent().invalidate();

				Pare<String, String> sdn = nodeDir.sdnPare();

				Sd3Tabs.changeCurrentUrl(sdn.key(), sdn.val(), nodeDir.nodeName());

			});


		}

	}


}
