package mpe.cmsg.ns;

public interface ISpaceID {

	String spaceName();

	String toObjId();

	default NodeID toObjID() {
		return NodeID.of(toObjId());
	}

	;

}
