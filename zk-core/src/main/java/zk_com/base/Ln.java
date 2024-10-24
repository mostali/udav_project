package zk_com.base;


import mpc.fs.UF;
import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import zk_com.core.IZCom;
import zk_page.ZKS;

public class Ln extends A implements IZCom {

	public static Ln ofEmojBlank(String url, String... labelPfx) {
		return new Ln(ARG.toDefOr(" ", labelPfx) + SYMJ.ARROW_RT, url, true).decoration_none();
	}

	public static Ln ofEmoj(String url, String... labelPfx) {
		return new Ln(ARG.toDefOr(" ", labelPfx) + SYMJ.ARROW_RT, url, false).decoration_none();
	}

	@Override
	public String getComStateName() {
		return UF.clearFileNameRU_RemoveSlash(getLabel());
	}

	public Ln addEventListener(EventListener<? extends Event> listener) {
		super.addEventListener(Events.ON_CLICK, listener);
		return this;
	}

	public Ln(String html, boolean markHtml) {
		super();
		appendChild(Xml.ofXml(html));
	}

	public Ln(String label) {
		this(label, null, null);
	}

	public Ln(String label, String href, boolean targetBlank) {
		this(label, href, (String) null);
		if (ARG.isDefEqTrue(targetBlank)) {
			setTarget("_blank");
		}
	}

	public Ln(String label, String href, String bgColor) {
		this(label, href, bgColor, null);
	}

	public Ln(String label, String href, String bgColor, String color) {
		super(label);

		if (href != null) {
			setHref(href);
		}

		if (bgColor != null) {
			ZKS.BGCOLOR(this, bgColor);
		}
		if (color != null) {
			ZKS.COLOR(this, color);
		}
	}

	public Ln decoration_none(boolean... withDecoration) {
		return ARG.isDefEqTrue(withDecoration) ? ZKS.TEXT_DECARATION_NONE(this, null) : ZKS.TEXT_DECARATION_NONE(this);
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {

	}
}
