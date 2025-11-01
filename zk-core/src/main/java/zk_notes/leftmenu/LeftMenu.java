package zk_notes.leftmenu;

import mpu.X;
import mpu.core.ARR;
import mpu.paree.Paree3;
import mpe.call_msg.core.NodeID;
import org.zkoss.zk.ui.Component;
import zk_com.base_ctr.Div0;
import zk_com.core.IReRender;
import zk_com.tabs.Tabbox0;
import zk_form.ext.MenuPicker;
import zk_notes.AxnTheme;
import zk_notes.control.NotesSpace;
import zk_os.core.Sdn;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LeftMenu extends Div0 implements IReRender {

	public static LeftMenu findFirst(LeftMenu... defRq) {
		return ZKCFinderExt.findFirst_inWin0(LeftMenu.class, false, defRq);
	}

	public final LmHeader header;

	@Override
	public Component newCom() {
		return new LeftMenu(header.state);
	}

	public LeftMenu(Paree3<Boolean, Boolean, Boolean> state) {
		super();
		this.header = new LmHeader(state);
		setClass(ZKS.getAppClassName(LeftMenu.class));
	}

	public static Component rerenderFirst() {
//		ZKC.printAll();
		LeftMenu leftMenu = ZKCFinderExt.rerenderFirst(LeftMenu.class, true);
		return leftMenu;
	}

	@Override
	protected void init() {
		super.init();

		ZKS.FIXED(this);
		ZKS.WIDTH(this, "300px");

		ZKS.PADDING_TOP(this, AxnTheme.NAV_HEADER_POS[0]);
		ZKS.PADDING_LEFT(this, AxnTheme.NAV_HEADER_POS[1]);

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
			tabs.put("Forms", MenuPicker.ofAllForms(plane, NodeID.PAGE_INDEX_ALIAS));

			List l = ARR.asListKeyValues(tabs);
			Tabbox0 tabbox0 = Tabbox0.newTabbox(l);
//			appendChild(tabbox0);

		}

		appendChild(header);

	}
}
