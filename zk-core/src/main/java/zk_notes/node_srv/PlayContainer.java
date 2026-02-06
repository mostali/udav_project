package zk_notes.node_srv;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import zk_com.base.Ln;
import zk_com.base_ctr.Span0;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.types.kafkaMsg.KafkaPlayContainer;
import zk_notes.node_srv.types.jarMsg.JarPlayContainer;
import zk_notes.node_srv.types.quartzMsg.QzEvalPlayContainer;

public class PlayContainer extends Span0 {
	final PlayLn playLn;

	public PlayContainer(Component... playLn) {
		super(playLn);
		this.playLn = (PlayLn) playLn[0];
	}

	public static PlayContainer toPlayContainer(PlayLn playLn) {
		switch (playLn.evalType) {
			default:
				return new PlayContainer(playLn);
			case JARTASK:
				return JarPlayContainer.toPlayContainer(playLn);
			case KAFKA:
				return KafkaPlayContainer.toPlayContainer(playLn);
			case QZEVAL:
				return QzEvalPlayContainer.toPlayContainer(playLn);
		}
	}

	public static class PlayLn extends Ln {
		public final NodeDir node;

		public final NodeEvalType evalType;

		public PlayLn(NodeDir node) {
			super();
			this.node = node;
			evalType = node.evalType(false);
			applyLn(evalType, this, e -> NodeEvalAction.doEvalNodeAction(node, evalType.evalIn(), evalType.evalOut(node)));
		}

		public static PlayLn of(NodeDir node) {
			return new PlayLn(node);
		}

		public void applyLn(NodeEvalType i, PlayLn ln, EventListener eventListener) {
			ln.setLabel(i.icon());
			ln.title(i.title());
			if (eventListener != null) {
				ln.onCLICK(eventListener);
			}
		}

	}
}
