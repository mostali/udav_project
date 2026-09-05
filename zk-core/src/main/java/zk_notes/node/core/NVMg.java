package zk_notes.node.core;

import mpc.fs.ext.GEXT;
import mpu.X;
import mpu.core.ARG;
import zk_notes.node.NodeDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

//NoteViewMedia
public enum NVMg {
	AUDIO_IMG;

	public static NVMg getAutoType(NodeDir nodeDir, NVMg... defRq) {
		Map<GEXT, List<Path>> map = nodeDir.getProxyRW().getTargetPathDir().dMapGExt(null);
		if (map != null) {
			boolean isAudio = map.containsKey(GEXT.AUDIO);
			boolean isIMg = map.containsKey(GEXT.IMG);
			boolean isVideo = map.containsKey(GEXT.VIDEO);
			if (isAudio && isIMg) {
				return AUDIO_IMG;
			}
		}
		return ARG.throwMsg(() -> X.f("NVMg Media type not found"), defRq);
	}
}
