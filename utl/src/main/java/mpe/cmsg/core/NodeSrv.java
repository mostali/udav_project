package mpe.cmsg.core;

import lombok.SneakyThrows;
import mpc.rfl.RFL;
import mpe.cmsg.TrackMap;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Paret;

import java.util.Map;

public interface NodeSrv<R> {
	@SneakyThrows
	static NodeSrv of(INodeType nodeType, NodeSrv... defRq) {
		NodeSrv nodeSrv = nodeType.stdDesc().stdTypeSrvInstance(null);
		return nodeSrv != null ? nodeSrv : ARG.throwMsg(() -> X.f("NodeSrv '%s' not found", nodeType), defRq);
	}

	//
	//

	Paret<R> doSendMsg_AsyncLog(INode inject, TrackMap.TrackId track);


}
