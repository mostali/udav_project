package zk_pages;

import lombok.SneakyThrows;
import mpc.exception.FIllegalArgumentException;
import mpc.fs.fd.RES;
import mpc.net.query.QueryUrl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.sun_editor.IPerPage;
import zk_os.sec.ROLE;
import zk_page.*;
import zk_page.core.PagePathInfoWithQuery;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;

@PageRoute(pagename = "_manual", role = ROLE.ANONIM)
public class DemoManualPageSP extends PageSP implements IPerPage {

	public DemoManualPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {

		ZkPageInitHeads.initPageHeadLibs(window);

		QueryUrl queryUrl = ZKR.getRequestQuery();
		String part = queryUrl.getFirstAsStr("p", null);

		if (part != null) {
			switch (part) {
				case "markup":
					appendChild(Xml.ofRsrcXml("/etc/manual/markup.md"));
					return;

				default:
//					L.info("Not found page part {}", part);
					throw new FIllegalArgumentException(part);

			}

		}
		ZKS.BGCOLOR_WIN(window, "rgba(0,0,0,0.0)", "rgba(0,0,0,0.0)");

		Div0 separator = Div0.separator(130, ZKColor.GREEN.nextColor(), 1, "ZNote Manual\n", "class=\"head\"", "style=\"padding-top:50px\"");
		window.appendChild(separator);

		String cat = RES.ofRoot(DemoManualPageSP.class, "/etc/manual/manual.html").cat();

		appendChild(Xml.P(cat));

	}

	@Override
	public PagePathInfoWithQuery ppiq() {
		return super.ppiq();
	}

	public void appendChild(Component child) {
		window.appendChild(child);
	}
}
