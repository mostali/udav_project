package zklogapp;

import lombok.SneakyThrows;
import org.zkoss.zul.Window;
import zk_com.sun_editor.IPerPage;
import zk_os.sec.ROLE;
import zk_page.ZKS;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zklogapp.header.BottomHistoryPanel;
import zklogapp.header.LogPageHeader;
import zklogapp.otr.srvloader.StandsControlPanel;

@PageRoute(pagename = "log", role = ROLE.USER)
public class LogPageSP extends PageSP implements IPerPage {//, WithLogo

	private AppLogSettingsPanel pageHeader = null;

	public LogPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {

		ZKS.PADDING0(window);
		ZKS.MARGIN(window, "30px 0 0 0");

//		BoolCom.initNewAndAppend(window);

		window.appendChild(new LogPageHeader());

		window.appendChild(new BottomHistoryPanel());

		window.appendChild(pageHeader = new AppLogSettingsPanel());

		window.appendChild(new StandsControlPanel());

	}


	//experimental

//	public static class BoolCom extends Html implements IBoolEvent {
//		public BoolCom() {
//		}
//
//		public BoolCom(String content) {
//			super(content);
//		}
//
//		public static void initNewAndAppend(Window window) {
//			BoolCom html = new BoolCom();
//			html.setClass("boolCom");
//			window.appendChild(html);
//
//			ZkPage.addJsTag(window.getPage(), "function onBool(data){\n" + "    zAu.send(new zk.Event(zk.Widget.$('.boolCom'), 'onBool', {'data':data?data:null}, {toServer:true}));\n" + "}");
//		}
//
//		@Override
//		public void onPageAttached(Page newpage, Page oldpage) {
//			super.onPageAttached(newpage, oldpage);
//			addEventListener("onBool", this);
//		}
//
//	}


}
