package zk_pages;

import mpu.core.ARG;
import org.zkoss.zul.*;
import zk_page.ADDH;
import zk_page.core.SpVM;

/**
 * @author dav 07.01.2022   19:24
 */
public class PageNotFoundSP {

	final SpVM spVM;

	public PageNotFoundSP(SpVM spVM) {
		this.spVM = spVM;
	}

	public void create(Window parentWindow, String... message) {
		String h1 = ARG.toDefOr("Page not found", message);
		ADDH.H0(parentWindow, 1, h1);
		Button home = new Button("Go To Home");
		home.setHref("/");
		parentWindow.appendChild(home);
//		throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Page not found");

	}


}
