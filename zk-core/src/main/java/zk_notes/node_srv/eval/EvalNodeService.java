package zk_notes.node_srv.eval;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpe.cmsg.core.INode;
import mpe.cmsg.core.NodeSrv;
import mpe.str.CN;
import mpc.exception.CleanDataResponseException;
import mpu.SysExec;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.STR;
import udav_net.apis.zznote.ItemPath;
import zk_notes.apiv1.NoteApiRestHandler;
import zk_notes.apiv1.client.NoteApi0;
import zk_notes.node.NodeDir;
import mpe.cmsg.TrackMap;
import mpe.cmsg.NodeData;
import zk_os.sec.SecCheck;
import zk_page.core.PagePathInfoWithQuery;
import zk_page.core.SpVM;

import java.util.List;
import java.util.Map;

public class EvalNodeService {

    public static String evalNodeByTrackId(NodeDir nodeDir, TrackMap.TrackId trackId) {

        NodeData inject = nodeDir.inject(trackId);

        PagePathInfoWithQuery ppiq = SpVM.ppiq(null);
        if (ppiq == null) {
            return evalNode_NULL_QUERY__std(inject, false, false);
        }

        String jp = ppiq.queryUrl().getFirstAsStr("jp", null);
        boolean withOuterJp = jp != null;
        String xp = ppiq.queryUrl().getFirstAsStr("xp", null);
        boolean withOuterXp = xp != null;

        String rsp;
        if (trackId == null) {
            rsp = evalNode_NULL_QUERY__std(inject, withOuterJp, withOuterXp);
        } else {
            rsp = new Std_EvalString_TrackedHandler(trackId, withOuterJp, withOuterXp).impl(inject);
        }

        rsp = NodeSrv.handlerRspViaJpOrXp.apply(rsp, jp, xp);

        return rsp;
    }


    public static String evalNodeNoWeb__std(INode nodeData, Map trackContext) {
        return evalNode_NULL_QUERY__std(nodeData, false, false, trackContext);
    }

    public static String evalNode_NULL_QUERY__std(INode iNode, boolean withOuterJp, boolean withOuterXp) {
        return evalNode_NULL_QUERY__std(iNode, withOuterJp, withOuterXp, SpVM.getTrackContext(ARR.EMPTY_MAP));
    }

    public static String evalNode_NULL_QUERY__std(INode iNode, boolean withOuterJp, boolean withOuterXp, Map initTrackContext) {

        String string = new TrackMap.EvalTrack<String>() {

            @Override
            protected String doEvalImpl(TrackMap.TrackId track) {

                NodeData inject0 = iNode.inject(track);

                Std_EvalString_TrackedHandler handler = new Std_EvalString_TrackedHandler(track, withOuterJp, withOuterXp);

                String rsp = handler.impl(inject0);
//                String rsp = evalNodeByTrackId(inject0, withOuterJp, withOuterXp, track);

                return rsp;

            }

        }.withNode(iNode).trackContext(initTrackContext).doEval();

        return string;

    }

    @RequiredArgsConstructor
    public static class TrackedHandler<T> {
        final TrackMap.TrackId track;

        public T impl(NodeData<NodeDir> inject) {
            return null;
        }
    }

    public static class EvalString_TrackedHandler extends TrackedHandler<String> {
        final boolean withOuterJp;
        final boolean withOuterXp;

        public EvalString_TrackedHandler(TrackMap.TrackId track, boolean withOuterJp, boolean withOuterXp) {
            super(track);
            this.withOuterJp = withOuterJp;
            this.withOuterXp = withOuterXp;
        }
    }


    private static String evalNode_undefined(NodeDir nodeDir, PagePathInfoWithQuery curPPI) {

        SecCheck.checkIsOwnerOr404();

        String exe = curPPI.queryUrl().getFirstAsStr("exe");
        switch (exe) {
            case "bash":
            case "python3":
                Pare<Integer, List<String>> rslt = SysExec.exec_filetmp(exe, nodeDir.nodeDataStr(), null, false);
                throw NoteApiRestHandler.sendCleanResponse(rslt);

            case "bash*":
            case "python3*":
                String nodeName = nodeDir.nodeName();
                String vl = new NoteApi0().zApiUrl.GET_toItem(ItemPath.of(curPPI.sdnUnsafe(), nodeName), Pare.of(CN.EXE, exe));
                String callCurl = X.f("curl -s '%s' | ", vl) + STR.substrCount(exe, -1);
                throw new CleanDataResponseException(callCurl);
            default:
                throw new WhatIsTypeException(exe);
        }


    }

}
