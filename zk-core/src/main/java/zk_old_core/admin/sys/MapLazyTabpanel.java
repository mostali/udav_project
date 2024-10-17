package zk_old_core.admin.sys;

import mpc.env.Env;
import zk_com.core.LazyBuilder;
import zk_old_core.admin.sys.tabs_old.HeadLazyTabpanel;

import java.nio.file.Paths;
import java.util.Map;

/**
 * @author dav 10.01.2022   01:24
 */
public class MapLazyTabpanel extends HeadLazyTabpanel {

	private final String tabName;
	private final Map map;

	public MapLazyTabpanel(String tabName, Map map) {
		super(tabName);
		this.tabName = tabName;
		this.map = map;
	}

	private LazyBuilder<? extends MapLazyTabpanel> lb = null;

	@Override
	public LazyBuilder<? extends MapLazyTabpanel> getLazyBuilder() {
		if (lb != null) {
			return lb;
		}
		lb = new LazyBuilder<MapLazyTabpanel>() {
			@Override
			public void buildAndAppend(MapLazyTabpanel mapSettingsTabPanel) throws Exception {

				mapSettingsTabPanel.getChildren().clear();

				MapDiv mapCom = null;
				if (tabName.equals("applicationConfig: [file:./application.properties]")) {
					mapCom = new FileMapDiv(Paths.get(Env.FILE_APPLICATION_PROPERTIES), tabName, map);
				} else {
					mapCom = new MapDiv(tabName, map);
				}
				mapSettingsTabPanel.appendChild(mapCom);

			}
		};

		return lb;
	}

}
