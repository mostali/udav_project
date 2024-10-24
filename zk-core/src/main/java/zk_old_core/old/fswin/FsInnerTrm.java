package zk_old_core.old.fswin;

import lombok.RequiredArgsConstructor;
import mpc.exception.NotifyMessageRtException;
import mpc.fs.UFS;
import mpc.json.GsonMap;
import mpc.str.sym.SYMJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Dd;
import zk_com.base_ctr.Span0;
import zk_old_core.old.fswin.core.FdView;
import zk_old_core.old.mwin.MwInnerTrm;
import zk_page.ZKCFinder;
import zk_form.notify.ZKI_Window;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class FsInnerTrm extends Span0 {
	private static final Logger L = LoggerFactory.getLogger(MwInnerTrm.class);

	public static final String HISTORY_FS_INNER_TRM = "history.fs.trm";
	public static final int HISTORY_FS_INNER_TRM_SIZE = 30;
	final FsWin fsWin;

	Dd innerDdTrm;

	public Dd getInnerDdTrm() {
		return innerDdTrm == null ? innerDdTrm = new Dd("", getAllHistoryItems()) : innerDdTrm;
	}

	@Override
	protected void init() {

		appendLb(SYMJ.MONITOR);

		Dd innerDdTrm = getInnerDdTrm();
		innerDdTrm.setWidth(FsWin.DEF_WIDTH_DD);
		innerDdTrm.onOK((SerializableEventListener) event -> {
			String cmd = innerDdTrm.getValue();
			try {
				handleCmd(cmd.trim());
			} catch (Throwable ex) {
				if (L.isErrorEnabled()) {
					L.error("MwInnerTrm:handleCmd", ex);
				}
				ZKI_Window.errorIQ(ex);
			} finally {
				Path pageUsrJsonPath = fsWin.getPageUsrJsonPath();
				addHistoryItem(pageUsrJsonPath, HISTORY_FS_INNER_TRM, cmd);
			}
		});

		appendChild(innerDdTrm);
	}

	private void handleCmd(String cmd) {
		if (cmd.isEmpty()) {
			return;
		}
		switch (cmd) {
//			case "!up": {
//				String uploadTo = fsWin.getChoicedFd().getChoicedPath().toString();
//				Path uploadDir = UCZK.isDir(Paths.get(uploadTo));
//				FileUploaderComposer.loadComponent("Upload to " + uploadDir, uploadDir, fsWin.getHeaderCaption());
//				return;
//			}
			case "rm": {
				String path = fsWin.getChoicedFd().getChoicedPath().toString();
				FsUI.rmSafe(path, () -> {
					List<FdView> files = ZKCFinder.findAllFromParent(fsWin, FdView.class, true, Collections.EMPTY_LIST);
					files.forEach(f -> {
						if (f.path().toString().equals(path)) {
							f.detach();
						}
					});
					return null;
				});
				return;
			}
		}
		if (cmd.startsWith("mv ") || cmd.startsWith("cp ")) {
			String path = fsWin.getChoicedFd().getChoicedPath().toString();
			String mvCmd = cmd.substring(3).trim();
			if (UFS.exist(Paths.get(mvCmd))) {
				throw NotifyMessageRtException.LEVEL.RED.I("Destination '%s' already exist", mvCmd);
			}
			Supplier supplier = () -> {
				List<FdView> files = ZKCFinder.findAllFromParent(fsWin, FdView.class, true, Collections.EMPTY_LIST);
				files.forEach(f -> {
					if (f.path().toString().equals(path)) {
						f.detach();
					}
				});
				return null;
			};
			if (cmd.startsWith("mv ")) {
				FsUI.mv(path, mvCmd, supplier);
			} else {
				FsUI.cp(path, mvCmd, supplier);
			}
			return;
		}
		throw NotifyMessageRtException.LEVEL.RED.I("Undefined Command  '%s'", cmd);
	}

	public static void addHistoryItem(Path pageUsrJsonPath, String historyProperty, String cmd) {
		GsonMap gm = GsonMap.read(pageUsrJsonPath, true);
		List l = (List) gm.get(HISTORY_FS_INNER_TRM, null);
		if (l == null) {
			gm.put(HISTORY_FS_INNER_TRM, l = new ArrayList());
		}
		if (!l.contains(cmd)) {
			l.add(cmd);
		}
		while (l.size() > HISTORY_FS_INNER_TRM_SIZE) {
			l.remove(HISTORY_FS_INNER_TRM_SIZE);
		}
		GsonMap.write(pageUsrJsonPath, gm);
	}

	private List<String> getAllHistoryItems() {
		return (List<String>) GsonMap.read(fsWin.getPageUsrJsonPath(), true).get(HISTORY_FS_INNER_TRM, Collections.EMPTY_LIST);
	}


}
