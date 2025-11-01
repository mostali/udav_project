package zk_notes.fsman;

import zk_notes.node.NodeDir;

public interface ITransportMan {
	NodeDir moveItemToSd3(String sd3);
	NodeDir deleteItem();
}
