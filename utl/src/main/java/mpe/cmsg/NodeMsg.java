package mpe.cmsg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpe.cmsg.core.CallMsg;
import mpe.cmsg.core.INode;

@RequiredArgsConstructor
public class NodeMsg<T extends CallMsg> implements INode<T> {

	final @Getter CallMsg callMsg;

	public static NodeMsg of(CallMsg callMsg) {
		return new NodeMsg(callMsg);
	}

	@Override
	public T toNodeImpl() {
		return (T) callMsg;
	}

	@Override
	public String readNodeDataStr(String... defRq) {
		return callMsg.getMsg();
	}

	@Override
	public String nodeName() {
//		Object fromSrc = getCallMsg().getFromSrc();

		return toObjId();
	}


	@Override
	public String toObjId() {
		return callMsg.toObjMsgId();
	}
}
