package zk_com.base_ctr;

import lombok.RequiredArgsConstructor;
import zk_page.ZKC;

@RequiredArgsConstructor
public abstract class DivWithControl extends Div0 {
	@Override
	protected void init() {
		doCycle();
//		doViewPersistence();
	}

	protected void doCycle() {
		doBeforeView();
		doLoadModel();
		doView();
		doAfterView();
	}

	protected Div0 view = null;

	public Div0 view() {
		return view;
	}

	protected void doView() {
		if (view != null) {
			ZKC.removeMeCheckWindowParentReturnParent(view);
			//			view.clear();
		}

		appendChild(view = buildView());

	}

	protected void doBeforeView() {
	}

	protected void doAfterView() {
	}

	protected abstract Div0 buildView();

	protected abstract void doLoadModel();
}
