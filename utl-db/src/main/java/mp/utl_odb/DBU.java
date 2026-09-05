package mp.utl_odb;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import lombok.SneakyThrows;
import mp.utl_ndb.SqlDbUrl;
import mp.utl_odb.query_core.OperDB;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.mdl.AModel;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.arr.STREAM;
import mpc.rfl.RFL;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpv.byteunit.ByteUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpz_deprecated.EER;
import mpc.exception.FIllegalStateException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class DBU extends DBUBase {
	public static final Logger L = LoggerFactory.getLogger(DBU.class);
	public static final String DB = "DB";

	public static void main(String[] args) throws SQLException, IOException {
		Sys.p(test());
	}

	public static Object test() throws SQLException, IOException {
		try {
			return "ok";
		} finally {
			try {
				if (true) {
					throw new IOException("connection");
				}
			} finally {
				Sys.p("finally");
			}
		}
	}

	//https://www.py4u.net/discuss/656276
	//https://stackoverflow.com/questions/12190258/using-max-in-ormlite
	@Deprecated
	public static <M> List<M> getModelsByMaxValue(SqlDbUrl dbUrl, Class<M> classModel, String colNameId, String colValueId, String colNameMax) throws SQLException {
		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);

			QueryBuilder<? extends AModel, String> qb = dao.queryBuilder();

			if (false) {
				long max = dao.queryRawValue("select max(" + colNameMax + ") from foo where " + colNameId + " = ?", colValueId);
				// now perform a second query to get the max row
				M foo = (M) dao.queryBuilder().where().eq(colNameMax, max).queryForFirst();
				//			return qb.queryRaw()
				//			return qb.queryRaw(sql, new GenericRowMapper() {
				//				@Override
				//				public Object mapRow(DatabaseResults databaseResults) throws SQLException {
				//					return null;
				//				}
				//			}).getResults();
			}
			return (List<M>) qb.query();
		} finally {
			closeDaoAndConnection(connectionSource, dao);
		}

	}

//	public static <M extends AModel> M findOrCreateModelRq(SqlDbUrl dbUrl, Class<M> classModel, String colName, String colValue) {
//		QP ep = QP.param(colName, colValue);
//		M model = DBU.getModel(dbUrl, classModel, ep);
//		if (model != null) {
//			return model;
//		}
//
//		try {
//			model = classModel.getConstructor().newInstance();
//		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
//				 InvocationTargetException | NoSuchMethodException | SecurityException e) {
//			throw EER.IS(e);
//		}
//
//		model.setObjectFieldOld(colName, colValue);
//		createModel(dbUrl, model);
//		return model;
//	}

	public static <M> boolean existModelQk(SqlDbUrl dbUrl, Class<M> classModel, QP... equalsParam) {
		try {
			return dbUrl != null && dbUrl.isExistDb() && getCountModel_(dbUrl, classModel, null, null, equalsParam) > 0;
		} catch (Exception ex) {
			return false;
		}
	}

	public static <M> boolean existModel(SqlDbUrl dbUrl, Class<M> classModel, QP... equalsParam) {
		try {
			return getCountModel_(dbUrl, classModel, null, null, equalsParam) > 0;
		} catch (Exception ex) {
			throw new FIllegalStateException(ex, "DBU#existModelRq");
		}
	}

	public static <M> boolean existModel_(SqlDbUrl dbUrl, Class<M> classModel, QP... equalsParam) throws SQLException {
		return getCountModel_(dbUrl, classModel, null, null, equalsParam) > 0;
	}

	public static List<String> getValues(AModel model, List<String> cols) {
		List<String> toValues = new ArrayList<String>();
		for (String colName : cols) {
			String v = X.toStringNN(model.getObjectFieldAsObject(colName), "");
			toValues.add(X.empty(v) ? "" : v);
		}
		return toValues;
	}

	public static Properties toProperties(AModel model) {
		Properties p = new Properties();
		Class clazz = model.getClass();
		List cols = AModel.getColumnNames(clazz);
		List<String> values = DBU.getValues(model, cols);
		for (int i = 0; i < cols.size(); i++) {
			String s = values.get(i);
			s = X.empty(s) ? "" : s;
			p.put(cols.get(i), s);
		}
		return p;
	}

	public static void recreateDb(SqlDbUrl urlDb, List<? extends AModel> models, Class... classes) {
		for (Class class1 : classes) {
			truncateTable(urlDb, class1);
		}
		checkOrCreateDb(urlDb, classes);
		for (AModel m : models) {
			updateModelQk(urlDb, m, OperDB.create);
		}
	}

	public static void reverse(SqlDbUrl srcDb, SqlDbUrl dstDb, Class<? extends AModel> clas) {
		if (!srcDb.isExistDb()) {
			throw EER.RT.I("src db not exist");
		}
		if (!dstDb.isExistDb()) {
			throw EER.RT.I("dst db not exist");
		}
		List<AModel> list = getModels(srcDb, clas);
		Collections.reverse(list);
		for (AModel model : list) {
			DBU.updateModelQk(dstDb, model, OperDB.create);
		}
	}

	@Deprecated // See DBUE
	public static long incrementOr(SqlDbUrl dbUrl, Class classModel, String colName, long def) throws IOException, SQLException {
		long i = (long) (double) getMaxValueDouble_OrDefIfEmptyExistedDb(dbUrl, classModel, colName, 1.00);
		i++;
		return i;
	}

	public static void ENABLE_LOG_WARN() {
		com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING);
	}

	public static void ENABLE_LOG_TRACE() {
		com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.TRACE);
	}

	public static <M extends AModel> boolean isEmptyDb_NotExistedDb(SqlDbUrl dbUrl, Class<M> classModel) {
		if (!dbUrl.isExistDb()) {
			return true;
		}
		return isEmptyDb_ExistedDb(dbUrl, classModel);
	}

	public static <M extends AModel> boolean isEmptyDb_ExistedDb(SqlDbUrl dbUrl, Class<M> classModel) {
		return DBU.getCountModel(dbUrl, classModel) <= 0;
	}

	public static void setLogLevel(Level level) {
		com.j256.ormlite.logger.Logger.setGlobalLogLevel(level);
	}

	@SneakyThrows
	public static double getDbFileSizeMb(ICtxDb ctxDb) {
		double currentMb = ByteUnit.BYTE.toMB(Files.size(ctxDb.getDbFilePath()));
		return currentMb;
	}

	public static void checkOrCreateDb(SqlDbUrl sql, Class<? extends AModel>... classes) {
		sql.createDbIfNotExists();
		for (Class clas : classes) {
			if (!isExistTable(sql, clas)) {
				modifyTableRq(sql, clas, EModifyTable.CREATE_TABLE);
			}
		}
	}

	public static List<String> getAllFieldNames(Class modelClass) {
		List<Field> fields = RFL.fields(modelClass, ARR.as(DatabaseField.class));
		List<String> strings = STREAM.mapToList(fields, Field::getName);
		return strings;
	}

	public static String getTableName(Class<? extends AModel> clazz) {
		return DatabaseTableConfig.extractTableName(null, clazz);
	}
}
