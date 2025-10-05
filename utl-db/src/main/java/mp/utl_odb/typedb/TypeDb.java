package mp.utl_odb.typedb;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import lombok.SneakyThrows;
import mpe.db.Db;
import mp.utl_ndb.NamedDbUrl;
import mp.utl_odb.*;
import mp.utl_odb.query_core.OperDB;
import mp.utl_odb.query_core.QP;
import mpu.X;
import mpu.core.ARG;
import mpu.IT;
import mpc.str.sym.SEP;
import mpc.types.abstype.AbsType;
import mpc.fs.Ns;
import mpe.str.CN;
import mpc.str.sym.SYM;
import mpc.str.sym.SYMJ;
import mpu.core.ARRi;
import mpu.str.Rt;
import mp.utl_odb.mdl.AModel;
import mp.utl_odb.mdl.ext_um2.UniModel;
import mpu.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpc.rfl.RFL;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


public class TypeDb<M extends AModel> {

	public static final Logger L = LoggerFactory.getLogger(TypeDb.class);

	private final NamedDbUrl namedDbUrl;
	private final Ns ns;
	private final Class<M> classModel;

	public static void checkNotExistModel(TypeDb db, QP[] params) {
		if (db.existModelQk(params)) {
			List<QP> eqParams = Arrays.asList(params);
			Map context = eqParams.stream().collect(Collectors.toMap(e -> e.columnName, v -> v.columnValue));
			throw TypeDbEE.EE.MODEL_EXISTS.I("Model already exist, by keys %s", context);
		}
	}

	public static <M extends UniModel> boolean removeModelQk(TypeDb db, AModel model) {
		if (model == null) {
			if (L.isInfoEnabled()) {
				L.info("Error remove. Model is NULL. Db:" + db.getNamedDbUrl());
			}
			return false;
		}
		try {
			int rslt = db.removeModelIfExist(model);
			if (L.isInfoEnabled()) {
				L.info("Remove SUCCESS ({})/({}):" + model.getClass().getSimpleName(), rslt > 0, rslt);
			}
			return rslt > 0;
		} catch (Exception ex) {
			if (L.isErrorEnabled()) {
				L.error("Error remove:" + model.getClass().getSimpleName(), ex);
			}
			return false;
		}
	}

	public static <M extends AModel> TypeDb<M> getTypeDbAsMaster_Or_Default(Class<M> classModel, TypeDb... db) {
		return ARG.isDef(db) ? ARG.toDef(db) : TypeDb.getDefaultTypeDb(classModel);
	}

	public static <M extends AModel> TypeDb getDefaultTypeDb(Class<M> modelClass, TypeDb... defRq) {
		return ((TypeDb) RFL.readSt(modelClass, AModel.FN_DB, true, false, defRq));
	}

	public static AbsType queryFF_(TypeDb typeDb, String sql, Object... args) throws SQLException {
		return ARRi.firstFirst(queryList_(typeDb, sql, args));
	}

	public static AbsType queryFFfm_(TypeDb typeDb, String sql, Object... args) throws SQLException {
		return ARRi.firstFirst(queryList_(typeDb, X.fm(sql, args)));
	}

	public static List<List<AbsType>> queryList_(TypeDb typeDb, String sql, Object... args) throws SQLException {
		return Db.queryList_(typeDb.getNamedDbUrl().getJdbcUrlType(), sql, args);
	}

	public static boolean execute_(TypeDb db, String sql, Object... args) throws SQLException {
		return Db.execute_(db.getNamedDbUrl().getJdbcUrlType(), sql, args);
	}

	public Class<M> getClassModel() {
		return classModel;
	}

	public static <M extends AModel> List<Object> getAsRow(M model, List<String> head) {
		return head.stream().map(key -> RFL.read(model, key, true, true)).collect(Collectors.toList());
	}

	public List<List<Object>> getAllRows(boolean includeHead) {
		final List<String> head = getHeadRow();
		List<List<Object>> values = getModels().stream().map(model -> getAsRow(model, head)).collect(Collectors.toList());
		if (!includeHead) {
			return values;
		}
		List newHead = new ArrayList<>();
		newHead.add(new ArrayList(head));
		newHead.addAll(values);
		return newHead;
	}

	private List<String> head;

	public List<String> getHeadRow() {
		return head == null ? head = AModel.getColumnNames(classModel) : head;
	}


	@Override
	public String toString() {
		return SYMJ.BOOKS + "TypeDb{" + "jdbc:" + namedDbUrl + ", ns=" + (ns == null ? "#" : ns) + ", model=" + classModel + ", cols=" + AModel.getColumnNames(classModel) + '}';
	}

	public StringBuilder toString(boolean includeData) {
		StringBuilder sb = new StringBuilder();
		sb.append(toString());
		if (!includeData) {
			return sb;
		}

		List<List<Object>> rowsWithHead = getAllRows(true);
		StringBuilder tbl = Rt.buildTable(rowsWithHead);

		String NL = SYM.NEWLINE;

		sb.append(NL).append(SEP._LINE_("Db:" + getNamedDbUrl().getDbFile()));
		sb.append(NL).append(SEP._LINE_("Table:" + DBU.getTableName(classModel)));
		sb.append(NL).append(tbl);
		sb.append(NL).append(SEP._LINE_("Total:" + rowsWithHead.size()));

		return sb;
	}

	public static <M extends AModel> TypeDb<M> of(Class<M> clazzModel, Path path, boolean... createDbIfNotExists) {
		return new TypeDb(clazzModel, path, createDbIfNotExists);
	}

	@Deprecated
	public static <M extends AModel> TypeDb<M> of(Class<M> clazzModel, boolean... createDbIfNotExists) {
		return of(clazzModel, Ns.of(".", "."), createDbIfNotExists);
	}

	public static <M extends AModel> TypeDb<M> of(Class<M> clazzModel, Ns ns, boolean... createDbIfNotExists) {
		return new TypeDb(clazzModel, ns, createDbIfNotExists);
	}

	public static <M extends AModel> TypeDb<M> of(Class<M> clazzModel, Ns ns, String dbName, boolean... createDbIfNotExists) {
		return new TypeDb(clazzModel, ns, dbName, createDbIfNotExists);
	}

	//
	//------------------Constructor's
	//
	public TypeDb(Class<M> classModel, boolean... createDbIfNotExists) {
		this(classModel, Ns.of(classModel.getName()), DBU.getTableName(classModel), createDbIfNotExists);
	}

	public TypeDb(Class<M> classModel, Ns ns, boolean... createDbIfNotExists) {
		this(classModel, ns, DBU.getTableName(classModel), createDbIfNotExists);
	}

	public TypeDb(Class<M> classModel, Ns ns, String dbName, boolean... createDbIfNotExists) {
		this(classModel, ns, ns.getPathStr(), ".", dbName, false, createDbIfNotExists);
	}

	public TypeDb(Class<M> classModel, String rootDir, String parentDir, boolean... createDbIfNotExists) {
		this(classModel, null, rootDir, parentDir, DBU.getTableName(classModel), false, createDbIfNotExists);
	}

	public TypeDb(Class<M> classModel, String rootDir, String parentDir, String dbName, boolean... createDbIfNotExists) {
		this(classModel, null, rootDir, parentDir, dbName, false, createDbIfNotExists);
	}

	private TypeDb(Class<M> classModel, Ns ns, String rootDir, String parentDir, String fileOrName, boolean isFileOrName, boolean... createDbIfNotExists) {
		if (ns == null) {
			this.namedDbUrl = new NamedDbUrl(rootDir, parentDir, IT.notEmpty(fileOrName), isFileOrName);
		} else {
			this.namedDbUrl = new NamedDbUrl(ns.getPathStr(), ".", IT.notEmpty(fileOrName), isFileOrName);
		}
		this.ns = ns;
		this.classModel = classModel;
		checkOrCreateDb(createDbIfNotExists);
	}

	public TypeDb(Class<M> classModel, Path path, boolean... createDbIfNotExists) {
		this.namedDbUrl = new NamedDbUrl(path);
		this.ns = null;
		this.classModel = classModel;
		checkOrCreateDb(createDbIfNotExists);
	}

	public List<M> getModelsQk(QP... params) {
		return DBU.getModelsQk(namedDbUrl, classModel, params);
	}

	public List<M> getModels(QP... params) {
		return DBU.getModels(namedDbUrl, classModel, params);
	}

	public boolean existModel(QP... params) {
		return DBU.existModel(namedDbUrl, classModel, params);
	}

	public boolean existModelQk(QP... params) {
		return DBU.existModelQk(namedDbUrl, classModel, params);
	}

	public boolean existModel_(QP... params) throws IOException, SQLException {
		return DBU.existModel_(namedDbUrl, classModel, params);
	}

	public M getModelFirstLastRandom(Boolean firstLastOrRandom, QP... eps) {
		return DBU.getModelFirstOrLastOrRandom(getNamedDbUrl(), getClassModel(), firstLastOrRandom, eps);
	}

	public M getModelNN(QP... params) {
		return DBU.getModelNN(getNamedDbUrl(), classModel, params);
	}

	public M getModel(QP... params) {
		return DBU.getModel(getNamedDbUrl(), classModel, params);
	}

	public M getModelById(int id, M... defRq) {
		return DBU.getModelById(getNamedDbUrl(), classModel, id, defRq);
	}

	public M getModelByUid(long uid) {
		return DBU.getModelByUidQk(getNamedDbUrl(), classModel, uid);
	}

	public TypeDb<M> createOrUpdateModelRq(M model) {
		DBU.saveModelAsCreateOrUpdateModelRq(getNamedDbUrl(), model);
		return this;
	}

	public TypeDb<M> createModelQk(M model) {
		DBU.createModelQk(getNamedDbUrl(), model);
		return this;
	}

	public TypeDb<M> createModel(M model) {
		DBU.createModel(getNamedDbUrl(), model);
		return this;
	}

	public NamedDbUrl getNamedDbUrl() {
		return this.namedDbUrl;
	}

	public void checkOrCreateDb(boolean... createDbIfNotExists) {
		if (ARG.isDefEqTrue(createDbIfNotExists)) {
			DBU.checkOrCreateDb(namedDbUrl, classModel);
		}
	}

	public TypeDb setAllRows(List<List<Object>> rows, boolean createOrUpdateModel) throws Exception {
		new TypeDbEE.UpdaterDb(classModel, getNamedDbUrl().getJdbcUrl()).setAllRows(getHeadRow(), rows);
		return this;
	}

	public void testDb() throws SQLException {
		JdbcConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(this.namedDbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);


			Sys.exit(dao.queryForAll().get(0));
		} finally {
			if (connectionSource != null) {
				DaoManager.unregisterDao(connectionSource, dao);
			}
		}
	}

	public void printDebugInfo() {
		L.info("@DB:{}", getNamedDbUrl().getDbFilePath().toAbsolutePath());
	}

	public boolean isEmptyDb() {
		return DBU.isEmptyDb_NotExistedDb(getNamedDbUrl(), classModel);
	}

	public <M extends AModel> void saveAsUpdateRq(AModel<M> model) {
		DBU.updateModel(getNamedDbUrl(), model, OperDB.update);
	}

	public long getCountRq(QP... eps) {
		return DBU.getCountModelRq(namedDbUrl, classModel, null, null, eps);
	}

	public <M extends AModel> int removeModelIfExist(M model) {
		return DBU.removeModelByIdIfExist(namedDbUrl, model);
	}

	public <M extends AModel> int removeModel(M model) {
		return DBU.removeModelById(namedDbUrl, model);
	}

	@SneakyThrows
	public <M extends AModel> int removeModelRq(QP qp) {
		return removeModelRq(new QP[]{qp});
	}

	@SneakyThrows
	public <M extends AModel> int removeModelRq(QP qp1, QP qp2) {
		return removeModelRq(new QP[]{qp1, qp2});
	}

	@SneakyThrows
	public <M extends AModel> int removeModelRq(QP[] qps) {
		int count = DBU.removeModelBy(namedDbUrl, getClassModel(), qps);
		if (count < 1) {
			throw DbEE.EE.REMOVE_MODEL.I("Not found to delete:" + Arrays.asList(qps));
		}
		return count;
	}

	public void removeDb_() throws IOException {
		getNamedDbUrl().removeDb();
	}

	public void recreateDb_() throws IOException {
		removeDb_();
		checkOrCreateDb(true);
	}

	public <M> Double getSumm(String colName, Double defIfNull, QP... eps) {
		return DBU.getSummRq(getNamedDbUrl(), getClassModel(), colName, defIfNull, eps);
	}

	public M newModel() {
		return AModel.newModel(getClassModel());
	}

	public M newModelWithIncrementCol(String colNameWithId) {
		return DBUE.incrementColValueSyncNewModel(this, colNameWithId);
	}

	public M incrementColValueSyncSID(M mdl) {
		return incrementColValueSync(mdl, CN.SID);
	}

	public M incrementColValueSync(M mdl, String colNameWithId) {
		return DBUE.incrementColValueSync(this, mdl, colNameWithId);
	}

	@Deprecated
	public long incrementOr(String colName, long def) throws IOException, SQLException {
		return DBU.incrementOr(getNamedDbUrl(), getClassModel(), colName, def);
	}

	public M newModelIfExist(QP[] qps, boolean... returnNullOrExistModel) {
		M model = getModel(qps);
		return model == null ? newModel() : ARG.isDefEqTrue(returnNullOrExistModel) ? null : model;
	}

	public List<List<AbsType>> sql_query_(String sql) throws SQLException {
		return queryList_(this, sql);
	}

	public boolean sql_execute_(String sql) throws SQLException {
		return execute_(this, sql);
	}

	public Ns getNs() {
		return getNamedDbUrl().getJdbcUrlType().getNs();
	}

}
