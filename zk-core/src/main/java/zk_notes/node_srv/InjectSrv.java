package zk_notes.node_srv;

import mp.utl_odb.netapp.srv.InjectSrvBase;
import mpc.env.AP;
import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpc.map.BootContext;
import mpe.str.URx;
import mpe.cmsg.core.CallMsg;
import mpu.IT;
import mpu.core.ARR;
import mpe.cmsg.ns.NodeID;
import zk_notes.node.NodeDir;
import mpe.cmsg.NodeData;
import mpe.cmsg.TrackMap;

import java.util.Map;
import java.util.function.Function;

public class InjectSrv extends InjectSrvBase {

	public static <T extends NodeDir> NodeData inject(NodeDir node, TrackMap.TrackId trackId) {

//		NodeDir node = (NodeDir) iNode;

		String data = doInject(node, null, trackId);

		NodeData<NodeDir> nodeData = NodeData.of(node, data, trackId);

		node.setNodeDataInjected(nodeData);

		return nodeData;
	}

	private static String doInject(NodeDir node, String nodeData, TrackMap.TrackId trackId) {

		Map mapQueryContext;
		if (trackId == null) {
			mapQueryContext = ARR.EMPTY_MAP;
			L.info("Inject node [{}] init", node.nodeName());
		} else {
			mapQueryContext = trackId.getContext();
			L.info("Inject node [{}] with map {}", node.nodeName(), mapQueryContext);
		}

		nodeData = nodeData == null ? node.nodeDataStr() : nodeData;

		CallMsg callMsg = CallMsg.ofData(nodeData);

		//
		// check %{{ GlobalNodeContext }}

		nodeData = applyPct_GncPattern(nodeData);

		//
		// check #{{ BootContent & application.properties }}

		Function<String, Object> placeholderResolverBcApKey = (key) -> BootContext.get() == null ? AP.get(key, null) : BootContext.get().get(key, null);
		nodeData = URx.PlaceholderRegex.NUMSIGN.findAndReplaceAll(nodeData, placeholderResolverBcApKey);

		//
		//check ${{ inner context, query args }}

		Function<String, Object> placeholderResolverDollar = URx.PlaceholderRegex.createMapResolver(callMsg.getHeaders_METAMAP(), mapQueryContext);
		nodeData = URx.PlaceholderRegex.DOLLAR.findAndReplaceAll(nodeData, placeholderResolverDollar);

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
			String vl = EvalNodeService.evalNodeByTrackId(innerNodeDir, trackId);
			return InjectSrv.doInject(innerNodeDir, vl, trackId);
		};

		nodeData = URx.PlaceholderRegex.DOG_TREE.findAndReplaceAll(nodeData, placeholderReolverDogKey);


		Function<String, Object> placeholderReolverDog = (nodeId) -> {
			NodeDir innerNodeDir = contextNodeGetter.apply(nodeId);
			IT.state(innerNodeDir.existNode(true), "Node '%s' not found", nodeId);
			//SecMan.isAllowedView(innerNodeDir, true);
			String vl = EvalNodeService.evalNodeByTrackId(innerNodeDir, trackId);
			return InjectSrv.doInject(innerNodeDir, vl, trackId);
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

	public static void initDefaultService() {
		NodeData.injectorSrv = (node, track) -> InjectSrv.inject((NodeDir) node, track);
	}
}
