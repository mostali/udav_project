package mpc.fs;

import lombok.RequiredArgsConstructor;
import mpc.fs.query.QueryUrl;

import java.util.Arrays;

@RequiredArgsConstructor
public class UrlInfo {
	public final String url;
	private String path, pathWoQuery, urlWoQuery;
	private String pagenameFirst, pagenameLast;
	private String[] hostAndPath;
	private QueryUrl queryUrl;

	public static UrlInfo of(String url) {
		return new UrlInfo(url);
	}

	public String path() {
		return path != null ? path : (path = UUrl.getPathFromUrl(url));
	}

	public String pathWoQuery() {
		return pathWoQuery != null ? pathWoQuery : (pathWoQuery = UUrl.getUrlWoQuery(path()));
	}

	public String urlWoQuery() {
		return urlWoQuery != null ? urlWoQuery : (urlWoQuery = UUrl.getUrlWoQuery(url));
	}

//		public String query() {
//			return urlWoQuery != null ? urlWoQuery : (urlWoQuery = getUrlWoQuery(url));
//		}

	public String[] hostAndPath() {
		return hostAndPath != null ? hostAndPath : (hostAndPath = UUrl.getHostAndPath(url));
	}

	public String pagenmameFirst() {
		return pagenameFirst != null ? pagenameFirst : (pagenameFirst = UUrl.getPathFirstItemFromUrlPath(path()));
	}

	public String pagenameLast() {
		return pagenameLast != null ? pagenameLast : (pagenameLast = UUrl.getPathLastItemFromUrlPath(pathWoQuery()));
	}

	@Override
	public String toString() {
		return "UrlInfo{" +
				"url='" + url + '\'' +
				",\n  path='" + path() + '\'' +
				",\n  pathWoQuery='" + pathWoQuery() + '\'' +
				",\n  urlWoQuery='" + urlWoQuery() + '\'' +
				",\n  hostAndPath=" + Arrays.toString(hostAndPath()) +
				",\n  pagenmameFirst=" + pagenmameFirst() +
				",\n  pagenmameLast=" + pagenameLast() +
				'}';
	}

	public QueryUrl queryUrl() {
		return queryUrl != null ? queryUrl : (queryUrl = QueryUrl.ofUrl(url));
	}
}
