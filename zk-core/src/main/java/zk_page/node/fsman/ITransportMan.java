package zk_page.node.fsman;

import zk_page.node.NodeDir;

public interface ITransportMan {
	NodeDir moveItemToSd3(String sd3);
	NodeDir deleteItem();
}
