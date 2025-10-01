package mpc.fs;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpu.core.EQ;
import mpc.exception.RequiredRuntimeException;
import mpu.str.STR;
import mpu.str.TKN;
import mpu.Sys;
import mpu.str.USToken0;

import java.net.URL;

//Safe characters	Alphanumeric [0-9a-zA-Z], special characters $-_.+!*'() >>>	NO ENCODING
//Reserved characters	; / ? : @ = &	ENCODING*
//Unsafe characters	Includes the blank/empty space and " < > # % { } | \ ^ ~ [ ] `	ENCODING
public class UUrl {

	//
	// https://en.wikipedia.org/wiki/Percent-encoding#Types_of_URI_characters
	// Allowed symbol's
	// !	#	$	&	'	(	)	*	+	,	/	:	;	=	?	@	[	]
	//
	// TODO
	public static void main(String[] args) {
//		UrlInfo urlInfo = UrlInfo.of(url);
//		P.exit(urlInfo.pagenameLast() + ":" + urlInfo.queryUrl().getFirstAs("focusedCommentId", Integer.class, null));
//		String[] urlWoQuery = getHostAndPath(url);
//		String getPathSecondFromUrlPath = getPathFromUrl(url);
//		P.exit(urlInfo.pagenmameFirst());
//		P.exit(urlInfo.pagenameLast());
//
//			P.pnice(UUrl.getHostAndPath("htpp://asd.asd"));
//			P.pnice(UUrl.getHostAndPath("htpp://asd.asd/"));
//			P.pnice(UUrl.getHostAndPath("htpp://asd.asd/a"));
//			P.pnice(UUrl.getHostAndPath("asd.asd/a"));
//			P.pnice(UUrl.getHostAndPath("asd.asd/"));
//			P.exit(UUrl.getHostAndPath("asd.asd"));
//
//			P.p(UUrl.getPathFirstItemFromUrlPath("/sa?"));
//			P.p(UUrl.getPathFirstItemFromUrlPath("/s/"));
//			P.p(UUrl.getPathFromUrl("htpp://asd.asd"));
	}

	public static final String HTTP = "http://";
	public static final String HTTPS = "https://";
	private static final String PATH_SEP = "/";
	private static final String DOMAIN_SEP = ".";

	public static String normUrlParts(String... url) {
		switch (url.length) {
			case 0:
				throw new IllegalArgumentException("empty url parts");
			case 1:
				return url[1];
			default:
				StringBuilder urlSb = new StringBuilder(normPartEnd(url[1]));
				for (int i = 0; i < url.length; i++) {
					urlSb.append(normFileStart(url[i]));
				}
				return urlSb.toString();
		}
	}

	public static String getUrlWoQuery(String url) {
		int li = url.indexOf('?');
		return li == -1 ? url : url.substring(0, li);
	}

	public static String getQueryString(String url) {
		int ind = url.indexOf('?');
		if (ind == -1) {
			return null;
		}
		return url.substring(ind + 1);
	}

	public static String normUrl(String url, boolean... appendLastSlash) {
		url = UF.normFileEndRk(url);
		return ARG.isDefEqTrue(appendLastSlash) ? url + UF.ROOT_DIR_UNIX : url;
	}

	public static String normUrl(String url, String part, boolean... appendLastSlash) {
		url = UF.normFileEndRk(url);
		part = UF.normFileStartRk(part);
		String u = url + UF.ROOT_DIR_UNIX + part;
		return ARG.isDefEqTrue(appendLastSlash) ? u + UF.ROOT_DIR_UNIX : u;
	}

	public static String getPathFirstItemFromUrl(String url, String... defRq) {
		String pathFromUrl = getPathFromUrl(url);
		if (pathFromUrl.isEmpty()) {
			return pathFromUrl;
		}
		return getPathFirstItemFromUrlPath(pathFromUrl, defRq);
	}

	public static String getExtFromUrlPath(String path, String... defRq) {
		if (path.length() > 1) {
			int indPoint = -1;
			out:
			for (int i = 0; i < path.length(); i++) {
				char c = path.charAt(i);
				switch (c) {
					case '.':
						indPoint = i;
						continue;
					case '/':
						if (indPoint == -1) {
							continue;
						} else if (i == path.length() - 1 || path.charAt(i + 1) == '?') {
							return path.substring(indPoint + 1, i);
						}
						indPoint = -1;
						continue;
					case '?':
						if (indPoint > -1) {
							return path.substring(indPoint + 1, i);
						}
						break;
				}
			}
			if (indPoint > -1) {
				return path.substring(indPoint + 1);
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Extension not found from url-path '%s'", path), defRq);
	}

	public static String getExtFromUrlPath_First(String path, String... defRq) {
		if (path.length() > 1) {
			int indPoint = -1;
			out:
			for (int i = 0; i < path.length(); i++) {
				char c = path.charAt(i);
				switch (c) {
					case '.':
						indPoint = i;
						continue;
					case '/':
						if (indPoint == -1) {
							continue;
						} else if (i < path.length()) {
							return path.substring(indPoint + 1, i);
						}
						continue;
					case '?':
						if (indPoint > -1) {
							return path.substring(indPoint + 1, i);
						}
						break;
				}
			}
			if (indPoint > -1) {
				return path.substring(indPoint + 1);
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Extension not found from url-path '%s'", path), defRq);
	}

	/**
	 * /path1 -> path1
	 * /path1/ -> path1
	 * path1/ -> path1
	 * /path1/path2 -> path1
	 */

	public static String getPathLastItemWoQuery(String path) {
		return getPathLastItemWithQuery(getUrlWoQuery(path));
	}

	public static String getPathLastItemWithQuery(String path) {
		int ind = path.lastIndexOf('/');
		if (ind == 0) {
			return path;
		}
		return ind == -1 ? path : path.substring(ind + 1);

	}

	public static String getPathFirstItemFromUrlPath(String path, String... defRq) {
		String org = path;
		while (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (!path.isEmpty()) {
			out:
			for (int i = 0; i < path.length(); i++) {
				char c = path.charAt(i);
				switch (c) {
					case '/':
						return path.substring(0, i);
					case '?':
						if (i == 0) {
							return "";
						}
						return path.substring(0, i);
				}
			}
			return path;
		}

		return ARG.toDefThrow(() -> new RequiredRuntimeException("First Path Item not found from url '%s'", org), defRq);
	}

	/**
	 * http://site.com ->
	 * http://site.com/ ->
	 * http://site.com/? -> ?
	 * http://site.com/path/ -> /path/
	 * http://site.com/path/? -> /path/?
	 */
	public static String getPathFromUrl(String url, String... defRq) {
		int found = -2;
		if (url != null) {
			for (int i = 0; i < url.length(); i++) {
				char c = url.charAt(i);
				switch (c) {
					case '/':
						if (found > 0) {
							return url.substring(i + 1);
						} else if (found == -2) {
							found = -1;
							continue;
						} else if (found == -1) {
							found = i;
							break;
						}
					default:
						if (found < 0) {
							found = -2;
						}
				}
			}
		}
		if (found > 0) {
			return "";
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Path not found from url '%s'", url), defRq);
	}

	public static String[] getHostAndPagename(String url, String[]... defRq) {
		String[] hostAndPath = getHostAndPath(url, null);
		if (hostAndPath != null) {
			String path = UF.normFileStart(hostAndPath[1]);
			hostAndPath[1] = USToken0.firstPath(path, path);
			return hostAndPath;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except url with any path '%s'", url), defRq);
	}

	public static String[] getHostAndPath(String url, String[]... defRq) {
		int found = -2;
		if (url != null) {
			for (int i = 0; i < url.length(); i++) {
				char c = url.charAt(i);
				switch (c) {
					case '/':
						if (found > 0) {
							return TKN.two(url, i);
						} else if (found == -2) {
							found = -1;
							continue;
						} else if (found == -1) {
							found = i;
							break;
						}
					default:
						if (found < 0) {
							found = -2;
						}
				}
			}
		}

		if (found > 0) {
			return new String[]{url, ""};
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Host not found from url '%s'", url), defRq);
	}

	//	public static String normDirEnd(String... part) {
//		return UF.isSlashLast(SEP) ? url : url + SEP;
//	}
	public static String normPartEnd(String url) {
		return UF.isSlashLast(url) ? url : url + PATH_SEP;
	}

	public static String normDomainEnd(String url) {
		return STR.endsWith(url, DOMAIN_SEP) ? url : url + DOMAIN_SEP;
	}

	public static String normPartClean(String file) {
		return UF.normFile(file);
	}

	public static String normPartStart(String url) {
		return UF.isSlashFirst(url) ? url : PATH_SEP + url;
	}

	public static String normUrlQueryEnd(String url) {
		return url.endsWith("&") ? url : url + "&";
	}

	public static String normFileStart(String url) {
		while (url.startsWith(PATH_SEP)) {
			url = url.substring(1);
		}
		return url;
	}

	@SneakyThrows
	public static String host(String url) {
		return new URL(url).getHost();
	}

	public static boolean hasPfxHttpOrHttps(String hostWithPath) {
		boolean http = !hostWithPath.startsWith("http");
		if (!http || hostWithPath.length() <= 12) {
			return false;
		}
		switch (hostWithPath.charAt(5)) {
			case 's':
				return hostWithPath.charAt(6) == ':' && hostWithPath.charAt(7) == '/' && hostWithPath.charAt(8) == '/';
			case ':':
				return hostWithPath.charAt(6) == '/' && hostWithPath.charAt(7) == '/';
			default:
				return false;
		}
	}

	public static String toUrl(String domain, String url_part) {
		return UF.normFileEnd(domain) + "/" + UF.normFileStart(url_part);
	}

	public static String toUrl(String domain, String url_part1, String url_part2) {
		return UF.normFileEnd(domain) + "/" + UF.normFile(url_part1) + "/" + UF.normFile(url_part2);
	}

	public static String addQueryParam(String url, String paramName, String value) {
		String queryString = UUrl.getQueryString(url);
		if (queryString == null) {
			return url + "?" + paramName + "=" + value;
		} else if (queryString.endsWith("&")) {
			return url + paramName + "=" + value;
		}
		return url + "&" + paramName + "=" + value;
	}

	public static String normHttpPfx(String url) {
		if (url.startsWith("http://")) {
			return url;
		} else if (url.startsWith("https://")) {
			return url.substring(8) + url;
		}
		return "http://" + normFileStart(url);
	}
	public static String normHttpsPfx(String url) {
		if (url.startsWith("http://")) {
			return url.substring(7) + url;
		} else if (url.startsWith("https://")) {
			return url;
		}
		return "https://" + normFileStart(url);
	}

	//
	//
	//

	public static class TestUrlPathExtension {
		public static void main(String[] args) {
			IT.state(EQ.eq(UUrl.getExtFromUrlPath("/logo.png", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath("/logo.png/", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath("/logo.png?", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath("/path.a/logo.png?", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath("/.png?", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath(".png?", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath(".png", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath("./", ""), ""));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath("./?", ""), ""));
			Sys.p("ok:TestUrlPathExtension");
		}
	}

	public static class TestUrlPathExtension_First {
		public static void main(String[] args) {
			IT.state(EQ.eq(UUrl.getExtFromUrlPath_First("/logo.png", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath_First("/logo.png/", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath_First("/logo.png?", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath_First("/path.ext0/logo.png?", ""), "ext0"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath_First("/.png?", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath_First(".png?", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath_First(".png", ""), "png"));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath_First("./", ""), ""));
			IT.state(EQ.eq(UUrl.getExtFromUrlPath_First("./?", ""), ""));
			Sys.p("ok:TestUrlPathExtension_First");
		}
	}

	public static class TestUrlPathFirstItem {
		public static void main(String[] args) {
			IT.state(UUrl.getPathFirstItemFromUrlPath("", null) == null);
			IT.state(UUrl.getPathFirstItemFromUrlPath("/", null) == null);
			IT.state(UUrl.getPathFirstItemFromUrlPath("//", null) == null);
			IT.state(UUrl.getPathFirstItemFromUrlPath("///", null) == null);
			IT.state(EQ.eq(UUrl.getPathFirstItemFromUrlPath("/sa?"), "sa"));
			IT.state(EQ.eq(UUrl.getPathFirstItemFromUrlPath("/sa"), "sa"));
			IT.state(EQ.eq(UUrl.getPathFirstItemFromUrlPath("/sa/"), "sa"));
			IT.state(EQ.eq(UUrl.getPathFirstItemFromUrlPath("sa/"), "sa"));
			IT.state(EQ.eq(UUrl.getPathFirstItemFromUrlPath("/sa/sa2"), "sa"));
			IT.state(EQ.eq(UUrl.getPathFirstItemFromUrlPath("sa"), "sa"));
			Sys.p("ok:TestUrlPathFirstItem");
		}
	}

	public static class TestUrlPath {
		public static void main(String[] args) {
			IT.state(EQ.eq(UUrl.getPathFromUrl("http://site.com"), ""));
			IT.state(EQ.eq(UUrl.getPathFromUrl("http://site.com/"), ""));
			IT.state(EQ.eq(UUrl.getPathFromUrl("http://site.com/?"), "?"));
			//UC.state(EQ.eq(UUrl.getPathFromUrl("http://site.com//?"), "/?"));
			IT.state(EQ.eq(UUrl.getPathFromUrl("http://site.com/sa"), "sa"));
			IT.state(EQ.eq(UUrl.getPathFromUrl("http://site.com/sa/"), "sa/"));
			IT.state(EQ.eq(UUrl.getPathFromUrl("http://site.com/sa/?"), "sa/?"));
			IT.state(EQ.eq(UUrl.getPathFromUrl("http://site.com/sa?"), "sa?"));
			Sys.p("ok:TestUrlPath");
		}
	}

	public static class TestUrlPathFn {
		public static void main(String[] args) {
			IT.state(EQ.eq(UUrl.getPathLastItemWoQuery("http://site.com/sa/"), ""));
			IT.state(EQ.eq(UUrl.getPathLastItemWithQuery("http://site.com/sa/?a=b&b=c"), "?a=b&b=c"));
			IT.state(EQ.eq(UUrl.getPathLastItemWoQuery("http://site.com/sa/file.item"), "file.item"));
			IT.state(EQ.eq(UUrl.getPathLastItemWoQuery("http://site.com/sa/file.item?a=b&b=c"), "file.item"));
			IT.state(EQ.eq(UUrl.getPathLastItemWithQuery("http://site.com/sa/file.item?a=b&b=c"), "file.item?a=b&b=c"));
			Sys.p("ok:TestUrlPathFn");
		}
	}

}
