package zk_old_core.app_ds.struct;

import java.nio.file.Path;

public class PageDirDS extends PageDS {

	public PageDirDS(String page) {
		super(page);
	}

	public static final PageDirDS meta = new PageDirDS("meta");
	public static final PageDirDS head = new PageDirDS("head");
	public static final PageDirDS BODY = new PageDirDS("body");


	public Path getPathFile_PageCss(Path entity) {
		return getPathWith(entity, "page.css");
	}

	public Path getPathFile_PageJs(Path entity) {
		return getPathWith(entity, "page.js");
	}
}
