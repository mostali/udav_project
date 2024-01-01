package mp.utl_odb.netapp.mdl;

import lombok.SneakyThrows;
import mpc.arr.Arr;
import mpc.args.ARG;

import mpc.core.EQ;
import mpc.ERR;
import mpc.exception.WrongLogicRuntimeException;
import mpc.rfl.RFL;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mp.utl_odb.netapp.usr.InaUser;
import mpe.NT;
import mp.utl_odb.netapp.NetUsrSrvEE;
import mpt.IUserNet;

import java.util.List;

public class NetUsrId<M extends NetUserModel> extends NetSrcDbId<M, NetUsrSrvEE> implements InaUser {

	public static NetUsrId of(String net_uid, String user_net_id, boolean... checkExist) {
		return of(NT.ofUid(net_uid), user_net_id, checkExist);
	}

	public static NetUsrId def() {
		return of(0L);
	}

	@SneakyThrows
	public static <U extends NetUsrId> U of(Class<U> clazz, Long user_uid, boolean... checkExist) {
		U usr = newId(clazz, user_uid);
		return ARG.isDefEqTrue(checkExist) ? (U) checkExist(usr) : usr;
	}

	public static NetUsrId of(Long user_uid, boolean... checkExist) {
		NetUsrId usr = new NetUsrId(user_uid, (String) null, null);
		return ARG.isDefEqTrue(checkExist) ? (NetUsrId) checkExist(usr) : usr;
	}

	public static NetUsrId of(NetUserModel usrmdl, boolean... checkExist) {
		NetUsrId usr = new NetUsrId(usrmdl.getSid(), usrmdl.getNt(), usrmdl.getUser_nid());
		usr.setLoadedModelCached(usrmdl);
		return ARG.isDefEqTrue(checkExist) ? (NetUsrId) checkExist(usr) : usr;
	}

	public static NetUsrId of(NT nt, long user_nid, boolean... checkExist) {
		return of(nt, String.valueOf(user_nid), checkExist);
	}

	public static NetUsrId of(NT nt, String user_nid, boolean... checkExist) {
		NetUsrId usr = new NetUsrId(null, nt.uidStr(), user_nid);
		return ARG.isDefEqTrue(checkExist) ? (NetUsrId) checkExist(usr) : usr;
	}

	public NetUsrId(Long user_uid, String nt, String user_nid) {
		super(user_uid, nt, user_nid);
	}

	public static String toStringNameNidQk(NetUsrId usr) {
		return usr == null ? null : usr.toStringNameNidQk();
	}

	public List<String> getRoles() {
		return loadModel().getRolesList();
	}

	@SneakyThrows
	public String getUserUidStr() {
		return getUserUid().toString();
	}

	@SneakyThrows
	public Long getUserUid() {
		return getUserUid_();
	}

	@Override
	public List<IUserNet> getUserNets() {
		return Arr.as(new IUserNet() {
			@Override
			public String getNt() {
				return NetUsrId.this.getNtUid();
			}

			@Override
			public String getNid() {
				return NetUsrId.this.getUserNID();
			}

			@Override
			public String getUserUid() {
				return NetUsrId.this.getUserUidStr();
			}
		});
	}

	public Long getUserUid_() throws NetUsrSrvEE {
		return getSid_();
	}

	@SneakyThrows
	public String getUserNID() {
		return getUserNID_();
	}

	public String getUserNID_() throws NetUsrSrvEE {
		return getNid_();
	}

	public String getUserName(int... maxLength) {
		return getName(true, maxLength);
	}

	public void setUserName(String firstName) {
		this.name = ERR.notNull(firstName);
	}

	/**
	 * *************************************************************
	 * ---------------------------- LOADABLE MODEL --------------------------
	 * *************************************************************
	 */
	public M loadModel_(boolean... fresh) throws NetUsrSrvEE {
		if (loadedModelCached != null && ARG.isDefNotEqTrue(fresh)) {
			return loadedModelCached;
		}
		TypeDb db = TypeDbEE.getDbEE(getDbModel());
		if (sid != null) {
			return (M) NetUserModel.loadUserRq_UID(db, sid);
		} else if (nid != null && net != null) {
			return (M) NetUserModel.loadUserRq_USER_NID(db, net, nid);
		} else {
			throw new WrongLogicRuntimeException();
		}
	}

	public Class<M> getDbModel(Class... defRq) {
		return RFL.getGenericType(getClass(), defRq);
	}

	public boolean hasRoleAny(Object... roles) {
		return EQ.equalsStringsAny(getRoles(), true, roles);
	}

}
