//package nett;
//
//
//import org.apache.http.util.Args;
//import org.jsoup.select.Elements;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import rux.vk.UJsoup;
//import rux.vk.UNet;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class ParserProxy {
//	public static final Logger L = LoggerFactory.getLogger(ParserProxy.class);
//	private static final String treeName = "ps5_" + ParserProxy.class.getSimpleName();
//	private static final String tempPageSrc = "tempSrcps5";
//
//	// TODO
//	public static void main(String[] args) {
////		System.setProperty("http.proxyHost", "http://149.56.27.45");
////		System.setProperty("http.proxyPort", "1080");
////
////		UT.hasConnection();
////		U.exit();
////		U.exit(UNet.hasConnectionProxySock5("149.56.27.45:1080",
////				TEST_CONNECTION, 9));
//
//
//		String proxy;
//		try {
//
//			proxy = getFreeProxy();
//			// TBot
//			U.p("Find next worked proxy ::: " + proxy);
//
//		} catch (TgException e) {
//			e.printStackTrace();
//		}
//	}
//
//	// TODO
//	static String getFreeProxy() throws TgException {
//		int tc = 3;
//		do {
//			List<String> proxys0 = UTree.treeKeysValues(treeName);
//			if (proxys0.isEmpty()) {
//				SeleniumParser_SpysOne.startParse();
//				proxys0 = UTree.treeKeysValues(treeName);
//			}
//			Args.notEmpty(proxys0, "proxys0");
//			L.trace("Found all free proxys :" + proxys0);
//			try {
//				return getWorkedProxy(proxys0);
//			} catch (TgException e) {
//				if (e.is(TgException.EError.PROXYEND)) {
//					L.warn("All free proxys, try reparse :" + proxys0);
//					SeleniumParser_SpysOne.startParse();
//					continue;
//				}
//			}
//		} while (tc-- >= 0);
//		throw TgException.EError.PROXYENDLONG.I();
//	}
//
//	private final static String STATEINIT = "0";
//	private final static String STATEWORK = "1";
//
//	private final static String TEST_CONNECTION = "https://telegram.org";
//
//	private static String getWorkedProxy(List<String> proxys0)
//			throws TgException {
//		Args.notEmpty(proxys0, "proxys0");
//		List<String> worked = UTree.tree(treeName).getModels().stream().filter(e -> {
//			return STATEWORK.equalsIgnoreCase(e.getValue());
//		}).map(e -> e.getKey()).collect(Collectors.toList());
//		if (!worked.isEmpty()) {
//			return worked.get(0);
//		} else {
//			L.info("Worked proxy's is empty, start search");
//		}
//		do {
//			worked = UTree.tree(treeName).getModels().stream().filter(e -> {
//				return !STATEWORK.equalsIgnoreCase(e.getValue());
//			}).map(e -> e.getKey()).collect(Collectors.toList());
//			if (worked.isEmpty()) {
//				throw TgException.EError.PROXYEND.I();
//			}
//			String nextIp = worked.get(0);
//			L.info("Try check next IP ::: " + nextIp);
//			String st = reinitStatusConnection(nextIp);
//			if (STATEWORK.equals(st)) {
//				return worked.get(0);
//			} else {
//				continue;
//			}
//		} while (true);
//	}
//
//
//	static void reinitStatusConnectionQuickly(String proxy, String quicklyStatus) {
//		UTree.treeSetValue(treeName, proxy, quicklyStatus);
//	}
//
//	static String reinitStatusConnection(String proxy) {
//		if (UNet.hasConnectionProxySock5(proxy, TEST_CONNECTION, 9)) {
//			UTree.treeSetValue(treeName, proxy, STATEWORK);
//			return STATEWORK;
//		} else {
//			UTree.treeRemoveValue(treeName, proxy);
//			return null;
//		}
//	}
//
//	public static void removeProxy(String proxy) {
//		UTree.treeRemoveValue(treeName, proxy);
//	}
//
//	static class SeleniumParser_SpysOne {
//		private static void startParse() {
//			String value = UTree.getH(tempPageSrc);
//			if (UQ.isEmpty(value)) {
//				parseMainPageSrc();
//			}
//			value = UTree.getH(tempPageSrc);
//			Args.notNull(value, tempPageSrc);
//			Elements es = UJsoup.select(value, "table table tr td font.spy14");
//			Collections.shuffle(es);
//			es.stream().map(el -> {
//				String ip = US.removeSpaces(el.text());
//				return PatternsUtils.getIpAddressWithPort(ip);
//			}).filter(el -> el != null).forEach(ip -> {
//				UTree.treeSetValue(treeName, ip, STATEINIT);
//			});
//			UTree.removeKeyH(tempPageSrc);
//		}
//
//		private static void parseMainPageSrc() {
//			throw new IllegalStateException("need impl");
////			ChromeDriver fd = new ChromeDriver();
////			fd.get("http://spys.one/socks/");
////			String pagesrc = fd.getPageSource();
////			UTree.setH(tempPageSrc, pagesrc);
////			fd.quit();
//		}
//	}
//}
