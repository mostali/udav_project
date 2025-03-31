package mp.utl_odb.tree.trees.api_expiremental;


import mp.utl_odb.tree.ctxdb.CKey;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpe.core.U;
import mpu.IT;
import mpu.X;
import mpu.pare.Pare;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UTreeRest {

	public static final Logger L = LoggerFactory.getLogger(UTreeRest.class);

	public static Pare<Integer, String> apply(ICtxDb tree, String oper, String key, String val) {
		if (L.isDebugEnabled()) {
			L.debug("Apply url, tree '{}',  oper '{}', key '{}', val '{}'", tree, oper, key, val);
		}
		if (!tree.isExistDb()) {
			return Pare.of(400, X.f("Tree not found '%s'", tree.getDbFilePath().getFileName()));
		}
		if (X.empty(oper)) {
			return Pare.of(400, X.f("Set tree operation"));
		}

		int dataRspStatus = 200;
		String dataRsp = null;

		ICtxDb.CtxModel targetVl = null;

		switch (oper) {
			case "ls":
			case "create":
				//ok
				break;

			case "add":
			case "set":
			case "put":
				if (X.isNull(val)) {
					return Pare.of(400, "Set value with query arg 'v' or use POST");
				}
				break;

			case "get":
			default:
				targetVl = tree.getModelByKey(key);
				if (targetVl == null) {
//					throw TreeRestEE.EE.KEY_NOT_FOUND.I(key);
					return Pare.of(400, X.f("Tree key not found.  Key '%s'", key));
				}
		}

		switch (oper) {

			case "": {
				dataRsp = tree.getValueOrNull(key);
				break;
			}

			case "addline"://add?v=line3
			{
				String prev = tree.getValueOrNull(key);
				val = prev == null ? val : prev + STR.NL + val;
				tree.put(key, val, null);
				dataRsp = prev;
				break;
			}

			case "put"://put?v=line1&nbsp;line2
			{
				String prev = tree.getValueOrNull(key);
				tree.put(key, val);
				dataRsp = U.toNullString(prev);
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
				List<Ctx3Db.CtxModelCtr> modelsLikeKey = tree.getModelsLike(CKey.of(key));
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
			L.info("Apply url success, tree '{}', owner '{}', action '{}', key '{}', val '{}'\n>>{}", tree, oper, key, val, dataRsp);
		}

//		if (X.blank(dataRsp)) {
//			dataRspStatus = 400;
//		}
		return Pare.of(dataRspStatus, dataRsp);
	}

	//	public static class TreeRestEE extends EException {
//
//		private static final long serialVersionUID = 1L;
//
//		@Override
//		public EE type() {
//			return super.type(EE.class);
//		}
//
//		public void sendIfStatus204() {
//			switch (type()) {
//				case KEY_NOT_FOUND:
//				case TREE_NOT_EXISTS:
//					throw RestStatusException.OK204(e.getMessage());
//				default:
//					throw e;
//			}
//		}
//
//		public enum EE {
//			NOSTATUS, TREE_NOT_EXISTS, KEY_NOT_FOUND;
//
//			public TreeRestEE I() {
//				return new TreeRestEE(this);
//			}
//
//			public TreeRestEE I(Throwable ex) {
//				TreeRestEE er = new TreeRestEE(this, ex);
//				return er;
//			}
//
//			public TreeRestEE I(String message) {
//				TreeRestEE er = new TreeRestEE(this, new RuntimeException(
//						message));
//				return er;
//			}
//		}
//
//		public TreeRestEE(EE error) {
//			super(error);
//		}
//
//		public TreeRestEE(EE error, Throwable cause) {
//			super(error, cause);
//		}
//
//	}

}
