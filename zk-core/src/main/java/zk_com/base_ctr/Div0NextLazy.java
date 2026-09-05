package zk_com.base_ctr;

import org.zkoss.zk.ui.Component;


public abstract class Div0NextLazy extends Div0Next {

	@Override
	protected void init() {
		super.init();
		super.appendChild(onBuildNext());
	}

	public void onNext() {
		appendChildNext(onBuildNext());
		replaceNextDiv();
	}

	public abstract Component onBuildNext();

}
