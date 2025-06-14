package zk_notes.control;

import lombok.Setter;
import mpc.arr.STREAM;
import mpc.exception.NI;
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
import zk_com.base.Tbxm;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Div0D;
import zk_com.core.IReRender;
import zk_com.core.IZCom;
import zk_com.core.IZDnd;
import zk_com.tabs.Tabbox0;
import zk_form.control.*;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.control.tabsmode.InnerPageTb;
import zk_notes.control.tabsmode.NotesSpaceTabsView;
import zk_notes.control.tabsmode.PageTb;
import zk_notes.factory.NFPageCom;
import zk_notes.node_state.libs.PageState;
import zk_os.AFC;
import zk_os.AppZos;
import zk_os.AppZosProps;
import zk_os.core.Sdn;
import zk_os.sec.Sec;
import zk_os.tasks.TaskPanel;
import zk_page.ZKS;
import zk_page.ZkPageAuto;
import zk_page.core.SpVM;
import zk_notes.node.NodeDir;
import zk_page.ZKCFinder;
import zk_os.tasks.v1.TaskPanel_V1;

import java.util.*;

public class NotesSpace extends Div0D implements IReRender {

	final Window window;
	final Pare<String, String> sd3pn;
	final boolean isFirstRun;

	public static NotesSpace findFirst() {
		return ZKCFinder.findFirst(NotesSpace.class, null);
	}

	public static Component rerenderFirst() {
//		ZKC.printAll();
		NotesSpace notesSpace = ZKCFinder.rerenderFirst(NotesSpace.class);
		return notesSpace;
	}

	public static void initPage(Window window) {

		//
		// Head Resources

		SeNoteTbxm.registerHeadCom();

		ZkPageAuto.initPageHeadLibs(window);

		if (Sec.isEditorAdminOwner()) {
			window.appendChild(new TopFixedPanel());
			window.appendChild(new RightControlPanel());
		}

		window.appendChild(new NotesSpace(window, SpVM.get().sdn0()));

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
				String[] tabsPages = tabsMode.val();
				boolean isAll = "*".equals(ARRi.first(tabsPages, null));
				if (isAll) {
					initTabs();
					return;
				}
				throw NI.stop("forms:" + tabsMode.val());
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
			List<String> pageNames = STREAM.mapToList(AFC.PAGES.DIR_PAGES_LS_CLEAN(currentSd3), UF::fn);
			tabsPages = pageNames.toArray(new String[0]);
		}
		Map<PageTb, ?> initTabs = new LinkedHashMap();
		for (String tbPage : tabsPages) {
			InnerPageTb nextPageTb = new InnerPageTb(tbPage, Sdn.of(currentSd3, tbPage));
			nextPageTb.setSkipChnageActiveTb(isAll);
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


	private void initTabs() {
		super.init();

		Map<NodeDir, Component> comMap = NFPageCom.buildPageComMap(sd3pn, this, true);

		if (L.isInfoEnabled()) {
			L.info(Rt.buildReport(comMap, "ComMap[" + sd3pn.key() + "/" + sd3pn.val() + "]").toString());
		}

		List<NodeLn> nodeComs = ARR.asAL();

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
				nodeComs.add((NodeLn) com);
			}

		});

		appendChild(new NotesSpaceTabsView(nodeComs));


	}

	private void initDefaultMode() {

		defaultAfterUpdateDragEventClb((e) -> {
//			NotesSpace.rerenderFirst();
		});

		super.init();

		Map<NodeDir, Component> comMap = NFPageCom.buildPageComMap(sd3pn, this, true);

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


			if (false) {

				if (!isFirstRun && com instanceof NodeLn) {
//				.checkAndOpenIfStateOpened(false)
				}

			}

			if (com instanceof NodeLn) {//need for add relative coms
				((NodeLn) com).parentForDependForm(this);
			}

			appendChild(com);

		});
	}

	public static class EvalView extends Div0 {

		final NodeDir nodeDir;
		final String data;

//		public EvalView(String data) {
//			this.data = data;
//		}

		public EvalView(NodeDir node, String data) {
			this.nodeDir = node;
			this.data = data;
		}


		@Override
		public IZCom openInFirstWindow(Window... parent) {

			Window window1 = _modal(Window.Mode.POPUP)._closable()._title(nodeDir.nodeName())._showInWindow(parent);

			ZKS.WIDTH_HEIGHT(window1, 500, 500);

			return this;
		}

		@Override
		protected void init() {
			super.init();
			Tbxm tbxm = Tbxm.of(data);

			appendChild(tbxm);

			ZKS.WIDTH(tbxm, 200);
			ZKS.HEIGHT(tbxm, 200);

		}
	}

}
