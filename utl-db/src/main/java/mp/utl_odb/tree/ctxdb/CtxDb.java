package mp.utl_odb.tree.ctxdb;

import lombok.Getter;
import mp.utl_ndb.NamedDbUrl;
import mp.utl_odb.DBU;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;

import java.nio.file.Path;


public abstract class CtxDb<M extends ICtxDb.CtxModel> implements ICtxDb<M> {

	//
	//

	private final @Getter NamedDbUrl dbUrl;


	private boolean createDbIfNotExist = true;

	public CtxDb withCreateDbIfNotExist(boolean... createDbIfNotExist) {
		this.createDbIfNotExist = ARG.isDefNotEqFalse(createDbIfNotExist);
		return this;
	}

	public @Getter UpdateMode updateMode = UpdateMode.PUT;

	public CtxDb withUpdateMode(UpdateMode _updateMode) {
		this.updateMode = _updateMode;
		return this;
	}

	private @Getter Integer[] autoCleanCfg_ctr_every_min_max_packet_first0End1;

	public CtxDb withAutoCleanCfg(Integer[] autoCleanCfg_ctr_every_min_max_packet_first0End1) {
		IT.state(X.sizeOf(autoCleanCfg_ctr_every_min_max_packet_first0End1) > 0, "set autoCleanCfg_ctr_every_min_max_packet_firstEnd");
		this.autoCleanCfg_ctr_every_min_max_packet_first0End1 = autoCleanCfg_ctr_every_min_max_packet_first0End1;
		return this;
	}

	public CtxDb checkLazyCreateDb() {
		if (createDbIfNotExist) {
			DBU.checkOrCreateDb(getDbUrl(), getModelClass());
			createDbIfNotExist = false;
		}
		return this;
	}

	//
 	//

	public CtxDb(Class clas, String key) {
		this(clas.getSimpleName(), key);
	}

	public CtxDb(String parentDir, String key) {
		super();
		this.dbUrl = new NamedDbUrl(ICtxDb.DEF_ROOT_PARENT, parentDir, key, false);
	}

	public CtxDb(Path path) {
		super();
		this.dbUrl = new NamedDbUrl(path);
	}

	@Override
	public String toString() {
		return UCtxDb.toString(this);
	}

}
