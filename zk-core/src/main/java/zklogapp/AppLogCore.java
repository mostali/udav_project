package zklogapp;

import mp.utl_odb.tree.CtxtDb;
import mp.utl_odb.tree.UTree;

import java.util.List;

public class AppLogCore {
	public static final UTree TREE_PROPS = UTree.tree(AppLogCore.class, "app.tree.props");
	public static final UTree TREE_QUICK_DIR_HISTORY = UTree.tree(AppLogCore.class, "quick-history-views");
//	public static final UTree TREE_INCLUDE_PHRASES = UTree.tree(AppLogCore.class, "include-phrases");
	public static final UTree TREE_EXCLUDE_PHRASES = UTree.tree(AppLogCore.class, "exclude-phrases");

	public static List<CtxtDb.CtxTimeModel> getItemsQuickHistory() {
		return TREE_QUICK_DIR_HISTORY.getModels();
	}

	public static List<CtxtDb.CtxTimeModel> getItemsExcludePhrases() {
		return TREE_EXCLUDE_PHRASES.getModels();
	}
}