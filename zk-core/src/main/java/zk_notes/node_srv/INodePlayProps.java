package zk_notes.node_srv;

import mpe.cmsg.core.INodeType;
import mpe.cmsg.core.NodeSrv;
import mpe.cmsg.std.JarCallMsg;
import mpe.cmsg.std.JqlCallMsg;
import mpe.cmsg.std.KafkaCallMsg;
import mpe.cmsg.std.QzCallMsg;
import zk_notes.node_srv.core.ZService;
import zk_notes.node_srv.types.jarMsg.JarPlayContainer;
import zk_notes.node_srv.types.jqlMsg.JqlEvalPlayContainer;
import zk_notes.node_srv.types.kafkaMsg.KafkaPlayContainer;
import zk_notes.node_srv.types.quartzMsg.QzEvalPlayContainer;

public interface INodePlayProps {
	//regstd

	String stdType();

	public static INodePlayProps of(String stdType) {
		return () -> stdType;
	}

	public static PlayContainer toPlayContainer(INodeType nodeType, PlayContainer.PlayLn playLn) {
		switch (nodeType.stdTypeUC()) {
			default:
				ZService izService = nodeType.stdSrv(null);
				if (izService != null) {
					return izService.toPlayContainer(playLn);
				}
				return new PlayContainer(playLn);
			case JarCallMsg.KEY:
				return JarPlayContainer.toPlayContainer(playLn);
			case KafkaCallMsg.KEY:
				return KafkaPlayContainer.toPlayContainer(playLn);
			case QzCallMsg.KEY:
				return QzEvalPlayContainer.toPlayContainer(playLn);
			case JqlCallMsg.KEY:
				return JqlEvalPlayContainer.toPlayContainer(playLn);
		}
	}

}
