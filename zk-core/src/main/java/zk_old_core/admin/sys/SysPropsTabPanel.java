package zk_old_core.admin.sys;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import zk_os.AppZos;
import zk_com.core.LazyBuilder;
import zk_old_core.admin.sys.tabs_old.TabsRender;
import zk_old_core.admin.sys.tabs_old.HeadLazyTabpanel;
import zk_old_core.admin.AdminPSP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dav 10.01.2022   01:23
 */
public class SysPropsTabPanel extends HeadLazyTabpanel {

	private final AdminPSP adminPageSP;

	public SysPropsTabPanel(AdminPSP adminPageSP, Object tabHead) {
		super(tabHead);
		this.adminPageSP = adminPageSP;
	}

	private LazyBuilder<SysPropsTabPanel> lb = null;

	@Override
	public LazyBuilder<SysPropsTabPanel> getLazyBuilder() {
		if (lb != null) {
			return lb;
		}
		lb = new LazyBuilder<SysPropsTabPanel>() {
			@Override
			public void buildAndAppend(SysPropsTabPanel sysPropsTabPanel) throws Exception {

				sysPropsTabPanel.getChildren().clear();

				List tabs = new ArrayList();

//					Map<String, Object> map = new HashMap();
				for (Iterator it = ((AbstractEnvironment) AppZos.get().ENV).getPropertySources().iterator(); it.hasNext(); ) {
					PropertySource propertySource = (PropertySource) it.next();
					if (propertySource instanceof MapPropertySource) {
						Map<String, Object> source = ((MapPropertySource) propertySource).getSource();

						String tabName = propertySource.getName();

						tabs.add(new MapLazyTabpanel(tabName, source));

					}
				}

				TabsRender.buildAndAdd(sysPropsTabPanel, tabs);

			}
		};

		return lb;
	}

}
