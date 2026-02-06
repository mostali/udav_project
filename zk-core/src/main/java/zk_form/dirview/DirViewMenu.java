package zk_form.dirview;

import mpc.env.Env;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.net.DLD;
import mpc.str.sym.SYMJ;
import mpe.rt_exec.GrepExecRq;
import mpe.rt_exec.UnZipExecEE;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.RW;
import mpu.str.TKN;
import mpu.str.UST;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base_ctr.Menupopup0;
import zk_form.SLib;
import zk_form.events.DefAction;
import zk_form.events.Grep_CfrmSerializableEventListener;
import zk_form.notify.ZKI;
import zk_notes.ANI;
import zk_page.ZKCFinderExt;
import zk_page.ZKR;
import zk_page.panels.BottomHistoryPanel;
import zklogapp.ALM;
import zklogapp.AppLogProps;
import zklogapp.header.LogMergerPageHeader;
import zklogapp.logview.LogFileView;
import zklogapp.merge.LogDirView;

import java.io.IOException;
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
		menupopup.addMI_Cfm1(SYMJ.PLUS + " Add File or Folder", placeholder, val -> {

			Path newFd = path.resolve(IT.NE(val, placeholder));

			boolean isDir = val.endsWith(UF.ROOT_DIR_UNIX);

			if (UFS.exist(newFd)) {
				IT.isDirOrFileNotExist(newFd, "Already exist");
			}

			if (isDir) {
				UFS.MKDIR.createDirs(newFd);
			} else {
				String[] two = TKN.two(val, "@@log@@", null);
				if (two == null) {
					UFS.MKFILE.createFile(newFd);
				} else {

					try {
						String filename = IT.isFilename(two[0]);
						String targetFile = path.resolve(filename).toString();
						IT.isDirOrFileNotExist(Paths.get(targetFile));
						DLD.url2file0(IT.isUrl(two[1]), targetFile);
						ZKI.infoAfterPointer(X.f_("Download log '%s'", filename));
//						LogMergerPageHeader.firerender();

						LogDirView.removeMeFirst();
						LogDirView.openSingly(path.toString());

						LogFileView.openSingly(targetFile);

					} catch (IOException e) {
						throw new RuntimeException(e);
					}

				}
			}

			List<DirView0> dirs = DirView0.findAll(path);

			dirs.forEach(d -> d.rerender());

			return true;
		});

		menupopup.add_______();
		menupopup.addMI_UploadTo(SYMJ.UPLOAD + " Upload file", path);
		menupopup.addMI_UploadDdTo(SYMJ.UPLOAD + " Upload file Drag&Drop", path);

		{
			Path parent = path.getParent();
			if (parent != null && !UFS.isDirRoot(parent)) {//it mb root
				menupopup.addMI(SYMJ.SEARCH_LUPA_RIGHT + " Search Files (Glob)", SLib.of(parent).toEventShowInModal());
			}
		}

		menupopup.add_______();
		menupopup.addMI_Href_in_Self(Grep_CfrmSerializableEventListener.toMenuItemComponent(path, "", grepSuccesCallbackWriteResult));
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

		menupopup.add_______();

		if (Env.isLocalDevMashine()) {
			menupopup.addMI(ANI.OS_OPEN + "Open Dir OS", getEventOpenSimpleMenu_OS(path));
		}

		menupopup.add_______();
		menupopup.addMI_DownloadAsZip(ANI.DOWNLOAD + " Download as Zip", path);
	}

	public static void applyMenuFile(Menupopup0 menupopup, Path path) {
		boolean isArc = GEXT.ARC.isPath(path);
		String pathStr = path.toString();
		menupopup.addMI(ANI.DOWNLOAD + " Download " + UF.fnWithSize(path), e -> ZKR.download(path));

		menupopup.add_______();

		if (EXT.of(path) == EXT.LOG) {
			ALM.applyLogUtils(menupopup, path);
		}

		if (Env.isLocalDevMashine()) {
			menupopup.addMI(ANI.OS_OPEN + " Open in Code", e -> Sys.open_Code(path));
			menupopup.addMI(ANI.OS_OPEN + " Open Dir", getEventOpenSimpleMenu_OS(path));
		}

		menupopup.add_______();

		menupopup.addMI_Href_in_Self(Grep_CfrmSerializableEventListener.toMenuItemComponent(path, "", grepSuccesCallbackWriteResult));

		menupopup.add_______();

		if (isArc) {
			String encoding = AppLogProps.APR_UNZIP_ENCODING.getValueOrDefault("");
			menupopup.addMI(ANI.UNZIP + " Unzip file (overwrite)", (e) -> {
				String msg = UnZipExecEE.unzip(pathStr, true, encoding);
				ZKI.log(msg);
				ZKI.showMsgBottomRightFast_INFO(msg);
				DirView.findFirst().rerender();
			});
			menupopup.addMI(ANI.UNZIP + " Unzip file (skip existed)", (e) -> {
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
