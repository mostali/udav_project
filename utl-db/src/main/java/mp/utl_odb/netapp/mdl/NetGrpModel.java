package mp.utl_odb.netapp.mdl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mpe.str.CN;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.typedb.TypeDb;

import java.util.List;

@DatabaseTable
public class NetGrpModel<M extends NetSrcModel> extends NetSrcModel<M> {

	private static final long serialVersionUID = 1L;

	public long getGroup_uid() {
		return super.getSid();
	}

	public void setGroup_uid(long group_uid) {
		super.setSid(group_uid);
	}

	public String getGroup_nid() {
		return super.getNid();
	}

	public void setGroup_nid(String group_nid) {
		super.setNid(group_nid);
	}

	@Setter
	@Getter
	@DatabaseField
	private String types;

	public List<String> getTypesList() {
		return getStringAsListBySpaceRx(getTypes());
	}

	public NetGrpModel(long id) {
		setId(id);
	}

	public NetGrpModel() {
	}

	/**
	 * *************************************************************
	 * ---------------------------- GRP_NID -----------------------
	 * *************************************************************
	 */

	public static <M extends NetGrpModel> M loadGrpNew_GRP_NID(TypeDb<M> typeDB, String net_uid, String grp_nid) throws NetGrpSrvEE {
		return loadGrpWithCheck_GRP_NID(typeDB, net_uid, grp_nid, true);
	}

	public static <M extends NetGrpModel> M loadGrpRq_GRP_NID(TypeDb<M> typeDB, String net_uid, String grp_nid) throws NetGrpSrvEE {
		return loadGrpWithCheck_GRP_NID(typeDB, net_uid, grp_nid, false);
	}

	private static <M extends NetGrpModel> M loadGrpWithCheck_GRP_NID(TypeDb<M> typeDB, String net_uid, String grp_nid, boolean checkExistOrNotFound, QP... withQps) throws NetGrpSrvEE {
		M m = loadGrp_GRP_NID(typeDB, net_uid, grp_nid, withQps);
		if (checkExistOrNotFound && m != null) {
			throw NetGrpSrvEE.EE.GROUP_EXIST.I("NetUid (%s), GrpNid(%s)", net_uid, grp_nid);
		} else if (!checkExistOrNotFound && m == null) {
			throw NetGrpSrvEE.EE.GROUP_NOT_FOUND.I("NetUid (%s), GrpNid(%s)", net_uid, grp_nid);
		}
		if (m != null) {
			return m;
		}
		M nm = typeDB.newModel();
		nm.setNt(net_uid);
		nm.setGroup_nid(grp_nid);
		return nm;
	}

	public static <M extends NetGrpModel> boolean isGrpExist_GRP_NID(TypeDb<M> typeDB, String net_uid, String grp_nid, QP... with) {
		QP[] qps = {QP.p(CN.NT, net_uid), QP.p(CN.NID, grp_nid)};
		return typeDB.existModel(QP.merge(qps, with));
	}

	private static <M extends NetGrpModel> M loadGrp_GRP_NID(TypeDb<M> typeDB, String net_uid, String grp_nid, QP... with) {
		QP[] qps = {QP.p(CN.NT, net_uid), QP.p(CN.NID, grp_nid)};
		return typeDB.getModel(QP.merge(qps, with));
	}

	/**
	 * *************************************************************
	 * ---------------------------- GRP_UID -----------------------
	 * *************************************************************
	 */

	public static <M extends NetGrpModel> M loadGrpNew_UID(TypeDb<M> typeDB, long grp_uid) throws NetGrpSrvEE {
		return loadGrpWithCheck_UID(typeDB, grp_uid, true);
	}

	public static <M extends NetGrpModel> M loadGrpRq_UID(TypeDb<M> typeDB, long grp_uid, QP... with) throws NetGrpSrvEE {
		return loadGrpWithCheck_UID(typeDB, grp_uid, false, with);
	}

	private static <M extends NetGrpModel> M loadGrpWithCheck_UID(TypeDb<M> typeDB, long grp_uid, boolean existOrNotFound, QP... with) throws NetGrpSrvEE {
		M m = loadGrp_UID(typeDB, grp_uid, with);
		if (existOrNotFound && m != null) {
			throw NetGrpSrvEE.EE.GROUP_EXIST.I();
		} else if (!existOrNotFound && m == null) {
			throw NetGrpSrvEE.EE.GROUP_NOT_FOUND.I();
		}
		if (m != null) {
			return m;
		}
		M nm = typeDB.newModel();
		nm.setGroup_uid(grp_uid);
		return nm;
	}

	public static <M extends NetGrpModel> boolean isGrpExist_UID(TypeDb<M> typeDB, long grp_uid, QP... with) {
		QP[] qps = {QP.p(CN.SID, grp_uid)};
		return typeDB.existModel(QP.merge(qps, with));
	}

	private static <M extends NetGrpModel> M loadGrp_UID(TypeDb<M> typeDB, long grp_uid, QP... with) {
		QP[] qps = {QP.p(CN.SID, grp_uid)};
		return typeDB.getModel(QP.merge(qps, with));
	}


}
