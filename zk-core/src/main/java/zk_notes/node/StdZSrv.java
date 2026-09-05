package zk_notes.node;

import mpe.cmsg.core.INode;
import mpe.cmsg.core.INodeType;
import mpe.cmsg.std.JarCallMsg;
import mpe.cmsg.std.KafkaCallMsg;
import mpe.cmsg.std.QzCallMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_notes.node_srv.PlayContainer;
import zk_notes.node_srv.core.ZService;
import zk_notes.node_srv.eval.EvalNodeService;
import zk_notes.node_srv.types.jarMsg.JarPlayContainer;
import zk_notes.node_srv.types.kafkaMsg.KafkaPlayContainer;
import zk_notes.node_srv.types.quartzMsg.QzEvalPlayContainer;

public class StdZSrv implements ZService {

    public static final Logger L = LoggerFactory.getLogger(StdZSrv.class);

    public static final StdZSrv DEFAULT = new StdZSrv();

    public static PlayContainer toPlayContainer(INodeType nodeType, PlayContainer.PlayLn playLn) {

        String stLc = nodeType.stdTypeLC();
        switch (stLc) {

            case JarCallMsg.KEY:
                return JarPlayContainer.toPlayContainer(playLn);
            case KafkaCallMsg.KEY:
                return KafkaPlayContainer.toPlayContainer(playLn);
            case QzCallMsg.KEY:
                return QzEvalPlayContainer.toPlayContainer(playLn);

//			case JqlCallMsg.KEY:
//				return JqlEvalPlayContainer.toPlayContainer(playLn);

            default:
                ZService izService = nodeType.stdSrv(null);
                if (izService != null) {
                    return izService.toPlayContainer(playLn);
                }
                return new PlayContainer(playLn);
        }
    }

    @Override
    public String evalAsString(INode node, EvalOpts opts) {
        return EvalNodeService.evalNode_NULL_QUERY__std(node, opts.withOuterJp(), opts.withOuterXp(), opts.trackContext);
    }

    @Override
    public PlayContainer toPlayContainer(PlayContainer.PlayLn playLn) {
        return ZService.super.toPlayContainer(playLn);
    }
}
