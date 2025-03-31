package mp.utl_odb.tree.ctxdb;

import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import mp.utl_ndb.Db;
import mp.utl_ndb.SqlDbUrl;
import mp.utl_odb.DBU;
import mp.utl_odb.mdl.AModel;
import mp.utl_odb.query_core.QP;
import mpc.env.Env;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.log.L;
import mpc.str.sym.SYMJ;
import mpc.types.abstype.AbsType;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.QDate;
import mpu.pare.Pare3;
import mpu.str.UST;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public interface ICtxDb<M extends ICtxDb.CtxModel> {

	String DEF_NS = "db-ctxt";
	String DEF_ROOT_PARENT = Env.RPA.resolve(DEF_NS).toString();
	String CN_KEY = "key";
	String CN_VALUE = "value";
	String CN_EXT = "ext";
	String CN_TIME = "time";

	String TN10 = "d10";
	String TN10H = "d10h";
	String TN5 = "d5";
	String TN5H = "d5h";

	static String getTablename(Path path, String... defRq) {
		List<String> allTableNames = Db.of(path).getAllTableNames();
		String table = null;
		if (allTableNames.contains(TN10)) {
			table = TN10;
		} else if (allTableNames.contains(TN5)) {
			table = TN5;
		} else if (allTableNames.contains(Ctx3Db.CtxModelCtr.TN_DATAWT)) {
			table = Ctx3Db.CtxModelCtr.TN_DATAWT;
		}
		if (table != null) {
			return table;
		}
		return ARG.toDefThrowMsg(() -> X.f("CtxDb tablename not found from {}", UF.ln(path)), defRq);
	}

//	static Pare<ICtxDb, String> ofWithTablename(Path path, Pare<ICtxDb, String>... defRq) {
//		List<String> allTableNames = Db.of(path).getAllTableNames();
//		String table = null;
//		if (allTableNames.contains(Ctx10Db.CtxModel10.TN_D_10)) {
//			table = Ctx10Db.CtxModel10.TN_D_10;
//		} else if (allTableNames.contains(Ctx10Db.CtxModel10.TN_D_5)) {
//			table = Ctx10Db.CtxModel10.TN_D_5;
//		} else if (allTableNames.contains(Ctx3Db.CtxModel3.TN_DATAWT)) {
//			table = Ctx3Db.CtxModel3.TN_DATAWT;
//		}
//		if (table != null) {
//			return Pare.of(ICtxDb.of(path, table), Ctx10Db.CtxModel10.TN_D_10);
//		}
//		return ARG.toDefThrowMsg(() -> X.f("CtxDb not found from {}", UF.ln(path)), defRq);
//	}

	static ICtxDb of(Path path, String tablename) {
		switch (tablename) {
			case TN10:
				return Ctx10Db.of(path);
			case TN5:
				return Ctx5Db.of(path);
			case Ctx3Db.CtxModelCtr.TN_DATAWT:
				return Ctx3Db.of(path);
			default:
				throw new WhatIsTypeException(tablename);
		}
	}

	static ICtxDb of(Path path, ICtxDb... defRq) {
		String tablename = getTablename(path, null);
		return tablename != null ? of(path, tablename) : ARG.toDefThrowMsg(() -> X.f("CtxDb not found from {}", UF.ln(path)), defRq);
	}

	Class<M> getModelClass();

	SqlDbUrl getDbUrl();

	UpdateMode getUpdateMode();

	void checkLazyCreateDb();

	Integer[] getAutoCleanCfg_ctr_every_min_max_packet_first0End1();

	default void saveModelAsUpdate(M model) {
		DBU.updateModel(getDbUrl(), model);
	}

	default void saveModelAsCreateOrUpdate(M model) {
		DBU.createOrUpdateModel(getDbUrl(), model);
	}

	default void saveModelAsCreate(M model) {
		if (L.isInfoEnabled()) {
			L.info("Update model - {} in db - {}", model, toStringLog());
		}
		DBU.createModel(getDbUrl(), model);
	}
	//
	//

	default Path getDbFilePath() {
		return getDbUrl().getDbFilePath();
	}

	default boolean isDbNotExistOrEmpty() {
		return !isExistDb() || isEmptyDb();
	}

	/**
	 * *************************************************************
	 * -----------------------------  PUT  --------------------------
	 * *************************************************************
	 */

	default M put(String key) {
		return put(CKey.of(key), null);
	}

	default M put(String key, Object value) {
		return put(CKey.of(key), null, CKey.Val.of(X.toString(value, null)));
	}

	default M put(String key, CharSequence value, CharSequence ext, Long... ms) {
		CKey.Time time = ARG.isDefNNF(ms) ? CKey.Time.of(ms[1]) : null;
//		return put(Key.of(key), Key.Val.of(X.toString(value, null)), Key.Ext.of(X.toString(ext, null)), time);
		CKey.Val valKey = CKey.Val.of(X.toString(value, null));
		CKey.Ext extKey = CKey.Ext.of(X.toString(ext, null));
		return put(CKey.of(key), time, valKey, extKey);
	}

	default M put(String key, CKey... values) {
		return put(CKey.of(key), null, values);
	}

	default M put(CKey key, CKey.Time time, CKey... values) {
		checkLazyCreateDb();
		M mdl = (M) UCtxDb.prepareCtxModel_ExistOrNew((ICtxDb) this, key, values);
		if (mdl.getTime() != null) {
			IT.state(time == null, "Double time - use only one time field");
		}
		if (time != null) {
			CtxModel.updateTime(mdl, UST.LONG(time.val));
		} else {
			CtxModel.updateTime(mdl);
		}
		saveModelAsCreateOrUpdate(mdl);
		UCtxDb.afterPut(this, mdl);
		return mdl;
	}
//	default M put(Key key, Key.Val val, Key.Ext ext, Key.Time time, Key... opts) {
//		checkLazyCreateDb();
//		M mdl = (M) UCtxDb.prepareCtxModel_ExistOrNew((ICtxDb) this, key, ARR.addItems(ARR.of(val, ext), opts));
//		if (time != null) {
//			CtxModel0.updateTime(mdl, UST.LONG(time.val));
//		} else {
//			CtxModel0.updateTime(mdl);
//		}
//		UCtxDb.saveModelAsCreateOrUpdate(this, mdl);
//		UCtxDb.afterPut(this, mdl);
//		return mdl;
//	}

	default void beforeSave(M mdl) {
		if (getUpdateMode() == UpdateMode.ALWAYSLAST) {
			UCtxDb.moveModelToLastRowIfExists(this, mdl);
		}
	}

	/**
	 * *************************************************************
	 * -----------------------------  SIZE  --------------------------
	 * *************************************************************
	 */

	default Long getCount(Long... defRq) {
		return DBU.getCountModel(getDbUrl(), getModelClass(), defRq);
	}

	@SneakyThrows
	default Long getCountOf(QP[] qps, Long... defRq) {
		return DBU.getCountModel_(getDbUrl(), getModelClass(), qps, defRq);
	}

	default boolean isExistModelBy(CKey by) {
		return getModelBy(by, false) != null;
	}

	default boolean isExistDb() {
		return UFS.isFileWithContent(getDbFilePath());
	}

	default boolean isEmptyDb() {
//		return getModels().isEmpty();
		Long count = getCount(null);
		return count == null || count == 0L;
	}

	/**
	 * *************************************************************
	 * -----------------------------  CONTAINS --------------------------
	 * *************************************************************
	 */

	default boolean containsBy(CKey... keys) {
		return isExistDb() && containsBy(Arrays.stream(keys).map(k -> QP.param(k.colName(), k.colVal())).toArray(QP[]::new));
	}

	default boolean containsBy(QP[] qps) {
		return DBU.existModel(getDbUrl(), getModelClass(), qps);
	}


	/**
	 * *************************************************************
	 * -----------------------------  GET MODEL  --------------------------
	 * *************************************************************
	 */

	default M getModelBy(CKey byKey) {
		return (M) UCtxDb.getCtxModelBy(this, byKey);
	}

	default M getModelBy(QP... qps) {
		return DBU.getModel(getDbUrl(), getModelClass(), qps);
	}

	default <M> M getModelFirstOrLast(Boolean firstOrLastOrRandom, M... defRq) {
		try {
			M model = (M) DBU.getModelFirstOrLastOrRandom(getDbUrl(), getModelClass(), firstOrLastOrRandom);
			return model != null ? model : ARG.toDefThrowMsg(() -> X.f("getModelFirstOrLastRq:%s", firstOrLastOrRandom), defRq);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	default M getModelFirstOrLastBy(Boolean firstOrLastOrRandom, QP... qps) {
		return DBU.getModelFirstOrLastOrRandom(getDbUrl(), getModelClass(), firstOrLastOrRandom, qps);
	}

	default M getModelByKey(String byKey) {
		return getModelBy(CKey.of(byKey));
	}

	default M getModelBy(CKey key, Boolean firstLastRandom) {
		return (M) UCtxDb.getCtxModelBy(this, key.colName(), key.val, firstLastRandom);
	}

	default M getModelAfterBeforeEq(CKey byKey, Boolean afterOrBeforeOrEq, M... defRq) {
		return DBU.getModelAfterBeforeEq(getDbUrl(), getModelClass(), afterOrBeforeOrEq, byKey.colName(), byKey.val, defRq);
	}

	//
	// MODELS

	default List<M> getModelsLike(CKey key) {
		return getModels(key.pLIKE());
	}

	default List<M> getModels(QP... qps) {
		return !isExistDb() ? Collections.EMPTY_LIST : DBU.getModels(getDbUrl(), getModelClass(), qps);
	}

	//
	// MODELS EXT

	default List<List<AbsType>> getModelsAsMapRow(String tablename, QP... qps) {
		return getModels(qps).stream().map(m -> CtxModel.toAbsRow(m, tablename)).collect(Collectors.toList());
	}

	default List<Pare3<String, String, String>> getModelsAsPare3(QP... qps) {
		return getModels(qps).stream().map(m -> CtxModel.toPare3(m)).collect(Collectors.toList());
	}

	//
	//
	//

	default <T> T getValueAs(String nkey, Class<T> asType, T... defRq) {
		try {
			T t = UST.strTo(getValue(nkey), asType);
			return t;
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error UTree#getAs '%s'", asType), defRq);
		}
	}

	default String getValueOrNull(String nkey) {
		return getValue(nkey, null);
	}

	default String getValue(String nkey, String... defRq) {
		M m = getModelByKey(nkey);
		return m != null ? m.getValue() : ARG.toDefThrow(new RequiredRuntimeException("Key '%s' value is required", nkey), defRq);
	}


	/**
	 * *************************************************************
	 * ---------------------------- CLEAR --------------------------
	 * *************************************************************
	 */

	@SneakyThrows
	default int removeById(Long id) {
		return DBU.removeModelBy(getDbUrl(), getModelClass(), new QP[]{QP.pID(id)});
	}

	default int removeByKeyIfExist(String nkey) {
		M m = getModelByKey(nkey);
		return m == null ? 0 : DBU.removeModelById(getDbUrl(), m);
	}

	default void clear() {//boolean... required
//		try {
		DBU.clearTableRq(getDbUrl(), getModelClass());
//		} catch (Exception ex) {
//			if (ARG.isDefEqTrue(required)) {
//				X.throwException(ex);
//			}
////		}
	}

	@SneakyThrows
	default void removeDb() {
		getDbUrl().removeDb();
	}

	default Db toDb() {
		return Db.of(getDbFilePath());
	}

	default List<List<Object>> getAllTableRows(boolean withHeadRow, String isColValIsNullThat) {
		return toDb().getAllTableRows(DBU.getTableName(getModelClass()), withHeadRow, isColValIsNullThat);
	}

	@SneakyThrows
	default ICtxDb putAllTableRows(List<List<Object>> rows, boolean withHeadRow, boolean... rmmDbBeforeWrite) {
		if (withHeadRow) {
			rows = new ArrayList<>(rows);
			List<Object> headRow = ARR.cutHeadRow(rows);
			if (L.isDebugEnabled()) {
				L.debug("Remove head rows:" + headRow);
			}
		}
		Db db = toDb().writeToDb(DBU.getTableName(getModelClass()), rows, rmmDbBeforeWrite);
		return this;
	}

	default String toStringLog() {
		return SYMJ.FILE_DB + "" + UF.ln(getDbFilePath());
	}

	@SneakyThrows
	default String getMaxValueString(String colName) {
		return DBU.getMaxValueString(getDbUrl(), getModelClass(), colName);
	}


	enum UpdateMode {
		PUT, ADD, ALWAYSLAST
	}

	class CtxModel<M extends AModel> extends AModel<M> {

		private static final long serialVersionUID = 1L;

		@Setter
		@DatabaseField(generatedId = true)
		private @Getter Long id;

		@Setter
		@DatabaseField
		private @Getter String key;

		@Setter
		@DatabaseField
		private @Getter String value;

		@Setter
		@DatabaseField
		private @Getter String ext;

		public <T> T getExtAs(Class<T> asType, T... defRq) {
			return UST.strTo(getExt(), asType, defRq);
		}

		@Setter
		@DatabaseField
		private @Getter Long time;

		public CtxModel() {
		}

		public static void updateTime(CtxModel m) {
			m.setTime(System.currentTimeMillis());
		}

		public static void updateTime(CtxModel m, long timeMs) {
			m.setTime(timeMs);
		}

		public <T> T getValueAs(Class<T> asType, T... defRq) {
			return UST.strTo(getValue(), asType, defRq);
		}

		public QDate getTimeAsQDate() {
			return QDate.of(new Date(getTime()));
		}


		public Pare3<String, String, String> toPare3() {
			return toPare3((CtxModel) this);
		}

		public static Pare3<String, String, String> toPare3(CtxModel model) {
			return Pare3.of(model.getKey(), model.getValue(), model.getExt());
		}

		public static List<AbsType> toAbsRow(CtxModel model, String... tablename) {
			String tablename0 = ARG.toDefOrNull(tablename);

			AbsType<QDate> time1 = AbsType.of("time", QDate.of(model.getTime(), null), QDate.class);
			AbsType<String> ext1 = AbsType.of("ext", model.getExt(), String.class);
			AbsType<String> val = AbsType.of("val", model.getValue(), String.class);
			AbsType<String> key1 = AbsType.of("key", model.getKey(), String.class);

			List<AbsType> baseCols = ARR.asAL(AbsType.of("id", model.getId(), Long.class), key1, val, ext1, time1);
			if (tablename0 == null) {
				return baseCols;
			}
			switch (tablename0) {
				case Ctx3Db.CtxModelCtr.TN_DATAWT:
//					baseCols5.add(AbsType.of("ctr", (Integer)model.getFieldValueObj("ctr"), String.class));
					return baseCols;
				case TN5:
					ARR.as("o1", "o2", "o3", "o4", "o5").stream().map(cn -> AbsType.of(cn, (String) model.getFieldValueObj(cn), String.class)).forEach(baseCols::add);
					return baseCols;
				case TN10:
					ARR.as("o1", "o2", "o3", "o4", "o5", "o6", "o7", "o8", "o9", "o10").stream().map(cn -> AbsType.of(cn, (String) model.getFieldValueObj(cn), String.class)).forEach(baseCols::add);
					return baseCols;
				default:
					throw new WhatIsTypeException(tablename0);
			}
		}


		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
//		@Override
//		public String toString() {
//			return "#" + getId() + "/K=" + getKey() + "/T=" + (getTime() == null ? null : QDate.of(new Date())) + "/V=" + STR.substr(getValue(), 0, 300, getValue());
//		}

		public void setColValue(CKey val) {
			String simpleName = val.getClass().getSimpleName().toLowerCase();
			switch (simpleName) {
				case "val":
					setValue(val.val);
					break;
				case "time":
					setTime(val.asLong());
					break;
				default:
					setObjectField(simpleName, val.colVal());
					break;

			}
		}

	}
}
