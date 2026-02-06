//package mp.utl_odb.tree.trees.api_expiremental;
//
//
//import mp.utl_odb.tree.ctxdb.CtxtDb;
//import mp.utl_odb.tree.UTree;
//import mpu.IT;
//import mpu.core.ARR;
//import mpu.core.ARRi;
//import mpc.exception.EmptyException;
//import mpc.exception.NI;
//import mpc.exception.WhatIsTypeException;
//import mpu.str.JOIN;
//import mpu.str.STR;
//import mpu.str.UST;
//import mpu.pare.Pare;
//import mpe.checks.UCRest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class UTreeRest2 {
//
//	public static final Logger L = LoggerFactory.getLogger(UTreeRest2.class);
//	public static final String KEY_DEL = ":";
//
//	public static String apply(UTree dataTree, String ownerKey, String[] pathArgs, String pathKey, String val) throws EmptyException {
//		if (L.isDebugEnabled()) {
//			L.debug("Apply url, tree '{}', owner '{}', action '{}', key '{}', val '{}'", dataTree, ownerKey, ARR.as(pathArgs), pathKey, val);
//		}
//
//		String action = pathArgs[0];
//		String action2 = pathArgs[1];
//
//		UCRest.notEmpty400(action, "set path action");
//
//		String key = ownerKey + KEY_DEL + (pathKey == null ? "" : pathKey);
//		String dataRsp = null;
//
//		Pare<Boolean, String> existKey = UTree.containsKey(dataTree, key);
//
//		switch (action) {
//			case "ls":
//			case "create":
//				//ok
//				break;
//
//			case "add":
//			case "set":
//			case "put":
//				UCRest.notEmpty400(val, "set arg value");
//				break;
//
//			default:
//				if (!existKey.key()) {
//					throw new EmptyException();
//				}
//		}
//		switch (action) {
//
//			case "": {
//				dataRsp = dataTree.getOrNull(key);
//				break;
//			}
//
//			case "create"://create?v=line3
//				if (existKey.key()) {
//					dataRsp = existKey.val();
//				} else {
//					dataTree.put(key, val);
//					dataRsp = val;
//				}
//				break;
//
//			case "add"://add?v=line3
//			{
//				String prev = dataTree.getOrNull(key);
//				val = prev == null ? val : prev + STR.NL + val;
//				dataTree.put(key, val);
//				dataRsp = prev;
//				break;
//			}
//
//			case "put"://put?v=line1&nbsp;line2
//			{
//				String prev = dataTree.getOrNull(key);
//				dataTree.put(key, val);
//				dataRsp = prev;
//				break;
//			}
//
//			case "set"://set/1?v=*
//			{
//
//				UCRest.notEmpty400(val, "set 'setted' value");
//
//				Integer index = UST.INT(action2, null);
//				IT.isPosOrZero(index, "set correct line index");
//				List<String> lines = dataTree.getAs(key, List.class, Collections.EMPTY_LIST);
//				ArrayList lines0 = new ArrayList(lines);
//				if (ARR.isIndex(index, lines)) {
//					lines0.add(index, val);
//				} else {
//					lines0.add(val);
//				}
//				dataRsp = JOIN.allByNL(lines0);
//				dataTree.put(key, dataRsp);
//				break;
//			}
//
//			case "get"://get/1
//			{
//				if (action2 == null) {
//					dataRsp = dataTree.getOrNull(key);
//					break;
//				}
//				Integer index = UST.INT(action2, null);
//				IT.isPosOrZero(index, "set correct line index");
//				List<String> lines = dataTree.getAs(key, List.class, Collections.EMPTY_LIST);
//				dataRsp = ARRi.item(lines, index, null);
//				break;
//			}
//
//			case "ls"://ls
//				List<CtxtDb.CtxTimeModel> modelsLikeKey = dataTree.getModelsLikeKey(key);
//				dataRsp = modelsLikeKey.stream().map(m -> m.getKey()).collect(Collectors.joining(STR.NL));
//				break;
//
//			case "del"://del/1
//			case "rm"://rm whole
//			case "clear"://clear val
//				throw new NI("ni:" + action);
////				break;
//			default:
//				throw new WhatIsTypeException("Illegal action '%s'", action);
//		}
//
//		if (L.isInfoEnabled()) {
//			L.info("Apply url success, tree '{}', owner '{}', action '{}', action2 '{}', key '{}', val '{}'\n>>{}", dataTree, ownerKey, action, action2, pathKey, val, dataRsp);
//		}
//
//		return dataRsp;
//	}
//
//
//
//}
