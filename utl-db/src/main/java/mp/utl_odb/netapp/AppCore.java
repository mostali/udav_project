package mp.utl_odb.netapp;

import lombok.SneakyThrows;
import mp.utl_odb.tree.ctxdb.Ctx5Db;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mp.utl_odb.tree.trees.lifecache.UTreeEveryLife;
import mp.utl_odb.tree.trees.lifecache.UTreeShortLife;
import mpc.env.APP;
import mpe.NT;
import mpe.app.AppCore0;
import mpe.cmsg.ns.NodeID;
import mpe.db.Db;
import mpu.core.ARG;
import mpc.env.Env;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mp.utl_odb.mdl.AModel;
import mp.utl_odb.tree.UTree;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

public class AppCore extends AppCore0 {

	public AppCore(String core_namespace) {
		super(Env.RPA_ROOT(), core_namespace);
	}

	public AppCore(Path rootDir, String core_namespace) {
		super(rootDir, core_namespace);
	}

	public static AppCore of() {
		return of(Env.getAppNameOrDef());
	}

	public static AppCore of(String app_namespace) {
		return new AppCore(app_namespace);
	}

	public static AppCore of(NT app) {
		return new AppCore(app.nameLC());
	}

	public static @NotNull ICtxDb __GNC() {
		return UTree.tree(APP.TREE_GNC());
	}

	public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRST0END1 = new Integer[]{0, 500, -1, 10_000, 1000, 0};

	@SneakyThrows
	public static List __VERSIONED_LS(String nodeID) {
		String sql = "select id from datawt where key='" + NodeID.toCanonicId(nodeID) + "' ORDER by time DESC limit 10";
		List<?> query = __VERSIONED().toDb().query(sql, Db.QMapResult.SEQ);
		return query;
	}

	public static @NotNull ICtxDb __VERSIONED() {
		return UTree.tree(APP.TREE_VERSIONED()).withUpdateMode(ICtxDb.UpdateMode.ADD).withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRST0END1);
	}

	public static ICtxDb.CtxModel __VERSIONED_GET(Integer id) {
		return __VERSIONED().getModelById(id);
	}

	//
	//
	//

	public UTree tree() {
		return UTree.tree(namespace(), Env.DEF);
	}

	public UTree tree(String dbName) {
		return UTree.tree(namespace(), dbName);
	}

	//
	//
	public Ctx5Db tree5(String dbName) {
		return Ctx5Db.of(namespace(), dbName);
	}

	public Ctx5Db tree5h(String dbName) {
		Ctx5Db ctx5Db = Ctx5Db.of(namespace(), dbName);
		ctx5Db.setHistory(true);
		return ctx5Db;
	}

	public Ctx5Db tree5(String ns_child, String dbName) {
		return Ctx5Db.of(namespace(ns_child), dbName);
	}

	//
	//
	public Ctx10Db tree10(String dbName) {
		return Ctx10Db.of(namespace(), dbName);
	}

	public Ctx10Db tree10(String ns_child, String dbName) {
		return Ctx10Db.of(namespace(ns_child), dbName);
	}
	//
	//

	public UTreeShortLife treeShortLife(String dbName) {
		return UTreeShortLife.tree(namespace(), dbName);
	}

	public UTreeShortLife treeShortLife(String ns_child, String dbName) {
		return UTreeShortLife.tree(namespace(ns_child), dbName);
	}

	//
	//

	public UTreeEveryLife treeEveryLife(String dbName) {
		return UTreeEveryLife.tree(namespace(), dbName);
	}

	public UTreeEveryLife treeEveryLife(String ns_child, String dbName) {
		return UTreeEveryLife.tree(namespace(ns_child), dbName);
	}

	//
	//

	public UTree tree(String ns_child, String dbName) {
		return UTree.tree(namespace(ns_child), dbName);
	}

	//
	//

	public <M extends AModel> void regTypeDbEE(Class<? extends AModel> existModelClass, Class<? extends AModel> modelClass, boolean... createDbIfNotExist) {
		TypeDbEE.regDb(existModelClass, modelClass, ARG.isDefEqTrue(createDbIfNotExist));
	}

	public <M extends AModel> void regTypeDbEE(Class<M> modelClass, String dbName, boolean... createDbIfNotExist) {
		TypeDbEE.regDb(modelClass, namespace(), dbName, ARG.isDefEqTrue(createDbIfNotExist));
	}

	public <M extends AModel> void regTypeDbEE(TypeDb<M> typeDb) {
		TypeDbEE.regDb(typeDb);
	}

	public <M extends AModel> TypeDb<M> getTypeDb(Class<M> modelClass, String dbName, boolean... createDbIfNotExist) {
		return TypeDb.of(modelClass, namespace(), dbName, createDbIfNotExist);
	}

	public <M extends AModel> TypeDb<M> getTypeDb(Class<M> modelClass, String child_ns, String dbName, boolean... createDbIfNotExist) {
		return TypeDb.of(modelClass, namespace(child_ns), dbName, createDbIfNotExist);
	}

	@Deprecated
	public <M extends AModel> TypeDb<M> getTypeDb(Class<M> modelClass, Path pathDb, boolean... createDbIfNotExist) {
		return TypeDb.of(modelClass, pathDb, createDbIfNotExist);
	}
}
