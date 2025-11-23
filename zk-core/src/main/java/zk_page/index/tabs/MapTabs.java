package zk_page.index.tabs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.AppPropDef;
import mpc.arr.STREAM;
import mpu.X;
import mpu.core.ARRi;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import zk_com.base_ctr.Div0;
import zk_com.tabs.Tab0;
import zk_com.tabs.Tabbox0;
import zk_com.tabs.Tabpanel0;
import zk_page.ZKJS;
import zk_page.core.SpVM;
import zk_page.index.RSPath;

import java.util.Map;

@RequiredArgsConstructor
public class MapTabs extends Div0 {

	private final @Getter AppPropDef<Boolean> isVerticalAppPropDef;

	public static void initAdaptiveStyle(Tabbox0 tabbox, Tabs tabs) {

		tabs.setWidth("130px");
//		tabs.setHflex("min");
		tabs.setVflex("min");
		tabs.setHeight("100%");
		tabbox.setHeight("100%");

	}

	protected Tabbox0 initWithTabs(Map map, boolean simulateFirstClick) {
		Tabbox0 tabbox = Tabbox0.newTabbox(map);
//		boolean isVertical = ZKSession.getSessionAttrs().getAsBoolean(IndexTabsPSP.APD_VERTICAL_TABS.key(), true);
		boolean isVertical = isVerticalAppPropDef.getValueOrDefault(true);
		Tabs tabs = isVertical ? tabbox.vertical().getTabs() : tabbox.getTabs();
//		Tabs tabs = tabbox.getTabs();
//		tabbox.setMold("accordion");

		appendChild(tabbox);

		initAdaptiveStyle(tabbox, tabs);

		String[] indexPath = IndexTabsPSP.getIndexPathForQuery();
		String tsd3 = indexPath[0];
		String tpage = indexPath[1];
		String tform = indexPath[2];

		//
		//
		//

		Tabpanels tabpanels = tabbox.getTabpanels();

		String findLabel;
		if (X.notEmpty(tsd3) && this instanceof Sd3Tabs) {
			findLabel = tsd3;
		} else if (X.notEmpty(tpage) && this instanceof PagesTabs) {
			findLabel = tpage;
		} else if (X.notEmpty(tform) && this instanceof FormsTabs) {
			findLabel = tform;
		} else {
			findLabel = null;
		}

		Component firstComWithLabel = findLabel == null ? null : STREAM.findFirst(tabpanels.getChildren(), c -> {
			Tabpanel0 tabpanel0 = (Tabpanel0) c;
			if (!tabpanel0.equalsBy(findLabel)) {
				return false;
			}
			Tab0 tab0 = tabpanel0.getTab0();
			if (!tab0.getLabel().equals(findLabel)) {
				return false;
			}
			tab0.setSelected(true);
			Events.postEvent(Events.ON_SELECT, tab0, null); //simulate a click
			return true;
		}, null);

		simulateFirstClick = firstComWithLabel == null;

		if (simulateFirstClick) {
			Tabpanel0 first = ARRi.first(tabpanels.getChildren(), null);
			if (first != null) {
				first.getTab0().setSelected(true);
				Events.postEvent(Events.ON_SELECT, first.getTab0(), null); //simulate a click
			}
		}

		return tabbox;
	}

	public static String changeCurrentUrl(String sd3, String page, String form) {
		String planPage = RSPath.PAGE.toPageLink(SpVM.get().sdn());
		String currentUrl = planPage + "&tsd3=" + sd3;
		if (page != null) {
			currentUrl += "&tpage=" + page;
		}
		if (form != null) {
			currentUrl += "&tform=" + form;
		}
		ZKJS.changeUrl(currentUrl);
		return currentUrl;
	}
}
