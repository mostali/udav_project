package zk_page;

import mpu.core.ARG;
import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpu.pare.Pare;
import mpc.ui.UColorTheme;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_com.win.CWindowComposer;
import zk_page.ZKC;
import zk_page.ZKS;

public class ZKM {
	public static Pare<Window, Tbxm> showModalEditor(Object title, String content, boolean... darkTheme) {
		Tbxm modalCom = new Tbxm(content, Tbx.DIMS.WH100);
		return Pare.of(showModal(title, modalCom, ZKC.getFirstWindow(), darkTheme), modalCom);
	}

	public static Window showModal(Object title_cap_com, HtmlBasedComponent modalCom, boolean... darkTheme) {
		return showModal(title_cap_com, modalCom, ZKC.getFirstWindow(), darkTheme);
	}

	public static Window showModalNative(Object title_cap_com, HtmlBasedComponent modalCom, boolean... darkTheme) {
		return showModal(title_cap_com, modalCom, ZKC.getFirstWindow(), darkTheme);
	}

	public static Window showModalZUL(String title, HtmlBasedComponent modalCom, Component parent) {
		return CWindowComposer.show(title, modalCom, parent);
	}

	public static Window showModal(Object title_cap_com, HtmlBasedComponent modalCom, Component parent, boolean... darkTheme) {
		return showModal(title_cap_com, modalCom, parent, new String[]{"80%", "80%"}, darkTheme);
	}

	public static Window showModal(Object title_cap_com, HtmlBasedComponent modalCom, Component parent, String[] wh, boolean... darkTheme) {

		if (title_cap_com == null) {
			title_cap_com = SYMJ.FILE_HTML + " View..";
		}
		Window w;
		if (title_cap_com instanceof CharSequence) {
			w = new Window(title_cap_com.toString(), null, true);
		} else {
			w = new Window(null, null, true);
			if (title_cap_com instanceof Caption) {
				w.appendChild((Caption) title_cap_com);
			} else if (title_cap_com instanceof Component) {
				Caption cap = w.getCaption();
				if (cap == null) {
					w.appendChild(cap = new Caption());
				}
				cap.appendChild((Component) title_cap_com);
			} else {
				throw new WhatIsTypeException("What is title?" + title_cap_com);
			}
		}

		Textbox tbx = new Textbox();
		tbx.setFocus(true);
		tbx.setStyle("opacity:0;position:absolute;margin-left:-10000px;");
		w.appendChild(tbx);

//		w.getCaption().appendChild(new Bt("+"));

		w.setParent(parent);
		w.doModal();
		w.appendChild(modalCom);
		if (ARG.isDefEqTrue(darkTheme)) {
			ZKS.enableDarkTheme(modalCom);
		}
		w.setWidth(wh[0]);
		w.setHeight(wh[1]);




		w.addEventListener(Events.ON_CANCEL, (SerializableEventListener) event -> {
			w.detach();
		});

		return w;
	}

}