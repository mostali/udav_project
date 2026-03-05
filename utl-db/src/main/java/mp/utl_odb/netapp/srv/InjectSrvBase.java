package mp.utl_odb.netapp.srv;

import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpe.str.URx;

import java.util.function.Function;

public class InjectSrvBase {


	public static String applyPct_GncPattern(String nodeData) {
		Function<String, Object> placeholderResolverPct = (gncKey -> UTree.tree(APP.TREE_GNC()).getValue(gncKey, null));
		nodeData = URx.PlaceholderRegex.PCT.findAndReplaceAll(nodeData, placeholderResolverPct);
		return nodeData;
	}

}
