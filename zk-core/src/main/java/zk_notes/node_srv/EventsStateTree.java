package zk_notes.node_srv;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpe.core.ERR;
import zk_notes.node.NodeDir;
import zk_notes.node_state.AppStateFactory;

@RequiredArgsConstructor
public class EventsStateTree {

	final NodeDir nodeDir;
//	final NodeID nodeDir;

	public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 1000, 1000, 5000, 500, 0};
//	public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 10, 10, 30, 15, 0};

	public UTree getDb() {
		return AppStateFactory.getEventTreeOrCreate(nodeDir.nodeID());
	}

	public void store(String src, String head, Exception e) {
		getDb().withUpdateMode(ICtxDb.UpdateMode.ADD).put(src, head, e == null ? null : ERR.getMessagesAsStringWithHead(e, head));
	}

}
