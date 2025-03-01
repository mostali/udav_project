package mp.utl_odb.tree.trees.api_expiremental;


import mp.utl_odb.tree.CtxtDb;
import mp.utl_odb.tree.UTree;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpe.checks.UCRest;
import mpc.exception.EmptyException;
import mpu.pare.Pare;
import mpu.str.JOIN;
import mpu.IT;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpu.str.STR;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UTreeRest {

	public static final Logger L = LoggerFactory.getLogger(UTreeRest.class);
	public static final String KEY_DEL = ":";

	public static String apply(UTree tree, String oper, String key, String val) throws EmptyException {
		if (L.isDebugEnabled()) {
			L.debug("Apply url, tree '{}',  oper '{}', key '{}', val '{}'", tree, oper, key, val);
		}

		String action2 = null;

		UCRest.notEmpty400(oper, "set path action");

		String dataRsp = null;

		CtxtDb.CtxTimeModel targetVl = null;

		switch (oper) {
			case "ls":
			case "create":
				//ok
				break;

			case "add":
			case "set":
			case "put":
//				UCRest.notEmpty400(val, "set arg value");
				break;

			default:
				targetVl = tree.getCtxTimeModelByKey(key);
				if (targetVl == null) {
					throw new EmptyException();
				}
		}

		switch (oper) {

			case "": {
				dataRsp = tree.getOrNull(key);
				break;
			}

			case "add"://add?v=line3
			{
				String prev = tree.getOrNull(key);
				val = prev == null ? val : prev + STR.NL + val;
				tree.add(key, val, null);
				dataRsp = prev;
				break;
			}

			case "put"://put?v=line1&nbsp;line2
			{
				String prev = tree.getOrNull(key);
				tree.put(key, val);
				dataRsp = prev;
				break;
			}

			case "set"://set?v=*
			{
				NI.stop();
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
				break;
			}

			case "get"://get/1
			{
				dataRsp = IT.NN(targetVl).getValue() == null ? "null" : targetVl.getValue();
				break;
			}

			case "ls"://ls
				List<CtxtDb.CtxTimeModel> modelsLikeKey = tree.getModelsLikeKey(key);
				dataRsp = modelsLikeKey.stream().map(m -> m.getKey()).collect(Collectors.joining(STR.NL));
				break;

			case "del"://del/1
			case "rm"://rm whole
			case "clear"://clear val
				throw new NI("ni:" + oper);
//				break;
			default:
				throw new WhatIsTypeException("Illegal action '%s'", oper);
		}

		if (L.isInfoEnabled()) {
			L.info("Apply url success, tree '{}', owner '{}', action '{}', key '{}', val '{}'\n>>{}", tree, oper,  key, val, dataRsp);
		}

		return dataRsp;
	}

	public static class APIP {

	}

}
