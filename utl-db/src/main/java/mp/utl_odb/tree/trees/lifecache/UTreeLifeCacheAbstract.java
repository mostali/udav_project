package mp.utl_odb.tree.trees.lifecache;

import lombok.Getter;
import mp.utl_odb.tree.ctxdb.CKey;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mpc.exception.WhatIsTypeException;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.QDate;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public abstract class UTreeLifeCacheAbstract extends Ctx3Db {

	public static final Logger L = LoggerFactory.getLogger(UTreeLifeCacheAbstract.class);

	public UTreeLifeCacheAbstract(Path path) {
		super(path);
	}

	public enum CacheType {
		SHORT_LIFE, EVERYMS
	}

	public abstract CacheType cacheType();

	public Pare<Boolean, CtxModelCtr> checkIsAllowedOrReg(String key, long life_ms, boolean... RETURN) throws ModelLifeMsException {
		boolean isReturn = ARG.isDefEqTrue(RETURN);
		try {
			CtxModelCtr model = getModel_WithLife(CKey.of(key), life_ms);
			if (model == null) {
				return Pare.of(true, put(key));
			}
			return Pare.of(true, model);
		} catch (ModelLifeMsException e) {
			if (isReturn) {
				return Pare.of(false, e.getTimeModel());
			}
			throw e;
		}
	}

	public CtxModelCtr getModel_WithLife(CKey key, long lifeMs) throws ModelLifeMsException {
		return getModel_WithLife(this, key, lifeMs, cacheType(), false);
	}

	private static CtxModelCtr getModel_WithLife(Ctx3Db db, CKey key, long maxLifeMs, CacheType cacheType, boolean lastModel) throws ModelLifeMsException {
		CtxModelCtr currentModel = db.getModelBy(key, lastModel);
		if (currentModel == null) {
			return null;
		}
//		else if (maxLifeMs < 1) {
//			return currentModel;
//		}
		IT.isPosNotZero(maxLifeMs);
		QDate dateUdpated = currentModel.getTimeAsQDate();
		switch (cacheType) {
			case SHORT_LIFE:
				if (System.currentTimeMillis() - dateUdpated.ms() <= maxLifeMs) {
					return currentModel;
				}
				break;
			case EVERYMS:
				if (System.currentTimeMillis() - dateUdpated.ms() > maxLifeMs) {
					CtxModel.updateTime(currentModel);
					db.saveModelAsUpdate(currentModel);
					return currentModel;
				}
				break;
			default:
				throw new WhatIsTypeException(cacheType);
		}
		throw new ModelLifeMsException(currentModel, maxLifeMs);
	}

	public static class ModelLifeMsException extends Exception {

		private static final long serialVersionUID = 1L;
		private final @Getter CtxModelCtr timeModel;
		final long cacheLock;

		public long getLastUpdateMs() {
			return timeModel.getTime();
		}

		protected ModelLifeMsException(CtxModelCtr mod, long lifeCache) {
			super(lifeCache + "");
			this.timeModel = mod;
			this.cacheLock = lifeCache;
		}

	}

//	public interface ILifeCache {
//		long getLifeCacheMs();
////		String getLifeCacheKey();
//
//		CacheType getLifeCacheType();
//	}

}
