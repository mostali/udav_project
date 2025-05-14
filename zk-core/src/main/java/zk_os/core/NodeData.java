package zk_os.core;

import lombok.RequiredArgsConstructor;
import mpu.X;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.InjectNode;
import zk_notes.node_srv.TrackMap;

@RequiredArgsConstructor
public class NodeData {
	public final NodeDir nodeDir;
	public final String nodeData;

	public static NodeData of(NodeDir nodeDir) {
		return new NodeData(nodeDir, null);
	}

	public static NodeData of(NodeDir nodeDir, String nodeData) {
		return new NodeData(nodeDir, nodeData);
	}

	public NodeData fillIfEmpty(TrackMap.TrackId track) {
		if (X.empty(nodeData)) {
			return InjectNode.inject(nodeDir, track);
		}
		return this;
	}
}
