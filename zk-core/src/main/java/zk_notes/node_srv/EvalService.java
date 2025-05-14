package zk_notes.node_srv;

import mpc.exception.WhatIsTypeException;
import mpc.net.query.QueryUrl;
import mpe.str.CN;
import mpe.wthttp.CleanDataResponseException;
import mpe.wthttp.KafkaCallMsg;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.STR;
import udav_net.apis.zznote.ItemPath;
import zk_notes.apiv1.NodeApiCallType;
import zk_notes.apiv1.client.LocalNoteApi;
import zk_notes.node_srv.core.NodeActionIO;
import zk_notes.node_srv.core.NodeEvalAction;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.core.NodeEvalType;
import zk_notes.node_srv.jarcall.JarCallService;
import zk_os.core.NodeData;
import zk_os.sec.Sec;
import zk_page.core.PagePathInfoWithQuery;
import zk_page.core.SpVM;

import java.util.List;
import java.util.Map;

public class EvalService {

//	public static String evalNodeNew(NodeDir nodeDir) {
//		return evalNodeTracked(nodeDir, null);
//	}

	public static String evalNodeTracked(NodeDir nodeDir, TrackMap.TrackId trackId) {

//		TaskPanel.addTask(nodeDir.);

		NodeData inject = InjectNode.inject(nodeDir, trackId);

		PagePathInfoWithQuery ppiq = SpVM.ppiq(null);
		if (ppiq == null) {
			return evalNode(inject, false, false);
		}

		String jp = ppiq.queryUrl().getFirstAsStr("jp", null);
		boolean withOuterJp = jp != null;
		String xp = ppiq.queryUrl().getFirstAsStr("xp", null);
		boolean withOuterXp = xp != null;

		String rsp;
		if (trackId == null) {
			rsp = evalNode(inject, withOuterJp, withOuterXp);
		} else {
			rsp = evalNodeTracked(inject, withOuterJp, withOuterXp, trackId);
		}

		rsp = NodeApiCallType.handlerRspViaJpOrXp.apply(rsp, jp, xp);

//		TaskPanel.removeTask(nodeDir);

		return rsp;
	}

	public static String evalNode(NodeData inject, boolean withOuterJp, boolean withOuterXp) {
		SpVM spVM = SpVM.get(null);
		Map trackContext;
		if (spVM == null) {
			trackContext = ARR.EMPTY_MAP;
		} else {
			QueryUrl query = spVM.getQuery();
			trackContext = query.getMapWithKeyPfx("$$", true);
		}
		return evalNode(inject, withOuterJp, withOuterXp, trackContext);
	}

	public static String evalNodeNoWeb(NodeData inject, Map trackContext) {
		return evalNode(inject, false, false, trackContext);
	}

	private static String evalNode(NodeData inject, boolean withOuterJp, boolean withOuterXp, Map initTrackContext) {

		String string = new TrackMap.EvalTrack<String>() {
			@Override
			String doEvalImpl(TrackMap.TrackId track) {

				NodeData inject0 = InjectNode.inject(inject.nodeDir, track);

				String rsp = evalNodeTracked(inject0, withOuterJp, withOuterXp, track);

				return rsp;
			}

		}.withNode(inject).trackContext(initTrackContext).doEval();

		return string;

	}

	private static String evalNodeTracked(NodeData inject, boolean withOuterJp, boolean withOuterXp, TrackMap.TrackId track) {

		NodeDir nodeDir = inject.nodeDir;

		NodeEvalType nodeEvalType = nodeDir.evalType(false, NodeEvalType.NODE);

		//		if (nodeEvalType == null) {
		//			return evalNode_undefined(nodeDir, curPPI, track);
		//		}

		switch (nodeEvalType) {
			case HTTP:
				return HttpCallService.doHttpCall_STRING(inject, withOuterJp, withOuterXp, true);

			case KAFKA: {
				Pare3<KafkaCallMsg, Object, Throwable> rsp = KafkaCallService.doKafkaCall(nodeDir);
				if (rsp.ext() != null) {
					X.throwException(rsp.ext());
				}
				return rsp.val() + "";
			}
//				throw NodeApiCallType.toCleanDataResponseException((Pare3) rsp, nodeDir);

			case GROOVY:
				return String.valueOf(GroovyCallService.doGroovyCall_VALUE(nodeDir, false));

			case JARTASK:
				return String.valueOf(JarCallService.doJarCallSyncRest_VALUE(nodeDir));

			case SENDMSG: {
				Pare3<Object, Throwable, String> rsp = SendMsgService.doSendMsg_AsyncLog(inject, track);
				if (rsp.val() != null) {
					X.throwException(rsp.val());
				}
				return rsp.key() + "";
			}


			case NODE:
				return inject.nodeData;

			case SHTASK:
				try {
					Object apply = NodeActionIO.in(nodeEvalType, track).apply(nodeDir);
					return apply == null ? "empty" : String.valueOf(apply);
				} catch (Throwable e) {
					return X.throwException(e);
				}

			case SQL:
				nodeDir.nodeDataInjected(true);

				NodeEvalAction evalAction = new NodeEvalAction(nodeDir, NodeEvalAction.ENV.WEB, NodeEvalAction.RESULT.STRING);

				return evalAction.evalString();

			case QZEVAL:

			default:
				throw new WhatIsTypeException("What you want eval from " + nodeEvalType + " ?");

		}

	}

	private static String evalNode_undefined(NodeDir nodeDir, PagePathInfoWithQuery curPPI, TrackMap.TrackId trackId) {

		Sec.checkIsOwnerOr404();

		String exe = curPPI.queryUrl().getFirstAsStr("exe");
		switch (exe) {
			case "bash":
			case "python3":
				break;
			case "bash*":
			case "python3*":
//				String vl = SpVM.get().getUrlTo(RSPath.PAGE, UP.ctrlSym + "/" + path2_nodeName);
				String nodeName = nodeDir.nodeName();
				String vl = new LocalNoteApi().zApiUrl.GET_toItem(ItemPath.of(curPPI.sdn0(), nodeName), Pare.of(CN.EXE, exe));
				String callCurl = X.f("curl -s '%s' | ", vl) + STR.substrCount(exe, -1);
				throw new CleanDataResponseException(callCurl);
			default:
				throw new WhatIsTypeException(exe);
		}

		Pare<Integer, List<String>> rslt = Sys.exec_filetmp(exe, nodeDir.nodeData(), null, false);

		throw NodeApiCallType.sendCleanResponse(rslt);
	}

}
