package mpe.cmsg.srv;

import mpc.exception.WhatIsTypeException;
import mpe.cmsg.FileNode;
import mpe.cmsg.NodeData;
import mpe.cmsg.TrackMap;
import mpe.cmsg.core.CallMsg;
import mpe.cmsg.core.INode;
import mpu.IT;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public interface INodeInjector {

	StdNodeInjector DEFAULT = new StdNodeInjector();

	AtomicReference<INodeInjector> USE = new AtomicReference(StdNodeInjector.DEFAULT);

	static INodeInjector get() {
		return USE.get();
	}

	static void set(INodeInjector nodeInjector) {
		USE.set(nodeInjector);
	}

	default NodeData doInject(CallMsg callMsg, TrackMap.TrackId track) {
		INode fromSrc = IT.isType0(callMsg.getFromSrc(), INode.class, "Except CallMsg with fromSource as INode");
		NodeData nodeData = doInject(fromSrc, track);
		nodeData.setCallMsg(callMsg);
		return nodeData;
	}

	NodeData doInject(INode node, TrackMap.TrackId track);

	default NodeData doInject(Path node, TrackMap.TrackId track) {
		return doInject(new FileNode(node), track);
	}

	default NodeData doInjectAny(Object node, TrackMap.TrackId track) {
		if (node instanceof CallMsg) {
			return doInject((CallMsg) node, track);
		} else if (node instanceof INode) {
			return doInject((INode) node, track);
		} else if (node instanceof Path) {
			return doInject((Path) node, track);
		}
		throw new WhatIsTypeException("Except legal inject typeof [INode|Path] of object [%s]", node);
	}

}
