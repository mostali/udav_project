package mpe.call_msg.core;

public interface INodeID extends IPageID {

//	Pare<String, String> sdn();

	String nodeName();

	String toObjId();

	@Override
	default String pageName() {
		return sdn().val();
	}

	@Override
	default String spaceName() {
		return sdn().key();
	}
}
