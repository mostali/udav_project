package zk_page.core;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import mpu.IT;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.sun_editor.IPerPage;
import zk_os.sec.ROLE;

import java.util.LinkedList;
import java.util.List;

@PageRoute(pagename = "", sd3 = "fs", role = ROLE.ADMIN)
public class WithComPSP extends PageSP implements IPerPage {

	@Getter
	@Setter
	private List<Component> components;

	public WithComPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {
		List<Component> l = IT.notEmpty(getComponents(), "set component's");
		for (Component component : l) {
			window.appendChild(component);
		}
	}

	public PageSP addComponent(Component com) {
		if (components == null) {
			components = new LinkedList<>();
		}
		components.add(IT.NN(com, "set component"));
		return this;
	}
}
