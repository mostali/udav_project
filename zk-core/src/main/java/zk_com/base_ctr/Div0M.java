package zk_com.base_ctr;

import mpc.str.sym.SYMJ;
import mpc.ui.UColorTheme;
import org.zkoss.zk.ui.Component;
import zk_com.base.Bt;
import zk_page.ZKC;
import zk_page.ZKS;

import java.util.Collection;

public class Div0M extends Div0 {
	public Div0M(Component... coms) {
		super(coms);
	}

	public Div0M(Collection<Component> coms) {
		super(coms);
	}

	public static Div0M openModal(Collection<Component> components) {
		Div0M mw = of(components);
		ZKC.getFirstWindow().appendChild(mw);
		return mw;
	}

	public static Div0M of(Collection<Component> components) {
		return new Div0M(components);
	}

	@Override
	protected void init() {
		super.init();
		Bt child = new Bt(SYMJ.FAIL_RED_THINK).onCLICK(e -> ZKC.removeMeReturnParent(this));
		applyBtStyle(child);
		appendChild(child);

		ZKS.BGCOLOR(this, UColorTheme.GRAY[1]);
		ZKS.OPACITY(this, 0.93);

		closableByEsc();

	}

	protected Div0M applyBtStyle(Bt child) {
		ZKS.ABSOLUTE(child);
		ZKS.TOP(child, "5px");
		ZKS.RIGHT(child, "5px");
		return this;
	}

	protected void init0() {
		super.init();
	}


}
