package zk_com.tabs;

import zk_com.base_ctr.Div0;

import java.util.function.Supplier;

public abstract class LazyTabpanel0 extends Tabpanel0 {

	public LazyTabpanel0(Tab0 tab0) {
		super(tab0);
	}

	private boolean isOpenedTab;

	public LazyTabpanel0 isOpenedTab(boolean isOpenedTab) {
		this.isOpenedTab = isOpenedTab;
		return this;
	}

	@Override
	protected void init() {
		super.init();

		onEventSelect(e -> {
			onHappensEventSelect();
			clearLazyTabContent();
			Div0 wrapper = buildTabpanelView().get();
			//X.p("Lazy load tb:" + getClass().getSimpleName());
			appendChildLazyTabContent(wrapper);
			getParent().invalidate();

		});

		if (this.isOpenedTab) {
			appendChildLazyTabContent(buildTabpanelView().get());
			getTab0().setSelected(true);
		}
//		appendChildLazyTabContent(buildWrapperView().get());
//		Events.postEvent(Events.ON_SELECT, this, null); //simulate a click

	}

	protected abstract Supplier<Div0> buildTabpanelView();
}
