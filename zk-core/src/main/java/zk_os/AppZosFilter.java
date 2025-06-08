package zk_os;

import mpu.X;
import mpc.env.boot.BootRunUtils;
import mpc.fs.UUrl;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import mpe.wthttp.ContentType;
import utl_web.URsp;
import utl_web.UWeb;
import zk_notes.apiv1.ApiCase;
import zk_notes.apiv1.treenode.DockerRestCall;
import zk_os.db.net.WebUsr;
import zk_os.sec.ZAuth;
import zk_page.core.PagePathInfo;
//import zk_page.core.PagePathInfoWithQuery;
import zk_page.core.ZosRequestInfo;
import zk_os.sec.Sec;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AppZosFilter implements Filter {

	private static final Logger L = LoggerFactory.getLogger(AppZosFilter.class);

	public AppZosFilter() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		X.nothing();
		//фильтр работает в контексте веб контейнера, и бины спринга ему не доступны.
		//поэтому, если хотим использовать здесь userDao, например, необходимо выполнить следующий метод:
//		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		String rsrcPath = UWeb.getServletPathWithoutContext(httpRequest);

		{
			Authentication auth = Sec.getAuth(null);
			if (auth == null) {
				String header = ((HttpServletRequest) request).getHeader("ZN-Remote-User");
				if (X.notEmpty(header)) {
					ZAuth zAuth = Sec.setAuthByUserUuid(header);
					WebUsr usr = zAuth.webUsr();
					if (L.isInfoEnabled()) {
						L.info("Apply {} setAuthByUserUuid", usr.getFirst_name());
					}
				} else {
					X.nothing();
				}
			}
		}

		//		String subdomain = UWeb.getSubDomian_part3(httpRequest);

//		if (rsrcPath.equals("notes/*/ivideo")) {
//			Path file = Paths.get("/home/dav/.data/bea/.sd3/.index/notes/.forms/ivideo/chmok.MP4");
//			UWeb.responseContentTypeFromFile((HttpServletResponse) response, ContentType.VIDEO_MP4, file.toFile());
//			return;
//		}
		ZosRequestInfo requestInfo = new ZosRequestInfo(httpRequest);

		PagePathInfo ppiRef = null;

		__SKIP:
		if (true) {

			/**
			 * *************************************************************
			 * ------------------------- Resources --------------------------
			 * *************************************************************
			 */

			ppiRef = requestInfo.getRefererPpi(null);

			if (ppiRef == null) {
				break __SKIP;
			}

			String ext = UUrl.getExtFromUrlPath(rsrcPath, "").toLowerCase();
			if (ext.isEmpty()) {
				break __SKIP;
			}

			ContentType contentType = ContentType.ofExt(ext, null);
			if (contentType == null) {
				//nothing
				break __SKIP;
			}
			switch (contentType) {
				default:
					//nothing
					break __SKIP;
				case TXT_CSS:
				case IMG_PNG:
				case TXT_JS:
				case IMG_ICON:
				case MP3:
				case VIDEO_MP4:
					//check from fs|rsrc only this content
			}

//			Path parent = SdRsrc.getParentLocation(rsrcPath, ppiRef);
//			if (parent == null) {
			//it not rsrc from fs, that from resources?
			if (rsrcPath.startsWith("_com/")) {
				URsp.sendResponseContentType_FromRsrc((HttpServletResponse) response, contentType, AppZosFilter.class, ".", rsrcPath);
			} else {
				URsp.sendResponseContentType_FromRsrc((HttpServletResponse) response, contentType, AppZosFilter.class, "/web", rsrcPath);
			}
//			} else {
//				String filenameWithoutFirstItem = rsrcPath.substring(rsrcPath.indexOf('/') + 1);
//				File rsrcFile = parent.resolve(filenameWithoutFirstItem).toFile();
//				URsp.sendResponseContentType_FromFile((HttpServletResponse) response, contentType, rsrcFile);
//			}
			return;

		}


		String servletPath = ((HttpServletRequest) request).getServletPath();

		switch (rsrcPath) {
			case "zkau":
				break;
			case "__v__":
				response.getWriter().write(BootRunUtils.getVersionFromBuildInfo());
				return;
			default:
				//nothing
		}

		ApiCase apiCase = ApiCase.ofServletPath(servletPath);
		if (apiCase != null) {

			switch (apiCase) {
				case _aci:
					DockerRestCall dockerRestCall = new DockerRestCall(Pare.of(servletPath, ((HttpServletRequest) request).getQueryString()));
					Pare<Integer, String> rsp = dockerRestCall.apply();
					response.getWriter().write(rsp.val().toString());
					return;
//					break;
				default:
					if (AppZosFilterApiCall.extractApiPostDeleteRequest(apiCase, request, (HttpServletResponse) response, servletPath, httpRequest)) {
						return;
					}
			}
		}

		switch (rsrcPath) {
//				request.getRequestDispatcher("/").forward(request, response);
//				break;
//			case "login":
//				request.getRequestDispatcher("/login").forward(request, response);
//				break;
			case "r":
				request.getRequestDispatcher("/").forward(request, response);
				break;
			case "p":
				response.getWriter().write("p go");
				break;
//			case "login":

			default:
//				if (path.endsWith(".css")) {
//
//					request.getRequestDispatcher("/").forward(request, response);
//				}//
//				U.p(subdomain + ":" + path);
				try {

					chain.doFilter(request, response); // goes to default servlet.
				} catch (Exception ex) {
					boolean isEofException = ex.getClass().getSimpleName().equals("EofException");
					if (!isEofException) {
						throw ex;
					}
					L.warn("EOF:" + rsrcPath + ":" + ex.getMessage());
//					L.warn("EOF:" + rsrcPath + ":" + ex.getMessage(), ex);
				}

		}
		//https://dzone.com/articles/build-a-responsive-zk-web-app-with-fancy-urls
//		HttpServletRequest req = (HttpServletRequest) request;
//		String path = req.getRequestUri().substring(req.getContextpath().length());
//		if (!(path.equals("/")
//			  || path.startsWith("/img")
//			  || path.startsWith("/css")
//			  || path.startsWith("/js")
//			  || path.startsWith("/zkau")
//			  || path.startsWith("/zkwm")
//			  || path.startsWith("/index")
//			  || path.contains(".zul")
//			  || path.contains("html")
//			  || path.contains("j_spring_security_check"))) {
//
//			request.getRequestDispatcher("/").forward(request, response);
//
//		} else {
//			chain.doFilter(request, response); // goes to default servlet.
//		}


//		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
