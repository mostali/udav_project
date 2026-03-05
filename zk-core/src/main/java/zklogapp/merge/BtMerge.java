package zklogapp.merge;

import lombok.Getter;
import mpu.X;
import mpc.fs.UDIR;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpe.logs.filter.ILogFilter;
import mpe.logs.filter.merger.LogMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Page;
import zk_com.base.Bt;
import zk_form.dirview.DirView;
import zk_form.notify.ZKI;
import zk_os.AppZos;
import zklogapp.AppLog;
import zk_page.panels.BottomHistoryPanel;
import zklogapp.filter.HeadLogFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BtMerge extends Bt {

	public static Logger L = LoggerFactory.getLogger(BtMerge.class);

	private final String dstParentDir;

	@Getter
	private final List<String> mergeFiles = new ArrayList<>();

	public BtMerge(String dstParentDir) {
		super("Merge");
		this.dstParentDir = dstParentDir;
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);

		onCLICK(event -> {

			HeadLogFilter headLogFilter = (HeadLogFilter) getParent();

			List<String> mergeFiles = getMergeFiles();
			if (isUseAllRootDir) {
				List<Path> mergeFiles1 = UDIR.lsAll(Paths.get(mergeFiles.get(0))).stream().filter(AppLog::isAllowedFile).collect(Collectors.toList());
				mergeFiles = (List<String>) UFS.convert(mergeFiles1, String.class);
				ZKI.log("Use all root dir:" + mergeFiles);
			}

			boolean collapseMultilineToSingleLine = headLogFilter.getCbCollapseMultiLine().isChecked();

			String sfxCollapsed = collapseMultilineToSingleLine ? "l" : "z";

			String filesFnPart = ILogFilter.toStringFnPart(mergeFiles);

			String fileName = "mr." + filesFnPart + "." + headLogFilter.toStringFnPart() + "_" + sfxCollapsed + ".log";

			String dstFile = UF.normFile(dstParentDir, fileName);
			if (UFS.isFileWithContent(dstFile)) {
				L.warn("Merged file {} ALREADY EXIST", dstFile);
			} else {
				List<Path> mergeFilePaths = (List<Path>) UFS.convert(mergeFiles, Path.class);
				LogMerger.mergeLog(AppZos.getLogGetterDate(), mergeFilePaths, dstFile, headLogFilter, collapseMultilineToSingleLine, L);
			}

			BottomHistoryPanel.addItem(dstFile, true);

			DirView.findFirst().rerender();

		});

		initDisableProperty();

	}

	public void initDisableProperty() {
		setDisabled(X.empty(mergeFiles));
	}

	private boolean isUseAllRootDir;

	public void initUseAllRootDir(boolean isUseAllRootDir) {
		this.isUseAllRootDir = isUseAllRootDir;
	}
}
