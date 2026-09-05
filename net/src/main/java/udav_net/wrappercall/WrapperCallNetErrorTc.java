package udav_net.wrappercall;

import udav_net.ConnectionRefusedException;

import java.io.IOException;
import java.net.SocketTimeoutException;

public abstract class WrapperCallNetErrorTc<T, E extends Exception> extends WrapperCallAnyTcAbstract<T, E> {

	public static final RefusedConnectionRecallStrategy RECALLSTRATEGY_REFUSEDCONNECTION = new RefusedConnectionRecallStrategy();
	public static final SocketTimeoutRecallStrategy RECALLSTRATEGY_SOCKETTIMEOUT = new SocketTimeoutRecallStrategy();

	public WrapperCallNetErrorTc() {
		this(null, DEF_TC);
	}

	public WrapperCallNetErrorTc(String callName, int tc) {
		super(callName, tc);
		getRecallStrategies().add(RECALLSTRATEGY_REFUSEDCONNECTION);
		getRecallStrategies().add(RECALLSTRATEGY_SOCKETTIMEOUT);
	}

	@Override
	protected boolean isRecallException(Throwable context) {
		if (context == null) {
			return false;
		}
		if (!(context instanceof IOException)) {
			return false;
		}
		IOException ex = (IOException) context;
		return ConnectionRefusedException.isConnectionRefusedException(ex);
	}

	public static class RefusedConnectionRecallStrategy extends DefaultRecallStrategy {

		@Override
		public boolean isRecallException(Throwable error) {
			if (super.isRecallException(error)) {
				return true;
			}
			if (!(error instanceof IOException)) {
				return false;
			}
			IOException ex = (IOException) error;
			return ConnectionRefusedException.isConnectionRefusedException(ex);
		}


	}

	public static class ToomanyRedirectRecallStrategy extends DefaultRecallStrategy {

		@Override
		public boolean isRecallException(Throwable error) {
			if (super.isRecallException(error)) {
				return true;
			}
			if (!(error instanceof IOException)) {
				return false;
			}
			IOException ex = (IOException) error;
			boolean tooManyRedirectException = ConnectionRefusedException.isTooManyRedirectException(ex);
			return tooManyRedirectException;
		}


	}

	public static class SocketTimeoutRecallStrategy extends DefaultRecallStrategy {

		@Override
		public boolean isRecallException(Throwable error) {
			return super.isRecallException(error) || error instanceof SocketTimeoutException;
		}

	}
}
