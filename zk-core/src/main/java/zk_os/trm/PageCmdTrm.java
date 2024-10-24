package zk_os.trm;

import mpu.X;
import mpu.str.JOIN;
import mpu.pare.Pare;
import mpc.types.tks.cmt.Cmd7;
import mpt.*;
import zk_com.base_ctr.Div0;
import zk_com.editable.EditableValueFile;
import zk_old_core.sd.core.SdMan;
import zk_page.ZKM;
import zk_old_core.mdl.PageDirModel;
import zk_old_core.sd.core.RepoPageDir;
import zk_old_core.sd.core.Sd3ID;
import zk_old_core.mdl.pageset.PageSet;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@TrmEntity(value = PageCmdTrm.KEY)
public class PageCmdTrm implements ITrm {
	public static final String KEY = "p";

	@Override
	public String key() {
		return KEY;
	}

//	@TrmCmdEntity(value = "add")
//	public static ITrmCmd TC_ADD = (usr, cmd) -> {
//		String ext = cmd.cmd7().extObj.str(null);
//		if (X.empty(ext)) {
//			return TrmRsp.ERR("Set page name");
//		}
//		Path newPage = ZkCore.getMasterRepo().resolve(ext);
//		if (!UFS.existDir(toPage)) {
//			return TrmRsp.ERR("Set existed page dir");
//		}
//		PageDirModel.ADD.addHtmlComponent(toPage, FrmEE.createBlankForm());
//		return TrmRsp.ERR("Add new blank form to page '%s'", toPage);
//	};

	@TrmCmdEntity(value = "reindex")
	public static ITrmCmd TC_FRESH_CTYPE = (usr, cmd) -> {
		Path pageDir;
		Cmd7<String, String, String, String, String, String, String> c7 = cmd.cmd7();
		if (c7.opt1Obj.isEmpty()) {
			pageDir = SpVM.get().findPageDirModel().path();
		} else {
			String pageID = c7.opt1();
			Sd3ID sd3ID = Sd3ID.of(pageID);
			Pare<RepoPageDir, Path> pare = SdMan.findPage(sd3ID);
			pageDir = pare.getVal();
		}
		List<Path> freshedPaths = PageDirModel.reindex(pageDir);
		return TrmRsp.MSG("fresh successfully:\n" + JOIN.allByNL(freshedPaths));
	};

	@TrmCmdEntity(value = "all")
	public static ITrmCmd TC_ALL_PAGES = (usr, cmd) -> {
		String subdomain3 = SpVM.get().subdomain3();
		return TrmRsp.OKR(SdMan.getAllPagesOfSd3(subdomain3));
	};

	@TrmCmdEntity(value = "ls")
	public static ITrmCmd TC_INDEX_PAGES = (usr, cmd) -> TrmRsp.OKR(PageDirModel.buildReport(SpVM.get().findPageDirModel(), 0));

//	@TrmCmdEntity(val = "mv") //p mv @df
//	public static ITrmCmd TC_MV = (usr, cmd) -> {
//		return SdApi.mv(usr, cmd);
//	};

	@TrmCmdEntity(value = "e")
	public static ITrmCmd TC_EDIT = (usr, cmd) -> {
		PageDirModel pageDirModel = PageDirModel.get();
		Div0 div;
		if (false) {
			PageSet pageSet = pageDirModel.getPageSet();
			List<Path> allPaths = pageSet.getIForms().stream().map(f -> f.fd().path()).collect(Collectors.toList());
			div = EditableValueFile.buildComs_EditView(allPaths);
		} else {
			div = EditableValueFile.buildComs_EditView_ForDir(pageDirModel.path());
		}
		ZKM.showModal(X.f("Edit page '%s'", pageDirModel.name()), div);
		return TrmRsp.OK();
	};

}
