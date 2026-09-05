package zk_com.base_ctr;

import mpu.core.ARRi;
import mpu.IT;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;
import org.zkoss.zul.Span;
import org.zkoss.zul.impl.XulElement;
import zk_com.core.IZComExt;

/**
 * @author dav 13.05.2022   08:23
 */

@Deprecated
public class SpanCtx extends Span implements IZComExt {
//	private XulElement onChild;

	public SpanCtx() {
	}

	public static SpanCtx wrap(Component... coms) {
		return SpanCtx.of(coms);
	}

	public static SpanCtx of(Component... coms) {
		SpanCtx spanCtx = new SpanCtx();
		for (Component com : coms) {
			spanCtx.appendChild(com);
		}
		return spanCtx;
	}

//	public SpanCtx(XulElement child) {
//		setChild(child);
//	}

//	public void setChild(XulElement onChild) {
//		UC.isNull(this.onChild);
//		this.onChild = onChild;
//		appendChild(onChild);
//	}

	/**
	 * *************************************************************
	 * ---------------------------- CONTEXT --------------------------
	 * *************************************************************
	 */
	private Menupopup menuPopup;

	public void addContextMenuItem(String name, String event, EventListener eventListener) {
		Menuitem menuItem = new Menuitem(IT.notBlank(name));
		menuItem.addEventListener(event, eventListener);
		addContextMenuItem(menuItem);
	}

	public void addContextMenuItem(Menuitem menuItem) {
		getContextMenuPopup().appendChild(menuItem);
	}

	public Menupopup getContextMenuPopup() {
		XulElement firstChild = IT.NN(ARRi.first(getChildren(), null), "before set child");
		return menuPopup != null ? menuPopup : (menuPopup = Menupopup0.createMenupopup(this, firstChild, null));
	}

	public Menupopup getContextMenuPopup(XulElement onChild, String openMenuOnEvent_orNullIfRightClick) {
		return menuPopup != null ? menuPopup : (menuPopup = Menupopup0.createMenupopup(this, onChild, openMenuOnEvent_orNullIfRightClick));
	}

	public void addContextMenuSeparator() {
		Menuseparator separator = new Menuseparator();
		getContextMenuPopup().appendChild(separator);
	}

	/**
	 * *************************************************************
	 * ---------------------------- REVERT --------------------------
	 * *************************************************************
	 */

	private boolean revert = false;

	public SpanCtx isRevert(boolean revert) {
		this.revert = revert;
		return this;
	}

	@Override
	public boolean insertBefore(Component newChild, Component refChild) {
		if (revert) {
			int size = getChildren().size();
			return super.insertBefore(newChild, getChildren().get(size - (size > 1 ? 2 : 1)));
		}
		return super.insertBefore(newChild, refChild);
	}
}
