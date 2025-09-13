package zk_os.core;

import lombok.RequiredArgsConstructor;
import mpe.wthttp.core.INode;
import mpu.X;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.InjectNode;
import zk_notes.node_srv.TrackMap;

import java.nio.file.Path;

@RequiredArgsConstructor
public class NodeData implements INode<NodeDir> {
	public final NodeDir nodeDir;
	public final String nodeData;

	public static NodeData of(NodeDir nodeDir) {
		return new NodeData(nodeDir, null);
	}

	public static NodeData of(NodeDir nodeDir, String nodeData) {
		return new NodeData(nodeDir, nodeData);
	}

	public NodeData newInjected(TrackMap.TrackId track) {
		return X.empty(nodeData) ? InjectNode.inject(nodeDir, track) : this;
	}

	@Override
	public NodeDir toNode() {
		return nodeDir;
	}

	@Override
	public String toNodeData() {
		return nodeData;
	}

	@Override
	public String toNodeId() {
		return toNode().toNodeId();
	}

	@Override
	public Path toPath() {
		return toNode().toPath();
	}
}
