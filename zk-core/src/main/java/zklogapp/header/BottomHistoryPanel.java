package zklogapp.header;

import mpc.fs.UFS;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.QDate;
import mpc.ui.ColorTheme;
import mpe.rt_exec.GrepExecRq;
import mpu.core.RW;
import mpu.func.FunctionV1;
import mpv.byteunit.ByteUnit;
import zk_com.base.Bt;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_com.core.IReRender;
import zk_form.events.DefAction;
import zk_form.events.RemoveFileWithConfirmation_SerializableEventListener;
import zk_form.notify.ZKI;
import zk_page.ZKColor;
import zk_page.ZKR;
import zk_page.ZKS;
import zklogapp.ALI;
import zklogapp.ALM;
import zklogapp.AppLogProps;
import zklogapp.logview.LogFileView;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class BottomHistoryPanel extends Div0 implements IReRender {

	public static final Path TMP_STORE_DIR = Paths.get("tmp").resolve("bottom-history-panel-data");

	private static List<Path> TMP_STORE_DIR_LS() {
		return UFS.ls(TMP_STORE_DIR, ARR.EMPTY_LIST);
	}

	private static BottomHistoryPanel findFirst(BottomHistoryPanel... defRq) {
		return IZCom.findFirstInPage(BottomHistoryPanel.class, true, defRq);
	}

	public static BottomHistoryPanel rerender(BottomHistoryPanel... defRq) {
		BottomHistoryPanel first = removeMeFirst(defRq);
		if (first != null) {
			first.rerender(true);
		}
		return first;
	}

	public static BottomHistoryPanel removeMeFirst(BottomHistoryPanel... defRq) {
		return IZCom.removeMeFirst(BottomHistoryPanel.class, true, defRq);
	}


	//	public static Path addItemAsDataWI(String rsp, boolean... skipInfo) {
//
//	}
	public static Path addItemAsData(String rsp, boolean... skipInfo) {
		Path file = TMP_STORE_DIR.resolve(QDate.now().f(QDate.F.MONO15_FILE_SEC) + ".tmp.rsp");
		RW.write(file, rsp, true);
		addItem(file.toString(), skipInfo);
		return file;
	}


	public static void addItem(String file, boolean... skipInfo) {
		BottomHistoryPanel firstPanel = BottomHistoryPanel.findFirst(null);
		if (firstPanel == null) {
			return;
		}
		addItem(firstPanel, file, true, skipInfo);
	}

	public static void addItem(BottomHistoryPanel firstPanel, String file, boolean open, boolean... skipInfo) {

		Path filePath = Paths.get(file);

		DefAction defAction = e -> {
			boolean isLarge = X.sizeOf(filePath, ByteUnit.MB) > AppLogProps.APR_LOG_CACHE_VIEW_MAX_MB.getValueOrDefault();
			if (isLarge) {

				ZKI.showMsgBottomRightFast_INFO("File too large");

				Sys.open_Code(filePath);
			} else {
				FunctionV1<Path> view = (path) -> ZKI.infoEditorBw(path);
				if (file.endsWith(GrepExecRq.EXT_GREP)) {
					//view
				} else if (file.endsWith(".log")) {
					LogFileView.openSingly(file);
					view = null;
				} else {
					//view
				}
				if (view != null) {
					view.apply(filePath);
				}
			}
		};

		String btName = ALI.LOGVIEW + QDate.now().f(QDate.F.HH_mm) + "*" + X.sizeOfHu(filePath);

		Bt bt = (Bt) firstPanel.appendBt(defAction, btName).title(file);

		if (open) {
			if (!ARG.isDefEqTrue(skipInfo)) {
				ZKI.infoEditorBw(filePath);
			}
			Menupopup0 orCreateMenupopup = bt.getOrCreateMenupopup(firstPanel);
			ALM.applyLogFileWithUtils(orCreateMenupopup, filePath);
		}

		ZKS.BGCOLOR(bt, ZKColor.GREEN.nextColor());
		ZKS.COLOR(bt, ZKColor.BLACK.nextColor());

	}

	int row = 1;
	int[] rows = new int[]{10, -10};

	@Override
	protected void init() {
		super.init();

		QDate now = QDate.now();
		List<Path> paths = TMP_STORE_DIR_LS();
//		if (X.notEmpty(paths)) {
//			DefAction defAction = a -> ZKR.restartPage();
		DefAction defAction = a -> rerender();
		Bt clearAll = appendBt(new RemoveFileWithConfirmation_SerializableEventListener(TMP_STORE_DIR.toString(), defAction), "Clear all");
//		}
		for (Path tmpFile : paths) {
//			Integer day = USToken.first(tmpFile.getFileName().toString(), ".", Integer.class);
//			if(day==now.day)
			addItem(this, tmpFile.toString(), false);

		}
		ZKS.BGCOLOR(this, ColorTheme.BLACK[0]);
		ZKS.POSITION(this, "fixed");
		ZKS.BOTTOM(this, rows[row] + "px");
		ZKS.ABSOLUTE(this);

	}

}
