package zk_com.core;

import mpu.X;
import mpu.str.STR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.A;
import org.zkoss.zul.Html;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.*;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.Span0;
import zk_com.base_ext.EscTbx;
import zk_page.events.ZKE;
import zk_page.ADDC;
import zk_page.ZKS;

import java.nio.file.Path;

public interface IZComExt<T extends XulElement> extends IZCom<T>, IZWin<T> {

	default XulElement appendChilds(Component... child) {
		XulElement com = comX();
		for (Component component : child) {
			com.appendChild(component);
		}
		return com;
	}

	default EscTbx appendClosableByEsc(Component... closeIT) {
		EscTbx escTbx = new EscTbx(closeIT);
		appendChilds(escTbx);
		return escTbx;
	}

	default Cb appendCb(String message, Object... args) {
		return ADDC.CB(com(), X.f(message, args));
	}

	default Xml appendH(int i, Object headerValue, String... tagAttrs) {
		Xml xml = Xml.H(i, headerValue, tagAttrs);
		comH().appendChild(xml);
		return xml;
	}

	default Lb appendLb(String message, Object... args) {
		return ADDC.LB(com(), X.f(message, args));
	}

	default Ln appendLn(Object href_action, String label, Object... args) {
		return ADDC.LN(com(), X.f(label, args), href_action);
	}

	default Img appendImg(Object path_file_href) {
		return ADDC.IMG(com(), path_file_href);
	}

	default Div0 appendDiv(Component... coms) {
		Div0 divWith = Div0.of(coms);
		appendChilds(divWith);
		return divWith;
	}

	default Span0 appendSpan(Component... coms) {
		Span0 divWith = Span0.of(coms);
		appendChilds(divWith);
		return divWith;
	}

	default Menupopup0 appendMenupopup(String openMenuOnEvent_orNullIfRightClick) {
		XulElement com = comX();
		return IZCom.createPopupMenu(com, com, openMenuOnEvent_orNullIfRightClick);
	}

	default Menupopup0 appendMenupopup(XulElement openOnChild) {
		return appendMenupopup(openOnChild, ZKE.getSensEventDesctopOrMobile());
	}

	default Menupopup0 appendMenupopup(XulElement openOnChild, String openMenuOnEvent_orNullIfRightClick) {
		XulElement com = comX();
		com.appendChild(openOnChild);
		return IZCom.createPopupMenu(com, openOnChild, openMenuOnEvent_orNullIfRightClick);
	}

	default Lb appendLbBlock(String message, Object... args) {
		Lb line = appendLb(message, args);
		ZKS.BLOCK(line);
		return line;
	}

	default Xml appendCode(String message, Object... args) {
		Xml pre = Xml.PRE(X.f_(message, args));
		appendChilds(pre);
		return pre;
	}

	default A appendA(String href, String label, Object... args) {
		Ln link = new Ln(X.f(label, args), href, false);
		com().appendChild(link);
		return link;
	}

	default Html appendHr() {
		return appendHtml("<hr/>");
	}

	default Html appendBr() {
		return appendHtml("<br/>");
	}

	default Html appendHtml(String html, Object... args) {
		Html component = new Html(X.f(html, args));
		com().appendChild(component);
		return component;
	}

	default Bt appendBt(Object href_action, String btName, Object... args) {
		return ADDC.BT(com(), X.f(btName, args), href_action);
	}


	default IZComExt visibleOnMouseOver(Component target) {
		Component com = (Component) this;
		com.addEventListener(Events.ON_MOUSE_OVER, (SerializableEventListener<Event>) event -> target.setVisible(true));
		com.addEventListener(Events.ON_MOUSE_OUT, (SerializableEventListener<Event>) event -> target.setVisible(false));
		return this;
	}

}
