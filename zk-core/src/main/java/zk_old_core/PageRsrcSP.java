//package zk_core.pages;
//
//import lombok.SneakyThrows;
//import mpc.log.Log2Html;
//import mpe.logs.filter.LogLinesProcessor;
//import org.zkoss.zul.Html;
//import org.zkoss.zul.Window;
//import net.query.QueryUrl;
//import zk_page.core.PageSP;
//import zk_page.core.SpVM;
//import zk_os.sd.core.SdRsrc;
//import zk_os.srv.ZK;
//
///**
// * @author dav 07.01.2022   19:24
// */
//public class PageRsrcSP extends PageSP {
//
//	final SdRsrc.LocRsrc locRsrc;
//
//	public PageRsrcSP(Window window, SpVM spVM, SdRsrc.LocRsrc locRsrc) {
//		super(window, spVM);
//		this.locRsrc = locRsrc;
//	}
//
//	@SneakyThrows
//	public void buildPage() {
//
////				TextboxFile tbf = new TextboxFile(Paths.get("./logs/server.log"));
////				tbf.setEnableWrite(false);
////				tbf.setWidth("100%");
//
//		QueryUrl queryUrl = ZK.getRequestQuery();
//		LogLinesProcessor lp = new LogLinesProcessor();
//		lp.addLineCondition_ByRegex(".*127.*");
////		if (X.notEmpty(queryUrl.getMap())) {
////			Map<String, Object> modelView = new HashMap<>();
////			throw new URest.VelocityContentResponseException("rest/log/index-logs-react.vtl.html", modelView);
////		}
//		String html = Log2Html.fromFile("./logs/server.log", -1000);
//		Html cLog = new Html(html);
//		cLog.setWidth("100%");
//
//		window.appendChild(cLog);
//
//	}
//
//}
