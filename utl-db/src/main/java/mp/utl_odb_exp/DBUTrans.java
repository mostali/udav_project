//package mp.utl_odb_exp;
//
//
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.dao.DaoManager;
//import com.j256.ormlite.jdbc.JdbcConnectionSource;
//import com.j256.ormlite.misc.TransactionManager;
//import com.j256.ormlite.support.ConnectionSource;
//import mp.url_ndb.SqlDbUrl;
//import mp.utl_db.DBU;
//import mp.utl_db.DbEE;
//import mp.utl_db.OperDB;
//import mp.utl_db.mdl.AModel;
//
//import java.sql.SQLException;
//import java.util.concurrent.Callable;
//
//public class DBUTrans {
//
//	public static void main(String[] args) {
//
//	}
//
//	//	static class DbTableOperation<M> extends AbstractDbOperation {
////
////		final EModifyTable operDb;
////
////		public DbTableOperation(SqlDbUrl dbUrl, Object data, EModifyTable operDb) {
////			super(dbUrl, data);
////			this.operDb = operDb;
////		}
////
////
////		protected Callable<Void> getCallable() {
////			return new Callable<Void>() {
////				@Override
////				public Void call() throws SQLException {
////					return null;
////				}
////			};
////		}
////	}
//	enum EDB {
//		countOf, create, createIfNotExists, update, all, createOrUpdate, REMOVE;
//
//		<M extends AModel> void trans(SqlDbUrl dbUrl, M data) throws Exception {
//			new DbTransaction(dbUrl, data, this).call();
//
//		}
//	}
//
//	public static class DbRemoveRowOperation<M extends AModel> extends DbTransaction<M> {
//
//		public DbRemoveRowOperation(SqlDbUrl dbUrl, M data) {
//			super(dbUrl, data, OperDB.REMOVE);
//		}
//
//		public static <M extends AModel> DbRemoveRowOperation create(SqlDbUrl dbUrl, M data) {
//			return new DbRemoveRowOperation(dbUrl, data);
//		}
//	}
//
//	public static class DbTransaction<M extends AModel> implements Callable<Void> {
//		final SqlDbUrl dbUrl;
//		final M data;
//		final Class<? extends AModel> classModel;
//		final Enum operDb;
//		boolean isIgnoreErrors = true;
//
//		public DbTransaction(SqlDbUrl dbUrl, M data, Enum operDb) {
//			this.dbUrl = dbUrl;
//			this.data = data;
//			this.classModel = data.getClass();
//			this.operDb = operDb;
//		}
//
//		public DbTransaction isIgnoreErrors(boolean isIgnoreErrors) {
//			this.isIgnoreErrors = isIgnoreErrors;
//			return this;
//		}
//
//		protected Dao dao = null;
//
//		public Dao getDAO() {
//			return dao;
//		}
//
//		public Dao setDAO(Dao dao) {
//			return dao;
//		}
//
//		protected Callable<Void> getCallable() {
//			return new Callable<Void>() {
//				@Override
//				public Void call() throws SQLException {
//					if (operDb instanceof OperDB) {
//						Dao dao = DaoManager.createDao(getConnectionSource(), classModel);
//						DbTransaction.this.setDAO(dao);
//						DBU.updateModel(dao, data, OperDB.valueOf(operDb.name()));
//					} else if (operDb instanceof DBU.EModifyTable) {
//						DBU.modifyTable(getConnectionSource(), classModel, (DBU.EModifyTable) operDb, isIgnoreErrors);
//					}
//					return null;
//				}
//			};
//		}
//
//		private ConnectionSource connectionSource = null;
//
//		public ConnectionSource getConnectionSource() {
//			return connectionSource;
//		}
//
//		@Override
//		public Void call() throws Exception {
//			Class<M> classModel = (Class<M>) data.getClass();
//			try {
//				Callable<Void> callable = null;
//				try {
//					this.connectionSource = new JdbcConnectionSource(dbUrl.getJdbcUrl());
//					callable = getCallable();
//					TransactionManager.callInTransaction(this.connectionSource, callable);
//				} finally {
//					DBU.closeDaoAndConnection(connectionSource, getDAO());
//				}
//			} catch (SQLException ex) {
//				throw DbEE.EE.IO_ERROR.I(ex);
//			}
//			return null;
//		}
//
//	}
//
//
//}
