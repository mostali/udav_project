package zk_notes.std_actions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.map.MAP;
import mpe.cmsg.core.INodeType;
import mpe.cmsg.core.StdType;
import mpu.func.FunctionIO;
import mpu.pare.Pare3;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.NodeEvalAction;
import zk_notes.node_srv.core.InAction;
import zk_notes.node_srv.core.OutAction;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class WebNodeAction {

	final @Getter NodeDir nodeDir;

	public static WebNodeAction of(NodeDir node) {
		return new WebNodeAction(node);
	}

	public <T> Pare3<NodeDir, Optional<T>, Object> doAction(Integer keys) {
		INodeType nodeEvalType = nodeDir.evalType(StdType.NODE);
		Map webContext = MAP.of("keys", keys);
		FunctionIO<NodeDir, Object> in = InAction.of(nodeEvalType).in(null);
		FunctionIO out = OutAction.of(nodeEvalType).out(nodeDir, webContext);
		return NodeEvalAction.doEvalNodeAction(nodeDir, in, out);
	}
}
