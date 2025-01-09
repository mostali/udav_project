package zk_com.sun_editor;

import zk_page.core.PageRoute;

public interface IPerPage {

	default PageRoute getPageRoute() {
		return getClass().getAnnotation(PageRoute.class);
	}

}
