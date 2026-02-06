package zk_page.index.tabs;

import mp.utl_odb.tree.AppPropDef;
import mpu.X;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tabs;
import mpe.call_msg.core.NodeID;
import zk_com.base.Lb;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_com.tabs.Tab0;
import zk_com.tabs.Tabbox0;
import zk_com.tabs.Tabpanel0;
import zk_form.notify.ZKI_Quest;
import zk_notes.ACN;
import zk_notes.events.ANMP;
import zk_notes.node.NodeDir;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.impl.PageState;
import zk_os.walkers.PagesWalker;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.core.FinderPSP;
import zk_page.index.RSPath;
import zk_notes.node_state.ObjState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PagesTabs extends MapTabs {

	final String sd3;
	public final boolean isSysPagename;

	public PagesTabs(AppPropDef<Boolean> isVerticalAppPropDef, String sd3, boolean isSysPagename) {
		super(isVerticalAppPropDef);
		this.sd3 = sd3;
		this.isSysPagename = isSysPagename;
	}

	@Override
	protected void init() {
		super.init();

		Map map = new LinkedHashMap<>();

		if (!isSysPagename) {

			new PagesWalker(sd3) {
				@Override
				protected Boolean walkPage(String pagename, PageState pageState) {
//					if (AFC.isIndex(pagename)) {
//						return true;
//					}
					map.put(new PageTabpanel0(pagename, Pare.of(_plane, pagename), false), null);
					return true;
				}
			}.withSysPages(false).doWalk();

		} else {

			//it system page

			Set<String> allBusyPages = FinderPSP.getAllBusyPages(sd3);
			for (String sysPage : allBusyPages) {
				map.put(new PageTabpanel0(sysPage, Pare.of(sd3, sysPage), isSysPagename), null);
			}

		}


		Tabbox0 tabbox0 = initWithTabs(map, true);
		Tabs tabs = tabbox0.getTabs();
		for (Component child : tabs.getChildren()) {

			Tab0 tab0 = (Tab0) child;

			Pare<String, String> sdn = PagesTabs.getPageSdn(tab0);

			ZKS.DRAG_DROP(tab0, true, true);

			tab0.addEventListener(Events.ON_DROP, e -> {
				Tab0 dragged = (Tab0) ((DropEvent) e).getDragged();
				NodeID movedNodeID = NodeID.of(FormsTabs.getNodeID(dragged));
				String dstPagename = sdn.val();
				String msg = X.f("Move note <%s> to page <%s>", movedNodeID.item(), dstPagename);
				ZKI_Quest.showMessageBoxBlueYN("Moving note", msg, (yes) -> {
					if (yes) {
						NodeFileTransferMan.moveItemNoteToPage(NodeDir.ofNodeId(movedNodeID), dstPagename);
//						ZKI.infoRef("Ok - " + msg);
						ZKR.restartPage();
					}
				});
			});


			Menupopup0 menu = tab0.getOrCreateMenupopup(this);

			ANMP.applyPageLink(menu, sdn, true);

//			Path dirPage = AFC.DIR_PAGE(sdn);
//			AppEventsFD.applyEvent_OPENDIR(menu, dirPage);
//			menu.add_______();

//			menu.addMenuItem(ANM.DELETE_ENTITY + " Delete page", e -> {
//				NodeFileTransferMan.deletePage(dirPage);
//				ZKR.restartPage();
//			});
//
//			AppEventsFD.applyEvent_OPENDIR_OS(menu, dirPage);
		}

	}

	public static Pare<String, String> getPageSdn(Tab0 tab0) {
		return (Pare<String, String>) tab0.getChildren().get(0).getAttribute(ACN.PAGE);
	}


	public class PageTabpanel0 extends Tabpanel0 {

		public final Pare<String, String> sdn;
		public final boolean isSysPagename;

//		class PageTab0 extends Tab0 {
//
//		}

		public PageTabpanel0(String tab0as, Pare<String, String> sdn, boolean isSysPagename) {
			super(tab0as);
			this.sdn = sdn;
			this.isSysPagename = isSysPagename;

			this.tab0 = Tab0.of(new Lb(tab0as).attr(ACN.PAGE, sdn));

		}

		@Override
		public void init() {
			super.init();

//			ZKS.PADDING(this,1);

			onEventSelect(e -> {
				clearLazyTabContent();
				if (isSysPagename) {
//				String link = new LocalNoteApi().zApiUrl.GET_toPage(sdn);
					String link = RSPath.PAGE.toPageLink(sdn);
					appendChildLazyTabContent(Ln.ofEmojBlank(link, "open"));
				} else {
					appendChildLazyTabContent(new FormsTabs(getIsVerticalAppPropDef(), sdn));
				}

				getParent().invalidate();

				Sd3Tabs.changeCurrentUrl(sd3, sdn.val(), null);
			});

		}

	}
}
