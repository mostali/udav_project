package zk_page.core;

import mpu.X;
import mpu.core.ARG;
import mpc.fs.UF;
import mpc.fs.Ns;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.path.UPath;
import mpc.fs.UUrl;
import mpu.pare.Pare;
import mpu.str.USToken;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import zk_os.AFCC;
import zk_os.AppZosWeb;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PagePathInfo implements Serializable {

	private Ns ns;

	public static PagePathInfo current() {
		Execution execution = Executions.getCurrent();
		HttpServletRequest servletRequest = (HttpServletRequest) execution.getNativeRequest();
		if (!execution.isForwarded()) {
			throw new IllegalStateException("Except forward request");
		}
		return new PagePathInfo(servletRequest, (Boolean) null);
	}

	@Override
	public String toString() {
		return "PagePathInfo{" +
				"sd3='" + subdomain3 + '\'' +
				", pagename='" + pagename + '\'' +
				", path='" + servlet_path + '\'' +
				", rootDomain='" + isRootDomain() + '\'' +
				", rootPagename='" + isEmptyPagename() + '\'' +
				'}';
	}

	private final String subdomain3, servlet_path, pagename;

	public String subdomain3() {
		return subdomain3;
	}

	public String path() {
		return servlet_path;
	}


	public Ns ns(Ns... defRq) {
		if (ns != null) {
			return ns;
		}
		ns = Ns.findChild(Paths.get(path()), null);
		if (ns != null) {
			return ns;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("except some one, path '%s'", path()), defRq);
	}

	public String pagename() {
		return pagename;
	}

	public String path_str(int index, Path... defRq) {
		Path item = UPath.item(Paths.get(servlet_path), index, defRq);
		return item != null ? item.toString() : null;
	}

	public Path path(int index, Path... defRq) {
		return UPath.item(Paths.get(servlet_path), index, defRq);
	}

	public boolean isRootDomain() {
		return subdomain3.isEmpty();
	}

	public boolean isEmptyPagename() {
		return pagename().isEmpty();
	}

	public boolean isRootPlanePage() {
		return isEmptyPagename() && subdomain3().isEmpty();
	}

	public PagePathInfo(HttpServletRequest servletRequest, Integer fromReferer) {
		this(AppZosWeb.getSd3(servletRequest), UUrl.getPathFromUrl(servletRequest.getHeader("Referer")));
	}

	public PagePathInfo(HttpServletRequest servletRequest, Boolean forward) {
		this(AppZosWeb.getSd3(servletRequest), (String) servletRequest.getAttribute("javax.servlet.forward.servlet_path"));
		//			javax.servlet.forward.query_string
		//			javax.servlet.forward.request_uri
		//			javax.servlet.forward.context_path
	}

	public PagePathInfo(String sd3, String servlet_path) {
		this.subdomain3 = sd3;
		this.servlet_path = servlet_path.startsWith("/") ? servlet_path.substring(1) : servlet_path;
		this.pagename = this.servlet_path.isEmpty() ? "" : UUrl.getPathFirstItemFromUrlPath(this.servlet_path);
	}

	public boolean isEqPagenameAndPath() {
		String path = path();
		String pagename = pagename();
		return path.equals(pagename) || UF.normFile(USToken.first(path, '?', path)).equals(pagename);
	}

	public static Pare<String, String> toPareSd3AndPage(String sd3, String pagename) {
		return Pare.of(X.empty(sd3) ? AFCC.PAGE_INDEX_ALIAS : sd3, pagename);
	}

	public Pare<String, String> sdn() {
		PagePathInfo ppi = SpVM.get().ppi();
		String pagename = ppi.pagename();
		String sd3 = ppi.subdomain3();
		return toPareSd3AndPage(sd3, pagename);
	}
}
