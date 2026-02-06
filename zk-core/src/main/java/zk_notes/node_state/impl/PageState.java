package zk_notes.node_state.impl;

import mpc.net.query.QueryUrl;
import mpe.call_msg.core.IPageID;
import mpu.X;
import mpu.core.ENUM;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import zk_notes.node_state.ObjState;
import zk_page.core.SpVM;

import java.util.List;
import java.util.function.Function;

public class PageState<P> extends ObjState<P> implements IPageID {

	public PageState(Pare sdn, String pathComStr) {
		super(sdn, pathComStr, false);
	}

	public static PageState get() {
		return SpVM.get().pageState();
	}

	public Pare<TabsMode, String[]> getTabsModeWithValues() {
		return TabsMode.valueOf(this, SpVM.get().ppiq().queryUrl());
	}

	@Override
	public String spaceName() {
		return sdn.key();
	}

	@Override
	public String pageName() {
		return sdn.val();
	}

	@Override
	public String toObjId() {
		return sdn.key() + "/" + sdn.val() + "/";
	}

	public enum NavMenuMode {
		def, mnd, mnp, mnf;

		public boolean isEnableBlankParam() {
			switch (this) {
				case def:
					throw new UnsupportedOperationException("illegal case with " + this);
			}
			String vl = SpVM.get().getQuery().getFirstAsStr(name(), null);
			return "".equals(vl);
		}

		public static Pare<NavMenuMode, String[]> valueOf(ObjState pageState, QueryUrl queryUrl) {
			return TabsMode.valueOf(pageState, queryUrl, NavMenuMode.class);
		}
	}

	public enum TabsMode {
		def, tbd, tbp, tbf;

		public static Pare<TabsMode, String[]> valueOf(ObjState pageState, QueryUrl queryUrl) {
			return valueOf(pageState, queryUrl, TabsMode.class);
		}

		public static <T extends Enum> Pare<T, String[]> valueOf(ObjState pageState, QueryUrl queryUrl, Class<T> asType) {
			Function<T, String[]> up = (tbMode) -> {
				String tbpQ = queryUrl.getFirstAsStr(tbMode.name(), null);
				if (X.notEmpty(tbpQ)) {
					return SPLIT.argsByComma(tbpQ);
				}
				String tbp = pageState.get(tbMode.name(), null);
				if (X.notEmpty(tbp)) {
					return SPLIT.argsByComma(tbp);
				}
				return null;
			};
			List<T> values0 = ENUM.getValues(asType);
			for (T value : values0) {
				if (value.name().equals(TabsMode.def.name()) || (value.name().equals(NavMenuMode.def.name()))) {
					continue;
				}
				String[] values = up.apply(value);
				if (values != null) {
					return Pare.of(value, values);
				}
			}
			T defVal = TabsMode.class == asType ? (T) TabsMode.def : (T) NavMenuMode.def;
			return Pare.of(defVal, null);
		}


	}
}
