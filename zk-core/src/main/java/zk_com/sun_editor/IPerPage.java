package zk_com.sun_editor;

import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import zk_old_core.sd.Sd3EE;
import zk_page.core.PageRoute;
import zk_page.core.SpVM;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Path;

public interface IPerPage {

	default PageRoute getPageRoute() {
		return getClass().getAnnotation(PageRoute.class);
	}

//	@Deprecated
//	default Path getFileState(Class com, boolean json) {
//		return AppCoreStateOld.getPathPageStateOf(getPageDir(), com, json);
//	}

//	default Path getPageDir() {
//		PagePathInfo ppi = SpVM.get().ppi();
//		Path pageDir = AppZosCore.getPageDir(ppi.subdomain3(), ppi.pagename());
//		return pageDir;
//	}

//	class Manager

	@SneakyThrows
	default PageDirModel loadPageModel(boolean createIfNotExist) {
		PageDirModel pdm = SpVM.get().findPageDirModel(null);
		if (pdm != null) {
			return pdm;
		} else if (!createIfNotExist) {
			throw new RequiredRuntimeException("Pdm not found from '%s', pageRoute '%s' \n%s", getClass(), getPageRoute(), this);
		}
		Path page = Sd3EE.getPageOrCreate(getPageRoute());
		return SpVM.get().findPageDirModel();
	}


}
