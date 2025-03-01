package mp.utl_odb.tree;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.SneakyThrows;
import mp.utl_ndb.NamedDbUrl;
import mp.utl_odb.DBU;
import mp.utl_odb.mdl.AModel;
import mp.utl_odb.query_core.OperDB;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.trees.UTreeAutoCleaner;
import mpc.env.Env;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpc.types.abstype.AbsType;
import mpc.types.ruprops.RuProps;
import mpe.core.OPR;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.QDate;
import mpu.pare.Pare3;
import mpu.str.STR;
import mpu.str.ToString;
import mpu.str.UST;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class CtxtDb extends NamedDbUrl {

	//	public static void main(String[] args) {
	//		CtxtDb adc = new CtxtDb("313");
	//		P.p(adc.getCtxTimeModelByKey("11233").getTimeAsDate());
	//		P.p(getModelNearOrLateAction(adc, false).getTimeAsDate().diffabs(QDate.now()) > TimeUnit.MINUTES.toMillis(27));
	//	}

	public final static String DEF_ROOT_PARENT = Env.RPA.resolve("db-ctxt").toString();
	public final static String DEF_PARENT = "def";

	private boolean createDbIfNotExist = true;

	public void createDbIfNotExist(boolean... createDbIfNotExist) {
		this.createDbIfNotExist = ARG.isDefNotEqTrue(createDbIfNotExist);
	}

	boolean updateExist = true;

	public CtxtDb _updateExist(boolean updateExist) {
		this.updateExist = updateExist;
		return this;
	}

	private Integer[] autoCleanCfg_ctr_every_min_max_packet_firstEnd = null;

	public CtxtDb setAutoCleanCfg(Integer[] autoCleanCfg_ctr_every_min_max_packet_firstEnd) {
		IT.state(X.sizeOf(autoCleanCfg_ctr_every_min_max_packet_firstEnd) > 0, "set autoCleanCfg_ctr_every_min_max_packet_firstEnd");
		this.autoCleanCfg_ctr_every_min_max_packet_firstEnd = autoCleanCfg_ctr_every_min_max_packet_firstEnd;
		return this;
	}

	public static CtxTimeModel getModelByKey(CtxtDb ctxDb, String keyValue, CtxTimeModel... defRq) {
		return getModelBy(ctxDb, CtxTimeModel.CN_KEY, keyValue, defRq);
	}

	public static CtxTimeModel getModelBy(CtxtDb ctxDb, String colname, Object colvalue, CtxTimeModel... defRq) {
		try {
			QP ep = colvalue == null ? QP.pNULL(colname) : QP.param(colname, colvalue);
			CtxTimeModel model = ctxDb.getModel(ep);
			if (model != null) {
				return model;
			}
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
		return ARG.toDefRq(defRq);
	}

	/**
	 * =======================CONSTRUCTOR
	 */
	public CtxtDb(Class clas) {
		this(clas.getName());
	}

	public CtxtDb(Class clas, String key) {
		this(clas.getName(), key);
	}

	public CtxtDb(String key) {
		this(DEF_PARENT, key);
	}

	public CtxtDb(String parentDir, String key) {
		this(DEF_ROOT_PARENT, parentDir, key, false);
	}

	public CtxtDb(String rootDir, String parentDir, String key) {
		super(rootDir, parentDir, key);
	}

	public CtxtDb(String rootDir, String parentDir, String key, boolean isFileOrName) {
		super(rootDir, parentDir, key, isFileOrName);
	}

	public CtxtDb(Path path) {
		super(path);
	}

	/**
	 * =======================
	 */

	protected void checkLazyCreateDb() {
		if (createDbIfNotExist) {
			checkOrCreateDb(this, CtxTimeModel.class);
			createDbIfNotExist = false;
		}
	}

	public boolean exist(CtxtlDb.Key by) {
		return getCtxTimeModel(by, false) != null;
	}

	public <T> T getAs(String nkey, Class<T> asType, T... defRq) {
		try {
			T t = UST.strTo(get(nkey), asType);
			return t;
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error UTree#getAs '%s'", asType), defRq);
		}
	}

	public RuProps getRuPropsOrPrepare(String usrUid) {
		String vl = get(usrUid, null);
		if (vl == null) {
			vl = "";
		}
		return RuProps.of(vl);
	}

	public CtxTimeModel putRuProps(String key, RuProps ruProps) {
		return put(key, ruProps.toStringClassic());
	}

	public CtxTimeModel putRuProps(String key, RuProps ruProps, Object ext) {
		return put(key, ruProps.toStringClassic(), ext);
	}

	public <M> M getModelFirstOrLastRq(Boolean firstOrLastOrRandom, M... defRq) {
		try {
			M model = (M) DBU.getModelFirstOrLastOrRandom(this, CtxTimeModel.class, firstOrLastOrRandom);
			return model != null ? model : ARG.toDefThrowMsg(() -> X.f("getModelFirstOrLastRq:%s", firstOrLastOrRandom), defRq);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public <M> M getModelAscDesc(Boolean firstOrLastOrRandom, QP... qps) {
		return (M) DBU.getModelFirstOrLastOrRandom(this, CtxTimeModel.class, firstOrLastOrRandom, qps);
	}

	//
	//
	//

	public String getOrNull(String nkey) {
		return get(nkey, null);
	}

	public String get(String nkey, String... defRq) {
		CtxTimeModel m = getCtxTimeModelByKey(nkey);
		if (m == null) {
			return ARG.toDefThrow(new RequiredRuntimeException("Key '%s' value is required", nkey), defRq);
		}
		return m.getValue();
	}

	public int remove(String nkey) {
		CtxTimeModel m = getCtxTimeModelByKey(nkey);
		if (m == null) {
			return 0;
		}
		return DBU.removeModel(this, m);
	}

	public Long getCount(Long... defRq) {
		return DBU.getCountModel(this, CtxTimeModel.class, defRq);
	}

	@SneakyThrows
	public Long getCountOf(QP[] qps, Long... defRq) {
		return DBU.getCountModel_(this, CtxTimeModel.class, qps, defRq);
	}

	/**
	 * *************************************************************
	 * -----------------------------  GET MODEL  --------------------------
	 * *************************************************************
	 */

	public CtxTimeModel getCtxTimeModelByKey(String nkey) {
		return false == isExistDb() ? null : DBU.getModelBy(this, CtxTimeModel.class, CtxFieldType.key.name(), nkey);
	}

	public CtxTimeModel getCtxTimeModelBy(String nkey) {
		return DBU.getModelBy(this, CtxTimeModel.class, CtxFieldType.key.name(), nkey);
//		return getCtxTimeModel(CtxFieldType.key, nkey, false);
	}

	public CtxTimeModel getCtxTimeModelByValue(String nkey) {
		return DBU.getModelBy(this, CtxTimeModel.class, CtxFieldType.value.name(), nkey);
//		return getCtxTimeModel(CtxFieldType.key, nkey, false);
	}

	public CtxTimeModel getCtxTimeModel(CtxtlDb.Key key, boolean firstLastRandom) {
		return getCtxTimeModel(key.type(), key.val, firstLastRandom);
	}

	public CtxTimeModel getCtxTimeModel(CtxFieldType fieldType, Object value, Boolean firstLastRandom) {
		if (false == isExistDb()) {
			return null;
		}
//		CtxTimeModel m = DBU.getModelBy(this, CtxTimeModel.class, fieldType.name(), value);
		CtxTimeModel m = DBU.getModelFirstOrLastOrRandom(this, CtxTimeModel.class, firstLastRandom, QP.pEQ(fieldType.name(), value));
		return m;
	}

	/**
	 * *************************************************************
	 * -----------------------------  CONTAINS --------------------------
	 * *************************************************************
	 */

	public boolean contains(String nkey, String value) {
		if (false == isExistDb()) {
			return false;
		}
		QP ep1 = QP.param(CN.KEY, nkey);
		QP ep2 = QP.param(CN.VALUE, value);
		return contains(ARR.of(ep1, ep2));
	}

	public boolean contains(QP[] qps) {
		if (false == isExistDb()) {
			return false;
		}
		return getCtxTimeModel(qps) != null;
	}

//	public static Pare<Boolean, String> containsKey(UTree tree, String key) {
//		String prev = tree.getOrNull(key);
//		boolean exist = prev == null && tree.contains(key);
//		return Pare.of(exist, prev);
//	}

	public boolean contains(String nkey) {
		return getCtxTimeModelByKey(nkey) != null;
	}

	/**
	 * *************************************************************
	 * -----------------------------  --------------------------
	 * *************************************************************
	 */

	public CtxTimeModel getCtxTimeModel(QP[] qps) {
		return DBU.getModel(this, CtxTimeModel.class, qps);
	}

	public List<Integer> keysInt(QP... eps) {
		List<String> keys = keys(eps);
		return keys.stream().map(Integer::parseInt).collect(Collectors.toList());
	}

	public List<String> keys(QP... eps) {
		if (false == isExistDb()) {
			return ARR.asAL();
		}
		List<CtxTimeModel> ms = DBU.getModels(this, CtxTimeModel.class, eps);
		return ms.stream().map(e -> e.getKey()).collect(Collectors.toList());
	}

	public List<String> values(QP... eps) {
		List<CtxTimeModel> ms = DBU.getModels(this, CtxTimeModel.class, eps);
		return ms.stream().map(e -> e.getValue()).collect(Collectors.toList());
	}

	public boolean isEmptyDb() {
		return getModels().isEmpty();
	}

	/**
	 * *************************************************************
	 * ---------------------------- GET --------------------------
	 * *************************************************************
	 */

	public List<CtxTimeModel> getModels(QP... qps) {
		if (!isExistDb()) {
			return Collections.EMPTY_LIST;
		}
		return DBU.getModels(this, CtxTimeModel.class, qps);
	}

	public CtxTimeModel getModelAfterBeforeEq(Boolean afterOrBeforeOrEq, String colname, Object value, CtxTimeModel... defRq) {
		return DBU.getModelAfterBeforeEq(this, CtxTimeModel.class, afterOrBeforeOrEq, colname, value, defRq);
	}

	public CtxTimeModel getModel(QP... eps) {
		if (false == isExistDb()) {
			return null;
		}
		return DBU.getModel(this, CtxTimeModel.class, eps);
	}

	public List<CtxTimeModel> getModelsLikeKey(String keyPart) {
		return getModels(QP.param(CtxTimeModel.CN_KEY, QP.likeWrapVal(keyPart), OPR.LIKE));
	}

	public List<CtxTimeModel> getModelsLikeValue(String keyPart) {
		return getModels(QP.param(CtxTimeModel.CN_VALUE, QP.likeWrapVal(keyPart), OPR.LIKE));
	}

	public List<CtxTimeModel> getModelsLikeExt(String keyPart) {
		return getModels(QP.param(CtxTimeModel.CN_EXT, QP.likeWrapVal(keyPart), OPR.LIKE));
	}

	/**
	 * *************************************************************
	 * ---------------------------- PUT --------------------------
	 * *************************************************************
	 */

	public CtxTimeModel put(String key) {
		return put(CtxtlDb.Key.of(key), null, null);
	}

	public CtxTimeModel put(String key, Object value) {
		return put(CtxtlDb.Key.of(key), CtxtlDb.Key.Val.of(ToString.strOr(value, null)), null);
	}

	public CtxTimeModel put(String key, Object value, Object ext) {
		return put(CtxtlDb.Key.of(key), CtxtlDb.Key.Val.of(ToString.strOr(value, null)), CtxtlDb.Key.Ext.of(ToString.strOr(ext, null)));
	}

	public CtxTimeModel put(CtxtlDb.Key key, CtxtlDb.Key.Val val, CtxtlDb.Key.Ext ext) {
		return put(key, val, ext, null);
	}

	public CtxTimeModel add(String key, String value, String ext) {
		_updateExist(false);
		return put(CtxtlDb.Key.of(key), CtxtlDb.Key.Val.of(ToString.strOr(value, null)), CtxtlDb.Key.Ext.of(ToString.strOr(ext, null)), null);
	}

	public CtxTimeModel put(CtxtlDb.Key key, CtxtlDb.Key.Val val, CtxtlDb.Key.Ext ext, CtxtlDb.Key.Time time) {

		checkLazyCreateDb();

		CtxTimeModel mdl = updateExist ? getCtxTimeModelByKey(key.val) : null;
		if (mdl == null) {
			mdl = new CtxTimeModel();
			mdl.setKey(key.val);
		}

		if (val != null) {
			mdl.setValue(val.val);
		}
		if (ext != null) {
			mdl.setExt(ext.val);
		}
		if (time != null) {
			mdl.setTime(UST.LONG(time.val));
		} else {
			updateTime(mdl);
		}

		updateCounter(mdl);

		saveModelAsCreateOrUpdate(mdl);

		afterPut(mdl);

		if (autoCleanCfg_ctr_every_min_max_packet_firstEnd != null) {
			String s = UTreeAutoCleaner.checkAutoClean(this, autoCleanCfg_ctr_every_min_max_packet_firstEnd);
//				X.p("rslt autocleean=" + s);
			if (L.isDebugEnabled()) {
				L.debug(s);
			}
		}

		return mdl;
	}


	public <M extends AModel> void saveModelAsUpdate(M model) {
		DBU.updateModel(this, model, OperDB.update);
	}

	public <M extends AModel> void saveModelAsCreateOrUpdate(M model) {
		DBU.updateModel(this, model, OperDB.createOrUpdate);
	}

	protected void afterPut(CtxTimeModel m) {
		if (L.isDebugEnabled()) {
			String shorKey = STR.toStringSE(m.getKey(), 20, m.getKey());
			L.debug(SYMJ.FILE_DB + "Put ctx-model by key '{}' in tree 'file://{}'\n{}", shorKey, getDbFilePath(), m);
		} else if (L.isInfoEnabled()) {
			String shorKey = STR.toStringSE(m.getKey(), 20, m.getKey());
			L.info(SYMJ.FILE_DB + "Put ctx-model by key '{}' in tree 'file://{}'", shorKey, getDbFilePath());
		}
	}

	public void updateTime(CtxTimeModel m) {
		m.setTime(System.currentTimeMillis());
	}

	protected void updateTime(CtxTimeModel m, long timeMs) {
		m.setTime(timeMs);
	}

	protected void updateCounter(CtxTimeModel m) {
		m.incrementCounter();
	}

	/**
	 * =======================CLEAR
	 */
	public void clear(String nkey) {
		CtxTimeModel m = getCtxTimeModelByKey(nkey);
		if (m != null) {
			DBU.updateModel(this, m, OperDB.REMOVE);
		}
	}

	public void clear() {
		DBU.clearTableRq(this, CtxTimeModel.class);
	}

	public enum CtxFieldType {
		key, value, ext, time;

		public static CtxFieldType typeOf(CtxtlDb.Key type) {
			Class<? extends CtxtlDb.Key> aClass = type.getClass();
			return CtxtlDb.Key.Val.class.isAssignableFrom(aClass) ? value : CtxtlDb.Key.Ext.class.isAssignableFrom(aClass) ? ext : CtxtlDb.Key.Time.class.isAssignableFrom(aClass) ? time : CtxFieldType.key;
		}

		public Object get(CtxTimeModel model) {
			switch (this) {
				case key:
					return model.getKey();
				case value:
					return model.getValue();
				case ext:
					return model.getExt();
				case time:
					return model.getTime();
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	@DatabaseTable(tableName = "datawt")
	public static class CtxTimeModel extends AModel<CtxTimeModel> {//implements CacheTimeUpdater.IUp

		private static final long serialVersionUID = 1L;
		public static final String CN_KEY = "key";
		public static final String CN_VALUE = "value";
		public static final String CN_EXT = "ext";
		public static final String CN_TIME = "time";

		@DatabaseField(generatedId = true)
		private Long id;

		@DatabaseField
		private String key;

		@DatabaseField
		private String value;

		@DatabaseField
		private String ext;

		@DatabaseField
		private Long counter = 0L;

		@DatabaseField
		private Long time;

		public CtxTimeModel() {
		}

		@Override
		public String toString() {
			return getKey() + "::" + getCounter() + "::" + (time == null ? null : QDate.of(new Date(time))) + "::" + STR.substr(value, 0, 300, value);
		}

		public QDate getTimeAsQDate() {
			return QDate.of(new Date(getTime()));
		}


		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public <T> T getValueAs(Class<T> asType, T... defRq) {
			return UST.strTo(getValue(), asType, defRq);
		}

		public String getValue() {
			return value;
		}

		public CtxTimeModel setValue(String value) {
			this.value = value;
			return this;
		}

		public String getKey() {
			return key;
		}

		public CtxTimeModel setKey(String key) {
			this.key = key;
			return this;
		}

		public Long getTime() {
//			return time != null ? time : ARG.toDefThrowMsg(() -> X.f("model without time"), defRq);
			return time;
		}

		public QDate getTimeAsDate() {
			return QDate.of(new Date(getTime()));
		}

		public CtxTimeModel setTime(Long ms) {
			this.time = ms;
			return this;
		}

		public <T> T getExtAs(Class<T> asType, T... defRq) {
			return UST.strTo(getExt(), asType, defRq);
		}

		public String getExt() {
//			return ext != null ? ext : ARG.toDefThrowMsg(() -> X.f("model without ext"), defRq);
			return ext;
		}

		public CtxTimeModel setExt(String ext) {
			this.ext = ext;
			return this;
		}


		public Long getCounter() {
			return counter;
		}

		public void setCounter(Long counter) {
			this.counter = counter;
		}

		public void incrementCounter() {
			if (counter == null) {
				counter = 0L;
			}
			counter++;
		}


		public Pare3<String, String, String> toPare3() {
			return Pare3.of(getKey(), getValue(), getExt());
		}

		public List<AbsType> toAbsRow5() {
			AbsType<QDate> time1 = AbsType.of("time", QDate.of(getTime(), null), QDate.class);
			AbsType<String> ext1 = AbsType.of("ext", getExt(), String.class);
			AbsType<String> val = AbsType.of("val", getValue(), String.class);
			AbsType<String> key1 = AbsType.of("key", getKey(), String.class);
			return ARR.as(AbsType.of("id", getId(), Long.class), key1, val, ext1, time1);
		}

	}
}
