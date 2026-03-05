package zk_notes.node_srv;

import mpe.cmsg.core.INodeTypeProps;
import mpe.cmsg.core.INodeType;
import mpu.func.FunctionIO;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import zk_com.base.Ln;
import zk_com.base_ctr.Span0;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.core.InAction;
import zk_notes.node_srv.core.OutAction;

public class PlayContainer extends Span0 {
	final PlayLn playLn;

	public PlayContainer(Component... playLn) {
		super(playLn);
		this.playLn = (PlayLn) playLn[0];
	}

	public static class PlayLn extends Ln {
		public final NodeDir node;

		public final INodeType evalType;

		public PlayLn(NodeDir node) {
			super();
			this.node = node;
			evalType = node.evalType();
			applyLn(evalType, this, e -> {
				FunctionIO<NodeDir, Object> in = InAction.of(evalType).in(null);
				FunctionIO out = OutAction.of(evalType).out(node);
				NodeEvalAction.doEvalNodeAction(node, in, out);
			});
		}

		public static PlayLn of(NodeDir node) {
			return new PlayLn(node);
		}

		public void applyLn(INodeType nodeType, PlayLn ln, EventListener eventListener) {
			INodeTypeProps iNodeStdProps = nodeType.stdProps();
			ln.setLabel(iNodeStdProps.icon());
			ln.title(iNodeStdProps.actionTitle());
			if (eventListener != null) {
				ln.onCLICK(eventListener);
			}
		}

	}
}
