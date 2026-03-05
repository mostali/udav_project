package mpe.cmsg.core;

import mpu.X;
import mpu.core.ARG;
import mpu.str.Rt;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NodeDescCache {

	private static final Map<String, INodeDesc> CACHE_DESC = new HashMap();

	public static final String CMF_KEY = "KEY";
	public static final String CMF_LINE0 = "LINE0";
	public static final String CMF_SUB0 = "SUB0";

	public static void main(String[] args) {
		Map<String, INodeDesc> allCallMsgClass = TYPES_CACHED();
		X.exit(allCallMsgClass);
	}

	public static Map<String, INodeDesc> TYPES_GET() {
		return CACHE_DESC;
	}

	public static Map<String, INodeDesc> TYPES_CACHED() {
		if (!CACHE_DESC.isEmpty()) {
			return CACHE_DESC;
		}

		loadData();

		return CACHE_DESC;
	}

	private static void loadData() {

		NodeDescLoader loader = new NodeDescLoader(CACHE_DESC);

		loader.applySysJsonMode();

		if (false) {
			if (loader.hasInit()) {

				loader.applyInitMode();

			} else {

				loader.applySysMode();

			}
			boolean hasAfterinit = loader.hasAfterInit();

			if (hasAfterinit) {
				loader.applyAfterinitMode();
			}
		}


	}

	public static INodeDesc getDesc(String name, INodeDesc... defRq) {

		INodeDesc iNodeDesc = TYPES_CACHED().get(name);

		if (iNodeDesc != null) {
			return iNodeDesc;
		}

		return ARG.throwMsg(() -> X.f("Except nodeDesc '%s'", name), defRq);
	}

	public static String loadPropsValueOrDefault(String stdType, String propKey, Supplier<String> defOrThrow) {
		INodeDesc desc = getDesc(stdType, null);
		if (desc != null) {
			String actTitle = (String) desc.props().get(propKey);
			if (X.notEmpty(actTitle)) {
				return actTitle;
			}
		}
		return defOrThrow != null ? defOrThrow.get() : X.throwException("loadPropsOrDefault not found type '%s' key '%s'", stdType, propKey);
	}


	public static String toStringLog(int level) {
//		int l = level;
//		int l2 = l + 1;
//		Sb sb = new Sb();
		Map<String, INodeDesc> map = TYPES_GET();
		return Rt.buildReport(map, "NodeDesc*" + X.sizeOf(map)).toString();
	}

}
