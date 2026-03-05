package mpe.cmsg.core;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.fs.fd.RES;
import mpc.json.AppStdTree;
import mpc.json.GsonMap;
import mpc.json.GsonTree;
import mpc.log.L;
import mpc.rfl.RFL;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
class NodeDescLoader {
	//INIT - from outer file
	//INIT - if has INIT that CURRENT
	final Map<String, INodeDesc> _TYPES;

	public static void initFromGsonTreeLoader(AppStdTree.TYPE type, Map<String, INodeDesc> stdTypesMap) {

		GsonTree gsonTree = type.stdTreeFresh();

		gsonTree.forEach((k, v) -> {
			if (!(v instanceof Map)) {
				return;
			}

			Map objJson = (Map) v;

			INodeDesc.StdTypeIC stdTypeIC = INodeDesc.StdTypeIC.of(objJson);

			INodeDesc v1 = new INodeDesc() {

				@Override
				public String stdTypeUC() {
					return (String) k;
				}

				@Override
				public Class stdTypeSrvClass(Class... defRq) {
					return stdTypeIC.getSrvclass0(defRq);
				}

				@Override
				public Class stdTypeClass(Class... defRq) {
					return stdTypeIC.getStdclass0(defRq);
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

			};

			if (X.nullAll(v1.line0(), v1.sub0())) {
				L.warn("NodeDEsc without required someone attr line0|sub0");
				return;
			}

			stdTypesMap.put((String) k, v1);

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
		fromClassesLoader(_TYPES);
		_TYPES.forEach((k, v) -> {
			AppStdTree.put(AppStdTree.TYPE.CURRENT, v);
		});
	}

	private static void fromClassesLoader(Map<String, INodeDesc> stdTypesCache) {

		List<Class<CallMsg>> allCallMsgClass = CallMsg.getAllClassesSys();

		allCallMsgClass.forEach(clz -> {

			//

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

			stdTypesCache.put(keyUC, new INodeDesc() {

				@Override
				public Class stdTypeSrvClass(Class... defRq) {
					return null;//
				}

				@Override
				public String stdTypeUC() {
					return keyUC;
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

			});
		});
	}


}
