package zk_notes.leftmenu;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARR;
import mpu.paree.Paree3;
import udav_net.apis.zznote.ItemPath;
import zk_com.base_ctr.Div0;
import zk_com.tabs.Tabbox0;
import zk_form.ext.MenuPicker;
import zk_os.AppZos;
import zk_os.core.Sdn;
import zk_page.ZKCFinder;
import zk_page.ZKColor;
import zk_page.ZKS;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeftMenu extends Div0 {

	public static LeftMenu findFirst(LeftMenu... defRq) {
		return ZKCFinder.findFirst(LeftMenu.class, false, defRq);
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

//		if (PageState.NavMenuMode.mnp.isEnableBlankParam()) {
//		}
//		Sys.say("go:" + ARR.as(header.ss_pg_fm));


		if (header.state.key()) {
			appendChild(MenuPicker.ofAllSd3());
		}
		if (header.state.val()) {
			appendChild(MenuPicker.ofAllPages(Sdn.plane()));
		}
		if (header.state.ext()) {
			appendChild(MenuPicker.ofAllForms(Sdn.plane(), Sdn.pagename()));
		}

		if (false) {

			String plane = Sdn.plane();

			Map tabs = new LinkedHashMap();
			tabs.put("Spaces", MenuPicker.ofAllSd3());
			tabs.put("Pages", MenuPicker.ofAllPages(plane));
			tabs.put("Forms", MenuPicker.ofAllForms(plane, ItemPath.PAGE_INDEX_ALIAS));

			List l = ARR.ofMap(tabs);
			Tabbox0 tabbox0 = Tabbox0.newTabbox(l);
//			appendChild(tabbox0);

		}

		appendChild(header);

	}
}
