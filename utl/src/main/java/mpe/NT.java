package mpe;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpc.net.CON;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.core.ENUM;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpu.str.UST;
import mpc.types.tks.FID;

@RequiredArgsConstructor
public enum NT {
	DEF(0),
	TG(1), VK(2),
	TSM(-1), BEA(-2), GSV(-3), ARS(-4), XN(-5),
	ANY1(-1001), ANY2(-1002), ANY3(-1003);


	public static final String VPFX = "v";
	public static final String TPFX = "t";
	public static final String BPFX = "b";
	public static final String XPFX = "x";
	public static final String ZPFX = "z";

	public final long uid;

	public String HOST_URL() {
		switch (this) {
			case TG:
				return CON.HTTPS + "t.me/";
			case VK:
				return CON.HTTPS + "vk.com/";
			default:
				throw new WhatIsTypeException(this);
		}
	}

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
		return ENUM.valueOf(nt, NT.class, true, defRq);
	}

	public static NT ofNET(String net, NT... defRq) {
		NT nt = ENUM.valueOf(net, NT.class, true, null);
		if (nt != null) {
			return nt;
		}
		nt = ofUid(net, null);
		if (nt != null) {
			return nt;
		}
		return ARG.toDefThrowMsg(() -> X.f("Undefined net [%s]", net), defRq);

	}

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
		IT.state(uid == UST.LONG(net_uid));
		return this;
	}

	public FID checkFid(String fid_with_nt, int... positionNtInFid) {
		FID fid = FID.of(fid_with_nt);
		int idx = ARGn.toDefOr(0, positionNtInFid);
		IT.state(uid == fid.LONG(idx));
		return fid;
	}

	public FID checkFid(FID fid, int... positionNtInFid) {
		int idx = ARGn.toDefOr(0, positionNtInFid);
		IT.state(uid == fid.LONG(idx));
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

	public String fid(int nid) {
		return name() + "_" + nid;
	}

	public FID FID(long userNid) {
		return FID.of(name(), userNid);
	}

	public String nameLC() {
		return name().toLowerCase();
	}

	public String nameUC() {
		return name();
	}

	public String shortPfx(String... defRq) {
		switch (this) {
			case VK:
				return VPFX;
			case TG:
				return TPFX;
			default:
				return name().charAt(0) + "";
//			case BEA:
//				return BPFX;
//			case XN:
//				return XPFX;
//			default:
//				return ARG.toDefThrow(() -> new WhatIsTypeException(this), defRq);
		}
	}


	public String toNetShortPfxLogin(long nid) {
		return shortPfx() + nid;
	}
}
