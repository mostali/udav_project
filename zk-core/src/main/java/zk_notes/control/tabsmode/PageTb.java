package zk_notes.control.tabsmode;

import lombok.Getter;
import lombok.Setter;
import mpe.str.CN;
import mpu.str.TKN;
import udav_net.apis.zznote.NoteApi;
import zk_com.tabs.LazyTabpanel0;
import zk_com.tabs.Tab0;
import zk_page.ZKJS;
import zk_page.core.SpVM;
import zk_page.index.RSPath;

public abstract class PageTb extends LazyTabpanel0 {

	public static String getDefaultName(Class clazz) {
		String simpleName = clazz.getSimpleName();
		return TKN.firstGreedy(simpleName, "Tb");
	}

	public final @Getter String name;

	public PageTb(String name) {
		super(null);
		this.name = name == null ? getDefaultName(getClass()) : name;
		this.tab0as = Tab0.of(this.name);
	}

	private @Setter boolean withChangeCurrentTabInUrlQuery = false;

	@Override
	public void onHappensEventSelect() {
		super.onHappensEventSelect();
		String planPageUrl = RSPath.PAGE.toPageLink(SpVM.get().sdn0());
		Tab0 tab0 = getTab0();

		if (withChangeCurrentTabInUrlQuery) {
			String sym = planPageUrl.contains("?" + NoteApi.SKA + "=") ? "&" : "?";
			ZKJS.changeUrl(planPageUrl + sym + CN.TB + "=" + tab0.getLabel());
		}
	}


}
