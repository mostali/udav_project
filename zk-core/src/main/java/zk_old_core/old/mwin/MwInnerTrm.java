package zk_old_core.old.mwin;

import lombok.RequiredArgsConstructor;
import mpu.X;
import mpc.exception.NotifyMessageRtException;
import mpc.fs.UFS;
import mpc.json.GsonMap;
import mpc.str.sym.SYMJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.*;
import zk_com.win.Win0;
import zk_com.win.WinPos;
import zk_com.base.Bt;
import zk_com.base.Dd;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Span0;
import zk_page.ZKC;
import zk_page.ZKCF;
import zk_page.ZKME;
import zk_page.ZKR;
import zk_old_core.old.fswin.FsUI;
import zk_old_core.old.per_win.PerWin;
import zk_com.uploader.ClipboardLoaderComposer;
import zk_os.AppCmdUI;
import zk_form.notify.ZKI_Log;
import zk_form.notify.ZKI_Window;
import zk_old_core.sd.core.SdRsrc;
import zk_old_core.mdl.FormDirModel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class MwInnerTrm extends Span0 {
	private static final Logger L = LoggerFactory.getLogger(MwInnerTrm.class);

	public static final String HISTORY_INNER_TRM = "history.inner.trm";
	public static final int HISTORY_MW_INNER_TRM_SIZE = 50;
	final MWin mWin;

	public static final String EV_PAGE = "`$PAGE`";
	public static final String EV_APP = "`$APP`";

	@Override
	protected void init() {

		appendLb(SYMJ.MONITOR);

		Dd innerDdTrm = new Dd("", getAllHistoryItems());
		innerDdTrm.setWidth(MWin.DEF_WIDTH_DD);
		innerDdTrm.onOK((SerializableEventListener) event -> {
			String cmd = innerDdTrm.getValue();
			try {
				handleCmd(cmd);
			} catch (Throwable ex) {
				if (L.isErrorEnabled()) {
					L.error("MwInnerTrm:handleCmd", ex);
				}
				ZKI_Window.errorIQ(ex);
			} finally {
				Path pageUsrJsonPath = mWin.getPageUsrJsonPath();
				addHistoryItem(pageUsrJsonPath, HISTORY_INNER_TRM, cmd);
			}
		});

		appendChild(innerDdTrm);

	}

	private void handleCmd(String cmd) {
		if (AppCmdUI.execute(cmd)) {
			return;
		}
		SdRsrc.LocRsrc locRsrc = SdRsrc.LocRsrc.ofShortCmd(cmd, null);
		if (locRsrc != null) {
//			Path stdLocation = locRsrc.getParentOfStdLocation(mWin.getPageDirModel().path());
			Path stdLocation = locRsrc.getParentOfStdLocation(mWin.getModelSP().ppi());
			PerWin.MainView mainView = mWin.getMainViewComponent();
			Div element = new Div();
			mainView.getChildren().add(0, element);
			ClipboardLoaderComposer.loadComponent("Upload image to '" + locRsrc.nameru() + "'", stdLocation, element);
			return;
		}

		if ("E".equals(cmd)) {
			Path path = mWin.getFormComPath(null);
			if (path == null) {
				path = mWin.getPageFormPath(null);
				if (path != null) {
					path = FormDirModel.of(path).getFileRootProps();
				}
			}
			if (path == null) {
				throw NotifyMessageRtException.LEVEL.RED.I("Set form");
			}
			ZKME.openEditorImgOrText(path);
			return;
		} else if (cmd.startsWith("E ")) {
			String filePath = cmd.substring(2);
			filePath = filePath.replace(EV_PAGE, mWin.getPageDirModel().path().toString());
			filePath = filePath.replace(EV_APP, ".");
			Path path0 = Paths.get(filePath);
			if (!UFS.existFile(path0)) {
				ZKI_Log.alert("File '%s' not found", filePath);
				return;
			}
			mWin.showContent(path0);
			return;
		}

		if ("rm".equals(cmd)) {
			Path pathFormCom = mWin.getFormComPath(null);
			Path pathPageForm = mWin.getPageFormPath(null);

			List<Bt> bts = new ArrayList<>();

			if (pathPageForm != null) {
				String pathPageFormStr = pathPageForm.toString();

				bts.add(Bt.of(X.f("Remove whole page form '%s' ?", pathPageFormStr), (SerializableEventListener<Event>) event -> {
					FsUI.rmSafe(pathPageFormStr, () -> {
						ZKCF.findAllFromParent(ZKC.getFirstWindow(), Win0.class, true).get(0).detach();
						//NtfEE.LEVEL.WRN.I("Removed '" + pathPageFormStr + "'").duration(5000).closable().position(NtfEE.NPos.after_pointer).show();
						ZKR.rebuildPage();
						return null;
					});
				}));
			}

			if (pathFormCom != null) {
				String pathFormComStr = pathFormCom.toString();
				bts.add(Bt.of(X.f("Remove only form com '%s' ?", pathFormComStr), (SerializableEventListener<Event>) event -> {
					FsUI.rmSafe(pathFormComStr, () -> {
						Win0 notifyPanel = ZKCF.findAllFromParent(ZKC.getFirstWindow(), Win0.class, true).get(0);
						//notifyPanel.closeViaJs();
						notifyPanel.detach();
						//.View.BOTTOM_RIGHT.show("Removed '" + pathFormComStr + "'", NotifyPanel.HideBy.TIMEOUT);
						//NtfEE.LEVEL.WRN.I("Removed '" + pathFormComStr + "'").duration(5000).closable().position(NtfEE.NPos.after_pointer).show();
						//TstEE.LEVEL.INFO.I("Removed '" + pathFormComStr + "'").closable().position(SWindow.WPos.center).show();
						ZKR.rebuildPage();
						return null;
					});
				}));

				Win0 swin = Win0.of(Div0.of((List) bts)).hl().caption("Removing form..").position(WinPos.center);
				((Win0) swin.appendTo()).doHighlighted();

				return;

			}
			if (X.empty(bts)) {
				throw NotifyMessageRtException.LEVEL.RED.I("Set page form or form component");
			}

			return;
		}

		throw NotifyMessageRtException.LEVEL.RED.I("Undefined Command  '%s'", cmd);
	}

	public static void addHistoryItem(Path pageUsrJsonPath, String historyProperty, String cmd) {
		GsonMap gm = GsonMap.read(pageUsrJsonPath, true);
		List l = (List) gm.get(HISTORY_INNER_TRM, null);
		if (l == null) {
			gm.put(HISTORY_INNER_TRM, l = new ArrayList());
		}
		if (!l.contains(cmd)) {
			l.add(cmd);
		}
		while (l.size() > HISTORY_MW_INNER_TRM_SIZE) {
			l.remove(HISTORY_MW_INNER_TRM_SIZE);
		}
		GsonMap.write(pageUsrJsonPath, gm);
	}

	private List<String> getAllHistoryItems() {
		return (List<String>) GsonMap.read(mWin.getPageUsrJsonPath(), true).get(HISTORY_INNER_TRM, Collections.EMPTY_LIST);
	}
}
