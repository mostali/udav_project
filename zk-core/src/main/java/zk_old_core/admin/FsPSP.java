package zk_old_core.admin;

import lombok.SneakyThrows;
import org.zkoss.zul.Window;
import zk_com.sun_editor.IPerPage;
import zk_old_core.old.fswin.FsWin;
import zk_os.sec.ROLE;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;

//@VariableResolver(DelegatingVariableResolver.class)
@PageRoute(pagename = "", sd3 = "fs", role = ROLE.ADMIN)
public class FsPSP extends PageSP implements IPerPage {

	public FsPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}


//	@Override
//	protected MatrixAccess getMA() {
//		return MatrixAccess.of(getPageRoute().role());
//		return ROLE.hasRole(getPageRoute().role());
//	}


	@SneakyThrows
	public void buildPageImpl() {

		loadPageModel(true).setAttributeTo(window);

		if (!FsWin.openRPA(false)) {
			return;
		}

	}

}
