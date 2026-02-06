package zk_page.index.tabs;

import lombok.SneakyThrows;
import mp.utl_odb.tree.AppPropDef;
import mpc.net.query.QueryUrl;
import mpu.func.Function2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.events.AppEventsPage;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.ObjState;
import zk_os.AppZosProps;
import zk_os.core.Sdn;
import zk_os.sec.ROLE;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.core.*;
import zk_page.with_com.WithMainTbx;
import zk_page.with_com.WithSearch;

//https://github.com/codecentric/spring-boot-admin?tab=readme-ov-file
//http://act.q.com:8080/?ska=go&tab=threaddump&jp=$.threads[*].[%27threadName%27,%27threadId%27,%27threadState%27]
//@PageRoute(sd3 = "index", role = ROLE.ADMIN)
@PageRoute(pagename = "@@index", role = ROLE.ADMIN)
public class IndexTabsPSP extends PageSP implements IPerPage, IZState, WithLogo, WithMainTbx, WithSearch {

	public static final Logger L = LoggerFactory.getLogger(IndexTabsPSP.class);
//	public static final String SK_VERTICAL_TABS = "Vertical Tabs";

	public static final AppPropDef<Boolean> APD_VERTICAL_TABS = new AppZosProps.SessionStateAppPropDef("Vertical Tabs", false);


	public IndexTabsPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	public static String[] getIndexPathForQuery() {
		QueryUrl query = SpVM.get().getQuery();
		String tsd3 = query.getFirstAsStr("tsd3", null);
		String tpage = query.getFirstAsStr("tpage", null);
		String tform = query.getFirstAsStr("tform", null);
		return new String[]{tsd3, tpage, tform};

	}

	@SneakyThrows
	public void buildPageImpl() {

		SeNoteTbxm.registerHeadCom();

		ZKS.PADDING0(window);
		ZKS.MARGIN(window, "30px 0 0 0");
		ZKS.HEIGHT_MIN(window, "1200px");

		ZKS.BGIMAGE(window, "url(_bg_img/bg_i_sec.png)", "contain", "top", "repeat");

		//
		//

		Menupopup0 menu = getLogoOrAdd().getContextMenu();

		AppEventsPage.applyEvent_EditPageProps(menu, getPageState().pathFc());

		menu.addMI_SESSSION_BOOLATTR(APD_VERTICAL_TABS.getPropName(), APD_VERTICAL_TABS.getValueOrDefault(true), true);

		{
			String label = "Add Subdomain/Page..";
			String[] initValues = null;
			String[] placeholders = {"subdomain", "pagename"};
			Function2<String, String, Object> successCallback = (t1, t2) -> {
				ObjState pageState = AppStateFactory.forPage(Sdn.of(t1.trim(), t2.trim()), true);
				ZKR.restartPage();
				return null;
			};
			menu.addMI_Tbx2_Cfrm(label, initValues, placeholders, successCallback);
		}
		//
		//

		window.appendChild(new Sd3Tabs(APD_VERTICAL_TABS));

	}


}
