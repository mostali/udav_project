//package zk_form.control.breadcrumbs.qview;
//
//import org.zkoss.zk.ui.util.Clients;
//import zk_page.ZKR;
//import zk_page.index.RSPath;
//
//public class QBreadMasterLn extends QBreadLn {
//
//	public QBreadMasterLn(QBreadPos level, String sym, boolean homeRestart) {
//		super(sym, level);
//		onCLICK((e) -> ZKR.redirectToHome());
//	}
//
//	public QBreadMasterLn(QBreadPos level, String sym) {
//		super(sym, level);
//		onCLICK((e) -> {
//			String javaScript = "window.location.href = \"" + RSPath.ROOT.toRootLink() + "\";\n";
//			Clients.evalJavaScript(javaScript);
//		});
//	}
//
//	@Override
//	protected void init() {
//		super.init();
//		fixed();
//	}
//}
