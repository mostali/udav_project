package zk_notes.node_srv;

import mpc.exception.WhatIsTypeException;
import mpe.call_msg.core.INode;
import mpe.str.CN;
import mpc.exception.CleanDataResponseException;
import mpe.call_msg.KafkaCallMsg;
import mpu.SysExec;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.STR;
import udav_net.apis.zznote.ItemPath;
import zk_notes.apiv1.NodeApiCallType;
import zk_notes.apiv1.client.NoteApi0;
import zk_notes.node.NodeDir;
import mpe.call_msg.injector.TrackMap;
import zk_notes.node_srv.types.jarMsg.JarECS;
import zk_notes.node_srv.types.GroovyECS;
import zk_notes.node_srv.types.HttpECS;
import zk_notes.node_srv.types.kafkaMsg.KafkaECS;
import zk_notes.node_srv.types.SendMsgECS;
import mpe.call_msg.injector.NodeData;
import zk_os.sec.SecCheck;
import zk_page.core.PagePathInfoWithQuery;
import zk_page.core.SpVM;

import java.util.List;
import java.util.Map;

public class EvalService {

	public static String evalNodeByTrackId(NodeDir nodeDir, TrackMap.TrackId trackId) {

//		NodeData inject = NodeData.injectorSrv.apply(nodeDir, trackId);
		NodeData inject = nodeDir.inject(trackId);
//		NodeData inject = InjectSrv.inject(nodeDir, trackId);

		PagePathInfoWithQuery ppiq = SpVM.ppiq(null);
		if (ppiq == null) {
			return evalNode_NULL_QUERY(inject, false, false);
		}

		String jp = ppiq.queryUrl().getFirstAsStr("jp", null);
		boolean withOuterJp = jp != null;
		String xp = ppiq.queryUrl().getFirstAsStr("xp", null);
		boolean withOuterXp = xp != null;

		String rsp;
		if (trackId == null) {
			rsp = evalNode_NULL_QUERY(inject, withOuterJp, withOuterXp);
		} else {
			rsp = evalNodeByTrackId(inject, withOuterJp, withOuterXp, trackId);
		}

		rsp = NodeApiCallType.handlerRspViaJpOrXp.apply(rsp, jp, xp);

//		TaskPanel.removeTask(nodeDir);

		return rsp;
	}


	public static String evalNodeNoWeb(INode nodeData, Map trackContext) {
		return evalNode_NULL_QUERY(nodeData, false, false, trackContext);
	}

	public static String evalNode_NULL_QUERY(INode iNode, boolean withOuterJp, boolean withOuterXp) {
		return evalNode_NULL_QUERY(iNode, withOuterJp, withOuterXp, SpVM.getTrackContext(ARR.EMPTY_MAP));
	}

	public static String evalNode_NULL_QUERY(INode iNode, boolean withOuterJp, boolean withOuterXp, Map initTrackContext) {

		String string = new TrackMap.EvalTrack<String>() {
			@Override
			protected String doEvalImpl(TrackMap.TrackId track) {

//				NodeData inject0 = nodeData.nodeDir.inject(track);
				NodeData inject0 = iNode.inject(track);

				String rsp = evalNodeByTrackId(inject0, withOuterJp, withOuterXp, track);

				return rsp;

			}

		}.withNode(iNode).trackContext(initTrackContext).doEval();

		return string;

	}

	private static String evalNodeByTrackId(NodeData<NodeDir> inject, boolean withOuterJp, boolean withOuterXp, TrackMap.TrackId track) {

		NodeDir nodeDir = inject.nodeDir;

		NodeEvalType nodeEvalType = nodeDir.evalType(false, NodeEvalType.NODE);

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
					Object apply = NodeActionIO.in(nodeEvalType, track).apply(nodeDir);
					return apply == null ? "empty" : String.valueOf(apply);
				} catch (Throwable e) {
					return X.throwException(e);
				}

			case SQL:

				nodeDir.inject(track, true);

				NodeEvalAction evalAction = new NodeEvalAction(nodeDir, NodeEvalAction.ENV.WEB, NodeEvalAction.RESULT.STRING);

				return evalAction.evalString();

			case QZEVAL:

			default:
				throw new WhatIsTypeException("What you want eval from " + nodeEvalType + " ?");

		}

	}

	private static String evalNode_undefined(NodeDir nodeDir, PagePathInfoWithQuery curPPI) {

		SecCheck.checkIsOwnerOr404();

		String exe = curPPI.queryUrl().getFirstAsStr("exe");
		switch (exe) {
			case "bash":
			case "python3":
				Pare<Integer, List<String>> rslt = SysExec.exec_filetmp(exe, nodeDir.nodeDataStr(), null, false);
				throw NodeApiCallType.sendCleanResponse(rslt);

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
