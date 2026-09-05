package langj;

import mpu.X;

public class PtException extends RuntimeException {
	//	public final CodePt codePt;
	public final String code;

	public PtException(String code, String message, Object... args) {
		super(X.f(message, args));
		this.code = code;
//		this.codePt = null;
	}

	public PtException(String code, Exception ex) {
		super(ex);
		this.code = code;
//		this.codePt = null;
	}

	public PtException(String code, Exception ex, String message, Object... args) {
		super(new WrapRuntimeException(ex, message, args));
		this.code = code;
//		this.codePt = null;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + "\n>>>" + code;
	}


	public static class WrapRuntimeException extends RuntimeException {
		public WrapRuntimeException() {
			super();
		}

		public WrapRuntimeException(String message) {
			super(message);
		}

		public WrapRuntimeException(Enum message) {
			super(message.name());
		}

		public WrapRuntimeException(String message, Object... args) {
			super(String.format(message, args));
		}

		public WrapRuntimeException(Throwable throwable, String message) {
			super(message, throwable);
		}

		public WrapRuntimeException(Throwable throwable, String message, Object... args) {
			this(throwable, String.format(message, args));
		}

	}


}
