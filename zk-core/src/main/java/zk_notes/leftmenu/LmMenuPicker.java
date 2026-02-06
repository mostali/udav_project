package zk_notes.leftmenu;

import mpc.exception.WhatIsTypeException;
import mpc.ui.ColorTheme;
import mpc.url.QueryArg;
import mpe.call_msg.core.NodeID;
import mpu.X;
import mpu.core.ARRi;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0M;
import zk_com.base_ctr.Menupopup0;
import zk_form.ext.MenuPicker;
import zk_notes.ANI;
import zk_notes.AxnTheme;
import zk_notes.events.ANMD;
import zk_notes.events.ANMF;
import zk_notes.events.ANMP;
import zk_notes.factory.NFOpen;
import zk_notes.node.NodeDir;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.PageState;
import zk_os.coms.AFC;
import zk_os.coms.AFCSec;
import zk_os.core.Sdn;
import zk_os.sec.SecMan;
import zk_page.ZKME;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.events.ECtrl;
import zk_page.index.RSPath;

import java.nio.file.Path;
import java.util.Collection;

public class LmMenuPicker extends MenuPicker {

	final Sdn sdn;
	final AFC.SpaceType spaceType;

	protected static void applyStyle(MenuPicker parent, Div0M menuPanel, AFC.SpaceType spaceType) {

//		ZKS.ABSOLUTE(parent);
		ZKS.FIXED(parent);
		ZKS.INLINE_BLOCK(menuPanel);
		ZKS.ZINDEX(parent, AxnTheme.ZI_MENU);
		ZKS.LEFT(parent, 20);
		ZKS.TOP(parent, 100);
		ZKS.BORDER_RADIUS(parent, 7);

		ZKS.BORDER(parent, "2px solid " + ColorTheme.PF_BORDER_COLOR);

		ZKS.applyNiceBg(menuPanel, spaceType.bgColorNext(), "white");

		ZKS.OPACITY(menuPanel, 0.69);

		ZKS.PADDING(menuPanel, "20pt");

		int size = AFCSec.getSpaceTypesCount(spaceType);
		if (size > 12) {
			ZKS.HEIGHT(menuPanel, 80.0);
			ZKS.OVERFLOW(menuPanel, 3);
		}

	}

	public LmMenuPicker(Sdn sdn, Collection items, AFC.SpaceType spaceType) {
		super(items);
		this.sdn = sdn;
		this.spaceType = spaceType;
		setVisible(X.notEmpty(items));
	}

	@Override
	protected void applyStyle(Div0M menuPanel) {
		applyStyle(this, menuPanel, spaceType);
	}

	@Override
	protected void applyStyleForItem(Ln ln) {

		ZKS.PADDING(ln, "10pt 20pt");
		ZKS.FONT_SIZE(ln, "20pt");

		boolean planeOwnerOrAdmin = SecMan.isAllowedEditPlane(Sdn.planeCurrent());
		if (!planeOwnerOrAdmin) {
			return;
		}

		Menupopup0 menu = ln.getOrCreateMenupopup(this);

		String eName = ln.getLabel();

		switch (spaceType) {
			case NODES:
				ANMF.applyForm(menu, NodeDir.ofNodeName(sdn, eName));
				break;
			case PAGES:
				ANMP.applyPageLink(menu, sdn.clonePage(eName));
				break;
			case SPACES:
				ANMD.applyPlaneLink(menu, eName);
				break;
			default:
				throw new WhatIsTypeException(spaceType);

		}

	}

	@Override
	public void onHappensClickItems(Event event, Collection item) {

		String clickedItemName = NodeID.unwrapIndexPath((String) ARRi.first(item)); //ARRi.first(item) + "";

		ECtrl withKeys = SecMan.isAnonimUnsafe() ? ECtrl.DEFAULT : ECtrl.ofAsCtrl(event);

		Sdn targetPageSdn = Sdn.of(Sdn.planeCurrent(), clickedItemName);

		switch (spaceType) {
			case SPACES: {

				String planeLink = RSPath.PLANE.toPlaneLink(clickedItemName);

				switch (withKeys) {

					default:
						ZKR.redirectToLocation(planeLink, false);
						break;

					case CTRL:
						ZKR.redirectToLocation(planeLink, true);
						break;
					case ALT:
						ZKR.openWindow800_1200(planeLink);
						break;

					//
					case CTRL_SHIFT: {
						ObjState planeState = AppStateFactory.forPlane(clickedItemName);
						ZKME.textSaveable("Plane props - " + clickedItemName, planeState.pathPropsCom(), true);
						break;
					}

					case CTRL_ALT:
					case SHIFT_ALT:
					case CTRL_ALT_SHIFT:

				}
				break;
			}
			case PAGES: {
				String planPage = RSPath.PAGE.toPageLink(targetPageSdn);
				switch (withKeys) {

					default:
						ZKR.redirectToLocation(planPage, false);
						break;

					case CTRL:
						ZKR.redirectToLocation(planPage, true);
						break;
					case ALT:
						ZKR.openWindow800_1200(planPage);
						break;

					//
					case CTRL_SHIFT: {
						ObjState pageState = AppStateFactory.forPage(targetPageSdn);
						ZKME.textSaveable(ANI.PROPS_ZZZ + " Page Props - " + clickedItemName, pageState.pathPropsCom(), true);
						break;
					}


					case CTRL_ALT:
					case SHIFT_ALT:
					case CTRL_ALT_SHIFT:


				}
				break;
			}
			case NODES:

				NodeDir nodeDir = NodeDir.ofNodeName(Sdn.get(), clickedItemName);

				switch (withKeys) {

					default:
						LmHeader component = (LmHeader) this.getParent().getChildren().get(1);
//						Component component = event.getTarget();
						NFOpen.openForm(nodeDir, component);
						break;

					case ALT: {
//						String planPage = QueryArg.joinToUrl(RSPath.PAGE.toPlanPage(Sdn.getRq()), QueryArg.of(PageState.TabsMode.tbf, clickedItemName));
						QueryArg qArgs = QueryArg.of(PageState.TabsMode.tbf.name(), clickedItemName);
						String planPage = RSPath.PAGE.toPageLink(Sdn.getUnsafe(), qArgs);
//						if (withKeys == ECtrl.ALT) {
						ZKR.openWindow800_1200(planPage);
//						} else {
//							ZKR.redirectToLocation(planPage, true);
//						}
						break;
					}
					case CTRL: {
						Path data = nodeDir.getPath_FormFc_Data();
						ZKME.textSaveable("Node " + clickedItemName, data, true);
						break;
					}

					case SHIFT:
						NFOpen.openFormOrCloseToggle(nodeDir);
						break;

					//
					case CTRL_SHIFT: {
						Path data = nodeDir.getPath_FormFc_Props();
						ZKME.jsonSaveable("Node Props - " + clickedItemName, data, true);
						break;
					}

					case CTRL_ALT: {
						Path data = nodeDir.getPath_ComFc();
						ZKME.jsonSaveable("Node Props Com - " + clickedItemName, data, true);
						break;
					}


					case SHIFT_ALT:
					case CTRL_ALT_SHIFT:


				}
				break;
			default:
				throw new WhatIsTypeException(spaceType);
		}

	}

}
