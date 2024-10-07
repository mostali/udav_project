package zk_os;

import mpu.X;
import mpc.env.boot.BootRunUtils;
import mpc.exception.NotifyMessageRtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_com.ck_editor.CkEditorComposer;
import zk_com.win.HideBy;
import zk_form.notify.NotifyPanel;
import zk_old_core.control_old.TopAdminMenu;
import zk_old_core.old.fswin.FsWin;
import zk_old_core.old.mwin.MWin;
import zk_page.ZKR;
import zk_page.ZKC;
import zk_form.notify.ZKI_Window;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Paths;
import java.util.Collections;

public class AppCmdUI {

	public static final Logger L = LoggerFactory.getLogger(AppCmdUI.class);

	public static boolean execute_(String cmd) {
		try {
			return execute(cmd);
		} catch (Throwable ex) {
			ZKI_Window.errorIQ(ex);
			return true;
		}
	}

	public static boolean execute(String cmd) {

		if (cmd.startsWith("^")) {
			if ("^".equals(cmd)) {
				FsWin.openRPA(true);
				return true;
			}
			String tryCmd = cmd.substring(1);
			FsWin.openStdLocation(tryCmd, true);
			return true;
		} else if (cmd.startsWith("download ")) {
			String file = cmd.substring("download ".length());
			ZKR.download(Paths.get(file));
			return true;
		}

		switch (cmd) {
			case "v":
				String versionFromAny = BootRunUtils.getVersionFromAny(ZKR.class, null);
				NotifyPanel.ViewPosition.BOTTOM_RIGHT.show(versionFromAny, HideBy.DBL_CLICK);
				return true;
			case "r": {
				ZKR.rebuildPage();
				return true;
			}
			case "ce": {
				MWin mWin = MWin.findFirstOrOpen(true);
				CkEditorComposer.loadComponent(mWin.getFormComPath(), mWin.getMainViewComponent());
//				mWin.showComponent(cKeditor);
				return true;
			}
			case "tm":
			case "tm~": {
				if (cmd.endsWith("~")) {
					TopAdminMenu.resetState();
				} else {
					new TopAdminMenu().append();
				}
				return true;
			}
			case "fs":
			case "fs~": {
				boolean reset = cmd.endsWith("~");
				PageDirModel pageDirModel = PageDirModel.get();
				if (reset) {
					FsWin.reset(FsWin.class, pageDirModel.getFileStateFs());
					ZKR.rebuildPage();
					throw NotifyMessageRtException.LEVEL.GREEN.I("FsWin reset");
				}
				if (X.empty(FsWin.findAll(Collections.EMPTY_LIST))) {
					FsWin.openPageDir(pageDirModel, true);
				}
				return true;
			}
			case "mw":
			case "mw~": {
				boolean reset = cmd.endsWith("~");
				PageDirModel pageDirModel = PageDirModel.get();
				MWin mw = MWin.ofPage(pageDirModel.path());
				if (reset) {
					MWin.reset(MWin.class, pageDirModel.getFileState(MWin.class, false));
					ZKR.rebuildPage();
					throw NotifyMessageRtException.LEVEL.GREEN.I("MWin reset");
				}
				if (X.empty(MWin.findAll(Collections.EMPTY_LIST))) {
					ZKC.getFirstWindow().appendChild(mw);
				}
				return true;
			}
			case "zklog+":
			case "zklog-": {
				AppZosConfig.ZK_LOG_ENABLE = cmd.endsWith("+");
				throw NotifyMessageRtException.LEVEL.GREEN.I("apply: " + cmd);
			}

			default:
				return false;
		}
	}


}
