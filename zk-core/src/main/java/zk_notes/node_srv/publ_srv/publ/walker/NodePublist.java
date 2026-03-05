package zk_notes.node_srv.publ_srv.publ.walker;

import lombok.Getter;
import mpe.cmsg.NodeData;

public class NodePublist {

//	public static final String FILE_PUBLIST = ".publist";

	private final @Getter NodeData node;

	public NodePublist(NodeData node) {
		this.node = node;
	}

}
