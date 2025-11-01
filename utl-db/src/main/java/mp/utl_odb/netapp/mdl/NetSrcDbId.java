package mp.utl_odb.netapp.mdl;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.IT;
import mpc.exception.EException;
import mpu.str.STR;

public abstract class NetSrcDbId<M extends NetSrcModel, E extends EException> extends NetSrcId {

	public static NetSrcDbId checkExist(NetSrcDbId usr) {
		usr.checkExist();
		return usr;
	}

	public NetSrcDbId(Long sid, String nt, String nid) {
		super(sid, nt, nid);
	}

	/**
	 * *************************************************************
	 * ---------------------------- SRC_UID -----------------------
	 * *************************************************************
	 */
	@Override
	@Deprecated
	@SneakyThrows
	public Long getSid() {
		return getSid_();
	}

	@Deprecated
	protected Long getSid_() throws E {
		return sid == null ? sid = loadModel_().getSid() : sid;
	}

	/**
	 * *************************************************************
	 * ---------------------------- NET_UID -----------------------
	 * *************************************************************
	 */
	@SneakyThrows
	public String getNtUid() {
		return getNt();
	}

	@SneakyThrows
	public String getNt() {
		return getNt_();
	}

	@Deprecated
	protected String getNt_() throws E {
		return net == null ? net = loadModel_().getNt() : net;
	}

	/**
	 * *************************************************************
	 * ---------------------------- SRC_NID -----------------------
	 * *************************************************************
	 */
	@Override
	@Deprecated
	@SneakyThrows
	public String getNid() {
		return getNid_();
	}

	@Deprecated
	protected String getNid_() throws E {
		return nid == null ? nid = loadModel_().getNid() : nid;
	}

	/**
	 * *************************************************************
	 * ---------------------------- NAME -----------------------
	 * *************************************************************
	 */
	@Override
	@Deprecated
	@SneakyThrows
	public String getName() {
		return getName_();
	}

	public String getName(boolean loadIfNull, int... maxLength) {
		if (name == null && !loadIfNull) {
			return null;
		}
		return ARG.isDefNum(maxLength) ? STR.substrQk(getName(), ARG.toDefNum(maxLength)) : getName();
	}

	@Deprecated
	protected String getName_() throws E {
		return name == null ? name = loadModel_().getNm() : name;
	}

	public boolean checkExist(boolean... RETURN) {
		return checkExist(this, RETURN);
	}

	public static boolean checkExist(NetSrcDbId usrId, boolean... RETURN) {
		try {
			if (usrId.sid == null) {
				usrId.getSid();
			} else if (usrId.nid == null) {
				usrId.getNid();
			}
			return true;
		} catch (Exception any) {
			if (L.isErrorEnabled()) {
				L.error("checkExist error:", any);
			}
			if (ARG.isDefEqTrue(RETURN)) {
				return false;
			}
			throw any;
		}
	}

	public boolean checkModelExist(boolean... defRq) {
		try {
			return IT.state(loadModel() != null);
		} catch (Exception ex) {
			return ARGn.toDefOrThrow(ex, defRq);
		}
	}

	@Getter
	@Setter
	protected M loadedModelCached;

	@SneakyThrows
	public M loadModel(boolean... fresh) {
		return loadModel_(fresh);
	}

	public abstract M loadModel_(boolean... fresh) throws E;


	public String toStringNameUid() {
		return getName() + "(" + getSid() + ")";
	}

	public String toStringNameNidQk() {
		try {
			return toStringNameNid();
		} catch (Exception ex) {
			return null;
		}
	}

	public String toStringNameNid() {
		return getName() + "(" + getNid() + ")";
	}

	public String toStringNameUidFid() {
		return getName() + "(" + getSid() + ":" + getNetFidString() + ")";
	}

}
