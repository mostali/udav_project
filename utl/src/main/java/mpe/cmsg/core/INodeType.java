package mpe.cmsg.core;

import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.core.ENUM;
import mpu.str.RANDOM;

import java.util.Map;

public interface INodeType {

	static INodeType defineNodeType(INode node, boolean strictValid, INodeType... defRq) {

		String nodeData = node.readNodeDataStr(null);

		String line0 = nodeData == null ? null : ARRi.firstLine(nodeData);

		String objMsgId = node.toObjId();

		if (X.empty(line0)) {
			return ARG.throwMsg(() -> X.f("INodeType '%s' line0 is empty", objMsgId), defRq);
		}

		INodeType iNodeType = findNodeTypeByLine0(line0, null);

		if (iNodeType == null) {
			return ARG.throwMsg(() -> X.f("INodeType '%s' not found from std, line0 : %s", objMsgId, line0), defRq);
		}

		if (!strictValid) {
			return iNodeType;
		}

		//only check
		ICallMsg callMsgValid = iNodeType.stdDesc().newInstanceCallMsgValid(null, nodeData, null);
		if (callMsgValid == null) {
			return ARG.throwMsg(() -> X.f("INodeType '%s' not deserialize from data", objMsgId), defRq);
		}

		if (!callMsgValid.isValidStrict()) {
			return ARG.throwErr(() -> new RequiredRuntimeException(callMsgValid.toErrCollector().getMultiOrSingleErrorOrNull(), "INodeType '%s' invalid", objMsgId), defRq);
		}
		return iNodeType;
	}

	static INodeType findNodeTypeByLine0(String line0, INodeType... defRq) {

		Map<String, INodeDesc> map = NodeDescCache.TYPES_CACHED();

		//find by line0
		for (Map.Entry<String, INodeDesc> typeEntry : map.entrySet()) {
			INodeDesc desc = typeEntry.getValue();
			String _line0 = desc.line0();
			if (X.notEmpty(_line0)) {
				if (line0.startsWith(_line0)) {
					return desc.toNodeType();
				}
			}
		}

		//find by sub
		for (Map.Entry<String, INodeDesc> typeEntry : map.entrySet()) {
			INodeDesc desc = typeEntry.getValue();
			Class _sub0 = desc.sub0();
			if (_sub0 != null) {
				String anyKey = ICallMsg.findAnyKey(line0, null);
				if (anyKey != null) {
					if (ENUM.getValuesAsString(_sub0).contains(anyKey)) {
						return desc.toNodeType();
					}
				}
			}
		}
		return ARG.throwMsg(() -> X.f("INodeType '%s' not found by mode startsWith", line0), defRq);
	}


	//
	//
	//

	String stdTypeUC();

	String stdTypeLC();

	INodeDesc stdDesc();

	default INodeTypeProps stdProps() {
		return INodeTypeProps.of(stdTypeUC());
	}

	default StdType stdType() {
		return StdType.valueOf(stdTypeUC());
	}

//    default <S extends NodeSrv> S stdSrvAny(S... defRrq) {
//        return (S) toNodeSrv(this, defRrq);
//    }

	default <S extends NodeSrv> S stdSrv(S... defRrq) {
		return (S) toNodeSrv(this, defRrq);
	}

	@SneakyThrows
	static NodeSrv toNodeSrv(INodeType nodeType, NodeSrv... defRq) {
		NodeSrv nodeSrv = nodeType.stdDesc().stdTypeSrvInstance(null);
		return nodeSrv != null ? nodeSrv : ARG.throwMsg(() -> X.f("NodeSrv '%s' not found", nodeType), defRq);
	}
	//
	//

	default Map serializeJson() {
		return serializeJson(this);
	}

	static Map serializeJson(INodeType nodeType) {
		return INodeDesc.serializeJson(nodeType.stdDesc());
	}


	default String stdColor() {
		return RANDOM.array_item(stdProps().toColor());
	}

}
