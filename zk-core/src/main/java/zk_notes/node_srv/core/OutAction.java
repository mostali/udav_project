package zk_notes.node_srv.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpc.types.abstype.AbsType;
import mpe.cmsg.core.INodeType;
import mpe.cmsg.core.NodeSrv;
//import mpe.cmsg.core.NodeReg;
import mpe.cmsg.core.StdType;
import mpe.core.P;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.func.FunctionIO;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.base.Xml;
import zk_com.listbox.Listbox0;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.NodeEvalAction;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.ZKM;
import zk_page.ZKME;
import zk_page.events.ECtrl;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class OutAction {
	//regstd

	private final @Getter INodeType evalType;

	public static OutAction of(INodeType evalType) {
		return new OutAction(evalType);
	}

	public FunctionIO out(NodeDir nodeDir, Map... context) {

		NodeSrv service = NodeSrv.of(evalType, null);

		if (service != null) {
			FunctionIO<Object, Object> evalFuncOut = (rsltOut) -> {
				ZKME.openEditorLog("Ok", rsltOut + "");
				return null;
			};
			return evalFuncOut;
		}

		StdType evalType = this.evalType.stdType();

		switch (evalType) {

			case SQL: {
				FunctionIO<List<List<AbsType>>, Object> evalFuncOut = (tableRows) -> {
					HtmlBasedComponent modalCom = Listbox0.fromListList(tableRows, null);
					String titleCapCom = "Found " + X.sizeOf(tableRows) + " rows";
					return ZKM.showModal(titleCapCom, modalCom, ZKC.getFirstWindow(), new String[]{"90%", null});
				};
				return evalFuncOut;
			}


			case SENDMSG: {
				{
					FunctionIO<String, Object> evalFuncOut = (rsltOut) -> {
						if (X.empty(rsltOut)) {
							ZKI.infoAfterPointer(X.f("Message sended"), ZKI.Level.INFO);
						} else {
							ZKME.openEditorLog("SendMsg response node ", rsltOut);
						}
						return null;
					};
					return evalFuncOut;
				}
			}

			case QZEVAL: {
				FunctionIO<String, Object> evalFuncOut = (rsltOut) -> {
					ZKI.infoAfterPointer(rsltOut, ZKI.Level.INFO);
					return null;
				};
				return evalFuncOut;
			}

			case SHTASK: {
				FunctionIO<String, Object> evalFuncOut = (rsltOut) -> {
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

//			case JQL:
//			case PUBL:
//			{
//				FunctionIO<Object, Object> evalFuncOut = (rsltOut) -> {
//					ZKME.openEditorLog("PublMsg response node", rsltOut + "");
//					return null;
//				};
//
//				return evalFuncOut;
//			}


			case PYTHON:
			case MVEL:
			case GROOVY:
			case IIPROMPT:

				return NodeEvalAction.DEFAULT_OUT_ANY_OBJECT;

			default:
				throw new WhatIsTypeException(evalType);

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
					FunctionIO<NodeDir, Object> evalFuncOut = node -> {
						String title = node.evalType(StdType.NODE).stdProps().icon() + " " + node.nodeName();
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

		}
	}
}
