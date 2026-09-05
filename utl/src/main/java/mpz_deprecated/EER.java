package mpz_deprecated;

import mpu.Sys;
import mpe.core.U;
import mpu.core.ARR;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Deprecated
public enum EER {
	RT(RuntimeException.class), RIO(IOException.class),
	// NP(
	// NullPointerException.class),
	IS(IllegalStateException.class), IA(IllegalArgumentException.class);

	public static RuntimeException RT(Exception ex) {
		return RT.I(ex);
	}

	public static Exception IE(String message) {
		return new Exception(message);
	}

	public static RuntimeException IS(Throwable ex) {
		return IS.I(ex);
	}

	public static RuntimeException IA(Throwable ex) {
		return IA.I(ex);
	}

	public static IllegalArgumentException IA(String message) {
		return (IllegalArgumentException) IA.I(message);
	}

	public static IllegalStateException IS(String message) {
		return (IllegalStateException) IS.I(message);
	}

	public static RuntimeException NEEDIMPL(String... message) {
		return IS(ARR.defIfNull("Need impl", message));
	}


	public static RuntimeException WRONGLOGIC(String... message) {
		return IS(ARR.defIfNull("Wrong logic", message));
	}

	public static String getMessages(String message, Throwable throwable) {
		String m = message + " >*> " + throwable.getMessage();
		if (throwable.getCause() == null) {
			return m;
		}
		return getMessages(m, throwable.getCause());
	}

	public final Class<? extends Exception> classEx;

	private EER(Class<? extends Exception> ex) {
		this.classEx = ex;
	}

	public static String getCommonMessageFromThrowables(Throwable throwable) {
		return U.multiMessageWithDel("\n-->", EER.getAllMessagesFromThrowable(throwable).toArray());
	}

	public static List<String> getAllMessagesFromThrowable(Throwable throwable) {
		List<String> result = new ArrayList<String>();
		while (throwable != null) {
			result.add(throwable.getMessage());
			throwable = throwable.getCause();
		}
		return result; //["THIRD EXCEPTION", "SECOND EXCEPTION", "FIRST EXCEPTION"]
	}

	public RuntimeException I(Throwable e) {

		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}

		switch (this) {
			// case RT:
			case RIO:
				return e instanceof RIOException ? (RIOException) e : new RIOException(e);
			// case NP:
			// throw new NullPointerException(e);
			case IS:
				return e instanceof IllegalStateException ? (IllegalStateException) e : new IllegalStateException(e);
			case IA:
				return e instanceof IllegalArgumentException ? (IllegalArgumentException) e
						: new IllegalArgumentException(e);
			default:
				return new RuntimeException(e);
		}
	}

	public RuntimeException I(String m) {

		switch (this) {
			// case RT:
			case RIO:
				throw new RIOException(m);
				// case NP:
				// throw new NullPointerException(m);
			case IS:
				throw new IllegalStateException(m);
			case IA:
				throw new IllegalArgumentException(m);
			default:
				return new RuntimeException(m);
		}
	}


	public static class RIOException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public RIOException() {
			super();
		}

		public RIOException(String message) {
			super(message);
		}

		public RIOException(Throwable cause) {
			super(cause);
		}

		public RIOException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	public static Throwable getInnerInstance(Exception ex, Class classException) {
		Throwable t = null;
		try {
			do {
				t = ex.getCause();
				if (t == null) {
					return null;
				}
				if (t.getClass().isAssignableFrom(classException)) {
					return t.getCause();
				}
			} while (true);
		} catch (Exception e) {
			return null;
		}
	}

	public static NullPointerException NP(String... message) {
		return message.length == 0 ? new NullPointerException() : new NullPointerException(message[0]);
	}

	public static boolean isFileNotFound(Exception e, Boolean... checkInnerException) {
		if (checkInnerException == null || checkInnerException.length == 0) {
			return e != null && e instanceof FileNotFoundException;
		} else {
			Throwable ei = e;
			do {
				if (ei instanceof FileNotFoundException) {
					return true;
				} else {
					ei = e.getCause();
				}
			} while (ei != null);
			return false;
		}
	}

	public static void p(Exception e) {
		if (e == null) {
			Sys.e("Exception is null");
		} else {
			Sys.e("Exception::" + e.getClass() + "::" + e.getMessage());
		}
	}
}
