package mpe.cmsg.core;

import mpu.X;
import mpu.core.ARG;
import mpu.core.ENUM;

import java.util.Map;

public interface INodeType {

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

	String stdTypeUC();

	default INodeTypeProps stdProps() {
		return INodeTypeProps.of(stdTypeUC());
	}

	default StdType stdType() {
		return StdType.valueOf(stdTypeUC());
	}

	INodeDesc stdDesc();

	default Map serializeJson() {
		return serializeJson(this);
	}

	static Map serializeJson(INodeType nodeType) {
		return INodeDesc.serializeJson(nodeType.stdDesc());
	}

	default <S extends NodeSrv> S stdSrv(S... defRrq) {
		return (S) NodeSrv.of(this, defRrq);
	}

}
