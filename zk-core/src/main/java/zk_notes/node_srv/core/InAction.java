package zk_notes.node_srv.core;

import groovy.lang.GroovyShell;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpe.cmsg.*;
import mpe.cmsg.core.*;
import mpe.cmsg.std.IICallMsg;
import mpu.SysExec;
import mpu.X;
import mpu.func.FunctionIO;
import org.zkoss.zul.Window;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.types.*;
import zk_notes.node_srv.types.jarMsg.JarECS;
import zk_notes.node_srv.types.kafkaMsg.KafkaECS;
import zk_notes.node_srv.types.quartzMsg.QzECS;
import zk_page.ZKC;

@RequiredArgsConstructor
public class InAction {
	//regstd

	private final @Getter INodeType evalType;

	public static InAction of(INodeType evalType) {
		return new InAction(evalType);
	}

	public FunctionIO<NodeDir, Object> in(TrackMap.TrackId trackId) {

		NodeSrv service = NodeSrv.of(evalType, null);

		if (service != null) {
			return (node) -> service.doSendMsg_AsyncLog(node, trackId);
		}

		//
		//


		Window pushHolderCom = ZKC.getFirstWindow((Window[]) null);

		FunctionIO<NodeDir, Object> evalFunction;

		StdType stdType0 = evalType.stdType();

		switch (stdType0) {

//			case PUBL:
//				evalFunction = (node) -> {
//
//					String s = PublStubSrv.doSendMsg_AsyncLog_Simple(node);
//
//					return s;
//
//				};
//
//				break;
//
//			case JQL:
//
//				evalFunction = (node) -> JqlStubSrv.doSendMsg_AsyncLog(node, trackId);
//
//				break;

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
				throw new WhatIsTypeException(stdType0);
		}
		if (evalFunction != null) {
			return evalFunction;
		}
		throw new WhatIsTypeException(stdType0);
	}


}
