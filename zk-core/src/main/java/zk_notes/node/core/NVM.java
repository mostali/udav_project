package zk_notes.node.core;

import mpc.fs.ext.GEXT;
import mpe.cmsg.std.MsvCallMsg;
import mpu.X;
import mpu.core.ARG;
import zk_notes.node.NodeDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

//NoteViewMedia
public enum NVM {
	IMG, AUDIO, VIDEO, BI;

	public static NVM defineType(NodeDir nodeDir, NVM... defRq) {
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
			switch (MsvCallMsg.ofQk(nodeDir.line0()).method) {
				case MVIEW:
				case MSTATS:
					return NVM.BI;
			}
//			else if (map.containsKey(GEXT.EDITABLE)) {
//				List<Path> paths = map.get(GEXT.EDITABLE);
//				if (EXT.MSV.hasIn(paths)) {
//					return NVM.BI;
//				}
//			}
		}
		return ARG.throwMsg(() -> X.f("Media type not found from node [%s]", nodeDir.nodeId()), defRq);
	}
}
