package zk_old_core.app_ds.struct;

import zk_old_core.app_ds.AppDS;

import java.nio.file.Files;
import java.nio.file.Path;

public class PageDS extends AppDS {

	public static final PageDS SELF = new PageDS(".");

	public PageDS(String page) {
		super(page);
	}

//	@Deprecated
//	public RuProps getPageProps(Path pageDir, boolean... syncWrite) {
//		return super.getRuProps(pageDir, FN_PAGE_PROPS, syncWrite);
//	}

//	public Path getPathFile_MWin(Path entity) {
//		return super.getPathWith(entity, FN_MWIN_PROPS);
//	}
//
//	public Path getPathFile_FsWin(Path entity) {
//		return super.getPathWith(entity, FN_FSWIN_PROPS);
//	}

	public Path getPageUsrJsonPath(Path entity) {
		return super.getPathWith(entity, FN_USR_JSON);
	}

	public Path getJsonOrPropsPath(Path entity) {
		Path path = getPathWith(entity, FN_PAGE_JSON);
		if (Files.isRegularFile(path)) {
			return path;
		}
		Path propsPath = getPathWith(entity, FN_PAGE_PROPS);
		if (Files.isRegularFile(propsPath)) {
			return propsPath;
		}
		return path;
	}
}
