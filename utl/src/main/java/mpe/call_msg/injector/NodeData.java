package mpe.call_msg.injector;

import lombok.Getter;
import mpe.call_msg.CallMsg;
import mpe.call_msg.core.INode;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;

import java.nio.file.Path;

public class NodeData<N extends INode> implements INode<N> {

	public static FuncInjector injectorSrv = null;

	public interface FuncInjector {
		NodeData doInject(INode node, TrackMap.TrackId track);
	}

	//
	//

	public static <N extends INode> NodeData<N> of(INode<N> nodeDir, String nodeDataStr, TrackMap.TrackId trackId) {
		return new NodeData(nodeDir, nodeDataStr, trackId);
	}

	//
	//

	public final N nodeDir;

	@Deprecated //use method
	public final String nodeDataStr;

	private final @Getter TrackMap.TrackId trackId;

	public NodeData(N nodeDir, String nodeDataStr, TrackMap.TrackId trackId) {
		this.nodeDir = nodeDir;
		this.nodeDataStr = IT.NN(nodeDataStr, "except data");
		this.trackId = trackId;
	}

	//
	//

	@Override
	public Pare<String, String> sdn() {
		return toNodeImpl().sdn();
	}

	@Override
	public String nodeName() {
		return toNodeImpl().nodeName();
	}

	@Override
	public N toNodeImpl() {
		return nodeDir;
	}

	@Override
	public String readNodeDataStr() {
		return nodeDataStr;
	}

	@Override
	public String toObjId() {
		return toNodeImpl().toObjId();
	}

	@Override
	public Path toPath() {
		return toNodeImpl().toPath();
	}

	public String nodeDataStr() {
		return nodeDataStr;
	}

	private CallMsg callMsg;

	public void setCallMsg(CallMsg callMsg) {
		this.callMsg = callMsg;
	}

	public <T extends CallMsg> T getCallMsg(T... defRq) {
		return callMsg != null ? (T) callMsg : ARG.toDefThrowMsg(() -> X.f("Except callMsg impl"), defRq);
	}


}
