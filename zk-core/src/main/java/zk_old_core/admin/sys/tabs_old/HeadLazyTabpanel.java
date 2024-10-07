package zk_old_core.admin.sys.tabs_old;

import zk_com.core.LazyBuilder;


@Deprecated
public class HeadLazyTabpanel<TAB> extends LazyTabpanel {

	final TAB tabHead;

	public HeadLazyTabpanel(TAB tabHead, LazyBuilder... lazyBuilder) {
		super(lazyBuilder);
		this.tabHead = tabHead;

	}

	public TAB getTabHead() {
		return tabHead;
	}
}

