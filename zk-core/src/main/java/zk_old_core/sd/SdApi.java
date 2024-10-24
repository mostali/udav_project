package zk_old_core.sd;

import mpc.exception.EException;
import mpc.exception.NI;
import mpu.str.STR;
import mpc.types.tks.cmt.Cmd7;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpt.TrmRq;
import mpt.TrmRsp;
import utl_web.UWeb;
import zk_os.AppZosWeb;
import zk_old_core.sd.core.RepoPageDir;
import zk_old_core.sd.core.Sd3ID;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Path;

public class SdApi {

	public static final String FLAG_DBL_FORCE = "f";

	public static TrmRsp add(NetUsrId usr, TrmRq cmd) throws Sd3EE {
		String ext = cmd.cmd7().ext();
		Sd3ID sd3ID = Sd3ID.of(ext, true);
		Sd3ID.checkSDID_NEEDNOTCLEAN(sd3ID);
		if (sd3ID.isSingleSd3()) {
			Sd3EE.checkNotExistSd3(sd3ID);
			RepoPageDir repoPage = Sd3EE.createRepoDirInDefaultLocation(sd3ID.sd3());
			if (cmd.getUserAgent() == TrmRq.UA.WEB) {
				return GoToSd.goTo(sd3ID);
			}
			return TrmRsp.OK("Subdomain '%s' created successfully. Added as '%s'", ext, STR.pfile(repoPage.path()));
		}
		if (sd3ID.isSignlePage()) {
//			checkAvailablePageModel(cmd);
			String sd3;
			PageDirModel pageDirModel = PageDirModel.get(null);
			if (pageDirModel == null) {
				sd3 = AppZosWeb.getSd3();
				if ("".equals(sd3)) {
					return TrmRsp.FAIL("Set subdomain.");
				}
			} else {
				sd3 = pageDirModel.getRepo().getSubdomain3();
			}
			sd3ID = Sd3ID.of(sd3 + "" + ext, true);
		}
		try {
			Sd3EE.checkNotExistPage(sd3ID);
			Path page = Sd3EE.getPageOrCreate(sd3ID, cmd.getSeqOpts().hasDouble(FLAG_DBL_FORCE, false));
			Object domain = UWeb.getHost();
			if (cmd.getUserAgent() == TrmRq.UA.WEB) {
				return GoToSd.goTo(sd3ID);
			}
			return TrmRsp.OKm("Page http://{0}.{1}/{2} created successfully. Added as {3}", sd3ID.sd3(), domain, sd3ID.page(), STR.pfile(page));
		} catch (EException e) {
			return TrmRsp.ERR(e);
		}
	}

	public static TrmRsp mv(NetUsrId usr, TrmRq cmd) throws Sd3EE {
		return SdMvApi.mv(usr, cmd);
	}

	public static TrmRsp rm(NetUsrId usr, TrmRq cmd) throws Sd3EE {
		NI.stop("ni rm");
		Cmd7<String, String, String, String, String, String, String> c7 = cmd.cmd7();
		String ext = c7.ext();
		String opt1 = c7.opt1();
		Sd3ID sd3ID = Sd3ID.of(ext);
		Sd3ID.checkSDID_NEEDCLEAN(sd3ID);
		RepoPageDir repoPage = Sd3EE.createRepoDirInDefaultLocation(sd3ID.sd3());

//		RepoPageDir.movePage();
		return null;

	}
}
