package mpc.fs;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpu.core.EQ;
import mpe.core.P;
import mpc.exception.RequiredRuntimeException;
import mpu.str.USToken;
import mpu.Sys;

import java.net.URL;

public class UUrl {

	//
	// https://en.wikipedia.org/wiki/Percent-encoding#Types_of_URI_characters
	// Allowed symbol's
	// !	#	$	&	'	(	)	*	+	,	/	:	;	=	?	@	[	]
	//
	// TODO
	public static void main(String[] args) {
		String url = "https://job-jira.otr.ru/browse/BU-18004?focusedCommentId=12137758&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel";
		UrlInfo urlInfo = UrlInfo.of(url);
		P.exit(urlInfo.pagenameLast() + ":" + urlInfo.queryUrl().getFirstAs("focusedCommentId", Integer.class, null));
//		String[] urlWoQuery = getHostAndPath(url);
//		String getPathSecondFromUrlPath = getPathFromUrl(url);
		P.exit(urlInfo.pagenmameFirst());
		P.exit(urlInfo.pagenameLast());
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

	public static final String PFX_HTTP = "http://";
	public static final String PFX_HTTPS = "https://";
	private static final String SEP = "/";

	public static String normUrlParts(String... url) {
		switch (url.length) {
			case 0:
				throw new IllegalArgumentException("empty url parts");
			case 1:
				return url[1];
			default:
				StringBuilder urlSb = new StringBuilder(normDirEnd(url[1]));
				for (int i = 0; i < url.length; i++) {
					urlSb.append(normFileStart(url[i]));
				}
				return urlSb.toString();
		}
	}

	//	public static String getPathAndQuery(String url) {
//		int li = url.indexOf('?');
//		if (li == -1) {
//
//		}else{}
//		return USToken.two(url, "?");
//	}

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

	public static String getPathSecondFromUrlPath(String path, String... defRq) {
		String first = getPathFirstItemFromUrlPath(path, null);
		String last = UUrl.normFileStart(path).substring(first.length());
		last = UUrl.normFileStart(last);
		return last;
	}

	/**
	 * /path1 -> path1
	 * /path1/ -> path1
	 * path1/ -> path1
	 * /path1/path2 -> path1
	 */

	public static String getPathLastItemFromUrlPath(String path) {
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

	public static String[] getHostAndPath(String url, String[]... defRq) {
		int found = -2;
		if (url != null) {
			for (int i = 0; i < url.length(); i++) {
				char c = url.charAt(i);
				switch (c) {
					case '/':
						if (found > 0) {
							return USToken.two(url, i);
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
	public static String normDirEnd(String url) {
		return UF.isSlashLast(SEP) ? url : url + SEP;
	}

	public static String normFileStart(String url) {
		while (url.startsWith(SEP)) {
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
			Sys.p("ok:getPathFromUrl");
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
			Sys.p("ok:getPathFromUrl");
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
			Sys.p("ok:getPathFirstItemFromUrlPath");
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
			Sys.p("ok:getPathFromUrl");
		}
	}

}
