package mp.utl_odb.tree.trees.api_expiremental;


import com.j256.ormlite.logger.Level;
import lombok.RequiredArgsConstructor;
import mp.utl_odb.DBU;
import mp.utl_odb.tree.ctxdb.CKey;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.UTree;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ENUM;
import mpc.exception.ERxception;
import mpc.exception.WhatIsTypeException;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TreeCallEE extends ERxception {

	public static final Logger L = LoggerFactory.getLogger(TreeCallEE.class);

	public static void main(String[] args) {
		DBU.setLogLevel(Level.WARNING);
		testBase();
//		P.exit(TreeCall.byKey(Pare.of("testp", "testk"), "ls", "k1").call());
//		P.exit(TreeCall.byKey(Pare.of("testp", "testk"), "rm", "k1").call());
	}

	private static void testBase() {
//		P.exit(TreeCall.byKeyValue(Pare.of("java-tests", "testk"), "put", "k1", "v1").call());
//		P.exit(TreeCall.byKey(Pare.of("java-tests", "testk"), "get", "k1").call());
//		P.exit(TreeCall.byKey(Pare.of("java-tests", "testk"), "rm", "k1").call());

//		P.exit(TreeCall.byKeyValue(Pare.of("java-tests", "testk"), "put", "k1", "v1").call());
		IT.state(TreeCall.byKeyValue(Pare.of("java-tests", "testk"), "put", "k1", "v1").call().equals(Pare3.of("k1", "v1", null)), "<--" + TreeCall.byKeyValue(Pare.of("java-tests", "testk"), "put", "k1", "v1").call());
		IT.state(TreeCall.byKeyValue(Pare.of("java-tests", "testk"), "get", "k1", null).call().equals(Pare3.of("k1", "v1", null)), "<--" + TreeCall.byKeyValue(Pare.of("java-tests", "testk"), "get", "k1", null).call());
		IT.state(TreeCall.byKeyValue(Pare.of("java-tests", "testk"), "has", "k1", null).call().equals(true), "<--" + TreeCall.byKeyValue(Pare.of("java-tests", "testk"), "has", "k1", null).call());
		IT.state(TreeCall.byKeyValue(Pare.of("java-tests", "testk"), "ls", "k1", null).call().equals(ARR.as("k1")), "<--" + TreeCall.byKeyValue(Pare.of("testp", "testk"), "ls", "k1", null).call());
		IT.state(TreeCall.byKeyValue(Pare.of("java-tests", "testk"), "rm", "k1", "v1").call().equals(Pare3.of("k1", "v1", null)), "already removed");


//		P.exit(TreeCall.byKey(Pare.of("testp", "testk"), "get", "k1").call());
	}

	@RequiredArgsConstructor
//	@Builder
	public static class TreeCall<T> {

		final String treeParent, treeKey, action, key, val, ext;
		final Long index;

		public static TreeCall byKey(Pare<String, String> db, String action, String key) {
			return new TreeCall(db.key(), db.val(), action, key, null, null, null);
		}

		public static TreeCall byKeyValue(Pare<String, String> db, String action, String key, String val) {
			return new TreeCall(db.key(), db.val(), action, key, val, null, null);
		}

		public static <T> T throwIfReturnNull(Supplier<Throwable> exception, T returnObject) {
			return returnObject == null ? X.throwException(exception.get()) : returnObject;
		}

		public T call() {


			UTree tree;
//			if (X.notEmptyAll(treeParent, treeKey)) {
			tree = getTree();

			boolean existDb = tree.isExistDb();
//			} else if (X.notEmpty(treeKey)) {
//				tree = UTree.tree(treeKey);
//			} else {
//				tree = UTree.tree(treeKey);
//			}
			if (L.isInfoEnabled()) {
				L.info("Init tree by parent='{}', key='{}' -> file://{} {}", treeParent, treeKey, tree.getDbFilePath(), existDb ? "" : "(NEW)");
			}

			TreeCallType treeCallType = ENUM.valueOf(action, TreeCallType.class, true, null);
			if (treeCallType == null) {
				throw EE.ILLEGAL_ACTION.I("Illegal action name '%s'", action);
			}

			switch (treeCallType) {
				case PUT:
					return (T) tree.put(key, val, ext).toPare3();
				case GET: {
					checkExistDb();
					Ctx3Db.CtxModelCtr ctxTimeModelByKey = tree.getModelByKey(key);
					return (T) throwIfReturnNull(() -> EE.KEY_NOT_FOUND.I("Key '%s' not found", key), ctxTimeModelByKey).toPare3();
				}
				case RM: {
					checkExistDb();
					Ctx3Db.CtxModelCtr ctxTimeModelByKey = tree.getModelByKey(key);
					if (ctxTimeModelByKey == null) {
						return (T) throwIfReturnNull(() -> EE.KEY_NOT_FOUND.I("Key '%s' not found", key), ctxTimeModelByKey).toPare3();
					}
					int remove = tree.removeByKeyIfExist(key);
					return (T) ctxTimeModelByKey.toPare3();
				}
				case LS: {
					if (!existDb) {
						return (T) ARR.EMPTY_LIST;
					}
					List<Ctx3Db.CtxModelCtr> models = tree.getModels();
					return (T) models.stream().map(m -> m.getKey()).collect(Collectors.toList());
				}
				case HAS:
					if (!existDb) {
						return (T) (Boolean) false;
					}
					return (T) (Boolean) tree.containsBy(CKey.of(key));

				case ADDLINE:
				case GETLINE:
				default:
					throw new WhatIsTypeException("ni:" + treeCallType);
			}
		}

		private UTree tree = null;

		private UTree getTree() {
			IT.notEmptyAll(treeParent, treeKey);
			return tree == null ? tree = UTree.tree(treeParent, treeKey) : tree;
		}

		private void checkExistDb() {
			UTree tree = getTree();
			if (!tree.isExistDb()) {
				throw EE.DB_NOT_FOUND.I("Store not exist, parent '%s', key='%s'", treeParent, treeKey);
			}
		}
	}

	enum TreeCallType {
		PUT, GET, RM, HAS, LS, ADDLINE, GETLINE;
	}

	/**
	 * *************************************************************
	 * ---------------------------- INIT --------------------------
	 * *************************************************************
	 */

	public enum EE {
		NOSTATUS, KEY_NOT_FOUND, ILLEGAL_ACTION, DB_NOT_FOUND;

		public TreeCallEE I() {
			return new TreeCallEE(this);
		}

		public TreeCallEE I(Throwable ex) {
			TreeCallEE er = new TreeCallEE(this, ex);
			return er;
		}

		public TreeCallEE I(String message) {
			TreeCallEE er = new TreeCallEE(this, new RuntimeException(message));
			return er;
		}

		public TreeCallEE I(String message, Object... args) {
			TreeCallEE er = new TreeCallEE(this, new RuntimeException(X.f(message, args)));
			return er;
		}

		public TreeCallEE I(Throwable ex, String message, Object... args) {
			TreeCallEE er = new TreeCallEE(this, new RuntimeException(X.f(message, args), ex));
			return er;
		}
	}

	public TreeCallEE() {
		super(EE.NOSTATUS);
	}

	public TreeCallEE(EE error) {
		super(error);
	}

	public TreeCallEE(EE error, Throwable cause) {
		super(error, cause);
	}


	@Retention(RetentionPolicy.RUNTIME)
	public @interface TypeDbAno {
		boolean main() default false;
	}

}

