package zklogapp.otr.srvloader;

import lombok.RequiredArgsConstructor;
import zk_com.base_ctr.Div0;
import zk_page.ZKCFinder;
import zk_page.ZKS;

@RequiredArgsConstructor
public class StandsControlPanel extends Div0 {

	public static StandsControlPanel findFirst(StandsControlPanel... defRq) {
		return ZKCFinder.findFirstIn_Page(StandsControlPanel.class, true, defRq);
	}


	@Override
	protected void init() {
		super.init();

		for (ServerLogDownloaderSrv.Stand value : ServerLogDownloaderSrv.Stand.values()) {
			StandControlPanel standLogPanel = new StandControlPanel(value);
			appendChild(standLogPanel);
		}

		ZKS.toggleDnone(this);
	}
}
