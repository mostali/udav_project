package zk_notes.control.tabsmode;

import lombok.Getter;
import lombok.Setter;
import mpc.url.QueryArg;
import mpc.url.UUrl;
import mpc.net.query.QueryUrl;
import mpe.str.CN;
import mpu.pare.Pare;
import mpu.str.TKN;
import zk_com.tabs.LazyTabpanel0;
import zk_com.tabs.Tab0;
import zk_page.ZKJS;
import zk_page.core.SpVM;
import zk_page.index.RSPath;

import java.util.List;
import java.util.stream.Collectors;

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
		String planPageUrl = RSPath.PAGE.toPageLink(SpVM.get().sdn());
		Tab0 tab0 = getTab0();

		if (!withChangeCurrentTabInUrlQuery) {
			return;
		}
		QueryArg queryArg = QueryArg.of(CN.TB, tab0.getLabel());
		String[] hostPathQuery = UUrl.getPathAndQueryString(planPageUrl, null);
		if (hostPathQuery == null) {
			return;
		}
		QueryUrl queryUrl = QueryUrl.of(hostPathQuery[1]);
		List<QueryArg> argsWoTb = queryUrl.getQueryArgsAsList().stream().filter(qa -> !CN.TB.equals(qa.keyStr())).collect(Collectors.toList());
		argsWoTb.add(queryArg);
		planPageUrl = QueryArg.joinToUrl(hostPathQuery[0], argsWoTb.toArray(new Pare[0]));
		ZKJS.changeUrl(planPageUrl);
	}


}
