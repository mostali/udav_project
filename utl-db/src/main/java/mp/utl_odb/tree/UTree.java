package mp.utl_odb.tree;

import mpe.db.JdbcUrl;
import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mpc.fs.Ns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;

public class UTree extends Ctx3Db {

	public static void clearAll(Path... path) {
		Arrays.stream(path).forEach(p -> UTree.tree(p).truncateTable());
	}

	public static boolean isExistTreeModel(Path path) {
		return tree(path).getModelFirstOrLast(true, null) != null;
	}

	public static final Logger L = LoggerFactory.getLogger(UTree.class);

	@Override
	public String toString() {
		return "UTree: file://" + super.getDbFilePath().toAbsolutePath();
	}

	public UTree(Path path) {
		super(path);
	}

	private UTree(String tree, String treeKey) {
		super(tree, treeKey);
	}

	private UTree(Class tree, String treeKey) {
		super(tree, treeKey);
	}

	public UTree(String rootParentDir, String parentDir, String key, boolean isFileOrName) {
		super(rootParentDir, parentDir, key, isFileOrName);
	}

	/**
	 * *************************************************************
	 * -------------------------- TREE -----------------------
	 * *************************************************************
	 */

	public static UTree tree(Class treeParent, String treeKey) {
		return new UTree(treeParent, treeKey);
	}

	@Deprecated
	public static UTree tree(String treeKey) {
		return new UTree(UTree.class, treeKey);
	}

	public static UTree tree(String treeParent, String treeKey) {
		return new UTree(treeParent, treeKey);
	}

	public static UTree tree(String rootDir, String treeParent, String treeName) {
		return tree(rootDir, treeParent, treeName, false);
	}

	public static UTree tree(String rootDir, String treeParent, String treeFileOrName, boolean isFileOrName) {
		return new UTree(rootDir, treeParent, treeFileOrName, isFileOrName);
	}

	public static UTree treeApp(String treename) {
		return AppCore.of().tree(treename);
	}

	public static UTree treeApp(String ns, String treename) {
		return AppCore.of().tree(ns, treename);
	}

	public static UTree tree(Ns ns, String dbName) {
		return tree(ns.path(JdbcUrl.buildDbFileName(dbName)));
	}

	public static UTree tree(Path path) {
		return new UTree(path);
	}

}
