package mp.utl_odb.netapp.mdl;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpc.exception.WrongLogicRuntimeException;
import mpc.rfl.RFL;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mpe.NT;

public class NetMemId<M extends NetMemModel> extends NetSrcDbId<M, NetMemSrvEE> {

	public static NetMemId of(Long mem_uid) {
		return new NetMemId(mem_uid, null, null);
	}

	public static NetMemId of(NetMemModel memMdl) {
		NetMemId memId = new NetMemId(memMdl.getMem_uid(), memMdl.getNt(), memMdl.getMem_nid());
		memId.setLoadedModelCached(memMdl);
		return memId;
	}

	public static NetMemId of(NT nt, long mem_nid) {
		return of(nt, String.valueOf(mem_nid));
	}

	public static NetMemId of(NT nt, String mem_nid) {
		return new NetMemId(null, nt.uidStr(), mem_nid);
	}

	@Override
	@Deprecated
	public Long getSid() {
		return super.getSid();
	}

	@Override
	@Deprecated
	public String getNid() {
		return super.getNid();
	}

	public NetMemId(Long mem_uid, String net_uid, String mem_nid) {
		super(mem_uid, net_uid, mem_nid);
	}

	@SneakyThrows
	public Long getMemUid() {
		return getMemUid_();
	}

	public Long getMemUid_() throws NetMemSrvEE {
		return getSid_();
	}

	@SneakyThrows
	public String getMemNID() {
		return getMemNID_();
	}

	public String getMemNID_() throws NetMemSrvEE {
		return getNid_();
	}

	public String getMemName() {
		return loadModel().getNm();
	}

	/**
	 * *************************************************************
	 * ---------------------------- LOADABLE MODEL --------------------------
	 * *************************************************************
	 */
	public M loadModel_(boolean... fresh) throws NetMemSrvEE {
		if (loadedModelCached != null && ARG.isDefNotEqTrue(fresh)) {
			return loadedModelCached;
		}
		Class<M> clazz = RFL.getGenericType(getClass());
		TypeDb db = TypeDbEE.getDbEE(clazz);
		if (sid != null) {
			return (M) NetMemModel.loadMemRq_UID(db, sid);
		} else if (nid != null && net != null) {
			return (M) NetMemModel.loadMemRq_MEM_NID(db, net, nid);
		} else {
			throw new WrongLogicRuntimeException();
		}
	}
}
