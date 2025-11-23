package mp.utl_odb;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.SneakyThrows;
import mp.utl_ndb.SqlDbUrl;
import mp.utl_odb.query_core.OperDB;
import mp.utl_odb.tree.ctxdb.Ctx5Db;
import mp.utl_odb.typedb.TypeDb;
import mpc.fs.UFS;
import mpu.core.ARGn;
import mpu.core.ARR;
import mpu.IT;
import mpc.exception.NI;
import mpc.rfl.RFL;
import mp.utl_odb.mdl.AModel;
import mpu.Sys;
import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpc.fs.UDIR;
import mpu.str.STR;

import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class DBUE {

//	public static void main(String[] args) {
//		renameTablename()
//	}

	public static final Logger L = LoggerFactory.getLogger(DBUE.class);
	public static final String FILE_DB_PREFIX = "db-";
	public static final String FILE_DB_EXT = ".sqlite";
	private static final Lock LOCK_NEXT_COLVALUE = new ReentrantLock(false);

	public static <M extends AModel> M incrementColValueSyncNewModel(TypeDb<M> typeDb, String colNameWithId) {
		Class<M> classModel = typeDb.getClassModel();
		M model = RFL.instEmptyConstructor(classModel);
		return incrementColValueSync(typeDb, model, colNameWithId);
	}

	public static <M extends AModel> M incrementColValueSync(TypeDb<M> typeDb, M model, String colNameWithId) {
		IT.notNullAll(typeDb, model, colNameWithId);
		try {
			if (!LOCK_NEXT_COLVALUE.tryLock(10, TimeUnit.SECONDS)) {
				throw new IllegalStateException("Create Uid Lock");
			}

			long newUid;
			try {
				newUid = (long) (double) DBU.getMaxValueDouble_OrDefIfEmptyExistedDb(typeDb.getNamedDbUrl(), model.getClass(), colNameWithId, 1.00);
			} catch (DbEE err) {
				if (!err.is(DbEE.EE.GET_COL_VALUE)) {
					X.throwException(err);
				}
				if (!typeDb.isEmptyDb()) {
					X.throwException(err);
				}
				newUid = 0;
			}

			model.setObjectField(colNameWithId, newUid + 1);
			model.saveAsCreateOrUpdate(typeDb);
			return model;

		} catch (Exception e) {
			return X.throwException(e);
		} finally {
			LOCK_NEXT_COLVALUE.unlock();
		}

	}

	private static void merge(String dir, String dbDst, Class<? extends AModel> classModel) {

		List<String> files = UDIR.dir2files(dir);

		Sys.pf("Merge dbs to :{} in :{} , found :{} ", dbDst, dir, files);
		Set<String> allDb = new HashSet(files);

		for (String db : allDb) {
			copy(db, dbDst, classModel);
		}

	}

	private static void copy(String dbSrc, String dbDst, Class<? extends AModel> classModel) {
		Sys.pf("Copy :%s db:%s to :%s", classModel.getSimpleName(), dbDst, dbDst);

		SqlDbUrl db_src = SqlDbUrl.ofFile(dbSrc);
		if (!db_src.isExistDb()) {
			throw new NullPointerException("Db not found :" + dbSrc);
		}

		SqlDbUrl db_dst = SqlDbUrl.ofFile(dbDst);
		DBU.checkOrCreateDb(db_dst, classModel);

		List<? extends AModel> l = DBU.getModels(db_src, classModel);
		for (AModel arModel : l) {
			Long id = arModel.getId();
			{
				arModel.setId(null);
				DBU.updateModelQk(db_dst, arModel, OperDB.create);
			}
			L.info("Moved :" + id);
			L.info(dbSrc + " >>> " + dbDst + "");
		}
	}

//	public static int appendColumnSafe(SqlDbUrl dbUrl, Class<? extends AModel> classModel, String colName, String colType) {
//		try {
//			return appendColumn(dbUrl, classModel, colName, colType);

	/// /		} catch (Exception ex) {
	/// /			L.error(EER.getMessages("appendColumnSafe", ex));
	/// /			// L.error("appendColumnSafe::" + ex.getMessage()
	/// /			// + (ex.getCause() != null ? ex.getCause().getMessage() : ""));
	/// /			return 0;
	/// /		}
//	}

	// https://stackoverflow.com/questions/4253804/insert-new-column-into-table-in-sqlite
	@SneakyThrows
	public static int appendColumn(SqlDbUrl dbUrl, Class<? extends AModel> classModel, String colName, String colType) {
		Object model = null;
//		try {
		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);
			String tableName = DBU.getTableName(classModel);
			return dao.executeRaw("ALTER TABLE " + tableName + " ADD COLUMN " + colName + " " + colType);
		} finally {
			DBU.closeDaoAndConnection(connectionSource, dao);
		}
//		} catch (SQLException ex) {
//			throw DbEE.EE.IO_ERROR.I(ex);
//		}
	}


	@SneakyThrows
	public static int renameTablename(SqlDbUrl dbUrl, Class<? extends AModel> classModel, String prevTablename, String newTablename) {
		Object model = null;
//		try {
		ConnectionSource connectionSource = null;
		Dao dao = null;
		try {
			connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
			dao = DaoManager.createDao(connectionSource, classModel);
//				String tableName = AModel.getTableName(classModel);
//				return dao.executeRaw("ALTER TABLE " + tableName + " ADD COLUMN " + colName + " " + colType);
			return dao.executeRaw(X.f("ALTER TABLE %s RENAME TO %s;", prevTablename, newTablename));

		} finally {
			DBU.closeDaoAndConnection(connectionSource, dao);
		}
//		} catch (SQLException ex) {
//			throw DbEE.EE.IO_ERROR.I(ex);
//		}
	}


	public static List<SqlDbUrl> getAllDb(List<String> dirs) {
		List<SqlDbUrl> l = ARR.asAL();
		dirs.forEach(e -> l.addAll(getAllDb(e)));
		return l;
	}

	public static List<SqlDbUrl> getAllDb(String dir) {
		return getAllDbFiles(dir).stream().map(SqlDbUrl::ofFile).collect(Collectors.toList());
	}

	public static final FilenameFilter FFILTER_SQLITE = (dir, name) -> UFS.isFileWithContent(Paths.get(dir.getAbsolutePath(), name)) && name.endsWith(".sqlite");

	public static List<String> getAllDbFiles(String dir) {
		return UDIR.dir2files(dir, FFILTER_SQLITE);
	}

	public static String removeStartAndEndPrefix(String db) {
		db = STR.removeStartString(db, FILE_DB_PREFIX, false);
		db = STR.removeEndStringQk(db, FILE_DB_EXT, false);
		return db;
	}

	public static <M extends AModel> void copy(SqlDbUrl sqlDbUrlMain, Class<M> clas, int srcRowId, Long... destRowIds) {
		M srcModel = DBU.getModelById(sqlDbUrlMain, clas, srcRowId, null);
		M destModel = DBU.getModelById(sqlDbUrlMain, clas, srcRowId, null);
		NI.stop("wtf, row is same");
		if (srcModel == null) {
			throw new NullPointerException("Unknown model by row id :" + srcRowId);
		}

		for (Long destRowId : destRowIds) {
			((AModel) srcModel).setId(destRowId);
			destModel = srcModel;

			boolean resUpdate = DBU.updateModelQk(sqlDbUrlMain, destModel, OperDB.createOrUpdate);
			DBU.L.info("DBUtils:copy [%s], src[%s],dst[%s], db[%s],result[%s], update:", clas.getSimpleName(), srcRowId, destRowId, sqlDbUrlMain.getDbFile(), resUpdate);
		}
	}

	public static <M extends AModel> M getNextModelById(List<M> rows, long... lastId) {
		if (rows.isEmpty()) {
			return null;
		}
		long _lastId;
		if (rows.size() == 1 || ARGn.isNotDef(lastId) || ((_lastId = ARGn.toDef(lastId)) <= 0)) {
			return rows.get(0);
		}
		M mdlNext = null;
		for (M mdl : rows) {
			if (mdlNext != null) {
				mdlNext = mdl;
				break;
			}
			if (mdl.getId() == _lastId) {
				mdlNext = mdl;
			}
		}
		if (mdlNext == null || mdlNext.getId() == _lastId) {
			mdlNext = rows.get(0);
		}
		return mdlNext;
	}

	public static void renameAll(List<Path> ls, String srcTablename, String dstTablename) {
		ls.forEach(p -> {
			Ctx5Db mydb = Ctx5Db.of(p); //create db foo
			renameTablename(mydb.getDbUrl(), mydb.getModelClass(), srcTablename, dstTablename);
		});
	}
}