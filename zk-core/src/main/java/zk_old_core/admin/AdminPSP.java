package zk_old_core.admin;

import org.zkoss.zul.*;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_old_core.admin.sys.ControlLazyTabpanel;
import zk_old_core.admin.sys.SettingsTabPanel;
import zk_page.ZKR;
import zk_old_core.admin.sys.tabs_old.TabsRender;
import zk_page.core.SpVM;
import zk_old_core.control_old.TopAdminMenu;

import java.util.*;

/**
 * @author dav 07.01.2022   19:24
 */
//@VariableResolver(DelegatingVariableResolver.class)
@PageRoute(pagename = "admin")
public class AdminPSP extends PageSP {

	public AdminPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

//	@WireVariable
//	public BaeApp baeApp;

	public void buildPageImpl() {

		TopAdminMenu ctrlMenu = (TopAdminMenu) new TopAdminMenu().appendTo(window);

		ZKR.activePush();

//		LazySpan.LazyBuilder lb = new LazySpan.LazyBuilder() {
//			@Override
//			public Component build() {
//				Label hello = new Label("hello");
////				child.appendChild(hello);
//				return hello;
//			}
//		};
//
//		LazySpan child = new LazySpan(lb);
//		parentWindow.appendChild(child);


		Tabpanel ROOT_PAGES_L1 = new Tabpanel();
		{
			List tabsL2 = new ArrayList();
			tabsL2.add("PAGES");
			tabsL2.add(ROOT_PAGES_L1);
			tabsL2.add(new ControlLazyTabpanel("APP"));
			tabsL2.add(new SettingsTabPanel(this, "Settings"));
			TabsRender.buildAndAdd(window, tabsL2);
		}
//		new BaeLazyTabpanel("BAE");
		TabsRender.buildAndAdd(ROOT_PAGES_L1, "BAE", new Label("ni"), "UNDEFINED", new Label("Tabpanel L2"));

	}

}
