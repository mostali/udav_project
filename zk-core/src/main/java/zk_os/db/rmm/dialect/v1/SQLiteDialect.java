//package zkbae.db.dialect.v1;
//
//import org.springframework.data.relational.core.dialect.AbstractDialect;
//import org.springframework.data.relational.core.dialect.ArrayColumns;
//import org.springframework.data.relational.core.dialect.H2Dialect;
//import org.springframework.data.relational.core.dialect.LimitClause;
//import org.springframework.data.relational.core.dialect.LockClause;
//import org.springframework.data.relational.core.sql.LockOptions;
//
//public class SQLiteDialect extends AbstractDialect {
//
//	public static final SQLiteDialect INSTANCE = new SQLiteDialect();
//
//	private static final LimitClause LIMIT_CLAUSE = new LimitClause() {
//
//		@Override
//		public String getLimit(long limit) {
//			return "LIMIT " + limit;
//		}
//
//		@Override
//		public String getOffset(long offset) {
//			return String.format("LIMIT %d, 18446744073709551615", offset);
//		}
//
//		@Override
//		public String getLimitOffset(long limit, long offset) {
//
//			return String.format("LIMIT %s, %s", offset, limit);
//		}
//
//		@Override
//		public Position getClausePosition() {
//			return Position.AFTER_ORDER_BY;
//		}
//	};
//
//	@Override
//	public LockClause lock() {
//		return LOCK_CLAUSE;
//	}
//
//	@Override
//	public ArrayColumns getArraySupport() {
//		return ArrayColumns.Unsupported.INSTANCE;
//	}
//
//	private static final LockClause LOCK_CLAUSE = new LockClause() {
//
//		@Override
//		public String getLock(LockOptions lockOptions) {
//			return "WITH LOCK";
//		}
//
//		@Override
//		public Position getClausePosition() {
//			return Position.AFTER_ORDER_BY;
//		}
//	};
//
//	@Override
//	public LimitClause limit() {
//		return LIMIT_CLAUSE;
//	}
//}
//
