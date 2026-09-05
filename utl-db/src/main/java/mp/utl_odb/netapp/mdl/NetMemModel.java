package mp.utl_odb.netapp.mdl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mpe.str.CN;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.mdl.ext_um1.UniOldModel;

import java.util.List;

@DatabaseTable
public class NetMemModel<M extends UniOldModel> extends NetSrcModel<M> {

	private static final long serialVersionUID = 1L;

	public long getMem_uid() {
		return super.getSid();
	}

	public NetMemModel setMem_uid(long user_uid) {
		super.setSid(user_uid);
		return this;
	}

	public String getMem_nid() {
		return super.getNid();
	}


	public NetMemModel setMem_nid(String nid) {
		super.setNid(nid);
		return this;
	}

	@Setter
	@Getter
	@DatabaseField
	private String types;

	public List<String> getTypesList() {
		return getStringAsListBySpaceRx(getTypes());
	}

	public NetMemModel(long id) {
		setId(id);
	}

	public NetMemModel() {
	}

	/**
	 * *************************************************************
	 * ---------------------------- MEM_NID -----------------------
	 * *************************************************************
	 */

	public static <M extends NetMemModel> M loadMemNew_MEM_NID(TypeDb<M> typeDB, String net_uid, String mem_nid) throws NetMemSrvEE {
		return loadMemWithCheck_MEM_NID(typeDB, net_uid, mem_nid, true);
	}

	public static <M extends NetMemModel> M loadMemRq_MEM_NID(TypeDb<M> typeDB, String net_uid, String mem_nid) throws NetMemSrvEE {
		return loadMemWithCheck_MEM_NID(typeDB, net_uid, mem_nid, false);
	}

	private static <M extends NetMemModel> M loadMemWithCheck_MEM_NID(TypeDb<M> typeDB, String net_uid, String mem_nid, boolean checkExistOrNotFound) throws NetMemSrvEE {
		M m = loadMem_MEM_NID(typeDB, net_uid, mem_nid);
		if (checkExistOrNotFound && m != null) {
			throw NetMemSrvEE.EE.MEM_EXIST.I("NetUid (%s), MemNid(%s)", net_uid, mem_nid);
		} else if (!checkExistOrNotFound && m == null) {
			throw NetMemSrvEE.EE.MEM_NOT_FOUND.I("NetUid (%s), MemNid(%s)", net_uid, mem_nid);
		}
		if (m != null) {
			return m;
		}
		M nm = typeDB.newModel();
		nm.setNt(net_uid);
		nm.setMem_nid(mem_nid);
		return nm;
	}

	public static <M extends NetMemModel> boolean isMemExist_MEM_NID(TypeDb<M> typeDB, long net_uid, String mem_nid, QP... with) {
		QP[] qps = {QP.p(CN.NT, net_uid), QP.p(CN.NID, mem_nid)};
		return typeDB.existModel(QP.merge(qps, with));
	}

	private static <M extends NetMemModel> M loadMem_MEM_NID(TypeDb<M> typeDB, String net_uid, String mem_nid, QP... with) {
		QP[] qps = {QP.p(CN.NT, net_uid), QP.p(CN.NID, mem_nid)};
		return typeDB.getModel(QP.merge(qps, with));
	}

	/**
	 * *************************************************************
	 * ---------------------------- MEM_UID -----------------------
	 * *************************************************************
	 */

	public static <M extends NetMemModel> M loadMemNew_UID(TypeDb<M> typeDB, long mem_uid) throws NetMemSrvEE {
		return loadMemWithCheck_UID(typeDB, mem_uid, true);
	}

	public static <M extends NetMemModel> M loadMemRq_UID(TypeDb<M> typeDB, long mem_uid) throws NetMemSrvEE {
		return loadMemWithCheck_UID(typeDB, mem_uid, false);
	}

	private static <M extends NetMemModel> M loadMemWithCheck_UID(TypeDb<M> typeDB, long mem_uid, boolean existOrNotFound) throws NetMemSrvEE {
		M m = loadMem_UID(typeDB, mem_uid);
		if (existOrNotFound && m != null) {
			throw NetMemSrvEE.EE.MEM_EXIST.I();
		} else if (!existOrNotFound && m == null) {
			throw NetMemSrvEE.EE.MEM_NOT_FOUND.I();
		}
		if (m != null) {
			return m;
		}
		M nm = typeDB.newModel();
		nm.setMem_uid(mem_uid);
		return nm;
	}

	public static <M extends NetMemModel> boolean isMemExist_UID(TypeDb<M> typeDB, long mem_uid, QP... with) {
		QP[] qps = {QP.p(CN.SID, mem_uid)};
		return typeDB.existModel(QP.merge(qps, with));
	}

	private static <M extends NetMemModel> M loadMem_UID(TypeDb<M> typeDB, long mem_uid, QP... with) {
		QP[] qps = {QP.p(CN.SID, mem_uid)};
		return typeDB.getModel(QP.merge(qps, with));
	}

	@Override
	public String toString() {
		return SYM_PARENT + "NetMemModel{" +
				"mem_uid='" + getMem_uid() + '\'' +
				", '" + super.toString() + '\'' +
				'}';
	}
}
