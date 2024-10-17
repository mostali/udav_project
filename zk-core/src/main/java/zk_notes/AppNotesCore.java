package zk_notes;

import mp.utl_odb.tree.UTree;
import mpu.str.SPLIT;
import zk_page.core.SpVM;

import java.util.List;

public class AppNotesCore {
	//	public static final UTree TREE_PROPS = UTree.tree(AppLogCore.class, "app.tree.props");
//	private static final UTree TREE_QUICK_HISTORY = UTree.tree(AppNotesCore.class, "quick-list");
//	public static final UTree TREE_INCLUDE_PHRASES = UTree.tree(AppLogCore.class, "include-phrases");
//	public static final UTree TREE_EXCLUDE_PHRASES = UTree.tree(AppLogCore.class, "exclude-phrases");

	public static UTree TREE_QUICK_HISTORY() {
		return UTree.tree(AppNotesCore.class, "quick-list-" + SpVM.get().ppi().pagename());
	}

	public static List<String> loadTopCtxValues(String listName) {
		String pares = TREE_QUICK_HISTORY().get(listName);
		List<String> paresList = SPLIT.allByNL(pares);
		return paresList;

	}

	public static UTree TREE_AUTH_USERS() {
		return UTree.tree(AppNotesCore.class, "auth");
	}
}
