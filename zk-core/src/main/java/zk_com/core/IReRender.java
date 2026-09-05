package zk_com.core;

import mpc.rfl.RFL;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import static zk_page.ZKC.getFirstWindow;

public interface IReRender<C extends Component> {

	default C rerender(boolean fistInWindow_last) {
		Window firstWindow = getFirstWindow();
		if (fistInWindow_last) {
			firstWindow.insertBefore(newCom(), firstWindow.getFirstChild());
		} else {
			firstWindow.appendChild(newCom());
		}
		return (C) this;
	}

	default C rerender() {
		removeThisComponentAndGetParent().key().appendChild(newCom());
		return (C) this;
	}

	default C newCom() {
		return (C) RFL.instEmptyConstructor(getClass());
	}

	default Pare<Component, Component> removeThisComponentAndGetParent() {
		Component com = (Component) this;
		Component parent = com.getParent();
		parent.removeChild(com);
		com.detach();//TODO lolo? mb no leak?
		return Pare.of(parent, com);
	}
}
