package mp.utl_odb.tree;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import mp.utl_odb.query_core.QP;
import mpc.core.P;
import mpc.Sys;
import mpc.args.ARG;
import mpc.ERR;
import mpc.env.Env;
import mpc.exception.RequiredRuntimeException;
import mpc.json.UGson;
import mpc.fs.Ns;
import mpc.str.UST;
import mp.utl_ndb.JdbcUrl;
import mpc.time.EPOCH;
import mpc.time.QDate;
import mpc.types.pare.Pare3;
import mpc.types.pare.Pare4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class UTree extends CtxtlDb {

	public List<Pare3<String, String, String>> getPares(QP... qps) {
		return getModels(qps).stream().map(CtxTimeModel::toPare3).collect(Collectors.toList());
	}

	public static class UTreeExamples {
		public static void main(String[] args) {
//			case_PutGet();
			case_TIME_CACHED_VALUE();
			case_TIME_LIMITED_STATE();
			case_TIME_ACCESS();
		}

		@SneakyThrows
		public static void case_TIME_CACHED_VALUE() {

			UTree fooTree = UTree.tree("foo");

			//if lifetime value less that 10sec that return 'foo_value'
			//else throw CtxtlDb$UtreeDelayException: CACHE_MS:-1ms:::2023-09-09 13:09:00:::diffabs=11s
			String fooValue = fooTree.getTC("foo_key", 10_000);
			Sys.p(fooValue);

		}

		@SneakyThrows
		public static void case_TIME_LIMITED_STATE() {

			UTree fooTree = UTree.tree("foo");

			//Pare4{ ALLOWED, WAIT_MS , NEXT_ACTION_DATE_MS, TIME_MODEL }
			boolean regAction = false;
			Pare4<Boolean, Long, Long, CtxTimeModel> fooValue = fooTree.getTLS("foo_key", 10, regAction);
			ERR.state(fooValue.key(), "please wait ( less 10s) ", EPOCH.epochToDate(fooValue.ext().intValue()));
			Sys.p(fooValue.key());//true if less 10 sec after action, else false

			try {
				fooTree.getTLS("foo_key_wrong", 10, false);
			} catch (RequiredRuntimeException ex) {
				//ok
			}
		}

		@SneakyThrows
		public static void case_TIME_ACCESS() {

			UTree fooTree = UTree.tree("foo");

			//return 'foo_value' or throw CtxtlDb$UtreeDelayException: ALLOWED_HOUR:9
			String fooValue = fooTree.getTA("foo_key", ETA.ALLOWED_HOUR.paramHoursOrDays(QDate.now().hour));
			Sys.p(fooValue);
		}

		public static void case_PutGet() {
			//Put & Get
			UTree fooTree = UTree.tree("foo");
			fooTree.put("foo_key", "foo_value");
			Sys.p(fooTree.get("foo_key"));//foo_value
			try {
				Sys.p(fooTree.get("foo_key_wrong"));//key not found
			} catch (RequiredRuntimeException ex) {
				P.w(ex.getMessage());
				Sys.p(fooTree.get("foo_key_wrong", null));//null
			}
		}
	}

//	public static void main(String[] args) throws UtreeDelayException {
//
//		UTree rmmTree = UTree.tree("rmm1");
//		//rmmTree.clear();
//		rmmTree.put("key2");
//		P.exit(rmmTree.getTC("key2", 10_000l));
//
//		P.exit(rmmTree.getTState("key", 10, true));
//
//		P.exit(rmmTree.getTC_MODEL("key", 10_000));
//
//		P.exit(rmmTree.getTC("rmm", 15000));
//
//		try {
//			P.p(UTreeL.getL_TLA("k2", 30_000L));
//		} catch (UtreeDelayException e) {
//			throw new IllegalStateException(e);
//		}
//		try {
//			U.p(UTreeL.getL_TLA("k", ETA.ALLOWED_HOUR.paramHoursOrDays(21)));
//		} catch (UtreeDelayException e) {
//			e.printStackTrace();
//		}
//
//	}

//	static class TL_MTest {
//		public static void main(String[] args) {
//			UTree rmmTree = UTree.tree("rmm-test");
//			int limitSec = 5;
//			//SLEEP.sleepSec(limitSec);
//			Supplier<Pare4<Boolean, Long, Long, CtxTimeModel>> getter = () -> rmmTree.getTState("key", limitSec, true);
//			UC.state(getter.get().key());
//			UC.state(!getter.get().key());
//			//SLEEP.sleepSec(limitSec);
//			UC.state(getter.get().key());
//			P.p("ok: " + rmmTree);
//		}
//	}


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

	public static UTree treeApp(String appname) {
		Path appDataDir = Env.RL_PPJM_OR_PRL_DATA.resolve(ERR.notEmpty(appname));
		return tree(appDataDir);
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
			JsonObject jo = UGson.JO(UGson.EMPTY_PATTERN);
			model = put(nkey, jo);
			return jo;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Row not found by key '%s'", nkey), defRq);
	}

	public CtxTimeModel putJson(String nkey, JsonObject json, String ext) {
		return put(nkey, json.toString(), ext);
	}

}
