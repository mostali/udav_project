package mpe.cmsg.biwork;

import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpe.cmsg.std.PublCallMsg;
import mpu.func.FunctionV;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BiWorkerMan {

	public static BiWorker.Status statusWorker(PublCallMsg callMsg) {
		BiWorker worker = new BiWorker(callMsg);
		return worker.getStatus();
	}

	public static void startWorker(PublCallMsg callMsg) {

		final BiWorker _worker = new BiWorker(callMsg);

		Integer oid = callMsg.getDstVkOID();

		FunctionV startNewJob = () -> {

//			PostLastStoreFullSrv.needStopSrv.set(false);
//			;

			_PUBL_WORKERS.put(oid, _worker);
			_worker.startNewJob();
		};

		BiWorker.Status status = statusWorker(callMsg);
		switch (status) {
			case NEW:
				L.info("startWorker [{}] - [{}] starting", status, oid);
				startNewJob.apply();
				return;
			case WORK:
				L.info("startWorker [{}] - [{}] was skip", status, oid);
				return;
			case DONE:
				L.info("startWorker [{}] - [{}] was cleaned", status, oid);
				_PUBL_WORKERS.remove(oid);
				startNewJob.apply();
				return;

			default:
				throw new WhatIsTypeException(status);
		}
	}

	public static void stopWorker(PublCallMsg callMsg) {

		BiWorker.Status status = statusWorker(callMsg);

		Integer oid = callMsg.getDstVkOID();

		switch (status) {

			case NEW:
				L.info("stopWorker [{}] - [{}] already stop", status, oid);
				return;

			case WORK:

				L.info("stopWorker [{}] - [{}] will be stoped", status, oid);

				BiWorker PBWorker = _PUBL_WORKERS.get(oid);

//				PostLastStoreFullSrv.needStopSrv.set(true);

//				PBWorker.getCall().join().iterrupt();

				Object o = PBWorker.getAndThrow();

				L.info("stopWorker [{}] - [{}] was stopped", status, oid);

				return;

			case DONE:
				L.info("stopWorker [{}] - [{}] was cleaned [OK]", status, oid);
				_PUBL_WORKERS.remove(oid);
				return;

			default:
				throw new WhatIsTypeException(status);
		}
	}

	static final ConcurrentMap<Integer, BiWorker> _PUBL_WORKERS = new ConcurrentHashMap<>();


//	public static SiModel getFullStoreModelOrCreate(int ownerId) {
//
//		ICtxDb iCtxDb = FULL_STORE_INDEX_DB();
//
//		ICtxDb.CtxModel modelByKeyOrCreate = iCtxDb.getModelByKeyOrCreate(ownerId + ""); //add
//
//		return SiModel.of(modelByKeyOrCreate);
//	}

//	public static ICtxDb FULL_STORE_INDEX_DB() {
//		return AppCoreVk.APP_CORE.tree(AppCoreVk.NSC_GROUP_STORE_LAST, "store-fullpost-index");
//	}

//	@RequiredArgsConstructor
//	public abstract static class SiModel {
//
//		public static final String IS_STARTED_JOB = "isStartedJob";
//		public final ICtxDb.CtxModel model;
//
//		public abstract ICtxDb getDb();
//
//		public static SiModel of(ICtxDb.CtxModel model) {
//			return new SiModel(model) {
//				@Override
//				public ICtxDb getDb() {
//					return null;
//				}
//			};
//		}
//
//		private @NotNull GsonMap getExtStateOrCreate() {
//			GsonMap as = (GsonMap) model.getAs(OCols.ext, GsonMap.class, null);
//			if (as == null) {
//				as = new GsonMap();
//			}
//			return as;
//		}
//
//		boolean isStartedJob() {
//			return (boolean) getExtStateOrCreate().getAs(IS_STARTED_JOB, Boolean.class, false);
//		}
//
//
//		boolean setProp(String key, boolean startOff) {
//			GsonMap state = getExtStateOrCreate();
//			if (startOff) {
//				state.put(key, true);
//			} else {
//				state.remove(key);
//			}
//			model.setExt(state.toStringPrettyJson());
//			getDb().saveModelAsUpdate(model);
//			return isStartedJob();
//		}
//	}

}
