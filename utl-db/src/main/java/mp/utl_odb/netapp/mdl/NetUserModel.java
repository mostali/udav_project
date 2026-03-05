package mp.utl_odb.netapp.mdl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mpu.core.ARG;
import mpe.str.CN;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.netapp.usr.InaUser;
import mp.utl_odb.netapp.NetUsrSrvEE;

import java.util.List;

@DatabaseTable //(tableName = NetUserModel.TABLE)
public class NetUserModel<M extends NetSrcModel> extends NetSrcModel<M> {

	public static final String TABLE = "appusr";

	private static final long serialVersionUID = 1L;

	public long getUser_uid() {
		return super.getSid();
	}

	public String getUser_nid() {
		return super.getNid();
	}

	public NetSrcModel setUser_uid(long user_uid) {
		super.setSid(user_uid);
		return this;
	}

	public NetSrcModel setUser_nid(String nid) {
		super.setNid(nid);
		return this;
	}

	public String getUser_name() {
		return super.getNm();
	}

	@Setter
	@Getter
	@DatabaseField
	private String roles;

	public List<String> getRolesList() {
		return getStringAsListBySpaceRx(getRoles());
	}

	public NetUserModel(long id) {
		setId(id);
	}

	public NetUserModel() {
	}

	/**
	 * *************************************************************
	 * ---------------------------- USER_NID -----------------------
	 * *************************************************************
	 */


	public static <M extends NetUserModel> M loadUserNew_USER_NID(TypeDb<M> typeDB, String net_uid, String user_nid) throws NetUsrSrvEE {
		return loadUserWithCheck_USER_NID(typeDB, net_uid, user_nid, true);
	}

	public static <M extends NetUserModel> M loadUserRq_USER_NID(TypeDb<M> typeDB, String net_uid, String user_nid) throws NetUsrSrvEE {
		return loadUserWithCheck_USER_NID(typeDB, net_uid, user_nid, false);
	}

	private static <M extends NetUserModel> M loadUserWithCheck_USER_NID(TypeDb<M> typeDB, String net, String user_nid, boolean checkExistOrNotFound) throws NetUsrSrvEE {
		M m = loadUser_USER_NID(typeDB, net, user_nid);
		if (checkExistOrNotFound && m != null) {
			throw NetUsrSrvEE.EE.USER_EXIST.I(InaUser.toStringNetId(net, user_nid));
		} else if (!checkExistOrNotFound && m == null) {
			throw NetUsrSrvEE.EE.USER_NOT_FOUND.I("NetUid (%s), UserNid(%s)", net, user_nid);
		}
		if (m != null) {
			return m;
		}
		M nm = typeDB.newModel();
		nm.setNt(net);
		nm.setUser_nid(user_nid);
		return nm;
	}

	public static <M extends NetUserModel> boolean isUserExist_USER_NID(TypeDb<M> typeDB, long net_uid, String user_nid, QP... with) {
		QP[] qps = {QP.p(CN.NT, net_uid), QP.p(CN.NID, user_nid)};
		return typeDB.existModel(QP.merge(qps, with));
	}

	private static <M extends NetUserModel> M loadUser_USER_NID(TypeDb<M> typeDB, String net_uid, String user_nid, QP... with) {
		QP[] qps = {QP.p(CN.NT, net_uid), QP.p(CN.NID, user_nid)};
		return typeDB.getModel(QP.merge(qps, with));
	}

	/**
	 * *************************************************************
	 * ---------------------------- USER_UID -----------------------
	 * *************************************************************
	 */

	public static <M extends NetUserModel> M loadUserNew_UID(TypeDb<M> typeDB, long user_uid) throws NetUsrSrvEE {
		return loadUserWithCheck_UID(typeDB, user_uid, true);
	}

	public static <M extends NetUserModel> M loadUserRq_UID(TypeDb<M> typeDB, long user_uid) throws NetUsrSrvEE {
		return loadUserWithCheck_UID(typeDB, user_uid, false);
	}

	private static <M extends NetUserModel> M loadUserWithCheck_UID(TypeDb<M> typeDB, long user_uid, boolean existOrNotFound) throws NetUsrSrvEE {
		M m = loadUser_UID(typeDB, user_uid);
		if (existOrNotFound && m != null) {
			throw NetUsrSrvEE.EE.USER_EXIST.I("UserUid:" + user_uid);
		} else if (!existOrNotFound && m == null) {
			throw NetUsrSrvEE.EE.USER_NOT_FOUND.I("UserUid:" + user_uid);
		}
		if (m != null) {
			return m;
		}
		M nm = typeDB.newModel();
		nm.setUser_uid(user_uid);
		return nm;
	}

	public static <M extends NetUserModel> boolean isUserExist_UID(TypeDb<M> typeDB, long user_uid, QP... with) {
		QP[] qps = {QP.p(CN.SID, user_uid)};
		return typeDB.existModel(QP.merge(qps, with));
	}

	private static <M extends NetUserModel> M loadUser_UID(TypeDb<M> typeDB, long user_uid, QP... with) {
		QP[] qps = {QP.p(CN.SID, user_uid)};
		return typeDB.getModel(QP.merge(qps, with));
	}

	/**
	 * *************************************************************
	 * ---------------------------- USER_UID -----------------------
	 * *************************************************************
	 */

	NetUsrId usrIdCached;

	public NetUsrId getUsrId(boolean... fresh) {
		boolean isFresh = ARG.isDefEqTrue(fresh);
		if (usrIdCached != null && !isFresh) {
			return usrIdCached;
		}
		usrIdCached = NetUsrId.of(getUser_uid());
		usrIdCached.setLoadedModelCached(this);
		return usrIdCached;
	}

	@Override
	public String toString() {
		return SYM_PARENT + "NetUserModel{" +
				"roles='" + roles + '\'' +
				", " + super.toString() +
				'}';
	}
}
