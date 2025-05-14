package zk_form.ext;

import mpu.Sys;
import mpu.X;
import mpu.core.ARRi;
import mpu.str.STR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Bt;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0M;
import zk_com.base_ext.EscTbx;
import zk_page.ZKS;

import java.util.Collection;

public class MenuPicker<T> extends ItemsPicker<T> {

	public MenuPicker(Collection<CharSequence> items) {
		super(items);
	}

	@Override
	public void onHappensClickItems(Event event, Collection<T> item) {
//				super.onHappensClosePciker();
		X.p("onHappensClickItems:" + ARRi.first(item));
	}

	@Override
	protected Bt getCloseBt() {
		return null;
	}

//	String sclass = STR.randstr(6, 6);

	@Override
	protected void init() {
		super.init();
//		super.appendChildStyle(".%s { width:100px; border:1px solid silver; }", sclass);
	}


	@Override
	protected void applyStyle(Div0M modalCom) {
//		modalCom.addSclass(sclass);
		ZKS.ABSOLUTE(this);

		ZKS.INLINE_BLOCK(modalCom);
		ZKS.ZINDEX(this, 9999);
		ZKS.LEFT(this, 0);
		ZKS.TOP(this, 0);

		ZKS.BORDER_GRAY(modalCom);
	}

	@Override
	protected void applyStyleForItem(Ln lb) {
//		lb.addSclass(sclass);
//		super.applyStyleForItem(lb);
//		ZKS.BLOCK(lb);
//		ZKS.WIDTH(lb,);
//		ZKS.BGCOLOR(lb, UColorTheme.GRAY[0]);
//		ZKS.MARGIN(lb, "100px");
//		ZKS.PADDING(lb, "50px");
	}


	@Override
	public void onHappensClosePciker() {
//				super.onHappensClosePciker();
	}

	@Override
	public EscTbx appendClosableByEsc(Component... closeIT) {
		return null;
	}

}
