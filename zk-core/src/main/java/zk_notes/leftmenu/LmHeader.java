package zk_notes.leftmenu;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARR;
import mpu.paree.Paree3;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Cb;
import zk_com.base_ctr.Div0;
import zk_notes.AxnTheme;
import zk_os.coms.AFC;
import zk_page.index.RSPath;

import java.util.Arrays;
import java.util.function.Function;

public class LmHeader extends Div0 {

	public static class LmCb extends Cb {
		public final AFC.SpaceType spaceType;

		public LmCb(AFC.SpaceType spaceType) {
			super(spaceType.name());
			this.spaceType = spaceType;

			onCLICK(e -> onUpdateLeftMenu(e, spaceType));

			moldSwitch();
		}

		private static void onUpdateLeftMenu(Event e, AFC.SpaceType spaceType) {
			LmHeader head = (LmHeader) e.getTarget().getParent();
			LeftMenu parent = (LeftMenu) head.getParent();
			if (spaceType == null) {
				return;
			}
			switch (spaceType) {
				case SPACES:
					parent.replaceWith(new LeftMenu(RSPath.ROOT));
					break;
				case PAGES:
					parent.replaceWith(new LeftMenu(RSPath.PLANE));
					break;
				case NODES:
					parent.replaceWith(new LeftMenu(RSPath.PAGE));
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

		Function<AFC.SpaceType, Boolean> hasType = (spaceType) -> {
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

		absolute();

		Arrays.stream(AFC.SpaceType.values()).forEach(spaceType -> {
			Cb cb = new LmCb(spaceType);
			cb.setChecked(hasType.apply(spaceType));
			cb.font_bold_nice(AxnTheme.FONT_SIZE_MENU);
			appendChild(cb);
		});

//		if (L.isInfoEnabled()) {
//			L.info("init with ss_pg_fm:" + ARR.as(state));
//		}

	}

}
