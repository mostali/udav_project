package udav_net.wrappercall;


import udav_net.ConnectionRefusedException;

public abstract class WrapperCallTooManyRedirectTc<T, E extends Exception> extends WrapperCallNetErrorTc<T, E> {

	public static final ToomanyRedirectRecallStrategy RECALLSTRATEGY_TOOMANYREDIRECT = new ToomanyRedirectRecallStrategy();

	public WrapperCallTooManyRedirectTc() {
		this(null, DEF_TC);
	}

	public WrapperCallTooManyRedirectTc(String callName, int tc) {
		super(callName, tc);
		getRecallStrategies().add(RECALLSTRATEGY_TOOMANYREDIRECT);

	}

	@Override
	protected boolean isRecallException(Throwable context) {
		return ConnectionRefusedException.isTooManyRedirectException(context) || super.isRecallException(context);
	}
}
