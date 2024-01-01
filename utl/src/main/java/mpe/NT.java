package mpe;

import lombok.RequiredArgsConstructor;
import mpc.args.ARG;
import mpc.args.ARGi;
import mpc.core.EN;
import mpc.ERR;
import mpc.exception.RequiredRuntimeException;
import mpc.str.UST;
import mpc.types.tks.FID;

@RequiredArgsConstructor
public enum NT {
	DEF(0),
	TG(1), VK(2),
	TSM(-1), ARS(-2), BEA(-3), ANY1(-1001), ANY2(-1002), ANY3(-1003);

	public final long uid;

	public static NT ofUid(long net_uid, NT... defRq) {
		for (NT nt : values()) {
			if (net_uid == nt.uid) {
				return nt;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Unknown NetType:" + net_uid);
	}

	public static NT of(String nt, NT... defRq) {
		return EN.valueOf(nt, NT.class, defRq);
	}

	@Deprecated
	public static NT ofUid(String net_uid, NT... defRq) {
		Long nid = UST.LONG(net_uid, null);
		NT nt = null;
		if (nid != null) {
			nt = ofUid(nid, null);
		}
		if (nt != null) {
			return nt;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Unknown NetType (String):" + net_uid);
	}

	public static NT valueOfIC(String appName) {
		return valueOf(appName.toUpperCase());
	}

	public NT ofRq(String net_uid) {
		ERR.state(uid == UST.LONG(net_uid));
		return this;
	}

	public FID checkFid(String fid_with_nt, int... positionNtInFid) {
		FID fid = FID.of(fid_with_nt);
		int idx = ARGi.toDefOr(0, positionNtInFid);
		ERR.state(uid == fid.LONG(idx));
		return fid;
	}

	public FID checkFid(FID fid, int... positionNtInFid) {
		int idx = ARGi.toDefOr(0, positionNtInFid);
		ERR.state(uid == fid.LONG(idx));
		return fid;
	}

	public long getNetUid() {
		return uid;
	}

	public String uidStr() {
		return String.valueOf(uid);
	}

	public long uid() {
		return uid;
	}

	public String fid(int userNid) {
		return name() + "_" + userNid;
	}

	public FID FID(long userNid) {
		return FID.of(name(), userNid);
	}

	public String name0() {
		return name().toLowerCase();
	}
}
