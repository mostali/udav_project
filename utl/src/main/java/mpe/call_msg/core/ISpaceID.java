package mpe.call_msg.core;

public interface ISpaceID {

	String spaceName();

	String toObjId();

	default NodeID toObjID() {
		return NodeID.of(toObjId());
	}

	;

}
