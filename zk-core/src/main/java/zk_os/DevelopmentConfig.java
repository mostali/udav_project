package zk_os;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.DesktopInit;

import javax.annotation.PostConstruct;

@Configuration
@Profile("dev")
public class DevelopmentConfig {
	private static Logger logger = LoggerFactory.getLogger(DevelopmentConfig.class);

	@PostConstruct
	public void initDevelopmentProperties() throws Exception {
		init();
	}

	public static void init() throws Exception {

		if (AppZosProps.APD_IS_PROM_ENABLE.getValueOrDefault(false)) {
			return;
		}

		logger.info("**************************************************************");
		logger.info("**** ZK-Springboot-Demo: development configuration active ****");
		logger.info("**************************************************************");

		//disable various caches to avoid server restarts
		Library.setProperty("org.zkoss.zk.ZUML.cache", "false");
		Library.setProperty("org.zkoss.zk.WPD.cache", "false");
		Library.setProperty("org.zkoss.zk.WCS.cache", "false");
		Library.setProperty("org.zkoss.web.classWebResource.cache", "false");
		WebApps.getCurrent().getConfiguration().addListener(DynamicLabelReloader.class);

		// enable non minified js
		WebApps.getCurrent().getConfiguration().setDebugJS(true);

		// enable for debugging MVVM commands and binding (very verbose)
		Library.setProperty("org.zkoss.bind.DebuggerFactory.enable", "false");
	}

	/**
	 * resets/reloads zk-labels for each page load
	 */
	public static class DynamicLabelReloader implements DesktopInit {
		@Override
		public void init(Desktop desktop, Object o) throws Exception {
			Labels.reset();
		}
	}

}
