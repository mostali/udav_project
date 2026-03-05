package mpe.cmsg.core;

import mpc.exception.NI;
import mpe.cmsg.std.*;

public enum StdType implements INodeType {
	NODE, //
	HTTP, KAFKA, //
	SQL, GROOVY, PYTHON, SHTASK, MVEL, //
	JARTASK, //
	QZEVAL, SENDMSG, //

	JQL, PUBL, //

	IIPROMPT, //
	;

	@Deprecated
	public static <M extends CallMsg> M newCallMsgNative(INode node, StdType nodeEvalType, boolean injected) {

//		String s = readNodeDataStr(null);
//		CallType nodeEvalType = evalType();

//		INode node = this;
		String nodeData = (!injected ? node.inject() : node).readNodeDataStr();
//		CallMsg callMsg = CallMsg.ofAnyNode(nodeData);
//		callMsg.evalType();
		switch (nodeEvalType) {
//			case PUBL:
//				return (M) PublCallMsg.of(nodeData).setFromSrc(node);
			case NODE:
				NI.stop();
				return null;
			case SENDMSG:
				return (M) SendCallMsg.of(nodeData).setFromSrc(node);
			case HTTP:
				return (M) HttpCallMsg.of(nodeData).setFromSrc(node);
			case SHTASK:
				return (M) BashCallMsg.of(nodeData).setFromSrc(node);
			case JARTASK:
				return (M) JarCallMsg.of(nodeData).setFromSrc(node);
			case GROOVY:
				return (M) GroovyCallMsg.of(nodeData).setFromSrc(node);
			case MVEL:
				return (M) MvelCallMsg.of(nodeData).setFromSrc(node);
			case PYTHON:
				return (M) PyCallMsg.of(nodeData).setFromSrc(node);
			case SQL:
				return (M) SqlCallMsg.of(nodeData).setFromSrc(node);
			case KAFKA:
				return (M) KafkaCallMsg.of(nodeData).setFromSrc(node);
			case QZEVAL:
				return (M) QzCallMsg.of(nodeData).setFromSrc(node);
			case IIPROMPT:
				return (M) IICallMsg.of(nodeData).setFromSrc(node);
			default:
				return null;
		}
	}

	@Override
	public String stdTypeUC() {
		return name();
	}

	@Override
	public INodeDesc stdDesc() {
		return INodeDesc.valueOf(name());
	}
}
