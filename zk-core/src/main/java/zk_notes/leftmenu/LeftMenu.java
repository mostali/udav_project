package zk_notes.leftmenu;

import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.core.ARR;
import mpu.paree.Paree3;
import udav_net.apis.zznote.ItemPath;
import zk_com.base_ctr.Div0;
import zk_com.tabs.Tabbox0;
import zk_form.ext.MenuPicker;
import zk_os.core.Sdn;
import zk_page.ZKCFinderExt;
import zk_page.ZKColor;
import zk_page.ZKS;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LeftMenu extends Div0 {

	public static LeftMenu findFirst(LeftMenu... defRq) {
		return ZKCFinderExt.findFirst_inWin0(LeftMenu.class, false, defRq);
	}

	public final LmHeader header;

	public LeftMenu(Paree3<Boolean, Boolean, Boolean> state) {
		super();
		this.header = new LmHeader(state);
		setClass(ZKS.getAppClassName(LeftMenu.class));
	}

	public enum SpaceType {
		SPACES, PAGES, NODES;

		public String bgColorNext() {
			return color().nextColor();
		}

		public ZKColor color() {
			switch (this) {
				case SPACES:
					return ZKColor.LBLUE;
				case PAGES:
					return ZKColor.GREEN;
				case NODES:
					return ZKColor.YELLOW;
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	@Override
	protected void init() {
		super.init();

		List<LmMenuPicker> items = new LinkedList<>();
		if (header.state.key()) {
			items.add(MenuPicker.ofAllSd3());
		}
		if (header.state.val()) {
			items.add(MenuPicker.ofAllPages(Sdn.planeCurrent()));
		}
		if (header.state.ext()) {
			items.add(MenuPicker.ofAllForms(Sdn.planeCurrent(), Sdn.pageCurrent()));
		}

		items.forEach(m -> {
					if (X.notEmpty(m.getLoadItems())) {
						appendChild(m);
					}
				}
		);

		if (false) {

			String plane = Sdn.planeCurrent();

			Map tabs = new LinkedHashMap();
			tabs.put("Spaces", MenuPicker.ofAllSd3());
			tabs.put("Pages", MenuPicker.ofAllPages(plane));
			tabs.put("Forms", MenuPicker.ofAllForms(plane, ItemPath.PAGE_INDEX_ALIAS));

			List l = ARR.asListKeyValues(tabs);
			Tabbox0 tabbox0 = Tabbox0.newTabbox(l);
//			appendChild(tabbox0);

		}

		appendChild(header);

	}
}
