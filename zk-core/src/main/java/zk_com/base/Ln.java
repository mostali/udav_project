package zk_com.base;


import mpc.fs.UF;
import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.A;
import zk_com.core.IZCom;
import zk_com.uploader.FileUploaderComposer;
import zk_com.uploader.FileUploaderDadComposer;
import zk_page.ZKS;

import java.nio.file.Path;

public class Ln extends A implements IZCom {

	public static Ln ofEmojBlank(String url, String... label) {
		return new Ln(ARG.toDefOr(" ", label) + SYMJ.ARROW_RT, url, true).decoration_none();
	}

	public static Ln ofEmoj(String url, String... label) {
		return new Ln(ARG.toDefOr(" ", label) + SYMJ.ARROW_RT, url, false).decoration_none();
	}

	public static Ln uploadTo(String label, Path path, Integer... max) {
		return (Ln) new Ln(label).onCLICK(e -> FileUploaderComposer.doChoiceFileAdnDownloadMedia(path, max));
	}

	public static Ln of(String label, SerializableEventListener... click) {
		Ln ln = new Ln(label);
		if (ARG.isDef(click)) {
			return (Ln) ln.onCLICK(ARG.toDef(click));
		}
		return ln;
	}

	@Override
	public String getComName() {
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

	public Ln(Component child) {
		super();
		appendChild(child);
	}

	public Ln() {
		this((String) null);
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
