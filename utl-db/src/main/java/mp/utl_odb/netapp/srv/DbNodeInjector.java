package mp.utl_odb.netapp.srv;

import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpe.cmsg.NodeData;
import mpe.cmsg.TrackMap;
import mpe.cmsg.core.INode;
import mpe.cmsg.srv.INodeInjector;
import mpe.cmsg.srv.BaseNodeInjector;
import mpe.cmsg.srv.StdNodeInjector;
import mpe.str.URx;
import mpu.pare.Pare;

import java.util.function.Function;

public class DbNodeInjector extends BaseNodeInjector {

	public static void set() {
		INodeInjector.set(new DbNodeInjector());
	}

	private static final Function<String, Object> kvFinder_GNC = (gncKey -> UTree.tree(APP.TREE_GNC()).getValue(gncKey, null));

	private static final Function<String, Object> kvFinder_GNC_GNCJ = (gncKey -> {
		Object gncVal = StdNodeInjector.kvFinder_GNCJ.apply(gncKey);
		if (gncVal != null) {
			return gncVal;
		}
		gncVal = kvFinder_GNC.apply(gncKey);
		if (gncVal != null) {
			return gncVal;
		}
		return null;
	});


//	static {
//		set();
//	}

	public static String inject_PCT(String nodeData) {
		Pare<String, Boolean> injectRslt = URx.PlaceholderRegex.PCT.findAndReplace(nodeData, kvFinder_GNC_GNCJ);
		nodeData = injectRslt.key();
		return nodeData;
	}

	@Override
	public NodeData doInject(INode node, TrackMap.TrackId track) {
		String nodeData = node.readNodeDataStr();
		String data = inject_PCT(nodeData);
		return NodeData.of(node, data, track);
	}
}
