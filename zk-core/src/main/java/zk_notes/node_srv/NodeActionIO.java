package zk_notes.node_srv;

import groovy.lang.GroovyShell;
import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpc.map.MAP;
import mpc.types.abstype.AbsType;
import mpe.core.P;
import mpe.call_msg.IICallMsg;
import mpu.SysExec;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.func.FunctionT;
import mpu.pare.Pare3;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Window;
import zk_com.base.Xml;
import zk_com.base_ctr.Menupopup0;
import zk_com.listbox.Listbox0;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import mpe.call_msg.injector.TrackMap;
import zk_notes.node_srv.types.jarMsg.JarECS;
import zk_notes.node_srv.types.kafkaMsg.KafkaECS;
import zk_notes.node_srv.types.quartzMsg.QzECS;
import zk_notes.node_srv.types.*;
import zk_os.sec.SecMan;
import zk_os.sec.SecManRMM;
import zk_page.ZKC;
import zk_page.ZKM;
import zk_page.ZKME;
import zk_page.events.ECtrl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NodeActionIO {

	public static FunctionT<NodeDir, Object> in(NodeEvalType nodeEvalType, TrackMap.TrackId trackId) {
		Window pushHolderCom = ZKC.getFirstWindow((Window[]) null);
		FunctionT<NodeDir, Object> evalFunction = null;
		switch (nodeEvalType) {

			case SENDMSG:
				evalFunction = (node) -> SendMsgECS.doSendMsg_AsyncLog_Simple(node);
				break;

			case QZEVAL:
				evalFunction = node -> {
					QzECS.runAll(node);
					return X.f("Successfully Run Quartz task of node " + node);
				};
				break;

			case NODE:

				evalFunction = nodeDir -> nodeDir;
//				evalFunction = evalFuncOut;
				//ok
				break;

			case HTTP:
				evalFunction = node -> HttpECS.doHttpCall_STRING(node.inject(trackId), false, false, false);
				break;

			case KAFKA:
				evalFunction = (node) -> KafkaECS.doEventAction(node, pushHolderCom);
				break;

			case SQL:
				evalFunction = (node) -> SqlECS.doSqlCall(node, trackId);
				break;

			case PYTHON:
				evalFunction = (node0) -> {
					String nodeData1 = node0.inject(trackId).nodeDataStr;
					return SysExec.exePython3(nodeData1);
				};
				break;

			case MVEL:
				evalFunction = (node0) -> MvelECS.of(node0, trackId).exec().getReturnedResult();
				break;
			case GROOVY:
				evalFunction = (node0) -> new GroovyShell().evaluate(node0.inject(trackId).nodeDataStr);
				break;

			case SHTASK:
				evalFunction = (node) -> BashECS.call(node, trackId);
				break;

			case JARTASK:
				evalFunction = JarECS.evalFunction(trackId);
				break;

			case IIPROMPT:

				evalFunction = (node) -> {

					String nodeData = node.inject(trackId).nodeDataStr;

					IICallMsg callMsg = IICallMsg.of(nodeData);

					Object iiRsp = callMsg.call(true);

					return iiRsp;
				};

				break;
			default:
				throw new WhatIsTypeException(nodeEvalType);
		}
		if (evalFunction != null) {
			return evalFunction;
		}
		throw new WhatIsTypeException(nodeEvalType);
	}

	public static FunctionT out(NodeEvalType evalType, NodeDir nodeDir, Map... context) {

		Map app = ARG.toDefOr(ARR.EMPTY_MAP, context);

		switch (evalType) {

			case SQL: {
				FunctionT<List<List<AbsType>>, Object> evalFuncOut = (tableRows) -> {
					HtmlBasedComponent modalCom = Listbox0.fromListList(tableRows, null);
					String titleCapCom = "Found " + X.sizeOf(tableRows) + " rows";
					return ZKM.showModal(titleCapCom, modalCom, ZKC.getFirstWindow(), new String[]{"90%", null});
				};
				return evalFuncOut;
			}

			case SENDMSG: {
				FunctionT<String, Object> evalFuncOut = (rsltOut) -> {
					if (X.empty(rsltOut)) {
						ZKI.infoAfterPointer(X.f("Message sended"), ZKI.Level.INFO);
					} else {
						ZKME.openEditorLog("SendMsg response node ", rsltOut);
					}
					return null;
				};
				return evalFuncOut;
			}

			case QZEVAL: {
				FunctionT<String, Object> evalFuncOut = (rsltOut) -> {
					ZKI.infoAfterPointer(rsltOut, ZKI.Level.INFO);
					return null;
				};
				return evalFuncOut;
			}

			case SHTASK: {
				FunctionT<String, Object> evalFuncOut = (rsltOut) -> {
					String msg = String.valueOf(rsltOut);
					if (nodeDir.inject().getCallMsg().isSync()) {
//					if (nodeDir.nodeDataInjected().getCallMsg().isSync()) {
						ZKME.openEditorLog("Bash call result:", msg);
					} else {
						ZKI.log(msg);
					}
					return null;
				};
				return evalFuncOut;
			}


			case PYTHON:
			case MVEL:
			case GROOVY:
			case IIPROMPT:

				return NodeEvalAction.DEFAULT_OUT_ANY_OBJECT;

			case KAFKA:
			case HTTP:
			case JARTASK: {
				P.warnBig("Wait oop impl:\n");
				return (v) -> {
					L.info("evalFuncOut Received {} \n {}", evalType, v);
					return null;
				};
			}
			case NODE: {
				{
					FunctionT<NodeDir, Object> evalFuncOut = node -> {
						String title = node.evalType(false, NodeEvalType.NODE).icon() + " " + node.nodeName();
						//if (nodeDir.nvt(null) == NodeDir.NVT.WYSIWYG) {
						Map defContext = ARG.toDefOr(ARR.EMPTY_MAP, context);
						Object keys = defContext.getOrDefault("keys", 0);
						switch ((Integer) keys) {
							case ECtrl.ZKE_2_CTRL_SHIFT_CODE:
								return ZKM.showModal(title, Xml.ofMd(node.getPath_FormFc_Data()), ZKC.getFirstWindow(), ZKM.WH100);
							case ECtrl.ZKE_2_CTRL_ALT_CODE:
								return ZKME.html(title, node.getPath_FormFc_Data(), SecMan.isOwnerOrAdmin());
							default:
								return ZKME.textSaveable(title, node.getPath_FormFc_Data());
						}
					};

					return evalFuncOut;

				}
			}
			default:
				throw new WhatIsTypeException(evalType);
		}
	}


	public static void applyFormMenu(Menupopup0 menu, NodeDir node) {
		NodeEvalType nodeEvalType = node.evalType(false);
		menu.addMI(nodeEvalType.titleWithIcon(), e -> doEventAction_ActiveWeb(node));
	}

	public static void doEventAction_ActiveWeb(NodeDir node) {
		NodeEvalType nodeEvalType = node.evalType(false);
		NodeEvalAction.doEvalNodeAction(node, nodeEvalType.evalIn(), nodeEvalType.evalOut(node));
	}

	public static <T> Pare3<NodeDir, Optional<T>, Object> doEventAction_ActiveWeb(NodeDir node, Integer keys) {
		NodeEvalType nodeEvalType = node.evalType(false, NodeEvalType.NODE);
		Map webContext = MAP.of("keys", keys);
		return NodeEvalAction.doEvalNodeAction(node, nodeEvalType.evalIn(), nodeEvalType.evalOut(node, webContext));
	}


}
