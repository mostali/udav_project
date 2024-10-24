package zk_os;

import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpc.fs.Ns;
import mpc.fs.query.QueryUrl;
import mpc.net.CON;
import mpu.IT;
import mpu.X;
import mpc.env.boot.BootRunUtils;
import mpc.fs.UUrl;
import mpf.ns.space.core.SpaceHomeMap;
import mpf.ns.space.Src;
import mpu.pare.Pare;
import mpu.str.USToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import utl_rest.URest;
import mpe.http.ContentType;
import utl_web.URsp;
import utl_web.UWeb;
import zk_notes.apiv1.NodeApiChars;
import zk_notes.apiv1.client.NoteApi;
import zk_os.core.ItemPath;
import zk_page.core.PagePathInfo;
import zk_page.core.RequestInfo;
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
					URsp.sendResponseContentType_FromRsrc((HttpServletResponse) response, contentType, ".", rsrcPath);
				} else {
					URsp.sendResponseContentType_FromRsrc((HttpServletResponse) response, contentType, "/web", rsrcPath);
				}
			} else {
				String filenameWithoutFirstItem = rsrcPath.substring(rsrcPath.indexOf('/') + 1);
				File rsrcFile = parent.resolve(filenameWithoutFirstItem).toFile();
				URsp.sendResponseContentType_FromFile((HttpServletResponse) response, contentType, rsrcFile);
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

		switch (rsrcPath) {
			case "zkau":
				break;
			case "__v__":
				response.getWriter().write(BootRunUtils.getVersionFromBuildInfo());
				return;
			default:
				//nothing
		}


		if (extractApiPostDeleteRequest(request, (HttpServletResponse) response, servletPath, httpRequest)) {
			return;
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

	private static boolean extractApiPostDeleteRequest(ServletRequest request, HttpServletResponse response, String servletPath, HttpServletRequest httpRequest) throws IOException {
		CON.Method method = CON.Method.valueOf(((HttpServletRequest) request).getMethod());

		if (!servletPath.startsWith(NoteApi._UP_API_PARTURL_)) {
			return false;
		}

		String callPath = servletPath.substring(NoteApi.UP_API_PARTURL.length() + 2);

		if (method == CON.Method.POST || method == CON.Method.DELETE) {

			String queryString = ((HttpServletRequest) request).getQueryString();
			QueryUrl qUrl = QueryUrl.of(queryString);

			String firstAsStr = qUrl.getFirstAsStr(Sec.SKA, null);

			if (!AppZosConfig.SUPER_KEY.equals(firstAsStr)) {
				//out - will be error for DELETE method
//				throw new FForbiddenException();
//				return false;
				URsp.sendResponseAndClose(response, 401, "no auth");
				return true;
			}

			String sd3 = AppZosWeb.getSd3(httpRequest);
			String sd3Part = AFC.wrapSd3(sd3);


			Path itemPath0;
			String ctrlOperSym;

			switch (method) {

				case POST: {

					boolean hasPage = !NodeApiChars.isCallPathStartWithUpDownPart(callPath);
					if (!hasPage) {
						callPath = AFCC.PAGE_INDEX_ALIAS + "/" + callPath;
					}
					String[] pageArgs = USToken.two(callPath, "/");

					String pagenamePart = IT.NE(pageArgs[0]);

					String[] two = USToken.two(pageArgs[1], "/");

					ctrlOperSym = two[0];
					IT.state(NodeApiChars.isCallPathStartWithUpDown(ctrlOperSym), "illegal %s", ctrlOperSym);

					String itemPart = IT.NE(two)[1];

					itemPath0 = Paths.get(sd3Part).resolve(pagenamePart).resolve(itemPart);

					break;
				}
				case DELETE: {
					itemPath0 = Paths.get(sd3Part).resolve(callPath);
					ctrlOperSym = null;
					break;
				}
				default:
					return false;
			}

			ItemPath itemPath = ItemPath.of(itemPath0).throwIsNotWhole();


			NodeDir nodeItem = NodeDir.ofNodeName(itemPath.name(), Pare.of(sd3, itemPath.page()));

			switch (method) {
				case DELETE: {
					boolean statusDeleted = false;
					if (nodeItem.fExist()) {
						NodeFileTransferMan.deleteItem(nodeItem);
						statusDeleted = true;
						L.info("Delete '" + itemPath + "' from '" + nodeItem.state().pathFc() + "'");
					} else {
						L.info("Note not exist '" + itemPath + "'");
					}
					URsp.sendResponseAndClose(response, 200, "Delete " + itemPath.name() + " item " + (statusDeleted ? " successfully" : "NO"));
					return true;
				}
				case POST: {

					String rspData;

					switch (ctrlOperSym) {
						case NodeApiChars.UP: {
							if (nodeItem.fExist()) {
								rspData = nodeItem.state().readFcData();
							} else {
								URsp.sendResponseAndClose(response, 404, X.f(NoteApi.MSG_404_ITEM_NOTE_FOUND, nodeItem.nodeName()));
								return true;
							}
							break;
						}
						case NodeApiChars.DOWN: {
							String body = X.toStringFrom(request.getInputStream(), null);
							if (X.empty(body)) {
								body = qUrl.getFirstAsStr("v", null);
								if (body == null) {
									URsp.sendResponseAndClose(response, 400, NoteApi.MSG_400_SET_BODY);
									return true;
								}
							}
							nodeItem.state().writeFcData(body);
							rspData = X.f(NoteApi.MSG_200_ITEM_ADDED, itemPath.name());
							break;
						}
						default:
							throw new WhatIsTypeException("What is pattern for POST call? " + ctrlOperSym);
					}


					URsp.sendResponseAndClose(response, 200, rspData);

					return true;
				}

			}

		}
		return false;
	}

	@Override
	public void destroy() {

	}

}
