package zk_os;

import mpc.exception.NI;
import mpc.fs.Ns;
import mpc.fs.query.QueryUrl;
import mpc.net.CON;
import mpu.X;
import mpc.env.boot.BootRunUtils;
import mpc.fs.UUrl;
import mpf.ns.space.core.SpaceHomeMap;
import mpf.ns.space.Src;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import utl_rest.URest;
import utl_web.ContentType;
import utl_web.UWeb;
import zk_os.api.client.NoteApi;
import zk_os.core.ItemPath;
import zk_page.core.PagePathInfo;
import zk_page.core.RequestInfo;
import zk_old_core.AppRs;
import zk_old_core.sd.core.SdRsrc;
import zk_os.sec.Sec;
import zk_page.node.NodeDir;
import zk_page.node.fsman.NodeFileTransferMan;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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
		//		String subdomain = UWeb.getSubDomian_part3(httpRequest);

//		if (rsrcPath.equals("notes/*/ivideo")) {
//			Path file = Paths.get("/home/dav/.data/bea/.sd3/.index/notes/.forms/ivideo/chmok.MP4");
//			UWeb.responseContentTypeFromFile((HttpServletResponse) response, ContentType.VIDEO_MP4, file.toFile());
//			return;
//		}
		Authentication auth = Sec.getAuth(null);
		RequestInfo requestInfo = new RequestInfo(httpRequest);

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

			ContentType contentType = ContentType.ofExt(ext);
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

			Path parent = SdRsrc.getParentLocation(rsrcPath, ppiRef);
			if (parent == null) {
				//it not rsrc from fs, that from resources?
				if (rsrcPath.startsWith("_com/")) {
					UWeb.responseContentTypeFromRsrc((HttpServletResponse) response, contentType, ".", rsrcPath);
				} else {
					UWeb.responseContentTypeFromRsrc((HttpServletResponse) response, contentType, "/web", rsrcPath);
				}
			} else {
				String filenameWithoutFirstItem = rsrcPath.substring(rsrcPath.indexOf('/') + 1);
				File rsrcFile = parent.resolve(filenameWithoutFirstItem).toFile();
				UWeb.responseContentTypeFromFile((HttpServletResponse) response, contentType, rsrcFile);
			}
			return;

		}


		String servletPath = ((HttpServletRequest) request).getServletPath();
		if (servletPath.startsWith("/_/")) {
			String sd3 = AppZosWeb.getSd3(httpRequest);
			if ("rs".equals(sd3)) {
				String path = servletPath.substring(3);
				if (true) {
					throw NI.stop("deprecated");
				}
				Path spaceChild = null;
//				Path spaceChild = Env.SPACE.resolve(path);
				Src spaceSrc = new Src(Ns.ofSafeChild(spaceChild));
				SpaceHomeMap homeMap = spaceSrc.homeMap(null);
				if (homeMap != null) {
					Boolean downloadable = homeMap.getAsBoolean("down", false);
					if (downloadable) {
						URest.sendOk_JSON_OR(spaceSrc.fCat());
						return;
					}
				}
//				response.resetBuffer();
				URest.throwStatus404("not found:" + spaceSrc.fName());
			}
		}

//		U.nothing();
		if (false) {
			__SKIP:
			if (ppiRef != null && "rs".equals(ppiRef.subdomain3())) {
				String pagename = ppiRef.pagename();
				if (X.empty(pagename)) {
					break __SKIP;
				}
				Path path = ppiRef.path(1, null);
				String rs_name1 = path == null ? null : path.toString();
				if ("_".equals(rs_name1)) {
					break __SKIP;
				}
				Path path2 = ppiRef.path(2, null);
				String rs_name2 = path2 == null ? null : path2.toString();
				if (X.empty(rs_name2)) {
					break __SKIP;
				}

				Ns rs_ns = AppRs.rs_ns(pagename, rs_name2);

				String cnt = rs_ns.fCat(null);
				if (cnt == null) {
//				URest.send404("rs not found");
					break __SKIP;
				}
				URest.sendOk_JSON_OR(cnt);
				return;
			}
		}

		switch (rsrcPath) {
			case "zkau":
				break;
			case "__v__":
				response.getWriter().write(BootRunUtils.getVersionFromBuildInfo());
				return;
			default:
				//nothing

		}

		CON.Method method = CON.Method.valueOf(((HttpServletRequest) request).getMethod());
		if (method == CON.Method.DELETE) {

			String callPath = servletPath;
			if (servletPath.startsWith("/" + NoteApi.UP_API_PARTURL + "/")) {
				callPath = servletPath.substring(NoteApi.UP_API_PARTURL.length() + 2);
			}

			String queryString = ((HttpServletRequest) request).getQueryString();
			QueryUrl qUrl = QueryUrl.of(queryString);

			String firstAsStr = qUrl.getFirstAsStr(Sec.SKA, null);

			if (!AppZosConfig.SUPER_KEY.equals(firstAsStr)) {
				//out - will be error for DELETE method
			} else {
				String sd3 = AppZosWeb.getSd3(httpRequest);
				Path itemPath0 = Paths.get(X.empty(sd3) ? AFCC.SD3_INDEX_ALIAS : sd3).resolve(callPath);
				ItemPath itemPath = ItemPath.of(itemPath0).throwIsNotWhole();
				NodeDir nodeItem = NodeDir.ofNodeName(itemPath.name(), Pare.of(sd3, itemPath.page()));
				boolean statusDeleted = false;
				if (nodeItem.fExist()) {
					NodeFileTransferMan.deleteItem(nodeItem);
					statusDeleted = true;
					String file;
					L.info("Delete '" + itemPath + "' from '" + nodeItem.state().pathFc() + "'");
				} else {
					L.info("Note not exist '" + itemPath + "'");
				}
				UWeb.sendResponseAndClose((HttpServletResponse) response, 200, "Delete " + itemPath.name() + " item " + (statusDeleted ? " successfully" : "NO"));
				return;
			}


		}

		switch (rsrcPath) {
//				request.getRequestDispatcher("/").forward(request, response);
//				break;
//			case "login":
//				request.getRequestDispatcher("/login").forward(request, response);
//				break;
			case "p":
				request.getRequestDispatcher("/").forward(request, response);
				break;
			case "r":
				response.getWriter().write("go-go");
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
					if (!ex.getClass().getSimpleName().equals("EofException")) {
						throw ex;
					}
					L.warn("EOF:" + rsrcPath + ":" + ex.getMessage());
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
