package zk_page.node;

import mpu.pare.Pare;
import zk_page.node_state.FileState;

import java.nio.file.Path;

public abstract class SdDir<FORMSTATE extends FileState> extends FileSiteEntity<FORMSTATE> {

	public SdDir(Pare sdn) {
		super(sdn);
	}

	public SdDir(Path siteDir, Pare sdn) {
		super(siteDir, sdn);
	}
}