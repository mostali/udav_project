package zk_os.trm;

import mpu.IT;
import mpu.core.ENUM;
import mpc.exception.WhatIsTypeException;
import mpe.core.P;
import mpu.pare.Pare;
import mpc.types.tks.cmt.Cmd7;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpu.X;
import org.reflections.Reflections;
import mpt.*;
import zk_old_core.sd.Sd3EE;
import zk_old_core.sd.SdApi;
import zk_old_core.sd.core.RepoPageDir;
import zk_old_core.sd.core.Sd3ID;
import zk_old_core.sd.core.SdMan;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

@TrmEntity(value = SdTrm.KEY)
public class SdTrm implements ITrm<IaUser> {
	public static final String KEY = "sd";

	public static void main(String[] args) {
//		TRM.run("zkbea");
		Reflections reflections = new Reflections("zkbea");
		Set<Class<?>> annotated = reflections.get(SubTypes.of(TypesAnnotated.with(TrmEntity.class)).asClass());
		P.exit(annotated);
	}

	@Override
	public String key() {
		return KEY;
	}

	@TrmCmdEntity(value = "index")
	public static ITrmCmd TC_INDEX = (usr, cmd) -> {
		return TrmRsp.OKR(SdMan.buildReport(0));
	};

	@TrmCmdEntity(value = "home")
	public static ITrmCmd TC_HOME = (usr, cmd) -> TrmRsp.OKR(SdMan.findPageIndex());

//	@TrmCmdEntity(value = "reindex")
//	public static ITrmCmd TC_REINDEX = (usr, cmd) -> {
//		boolean clean = cmd.seqOpts().hasDouble("clean", false);
//		boolean scan = cmd.seqOpts().hasDouble("scan", true);
//		throw NI.stop("nimpl");
//		//return TrmRsp.OKR(SdIndex.reindex(clean, scan));
//	};

	@TrmCmdEntity(value = "ls")
	public static ITrmCmd TC_LS = (usr, cmd) -> {
		List<String> allNames = SdMan.getAllSubdomainNames();
		Object r = X.empty(allNames) ? 0 : allNames;
		return TrmRsp.OKR(r);
	};


	@TrmCmdEntity(value = "state")
	public static ITrmCmd TC_STATE = (usr, cmd) -> {
		Cmd7<String, String, String, String, String, String, String> cmd7 = cmd.cmd7();
		String ext = cmd7.ext();
		String opt1 = cmd7.opt1();
		Sd3ID sd3pageID = Sd3ID.of(ext);
		if (!sd3pageID.isStrictSyntax()) {
			return TrmRsp.ERR("error sd3-id [%s]. Set valid sd3id, e.g. sd3@pagename", ext);
		}
		Sd3EE.PageState state = ENUM.valueOf(opt1, Sd3EE.PageState.class, null);
		IT.NN(state, "unknown state", opt1);
		Pare<RepoPageDir, Path> repo = SdMan.findPage(sd3pageID);
		state.change(repo);
		return TrmRsp.OK("State changed:" + sd3pageID + ":" + state);
	};

	@TrmCmdEntity(value = "mv")
	public static ITrmCmd<NetUsrId> TC_MV = SdApi::mv;//(usr, cmd) -> {
//		return SdApi.mv(usr,cmd);//
//		Cmd7<String, String, String, String, String, String, String> cmd7 = cmd.cmd7();
//		String ext = cmd7.ext();
//		String opt1 = cmd7.opt1();
//		Sd3ID idSrc = Sd3ID.of(ext);
//		Sd3ID idDst = Sd3ID.of(opt1);
//		try {
//			RepoPageDir.movePage(idSrc, idDst);
//		} catch (Exception ex) {
//			String msg = U.f("Page [%s] NOT moved to [%s] ( %s )", idSrc, idDst, ex.getMessage());
//			if (L.isErrorEnabled()) {
//				L.error(msg, ex);
//			}
//			return TrmRsp.ERR(msg);
//		}
//		return TrmRsp.OK("Page [%s] moved to [%s]", idSrc, idDst);
//};//

	@TrmCmdEntity(value = "rm")
	public static ITrmCmd<NetUsrId> TC_RM = (usr, cmd) -> SdApi.rm(usr, cmd);

	@TrmCmdEntity(value = "add")
	public static ITrmCmd<NetUsrId> TC_ADD = (usr, cmd) -> SdApi.add(usr, cmd);

	@Override
	public TrmRsp exe_(IaUser usr, TrmRq cmd) throws Throwable {

		Cmd7<String, String, String, String, String, String, String> c7 = cmd.cmd7();

		checkKey(cmd);

		String val = c7.val();
		if (X.empty(val)) {
			return TrmRsp.OK(cmds(Collections.EMPTY_MAP).keySet());
		}

		ITrmCmd tcmd = trmcmd(val, null);
		if (tcmd != null) {
			return tcmd.exe_(usr, cmd);
		}

		throw new WhatIsTypeException("Not found cmd handler >> " + cmd.cmd());

	}
}
