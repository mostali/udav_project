package mpe.cmsg.ns;

import lombok.RequiredArgsConstructor;
import mpc.env.APP;

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

	default String canonicId() {
		return NodeID.toCanonicId(this);
	}

	@RequiredArgsConstructor
	class UrlTo {
		final INodeID nodeID;

		public String toItemCan() {
			return APP.HOST.getAppUrlWithPlaneAndPath(nodeID.spaceName(), nodeID.pageName(), nodeID.nodeName());
		}
	}

	default UrlTo urlTo() {
		return new UrlTo(this);
	}
}
