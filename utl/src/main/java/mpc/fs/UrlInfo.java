package mpc.fs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.query.QueryUrl;
import mpu.IT;
import mpu.core.ARG;

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
//		try {
//			UrlInfo urlInfo = ;
//			urlInfo.throwIsNotWhole();
//			return urlInfo;
//		} catch (Exception ex) {
//			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Illegal url '%s'", url), defRq);
//		}
	}

//	@Getter
//	private Exception lastError;
//
//	public boolean throwIsNotWhole(boolean... RETURN) {
//		try {
//			IT.notEmpty(url);
//			IT.notEmpty(hostAndPath);
//			return true;
//		} catch (Exception ex) {
//			lastError = ex;
//			if (ARG.isDefEqTrue(RETURN)) {
//				return false;
//			}
//			throw ex;
//		}
//	}

	public String path() {
		return path != null ? path : (path = UUrl.getPathFromUrl(url));
	}

	public String pathWoQuery() {
		return pathWoQuery != null ? pathWoQuery : (pathWoQuery = UUrl.getUrlWoQuery(path()));
	}

	public String urlWoQuery() {
		return urlWoQuery != null ? urlWoQuery : (urlWoQuery = UUrl.getUrlWoQuery(url));
	}

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
