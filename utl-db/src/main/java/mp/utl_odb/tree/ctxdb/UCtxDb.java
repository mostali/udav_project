package mp.utl_odb.tree.ctxdb;

import lombok.SneakyThrows;
import mp.utl_odb.DBU;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.trees.UTreeAutoCleaner;
import mpc.exception.WhatIsTypeException;
import mpc.rfl.RFL;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARR;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UCtxDb {

	public static void main(String[] args) {
		List<String> strings = DBU.getAllFieldNames(Ctx5Db.CtxModel5.class);
		X.exit(strings);
	}

	public static final Logger L = LoggerFactory.getLogger(UCtxDb.class);

	public static void writeTo(ICtxDb tree, Map<String, ?> map) {
		map.forEach((k, v) -> tree.putAppend(k, v));
	}

	@SneakyThrows
	public static <M extends ICtxDb.CtxModel> M prepareCtxModel_ExistOrNew(ICtxDb<M> ctxDb, CKey key, CKey... values) {
		ICtxDb.CtxModel mdl;
		switch (ctxDb.getUpdateMode()) {
			case ALWAYSLAST:
			case PUT:
				mdl = ctxDb.getModelBy(key);
				break;
			case ADD:
				mdl = null;
				break;
			default:
				throw new WhatIsTypeException(ctxDb.getUpdateMode());
		}
		if (mdl == null) {
			mdl = ctxDb.getModelClass().getConstructor().newInstance();
			mdl.setKey(key.val);
		}
		for (CKey val : values) {
			if (val == null) {
				continue;
			}
			mdl.setColValue(val);
		}
		return (M) mdl;
	}

//	public static <M extends AModel> void saveModelAsUpdate(ICtxDb ctxDb, M model) {
//	}

//	public static <M extends AModel> void saveModelAsCreateOrUpdate(ICtxDb ctxDb, M model) {
//		DBU.createOrUpdateModel(ctxDb.getDbUrl(), model);
//	}

	public static <M extends ICtxDb.CtxModel> void afterPut(ICtxDb<M> ctxDb, M m) {

		if (L.isDebugEnabled()) {
			String shorKey = STR.toStringSE(m.getKey(), 20, m.getKey());
			L.debug(SYMJ.FILE_DB + "Put ctx-model by key '{}' in tree 'file://{}'\n{}", shorKey, ctxDb.getDbFilePath(), m);
		} else if (L.isInfoEnabled()) {
			String shorKey = STR.toStringSE(m.getKey(), 20, m.getKey());
			L.info(SYMJ.FILE_DB + "Put ctx-model by key '{}' in tree 'file://{}'", shorKey, ctxDb.getDbFilePath());
		}

		//checkAutoClean
		if (ctxDb.getAutoCleanCfg_ctr_every_min_max_packet_first0End1() != null) {
			UTreeAutoCleaner.checkAutoClean(ctxDb, ctxDb.getAutoCleanCfg_ctr_every_min_max_packet_first0End1());
		}

	}

	public static void moveModelToLastRowIfExists(ICtxDb ctxDb, ICtxDb.CtxModel mdl) {
		if (mdl.getId() != null) {//it not new
			DBU.removeModelById(ctxDb.getDbUrl(), mdl);
			mdl.setId(null);
		}
	}

	public static <M extends ICtxDb.CtxModel> M getCtxModelBy(ICtxDb<M> iCtxDb, String colName, Object value, Boolean firstLastRandom) {
		return false == iCtxDb.isExistDb() ? null : DBU.getModelFirstOrLastOrRandom(iCtxDb.getDbUrl(), iCtxDb.getModelClass(), firstLastRandom, QP.pEQ(colName, value));
	}

	public static <M extends ICtxDb.CtxModel> M getCtxModelBy(ICtxDb<M> iCtxDb, CKey byKey) {
		return false == iCtxDb.isExistDb() ? null : DBU.getModelBy(iCtxDb.getDbUrl(), iCtxDb.getModelClass(), byKey.colName(), byKey.colVal());
	}

	/**
	 * *************************************************************
	 * -----------------------------  --------------------------
	 * *************************************************************
	 */

	public static List<String> getAllKeysBy(ICtxDb ctxDb, QP... eps) {
		if (false == ctxDb.isExistDb()) {
			return ARR.asAL();
		}
		List<ICtxDb.CtxModel> ms = DBU.getModels(ctxDb.getDbUrl(), ctxDb.getModelClass(), eps);
		return ms.stream().map(e -> e.getKey()).collect(Collectors.toList());
	}

	public static List<String> getAllValuesBy(ICtxDb ctxDb, QP... eps) {
		List<ICtxDb.CtxModel> ms = DBU.getModels(ctxDb.getDbUrl(), ctxDb.getModelClass(), eps);
		return ms.stream().map(e -> e.getValue()).collect(Collectors.toList());
	}

	public static String toString(ICtxDb ctxDb) {
		return SYMJ.FILE_DB + RFL.scn(ctxDb.getModelClass()) + " >> file://" + ctxDb.getDbFilePath().toAbsolutePath();
	}
}
