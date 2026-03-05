package zk_notes.node.core;

import mpc.fs.ext.GEXT;
import mpu.X;
import mpu.core.ARG;
import zk_notes.node.NodeDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

//NoteViewMedia
public enum NVM {
	IMG, AUDIO, VIDEO;

	public static NVM getAutoType(NodeDir nodeDir, NVM... defRq) {
		Map<GEXT, List<Path>> map = nodeDir.dMapGExt(null);
//		Map<GEXT, List<Path>> map = nodeDir.getProxyRW().getTargetAnyIPath().dMapGExt(null);
		if (map != null) {
			if (map.containsKey(GEXT.VIDEO)) {
				return NVM.VIDEO;
			} else if (map.containsKey(GEXT.AUDIO)) {
				return NVM.AUDIO;
			} else if (map.containsKey(GEXT.IMG)) {
				return NVM.IMG;
			}
		}
		return ARG.throwMsg(() -> X.f("Media type not found from node [%s]", nodeDir.nodeId()), defRq);
	}
}
