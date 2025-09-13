package zk_notes.control;

import lombok.Setter;
import mpc.arr.STREAM;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpe.core.P;
import mpe.str.CN;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import mpu.str.Rt;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0D;
import zk_com.core.IReRender;
import zk_com.core.IZDnd;
import zk_com.tabs.Tabbox0;
import zk_form.control.BreadDiv;
import zk_form.control.ErrLb;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.control.tabsmode.InnerPageTb;
import zk_notes.control.tabsmode.NotesSpaceTabsView;
import zk_notes.control.tabsmode.PageTb;
import zk_notes.factory.NFPageCom;
import zk_notes.node.NodeDir;
import zk_notes.node_state.libs.PageState;
import zk_os.AFC;
import zk_os.AppZos;
import zk_os.AppZosProps;
import zk_os.core.Sdn;
import zk_os.sec.Sec;
import zk_os.tasks.TaskPanel;
import zk_os.tasks.v1.TaskPanel_V1;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;
import zk_page.ZkPageInitHeads;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.index.control.TopFixedPanel;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class NotesSpace extends Div0D implements IReRender {//, IZComFadeIO

	final Window window;
	final Pare<String, String> sd3pn;
	final boolean isFirstRun;

	public static NotesSpace findFirst() {
		return ZKCFinderExt.findFirst_inWin0(NotesSpace.class, false);
	}

	public static Component rerenderFirst() {
//		ZKC.printAll();
		NotesSpace notesSpace = ZKCFinderExt.rerenderFirst(NotesSpace.class, true);
		return notesSpace;
	}

	public static void initPage(Window window) {

		window.setClass(ZKS.getAppClassName(PageSP.class));

		//
		// Head Resources

		SeNoteTbxm.registerHeadCom();

		ZkPageInitHeads.initPageHeadLibs(window);

		if (Sec.isEditorAdminOwner()) {
			window.appendChild(new TopFixedPanel());
			window.appendChild(new RightControlPanel());
		}

		NotesSpace notesSpace = new NotesSpace(window, SpVM.get().sdn0());

//		if (AppZosProps.APD_UI_EFFECTS_ENABLE.getValueOrDefault()) {
//			IZComFadeIO.addEffectInImpl(notesSpace);
//		}

		window.appendChild(notesSpace);

		if (AppZosProps.APP_TASKS_V1_PANEL_ENABLE.getValueOrDefault(false)) {
			window.appendChild(new TaskPanel_V1());
		}

		if (AppZosProps.APP_TASKS_PANEL_ENABLE.getValueOrDefault(false)) {
			window.appendChild(new TaskPanel());
		}
	}

	@Override
	public Component newCom() {
		return new NotesSpace(window, sd3pn, false);
	}

	public NotesSpace(Window window, Pare<String, String> sd3pn) {
		this(window, sd3pn, true);
	}

	public NotesSpace(Window window, Pare<String, String> sd3pn, boolean isFirstRun) {
		super();
		this.window = window;
		this.sd3pn = sd3pn;
		this.isFirstRun = isFirstRun;
	}

	private @Setter boolean tabMode = false;

	@Override
	protected void initDND() {
		if (tabMode == false && getPageState().isAllowedAccess_EDIT()) {
			IZDnd.initDND(window, ARG.ofNN(defaultAfterUpdateClb));
		}
	}


	@Override
	protected void init() {

//		addEffectIn(this);

		window.setClass(ZKS.getAppClassName(NotesSpace.class));
//		window.setContentSclass(ZKS.getAppClassName(NotesSpace.class));
//		window.setContentStyle("margin:15px 0 0 0");
//		ZKS.addSTYLE(window, "");
		if (isFirstRun) {
			window.appendChild(new BreadDiv(sd3pn));
		}

		PageState pageState = getPageState();

		Pare<PageState.TabsMode, String[]> tabsMode = pageState.getTabsModeWithValues();

		switch (tabsMode.key()) {
			case def:

				initDefaultMode();

				return;

			case tbf: {

				String[] tabsForms = tabsMode.val();

				initTabsWithFormView(tabsForms);

//				throw NI.stop("forms:" + tabsMode.val());
				return;

			}

			case tbp:

				initTabsMode(tabsMode);

				return;

			default:
				throw new WhatIsTypeException(tabsMode + " ");
		}

	}

	private void initTabsMode(Pare<PageState.TabsMode, String[]> tabsMode) {

		SpVM spVM = SpVM.get();

		String currentSd3 = sd3pn.key();

		String[] tabsPages = tabsMode.val();
		boolean isAll = "*".equals(ARRi.first(tabsPages, null));
		if (isAll) {
			TreeSet<Path> items = AFC.PAGES.DIR_PAGES_LS_CLEAN(currentSd3);
			List<String> pageNames = STREAM.mapToList(items, UF::fn);
			tabsPages = pageNames.toArray(new String[0]);
		}
		Map<PageTb, ?> initTabs = new LinkedHashMap();
		for (String tbPage : tabsPages) {
			InnerPageTb nextPageTb = new InnerPageTb(tbPage, Sdn.of(currentSd3, tbPage));
			nextPageTb.setWithChangeCurrentTabInUrlQuery(!isAll);
			initTabs.put(nextPageTb, null);
		}

		String activeTab = spVM.ppiq().queryUrl().getFirstAsStr(CN.TB, null);
		if (activeTab != null) {
			Optional<PageTb> first = STREAM.findFirstOpt(initTabs.keySet(), tb -> activeTab.equals(tb.getName()));
			(first.isPresent() ? first.get() : ARRi.first(initTabs.keySet())).isOpenedTab(true);
		} else {
			//enable first
			ARRi.first(initTabs.keySet()).isOpenedTab(true);
		}

		FunctionV1<Component> createTabbox = (parent0) -> {
			Tabbox0 tabbox = Tabbox0.newTabbox(initTabs);
			Tabs tabs = tabbox.vertical(false).getTabs();
			tabbox.setHeight("100%");
			tabbox.setWidth("100%");
			parent0.appendChild(tabbox);
			parent0.appendChild(Xml.HR());
		};

		createTabbox.apply(this);
	}


	private void initTabsWithFormView(String[] tabsForms) {
		super.init();

		Map<NodeDir, Component> comMap = NFPageCom.buildPageComMap(sd3pn, this);

		if (L.isInfoEnabled()) {
			L.info(Rt.buildReport(comMap, "ComMap[" + sd3pn.key() + "/" + sd3pn.val() + "]").toString());
		}

		List<NodeLn> nodeComs = ARR.asAL();

		Boolean isAll = "*".equals(ARRi.first(tabsForms, null));

		if (isAll != null && !isAll) {

			comMap = comMap.entrySet().stream().filter(p -> ARR.as(tabsForms).contains(p.getKey().nodeName())).collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));

			if (L.isInfoEnabled()) {
				L.info(Rt.buildReport(comMap, "Filtered " + ARR.as(tabsForms) + " -> ComMap[" + sd3pn.key() + "/" + sd3pn.val() + "]").toString());
			}

//			return;

		}
//		else {
		comMap.entrySet().forEach(e -> {

			Component com = e.getValue();

			if (com == null) {
				//happens error in log
				return;
			} else if (com instanceof ErrLb) {
				if (AppZos.isDebugEnable()) {
					appendChild(com);
				} else {
					P.warnBig("ErrLb:" + com);
				}
				return;
			}

			if (com instanceof NodeLn) {
				NodeLn lnNote = (NodeLn) com;
				nodeComs.add(lnNote);
			}

		});
//		}

		NotesSpaceTabsView child = new NotesSpaceTabsView(nodeComs);

		appendChild(child);


	}

	private void initDefaultMode() {

		defaultAfterUpdateDragEventClb((e) -> {
//			NotesSpace.rerenderFirst();
		});

		super.init();

		Map<NodeDir, Component> comMap = NFPageCom.buildPageComMap(sd3pn, this);

		if (L.isInfoEnabled()) {
			L.info(Rt.buildReport(comMap, "ComMap[" + sd3pn.key() + "/" + sd3pn.val() + "]").toString());
		}

		comMap.entrySet().forEach(e -> {

			Component com = e.getValue();

			if (com == null) {
				//happens error in log
				return;
			} else if (com instanceof ErrLb) {
				if (AppZos.isDebugEnable()) {
					appendChild(com);
				} else {
					P.warnBig("ErrLb:" + com);
				}
				return;
			}

			if (com instanceof NodeLn) {//need for add relative coms
				((NodeLn) com).parentForDependForm(this);
			}

			appendChild(com);

		});
	}

}
