package mpe.cmsg.core;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.exception.NI;
import mpc.fs.fd.RES;
import mpc.json.AppStdTree;
import mpc.json.GsonMap;
import mpc.json.GsonTree;
import mpc.log.L;
import mpc.rfl.RFL;
import mpu.IT;
import mpu.X;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
class NodeDescLoader {
	//INIT - from outer file
	//INIT - if has INIT that CURRENT
	final Map<String, INodeDesc> _TYPES;

	public static void initFromGsonTreeLoader(AppStdTree.TYPE type, Map<String, INodeDesc> stdTypesMap) {

		GsonTree gsonTree = type.stdTreeFresh();

		gsonTree.forEach((key, json) -> {
			if (!(json instanceof Map)) {
				return;
			}

			Map objJson = (Map) json;

			INodeDesc.StdTypeIC stdTypeIC = INodeDesc.StdTypeIC.of(objJson);

			INodeDesc v1 = new NodeDescStd((String) key, stdTypeIC, objJson);

			if (X.nullAll(v1.line0(), v1.sub0())) {
				L.warn("NodeDEsc without required someone attr line0|sub0");
				return;
			}

			stdTypesMap.put((String) key, v1);

		});


		List<Class<CallMsg>> allCallMsgClass = CallMsg.getAllClassesSys();

		allCallMsgClass.forEach(clz -> {

			String key0 = (String) RFL.fieldValueSt(clz, NodeDescCache.CMF_KEY, true, null);
			if (key0 == null) {
				key0 = INodeDesc.getNameFromClass(clz, null);
			}
			if (X.empty(key0)) {
				L.warn("Skip CallMsg '%s' - key field not found, or name class wo name", clz);
				return;
			}

			String keyUC = key0.toUpperCase();

			//
			//

			//try get field line0, otherwise except sub0 type
			String line0 = (String) RFL.fieldValueSt(clz, NodeDescCache.CMF_LINE0, true, null);
			Class sub0;
			if (line0 == null) {
				sub0 = (Class) RFL.fieldValueSt(clz, NodeDescCache.CMF_SUB0, true, null);
			} else {
				sub0 = null;
			}

			//
			//

			if (X.nullAll(line0, sub0)) {
				L.warn("Skip CallMsg '%s' - LINE0 & SUB0 is empty", clz);
				return;
			}


		});
	}

	public boolean hasAfterInit() {
		return AppStdTree.TYPE.AFTERINIT.exist();
	}

	public boolean hasInit() {
		return AppStdTree.TYPE.INIT.exist();
	}

	@SneakyThrows
	public void applyAfterinitMode() {

		applySysJsonMode();

//		String rsp = JHttp.GET_BODY("http://q.com:8083/_api/stdtypes/*/PUBL", String.class, 200);
//		NodeReg.regStdTypeFromCallMsg(rsp);
//
//		rsp = JHttp.GET_BODY("http://q.com:8083/_api/stdtypes/*/JQL", String.class, 200);
//		NodeReg.regStdTypeFromCallMsg(rsp);
	}

	@SneakyThrows
	public void applySysJsonMode() {
		String res = RES.of(StdType.class, "/etc/stdtypes/stdtypes.json").cat_(null);
		GsonMap gsonMap = GsonMap.of(res);

		//copy to FS native version
		AppStdTree.TYPE.CURRENT.onMoveDataHere(gsonMap);

		initFromGsonTreeLoader(AppStdTree.TYPE.CURRENT, _TYPES);

		//check Zservices
//		_TYPES.forEach((k, v) -> {
//			if (v.stdTypeSrvClass(null) != null) {
//				NodeReg.regStdTypeFromCallMsg(v.toNodeType());
//			}
//		});

		L.info("Apply sys json mode");
//		toStringLog();
	}

	private static void toStringLog() {
//		NodeReg.toStringLog(0);
		NodeDescCache.toStringLog(0);
	}

	public void applyInitMode() {

		AppStdTree.TYPE.INIT.onMoveToCurrent();

		initFromGsonTreeLoader(AppStdTree.TYPE.CURRENT, _TYPES);

	}

	public void applySysMode() {
		NI.stop();
		fromClassesLoader(_TYPES);
		_TYPES.forEach((k, v) -> {
			AppStdTree.put(AppStdTree.TYPE.CURRENT, v);
		});
	}

	private static void fromClassesLoader(Map<String, INodeDesc> stdTypesCache) {
		NI.stop();
		List<Class<CallMsg>> allCallMsgClass = CallMsg.getAllClassesSys();

		allCallMsgClass.forEach(clz -> {

			String key0 = (String) RFL.fieldValueSt(clz, NodeDescCache.CMF_KEY, true, null);
			if (key0 == null) {
				key0 = INodeDesc.getNameFromClass(clz, null);
			}
			if (X.empty(key0)) {
				L.warn("Skip CallMsg '%s' - key field not found, or name class wo name", clz);
				return;
			}

			//
			//

			//try get field line0, otherwise except sub0 type
			String line0 = (String) RFL.fieldValueSt(clz, NodeDescCache.CMF_LINE0, true, null);
			Class sub0;
			if (line0 == null) {
				sub0 = (Class) RFL.fieldValueSt(clz, NodeDescCache.CMF_SUB0, true, null);
			} else {
				sub0 = null;
			}

			//
			//

			if (X.nullAll(line0, sub0)) {
				L.warn("Skip CallMsg '%s' - LINE0 & SUB0 is empty", clz);
				return;
			}

			String keyLC = key0;
			String keyUC = key0.toUpperCase();

			stdTypesCache.put(keyUC, new NodeDescStruct(keyUC, keyLC, clz, line0, sub0));
		});
	}


	private static class NodeDescStruct implements INodeDesc {

		private final String keyUC;
		private final String keyLC;
		private final Class<CallMsg> clz;
		private final String line0;
		private final Class sub0;

		public NodeDescStruct(String keyUC, String keyLC, Class<CallMsg> clz, String line0, Class sub0) {
			this.keyUC = keyUC;
			this.keyLC = keyLC;
			this.clz = clz;
			this.line0 = line0;
			this.sub0 = sub0;
		}

		@Override
		public Class stdTypeSrvClass(Class... defRq) {
			return null;//
		}

		@Override
		public String stdTypeUC() {
			return keyUC;
		}

		@Override
		public String stdTypeLC() {
			return keyLC;
		}

		@Override
		public Class stdTypeClass(Class... defRq) {
			return clz;
		}

		@Override
		public String line0() {
			return line0;
		}

		@Override
		public Class sub0() {
			return sub0;
		}

		@Override
		public Object holder() {
			return clz;
		}

		@Override
		public Map<String, Object> props() {
			return Map.of();
		}

	}

	private static class NodeDescStd implements INodeDesc {

		private final String nameUc;
		private final StdTypeIC stdTypeIC;
		private final Map objJson;

		public NodeDescStd(String nameUc, StdTypeIC stdTypeIC, Map objJson) {
			this.nameUc = nameUc;
			this.stdTypeIC = stdTypeIC;
			IT.NN(stdTypeIC.getStdclass(null), "except stdType class impl for '%s', props\n%s", nameUc, objJson);
			IT.state(stdTypeIC.getStdclass0() != CallMsg.class, "except correct stdType class impl for '%s', props\n%s", nameUc, objJson);
			this.objJson = objJson;
		}

		@Override
		public String toString() {
			String str = NodeDescStd.class.getSimpleName() + ":" + stdTypeUC() + ":" + RFL.scn(stdTypeClass(null), null) + ":" + RFL.scn(stdTypeSrvClass(null), null) + "::" + line0() + "->" + sub0();
			return str;
		}

		@Override
		public String stdTypeUC() {
			return nameUc;
		}

		@Override
		public Class stdTypeClass(Class... defRq) {
			return stdTypeIC.getStdclass0(defRq);
		}

		@Override
		public Class stdTypeSrvClass(Class... defRq) {
			return stdTypeIC.getSrvclass0(defRq);
		}


		@Override
		public String line0() {
			return stdTypeIC.getLine0(null);
		}

		@Override
		public Class sub0() {
			String sub0 = stdTypeIC.getSub0(null);
			return sub0 == null ? null : RFL.clazz(sub0);
		}

		@Override
		public Object holder() {
			return stdTypeIC;
		}

		@Override
		public Map<String, Object> props() {
			return (Map<String, Object>) objJson.get("props");
		}

	}
}
