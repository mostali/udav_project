package mp.utl_odb.typedb;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.table.TableUtils;
import lombok.SneakyThrows;
import mpe.db.JdbcUrl;
import mp.utl_odb.DBUBase;
import mp.utl_odb.mdl.ext_um2.UniModel;
import mpu.core.ARG;
import mpu.core.ARR;
import mpc.exception.ERxception;
import mpc.exception.FNullPointerException;
import mpc.exception.WrongLogicRuntimeException;
import mpc.fs.Ns;
import mpc.rfl.FindFieldsPredicate;
import mpc.rfl.RFL;
import mpu.str.UST;
import mp.utl_odb.mdl.AModel;
import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class TypeDbEE extends ERxception {

	private static final long serialVersionUID = 1L;

	public static final Logger L = LoggerFactory.getLogger(TypeDbEE.class);

	public static final Cache<Class, TypeDb> _CACHE_DB = CacheBuilder.newBuilder().build();//softValues()

	@Deprecated
	public static <M extends AModel> TypeDb<M> regDb(Class<M> mdlClass, Path root, String app_store, String dbName, boolean createDbIfNotExist) {
		return regDb(mdlClass, root.resolve(app_store).resolve(JdbcUrl.buildDbFileName(dbName)), createDbIfNotExist);
	}

	@Deprecated
	public static <M extends AModel> TypeDb<M> regDb(Class<M> mdlClass, String file, boolean createDbIfNotExist) {
		return regDb(mdlClass, Paths.get(file), createDbIfNotExist);
	}

	public static <M extends AModel> TypeDb<M> regDb(Class<? extends AModel> existMdlClass, Class<M> newMdlClass, boolean createDbIfNotExist) {
		TypeDb db = getDbEE(existMdlClass);
		return regDb(newMdlClass, db.getNs(), db.getNamedDbUrl().getDbName(), createDbIfNotExist);
	}

	public static <M extends AModel> TypeDb<M> regDb(Class<M> mdlClass, Ns app_ns, String dbName, boolean createDbIfNotExist) {
		TypeDb<M> tdb = TypeDb.of(mdlClass, app_ns, dbName, createDbIfNotExist);
		putDbInCache(mdlClass, tdb);
		return tdb;
	}

	public static <M extends AModel> TypeDb<M> regDb(TypeDb<M> typeDb) {
		putDbInCache(typeDb.getClassModel(), typeDb);
		return typeDb;
	}

	public static <M extends AModel> TypeDb<M> regDb(Class<M> mdlClass, Path file, boolean createDbIfNotExist) {
		TypeDb<M> tdb = TypeDb.of(mdlClass, file, createDbIfNotExist);
		putDbInCache(mdlClass, tdb);
		return tdb;
	}

	public static <M extends AModel> void putDbInCache(Class<M> mdlClass, TypeDb<M> tdb) {
		if (L.isInfoEnabled()) {
			L.info("TypeDbEE:REG: '{}' db 'file://{}'", mdlClass.getName(), tdb.getNamedDbUrl().getDbFile());
		}
		_CACHE_DB.put(mdlClass, tdb);
	}

	public static <M extends AModel> TypeDb<M> createDb(Class<M> mdlClass) {
		TypeDb<M> db = getDbEE(mdlClass);
		db.checkOrCreateDb(true);
		return db;
	}

	public static <M extends AModel> TypeDb<M> getDbEE(Class<M> mdlClass) {
		TypeDb db = _CACHE_DB.getIfPresent(mdlClass);
		if (db == null) {
			throw new FNullPointerException("TypeDbEE not found by model:" + mdlClass);
		}
		return db;
	}

	@Deprecated
	public static TypeDb findDbByClass(String className) {
		Class clazz = RFL.clazz(className);
		TypeDb db = findDbByClass(clazz);
		return db;
	}

	@SneakyThrows
	public static TypeDb findDbByClass(Class clazz, TypeDb... defRq) {
		TypeDb ifPresent = _CACHE_DB.getIfPresent(clazz);
		if (ifPresent != null) {
			return ifPresent;
		}
		ifPresent = findDbFromClass_(clazz, null);
		if (ifPresent != null) {
			_CACHE_DB.put(clazz, ifPresent);
			return ifPresent;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw EE.DB_NOT_FOUND.I(clazz.getSimpleName());
	}

	public static TypeDb findDbFromClass_(Class clazz, TypeDb... defRq) {
		List<Field> dbsWithoutAno = new ArrayList<>();
		FindFieldsPredicate ffp = new FindFieldsPredicate(true, field -> {
			boolean assignableFrom = field.getType().isAssignableFrom(TypeDb.class);
			if (!assignableFrom) {
				return false;
			}
			TypeDbAno ano = field.getAnnotation(TypeDbAno.class);
			if (ano != null && ano.main()) {
				return true;
			}
			dbsWithoutAno.add(field);
			return false;
		});
		List<TypeDb> dbsMain = RFL.fieldValues(clazz, null, ffp, true, Collections.EMPTY_LIST);
		TypeDb typeDb = choiceDb(clazz, dbsMain, dbsWithoutAno, defRq);
		return typeDb;
	}


	@SneakyThrows
	private static TypeDb choiceDb(Class clazz, List<TypeDb> dbsMain, List<Field> fieldsDbWithoutAno, TypeDb... defRq) {
		switch (dbsMain.size()) {
			case 0:
				break;
			case 1:
				return dbsMain.get(0);
			default:
				throw new WrongLogicRuntimeException("Too many main db's:" + dbsMain.size() + ":" + dbsMain.stream().map(db -> db.getNamedDbUrl().getDbFile()).collect(Collectors.joining(", ")));

		}
		if (fieldsDbWithoutAno.size() == 1) {
			return (TypeDb) fieldsDbWithoutAno.get(0).get(clazz);
		}
		for (Field dbMain : fieldsDbWithoutAno) {
			if (UniModel.FN_DB.equals(dbMain.getName())) {
				return (TypeDb) fieldsDbWithoutAno.get(0).get(clazz);
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw EE.DB_NOT_FOUND.I(clazz.getSimpleName());
	}

	/**
	 * *************************************************************
	 * ---------------------------- INIT --------------------------
	 * *************************************************************
	 */

	public enum EE {
		NOSTATUS, MODEL_EXISTS, DB_NOT_FOUND;

		public TypeDbEE I() {
			return new TypeDbEE(this);
		}

		public TypeDbEE I(Throwable ex) {
			TypeDbEE er = new TypeDbEE(this, ex);
			return er;
		}

		public TypeDbEE I(String message) {
			TypeDbEE er = new TypeDbEE(this, new RuntimeException(message));
			return er;
		}

		public TypeDbEE I(String message, Object... args) {
			TypeDbEE er = new TypeDbEE(this, new RuntimeException(X.f(message, args)));
			return er;
		}

		public TypeDbEE I(Throwable ex, String message, Object... args) {
			TypeDbEE er = new TypeDbEE(this, new RuntimeException(X.f(message, args), ex));
			return er;
		}
	}

	public TypeDbEE() {
		super(EE.NOSTATUS);
	}

	public TypeDbEE(EE error) {
		super(error);
	}

	public TypeDbEE(EE error, Throwable cause) {
		super(error, cause);
	}


	@Retention(RetentionPolicy.RUNTIME)
	public @interface TypeDbAno {
		boolean main() default false;
	}

	public static class UpdaterDb<M extends AModel> {
		final Class<M> classModel;
		final String jdbcUrl;

		boolean createOrUpdateModel;
		boolean truncateTableBeforeWrite = true;

		boolean useTransactionOrBatch = true;

		public UpdaterDb(Class<M> classModel, String jdbcUrl) {
			this.classModel = classModel;
			this.jdbcUrl = jdbcUrl;
		}

		//
		//
		private JdbcConnectionSource connectionSource;
		private Dao<M, ?> dao;

		public void setAllRows(List<String> headOrg, List<List<Object>> rows) throws Exception {

			try {

				connectionSource = new JdbcConnectionSource(jdbcUrl);
				dao = DaoManager.createDao(connectionSource, classModel);

				Callable call2db = new Callable<Void>() {
					public Void call() throws SQLException {

						if (truncateTableBeforeWrite) {
							TableUtils.clearTable(connectionSource, classModel);
							if (TypeDb.L.isInfoEnabled()) {
								TypeDb.L.info("Db [{}] was truncated success", jdbcUrl);
							}
						}

						setAllRows(dao, headOrg, rows);
						if (TypeDb.L.isInfoEnabled()) {
							TypeDb.L.info("Db [{}] was updated success", jdbcUrl);
						}
						return null;
					}
				};

				if (useTransactionOrBatch) {
					TransactionManager.callInTransaction(connectionSource, call2db);
				} else {
					dao.callBatchTasks(call2db);
				}
			} finally {
				DBUBase.closeDaoAndConnection(connectionSource, dao);
			}
		}

		private void setAllRows(Dao<M, ?> dao, List<String> headOrg, List<List<Object>> rows) throws SQLException {
			List<Object> headIncomming = ARR.cutHeadRow(rows);
			List<Integer> twins = ARR.getIndexesOfDublicates(headIncomming, headOrg, true);
			Map<String, Field> cache = new HashMap<>();
			int i = 0;
			for (List<Object> row : rows) {
				M model = (M) RFL.instEmptyConstructor(classModel);
				endTwins:
				for (Integer ind : twins) {
					int index = ind.intValue();
					if (index >= row.size()) {
						break endTwins;
					}
					Object key = headIncomming.get(index);
					Object val = index <= row.size() ? row.get(index) : null;
					Field field = cache.get(key);
					String keyStr = (String) key;
					if (field == null) {
						//TODO Wtf? here was fieldSt !!!!!
						field = RFL.field(classModel, keyStr, true, true);
						cache.put(keyStr, field);
					}
					try {
						String valStr = (String) val;
						Object str2type;
						if (X.empty(valStr)) {
							str2type = null;
						} else {
							str2type = UST.strTo(valStr, field.getType());
						}
						RFL.write(model, keyStr, str2type, true, true);
					} catch (Exception ex) {
						throw new IllegalStateException(X.f("Error str2type. Filed type [%s]. Value [%s]", field.getType(), val), ex);
					}
				}
				dao.create(model);
			}
		}
	}
}

