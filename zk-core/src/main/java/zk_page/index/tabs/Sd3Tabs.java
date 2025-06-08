package zk_page.index.tabs;

import mp.utl_odb.tree.AppPropDef;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tabs;
import udav_net.apis.zznote.ItemPath;
import udav_net.apis.zznote.NodeID;
import zk_com.base.Lb;
import zk_com.base_ctr.Menupopup0;
import zk_com.tabs.Tab0;
import zk_com.tabs.Tabbox0;
import zk_com.tabs.Tabpanel0;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Messagebox;
import zk_notes.ACN;
import zk_notes.ANI;
import zk_notes.events.ANM;
import zk_notes.events.AppEventsFD;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_os.AFC;
import zk_os.AFCC;
import zk_os.core.Sdn;
import zk_os.walkers.PlaneWalker;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.core.FinderPSP;
import zk_page.index.PageDdChoicer;
import zk_page.index.RSPath;
import zk_notes.node_state.FormState;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Sd3Tabs extends MapTabs {
	public Sd3Tabs(AppPropDef<Boolean> isVerticalAppPropDef) {
		super(isVerticalAppPropDef);
	}

	@Override
	protected void init() {
		super.init();

		Map map = new LinkedHashMap<>();

		new PlaneWalker() {
			@Override
			protected Boolean walkSd3(String sd3, Path dir, FormState planeState) {
				map.put(new Sd3Tabpanel0(wrapUserSd3(sd3), sd3, false), null);
				return true;
			}
		}.withSysSd3(true).withUserDomain(true).withIndex(true).doWalk();

		//
		// ------> SYSTEM PAGES
		//

		map.put(new Sd3Tabpanel0(wrapSysSd3(ItemPath.PAGE_INDEX_ALIAS), ItemPath.PAGE_INDEX_ALIAS, true), null);

		Set<String> allBusySd3 = FinderPSP.getAllBusySd3();
		for (String nativeSd3 : allBusySd3) {
			nativeSd3 = ItemPath.wrapSd3(nativeSd3);
			map.put(new Sd3Tabpanel0(wrapSysSd3(nativeSd3), nativeSd3, true), null);
		}

		Tabbox0 tabbox0 = initWithTabs(map, true);
		Tabs tabs = tabbox0.getTabs();
		for (Component child : tabs.getChildren()) {
			Tab0 tab0 = (Tab0) child;

			ZKS.DRAG_DROP(tab0, false, true);

			String targetSd3 = getSd3(tab0);

			tab0.addEventListener(Events.ON_DROP, e -> {
				Pare<String, String> movedPageSdn = PagesTabs.getPageSdn((Tab0) ((DropEvent) e).getDragged());
				//it page?
				if (movedPageSdn == null) {
					//it form?
					String nodeID = FormsTabs.getNodeID((Tab0) ((DropEvent) e).getDragged());
					if (nodeID == null) {
						ZKI.infoAfterPointer("not supported type", ZKI.Level.ERR);
						return;
					}
					NodeID nodeID0 = NodeID.of(nodeID);
					new PageDdChoicer(targetSd3) {
						@Override
						public void onChoicePage(String choicedPagename) {
							NodeDir src = NodeDir.ofNodeId(nodeID0);
							NodeDir dst = NodeDir.ofNodeName(Sdn.of(targetSd3, choicedPagename), nodeID0.item());
							NodeFileTransferMan.moveItemNote(src, dst);
							ZKR.restartPage();
						}
					}._title(X.f("Move note <%s> to page..", nodeID0.item()))._modal()._closable()._showInWindow();
					return;
				}
				String msg = X.f("Move Page <%s> to plane <%s>", movedPageSdn.val(), targetSd3);
				ZKI_Messagebox.showMessageBoxBlueYN("Moving page", msg, (yes) -> {
					if (yes) {
						NodeFileTransferMan.movePageToSd3(movedPageSdn, targetSd3);
//						ZKI.infoAfterPointer("Ok - " + msg);
						ZKR.restartPage();
					}
					return null;
				});
			});

			Menupopup0 menu = tab0.getOrCreateMenupopup(this);

			menu.add_______();
			String linkToPage = ItemPath.isIndex(targetSd3) ? RSPath.ROOT.toRootLink() : RSPath.PLANE.toPlaneLink(targetSd3);
			menu.addMI_Href_v1("Open Page", linkToPage, true);

			ANM.applyPlaneLink(menu, targetSd3);
			Path dirSd3 = AFC.PLANES.DIR_PLANE(targetSd3);
			AppEventsFD.applyEvent_OPENDIR(menu, dirSd3);

			menu.add_______();

			menu.addMI(ANI.DELETE_ENTITY + " Delete plane", e -> {
				NodeFileTransferMan.deleteSd3(dirSd3);
				ZKR.restartPage();
			});

			AppEventsFD.applyEvent_OPENDIR_OS(menu, dirSd3);
			AppEventsFD.applyEvent_OPENDIR_TERMINAL(menu, dirSd3);

		}
	}


	private static String wrapUserSd3(String sd3) {
		return AFCC.Filter.USER_NET_NAMES.test(sd3) ? SYMJ.USER + sd3 : sd3;
	}

	private static String unwrapUserSd3(String sd3) {
		return sd3.startsWith(SYMJ.USER) ? sd3.substring(1) : sd3;
	}

	public static @NotNull String wrapSysSd3(String sd3) {
		return SYMJ.MONITOR + sd3;
	}

	public static @NotNull String unwrapSysSd3(String sd3) {
		return sd3.startsWith(SYMJ.MONITOR) ? sd3.substring(1) : sd3;
	}


	public static String getSd3(Tab0 tab0) {
		return (String) tab0.getChildren().get(0).getAttribute(ACN.SD3);
	}

	//	@RequiredArgsConstructor
	public class Sd3Tabpanel0 extends Tabpanel0 {

		public final String sd3;

		public final boolean sysSd3;

		@Override
		public boolean equalsBy(String label) {
			return sd3.equals(label);
		}

		public Sd3Tabpanel0(String label, String sd3, boolean sysSd3) {
			super(label);
			this.sd3 = sd3;
			this.sysSd3 = sysSd3;

			this.tab0 = Tab0.of(new Lb(label).attr(ACN.SD3, sd3));

		}

		@Override
		public void init() {
			super.init();

			onEventSelect(e -> {

				clearLazyTabContent();

				appendChildLazyTabContent(new PagesTabs(getIsVerticalAppPropDef(), sd3, sysSd3));

				getParent().invalidate();

				changeCurrentUrl(sd3, null, null);
			});

		}


	}


}
