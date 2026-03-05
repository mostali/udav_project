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
import zk_form.dirview.DirView;
import zk_form.dirview.DirViewMenu;
import zk_os.AppZos;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
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
		DirViewMenu.applyMenuDir(menupopup, path);
		menupopup.add_______();
		applyDirMenuUtils(menupopup, path);
	}

	private static void applyDirMenuUtils(Menupopup0 menupopup, Path path) {
		Menupopup0 fileUtilsMenu = menupopup.addInnerMenu("File Utils..");
		fileUtilsMenu.addMI(ALI.SEARCH_FILE + "Find file twins..", e -> {
			Map<Path, List<Path>> twins = UFS.TWINS.findTwinsAsMap(path, true);
			if (X.empty(twins)) {
				ZKI.infoSingleLine("Twins not found");
			} else {
				AtomicReference<Window> ref = new AtomicReference();
				SerializableEventListener btRmEvent = (btE) -> ZKI_Quest.showMessageBoxBlueYN("Removing twins..", "Remove twins files?", (Boolean yn) -> {
					if (!yn) {
						return;
					}
					UUFS.removeTwins(twins);
					ref.get().onClose();
					DirView.findFirst().rerender();
					ZKI.showMsgBottomRightFast_INFO("Removed");
				});
				Pare<Window, Tbxm> removeTwins = ZKI.infoEditorDark(Bt.of("Remove empty files", btRmEvent), Rt.buildReport(twins));
				ref.set(removeTwins.key());
			}
		});

		fileUtilsMenu.addMI(ALI.SEARCH_FILE + "Find empty files..", e -> {
			Collection<File> emptyFiles = UDIR.lsAll(path.toFile(), f -> !UFS.isFileWithContent(f.toPath()));
			if (X.empty(emptyFiles)) {
				ZKI.infoSingleLine("Empty files not found");
			} else {
				AtomicReference<Window> ref = new AtomicReference();
				SerializableEventListener btRmEvent = (btE) -> ZKI_Quest.showMessageBoxBlueYN("Removing empty files..", "Remove this files?", (Boolean yn) -> {
					if (!yn) {
						return;
					}
					emptyFiles.forEach(UFS.RM::fileQk);
					ref.get().onClose();
					DirView.findFirst().rerender();
					ZKI.showMsgBottomRightFast_INFO("Removed");
				});
				Pare<Window, Tbxm> removeTwins = ZKI.infoEditorDark(Bt.of("Removing empty files", btRmEvent), Rt.buildReport(emptyFiles));
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
		DirViewMenu.applyMenuFile(menupopup, path);
		menupopup.add_______();
		applyLogUtilsExt(menupopup, path);
	}

	public static void applyLogUtilsExt(Menupopup0 menupopup, Path path) {
		menupopup.add_______();
		applyLogUtils(menupopup, path);

		menupopup.add_______(true);

		String pathStr = path.toString();

		menupopup.addMI(ALI.STATS + " Log Analyze GroupByThread", e -> ZKME.textReadonly("Group By Thread", Rt.buildReport(LogStatAnalyze_GroupBy.collectStatsByThread(pathStr, AppZos.getLogGetterDate()), "LogStats:" + pathStr).toString(), true).toString());
		menupopup.addMI(ALI.STATS + " Log Analyze GroupByClass", e -> ZKME.textReadonly("Group By Class", Rt.buildReport(LogStatAnalyze_GroupBy.collectStatsByGroup(pathStr, AppZos.getLogGetterDate()), "LogStats:" + pathStr).toString(), true).toString());
		menupopup.add_______();
		menupopup.addMI(ALI.STATS + " Log Analyze Thread History", e -> ZKME.textReadonly("Group By Thread History", LogStatAnalyze_ThreadHistory.collectHistoryByThread_REPORT(pathStr, AppZos.getLogGetterDate(), AppZos.getLogLineMapping()).toString(), true).toString());
	}

	public static void applyLogUtils(Menupopup0 menupopup, Path path) {
		String pathStr = path.toString();
		menupopup.addMI(ALI.LOGVIEW + " Log View", e -> LogFileView.openSingly(pathStr));
		menupopup.add_______();
	}

}
