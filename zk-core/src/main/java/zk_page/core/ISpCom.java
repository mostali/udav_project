package zk_page.core;

import org.zkoss.zul.Window;
import zk_com.core.IZComExt;
import zk_page.ZKC;

/**
 * @author dav 08.05.2022
 */
public interface ISpCom extends IZComExt {

	default SpVM getModelSP(SpVM... defRq) {
		return findSP(defRq);
	}

//	default PerWin getMWin() {
//		Component com = (Component) this;
//		MWin mwin = ZKCFinder.findAll(com.getPage(), MWin.class, true).get(0);
//		return mwin;
//	}

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

}
