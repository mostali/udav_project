package mpc.net;


public abstract class AbsNetRsp<B, E> implements INetRsp<B, E> {

	@Override
	public String toString() {
		return INetRsp.toString(this);
	}

}
