package mpe.cmsg.srv;

import lombok.RequiredArgsConstructor;
import mpc.env.APP;
import mpc.json.GsonTree;
import mpc.map.BootContext;
import mpe.cmsg.NodeData;
import mpe.cmsg.TrackMap;
import mpe.cmsg.core.INode;
import mpe.str.URx;
import mpu.pare.Pare;

import java.util.Map;
import java.util.function.Function;

public class StdNodeInjector extends BaseNodeInjector {

	static final GsonTree gncGTree = APP.TREE_GNCJ();

	public static final Function<String, Object> kvFinder_GNCJ = gncKey -> gncGTree.get(gncKey, null);

	public static final Function<String, Object> kvFinder_BSEA = (key) -> {
		BootContext bootContext = BootContext.get();
		if (bootContext == null) {
			return APP.getPropFrom_Sys_Env_AP(key, null);
		}
		return bootContext.get(key, null);
	};

	public static final Function<String, Object> kvFinder_QUEST = (gncKey -> {
		String gncVal = (String) kvFinder_BSEA.apply(gncKey);
		if (gncVal != null) {
			return gncVal;
		}
		gncVal = (String) kvFinder_GNCJ.apply(gncKey);
		return gncVal;
	});

	//
	//

	public static Pare<String, Boolean> inject_NUMSIGN_BSEA(String nodeData) {
		return StdInject.SEA.replaceAll(nodeData);
	}

	public static Pare<String, Boolean> inject_PCT_GNCJ(String nodeData) {
		return StdInject.GNCJ.replaceAll(nodeData);
	}

	public static Pare<String, Boolean> inject_DOLLAR_MAP(String nodeData, Map... context) {
		Function<String, Object> placeholderResolverDollar = URx.PlaceholderRegex.createMapResolver(context);
		return URx.PlaceholderRegex.DOLLAR.findAndReplace(nodeData, placeholderResolverDollar);
	}

	@Override
	public NodeData doInject(INode node, TrackMap.TrackId track) {

		String nodeData = node.readNodeDataStr();

		Pare<String, Boolean> rslt;

		rslt = inject_NUMSIGN_BSEA(nodeData);

		rslt = inject_PCT_GNCJ(rslt.key());

		String rsltVal = rslt.key();

		return NodeData.of(node, rsltVal, track);
	}


	@RequiredArgsConstructor
	public enum StdInject {

		SEA(URx.PlaceholderRegex.NUMSIGN, kvFinder_BSEA), //
		GNCJ(URx.PlaceholderRegex.PCT, kvFinder_GNCJ), //
		ANY(URx.PlaceholderRegex.QUEST, kvFinder_QUEST), //
		;

		private final URx.PlaceholderRegex pattern;
		private final Function<String, Object> placeholderResolverFunc;

		public Pare<String, Boolean> replaceAll(String nodeData) {
			return pattern.findAndReplace(nodeData, placeholderResolverFunc);
		}

	}
}
