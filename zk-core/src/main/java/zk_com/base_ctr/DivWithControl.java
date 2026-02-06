package zk_com.base_ctr;

import lombok.RequiredArgsConstructor;
import mpu.X;
import org.zkoss.zk.ui.Component;
import zk_page.ZKC;

import java.util.List;

@RequiredArgsConstructor
public abstract class DivWithControl extends Div0 {
	@Override
	protected void init() {
		doCycle();
		doViewPersistence();
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
		view = new Div0();
		appendChild(view);
		view.appendChilds(buildViewComponents());
	}

	protected void doBeforeView() {
	}

	protected void doAfterView() {
	}


	protected void doViewPersistence() {
		List<Component> coms = buildControlComponents();
		if (X.notEmpty(coms)) {
			coms.forEach(c -> appendChild(c));
		}
	}

	protected abstract List<Component> buildControlComponents();

	protected abstract List<Component> buildViewComponents();

	protected abstract void doLoadModel();
}
