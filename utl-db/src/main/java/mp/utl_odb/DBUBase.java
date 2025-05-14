package mp.utl_odb;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import mp.utl_odb.query_core.*;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mpc.str.sym.SYMJ;
import mpe.str.CN;
import mp.utl_ndb.SqlDbUrl;
import mpu.core.ARG;
import mpu.IT;
import mpe.core.ERR;
import mpu.str.UST;
import mp.utl_odb.mdl.AModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpc.rfl.RFL;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DBUBase {
	public static final Logger L = LoggerFactory.getLogger(DBUBase.class);
	public static final String MSG_ERROR_GET_MAX_VALUE = "Error get max value";

	/**
	 * *************************************************************
	 * ------------------------ getColValues -----------------------
	 * *************************************************************
	 */

	public static List<String> getColValuesQk(SqlDbUrl dbUrl, Class classModel, String col) {
		List<String[]> rms = getColValuesQk(dbUrl, classModel, Arrays.asList(col));
		List<String> ms = new ArrayList<String>();
		for (String[] vls : rms) {
			ms.add(vls[0]);
		}
		return ms;
	}

	public static List<String[]> getColValuesQk(SqlDbUrl dbUrl, Class classModel, List<String> cols) {
		try {
			return getColValues(dbUrl, classModel, cols, null, null, null, null);
		} catch (Throwable e) {
			return Collections.EMPTY_LIST;
		}
	}

	public static List<String[]> getColValuesRq(SqlDbUrl dbUrl, Class classModel, List<String> cols) {
		try {
			return getColValues(dbUrl, classModel, cols, null, null, null, null);
		} catch (SQLException ex) {
			throw DbEE.EE.SQL_ERROR.I(ex);
		}
	}

	public static List<String[]> getColValues(SqlDbUrl dbUrl, Class classModel, List<String> cols, OrderParam[] orders, Long offset, Long limit, DistinctParam distinct, QP... equalsParam) throws SQLException {
		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);

			QueryBuilder<? extends AModel, String> qb = dao.queryBuilder();

			if (distinct != null && distinct.columns().length != 0) {
				qb = qb.distinct().selectColumns(distinct.columns());
			}

			if (limit != null) {
				qb.limit(limit);
			}

			if (offset != null) {
				qb.offset(offset);
			}

			if (orders != null) {
				for (OrderParam order : orders) {
					if (order != null) {
						qb = order.apply(qb);
					}
				}
			}
			// qb=order.apply(qb);

			if (equalsParam != null) {
				QP.apply(qb, equalsParam);
			}

			qb = qb.selectColumns(cols);

			List<String[]> rms = qb.queryRaw().getResults();
			return rms;

		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}

	}

	/**
	 * *************************************************************
	 * -------------------------- TABLE ----------------------------
	 * *************************************************************
	 */

	public enum EModifyTable {
		CREATE_IF_NOT_EXIST, CREATE_TABLE, TRUNCATE, DROP
	}

	public static <M> boolean isExistTable(SqlDbUrl dbUrl, Class<M> classModel) {
		try {
			long count = getCountModelTotal_(dbUrl, classModel);
			return count >= 0;
		} catch (SQLException | IOException e) {
			return false;
		}
	}

	public static boolean createTableIfNotExistsQk(SqlDbUrl dbUrl, Class<? extends AModel> classModel) {
		try {
			return createTableIfNotExists(dbUrl, classModel);
		} catch (Throwable ex) {
			return false;
		}
	}

	public static boolean createTableIfNotExistsRq(SqlDbUrl dbUrl, Class<? extends AModel> classModel) {
		try {
			return createTableIfNotExists(dbUrl, classModel);
		} catch (IOException ex) {
			throw DbEE.EE.IO_ERROR.I(ex);
		} catch (SQLException ex) {
			throw DbEE.EE.SQL_ERROR.I(ex);
		}
	}

	public static boolean createTableIfNotExists(SqlDbUrl dbUrl, Class<? extends AModel> classModel) throws SQLException, IOException {
		return modifyTable(dbUrl, classModel, EModifyTable.CREATE_IF_NOT_EXIST, false);
	}

	public static boolean modifyTableQk(SqlDbUrl dbUrl, Class<? extends AModel> classModel, EModifyTable eModifyTable) {
		try {
			return modifyTable(dbUrl, classModel, eModifyTable, true);
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean modifyTableRq(SqlDbUrl dbUrl, Class<? extends AModel> classModel, EModifyTable eModifyTable) {
		try {
			return modifyTable(dbUrl, classModel, eModifyTable, false);
		} catch (SQLException ex) {
			throw DbEE.EE.SQL_ERROR.I(ex);
		}
	}

	public static boolean modifyTable(SqlDbUrl dbUrl, Class<? extends AModel> classModel, EModifyTable eModifyTable, boolean ignoreErrors) throws SQLException {
		if (L.isInfoEnabled()) {
			L.info(SYMJ.FILE_DB + "Modify [file://{}] [{}][{}]", dbUrl.getDbFile(), eModifyTable, DBU.getTableName(classModel));
		}
		ConnectionSource connectionSource = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			return modifyTable(connectionSource, classModel, eModifyTable, ignoreErrors);
		} finally {
			closeDaoAndConnection(connectionSource, null);
		}
	}

	public static boolean modifyTable(ConnectionSource connectionSource, Class<? extends AModel> classModel, EModifyTable eModifyTable, boolean ignoreErrors) throws SQLException {
		switch (eModifyTable) {
			case CREATE_IF_NOT_EXIST: {
				TableUtils.createTableIfNotExists(connectionSource, classModel);
				return true;
			}
			case CREATE_TABLE: {
				TableUtils.createTable(connectionSource, classModel);
				return true;
			}
			case TRUNCATE: {
				TableUtils.clearTable(connectionSource, classModel);
				return true;
			}
			case DROP: {
				TableUtils.dropTable(connectionSource, classModel, ignoreErrors);
				return true;
			}
		}
		return false;
	}

	public static void truncateTable(SqlDbUrl urlDb, Class<? extends AModel> clas) {
		if (!DBU.isEmptyDb_ExistedDb(urlDb, clas)) {
			DBU.modifyTableRq(urlDb, clas, EModifyTable.TRUNCATE);
		}
	}

	public static void dropTable(SqlDbUrl urlDb, Class<? extends AModel> clas) {
		DBU.modifyTableRq(urlDb, clas, EModifyTable.DROP);
	}


	/**
	 * *************************************************************
	 * ------------------------- getMinMax -------------------------
	 * *************************************************************
	 */

	public static <M> double[] getMinMaxValuesQk(SqlDbUrl dbUrl, Class<M> classModel, String colName) {
		try {
			double[] vls = getMinMaxValues(dbUrl, classModel, colName);
			return vls;
		} catch (Throwable ex) {
			return null;
		}
	}

	public static <M> double[] getMinMaxValuesRq(SqlDbUrl dbUrl, Class<M> classModel, String colName) {
		try {
			return getMinMaxValues(dbUrl, classModel, colName);
		} catch (SQLException ex) {
			throw DbEE.EE.SQL_ERROR.I(ex);
		}
	}

	public static <M> double[] getMinMaxValues(SqlDbUrl dbUrl, Class<M> classModel, String colName) throws SQLException {
		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);
			QueryBuilder<? extends AModel, String> qb = dao.queryBuilder();
			qb.selectRaw("MIN(" + colName + ")", "MAX(" + colName + ")");
			GenericRawResults results = dao.queryRaw(qb.prepareStatementString());
			String[] values = (String[]) results.getFirstResult();
			try {
				return new double[]{Double.parseDouble(values[0]), Double.parseDouble(values[1])};
			} catch (Exception ex) {
				throw new SQLException("Error get min&max values::" + colName + "::", ex);
			}
		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}
	}

	public static <M> Double getMaxValueDouble_OrDefIfEmptyExistedDb(SqlDbUrl dbUrl, Class<M> classModel, String colName, Double ifEmptyDb) throws SQLException {
		String maxValueString = getMaxValueString(dbUrl, classModel, colName);
		if (maxValueString != null) {
			return Double.parseDouble(maxValueString);
		}
		if (maxValueString == null) {
			return ifEmptyDb;
		}
		return Double.parseDouble(maxValueString);
	}

	public static <M> String getMaxValueString(SqlDbUrl dbUrl, Class<M> classModel, String colName, String... defRq) throws SQLException {
		String maxValueString = getMaxValueString(dbUrl, classModel, colName);
		if (maxValueString != null) {
			return maxValueString;
		}
		return ARG.toDefRq(defRq);
	}

	public static <M> String getMaxValueString(SqlDbUrl dbUrl, Class<M> classModel, String colName) throws SQLException {
		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);
			QueryBuilder<? extends AModel, String> qb = dao.queryBuilder();
			qb.selectRaw("MAX(" + colName + ")");
			GenericRawResults results = dao.queryRaw(qb.prepareStatementString());
			String[] values = (String[]) results.getFirstResult();
			return values[0];
		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}
	}

	/**
	 * *************************************************************
	 * ------------------------- getSumm ---------------------------
	 * *************************************************************
	 */

	public static <M> Double getSummQk(SqlDbUrl dbUrl, Class<M> classModel, String colName, Double defIfNull, QP... qps) {
		try {
			Double vl = getSumm(dbUrl, classModel, colName, defIfNull, qps);
			return vl;
		} catch (Throwable ex) {
			return defIfNull;
		}
	}

	public static <M> Double getSummRq(SqlDbUrl dbUrl, Class<M> classModel, String colName, Double defIfNull, QP... qps) {
		try {
			Double vl = getSumm(dbUrl, classModel, colName, defIfNull, qps);
			return vl;
		} catch (SQLException ex) {
			throw DbEE.EE.SQL_ERROR.I(ex);
		}
	}

	public static <M> Double getSumm(SqlDbUrl dbUrl, Class<M> classModel, String colName, Double defIfNull, QP... eps) throws SQLException {
		Long count = null;
		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);
			QueryBuilder<? extends AModel, String> qb = dao.queryBuilder();
			qb.selectRaw("SUM(" + colName + ")");

			if (eps != null) {
				QP.apply(qb, eps);
			}

			GenericRawResults results = dao.queryRaw(qb.prepareStatementString());
			String[] values = (String[]) results.getFirstResult();
			// Long min = null;


			Double val = UST.DBL(values[0], defIfNull);
			return val;
		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}

	}


	/**
	 * *************************************************************
	 * ------------------------ getModelBy -------------------------
	 * *************************************************************
	 */

	public static <M> M getModelByUidQk(SqlDbUrl dbUrl, Class<M> classModel, long uid) {
		return getModelByQk(dbUrl, classModel, CN.UID, uid);
	}

	public static <M> M getModelByQk(SqlDbUrl dbUrl, Class<M> classModel, String colName, Object value) {
		try {
			return getModelBy_(dbUrl, classModel, colName, value);
		} catch (Throwable ex) {
			return null;
		}
	}

	public static <M> M getModelBy(SqlDbUrl dbUrl, Class<M> classModel, String colName, Object value) {
		try {
			return getModelBy_(dbUrl, classModel, colName, value);
		} catch (SQLException e) {
			throw DbEE.EE.SQL_ERROR.I(e);
		}
	}

	public static <M> M getModelBy_(SqlDbUrl dbUrl, Class<M> classModel, String colName, Object value) throws SQLException {
		M model = null;
		ConnectionSource connectionSource = null;
		Dao<M, Integer> dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);

			QueryBuilder<M, Integer> qb = dao.queryBuilder().limit(1L);

			if (value == null) {
				qb.where().isNull(colName);
			} else {
				qb.where().eq(colName, value);
			}
//			qb.orderBy("id",true);
			PreparedQuery<M> preparedQuery = qb.prepare();
			model = (M) dao.queryForFirst(preparedQuery);

			return model;
		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}
	}

	public static <M> M getModelById(SqlDbUrl dbUrl, Class<M> classModel, Integer primaryId, M... defRq) {
		try {
			return getModelById_(dbUrl, classModel, primaryId);
		} catch (Throwable ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static <M> M getModelById_(SqlDbUrl dbUrl, Class<M> classModel, Integer primaryId) throws SQLException {
		M model = null;
		ConnectionSource connectionSource = null;
		Dao<M, Integer> dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);
			model = dao.queryForId(primaryId);
			return model;
		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}
	}

	/**
	 * *************************************************************
	 * -------------------------- getModel -------------------------
	 * *************************************************************
	 */
	public static <M extends AModel> M getModelAfterBeforeEq(SqlDbUrl dbUrl, Class<M> classModel, Boolean afterOrBeforeOrEq, String colname, Object value, M... defRq) {
		if (false == dbUrl.isExistDb()) {
			return ARG.toDefRq(defRq);
		}
		Exception ex = null;
		M model = null;
		try {
			model = DBU.getModelBy_(dbUrl, classModel, colname, value);
		} catch (SQLException e) {
			ex = e;
		}
		if (model == null) {
			return ARG.toDefThrow(ex, defRq);
		}
		try {
			model = DBU.getModel_(dbUrl, classModel, QP.afterOrBeforeOrEq(afterOrBeforeOrEq, CN.ID, model.getId()));
		} catch (SQLException e) {
			ex = e;
		}
		if (model == null) {
			return ARG.toDefThrow(ex, defRq);
		}
		return model;
	}

	public static <M> M getModelFirstOrLastOrRandomBy(SqlDbUrl dbUrl, Class<M> clazz, Boolean firstOrLastOrRandom, String col, QP... eps) {
		return getModelFirstOrLastOrRandom(dbUrl, clazz, firstOrLastOrRandom,  col, eps);
	}

	public static <M> M getModelFirstOrLastOrRandom(SqlDbUrl dbUrl, Class<M> clazz, Boolean firstOrLastOrRandom, QP... eps) {
		return getModelFirstOrLastOrRandom(dbUrl, clazz, firstOrLastOrRandom, CN.ID, eps);
	}

	public static <M> M getModelFirstOrLastOrRandom(SqlDbUrl dbUrl, Class<M> clazz, Boolean firstOrLastOrRandom, String colName, QP... eps) {
		OrderParam orderParam = firstOrLastOrRandom == null ? OrderParam.paramRandom(colName) : (firstOrLastOrRandom ? OrderParam.paramAsc(colName) : OrderParam.paramDesc(colName));
		return getModel(dbUrl, clazz, null, orderParam, eps);
	}

	public static <M> M getModelFromRq(SqlDbUrl dbUrl, M model, List<String> cols, QP... equalsParam) {
		List<QP> newEqParams = new ArrayList<>();
		for (String col : cols) {
			QP eq = QP.param(col, RFL.read(model, col, true, false));
			newEqParams.add(eq);
		}
		newEqParams.addAll(Arrays.asList(equalsParam));
		return (M) getModel(dbUrl, model.getClass(), null, null, newEqParams.toArray(new QP[newEqParams.size()]));

	}

	public static <M> M getModelNN(SqlDbUrl dbUrl, Class<M> classModel, QP... equalsParam) {
		return IT.NN(getModel(dbUrl, classModel, null, null, equalsParam));
	}

	public static <M> M getModel(SqlDbUrl dbUrl, Class<M> classModel, QP... equalsParam) {
		return getModel(dbUrl, classModel, null, null, equalsParam);
	}

	public static <M> M getModel(SqlDbUrl dbUrl, Class<M> classModel, Long offset) {
		return getModel(dbUrl, classModel, offset, null, null);
	}

	public static <M> M getModelQk(SqlDbUrl dbUrl, Class<M> classModel, Long offset, OrderParam order, QP... equalsParam) {
		try {
			return getModel_(dbUrl, classModel, offset, order, equalsParam);
		} catch (SQLException ex) {
			return null;
		}
	}

	public static <M> M getModel(SqlDbUrl dbUrl, Class<M> classModel, Long offset, OrderParam order, QP... equalsParam) {
		try {
			return getModel_(dbUrl, classModel, offset, order, equalsParam);
		} catch (SQLException e) {
			throw DbEE.EE.SQL_ERROR.I(e, dbUrl.getDbFile());
		}
	}

	public static <M> M getModel_(SqlDbUrl dbUrl, Class<M> classModel, QP[] qps) throws SQLException {
		return getModel_(dbUrl, classModel, null, null, qps);
	}

	public static <M> M getModel_(SqlDbUrl dbUrl, Class<M> classModel, Long offset, OrderParam order, QP... qps) throws SQLException {
		M model = null;
		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {

			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);

			QueryBuilder<M, String> qb = dao.queryBuilder().limit(1L);

			if (offset != null) {
				qb.offset(offset);
			}

			if (order != null) {
				order.apply(qb);
			}

			if (qps != null) {
				QP.apply(qb, qps);
			}

			PreparedQuery<M> preparedQuery = qb.prepare();
			if (L.isTraceEnabled()) {
				L.trace("__________________________________________________________________________");
				L.trace("SQL:" + qb.prepareStatementString());
				L.trace("SQL ???:" + Arrays.asList(qps));
			}
			model = (M) dao.queryForFirst(preparedQuery);

			return model;

		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}
	}

	/**
	 * *************************************************************
	 * ------------------------- getModels -------------------------
	 * *************************************************************
	 */

	public static <M> List getModelsByRq(SqlDbUrl dbUrl, Class<M> classModel, String colName, Object value) {
		return getModels(dbUrl, classModel, (OrderParam[]) null, (Long) null, null, null, QP.param(colName, value));
	}

	public static <M> List getModels(SqlDbUrl dbUrl, Class<M> classModel) {
		return getModels(dbUrl, classModel, (OrderParam[]) null, null, null, null, null);
	}

	public static <M> List getModelsQk(SqlDbUrl dbUrl, Class<M> classModel, QP... equalsParam) {
		return getModelsQk(dbUrl, classModel, (OrderParam[]) null, null, null, null, equalsParam);
	}

	public static <M> List getModels(SqlDbUrl dbUrl, Class<M> classModel, QP... equalsParam) {
		return getModels(dbUrl, classModel, (OrderParam[]) null, null, null, null, equalsParam);
	}

	public static <M> List getModels(SqlDbUrl dbUrl, Class<M> classModel, OrderParam[] ops, QP... equalsParam) {
		return getModels(dbUrl, classModel, ops, null, null, null, equalsParam);
	}


	public static <M> List<M> getModels(SqlDbUrl dbUrl, Class<M> classModel, OrderParam order, Long offset, Long limit, QP... equalsParam) {
		return getModels(dbUrl, classModel, new OrderParam[]{order}, offset, limit, null, equalsParam);
	}

	public static <M> List<M> getModelsQk(SqlDbUrl dbUrl, Class<M> classModel, OrderParam[] orders, Long offset, Long limit, DistinctParam distinct, QP... equalsParam) {
		try {
			return getModels_(dbUrl, classModel, orders, offset, limit, distinct, null, equalsParam);
		} catch (Throwable ex) {
			return Collections.EMPTY_LIST;
		}
	}

	public static <M> List<M> getModels(SqlDbUrl dbUrl, Class<M> classModel, OrderParam[] orders, Long offset, Long limit, DistinctParam distinct, QP... equalsParam) {
		try {
			return getModels_(dbUrl, classModel, orders, offset, limit, distinct, null, equalsParam);
		} catch (SQLException e) {
			throw DbEE.EE.SQL_ERROR.I(e);
		}
	}

	public static <M> List getModels_(SqlDbUrl dbUrl, Class<M> classModel) throws SQLException {
		return getModels_(dbUrl, classModel, null, null, null, null, null);
	}

	public static <M> List<M> getModels_(SqlDbUrl dbUrl, Class<M> classModel, OrderParam[] orders, Long offset, Long limit, DistinctParam distinct, GroupByParam groupBy, QP... qps) throws SQLException {

		if (!dbUrl.isExistDb()) {
			throw new SQLException("db with '" + classModel + "' is empty :" + dbUrl.getJdbcUrl());
		}

		List models = null;

		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);

			QueryBuilder<? extends AModel, String> qb = dao.queryBuilder();

			if (distinct != null && distinct.columns().length != 0) {
				qb = qb.distinct().selectColumns(distinct.columns());
			}

			if (groupBy != null && groupBy.columns().length != 0) {
				for (String col : groupBy.columns()) {
					qb = qb.groupBy(col);
				}
			}

			// NU
			if (limit != null) {
				qb.limit(limit);
			}

			// NU
			if (offset != null) {
				qb.offset(offset);
			}

			// NU
			if (orders != null) {
				for (OrderParam order : orders) {
					if (order != null) {
						qb = order.apply(qb);
					}
				}
			}

			if (qps != null) {
				QP.apply(qb, qps);
			}

			PreparedQuery<? extends AModel> preparedQuery = qb.prepare();
			models = dao.query(preparedQuery);

			return models;

		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}
	}

	/**
	 * *************************************************************
	 * ------------------------ removeModel ------------------------
	 * *************************************************************
	 */

	public static int removeModelQk(SqlDbUrl dbUrl, Class classModel, QP[] qps) {
		try {
			return removeModelBy(dbUrl, classModel, qps);
		} catch (SQLException e) {
			return -1;
		}
	}

	public static int removeModelQk(SqlDbUrl dbUrl, AModel arm) {
		try {
			return removeModelBy(dbUrl, arm.getClass(), new QP[]{QP.pID(arm.getId())});
		} catch (Throwable e) {
			return -1;
		}
	}

	public static int removeModelById(SqlDbUrl dbUrl, AModel model) {
		int res = removeModelByIdIfExist(dbUrl, model);
		if (res > 0) {
			return res;
		}
		throw DbEE.EE.REMOVE_MODEL.I(model.getClass().getSimpleName() + ":remove not found id:" + model.getId());
	}

	public static int removeModelByIdIfExist(SqlDbUrl dbUrl, AModel arm) {
		try {
			return removeModelBy(dbUrl, arm.getClass(), new QP[]{QP.pID(IT.NN(arm.getId()))});
		} catch (SQLException e) {
			throw DbEE.EE.SQL_ERROR.I(e);
		}
	}

	public static int removeModelBy(SqlDbUrl dbUrl, Class classModel, QP[] qps) throws SQLException {
		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);
			return removeModelBy(dao, qps);
		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}

	}

	private static int removeModelBy(Dao dao, QP[] equalsParam) throws SQLException {
		StatementBuilder<?, String> qb = dao.deleteBuilder();
		QP.applyStatement(qb, equalsParam);
		return dao.delete(((DeleteBuilder) qb).prepare());
	}

	//
	//
	public static void removeAllModels(SqlDbUrl treeDb, List<Ctx3Db.CtxModelCtr> models) {
		models.forEach(m -> DBU.removeModelById(treeDb, m));
	}

	/**
	 * *************************************************************
	 * ----------------------- getCountModel -----------------------
	 * *************************************************************
	 */

	public static <M> Long getCountModel(SqlDbUrl dbUrl, Class<M> classModel, Long... defRq) {
		try {
			return getCountModel_(dbUrl, classModel, null, null, null);
		} catch (SQLException e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static <M> long getCountModelTotal_(SqlDbUrl dbUrl, Class<M> classModel) throws SQLException, IOException {
		return getCountModel_(dbUrl, classModel, null, null, null);
	}

	public static <M> Long getCountModel_(SqlDbUrl dbUrl, Class<M> classModel, QP[] equalsFields, Long... defRq) {
		try {
			return getCountModel_(dbUrl, classModel, null, null, equalsFields);
		} catch (SQLException ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static <M> long getCountModelRq(SqlDbUrl dbUrl, Class<M> classModel, OrderParam order, Long offset, QP... equalsFields) {
		try {
			return getCountModel_(dbUrl, classModel, order, offset, equalsFields);
		} catch (SQLException ex) {
			throw DbEE.EE.SQL_ERROR.I(ex);
		}
	}

	public static <M> long getCountModel_(SqlDbUrl dbUrl, Class<M> classModel, OrderParam order, Long offset, QP... equalsFields) throws SQLException {
		Long count = null;
		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());

			dao = DaoManager.createDao(connectionSource, classModel);

			QueryBuilder<? extends AModel, String> qb = dao.queryBuilder();

			qb.setCountOf(true);

			if (offset != null) {
				qb.offset(offset);
			}

			if (order != null) {
				order.apply(qb);
			}

			if (equalsFields != null) {
				QP.apply(qb, equalsFields);
			}

			PreparedQuery<? extends AModel> preparedQuery = qb.prepare();

			count = dao.countOf(preparedQuery);

			return count;

		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}
	}

	/**
	 * *************************************************************
	 * ------------------------- createModel -----------------------
	 * *************************************************************
	 */
	public static <M extends AModel> boolean createModelQk(SqlDbUrl dbUrl, M model) {
		return DBU.updateModelQk(dbUrl, model, OperDB.create);
	}

	public static <M extends AModel> void createModel(SqlDbUrl dbUrl, M data) {
		updateModel(dbUrl, data, OperDB.create);
	}

	public static <M extends AModel> void createIfNotExistsModel(SqlDbUrl dbUrl, M data) {
		updateModel(dbUrl, data, OperDB.createIfNotExists);
	}

	public static <M extends AModel> void createOrUpdateModel(SqlDbUrl dbUrl, M data) {
		updateModel(dbUrl, data, OperDB.createOrUpdate);
	}


	/**
	 * *************************************************************
	 * ------------------------- updateModel -----------------------
	 * *************************************************************
	 */

	public static <M extends AModel> long saveModelAsCreateOrUpdateModelRq(SqlDbUrl dbUrl, M data) {
		return DBU.updateModel(dbUrl, data, OperDB.createOrUpdate);
	}

	public static <M extends AModel> void updateModelUngenericRq(SqlDbUrl dbUrl, Class classModel, M arModel, OperDB createorupdate) {
		updateModel(dbUrl, arModel, createorupdate);
	}


	public static <M extends AModel> boolean updateModelQk(SqlDbUrl dbUrl, M data, OperDB oper) {
		try {
			updateModel_(dbUrl, data, oper);
			return true;
		} catch (Throwable ex) {
			return false;
		}
	}

	public static <M extends AModel> void updateModel(SqlDbUrl dbUrl, M data) {
		updateModel(dbUrl, data, OperDB.update);
	}

	public static <M extends AModel> long updateModel(SqlDbUrl dbUrl, M data, OperDB oper) {
		try {
			return updateModel_(dbUrl, data, oper);
		} catch (SQLException ex) {
			if (isLockedDbException(ex)) {
				throw DbEE.EE.DB_LOCKED.I(ex, dbUrl.getDbFile());
			} else {
				throw DbEE.EE.SQL_ERROR.I(ex, dbUrl.getDbFile());
			}
		}
	}

	public static boolean isLockedDbException(SQLException ex) {
		return ERR.endsWith(ex, true, "(database is locked)");
	}

	public static <M extends AModel> long updateModel_(SqlDbUrl dbUrl, M data, OperDB oper) throws SQLException {
		Class<M> classModel = (Class<M>) data.getClass();

		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);
			long id = updateModel(dao, data, oper);
			return id;
		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}
	}

	static <M extends AModel> long updateModel(Dao dao, M data, OperDB oper) throws SQLException {
		switch (oper) {
			case createIfNotExists: {
				dao.createIfNotExists(data);
				break;
			}
			case update: {
				long res = dao.update(data);
				if (res == 0) {
					throw DbEE.EE.UPDATE_ROW_NOT_EXIST.I(data.getClass().getSimpleName() + ":update id:" + data.getId());
				}
				break;
			}
			case create: {
				if (dao.idExists(data.getId())) {
					throw DbEE.EE.CREATE_ROW_WITH_EXIST_ID.I(data.getClass().getSimpleName() + ":create already existed id:" + data.getId());
//					throw new IllegalStateException(U.f("Create model error. Id [%s] already exist", data.getId()));
				}
				dao.create(data);
				break;
			}
			case createOrUpdate: {
				Dao.CreateOrUpdateStatus status = dao.createOrUpdate(data);
				break;
			}
			case REMOVE_IF_EXIST:
			case REMOVE: {
				int rows = dao.delete(data);
				if (rows == 0 && oper == OperDB.REMOVE) {
					throw DbEE.EE.REMOVE_MODEL.I(data.getClass().getSimpleName() + ":remove not found id:" + data.getId());
				}
				return rows;
			}
			default:
				L.warn("Unsupported operation:{}", oper);
				throw DbEE.EE.UNSUPPORTED_OPER.I(oper.name());
		}
		return 0;
	}

	/**
	 * *************************************************************
	 * ----------------------------- COMMON ------------------------
	 * *************************************************************
	 */

	public static void closeDaoAndConnection(ConnectionSource connectionSource, Dao dao) {
		try {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		} finally {
			if (dao != null) {
				// dao.clearObjectCache();
				DaoManager.unregisterDao(connectionSource, dao);
			}
		}
	}
}
