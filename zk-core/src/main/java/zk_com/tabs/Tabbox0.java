package zk_com.tabs;

import com.google.common.collect.Lists;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import org.zkoss.zul.*;

import java.util.*;

public class Tabbox0 extends Tabbox {

//	public static Tabbox0 newTabboxAs(Map map) {
//		List collect = map.entrySet().stream().collect(Collectors.toList());
//		return newTabboxAs(collect);
//	}


	public static Tabbox0 newTabboxAs(Object... tab_i_panel_map) {
		return newTabbox(Arrays.asList(tab_i_panel_map));
	}

	public static Tabbox0 newTabbox(List coms) {
		IT.notEmpty(coms);
		IT.isEven2(X.sizeOf(coms));
		List newComs = new ArrayList();
		for (Object o : coms) {
			if (o instanceof Tabpanel0) {
				Tab0 tab0 = ((Tabpanel0) o).getTab0(null);
				if (tab0 == null) {
					continue;
				}
				newComs.add(tab0);
			}
			newComs.add(o);
		}
		coms = newComs;
		List<List<Object>> subSets = Lists.partition(coms, 2);
		Map<Tab, Tabpanel> tabpanelMap = new LinkedHashMap<>();
		for (List<Object> two : subSets) {
			Tab tab = Tab0.of(two.get(0));
			Tabpanel tabpanel = Tabpanel0.of(two.get(1));
			tabpanelMap.put(tab, tabpanel);
		}
		Tabbox0 tabbox0 = newTabbox(tabpanelMap);

		return tabbox0;
	}

	public Tabbox0 vertical(boolean... isVertical) {
		if (ARG.isDefNotEqFalse(isVertical)) {
			setOrient("vertical");
		}
		return this;
	}

	public static Tabbox0 newTabbox(Map tabboxMapDirty) {
		Map<? extends Tab, ? extends Tabpanel> tabboxMap = normalize(tabboxMapDirty);
		Tabbox0 tabbox = new Tabbox0();

		Tabs tabsHead = new Tabs();

		tabsHead.setParent(tabbox);

		Tabpanels panels = new Tabpanels();
		panels.setParent(tabbox);

		tabboxMap.entrySet().forEach(e -> {
			Tab key = e.getKey();
			key.setParent(tabsHead);
			if (X.notEmpty(key.getLabel())) {
				key.setTooltiptext(key.getLabel());
			}
			e.getValue().setParent(panels);
		});

		return tabbox;
	}

	private static Map<? extends Tab, ? extends Tabpanel> normalize(Map<Object, Object> tabboxMap) {

		Map<Tab, Tabpanel> newComs = new LinkedHashMap<>();

		for (Map.Entry<Object, Object> tabWithPanels : tabboxMap.entrySet()) {
			if (tabWithPanels.getKey() instanceof Tabpanel0) {
				Tab0 tab0 = ((Tabpanel0) tabWithPanels.getKey()).getTab0(null);
				if (tab0 == null) {
					continue;
				}
				newComs.put(tab0, (Tabpanel) tabWithPanels.getKey());
			} else {
				newComs.put(Tab0.of(tabWithPanels.getKey()), Tabpanel0.of(tabWithPanels.getValue()));
			}
		}
		return newComs;
	}

	public Tabbox0 accordeon() {
		setMold("accordion");
		return this;
	}


//
//
//	public static void org(Window parentWindow) {
//		Tabbox tabbox = n
//		ew Tabbox();
//		parentWindow.appendChild(tabbox);
//
//		Tabs tabs = new Tabs();
//		tabs.setParent(tabbox);
//
//		Tab tab1 = new Tab("Parsed & not created");
//		tab1.setParent(tabs);
//		Tab tab2 = new Tab("Parsed & created");
//		tab2.setParent(tabs);
//
//		Tabpanels panels = new Tabpanels();
//		panels.setParent(tabbox);
//
//		Tabpanel tp1 = new Tabpanel();
//		tp1.setParent(panels);
////		List<EventBae> parsedStatus0 = BaeParserArchive.ARCHIVE_DB_SRV.getAllParsedModels_STATUS0();
////		List<EventBae> withoutMeta = BaePageDataSrv.filter(parsedStatus0, BaePageDataSrv.STATUS_PAGE_PARSED);
////			tp1.appendChild(createComponent_TAB_PARSED(parsedStatus0));
//
//		Tabpanel tp2 = new Tabpanel();
//		tp2.setParent(panels);
////			tp2.appendChild(createComponent_TAB_PARSED(withoutMeta));
//	}
}
