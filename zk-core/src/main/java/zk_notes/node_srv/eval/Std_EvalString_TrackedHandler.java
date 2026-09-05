package zk_notes.node_srv.eval;

import mpc.exception.WhatIsTypeException;
import mpe.cmsg.NodeData;
import mpe.cmsg.TrackMap;
import mpe.cmsg.core.INodeType;
import mpe.cmsg.core.StdType;
import mpe.cmsg.std.KafkaCallMsg;
import mpu.X;
import mpu.pare.Pare3;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.core.InAction;
import zk_notes.node_srv.types.GroovyECS;
import zk_notes.node_srv.types.HttpECS;
import zk_notes.node_srv.types.SendMsgECS;
import zk_notes.node_srv.types.SqlECS;
import zk_notes.node_srv.types.jarMsg.JarECS;
import zk_notes.node_srv.types.kafkaMsg.KafkaECS;

public class Std_EvalString_TrackedHandler extends EvalNodeService.EvalString_TrackedHandler {

    public Std_EvalString_TrackedHandler(TrackMap.TrackId track, boolean withOuterJp, boolean withOuterXp) {
        super(track, withOuterJp, withOuterXp);
    }

    @Override
    public String impl(NodeData<NodeDir> inject) {

        NodeDir nodeDir = inject.nodeDir;

        INodeType iNodeType = nodeDir.stdType(StdType.NODE);

        StdType nodeEvalType = iNodeType.stdType();

        switch (nodeEvalType) {
            case HTTP:
                return HttpECS.doHttpCall_STRING(inject, withOuterJp, withOuterXp, true);

            case KAFKA: {
                Pare3<KafkaCallMsg, Object, Throwable> rsp = KafkaECS.doKafkaCall(nodeDir);
                if (rsp.ext() != null) {
                    X.throwException(rsp.ext());
                }
                return rsp.val() + "";
            }
//				throw NodeApiCallType.toCleanDataResponseException((Pare3) rsp, nodeDir);

            case GROOVY:
                return String.valueOf(GroovyECS.doGroovyCall_VALUE(nodeDir, track, false));

            case JARTASK:
                return String.valueOf(JarECS.EVAL.doJarCallSyncRest_VALUE(inject));

            case SENDMSG: {
                Pare3<Object, Throwable, String> rsp = SendMsgECS.doSendMsg_AsyncLog(inject, track);
                if (rsp.val() != null) {
                    X.throwException(rsp.val());
                }
                return rsp.key() + "";
            }

            case NODE:
                return inject.nodeDataStr;

            case SHTASK:
                try {
                    Object apply = InAction.of(nodeEvalType).srvIn(track).apply(nodeDir);
                    return apply == null ? "empty" : String.valueOf(apply);
                } catch (Throwable e) {
                    return X.throwException(e);
                }

            case SQL:

                nodeDir.inject(track, true);

                Object obj = SqlECS.doSqlCall_VALUE(nodeDir, false);

                return String.valueOf(obj);

            case QZEVAL:

            default:
                throw new WhatIsTypeException("What you want eval from " + nodeEvalType + " ?");

        }
    }
}
