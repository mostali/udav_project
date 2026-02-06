//package mp.utl_odb_exp;
//
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.dao.DaoManager;
//import com.j256.ormlite.jdbc.JdbcConnectionSource;
//import com.j256.ormlite.misc.TransactionManager;
//import com.j256.ormlite.support.ConnectionSource;
//import mp.utl_odb.DBU;
//import mp.utl_odb.typedb.TypeDb;
//import mp.utl_odb.mdl.AModel;
//
//import java.sql.SQLException;
//import java.util.concurrent.Callable;
//
//public abstract class SimpleDbTransaction<R> implements Callable<R> {
//	final TypeDb typedDb;
//	boolean isIgnoreErrors = true;
//
//	final Class<AModel> aModel;
//
//	public SimpleDbTransaction(TypeDb typedDb) {
//		this.typedDb = typedDb;
//		this.aModel = typedDb.getClassModel();
//	}
//
////		public DbTransaction isIgnoreErrors(boolean isIgnoreErrors) {
////			this.isIgnoreErrors = isIgnoreErrors;
////			return this;
////		}
//
//	protected Dao dao = null;
//
//	public Dao getDAO() {
//		return dao;
//	}
//
//	public Dao setDAO(Dao dao) {
//		return dao;
//	}
//
//	public abstract R callInTransaction(Dao dao, TypeDb typedDb) throws Exception;
//
//	protected Callable<R> getCallable() {
//		return new Callable<R>() {
//			@Override
//			public R call() throws Exception {
//				Dao dao = null;
//				dao = DaoManager.createDao(getConnectionSource(), typedDb.getClassModel());
//				SimpleDbTransaction.this.setDAO(dao);
//				return callInTransaction(dao, typedDb);
//			}
//		};
//	}
//
//	private ConnectionSource connectionSource = null;
//
//	public ConnectionSource getConnectionSource() {
//		return connectionSource;
//	}
//
//	@Override
//	public R call() throws Exception {
//		try {
////				Callable<R> callable = null;
//			try {
//				this.connectionSource = new JdbcConnectionSource(typedDb.getNamedDbUrl().getJdbcUrl());
////					callable = ;
//				return TransactionManager.callInTransaction(this.connectionSource, getCallable());
//			} finally {
//				DBU.closeDaoAndConnection(connectionSource, getDAO());
//			}
//		} catch (SQLException ex) {
////				throw DbError.EErrors.IO_ERROR.I(ex);
//			throw ex;
//		}
////			return null;
//	}
//
//}
