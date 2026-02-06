package mp.utl_odb.tree.ctxdb;

import com.google.gson.JsonObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mp.utl_ndb.NamedDbUrl;
import mp.utl_ndb.SqlDbUrl;
import mp.utl_odb.DBU;
import mpc.exception.RequiredRuntimeException;
import mpc.json.UGson;
import mpc.map.IGetterAs;
import mpe.db.Db;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.QDate;
import mpu.str.STR;
import mpv.sql_morpheus.SQLPlatform;

import java.nio.file.Path;
import java.util.Date;

@Deprecated
public class Ctx3Db extends NamedDbUrl implements ICtxDb<Ctx3Db.CtxModelCtr>, IGetterAs {

	private boolean createDbIfNotExist = true;

	public static ICtxDb of(Path path) {
		return new Ctx3Db(path);
	}

	public Ctx3Db withCreateDbIfNotExist(boolean... createDbIfNotExist) {
		this.createDbIfNotExist = ARG.isDefNotEqTrue(createDbIfNotExist);
		return this;
	}

	private @Getter UpdateMode updateMode = UpdateMode.PUT;

	public Ctx3Db withUpdateMode(UpdateMode _updateMode) {
		this.updateMode = _updateMode;
		return this;
	}

	private @Getter Integer[] autoCleanCfg_ctr_every_min_max_packet_first0End1 = null;

	public Ctx3Db withAutoCleanCfg(Integer[] autoCleanCfg_ctr_every_min_max_packet_first0End1) {
		IT.state(X.sizeOf(autoCleanCfg_ctr_every_min_max_packet_first0End1) > 0, "set autoCleanCfg_ctr_every_min_max_packet_firstEnd");
		this.autoCleanCfg_ctr_every_min_max_packet_first0End1 = autoCleanCfg_ctr_every_min_max_packet_first0End1;
		return this;
	}

	/**
	 * =======================CONSTRUCTOR
	 */

	public Ctx3Db(Class clas, String key) {
		this(clas.getName(), key);
	}

	public Ctx3Db(String parentDir, String key) {
		this(DEF_ROOT_PARENT, parentDir, key, false);
	}

	public Ctx3Db(String rootDir, String parentDir, String key) {
		super(rootDir, parentDir, key);
	}

	public Ctx3Db(String rootDir, String parentDir, String key, boolean isFileOrName) {
		super(rootDir, parentDir, key, isFileOrName);
	}

	public Ctx3Db(Path path) {
		super(path);
	}

	//
	//
	//

	@Override
	public void beforeSave(CtxModelCtr mdl) {
		mdl.incrementCounter();
		ICtxDb.super.beforeSave(mdl);
	}

	@Override
	public ICtxDb checkLazyCreateDb() {
		if (createDbIfNotExist) {
			DBU.checkOrCreateDb(this, getModelClass());
			createDbIfNotExist = false;
		}
		return this;
	}

	@Override
	public SqlDbUrl getDbUrl() {
		return this;
	}



	@Override
	public Class<CtxModelCtr> getModelClass() {
		return CtxModelCtr.class;
	}

	/**
	 * *************************************************************
	 * -------------------------- JSON -----------------------
	 * *************************************************************
	 */

	public JsonObject getValueAsJson(String nkey, boolean create, JsonObject... defRq) {
		CtxModelCtr model = getModelByKey(nkey);
		if (model != null) {
			return UGson.JO(model.getValue());
		} else if (create) {
			JsonObject jo = UGson.JO(UGson.EMPTY);
			model = putAppend(nkey, jo);
			return jo;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Row not found by key '%s'", nkey), defRq);
	}

	public CtxModelCtr putValueAsJson(String nkey, JsonObject json, String ext) {
		return put(nkey, json.toString(), ext);
	}

	@Override
	public <T> T getAs(String key, Class<T> asType, T... defRq) {
		return getValueAs(key, asType, defRq);
	}

	/**
	 * *************************************************************
	 * -------------------------- CtxModel -----------------------
	 * *************************************************************
	 */

	@DatabaseTable(tableName = CtxModelCtr.TN_DATAWT)
	public static class CtxModelCtr extends CtxModel<CtxModelCtr> {

		public static final String TN_DATAWT = "datawt";
		@Setter
		@DatabaseField
		private @Getter Long counter = 0L;

		public void incrementCounter() {
			if (counter == null) {
				counter = 0L;
			}
			counter++;
		}

		@Override
		public String toString() {
			return "#" + getId() + "/K=" + getKey() + "/C=" + getCounter() + "/T=" + (getTime() == null ? null : QDate.of(new Date())) + "/V=" + STR.substr(getValue(), 0, 300, getValue());
		}

	}

	@Override
	public String toString() {
		return UCtxDb.toString(this);
	}

}
