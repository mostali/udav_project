package zk_notes.leftmenu;

import mpc.exception.WhatIsTypeException;
import mpc.ui.ColorTheme;
import mpu.core.ARRi;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0M;
import zk_form.ext.MenuPicker;
import zk_notes.node.NodeDir;
import zk_os.core.Sdn;
import zk_page.ZKME;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.index.RSPath;

import java.nio.file.Path;
import java.util.Collection;

public class LmMenuPicker extends MenuPicker {

	final LeftMenu.SpaceType spaceType;

	protected static void applyStyle(MenuPicker parent, Div0M menuPanel, LeftMenu.SpaceType spaceType) {

		ZKS.ABSOLUTE(parent);
		ZKS.INLINE_BLOCK(menuPanel);
		ZKS.ZINDEX(parent, 9999);
		ZKS.LEFT(parent, 20);
		ZKS.TOP(parent, 100);
		ZKS.BORDER_RADIUS(parent, 7);

//		ZKS.PADDING(menuPanel,"15px 0 0 0 ");

		ZKS.BORDER(parent, "2px solid " + ColorTheme.PF_BORDER_COLOR);

		ZKS.BGCOLOR(menuPanel, spaceType.bgColorNext());

		ZKS.OPACITY(menuPanel, 0.83);

		ZKS.PADDING(menuPanel, "20pt");

	}

	public LmMenuPicker(Collection items, LeftMenu.SpaceType spaceType) {
		super(items);
		this.spaceType = spaceType;
	}

	@Override
	protected void applyStyle(Div0M menuPanel) {
		applyStyle(this, menuPanel, spaceType);
	}

//	@Override
//	protected Bt getCloseBt() {
//		return super.getCloseBt();
//	}

	@Override
	protected void applyStyleForItem(Ln ln) {
//				super.applyStyleForItem(ln);
		ZKS.PADDING(ln, "10pt 20pt");
		ZKS.FONT_SIZE(ln, "20pt");
	}

	@Override
	public void onHappensClickItems(Event event, Collection item) {
//				super.onHappensClickItems(event, item);

//		LeftMenu target = (LeftMenu) event.getTarget().getParent().getParent();

		String clickedItemName = ARRi.first(item) + "";
		switch (spaceType) {
			case SPACES:
				ZKR.redirectToLocation(RSPath.PLANE.toPlaneLink(clickedItemName), false);
				break;
			case PAGES:
				ZKR.redirectToLocation(RSPath.PAGE.toPlanPage(Sdn.plane(), clickedItemName), false);
				break;
			case NODES:
//				String data = NodeDir.loadData(Sdn.getRq(), clickedItemName);
				Path data = NodeDir.ofNodeName(Sdn.getRq(), clickedItemName).getPathFormFc();
				ZKME.openEditorText("Node " + clickedItemName, data, true);
				break;
			default:
				throw new WhatIsTypeException(spaceType);
		}

	}

}
