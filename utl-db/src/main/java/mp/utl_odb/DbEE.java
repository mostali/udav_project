package mp.utl_odb;

import mpc.exception.ERxception;
import mpc.exception.SimpleMessageRuntimeException;

public class DbEE extends ERxception {
	private static final long serialVersionUID = 1L;
	public static final int DB_NOT_EXIST = 1;
	public static final int CREATE_DB_IF_NOT_EXIST = 2;

	public enum EE {
		NOSTATUS, DB_NOT_EXIST, CREATE_DB_IF_NOT_EXIST,
		UNSUPPORTED_OPER, IO_ERROR, SQL_ERROR, GET_COL_VALUE,
		UPDATE_ROW_NOT_EXIST, CREATE_ROW_WITH_EXIST_ID, REMOVE_MODEL, DB_LOCKED;

		public DbEE I() {
			return new DbEE(this);
		}

		public DbEE I(Throwable ex, String message) {
			return new DbEE(this, new SimpleMessageRuntimeException(ex, message));
		}

		public DbEE I(Throwable ex) {
			return new DbEE(this, ex);
		}

		public DbEE I(String message) {
			return new DbEE(this, new SimpleMessageRuntimeException(message));
		}
	}

	public DbEE(EE error) {
		super(error);
	}

	public DbEE(EE error, Throwable cause) {
		super(error, cause);
	}

}
