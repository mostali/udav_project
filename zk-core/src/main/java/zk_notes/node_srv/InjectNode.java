package zk_notes.node_srv;

import mp.utl_odb.tree.UTree;
import mpc.env.AP;
import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpc.map.BootContext;
import mpe.str.URx;
import mpe.wthttp.CallMsg;
import mpu.IT;
import mpu.core.ARR;
import udav_net.apis.zznote.NodeID;
import zk_notes.node.NodeDir;
import zk_os.core.NodeData;

import java.util.Map;
import java.util.function.Function;

public class InjectNode {

	public static NodeData inject(NodeDir node) {

		String data = newInject(node, null, null);

		return NodeData.of(node, data);
	}

	public static NodeData inject(NodeDir node, TrackMap.TrackId trackId) {

		String data = newInject(node, null, trackId);

		return NodeData.of(node, data);
	}

	private static String newInject(NodeDir node, String nodeData, TrackMap.TrackId trackId) {

		Map mapQueryContext;
		if (trackId == null) {
			mapQueryContext = ARR.EMPTY_MAP;
			L.info("Map context is empty:{}", node.nodeName());
		} else {
			mapQueryContext = trackId.getContext();
			L.info("Map context found:{}", mapQueryContext);

		}

		nodeData = nodeData == null ? node.nodeData() : nodeData;

		CallMsg callMsg = CallMsg.ofQk(nodeData);

		//
		// check %{{ GlobalNodeContext }}

		Function<String, Object> placeholderReolverPct = (gncKey -> UTree.tree(APP.TREE_GNC()).getValue(gncKey, null));
		nodeData = URx.PlaceholderRegex.PCT.findAndReplaceAll(nodeData, placeholderReolverPct);

		//
		// check #{{ BootContent & application.properties }}

		Function<String, Object> placeholderReolverBcApKey = (key) -> BootContext.get() == null ? AP.get(key, null) : BootContext.get().get(key, null);
		nodeData = URx.PlaceholderRegex.NUMSIGN.findAndReplaceAll(nodeData, placeholderReolverBcApKey);

		//
		//check ${{ inner context, query args }}

		Function<String, Object> placeholderReolverDollar = URx.PlaceholderRegex.createMapResolver(callMsg.getHeaders_METAMAP(), mapQueryContext);
		nodeData = URx.PlaceholderRegex.DOLLAR.findAndReplaceAll(nodeData, placeholderReolverDollar);

		//
		//Check @{{ link to node }}

		Function<String, NodeDir> contextNodeGetter = (nodeId) -> {
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
			return innerNodeDir;
		};

		Function<String, Object> placeholderReolverDogKey = (nodeId) -> {
			NodeDir innerNodeDir = contextNodeGetter.apply(nodeId);
			IT.state(innerNodeDir.existNode(true), "Node '%s' not found", nodeId);
			//SecMan.isAllowedView(innerNodeDir, true);
			String vl = EvalService.evalNodeByTrackId(innerNodeDir, trackId);
			return InjectNode.newInject(innerNodeDir, vl, trackId);
		};

		nodeData = URx.PlaceholderRegex.DOG_TREE.findAndReplaceAll(nodeData, placeholderReolverDogKey);


		Function<String, Object> placeholderReolverDog = (nodeId) -> {
			NodeDir innerNodeDir = contextNodeGetter.apply(nodeId);
			IT.state(innerNodeDir.existNode(true), "Node '%s' not found", nodeId);
			//SecMan.isAllowedView(innerNodeDir, true);
			String vl = EvalService.evalNodeByTrackId(innerNodeDir, trackId);
			return InjectNode.newInject(innerNodeDir, vl, trackId);
		};

		nodeData = URx.PlaceholderRegex.DOG.findAndReplaceAll(nodeData, placeholderReolverDog);

		//
		//Check &{{ link to node }}

		Function<String, Object> placeholderReolverAmp = (nodeId) -> {
			NodeDir innerNodeDir = contextNodeGetter.apply(nodeId);
			IT.state(innerNodeDir.existNode(true), "Node '%s' not found", nodeId);
			return innerNodeDir.state().readFcData(1, "empty");

		};

		Function<String, Object> placeholderReolverAmp2 = (nodeId) -> {
			NodeDir innerNodeDir = contextNodeGetter.apply(nodeId);
			IT.state(innerNodeDir.existNode(true), "Node '%s' not found", nodeId);
			return innerNodeDir.state().readFcData(2, "empty");

		};

		Function<String, Object> placeholderReolverAmp3 = (nodeId) -> {
			NodeDir innerNodeDir = contextNodeGetter.apply(nodeId);
			IT.state(innerNodeDir.existNode(true), "Node '%s' not found", nodeId);
			return innerNodeDir.state().readFcData(3, "empty");

		};

		nodeData = URx.PlaceholderRegex.AMP3.findAndReplaceAll(nodeData, placeholderReolverAmp3);
		nodeData = URx.PlaceholderRegex.AMP2.findAndReplaceAll(nodeData, placeholderReolverAmp2);
		nodeData = URx.PlaceholderRegex.AMP.findAndReplaceAll(nodeData, placeholderReolverAmp);

		return nodeData;
	}

}
