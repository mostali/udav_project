package zk_notes.node_state.libs;

import mpc.net.query.QueryUrl;
import mpu.X;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import zk_notes.node_state.FormState;
import zk_page.core.SpVM;

import java.util.function.Function;

public class PageState<P> extends FormState<P> {

	public PageState(Pare sdn, String pathComStr, boolean isForm) {
		super(sdn, pathComStr, isForm);
	}

	public Pare<TabsMode, String[]> getTabsModeWithValues() {
		return TabsMode.valueOf(this, SpVM.get().ppiq().queryUrl());
	}

	public enum TabsMode {
		def, tbp, tbf;

		public static Pare<TabsMode, String[]> valueOf(FormState pageState, QueryUrl queryUrl) {
			Function<TabsMode, String[]> up = (tbMode) -> {
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
			for (TabsMode value : values()) {
				if (value == def) {
					continue;
				}
				String[] values = up.apply(value);
				if (values != null) {
					return Pare.of(value, values);
				}
			}
			return Pare.of(def, null);
		}
	}
}
