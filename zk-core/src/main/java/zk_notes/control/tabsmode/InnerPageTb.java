package zk_notes.control.tabsmode;

import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_os.core.Sdn;

import java.util.function.Supplier;

public class InnerPageTb extends PageTb {

	final Sdn sdn;

	public InnerPageTb(String name, Sdn sdn) {
		super(name);
		this.sdn = sdn;
	}

	final Supplier<Div0> buildWrapperView = this::get;

	@Override
	protected Supplier<Div0> buildTabpanelView() {
		return buildWrapperView;
	}

	private Div0 get() {
		Div0 parent = new Div0();
//		L.info("Start inner page on url {} vs {}", sdn.toLocalUrl(), sdn.toCurrentUrl());
//		parent.appendChild(Xml.ofFrame(sdn.toCurrentUrl(), "width=100%", "height=1200px"));
		parent.appendChild(Xml.ofFrame(sdn.toCurrentUrl(), "width=100%", "height=1200px"));
		return parent;
	}
}
