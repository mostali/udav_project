package zk_page.core;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_old_core.old.mwin.MWin;
import zk_old_core.old.per_win.PerWin;
import zk_old_core.sd.core.SdMan;
import zk_page.ZKCF;
import zk_old_core.app_ds.struct.PageDirDS;
import zk_old_core.mdl.PageDirModel;
import zk_com.core.IZComExt;
import zk_old_core.mdl.pageset.PageSet;
import zk_page.ZKC;

import java.nio.file.Path;

/**
 * @author dav 08.05.2022
 */
public interface ISpCom extends IZComExt {

	default SpVM getModelSP(SpVM... defRq) {
		return findSP(defRq);
	}

	default PerWin getMWin() {
		Component com = (Component) this;
		MWin mwin = ZKCF.findAll(com.getPage(), MWin.class, true).get(0);
		return mwin;
	}

	static SpVM findSP(SpVM... defRq) {
//		SpVM vm = ZkQ.Q_SPVM.get();
//		if (vm != null) {
//			return vm;
//		}
		//vm, $VM$, $VM_ID$
//		vm = SpVM.get(defRq);

		return SpVM.get(defRq);
	}

	default Window getFirstWindow() {
		return ZKC.getFirstWindow();
	}

	default PageDirModel getPageDirModel(PageDirModel... defRq) {
		return SdMan.getPageModelFromComponent(getFirstWindow(), defRq);
	}

	default Path getPageUsrJsonPath(Path... defRq) {
		return PageDirDS.meta.getPageUsrJsonPath(getPageDirModel().path());
	}

	default PageSet getPageSet(PageSet... defRq) {
		PageDirModel pdm = getPageDirModel(null);
		if (pdm != null) {
			return pdm.getPageSet();
		}
		return ARG.toDefRq(defRq);
	}

}
