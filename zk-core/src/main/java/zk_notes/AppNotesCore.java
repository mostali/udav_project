package zk_notes;

import mp.utl_odb.tree.UTree;
import mpu.str.SPLIT;
import zk_os.AppZosProps;
import zk_page.core.SpVM;

import java.util.List;

public class AppNotesCore {

	public static final UTree TREE_PROPS = UTree.tree(AppZosProps.class, "app.tree.props");

	public static UTree TREE_QUICK_HISTORY__PAGED() {
		return UTree.tree(AppNotesCore.class, "quick-list-" + SpVM.get().ppi().pagename());
	}

	public static List<String> loadTopCtxValues(String listName) {
		String pares = TREE_QUICK_HISTORY__PAGED().get(listName);
		List<String> paresList = SPLIT.allByNL(pares);
		return paresList;

	}

	public static UTree TREE_AUTH_USERS() {
		return UTree.tree(AppNotesCore.class, "auth");
	}
}
