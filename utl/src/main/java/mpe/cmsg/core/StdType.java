package mpe.cmsg.core;

import lombok.RequiredArgsConstructor;
import mpc.exception.NI;
import mpe.cmsg.std.*;

@RequiredArgsConstructor
public enum StdType implements INodeType {
    NODE, //
    HTTP, KAFKA, //
    SQL, GROOVY, PYTHON, SHTASK, MVEL, //
    JARTASK, //
    QZEVAL, SENDMSG, //
    //rmm
    MSV, JQL, PUBL, //

    IIPROMT, //
    ;

    final String nameLC;

    StdType() {
        this.nameLC = name().toUpperCase();
    }

//    @Deprecated
//    public static <M extends CallMsg> M newCallMsgNative(INode node, StdType nodeEvalType, boolean injected) {
//        String nodeData = (!injected ? node.inject() : node).readNodeDataStr();
//        switch (nodeEvalType) {
////			case IIPROMT:
////				return (M) IICallMsg.of(nodeData).setFromSrc(node);
////			case PUBL:
////				return (M) PublCallMsg.of(nodeData).setFromSrc(node);
//            case NODE:
//                NI.stop();
//                return null;
//            case SENDMSG:
//                return (M) SendCallMsg.of(nodeData).setFromSrc(node);
//            case HTTP:
//                return (M) HttpCallMsg.of(nodeData).setFromSrc(node);
//            case SHTASK:
//                return (M) BashCallMsg.of(nodeData).setFromSrc(node);
//            case JARTASK:
//                return (M) JarCallMsg.of(nodeData).setFromSrc(node);
//            case GROOVY:
//                return (M) GroovyCallMsg.of(nodeData).setFromSrc(node);
//            case MVEL:
//                return (M) MvelCallMsg.of(nodeData).setFromSrc(node);
//            case PYTHON:
//                return (M) PyCallMsg.of(nodeData).setFromSrc(node);
//            case SQL:
//                return (M) SqlCallMsg.of(nodeData).setFromSrc(node);
//            case KAFKA:
//                return (M) KafkaCallMsg.of(nodeData).setFromSrc(node);
//            case QZEVAL:
//                return (M) QzCallMsg.of(nodeData).setFromSrc(node);
//
//            default:
//                return null;
//        }
//    }

    @Override
    public String stdTypeUC() {
        return name();
    }

    @Override
    public String stdTypeLC() {
        return nameLC;
    }

    @Override
    public INodeDesc stdDesc() {
        return INodeDesc.valueOf(name());
    }
}
