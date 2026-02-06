package zk_page.core;

import mpu.core.ARG;
import mpc.fs.UF;
import mpc.fs.Ns;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.path.UPath;
import mpc.url.UUrl;
import mpu.str.TKN;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import mpe.call_msg.core.NodeID;
import zk_os.core.Sdn;
import zk_page.ZKR;

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
				"sd3='" + plane + '\'' +
				", pagename='" + pagename + '\'' +
				", path='" + servlet_path + '\'' +
				", rootDomain='" + isEmptySd3() + '\'' +
				", rootPagename='" + isEmptyPagename() + '\'' +
				'}';
	}

	private final String plane, servlet_path, pagename, itemname;

	//
	//
	//
	public boolean isRootPage() {
		return isEmptyPagename() && isEmptySd3();
	}

	public boolean isEmptySd3() {
		return plane().isEmpty();
	}

	public boolean isEmptyPagename() {
		return pagename0().isEmpty();
	}

	public String plane() {
		return plane;
	}

	public String item() {
		return itemname;
	}

	public String planeRq() {
		return isEmptySd3() ? NodeID.PLANE_INDEX_ALIAS : plane();
	}


	public String pagenameRq() {
		return isEmptyPagename() ? NodeID.PAGE_INDEX_ALIAS : pagename0();
	}

	public String pagename0() {
		return pagename;
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
		this(ZKR.getPlaneFromRequest(servletRequest), UUrl.getPathFromUrl(servletRequest.getHeader("Referer")));
	}

	public PagePathInfo(HttpServletRequest servletRequest, Boolean forward) {
		this(ZKR.getPlaneFromRequest(servletRequest), (String) servletRequest.getAttribute("javax.servlet.forward.servlet_path"));
		//			javax.servlet.forward.query_string
		//			javax.servlet.forward.request_uri
		//			javax.servlet.forward.context_path
	}

	public PagePathInfo(String sd3, String servlet_path) {
		this.plane = sd3;
		this.servlet_path = servlet_path.startsWith("/") ? servlet_path.substring(1) : servlet_path;
		this.pagename = this.servlet_path.isEmpty() ? "" : UUrl.getPathFirstItemFromUrlPath(this.servlet_path);

		Path path1 = path(1, null);
		this.itemname = path1 == null ? null : path1.toString();
	}

	public boolean isEqPagenameAndPath() {
		String path = path();
		String pagename = pagename0();
		return path.equals(pagename) || UF.normFile(TKN.first(path, '?', path)).equals(pagename);
	}


	@Deprecated
	public Sdn sdnAny() {
		PagePathInfo ppi = SpVM.get().ppi();
		return Sdn.of(ppi.planeRq(), ppi.pagename0());
	}

	public Sdn sdn() {
		PagePathInfo ppi = SpVM.get().ppi();
		return Sdn.of(ppi.planeRq(), ppi.pagenameRq());
	}

	public Sdn sdnUnsafe() {
		PagePathInfo ppi = SpVM.get().ppi();
		return Sdn.of(ppi.plane(), ppi.pagename0());
	}

	public String pathWoQuery() {
		return UUrl.getUrlWoQuery(path());
	}
}
