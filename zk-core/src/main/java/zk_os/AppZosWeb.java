package zk_os;

import mpu.core.ARR;
import mpu.IT;
import mpc.env.AP;
import mpc.rfl.UReflScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import utl_web.UWeb;
import zk_page.core.PageRoute;
import zk_page.core.SpVM;
import zk_page.core.FinderPSP;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AppZosWeb {

	public static final Logger L = LoggerFactory.getLogger(AppZosWeb.class);

	//	public static final String APK_NODE_DOMAIN = "app.node.domain";
	public static final String APK_SD3_INDEX = "sd3.index";
	public static final int SD3_INDEX;

	static {
		SD3_INDEX = AP.getAs(APK_SD3_INDEX, Integer.class, 2);
	}

	public static void regPageEntity(String... packages) {
		List<Class> pages = UReflScanner.getAllPackageClassViaClassgraph(packages, PageRoute.class);
		regPageEntity(pages);
	}

	public static void regPageEntity(Class... pages) {
		regPageEntity(ARR.as(pages));
	}

	public static void regPageEntity(List<Class> pages) {
		for (Class page : pages) {
			PageRoute pageRoute = (PageRoute) page.getAnnotation(PageRoute.class);
			IT.NN(pageRoute, page.getSimpleName());
			IT.notEmptyAny(pageRoute.sd3(), pageRoute.pagename());
			if (L.isInfoEnabled()) {
				L.info(">>> >>> Reg PageSP {}@{}, handler:{}", pageRoute.sd3(), pageRoute.pagename(), page);
			}
			FinderPSP._HANDLERS.put(pageRoute, page);
		}
	}

//	private static boolean isEq(PageRoute key, SpVM spVM) {
//		//		if (X.notEmpty(spVM.getAddress()) && X.notEmpty(spVM.getSubdomain3())) {
//		//return key.sd3()
//		//		}
//		return key.sd3().equals(spVM.subdomain3()) && key.pagename().equals(spVM.pagename());
//	}

	public static String getSd3() {
		Execution execution = Executions.getCurrent();
		HttpServletRequest servletRequest = (HttpServletRequest) execution.getNativeRequest();
		return getSd3(servletRequest);
	}

	public static String getSd3(HttpServletRequest servletRequest) {
		return UWeb.getSubDomian_part3(servletRequest, AppZosWeb.SD3_INDEX, "");
	}
}
