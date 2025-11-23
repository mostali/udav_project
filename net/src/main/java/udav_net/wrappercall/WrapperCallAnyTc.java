package udav_net.wrappercall;

public abstract class WrapperCallAnyTc<R, E extends Exception> extends WrapperCallAnyTcAbstract<R, E> {

	public WrapperCallAnyTc() {
		this(null, DEF_TC);
	}

	public WrapperCallAnyTc(String callName, int tc) {
		super(callName, tc);
	}

}
