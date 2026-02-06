package zk_page.core;

import mpc.net.query.QueryUrl;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import utl_web.UWeb;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PagePathInfoWithQuery extends PagePathInfo {

	public static PagePathInfoWithQuery current() {
		Execution execution = Executions.getCurrent();
		HttpServletRequest servletRequest = (HttpServletRequest) execution.getNativeRequest();
		if (!execution.isForwarded()) {
			throw new IllegalStateException("Except forward request");
		}
		return new PagePathInfoWithQuery(servletRequest, (Boolean) null);
	}

	@Override
	public String path() {
		return super.path() + (query == null ? "" : "?" + query);
	}

	public Path pathAs() {
		return Paths.get(path());
	}

	@Override
	public String toString() {
		return "PagePathInfoWithQuery{" +
				"ppi='" + super.toString() + '\'' +
				", query='" + query + '\'' +
				'}';
	}

	private final String query;

	public String query() {
		return query;
	}

	private transient QueryUrl queryUrl;

	public QueryUrl queryUrl() {
		return queryUrl == null ? (queryUrl = QueryUrl.of(query)) : queryUrl;
	}

	public PagePathInfoWithQuery(HttpServletRequest servletRequest, Integer fromReferer) {
		super(servletRequest, (Integer) null);
		query = UWeb.getQueryString(servletRequest);
	}

	public PagePathInfoWithQuery(HttpServletRequest servletRequest, Boolean forward) {
		super(servletRequest, (Boolean) null);
		this.query = UWeb.getQueryString(servletRequest);
	}

}
