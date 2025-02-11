package mpc.fs;

import lombok.RequiredArgsConstructor;
import mpc.net.query.QueryUrl;
import mpe.core.P;

import java.util.Arrays;

@RequiredArgsConstructor
public class UrlInfo {
	public final String url;
	private String path, pathWoQuery, urlWoQuery;
	private String pagenameFirst, pagenameLast;
	private String[] hostAndPath;
	private QueryUrl queryUrl;

	public static void main(String[] args) {
//		P.exit(of("http://a.d/asd/?asd&asd=1").pagenameLast());
//		P.exit(of("http://a.d/asd/name.asd?asd&asd=1").pagenameLast());
//		P.exit(UUrl.getPathLastItemWoQuery("http://a.d/asd/?asd&asd=1"));
		P.exit(UUrl.getPathLastItemWithQuery("http://a.d/asd/?asd&asd=1"));
		P.exit(UUrl.getPathLastItemWoQuery("http://a.d/asd/name.ext?asd&asd=1"));
		P.exit(UUrl.getPathLastItemWithQuery("http://a.d/asd/name.ext?asd&asd=1"));
		P.exit(UUrl.getExtFromUrlPath_First("http://a.d/asd/name.ext?asd&asd=1"));
		P.exit(UUrl.getExtFromUrlPath("http://a.d/asd/name.ext?asd&asd=1"));
		P.exit(UUrl.getHostAndPagename("http://a.d/asd/name.ext?asd&asd=1"));
		P.exit(UUrl.getPathLastItemWithQuery("http://a.d/asd/name.ext?asd&asd=1"));
//		P.exit(UF.getPathFileNameWithQuery("http://a.d/asd/name.ext?asd&asd=1"));
	}
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
		return pagenameLast != null ? pagenameLast : (pagenameLast = UUrl.getPathLastItemWithQuery(pathWoQuery()));
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
