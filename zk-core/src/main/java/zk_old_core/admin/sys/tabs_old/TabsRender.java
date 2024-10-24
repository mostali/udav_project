package zk_old_core.admin.sys.tabs_old;

import com.google.common.collect.Lists;
import mpc.exception.RequiredRuntimeException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.*;
import mpu.IT;
import mpc.exception.WhatIsTypeException;

import java.util.*;

@Deprecated
public class TabsRender {

	List<Tab> tabs = new ArrayList<>();
	List<Tabpanel> tabPanels = new ArrayList<>();

	public void addTab(Tab tab, Tabpanel tabPanel) {
		tabs.add(tab);
		tabPanels.add(tabPanel);
	}

	public void addTab(Tab tab, Component tabPanel) {
		Tabpanel tp1 = new Tabpanel();
		tp1.appendChild(tabPanel);
		addTab(tab, tp1);
	}

	public Tabbox render(Component parentWindow) {
		Tabbox tabbox = new Tabbox();
//		tabbox.setOrient("vertical");
		parentWindow.appendChild(tabbox);

		Tabs tabsHead = new Tabs();
		tabsHead.setParent(tabbox);

		Tabpanels panels = new Tabpanels();
		panels.setParent(tabbox);

		for (int i = 0; i < this.tabs.size(); i++) {
			Tab tab = this.tabs.get(i);
			tab.setParent(tabsHead);

			Tabpanel tabPanel = this.tabPanels.get(i);
			tabPanel.setParent(panels);

		}
		return tabbox;
	}

	public static Tabbox buildAndAdd(Component parentWindow, Object... coms) {
		return buildAndAdd(parentWindow, Arrays.asList(coms));
	}

	public static Tabbox buildAndAdd(Component parentWindow, List coms) {
		IT.notEmpty(coms);
		List newComs = new ArrayList();
		for (Object o : coms) {
			if (o instanceof HeadLazyTabpanel) {
				newComs.add(((HeadLazyTabpanel) o).getTabHead());
			}
			newComs.add(o);
		}
		coms = newComs;
		if (coms.size() % 2 != 0) {
			throw new RequiredRuntimeException("%2 need");
		}
		List<List<Object>> subSets = Lists.partition(coms, 2);
		TabsRender render = new TabsRender();
		for (List<Object> two : subSets) {
			Tab tab = createTabHead(two.get(0));
			Tabpanel tabpanel = createTabPanel(two.get(1));
//			tabpanel.addEventListener(Events.ON_AFTER_SIZE, new EventListener<Event>() {
//				@Override
//				public void onEvent(Event event) throws Exception {
//					U.say("size");
//				}
//			});
			render.addTab(tab, tabpanel);
		}
		return render.render(parentWindow);

	}

	private static Tab createTabHead(Object com) {
		if (com instanceof Tab) {
			return (Tab) com;
		} else if (com instanceof CharSequence || com instanceof Enum) {
			Tab tab = new Tab(com.toString());
//			tab.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
//				@Override
//				public void onEvent(Event event) throws Exception {
////					U.say("click");
//				}
//			});
			return tab;
		} else if (com instanceof Component) {
			Tab tab = new Tab();
//			tab.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
//				@Override
//				public void onEvent(Event event) throws Exception {
////					U.say("click");
//				}
//			});
			tab.appendChild((Component) com);
			return tab;
		}
		throw new WhatIsTypeException(com.getClass());
	}

	private static Tabpanel createTabPanel(Object com) {
		if (com instanceof Tabpanel) {
			return (Tabpanel) com;
		} else if (com instanceof CharSequence) {
			Tabpanel tab = new Tabpanel();
			tab.appendChild(new Label(com.toString()));
			return tab;
		} else if (com instanceof Component) {
			Tabpanel tab = new Tabpanel();
			tab.appendChild((Component) com);
			return tab;
		}
		throw new WhatIsTypeException(com.getClass());
	}

	public static void ex(Window parentWindow) {
		TabsRender render = new TabsRender();
		render.addTab(new Tab("tab1"), new Label("Tabpanel1"));
		render.addTab(new Tab("tab2"), new Label("Tabpanel2"));
		render.render(parentWindow);
	}

	public static void org(Window parentWindow) {
		Tabbox tabbox = new Tabbox();
		parentWindow.appendChild(tabbox);

		Tabs tabs = new Tabs();
		tabs.setParent(tabbox);

		Tab tab1 = new Tab("Parsed & not created");
		tab1.setParent(tabs);
		Tab tab2 = new Tab("Parsed & created");
		tab2.setParent(tabs);

		Tabpanels panels = new Tabpanels();
		panels.setParent(tabbox);

		Tabpanel tp1 = new Tabpanel();
		tp1.setParent(panels);
//		List<EventBae> parsedStatus0 = BaeParserArchive.ARCHIVE_DB_SRV.getAllParsedModels_STATUS0();
//		List<EventBae> withoutMeta = BaePageDataSrv.filter(parsedStatus0, BaePageDataSrv.STATUS_PAGE_PARSED);
//			tp1.appendChild(createComponent_TAB_PARSED(parsedStatus0));

		Tabpanel tp2 = new Tabpanel();
		tp2.setParent(panels);
//			tp2.appendChild(createComponent_TAB_PARSED(withoutMeta));
	}
}
