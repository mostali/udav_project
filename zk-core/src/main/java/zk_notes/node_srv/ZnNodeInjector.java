package zk_notes.node_srv;

import mp.utl_odb.netapp.srv.DbNodeInjector;
import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpe.cmsg.NodeData;
import mpe.cmsg.TrackMap;
import mpe.cmsg.core.CallMsg;
import mpe.cmsg.core.INode;
import mpe.cmsg.ns.NodeID;
import mpe.cmsg.srv.INodeInjector;
import mpe.cmsg.srv.BaseNodeInjector;
import mpe.cmsg.srv.StdNodeInjector;
import mpe.str.URx;
import mpu.IT;
import mpu.core.ARR;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.eval.EvalNodeService;

import java.util.Map;
import java.util.function.Function;

public class ZnNodeInjector extends BaseNodeInjector {

	public static void set() {
		DbNodeInjector.set(); //init class before, otherwise will be replaced
		INodeInjector.set(new ZnNodeInjector());
	}

	@Override
	public NodeData doInject(INode node, TrackMap.TrackId track) {
		if (node instanceof NodeDir) {
			return inject((NodeDir) node, track);
		}
		return INodeInjector.DEFAULT.doInject(node, track);
	}

	private static <T extends NodeDir> NodeData inject(NodeDir node, TrackMap.TrackId trackId) {

		String data = doInjectImpl(node, null, trackId);

		NodeData<NodeDir> nodeData = NodeData.of(node, data, trackId);

		node.setNodeDataInjected(nodeData);

		return nodeData;
	}

	private static String doInjectImpl(NodeDir node, String nodeData, TrackMap.TrackId trackId) {

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

		nodeData = DbNodeInjector.inject_PCT(nodeData);

		//
		// check #{{ BootContent & application.properties }}

//		Function<String, Object> placeholderResolverBcApKey = (key) -> BootContext.get() == null ? AP.get(key, null) : BootContext.get().get(key, null);
//		nodeData = URx.PlaceholderRegex.NUMSIGN.findAndReplaceAll_Get(nodeData, placeholderResolverBcApKey);

		nodeData = StdNodeInjector.inject_NUMSIGN_BSEA(nodeData).key();

		//
		//check ${{ inner context, query args }}
		nodeData = StdNodeInjector.inject_DOLLAR_MAP(nodeData, callMsg.getHeaders_METAMAP(), mapQueryContext).key();

		//
		//Check @{{ link to node }}

		Function<String, NodeDir> contextNodeGetter = (nodeId) -> {
			NodeID nodeID = NodeID.of(nodeId);
			NodeDir innerNodeDir;
			switch (nodeID.state) {
				case SINGLE:
					innerNodeDir = node.cloneWithItem(nodeId);
					break;
				case ITEM_I_PAGE:
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
			return doInjectImpl(innerNodeDir, vl, trackId);
		};

		nodeData = URx.PlaceholderRegex.DOG_TREE.findAndReplaceAll_Get(nodeData, placeholderReolverDogKey);


		Function<String, Object> placeholderReolverDog = (nodeId) -> {
			NodeDir innerNodeDir = contextNodeGetter.apply(nodeId);
			IT.state(innerNodeDir.existNode(true), "Node '%s' not found", nodeId);
			//SecMan.isAllowedView(innerNodeDir, true);
			String vl = EvalNodeService.evalNodeByTrackId(innerNodeDir, trackId);
			return doInjectImpl(innerNodeDir, vl, trackId);
		};

		nodeData = URx.PlaceholderRegex.DOG.findAndReplaceAll_Get(nodeData, placeholderReolverDog);

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

		nodeData = URx.PlaceholderRegex.AMP3.findAndReplaceAll_Get(nodeData, placeholderReolverAmp3);
		nodeData = URx.PlaceholderRegex.AMP2.findAndReplaceAll_Get(nodeData, placeholderReolverAmp2);
		nodeData = URx.PlaceholderRegex.AMP.findAndReplaceAll_Get(nodeData, placeholderReolverAmp);

		return nodeData;
	}


}
