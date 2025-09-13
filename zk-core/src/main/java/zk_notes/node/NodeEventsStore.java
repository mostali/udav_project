package zk_notes.node;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpe.core.ERR;
import mpu.pare.Pare;
import zk_os.AFC;

import java.nio.file.Path;

@RequiredArgsConstructor
public class NodeEventsStore {
	final NodeDir nodeDir;

	public static NodeEventsStore of(NodeDir nodeDir) {
		return new NodeEventsStore(nodeDir);
	}

		public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 1000, 1000, 5000, 500, 0};
//	public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 10, 10, 30, 15, 0};

	public UTree getDb() {
		Pare<String, String> sdn = nodeDir.sdn();
		UTree tree = (UTree) UTree.tree(nodeDir.toPathEventTree()).withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
		return tree;
	}

	public void store(String src, String head, Exception e) {
		getDb().withUpdateMode(ICtxDb.UpdateMode.ADD).put(src, head, e == null ? null : ERR.getMessagesAsStringWithHead(e, head));
	}

}
