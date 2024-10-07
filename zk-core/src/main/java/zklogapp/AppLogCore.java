package zklogapp;

import mp.utl_odb.tree.UTree;

public class AppLogCore {
	public static final UTree TREE_PROPS = UTree.tree(AppLogCore.class, "app.tree.props");
	public static final UTree TREE_QUICK_HISTORY = UTree.tree(AppLogCore.class, "quick-history-views");
//	public static final UTree TREE_INCLUDE_PHRASES = UTree.tree(AppLogCore.class, "include-phrases");
	public static final UTree TREE_EXCLUDE_PHRASES = UTree.tree(AppLogCore.class, "exclude-phrases");
}
