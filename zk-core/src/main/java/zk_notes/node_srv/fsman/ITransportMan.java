package zk_notes.node_srv.fsman;

import zk_notes.node.NodeDir;

public interface ITransportMan {
	NodeDir moveItemToSd3(String sd3);
	NodeDir deleteItem();
}
