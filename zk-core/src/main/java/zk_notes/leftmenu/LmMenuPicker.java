package zk_notes.leftmenu;

import mpc.exception.WhatIsTypeException;
import mpc.fs.QueryArg;
import mpc.ui.ColorTheme;
import mpu.X;
import mpu.core.ARRi;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import udav_net.apis.zznote.ItemPath;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0M;
import zk_com.base_ctr.Menupopup0;
import zk_form.ext.MenuPicker;
import zk_notes.AxnTheme;
import zk_notes.events.ANMD;
import zk_notes.events.ANMF;
import zk_notes.events.ANMP;
import zk_notes.factory.NFOpen;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FormState;
import zk_notes.node_state.libs.PageState;
import zk_os.core.Sdn;
import zk_page.ZKJS;
import zk_page.ZKME;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.events.ECtrl;
import zk_page.index.RSPath;

import java.nio.file.Path;
import java.util.Collection;

public class LmMenuPicker extends MenuPicker {

	final Sdn sdn;
	final LeftMenu.SpaceType spaceType;

	protected static void applyStyle(MenuPicker parent, Div0M menuPanel, LeftMenu.SpaceType spaceType) {

		ZKS.ABSOLUTE(parent);
		ZKS.INLINE_BLOCK(menuPanel);
		ZKS.ZINDEX(parent, AxnTheme.ZI_MENU);
		ZKS.LEFT(parent, 20);
		ZKS.TOP(parent, 100);
		ZKS.BORDER_RADIUS(parent, 7);

		ZKS.BORDER(parent, "2px solid " + ColorTheme.PF_BORDER_COLOR);

		ZKS.BGCOLOR(menuPanel, spaceType.bgColorNext());

		ZKS.OPACITY(menuPanel, 0.83);

		ZKS.PADDING(menuPanel, "20pt");

	}

	public LmMenuPicker(Sdn sdn, Collection items, LeftMenu.SpaceType spaceType) {
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


		ZKS.PADDING(ln, "10pt 20pt");
		ZKS.FONT_SIZE(ln, "20pt");
	}

	@Override
	public void onHappensClickItems(Event event, Collection item) {

		String clickedItemName = ItemPath.unwrapIndexPath((String) ARRi.first(item)); //ARRi.first(item) + "";

		ECtrl withKeys = ECtrl.of(event);

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
						ZKJS.openWindow800_1200(planeLink);
						break;

					//
					case CTRL_SHIFT: {
						FormState planeState = FormState.ofPlaneState_orCreate(clickedItemName);
						ZKME.openEditorText("Plane props - " + clickedItemName, planeState.pathPropsCom(), true);
						break;
					}

					case CTRL_ALT:
					case SHIFT_ALT:
					case CTRL_ALT_SHIFT:

				}
				break;
			}
			case PAGES: {
				String planPage = RSPath.PAGE.toPlanPage(targetPageSdn);
				switch (withKeys) {

					default:
						ZKR.redirectToLocation(planPage, false);
						break;

					case CTRL:
						ZKR.redirectToLocation(planPage, true);
						break;
					case ALT:
						ZKJS.openWindow800_1200(planPage);
						break;

					//
					case CTRL_SHIFT: {
						FormState pageState = FormState.ofPageState_orCreate(targetPageSdn);
						ZKME.openEditorText("Page Props - " + clickedItemName, pageState.pathPropsCom(), true);
						break;
					}


					case CTRL_ALT:
					case SHIFT_ALT:
					case CTRL_ALT_SHIFT:


				}
				break;
			}
			case NODES:

				NodeDir nodeDir = NodeDir.ofNodeName(Sdn.getRq(), clickedItemName);

				switch (withKeys) {

					default:
						LmHeader component = (LmHeader) this.getParent().getChildren().get(1);
//						Component component = event.getTarget();
						NFOpen.openNoteWinAsNotify(nodeDir, component);
						break;

					case ALT: {
//						String planPage = QueryArg.joinToUrl(RSPath.PAGE.toPlanPage(Sdn.getRq()), QueryArg.of(PageState.TabsMode.tbf, clickedItemName));
						QueryArg qArgs = QueryArg.of(PageState.TabsMode.tbf.name(), clickedItemName);
						String planPage = RSPath.PAGE.toPlanPage(Sdn.get(), qArgs);
//						if (withKeys == ECtrl.ALT) {
						ZKJS.openWindow800_1200(planPage);
//						} else {
//							ZKR.redirectToLocation(planPage, true);
//						}
						break;
					}
					case CTRL: {
						Path data = nodeDir.getPathFormFc();
						ZKME.openEditorText("Node " + clickedItemName, data, true);
						break;
					}

					case SHIFT:
						NFOpen.openOrCloseToggle(nodeDir);
						break;

					//
					case CTRL_SHIFT: {
						Path data = nodeDir.getPathComFc();
						ZKME.openEditorJson("Node Props - " + clickedItemName, data, true);
						break;
					}

					case CTRL_ALT:
					case SHIFT_ALT:
					case CTRL_ALT_SHIFT:


				}
				break;
			default:
				throw new WhatIsTypeException(spaceType);
		}

	}

}
