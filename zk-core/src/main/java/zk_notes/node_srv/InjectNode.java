package zk_notes.node_srv;

import mpc.env.AP;
import mpc.exception.WhatIsTypeException;
import mpc.map.BootContext;
import mpe.str.URx;
import mpe.wthttp.CallMsg;
import mpu.IT;
import mpu.core.ARR;
import mpu.pare.Pare;
import udav_net.apis.zznote.NodeID;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.jarcall.JarCallService;
import zk_os.sec.SecMan;

import java.util.Map;
import java.util.function.Function;

public class InjectNode {

	//	public static String inject(NodeDir node) {
//		return inject(node, null);
//	}

//	public static String inject(NodeDir node, String nodeData) {
//		String trackId = getTackId();
//		return trackId == null ? injectTracked(node, nodeData) : inject(node, nodeData, trackId);
//	}

	//	public static String injectTracked(NodeDir node) {
//		return injectTracked(node, null);
//	}

	public static String inject(NodeDir node, String nodeData, String trackId) {
		return injectImpl(node, nodeData, trackId);
	}

	private static String injectImpl(NodeDir node, String nodeData, String trackId) {

		Map mapQueryContext = TrackMap.getContext(trackId, ARR.EMPTY_MAP);

		nodeData = nodeData == null ? node.nodeData() : nodeData;

		CallMsg callMsg = CallMsg.ofQk(nodeData);

		//
		// check #{{ BootContent & application.properties }}

		Function<String, Object> placeholderReolverBcApKey = (key) -> BootContext.get() == null ? AP.get(key, null) : BootContext.get().get(key, null);
		nodeData = URx.PlaceholderRegex.NUMSIGN.findAndReplaceAll(nodeData, placeholderReolverBcApKey);

		//
		//check ${{ inner context, query args }}

		Function<String, Object> placeholderReolverDollar = URx.PlaceholderRegex.createMapResolver(callMsg.getHeadersAsMap_Meta(), mapQueryContext);
		nodeData = URx.PlaceholderRegex.DOLLAR.findAndReplaceAll(nodeData, placeholderReolverDollar);

		//
		//Check @{{ link to node }}

		Function<String, Object> placeholderReolverDog = (nodeId) -> {
			NodeID nodeID = NodeID.of(nodeId);
			NodeDir innerNodeDir;
			switch (nodeID.state) {
				case SINGLE:
					innerNodeDir = node.cloneWithItem(nodeId);
					break;
				case PAGED:
					innerNodeDir = node.cloneWithItem(nodeID.itemRq(), nodeID.pageRq());
					break;
				case FULL:
					innerNodeDir = NodeDir.ofNodeId(nodeID);
					break;
				default:
					throw new WhatIsTypeException("Illegal state '%s' with node pattern '%s'", nodeID.state, nodeId);
			}
			IT.state(innerNodeDir.existNode(true), "Node '%s' not found", nodeId);
			SecMan.isAllowedView(innerNodeDir, true);

			Pare<NodeDir, String> evalInnerNode = evalInnerNode(trackId, innerNodeDir);

			return InjectNode.injectImpl(innerNodeDir, evalInnerNode.val(), trackId);

		};
		nodeData = URx.PlaceholderRegex.DOG.findAndReplaceAll(nodeData, placeholderReolverDog);
		return nodeData;
	}

	private static Pare<NodeDir, String> evalInnerNode(String trackId, NodeDir node) {
		return Pare.of(node, evalInnerNodeImpl(trackId, node));
	}

	private static String evalInnerNodeImpl(String trackId, NodeDir node) {
		NodeEvalType nodeEvalType = node.evalType(false, null);
		if (nodeEvalType == null) {
			return node.nodeData();
		}
		switch (nodeEvalType) {
			case HTTP:
				return HttpCallService.doHttpCall_VALUE(trackId, node);
			case JARTASK:
				return String.valueOf(JarCallService.doJarCallSyncRest_VALUE(node, false));
			case SQL:
				return String.valueOf(SqlCallService.doSqlCall_VALUE(node, false));
			case GROOVY:
				return String.valueOf(GroovyCallService.doGroovyCall_VALUE(node, false));

			case QZTASK:
			case KAFKA:
			default:
				throw new WhatIsTypeException("Unsupported placeholder call:" + nodeEvalType);
		}
	}

}
