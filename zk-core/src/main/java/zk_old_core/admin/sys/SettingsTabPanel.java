package zk_old_core.admin.sys;

import zk_com.core.LazyBuilder;
import zk_old_core.admin.sys.tabs_old.TabsRender;
import zk_old_core.admin.sys.tabs_old.HeadLazyTabpanel;
import zk_old_core.admin.AdminPSP;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dav 10.01.2022   01:27
 */
public class SettingsTabPanel extends HeadLazyTabpanel {

	private final AdminPSP adminPageSP;

	public SettingsTabPanel(AdminPSP adminPageSP, String tabHead) {
		super(tabHead);
		this.adminPageSP = adminPageSP;
	}

	private LazyBuilder<SettingsTabPanel> lb = null;

	@Override
	public LazyBuilder<SettingsTabPanel> getLazyBuilder() {
		if (lb != null) {
			return lb;
		}
		lb = new LazyBuilder<SettingsTabPanel>() {
			@Override
			public void buildAndAppend(SettingsTabPanel settingsTabPanel) throws Exception {

				settingsTabPanel.getChildren().clear();

				List tabs = new ArrayList();

				tabs.add(new SysPropsTabPanel(adminPageSP, "System"));

				TabsRender.buildAndAdd(settingsTabPanel, tabs);

			}
		};

		return lb;
	}

}
