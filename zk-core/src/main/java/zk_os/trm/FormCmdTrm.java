package zk_os.trm;

import mpu.X;
import mpc.fs.UFS;
import mpt.*;
import zk_com.base_ctr.Div0;
import zk_com.editable.RenameFileTextbox;
import zk_com.editable.EditableValueFile;
import zk_old_core.std_core.FrmEE;
import zk_page.ZKM;
import zk_old_core.mdl.FdModel;
import zk_old_core.mdl.FormDirModel;
import zk_old_core.mdl.PageDirModel;
import zk_old_core.app_ds.struct.FormDirDS;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@TrmEntity(value = FormCmdTrm.KEY)
public class FormCmdTrm implements ITrm {
	public static final String KEY = "f";

	@Override
	public String key() {
		return KEY;
	}

	@TrmCmdEntity(value = "add")
	public static ITrmCmd TC_ADD = (usr, cmd) -> {
		String ext = cmd.cmd7().extObj.str(null);
		Path toPage;
		if (X.empty(ext)) {
			PageDirModel pageDirModel = null;
			pageDirModel = PageDirModel.get(null);
			if (pageDirModel == null) {
				return TrmRsp.ERR("Set page dir");
			}
			toPage = pageDirModel.dir().path();
		} else {
			toPage = Paths.get(ext);
			if (!UFS.existDir(toPage)) {
				return TrmRsp.ERR("Set existed page dir");
			}
		}
		FormDirModel.ADD.addHtmlComponent(toPage, FrmEE.createBlankForm());
		return TrmRsp.OK("Add new blank form to page '%s'", toPage);
	};

	@TrmCmdEntity(value = "mv")
	public static ITrmCmd TC_MV = (usr, cmd) -> {
		FdModel fdModel = FdModel.getAny(cmd.cmd7().ext());
		if (fdModel instanceof FormDirModel) {
			String formname = cmd.cmd7().ext();
			Div0 div = RenameFileTextbox.buildComsForChildsDir(FormDirModel.class.cast(fdModel).path());
			ZKM.showModal("Rename", div);
			return TrmRsp.OK();
		} else {
			return TrmRsp.FAIL("unsupported ffm");
		}
	};

	@TrmCmdEntity(value = "rm")
	public static ITrmCmd TC_RM = (usr, cmd) -> {
		FdModel fdModel = FdModel.getAny(cmd.cmd7().ext());
		Path path = FormDirDS.rmm(fdModel.path());
		return TrmRsp.OK("Deleted as %s", path);

	};

	@TrmCmdEntity(value = "e")
	public static ITrmCmd TC_EDIT = (usr, cmd) -> {
		FdModel fdModel = FdModel.getAny(cmd.cmd7().ext());
		if (fdModel instanceof FormDirModel) {
			FormDirModel formDirModel = FormDirModel.class.cast(fdModel);
			Div0 div = EditableValueFile.buildComs_EditView_ForDir(formDirModel.path());
			ZKM.showModal(X.f("Edit form '%s'", formDirModel.name()), div);
			return TrmRsp.OK();
		} else {
			return TrmRsp.FAIL("unsupported ffm");
		}
	};

	@TrmCmdEntity(value = "ls")
	public static ITrmCmd TC_LC = (usr, cmd) -> {
		String ext = cmd.cmd7().ext();
		if (ext == null) {
			return TrmRsp.ERR("Set form name");
		}
		FdModel fdModel = FdModel.getAny(ext);
		if (fdModel instanceof FormDirModel) {
			FormDirModel formDirModel = FormDirModel.class.cast(fdModel);
			List<String> names = formDirModel.getAllPaths().stream().map(f -> f.getFileName().toString()).collect(Collectors.toList());
			return TrmRsp.OK(names);
		} else {
			return TrmRsp.FAIL("unsupported ffm");
		}
	};
}
