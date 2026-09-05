package mpt;

import mpu.X;
import mpc.exception.CleanMessageRuntimeException;
import mpc.exception.EException;
import mpc.exception.SimpleMessageRuntimeException;

public class TrmEE extends EException {


	/**
	 * *************************************************************
	 * ---------------------------- INIT --------------------------
	 * *************************************************************
	 */
	@Override
	public TrmEE.EE type() {
		return super.type(TrmEE.EE.class);
	}

	public enum EE {
		NOSTATUS, TRM_NOT_FOUND, TRMCMD_NOT_FOUND, TRMCMDS_EMPTY;

		public TrmEE I() {
			return new TrmEE(this);
		}

		public TrmEE I(Throwable ex) {
			return new TrmEE(this, ex);
		}

		public TrmEE I(Throwable ex, String msg, Object... args) {
			return new TrmEE(this, new SimpleMessageRuntimeException(ex, msg, args));
		}

		public TrmEE I(String message) {
			return new TrmEE(this, new SimpleMessageRuntimeException(message));
		}

		public TrmEE I(String message, Object... args) {
			return new TrmEE(this, new SimpleMessageRuntimeException(X.f(message, args)));
		}

		public TrmEE M(String message, Object... args) {
			return new TrmEE(this, new CleanMessageRuntimeException(X.f(message, args)));
		}
	}

	public TrmEE() {
		super(TrmEE.EE.NOSTATUS);
	}

	public TrmEE(TrmEE.EE error) {
		super(error);
	}

	public TrmEE(TrmEE.EE error, Throwable cause) {
		super(error, cause);
	}


}
