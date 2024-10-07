package zklogapp;

import mpe.logs.filter.merger.LogStatAnalyze_ThreadHistory;
import mpu.Sys;
import mpu.X;
import mpc.fs.*;
import mpc.fs.ext.GEXT;
import mpu.str.Rt;
import mpu.pare.Pare;
import mpe.logs.filter.merger.LogStatAnalyze_GroupBy;
import mpe.rt_exec.GrepExecRq;
import mpe.rt_exec.UnZipExecEE;
import mpu.core.RW;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import zk_com.base.Bt;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Menupopup0;
import zk_form.events.*;
import zk_old_core.old.fswin.core.DirView;
import zk_os.AppZos;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Log;
import zk_form.notify.ZKI_Modal;
import zk_page.ZKME;
import zk_page.ZKR;
import zklogapp.header.BottomHistoryPanel;
import zklogapp.logview.LogFileView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

//AppLogMenu
public class ALM {

	static Function<List<String>, Path> grepSuccesCallbackWriteResult = grepLines -> {
		String first = "/tmp/" + UUID.randomUUID() + GrepExecRq.EXT_GREP;
		RW.writeLines(Paths.get(first), grepLines);
		BottomHistoryPanel.addItem(first.toString());
		return Paths.get(first);
	};

	public static void applyLogDir(Menupopup0 menupopup, Path path) {

//		menupopup.addMenuitem(ALI.DIR_UP + "UP", e -> DirView.findFirst().upDown(true).rerender());
//		menupopup.addMenuitem(ALI.DIR_UP + "DOWN", e -> Sys.openNautilus(path.toString()));
//		menupopup.addContextMenuSeparator();
		menupopup.addMenuitem(ALI.OS_OPEN + "Open Dir", e -> Sys.open_Nautilus(path.toString()));
		menupopup.addSeparator();
		menupopup.addMenuitem(Grep_CfrmSerializableEventListener.toMenuItemComponent(path, "", grepSuccesCallbackWriteResult));
		menupopup.addSeparator();
		menupopup.addMenuitem_RenameFile_Cfrm(path.toString(), null, false);
		menupopup.addMenuitem_RenameFile_Cfrm(path.toString(), null, true);

		DefAction successCalbackAction = e -> {
			DirView.findFirst().rerender();
			ZKI.infoBottomRightFast("Removed");
		};
		menupopup.addMenuitem_RemoveFile(path.toString(), successCalbackAction);
		menupopup.addMenuitem(RemoveFileWithConfirmation_SerializableEventListener.toMenuItemComponent(path.toString(), successCalbackAction));

		menupopup.addSeparator();

		fillFileUtilsMenu(menupopup, path);
	}

	private static void fillFileUtilsMenu(Menupopup0 menupopup, Path path) {
		Menupopup0 fileUtilsMenu = menupopup.addInnerMenu("File Utils..");
		fileUtilsMenu.addMenuitem(ALI.SEARCH_FILE + "Find file twins..", e -> {
			Map<Path, List<Path>> twins = UFS.findTwinsAsMap(path, true);
			if (X.empty(twins)) {
				ZKI.infoSingleLine("Twins not found");
			} else {
				AtomicReference<Window> ref = new AtomicReference();
				SerializableEventListener btRmEvent = (btE) -> ZKI_Modal.showMessageBoxBlueYN("Removing twins..", "Remove twins files?", (Boolean yn) -> {
					if (yn) {
						UFS.removeTwins(twins);
						ref.get().onClose();
						DirView.findFirst().rerender();
						ZKI.infoBottomRightFast("Removed");
						return null;
					}
					return null;
				});
				Pare<Window, Tbxm> removeTwins = ZKI.infoEditorBw(Bt.of("Remove empty files", btRmEvent), Rt.buildReport(twins));
				ref.set(removeTwins.key());
			}
		});

		fileUtilsMenu.addMenuitem(ALI.SEARCH_FILE + "Find empty files..", e -> {
			Collection<File> emptyFiles = UDIR.lsAll(path.toFile(), f -> !UFS.isFileWithContent(f.toPath()));
			if (X.empty(emptyFiles)) {
				ZKI.infoSingleLine("Empty files not found");
			} else {
				AtomicReference<Window> ref = new AtomicReference();
				SerializableEventListener btRmEvent = (btE) -> ZKI_Modal.showMessageBoxBlueYN("Removing empty files..", "Remove this files?", (Boolean yn) -> {
					if (yn) {
						emptyFiles.forEach(UFS.RM::removeFileQk);
						ref.get().onClose();
						DirView.findFirst().rerender();
						ZKI.infoBottomRightFast("Removed");
						return null;
					}
					return null;
				});
				Pare<Window, Tbxm> removeTwins = ZKI.infoEditorBw(Bt.of("Removing empty files", btRmEvent), Rt.buildReport(emptyFiles));
				ref.set(removeTwins.key());
			}
		});

//		Mi fillFromJiraComment = Tbx_CfrmSerializableEventListener.toMenuItemComponent("Fill from jira..", ANI.COPY + "Fill from url", "", "Set kira url", (String jiraUrl) -> {
////			JiraC
//			Path newNotesPath = AppNotes.getPathOfNote(jiraUrl);
//			if (UFS.exist(newNotesPath)) {
//				ZKI.alert("Notes '%s' already exist", jiraUrl);
//			} else {
//				UFS_BASE.COPY.copyDirectory(pathDir, newNotesPath.getParent());
//				NotesPageSP.NotesSpace.rerenderFirst();
//			}
//			return null;
//		});
//		fileUtilsMenu.addMenuitem(fillFromJiraComment);

	}

	public static void applyLogFile(Menupopup0 menupopup, Path path) {
		boolean isArc = GEXT.ARC.hasIn(path);
		String pathStr = path.toString();
		menupopup.addMenuitem(ALI.DOWNLOAD + UF.fnWithSize(path), e -> ZKR.download(path));
		menupopup.addSeparator();
		menupopup.addMenuitem(ALI.LOGVIEW + "Log View", e -> LogFileView.openSingly(pathStr));
		menupopup.addSeparator();
		menupopup.addMenuitem(ALI.STATS + "Log Analyze GroupByThread", e -> ZKME.openEditorWithContent("Group By Thread", Rt.buildReport(LogStatAnalyze_GroupBy.collectStatsByThread(pathStr, AppZos.getLogGetterDate()), "LogStats:" + pathStr).toString(), true).toString());
		menupopup.addMenuitem(ALI.STATS + "Log Analyze GroupByClass", e -> ZKME.openEditorWithContent("Group By Class", Rt.buildReport(LogStatAnalyze_GroupBy.collectStatsByGroup(pathStr, AppZos.getLogGetterDate()), "LogStats:" + pathStr).toString(), true).toString());
		menupopup.addSeparator();
		menupopup.addMenuitem(ALI.STATS + "Log Analyze Thread History", e -> ZKME.openEditorWithContent("Group By Thread History", LogStatAnalyze_ThreadHistory.collectHistoryByThread_REPORT(pathStr, AppZos.getLogGetterDate(), AppZos.getLogLineMapping()).toString(), true).toString());

		menupopup.addSeparator();
		menupopup.addMenuitem(ALI.OS_OPEN + "Open in Code", e -> Sys.open_Code(path));
		menupopup.addMenuitem(ALI.OS_OPEN + "Open Dir", e -> Sys.open_Nautilus(UF.fPr(path)));

		menupopup.addSeparator();

		menupopup.addMenuitem(Grep_CfrmSerializableEventListener.toMenuItemComponent(path, "", grepSuccesCallbackWriteResult));

		menupopup.addSeparator();

		if (isArc) {
			String encoding = AppLogProps.APR_UNZIP_ENCODING.getValueOrDefault("");
			menupopup.addMenuitem(ALI.UNZIP + "Unzip file (overwrite)", (e) -> {
				String msg = UnZipExecEE.unzip(pathStr, true, encoding);
				ZKI_Log.log(msg);
				ZKI.infoBottomRightFast(msg);
				DirView.findFirst().rerender();
			});
			menupopup.addMenuitem(ALI.UNZIP + "Unzip file (skip existed)", (e) -> {
				String msg = UnZipExecEE.unzip(pathStr, false, encoding);
				ZKI_Log.log(msg);
				ZKI.infoBottomRightFast(msg);
				DirView.findFirst().rerender();
			});

			menupopup.addSeparator();

		}

		menupopup.addMenuitem_RenameFile_Cfrm(pathStr, null);
		menupopup.addMenuitem_RemoveFile(pathStr, null);

	}
}
