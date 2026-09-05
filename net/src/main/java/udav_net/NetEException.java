package udav_net;

import mpc.exception.EException;

public class NetEException extends EException {
	public enum EError {
		NOSTATUS, SOCKET_TIMEOUT, REFUSED_CONNECTION;

		public NetEException I() {
			return new NetEException(this);
		}

		public NetEException I(Throwable ex) {
			NetEException er = new NetEException(this, ex);
			return er;
		}

		public NetEException I(String message) {
			NetEException er = new NetEException(this, new RuntimeException(
					message));
			return er;
		}
	}

	public NetEException(EError error) {
		super(error);
	}

	public NetEException(EError error, Throwable cause) {
		super(error, cause);
	}

	public static EError getTypeExceptionOf(Exception ex) {
		if (ex instanceof java.net.SocketTimeoutException) {
			return EError.SOCKET_TIMEOUT;
		} else if (ex instanceof java.net.ConnectException) {
			if (ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
				return EError.REFUSED_CONNECTION;
			}
		}
		return EError.NOSTATUS;
	}
}
