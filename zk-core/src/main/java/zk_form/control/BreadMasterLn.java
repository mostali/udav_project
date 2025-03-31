package zk_form.control;

import org.zkoss.zk.ui.util.Clients;
import zk_page.ZKR;
import zk_page.index.RSPath;

public class BreadMasterLn extends BreadLn {

	public BreadMasterLn(int level, String sym, boolean homeRestart) {
		super(sym, level);
		onCLICK((e) -> ZKR.redirectToPage("/"));
	}

	public BreadMasterLn(int level, String sym) {
		super(sym, level);
		onCLICK((e) -> {
			String javaScript = "window.location.href = \"" + RSPath.ROOT.toRootLink() + "\";\n";
			Clients.evalJavaScript(javaScript);
		});
	}

}
