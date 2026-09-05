package zk_os.trm;

import mpc.exception.WhatIsTypeException;
import mpc.types.tks.cmt.Cmd7;
import mpt.*;
import utl_web.UWeb;
import zk_os.AppZosConfig;

/**
 * @author dav 05.04.2022   23:48
 */
@TrmEntity(value = AppTrm.KEY)
public class AppTrm implements ITrm {

	public static final String KEY = "a";

	@Override
	public String key() {
		return KEY;
	}

	public enum OnOff {
		UNDEFINED, on, off, status;
	}

	@TrmCmdEntity(value = "cfg")
	public static ITrmCmd TC_CFG = (usr, cmd) -> {
		Cmd7<String, String, String, String, String, String, String> c7 = cmd.cmd7();
		if (c7.extObj.isEmpty()) {
			return TrmRsp.ERR("Set fieldname and value if AppBeaConfig");
		}
		String property = c7.extObj.as(String.class, null);
		switch (property) {
			case AppZosConfig.APK_BEA_TRM_ENABLE:
				Boolean val = c7.opt1Obj.as(Boolean.class, null);
				if (val == null) {
					return TrmRsp.ERR("Fieldname '%s' except boolean (but value equals '%s')", property, c7.opt1Obj.get());
				}
				AppZosConfig.initProperty(property, c7.opt1Obj.get());
				return TrmRsp.OK("Property '%s' updated as '%s'", property, c7.opt1Obj.get());
			default:
				throw new WhatIsTypeException(property);
		}

	};
//	@TrmCmdEntity(value = "sec")
//	public static ITrmCmd TC_SEC = (usr, cmd) -> {
	/// /		TRM.checkUserNotNull(usr);
//		Cmd7<String, String, String, String, String, String, String> c7 = cmd.cmd7();
//		if (c7.extObj.isEmpty()) {
//			return TrmRsp.ERR("Set on/off");
//		}
//		OnOff as = (OnOff) c7.extObj.getAs(OnOff.class, OnOff.UNDEFINED);
//		switch (as) {
//			case status:
//				return TrmRsp.OK("Sec:" + Sec.secOn + ":" + Sec.getSecMode());
//			case on:
//			case off:
//				Sec.secOn = as == OnOff.on;
//				return TrmRsp.OK("Sec:" + as);
//			default:
//				return TrmRsp.ERR("Wrong value '%s' (Set on/off)", as);
//		}
//	};


	@TrmCmdEntity(value = "rq")
	public static ITrmCmd TC_RQ = (usr, cmd) -> TrmRsp.OK(UWeb.buildReportRequest(true, true, 0));

//	@TrmCmdEntity(value = "go")
//	public static ITrmCmd TC_GO = (usr, cmd) -> {
//		String sd3id = USToken.startWith(cmd.cmd(), "go", " ").trim();
//		Sd3ID sd3ID = Sd3ID.of(sd3id);
//		return GoToSd.goTo(sd3ID);
//	};
//	@TrmCmdEntity(value = "info") //aa rpa ../.data/web_bea_local
//	public static ITrmCmd TC_INFO = (usr, cmd) -> {
//		String ext = cmd.cmd7().extObj.str(null);
//		if (X.empty(ext)) {
//			return TrmRsp.OK(AppZos.buildReport(true, true, true));
//		}
//		switch (ext.toLowerCase()) {
//			case "env":
//				return TrmRsp.OK(AppZos.buildReport(true, false, false));
//			case "sd":
//				return TrmRsp.OK(AppZos.buildReport(false, true, false));
//			case "trm":
//				return TrmRsp.OK(AppZos.buildReport(false, false, true));
//			default:
//				throw new WhatIsTypeException(ext);
//		}
//	};

	//aa rpa ../.data/web_bea_local
	//aa rpa ../.data/0limeusr/.data/web_bea/
//	public static Pare3<Path, Path, Path> RPA2 = null;
//	@TrmCmdEntity(value = "rpa")
//	public static ITrmCmd TC_RPA = (usr, cmd) -> {
//		String newRpa = cmd.cmd7().extObj.str(null);
//		if (X.empty(newRpa)) {
//			return TrmRsp.OK("RPA '%s'", Env.RPA);
//		}
//		if ("--r".equals(newRpa)) {
//			if (RPA2.ext() == RPA2.val()) {
//				Env.RPA = (RPA2 = Pare3.of(RPA2.key(), RPA2.val(), RPA2.key())).ext();
//			} else {
//				Env.RPA = (RPA2 = Pare3.of(RPA2.key(), RPA2.val(), RPA2.val())).ext();
//			}
//			AppZosCore_Old.reinitEnv(true);
//			return TrmRsp.OK("Revert RPA '%s'", Env.RPA);
//		}
//		Path newRpaPath = Paths.get(newRpa);
//		if (newRpaPath.toAbsolutePath().equals(Env.RPA.toAbsolutePath())) {
//			return TrmRsp.ERR("RPA is same");
//		}
//		if (!UFS.existDir(newRpaPath)) {
//			return TrmRsp.ERR("Dir '%s' not exist", newRpa);
//		}
//		if (RPA2 == null) {
//			RPA2 = Pare3.of(Env.RPA, newRpaPath, newRpaPath);
//		}
//		Env.RPA = RPA2.ext();
//		AppZosCore_Old.reinitEnv(true);
//		return TrmRsp.OK("Set new RPA '%s'", Env.RPA);
//	};

}
