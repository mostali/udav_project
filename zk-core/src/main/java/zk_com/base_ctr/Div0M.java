package zk_com.base_ctr;

import mpc.str.sym.SYMJ;
import mpc.ui.ColorTheme;
import org.zkoss.zk.ui.Component;
import zk_com.base.Bt;
import zk_page.ZKC;
import zk_page.ZKS;

import java.util.Collection;

//modal
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

//	protected boolean withCloseButton = true;
//
//	public Div0M withCloseButton(boolean... fullscreen) {
//		this.withCloseButton = ARG.isDefNotEqFalse(fullscreen);
//		return this;
//	}

//	protected String bgColor = UColorTheme.GRAY[1];
//
//	public Div0M withBgColor(String[] bgColor, boolean... random) {
//		this.bgColor = ARG.isDefEqTrue(random) ? ARRi.rand(bgColor) : (X.empty(bgColor) ? null : bgColor[1]);
//		return this;
//	}

	@Override
	protected void init() {
		super.init();
//		if (withCloseButton) {
		Bt closeBt = getCloseBt();
		if (closeBt != null) {
			applyBtStyle(closeBt);
			appendChild(closeBt);
		}
//		}

		applyStyle(this);

		appendClosableByEsc();

	}

	protected void applyStyle(Div0M modalCom) {
		ZKS.BGCOLOR(modalCom, ColorTheme.GRAY[1]);
		ZKS.OPACITY(modalCom, 0.93);
	}

	protected Bt getCloseBt() {
		return new Bt(SYMJ.FAIL_RED_THINK).onCLICK(e -> onHappensClosePciker());
	}

	public void onHappensClosePciker() {
		removeMe();
//		ZKC.removeMeCheckWindowParentReturnParent(this);
	}


	enum Mode {
		RIGHTTOP, CENTERTOP
	}

	Mode mode = Mode.CENTERTOP;

	protected Div0M applyBtStyle(Bt child) {
		ZKS.ABSOLUTE(child);
		ZKS.TOP(child, "5px");

		switch (mode) {
			case RIGHTTOP:
				ZKS.RIGHT(child, "5px");
				break;
			case CENTERTOP:
				ZKS.LEFT(child, "50%");
				break;


		}
		return this;
	}

	protected void init0() {
		super.init();
	}


}
