package zk_page.node;

import mpu.pare.Pare;
import zk_page.node_state.FileState;

import java.nio.file.Path;

public abstract class SiteDir<FORMSTATE extends FileState> extends FileSiteEntity<FORMSTATE> {

	public SiteDir(Pare sdn) {
		super(sdn);
	}

	public SiteDir(Path siteDir, Pare sdn) {
		super(siteDir, sdn);
	}
}