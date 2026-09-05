package mp.utl_odb.tree.trees;

import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpu.func.FunctionV1;

import java.nio.file.Paths;
import java.util.List;


public class WalkerCtxDb<M extends ICtxDb.CtxModel> {

	private final String pathDb;
	private ICtxDb db;

	public WalkerCtxDb(String pathDb) {
		this.pathDb = pathDb;
		this.db = null;
	}

	public WalkerCtxDb(ICtxDb db) {
		this.db = db;
		this.pathDb = null;
	}

	public List<M> walkAndFind(FunctionV1<M> applier) {
		List<M> models = getICtxDb().getModels();
		models.forEach(m -> applier.apply(m));
		return models;
	}

	private ICtxDb getICtxDb() {
		return db != null ? db : (db = ICtxDb.of(Paths.get(pathDb)));
	}

}
