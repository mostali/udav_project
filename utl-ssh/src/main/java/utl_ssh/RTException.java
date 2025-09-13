package utl_ssh;

import mpc.exception.EException;

public class RTException extends EException {
	public enum EErrors {
		NOSTATUS, TIMEOUTCONNECT;

		public void ON() throws RTException {
			throw I();
		}

		public RTException I() {
			return new RTException(this);
		}

		public RTException I(Exception ex) {
			return new RTException(this, ex);
		}
	}

	private static final long serialVersionUID = 1L;

	public RTException(EErrors error) {
		super(error);
	}

	public RTException(EErrors error, Throwable cause) {
		super(error, cause);
	}

	public static boolean isChannelNotOpen(Exception e) {
		if (_isChannelNotOpen(e)) {
			return true;
		} else if (e instanceof EException) {
			if (_isChannelNotOpen( e.getCause())) {
				return true;
			}
		}
		return false;
	}

	private static boolean _isChannelNotOpen(Throwable e) {
		return (e instanceof com.jcraft.jsch.JSchException && e.getMessage().contains("channel is not opened"));
	}
}
