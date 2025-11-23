package zk_pages.zk_actuator;

import lombok.SneakyThrows;
import mpu.IT;
import mpu.X;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;
import zk_com.base.Xml;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_com.tabs.Tabbox0;
import zk_com.tabs.Tabpanel0;
import zk_form.WithLogo;
import zk_form.control.StatePropTbxm;
import zk_notes.events.AppEventsPage;
import zk_os.sec.ROLE;
import zk_page.ZKS;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.with_com.WithSearch;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

//https://github.com/codecentric/spring-boot-admin?tab=readme-ov-file
//http://act.q.com:8080/?ska=go&tab=threaddump&jp=$.threads[*].[%27threadName%27,%27threadId%27,%27threadState%27]
@PageRoute(pagename = "@@actuator", role = ROLE.ADMIN)
public class ActuatorPSP extends PageSP implements IPerPage, IZState, WithLogo, WithSearch {

	public static final Logger L = LoggerFactory.getLogger(ActuatorPSP.class);

	public ActuatorPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {

		ZKS.PADDING0(window);
		ZKS.MARGIN(window, "30px 0 0 0");
		ZKS.HEIGHT_MIN(window, "1200px");

		ZKS.BGIMAGE(window, "url(_bg_img/bg_i_sec.png)", "contain", "top", "repeat");

		//
		//

		Menupopup0 menu = getLogoOrAdd().getContextMenu();

		AppEventsPage.applyEvent_EditPageProps(menu, getPageState().pathFc());

		//
		//

		String currentTab = spVM().getQuery().getFirstAsStr("tab", null);

		//
		//

		StatePropTbxm urlStateProp = new StatePropTbxm(getPageState(), "url") {
			@Override
			protected void onSubmitTextValue(Event e) {
				String newUrlValue = getValue();
				if (X.notBlank(newUrlValue)) {
					URL url = UST.URL(newUrlValue, null);
					IT.NN(url);
					IT.state(getValue().contains(ActRequest.ACT_PLACEHOLDER), "set placeholder '%s'", ActRequest.ACT_PLACEHOLDER);
					Set<String> beanNames = ActRequest.of(newUrlValue).getBeanNames();
					IT.notEmpty(beanNames, "This url is not correct (beans not found)");
				}
				super.onSubmitTextValue(e);
			}
		};

//		String url = "http://localhost:8080/actuator/{{ACT}}";
		String value = urlStateProp.getValue();
		String url = X.notBlank(value) ? value : "http://localhost:8080/actuator/{{ACT}}";
//		IT.state(url.contains(ActRequest.ACT_PLACEHOLDER), "set placeholder '%s'", ActRequest.ACT_PLACEHOLDER);

		//
		//

		Map map = new LinkedHashMap<>();
		map.put(new CommonTabpanel0("Common", urlStateProp), null);

		Exception err = null;
		{
			ActRequest actRequest = ActRequest.of(url);
			for (ActType actType : ActType.values()) {
				try {
					if (actType.isWithArgs() || actType.isHeapDump()) {
						continue;
					}
					ActTabpanel0 build = new ActTabpanel0(actRequest, actType).build();
					if (currentTab != null && currentTab.equalsIgnoreCase(build.actType.name())) {
						build.getTab0().setSelected(true);
						Events.postEvent(Events.ON_SELECT, build.getTab0(), null); //simulate a click
					}

					map.put(build, null);

				} catch (Exception ex) {
					L.error("ActType " + actType, ex);
					err = ex;
				}
			}
		}
		if (err != null) {
			map.put("Errors", err.getClass() + ":" + err.getMessage());
		}

		Tabbox0 tabbox = Tabbox0.newTabbox(map);
		Tabs tabs = tabbox.vertical().getTabs();
//		tabs.setWidth("200px");
		tabs.setHflex("min");
		tabs.setHeight("100%");
		tabbox.setHeight("100%");

		window.appendChild(tabbox);

	}

	class CommonTabpanel0 extends Tabpanel0 {
		final StatePropTbxm statePropTbxmUrl;

		public CommonTabpanel0(Object tab0as, StatePropTbxm statePropTbxmUrl) {
			super(tab0as);
			this.statePropTbxmUrl = statePropTbxmUrl;
		}

		@Override
		public void onPageAttached(Page newpage, Page oldpage) {

//			appendChild(new Lb("Dev Info"));
//			appendChild((Lb) new Lb("url=" + url).absolute());

			;
			appendChild(Xml.H(2, "Configuration"));
			appendChild(statePropTbxmUrl);
			appendChild(new StatePropTbxm(getPageState(), "draft"));

			appendChild(Xml.H(2, "Common Info"));
			appendChild(Xml.PRE("management.endpoints.web.exposure.include=*\n" +
					"management.endpoint.health.show-details=always\n" +
					"management.endpoint.health.show-details=when-authorized"));


			super.onPageAttached(newpage, oldpage);
		}
	}

}
