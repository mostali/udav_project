package mp.utl_odb.netapp.mdl;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpc.exception.WrongLogicRuntimeException;
import mpc.exception.NI;
import mpc.rfl.RFL;
import mpu.str.UST;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mpe.NT;

public class NetGrpId<M extends NetGrpModel> extends NetSrcDbId<M, NetGrpSrvEE> {

	public static NetGrpId of(Long grp_uid) {
		return new NetGrpId(grp_uid, null, null);
	}

	public static NetGrpId of(NetGrpModel grpMdl) {
		NetGrpId grpId = new NetGrpId(grpMdl.getSid(), grpMdl.getNt(), grpMdl.getNid());
		grpId.setLoadedModelCached(grpMdl);
		return grpId;
	}

	public static NetGrpId of(NT nt, long grp_nid) {
		return of(nt, String.valueOf(grp_nid));
	}

	public static NetGrpId of(NT nt, String grp_nid) {
		return new NetGrpId(null, nt.uidStr(), grp_nid);
	}


	public NetGrpId(Long grp_uid, String net_uid, String grp_nid) {
		super(grp_uid, net_uid, grp_nid);
	}

	public static NetGrpId ofVk(Integer grNid) {
		return NetGrpId.of(NT.VK, grNid.toString());
	}

	/**
	 * *************************************************************
	 * ---------------------------- GRP_ID -----------------------
	 * *************************************************************
	 */
	@SneakyThrows
	public Long getGroupUid() {
		return getGroupUid_();
	}

	public Long getGroupUid_() throws NetGrpSrvEE {
		return super.getSid_();
	}

	/**
	 * *************************************************************
	 * ---------------------------- GRP_NID -----------------------
	 * *************************************************************
	 */
	@SneakyThrows
	public String getGroupNID() {
		return getGroupNID_();
	}

	public String getGroupNID_() throws NetGrpSrvEE {
		return getNid_();
	}

	/**
	 * *************************************************************
	 * ---------------------------- API --------------------------
	 * *************************************************************
	 */

	public int getGroupNIDint() {
		return (int) (long) getGroupNIDlong();
	}

	@SneakyThrows
	public Integer getGroupNIDint(boolean... checkNegative) {
		return ARG.isDefEqTrue(checkNegative) ? IT.isNegNotZero(getGroupNIDint()) : getGroupNIDint();
	}

	@SneakyThrows
	public Long getGroupNIDlong(boolean... checkNegative) {
		return ARG.isDefEqTrue(checkNegative) ? IT.isNegNotZero(getGroupNIDlong_()) : getGroupNIDlong_();
	}

	public Long getGroupNIDlong_(Long... defRq) throws NetGrpSrvEE {
		return UST.LONG(getGroupNID_(), defRq);
	}

	public String getLink() {
		switch (NT.ofUid(getNt())) {
			case VK:
				return "https://vk.com/club" + Math.abs(getGroupNIDlong());
			default:
				throw new NI();
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- LOADABLE MODEL --------------------------
	 * *************************************************************
	 */
	public M loadModel_(boolean... fresh) throws NetGrpSrvEE {
		if (loadedModelCached != null && ARG.isDefNotEqTrue(fresh)) {
			return loadedModelCached;
		}
		Class<M> clazz = RFL.getGenericType(getClass());
		TypeDb db = TypeDbEE.getDbEE(clazz);
		if (sid != null) {
			return loadedModelCached = (M) NetGrpModel.loadGrpRq_UID(db, sid);
		} else if (nid != null && net != null) {
			return loadedModelCached = (M) NetGrpModel.loadGrpRq_GRP_NID(db, net, nid);
		} else {
			throw new WrongLogicRuntimeException();
		}
	}

}
