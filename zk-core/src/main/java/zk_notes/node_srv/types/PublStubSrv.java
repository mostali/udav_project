package zk_notes.node_srv.types;

import lombok.SneakyThrows;
import mpe.cmsg.core.INode;
import mpe.cmsg.TrackMap;
import mpu.X;
import mpu.pare.Pare3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_notes.node.NodeDir;

public class PublStubSrv {

	public static final Logger L = LoggerFactory.getLogger(PublStubSrv.class);

	@SneakyThrows
	public static String doSendMsg_AsyncLog_Simple(NodeDir node) {

		Pare3<Object, Throwable, String> rsp = doSendMsg_AsyncLog(node, null);

		if (rsp.hasVal()) {
			throw rsp.val();
		}
		if (L.isInfoEnabled()) {
			L.info("Rslt Object doSendMsg_AsyncLog:" + rsp);
		}
//		return X.blank(rsp.ext()) ? rsp.key() + "" : rsp.ext();
		return X.blank(rsp.ext()) ? rsp.key() + "" : rsp.ext();
	}

	public static Pare3<Object, Throwable, String> doSendMsg_AsyncLog(INode<NodeDir> inject, TrackMap.TrackId track) {

		String nodeDataVal = inject.inject(track).nodeDataStr;

		return Pare3.of("STUB\n" + nodeDataVal, null, null);
	}


}
