package zk_notes.leftmenu;

import mpc.exception.FIllegalStateException;
import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.core.ARR;
import mpu.paree.Paree;
import mpu.paree.Paree3;
import mpe.call_msg.core.NodeID;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import zk_com.base_ctr.Div0;
import zk_com.core.IReRender;
import zk_com.tabs.Tabbox0;
import zk_form.ext.MenuPicker;
import zk_notes.AxnTheme;
import zk_notes.control.NotesSpace;
import zk_os.coms.AFC;
import zk_os.core.Sdn;
import zk_page.ZKC;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;
import zk_page.index.RSPath;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LeftMenu extends Div0 implements IReRender {

	public static LeftMenu findFirst(LeftMenu... defRq) {
		return ZKCFinderExt.findFirst_inWin0(LeftMenu.class, false, defRq);
	}

	public final LmHeader header;

//	public AFC.SpaceType getDefaultStateType() {
//		Paree3<Boolean, Boolean, Boolean> state = header.state;
//		if (state.key()) {
//			return AFC.SpaceType.SPACES;
//		} else if (state.val()) {
//			return AFC.SpaceType.PAGES;
//		} else if (state.ext()) {
//			return AFC.SpaceType.NODES;
//		}
//		throw new FIllegalStateException(state + "");
//	}

	public RSPath getDefaultStateType() {
		Paree3<Boolean, Boolean, Boolean> state = header.state;
		if (state.key()) {
			return RSPath.ROOT;
		} else if (state.val()) {
			return RSPath.PLANE;
		} else if (state.ext()) {
			return RSPath.PAGE;
		}
		throw new FIllegalStateException(state + "");
	}

	public static Paree3<Boolean, Boolean, Boolean> getDefaultState(RSPath pathType) {
		switch (pathType) {
			case ROOT:
				return Paree.of3(true, false, false);
			case PLANE:
				return Paree.of3(false, true, false);
			case PAGE:
				return Paree.of3(false, false, true);
			default:
				throw new WhatIsTypeException(pathType);
		}
	}

	public static @NotNull LeftMenu openSimple() {
		LeftMenu child = new LeftMenu(Sdn.get().getPathType());
		ZKC.getFirstWindow().appendChild(child);
		return child;
	}

	@Override
	public Component newCom() {
		return new LeftMenu(header.state);
	}

	public LeftMenu(RSPath rsPath) {
		this(getDefaultState(rsPath));
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

		RSPath defaultStateType = getDefaultStateType();
		switch (defaultStateType) {
			case ROOT:
				items.add(MenuPicker.ofAllSd3());
				break;
			case PLANE:
				items.add(MenuPicker.ofAllPages(Sdn.planeCurrent()));
				break;
			case PAGE:
				items.add(MenuPicker.ofAllForms(Sdn.planeCurrent(), Sdn.pageCurrent()));
				break;
			default:
				throw new WhatIsTypeException(defaultStateType);
		}

//		if (header.state.key()) {
//			items.add(MenuPicker.ofAllSd3());
//		} else if (header.state.val()) {
//			items.add(MenuPicker.ofAllPages(Sdn.planeCurrent()));
//		} else if (header.state.ext()) {
//			items.add(MenuPicker.ofAllForms(Sdn.planeCurrent(), Sdn.pageCurrent()));
//		}

		items.forEach(m -> {
					if (X.notEmpty(m.getLoadItems())) {
						appendChild(m);
					}
				}
		);


		appendChild(header);

	}
}
