package zk_notes.leftmenu;

import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpu.core.ARR;
import mpu.paree.Paree3;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Cb;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_notes.AxnTheme;
import zk_page.ZKS;

import java.util.Arrays;
import java.util.function.Function;

public class LmHeader extends Div0 {

	public static class LmCb extends Cb {
		public final LeftMenu.SpaceType spaceType;

		public LmCb(LeftMenu.SpaceType spaceType) {
			super(spaceType.name());
			this.spaceType = spaceType;

			onCLICK(e -> onUpdateLeftMenu(e, spaceType));

			moldSwitch();
		}

		private static void onUpdateLeftMenu(Event e, LeftMenu.SpaceType spaceType) {
			LmHeader head = (LmHeader) e.getTarget().getParent();
			LeftMenu parent = (LeftMenu) head.getParent();
			if (spaceType == null) {
				return;
			}
			switch (spaceType) {
				case SPACES:
					parent.replaceWith(new LeftMenu(Paree3.of(true, false, false)));
					break;
				case PAGES:
					parent.replaceWith(new LeftMenu(Paree3.of(false, true, false)));
					break;
				case NODES:
					parent.replaceWith(new LeftMenu(Paree3.of(false, false, true)));
					break;
				default:
					throw new WhatIsTypeException(spaceType);

			}

		}
	}

	public final Paree3<Boolean, Boolean, Boolean> state;

	public LmHeader(Paree3<Boolean, Boolean, Boolean> state) {
		super();
		this.state = state;

		Function<LeftMenu.SpaceType, Boolean> hasType = (spaceType) -> {
			if (spaceType == null) {
				return false;
			}
			switch (spaceType) {
				case SPACES:
					return state.key();
				case PAGES:
					return state.val();
				case NODES:
					return state.ext();
				default:
					return false;
			}
		};

//		ZKS.MARGIN(this,"15px 0 0 0 ");

		Arrays.stream(LeftMenu.SpaceType.values()).forEach(spaceType -> {
			Cb cb = new LmCb(spaceType);
			cb.setChecked(hasType.apply(spaceType));
			cb.font_bold_nice(AxnTheme.FONT_SIZE_MENU);
			appendChild(cb);
		});

		if (L.isInfoEnabled()) {
			L.info("init with ss_pg_fm:" + ARR.as(state));
		}

	}

}
