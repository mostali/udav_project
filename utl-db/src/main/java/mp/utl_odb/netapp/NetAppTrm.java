package mp.utl_odb.netapp;

import mpu.core.ARG;
import mpu.IT;
import mpc.exception.FIllegalStateException;
import mpc.exception.NI;
import mpu.str.UST;
import mpu.str.TKN;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpt.ITrm;
import mpt.TrmRq;
import mpt.TrmRsp;

public class NetAppTrm implements ITrm<NetUsrId> {

	public static final String KEY = "an";

	@Override
	public String key() {
		return KEY;
	}

	public static String CMD(String body) {
		return KEY + " " + body;
	}

	@Override
	public TrmRsp exe_(NetUsrId usr, TrmRq cmd) throws Throwable {
		String org = cmd.cmd();
		String key = TKN.first(org, ' ', org);
		IT.state(KEY.equals(key));
		String next = TKN.startWith(org, key, org).trim();

		AmpPfx ampPfx = AmpPfx.of(next, null);
		if (ampPfx != null) {
			Long userUid = ampPfx.LONG();
			if (userUid == null) {
				return TrmRsp.ERR("Illegal link @usr from cmd: " + org);
			}
			NetUsrId usrId = INetApp.getApp(usr.getNt()).getUsrBySID(userUid, null);
			if (usrId == null) {
				return TrmRsp.ERR("User not found: " + userUid);
			}
			return TrmRsp.OBJ(usrId);
		}

		throw new NI("NI:" + org);
	}

	public static class AmpPfx extends PfxArg {

		public static final String PFX = "&";

		public AmpPfx(String body) {
			super(PFX, body);
		}

		public static AmpPfx of(String cmd, AmpPfx... defRq) {
			return check(cmd, PFX, ARG.isDef(defRq)) ? new AmpPfx(cmd) : ARG.toDef(defRq);
		}

	}

	public static class PfxArg {
		public final String pfx;
		public final String body;

		public <T> T getBodyAs(Class<T> asType, T... defRq) {
			return UST.strTo(body, asType, defRq);
		}

		public Long LONG(Long... defRq) {
			return getBodyAs(Long.class);
		}

		public Integer INT(Integer... defRq) {
			return getBodyAs(Integer.class);
		}

		public PfxArg(String pfx, String body) {
			check(body, pfx);
			this.pfx = pfx;
			this.body = body.substring(pfx.length());
		}

		public static boolean check(String str, String pfx, boolean... RETURN) {
			boolean is = str.startsWith(pfx);
			if (!is && ARG.isDefNotEqTrue(RETURN)) {
				throw new FIllegalStateException("Illegal pattern '%s' with prefix '%s'", str, pfx);
			}
			return is;
		}
	}
}
