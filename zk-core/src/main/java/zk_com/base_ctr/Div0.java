package zk_com.base_ctr;

import lombok.Setter;
import mpu.core.ARR;
import mpu.core.ARG;
import mpu.str.SPLIT;
import mpc.html.EHtml5;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Div;
import zk_com.base.Xml;
import zk_com.core.IZComExt;
import zk_form.dirview.DirView;
import zk_form.dirview.FdView;
import zk_page.ZKC;
import zk_page.ZKS;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static mpu.str.STR.NL;

public class Div0 extends Div implements IZComExt {
	//	@Getter
	@Setter
//	private List<Component> coms;
	private CopyOnWriteArrayList<Component> coms;

	public static Div0 buildMultilineDiv(String data) {
		return buildMultilineDiv(ARR.as(SPLIT.argsBy(data, NL)));
	}

	public static Div0 buildMultilineDiv(Object... lines) {
		return buildMultilineDiv(ARR.as(lines));
	}

	public static Div0 buildMultilineDiv(Iterable lines) {
		Div0 div = new Div0();
		for (Object line : lines) {
			div.appendChild(new Xml(EHtml5.div, String.valueOf(line), true));
		}
		return div;
	}

	public static Div0 open(Collection<Component> components, Component... parent) {
		Div0 divWith = of(components);
		boolean append = ARG.isDef(parent) ? parent[0].appendChild(divWith) : ZKC.getFirstWindow().appendChild(divWith);
		return divWith;
	}

	public static Div0 separator(Object heghit_px_pct, String bgColor, int size, String headValue, String... tagAttrs) {

		String html = EHtml5.H(size, headValue, tagAttrs);

		Xml innerXml = Xml.ofXml(html);

		ZKS.MARGIN_CENTER(innerXml,true);

		Div0 outerDiv = Div0.of(innerXml);

		outerDiv.height(heghit_px_pct);

		if (bgColor != null) {
			ZKS.BGCOLOR(outerDiv, bgColor);
		}

		ZKS.CENTER(outerDiv);

//		ZKS.MARGIN_CENTER()
//		if (heghit_px_pct instanceof Integer) {
//			Number num = (Number) heghit_px_pct;
//			int i = num.intValue() / 2;
//			String margin = i + "px 0";
//			ZKS.MARGIN(innerXml, margin);
//			ZKS.HEIGHT(innerXml, 50);
//		}
//		ZKS.CENTER(div0,10);
		return outerDiv;
	}

	public static Div0 separator(Object heghit_px_pct, String bgColor) {
		Div0 div0 = Div0.of();
		div0.height(heghit_px_pct);
		if (bgColor != null) {
			ZKS.BGCOLOR(div0, bgColor);
		}
		return div0;
	}

	public List<Component> getComs() {
		return coms;
	}

	public static Div0 of(Component... coms) {
		return new Div0(coms);
	}

	public static Div0 of(Collection<Component> coms) {
		return new Div0(coms);
	}

	public static Div0 wrap(Component... coms) {
		return Div0.of(coms);
	}

	public Div0(Component... coms) {
		this(ARR.asCAL(coms));
	}

	public Div0(Collection<Component> coms) {
		if (coms instanceof CopyOnWriteArrayList) {
			this.coms = (CopyOnWriteArrayList) coms;
		} else {
			this.coms = new CopyOnWriteArrayList<>(coms);
		}
	}

	protected boolean attachAll = true;

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		if (attachAll) {
			for (Component component : coms) {
				super.appendChild(component);
			}
		}
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
	}


	@Override
	public boolean appendChild(Component child) {
		getComs().add(child);
//		if (child instanceof IHeadCom) {
//			ZkPage.renderHeadRsrc_Form(ZKC.getFirstPageCtrl(), (IHeadCom) child);
//		}
		return super.appendChild(child);
	}

	private boolean revert = false;

	public Div0 revert(boolean... revert) {
		this.revert = ARG.isDefNotEqFalse(revert);
		return this;
	}

	@Override
	public boolean insertBefore(Component newChild, Component refChild) {
		if (revert) {
			int size = getComs().size();
			return super.insertBefore(newChild, getComs().get(size - (size > 1 ? 2 : 1)));
		}
		return super.insertBefore(newChild, refChild);
	}

	public void clear() {
		this.coms.clear();
		getChildren().clear();
	}

	public void appendChilds(List<Component> coms) {
		coms.forEach(c -> appendChild(c));
	}

}
