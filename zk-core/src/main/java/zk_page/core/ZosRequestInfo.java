package zk_page.core;

import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class ZosRequestInfo implements Serializable {

	private final HttpServletRequest request;
	private final String referer;

	public ZosRequestInfo(HttpServletRequest request) {
		this.request = request;
		this.referer = request.getHeader("Referer");
	}

	public PagePathInfo getRefererPpi(PagePathInfo... defRq) {
		if (referer != null) {
			return new PagePathInfo(request, (Integer) null);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Referer PPI not found from request '%s'", request), defRq);
	}

}
