package zk_page.core;

import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class RequestInfo implements Serializable {

	private final HttpServletRequest request;
	private final String referer;

	public RequestInfo(HttpServletRequest request) {
		this.request = request;
		this.referer = request.getHeader("Referer");
	}

	public PagePathInfo getRefererPpi(PagePathInfo... defRq) {
		if (referer != null) {
			return new PagePathInfo(request, (Integer) null);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Referer PPI not found from request '%s'", request), defRq);
	}

//	public PagePathInfo getForwardPpi(PagePathInfo... defRq) {
//		//String fwd = (String) request.getAttribute("javax.servlet.forward.servlet_path");
//		String fwd = (String) request.getAttribute("javax.servlet.forward.request_uri");
//		if (fwd != null) {
//			String sd3 = AppZosWeb.getSd3(request);
//			return new PagePathInfo(sd3, fwd);
//		}
//
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("Forward PPI not found from request '%s'", request), defRq);
//	}
}
