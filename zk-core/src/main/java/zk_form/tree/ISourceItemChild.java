package zk_form.tree;

import java.util.List;

public interface ISourceItemChild extends ISourceItem {
	List<ISourceItem> getChilds();

	default boolean isLeaf() {
		return getChilds() == null;
	}
}
