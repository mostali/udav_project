package zk_page.core;

import mpu.core.ARG;
import mpc.fs.UF;
import mpc.fs.Ns;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.path.UPath;
import mpc.fs.UUrl;
import mpu.str.TKN;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import zk_os.AppZosConfig;
import udav_net.apis.zznote.ItemPath;
import zk_os.core.Sdn;

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
				", rootDomain='" + isEmptySd3() + '\'' +
				", rootPagename='" + isEmptyPagename() + '\'' +
				'}';
	}

	private final String subdomain3, servlet_path, pagename;

	//
	//
	//
	public boolean isRootPage() {
		return isEmptyPagename() && isEmptySd3();
	}

	public boolean isEmptySd3() {
		return subdomain30().isEmpty();
	}

	public boolean isEmptyPagename() {
		return pagename0().isEmpty();
	}

	public String subdomain30() {
		return subdomain3;
	}

	public String subdomain3Rq() {
		return isEmptySd3() ? ItemPath.SD3_INDEX_ALIAS : subdomain30();
	}

	public String pagename0() {
		return pagename;
	}

	public String pagenameRq() {
		return isEmptyPagename() ? ItemPath.PAGE_INDEX_ALIAS : pagename0();
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


	public String pathStr(int index, String... defRq) {
		Path item = UPath.item(Paths.get(servlet_path), index, null);
		return item != null ? item.toString() : (ARG.toDefThrow(() -> new RequiredRuntimeException("Except path item by index"), defRq));
	}

	public Path path(int index, Path... defRq) {
		return UPath.item(Paths.get(servlet_path), index, defRq);
	}

	public PagePathInfo(HttpServletRequest servletRequest, Integer fromReferer) {
		this(AppZosConfig.getSd3(servletRequest), UUrl.getPathFromUrl(servletRequest.getHeader("Referer")));
	}

	public PagePathInfo(HttpServletRequest servletRequest, Boolean forward) {
		this(AppZosConfig.getSd3(servletRequest), (String) servletRequest.getAttribute("javax.servlet.forward.servlet_path"));
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
		String pagename = pagename0();
		return path.equals(pagename) || UF.normFile(TKN.first(path, '?', path)).equals(pagename);
	}


	@Deprecated
	public Sdn sdnAny() {
		PagePathInfo ppi = SpVM.get().ppi();
		return Sdn.of(ppi.subdomain3Rq(), ppi.pagename0());
	}

	public Sdn sdnRq() {
		PagePathInfo ppi = SpVM.get().ppi();
		return Sdn.of(ppi.subdomain3Rq(), ppi.pagenameRq());
	}

	public Sdn sdn0() {
		PagePathInfo ppi = SpVM.get().ppi();
		return Sdn.of(ppi.subdomain30(), ppi.pagename0());
	}

	public String pathWoQuery() {
		return UUrl.getUrlWoQuery(path());
	}
}
