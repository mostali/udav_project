package zk_com.win;

import lombok.Setter;
import mpc.exception.WhatIsTypeException;
import mpc.html.EHtml5;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.SPLIT;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.Xml;
import zk_com.base_ctr.Cap0;
import zk_com.core.IZComExt;
import zk_page.ZKC;

import java.util.Collection;
import java.util.List;

import static mpu.str.STR.NL;

/**
 * @author dav 12.01.2022   18:50
 */
//https://www.zkoss.org/wiki/ZK_Developer's_Guide/ZUL_Components/Layout_and_Windows/Windows

public class Win0 extends Window implements IZComExt {
	//	@Getter
	@Setter
	private List<Component> coms;

	public static Win0 buildMultilineDiv(String data) {
		return buildMultilineDiv(ARR.as(SPLIT.argsBy(data, NL)));
	}

	public static Win0 buildMultilineDiv(Object... lines) {
		return buildMultilineDiv(ARR.as(lines));
	}

	public static Win0 buildMultilineDiv(Iterable lines) {
		Win0 div = new Win0();
		for (Object line : lines) {
			div.appendChild(new Xml(EHtml5.div, String.valueOf(line), true));
		}
		return div;
	}

	public static Win0 open(Collection<Component> components) {
		Win0 divWith = of(components);
		ZKC.getFirstWindow().appendChild(divWith);
		return divWith;
	}

	public List<Component> getComs() {
		return coms;
	}

	public static Win0 of(Component... coms) {
		return new Win0(coms);
	}

	public static Win0 of(Collection<Component> coms) {
		return new Win0(coms);
	}

	public static Win0 wrap(Component... coms) {
		return Win0.of(coms);
	}

	public Win0(Component... coms) {
		this(ARR.asAL(coms));
	}

	public Win0(Collection<Component> coms) {
		this.coms = coms instanceof List ? (List) coms : ARR.asAL(coms);
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
		return super.appendChild(child);
	}

	private boolean revert = false;

	public Win0 revert(boolean b) {
		this.revert = b;
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

	public Win0 position(WinPos pos, WinPos pos2) {
		setPosition(pos + "," + pos2);
		return this;
	}

	public Win0 closable(boolean... closable) {
		if (ARG.isDefNotEqFalse(closable)) {
			setClosable(true);
		}
		return this;
	}

	public Win0 position(WinPos wpos) {
		position(wpos.getPattern());
		return this;
	}

	public Win0 position(String position) {
		setPosition(position);
		return this;
	}

	public Win0 hl(boolean... doHighlighted) {
		if (ARG.isDefNotEqFalse(doHighlighted)) {
			doHighlighted();
		}
		return this;
	}

	public Win0 ovl(boolean... doOverlapped) {
		if (ARG.isDefNotEqFalse(doOverlapped)) {
			doOverlapped();
		}
		return this;
	}

	public Win0 embd(boolean... doEmbedded) {
		if (ARG.isDefNotEqFalse(doEmbedded)) {
			doEmbedded();
		}
		return this;
	}

	public Win0 modal(boolean... doModal) {
		if (ARG.isDefNotEqFalse(doModal)) {
			doModal();
		}
		return this;
	}


	public Win0 popup(boolean... doPopup) {
		if (ARG.isDefNotEqFalse(doPopup)) {
			doPopup();
		}
		return this;
	}

	//
//	public SWindow position(WPos... pos) {
//		if (pos.length == 1) {
//			setPosition(pos[0].name());
//		} else {
//			setPosition(Arrays.stream(pos).map(Enum::name).collect(Collectors.joining(",")));
//		}
//		return this;
//	}
	public Caption getCap0OrCreate(boolean... createIfNotExist) {
		return getCap0OrCreate(this, createIfNotExist);
	}

	public static Caption getCap0OrCreate(Window com, boolean... createIfNotExist) {
		if (com.getCaption() != null) {
			return com.getCaption();
		}
		if (ARG.isDefEqTrue(createIfNotExist)) {
			Cap0 child = new Cap0();
			com.appendChild(child);
			return child;
		}
		return null;
	}

	@Override
	public XulElement show(Component... parent) {
		return IZComExt.super.show(parent);
	}

	public static Win0 open(Collection<Component> components, Component... parent) {
		Win0 sWindow = of(components);
		boolean append = ARG.isDef(parent) ? parent[0].appendChild(sWindow) : ZKC.getFirstWindow().appendChild(sWindow);
		return sWindow;
	}

	public Win0 caption(Object captionTitleOrCom) {
		if (captionTitleOrCom instanceof CharSequence) {
			getCap0OrCreate(true).setLabel(captionTitleOrCom.toString());
		} else if (captionTitleOrCom instanceof Component) {
			getCaption().appendChild((Component) captionTitleOrCom);
		} else {
			throw new WhatIsTypeException("except string ot component, but it:" + captionTitleOrCom);
		}
		return this;
	}

}
