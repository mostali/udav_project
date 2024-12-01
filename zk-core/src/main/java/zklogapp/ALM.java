package zklogapp;

import mpe.logs.filter.merger.LogStatAnalyze_ThreadHistory;
import mpu.X;
import mpc.fs.*;
import mpu.str.Rt;
import mpu.pare.Pare;
import mpe.logs.filter.merger.LogStatAnalyze_GroupBy;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import zk_com.base.Bt;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Menupopup0;
import zk_old_core.dirview.DirView;
import zk_old_core.dirview.SimpleDirView;
import zk_os.AppZos;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Modal;
import zk_page.ZKME;
import zklogapp.logview.LogFileView;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

//AppLogMenu
public class ALM {

	public static void applyLogDirWithUtils(Menupopup0 menupopup, Path path) {
		SimpleDirView.applyMenuDir(menupopup, path);
		menupopup.add_______();
		applyDirMenuUtils(menupopup, path);
	}

	private static void applyDirMenuUtils(Menupopup0 menupopup, Path path) {
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

	public static void applyLogFileWithUtils(Menupopup0 menupopup, Path path) {
		SimpleDirView.applyMenuFile(menupopup, path);
		menupopup.add_______();
		applyLogUtils(menupopup, path);
	}

	public static void applyLogUtils(Menupopup0 menupopup, Path path) {
		menupopup.add_______();
		String pathStr = path.toString();
		menupopup.addMenuitem(ALI.LOGVIEW + "Log View", e -> LogFileView.openSingly(pathStr));
		menupopup.add_______();
		menupopup.addMenuitem(ALI.STATS + "Log Analyze GroupByThread", e -> ZKME.openEditorWithContent("Group By Thread", Rt.buildReport(LogStatAnalyze_GroupBy.collectStatsByThread(pathStr, AppZos.getLogGetterDate()), "LogStats:" + pathStr).toString(), true).toString());
		menupopup.addMenuitem(ALI.STATS + "Log Analyze GroupByClass", e -> ZKME.openEditorWithContent("Group By Class", Rt.buildReport(LogStatAnalyze_GroupBy.collectStatsByGroup(pathStr, AppZos.getLogGetterDate()), "LogStats:" + pathStr).toString(), true).toString());
		menupopup.add_______();
		menupopup.addMenuitem(ALI.STATS + "Log Analyze Thread History", e -> ZKME.openEditorWithContent("Group By Thread History", LogStatAnalyze_ThreadHistory.collectHistoryByThread_REPORT(pathStr, AppZos.getLogGetterDate(), AppZos.getLogLineMapping()).toString(), true).toString());

	}


}
