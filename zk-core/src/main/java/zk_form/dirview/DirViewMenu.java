package zk_form.dirview;

import mpc.env.Env;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import mpe.rt_exec.GrepExecRq;
import mpe.rt_exec.UnZipExecEE;
import mpu.IT;
import mpu.Sys;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.core.RW;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IReRender;
import zk_form.SLib;
import zk_form.events.DefAction;
import zk_form.events.Grep_CfrmSerializableEventListener;
import zk_form.notify.ZKI;
import zk_notes.control.NotesSpace;
import zk_page.ZKCFinderExt;
import zk_page.ZKR;
import zk_page.panels.BottomHistoryPanel;
import zklogapp.ALI;
import zklogapp.AppLogProps;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class DirViewMenu {
	public static Function<List<String>, Path> grepSuccesCallbackWriteResult = grepLines -> {
		String first = "/tmp/" + UUID.randomUUID() + GrepExecRq.EXT_GREP;
		RW.writeLines(Paths.get(first), grepLines);
		BottomHistoryPanel.addItem(first.toString(), true);
		return Paths.get(first);
	};

	public static void applyMenuDir(Menupopup0 menupopup, Path path) {

		String placeholder = "set 'filename' or 'dirname/'";
		menupopup.addMI_Cfm1(SYMJ.PLUS + " Add File|Dir", placeholder, val -> {

			Path newFd = path.resolve(IT.NE(val, placeholder));

			boolean isDir = val.endsWith(UF.ROOT_DIR_UNIX);

			if (UFS.exist(newFd)) {
//				ZKI.alert(val + " already exist's");
//				return false;
				IT.isDirOrFileNotExist(newFd, "Already exist");
			}

			if (isDir) {
				UFS_BASE.MKDIR.createDirs(newFd);
			} else {
				UFS_BASE.MKFILE.createFile(newFd);
			}

			List<DirView0> dirs = DirView0.findAll(path);

			dirs.forEach(d -> d.rerender());

			return true;
		});


//		menupopup.addMenuitem(ALI.DIR_UP + "UP", e -> DirView.findFirst().upDown(true).rerender());
//		menupopup.addMenuitem(ALI.DIR_UP + "DOWN", e -> Sys.openNautilus(path.toString()));
//		menupopup.addContextMenuSeparator();
		if (Env.isLocalDevMashine()) {
			menupopup.addMI(ALI.OS_OPEN + "Open Dir OS", getEventOpenSimpleMenu_OS(path));
		}


		{
			Path parent = path.getParent();
			if (parent != null) {//wth??
				menupopup.addMI(SYMJ.SEARCH_LUPA_RIGHT + " Search Files (Glob)", SLib.of(parent).toEventShowInModal());
			}
		}

		menupopup.add_______();
		menupopup.addMI_Href(Grep_CfrmSerializableEventListener.toMenuItemComponent(path, "", grepSuccesCallbackWriteResult));
		menupopup.add_______();
		menupopup.addMI_RenameFile_Cfrm(path.toString(), null, false);
		menupopup.addMI_RenameFile_Cfrm(path.toString(), null, true);

		DefAction successCalbackAction = e -> {
			ZKCFinderExt.rerenderFirst(DirView0.class, true);
//			Component firstInPage = IZCom.findFirstInPage(, true);
//			Component firstInPage = (Component) ARRi.first(ZKCFinderExt.findByParent(e.getTarget(), DirView0.class, ARR.EMPTY_LIST), null);
//			if (firstInPage != null && firstInPage instanceof IReRender) {
//				((IReRender<?>) firstInPage).rerender();
//			} else {
//				DirView.findFirst().rerender();
//			}
			ZKI.showMsgBottomRightFast_INFO("Removed");
		};
		menupopup.addMI_DeleteFile(path.toString(), successCalbackAction);


	}

	public static void applyMenuFile(Menupopup0 menupopup, Path path) {
		boolean isArc = GEXT.ARC.isPath(path);
		String pathStr = path.toString();
		menupopup.addMI(ALI.DOWNLOAD + UF.fnWithSize(path), e -> ZKR.download(path));


		menupopup.add_______();
		menupopup.addMI(ALI.OS_OPEN + " Open in Code", e -> Sys.open_Code(path));
		menupopup.addMI(ALI.OS_OPEN + " Open Dir", getEventOpenSimpleMenu_OS(path));

		menupopup.add_______();

		menupopup.addMI_Href(Grep_CfrmSerializableEventListener.toMenuItemComponent(path, "", grepSuccesCallbackWriteResult));

		menupopup.add_______();

		if (isArc) {
			String encoding = AppLogProps.APR_UNZIP_ENCODING.getValueOrDefault("");
			menupopup.addMI(ALI.UNZIP + " Unzip file (overwrite)", (e) -> {
				String msg = UnZipExecEE.unzip(pathStr, true, encoding);
				ZKI.log(msg);
				ZKI.showMsgBottomRightFast_INFO(msg);
				DirView.findFirst().rerender();
			});
			menupopup.addMI(ALI.UNZIP + " Unzip file (skip existed)", (e) -> {
				String msg = UnZipExecEE.unzip(pathStr, false, encoding);
				ZKI.log(msg);
				ZKI.showMsgBottomRightFast_INFO(msg);
				DirView.findFirst().rerender();
			});

			menupopup.add_______();

		}

		menupopup.addMI_RenameFile_Cfrm(pathStr, e -> ZKCFinderExt.rerenderFirst(DirView0.class));

		menupopup.addMI_DeleteFile(pathStr, e -> ZKCFinderExt.rerenderFirst(DirView0.class));

	}

	public static SerializableEventListener getEventOpenSimpleMenu_OS(Path path) {
		return e -> Sys.open_Nautilus(path);
	}

	public static SerializableEventListener getEventOpenSimpleMenu_Terminal(Path path) {
		return e -> Sys.open_Terminal(path);
	}
}
