package zklogapp.header;

import mpu.Sys;
import mpu.X;
import mpu.core.QDate;
import mpc.ui.UColorTheme;
import mpe.rt_exec.GrepExecRq;
import mpv.byteunit.ByteUnit;
import zk_com.base.Bt;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_com.core.IReRender;
import zk_form.events.DefAction;
import zk_form.notify.ZKI;
import zk_page.ZKColor;
import zk_page.ZKS;
import zklogapp.ALI;
import zklogapp.ALM;
import zklogapp.AppLogProps;
import zklogapp.logview.LogFileView;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BottomHistoryPanel extends Div0 implements IReRender {
	private static BottomHistoryPanel findFirst() {
		return IZCom.findFirstInPage(BottomHistoryPanel.class, true);
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

	public static void addItem(String file) {
		Path filePath = Paths.get(file);

		DefAction defAction = e -> {
			boolean isLarge = X.sizeOf(filePath, ByteUnit.MB) > AppLogProps.APR_LOG_CACHE_VIEW_MAX_MB.val();
			if (isLarge) {
				Sys.open_Code(filePath);
			} else {
				if (file.endsWith(GrepExecRq.EXT_GREP)) {
					ZKI.infoEditorBw(filePath);
				} else if (file.endsWith(".log")) {
					LogFileView.openSingly(file);
//					ZKI.infoEditorBw(filePath);
				} else {
					LogFileView.openSingly(file);
				}
			}
		};
		BottomHistoryPanel first = BottomHistoryPanel.findFirst();
		String btName = ALI.LOGVIEW + QDate.now().f(QDate.F.HH_mm) + "*" + X.sizeOfHuStr(filePath);
		Bt bt = (Bt) first.appendBt(defAction, btName).title(file);

		ALM.applyLogFile(Menupopup0.createMenupopup(first, bt, null), filePath);

		ZKS.BGCOLOR(bt, ZKColor.GREENS.nextColor());
		ZKS.COLOR(bt, ZKColor.BLACK.nextColor());

	}

	@Override
	protected void init() {
		super.init();

		ZKS.BGCOLOR(this, UColorTheme.BLACK[0]);
		ZKS.POSITION(this, "fixed");
//		ZKS.WIDTH(this, 100.0);
//		ZKS.HEIGHT(this, 30);
//		ZKS.OPACITY(this, 0.5);
////
//		ZKS.BGCOLOR(this, UColorTheme.BLACK[0]);
//		ZKS.POSITION(this, "fixed");
////		ZKS.WIDTH(this, "50px");
////		ZKS.HEIGHT(this, 30);
//		ZKS.OPACITY(this, 0.5);
//
//		ZKS.RIGHT(this, "10px");
		ZKS.BOTTOM(this, "10px");
		ZKS.ABSOLUTE(this);

	}

}
