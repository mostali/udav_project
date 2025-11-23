package zk_page.index.qview;

import lombok.RequiredArgsConstructor;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.ui.ColorTheme;
import mpu.str.RANDOM;
import org.zkoss.zk.ui.Component;
import zk_com.base.Bt;
import zk_com.base_ctr.Div0M;
import zk_com.core.IZComFadeIO;
import zk_notes.AxnTheme;
import zk_page.ZKS;

@RequiredArgsConstructor
public abstract class QView extends Div0M implements IZComFadeIO {

	@Override
	protected void init() {

		addEffectIn(this);

//		appendChild(newBreadDiv());

		if (this instanceof PlanesQView) {
			init0();
			return;
		}

		super.init();

		daemon();

		ZKS.WIDTH(this, 100.0);
		ZKS.HEIGHT(this, 100.0);
		ZKS.ABSOLUTE(this);
		ZKS.TOP_LEFT(this, 0.0, 0.0);
		ZKS.OPACITY(this, 0.77);
		ZKS.ZINDEX(this, AxnTheme.ZI_QVIEW);

	}

	protected abstract Component newBreadDiv();

	public enum EPlane {
		ROOT, SD3, PAGE;
	}

	private EPlane planeType() {
		if (isRootPlane()) {
			return EPlane.ROOT;
		} else if (isSdPlane()) {
			return EPlane.SD3;
		} else if (isPagePlane()) {
			return EPlane.PAGE;
		}
		throw new RequiredRuntimeException("Except plane type: " + getClass());
	}

	public abstract String planeName();

	@Override
	protected Div0M applyBtStyle(Bt child) {
		if (isSdPlane() || isRootPlane()) {
			return super.applyBtStyle(child);
		} else if (isPagePlane()) {
			ZKS.ABSOLUTE(child);
			ZKS.TOP(child, "55px");
			ZKS.LEFT(child, "50%");
			ZKS.BGCOLOR(child, randomBtColor());
			return this;
		} else {
			throw new WhatIsTypeException("ni applyBtStyle for class: " + getClass());
		}
	}

	private String randomBtColor() {
		if (isRootPlane()) {
			return RANDOM.array_item(ColorTheme.BLACK);
		} else if (isSdPlane()) {
			return RANDOM.array_item(ColorTheme.BLUE);
		} else if (isPagePlane()) {
			return RANDOM.array_item(ColorTheme.GREEN);
		} else {
			throw new WhatIsTypeException("ni applyBtStyle for class: " + getClass());
		}
	}

	public boolean isPagePlane() {
		return this instanceof ItemsQView;
	}

	public boolean isRootPlane() {
		return this instanceof PlanesQView;
	}

	public boolean isSdPlane() {
		return this instanceof PagesQView;
	}

}
