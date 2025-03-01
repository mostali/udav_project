package mp.utl_odb.tree;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import mp.utl_ndb.JdbcUrl;
import mp.utl_odb.DBU;
import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.query_core.QP;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.Ns;
import mpc.json.UGson;
import mpc.types.abstype.AbsType;
import mpu.core.ARG;
import mpu.pare.Pare3;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UTree extends CtxtlDb {

	public static void clear(Path... path) {
		Arrays.stream(path).forEach(p -> UTree.tree(p).clear());
	}

	public static boolean isExistTreeModel(Path path) {
		return tree(path).getModelFirstOrLastRq(true, null) != null;
	}

	public List<Pare3<String, String, String>> getPares(QP... qps) {
		return getModels(qps).stream().map(CtxTimeModel::toPare3).collect(Collectors.toList());
	}

	public static final Logger L = LoggerFactory.getLogger(UTree.class);

	@Override
	public String toString() {
		return "UTree: file://" + super.getDbFilePath().toAbsolutePath();
	}

	public UTree(Path path) {
		super(path);
	}

	public UTree(String key) {
		super(UTree.class.getSimpleName() + key);
	}

	private UTree(String tree, String treeKey) {
		super(tree, treeKey);
	}

	private UTree(Class tree) {
		super(tree);
	}

	private UTree(Class tree, String treeKey) {
		super(tree, treeKey);
	}

	public UTree(String rootParentDir, String parentDir, String key, boolean isFileOrName) {
		super(rootParentDir, parentDir, key, isFileOrName);
	}

	/**
	 * GET DEFAULT TYPES
	 */
	public String getString(String nkey, String... defRq) {
		return get(nkey, defRq);
	}

	public Long getLong(String nkey, Long... defRq) {
		return UST.LONG(getString(nkey, null), defRq);
	}

	public Integer getInt(String nkey, Integer... defRq) {
		return UST.INT(getString(nkey, null), defRq);
	}

	public Boolean getBoolean(String nkey, Boolean... defRq) {
		return UST.BOOL(getString(nkey, null), defRq);
	}

	public Float getFloat(String nkey, Float... defRq) {
		return UST.FLOAT(getString(nkey, null), defRq);
	}

	public Double getDouble(String nkey, Double... defRq) {
		return UST.DBL(getString(nkey, null), defRq);
	}


	// **********************************************
	// **********************************************SPECIFIC TREE
	// **********************************************
	public static String treeGetValue(String tree, String key) {
		return tree(tree).get(key, null);
	}

	public static String treeGetValue(Class tree, String key) {
		return tree(tree).get(key, null);
	}

	public static CtxtlDb treeSetValue(String tree, String key, String value) {
		CtxtlDb db = tree(tree);
		db.put(key, value);
		return db;
	}

	public static void treeRemoveValue(String tree, String key) {
		CtxtlDb db = tree(tree);
		db.remove(key);
	}

	public static List<String> treeKeysValues(String treeName) {
		return treeKeysValues(tree(treeName), treeName);
	}

	public static List<String> treeKeysValues(UTree tree, String treeName) {
		return tree.getModels().stream().map(e -> e.getKey()).collect(Collectors.toList());
	}

	/**
	 * *************************************************************
	 * -------------------------- TREE -----------------------
	 * *************************************************************
	 */

	public static UTree tree(Class treeParent) {
		return new UTree(treeParent);
	}

	public static UTree tree(Class treeParent, String treeKey) {
		return new UTree(treeParent, treeKey);
	}

	public static UTree treeERRORS() {
		return tree("ERRORS");
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
//		Path appDataDir = Env.RL_PPJM_OR_PRL_DATA.resolve(IT.notEmpty(appname));
//		return tree(appDataDir);
		return AppCore.of().tree(treename);
	}

	public static UTree tree(Path path) {
		return new UTree(path);
	}

	public static UTree tree(Ns ns, String dbName) {
		return new UTree(ns.path(JdbcUrl.buildDbFileName(dbName)));
	}

	/**
	 * *************************************************************
	 * -------------------------- JSON -----------------------
	 * *************************************************************
	 */

	public JsonObject getJson(String nkey, boolean create, JsonObject... defRq) {
		CtxTimeModel model = getCtxTimeModelByKey(nkey);
		if (model != null) {
			return UGson.JO(model.getValue());
		} else if (create) {
			JsonObject jo = UGson.JO(UGson.EMPTY);
			model = put(nkey, jo);
			return jo;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Row not found by key '%s'", nkey), defRq);
	}

	public CtxTimeModel putJson(String nkey, JsonObject json, String ext) {
		return put(nkey, json.toString(), ext);
	}

	public List<List<AbsType>> getModelsAsMapRow(QP... qps) {
		return getModels(qps).stream().map(CtxtDb.CtxTimeModel::toAbsRow5).collect(Collectors.toList());
	}

	@SneakyThrows
	public int deleteById(Long id) {
		return DBU.removeModel_(this, CtxTimeModel.class, new QP[]{QP.pID(id)});
	}
}
