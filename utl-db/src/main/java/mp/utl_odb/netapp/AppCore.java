package mp.utl_odb.netapp;

import mpe.NT;
import mpu.core.ARG;
import mpc.fs.Ns;
import mpc.env.Env;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mp.utl_odb.mdl.AModel;
import mp.utl_odb.tree.UTree;

import java.nio.file.Path;

public class AppCore {

	public final Ns ns;

	public AppCore(String core_namespace) {
		this(Env.RL_PPJM_OR_PRL_DATA, core_namespace);
	}

	public AppCore(Path rootDir, String core_namespace) {
		ns = Ns.of(rootDir, core_namespace);
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

	//
	//
	public Path path() {
		return namespace().toPath();
	}

	public Path path(String path) {
		return namespace().path(path);
	}

	public Ns namespace() {
		return ns;
	}

	public Ns resolve(String ns_child) {
		return ns.getNamespaceOfChild(ns_child);
	}

	public Ns namespace(String ns_child) {
		return resolve(ns_child);
	}

	public UTree tree() {
		return UTree.tree(namespace(), Env.DEF);
	}

	public UTree tree(String dbName) {
		return UTree.tree(namespace(), dbName);
	}

	public UTree tree(String ns_child, String dbName) {
		Ns ns = namespace(ns_child);
		UTree tree = UTree.tree(ns, dbName);
		return tree;
	}

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
