package mpe.cmsg.core;

import mpc.fs.path.IPath;
import mpe.cmsg.NodeData;
import mpe.cmsg.TrackMap;
import mpe.cmsg.ns.INodeID;
import mpe.cmsg.srv.INodeInjector;
import mpu.X;
import mpu.core.ARG;

import java.util.function.Supplier;

public interface INode<T> extends IPath, INodeID {

	T toNodeImpl();

	String readNodeDataStr(String... defRq);

	String line0(String... defRq);

	default NodeData inject(boolean... fresh) {
		return inject(null, ARG.isDefEqTrue(fresh));
	}

	default NodeData inject(TrackMap.TrackId trackId, boolean... fresh) {
		return inject(trackId, false);
	}

	default NodeData inject(TrackMap.TrackId trackId, boolean fresh) {
		INode node = (INode) toNodeImpl();
		if (node instanceof NodeData) {
			NodeData nodeData = (NodeData) node;
			if (!fresh) {
				return nodeData;
			}
			node = nodeData.nodeDir;
		}
//		if (InjectSrvSys.injectorSrvUse == null) {
////			NI.stop("set injectorSrv");
//			L.error("set injectorSrv", new FIllegalStateException());
//			return new NodeData(node, node.readNodeDataStr(), trackId);
//		}
		return INodeInjector.get().doInject(node, trackId);
	}

	default <T extends ICallMsg> T newInstanceCallMsgValid(T... defRq) {
		String data = readNodeDataStr(null);
		if (data != null) {
			INodeType iNodeType = defineNodeType(true, null);
			if (iNodeType != null) {
				ICallMsg iCallMsg = iNodeType.stdDesc().newInstanceCallMsgValid(this, data, null);
				if (iCallMsg != null) {
					return (T) iCallMsg;
				}
			}
		}
		Supplier<String> errMsg = () -> X.f("Node '%s' except valid CallMsg", toObjId());
		return ARG.throwMsg(errMsg, defRq);

	}

	default INodeType defineNodeType(boolean strictValid, INodeType... defRq) {
		return INodeType.defineNodeType(this, strictValid, defRq);
	}
}
