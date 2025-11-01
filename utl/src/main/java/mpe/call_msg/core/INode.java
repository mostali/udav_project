package mpe.call_msg.core;

import mpc.fs.path.IPath;
import mpe.call_msg.injector.NodeData;
import mpe.call_msg.injector.TrackMap;
import mpu.core.ARG;

public interface INode<T> extends IPath {

	String nodeName();

	T toNodeImpl();

	String readNodeDataStr();

	String toNodeId();

	default NodeData inject(boolean... fresh) {
		return inject(null, ARG.isDefEqTrue(fresh));
	}

	default NodeData inject(TrackMap.TrackId trackId, boolean... fresh) {
		return inject(trackId, false);
	}

	default NodeData inject(TrackMap.TrackId trackId, boolean fresh) {
//		return NodeData.injectorSrv.apply(toNode(), trackId);

		INode node = (INode) toNodeImpl();
//		if (fresh) {
//			return NodeData.injectorSrv.doInject(node, trackId);
//		}

		if (node instanceof NodeData) {
			NodeData nodeData = (NodeData) node;
			if (!fresh) {
				return nodeData;
			}
			node = nodeData.nodeDir;
//			if (nodeData.hasInjectedData()) {
//				if (ARG.isDefEqTrue(fresh)) {
//					//ok - go new inject
//				} else {
//					return nodeData;
//				}
//			}
//			node = nodeData.nodeDir;
		}
		return NodeData.injectorSrv.doInject(node, trackId);
	}

}
