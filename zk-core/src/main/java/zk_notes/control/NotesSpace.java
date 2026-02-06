package zk_notes.control;

import lombok.Setter;
import mpc.arr.STREAM;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpe.core.P;
import mpe.str.CN;
import mpu.X;
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
import zk_com.core.IZState;
import zk_com.tabs.Tabbox0;
import zk_form.control.breadcrumbs.BreadDiv;
import zk_form.control.ErrLb;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.control.tabsmode.InnerPageTb;
import zk_notes.control.tabsmode.NotesSpaceTabsView;
import zk_notes.control.tabsmode.PageTb;
import zk_notes.factory.NFPageCom;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.PageState;
import zk_os.coms.AFC;
import zk_os.AppZos;
import zk_os.core.Sdn;
import zk_os.sec.SecMan;
import zk_os.sec.SecManRMM;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;
import zk_page.ZkPageInitHeads;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.index.control.TopSpacePanel;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class NotesSpace extends Div0D implements IReRender {//, IZComFadeIO

	final Window window;
	final Sdn sdn;
	final boolean isFirstRun;

	public static NotesSpace findFirst() {
		return ZKCFinderExt.findFirst_inWin0(NotesSpace.class, false);
	}

//	@Override
//	public Component rerender() {
//		return IReRender.super.rerender();
//	}

	public static Component rerenderFirst() {
//		ZKC.printAll();
		NotesSpace notesSpace = ZKCFinderExt.rerenderFirst(NotesSpace.class, true);
		return notesSpace;
	}

	public static NotesSpace initOnPage(Window window, boolean... woNotesSpace) {

		window.setClass(ZKS.getAppClassName(PageSP.class));

		//
		// Head Resources

		SeNoteTbxm.registerHeadCom();

		ZkPageInitHeads.initPageHeadLibs(window);

		window.appendChild(new TopSpacePanel());

		if (SecMan.isAllowedEditPlane()) {
			window.appendChild(new RightSpacePanel());
//			NotifySrv.addNotify("errr-" + QDate.now(), new RuntimeException("some error", new WhatIsTypeException("inner err")));
//			window.appendChild(new RightNotifyPanel());
		}
		NotesSpace notesSpace = null;
		if (ARG.isDefEqTrue(woNotesSpace)) {
			//ok - no spaces
		} else {
			notesSpace = new NotesSpace(window, SpVM.get().sdn());
			window.appendChild(notesSpace);
		}

		return notesSpace;
	}

	@Override
	public Component newCom() {
		return new NotesSpace(window, sdn, false);
	}

	public NotesSpace(Window window, Sdn sdn) {
		this(window, sdn, true);
	}

	public NotesSpace(Window window, Sdn sdn, boolean isFirstRun) {
		super();
		this.window = window;
		this.sdn = sdn;
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
			window.appendChild(new BreadDiv(sdn));
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

		String currentSd3 = sdn.key();

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

		Map<NodeDir, Component> comMap = NFPageCom.buildPageComMap(sdn, this);

		if (L.isInfoEnabled()) {
			L.info(Rt.buildReport(comMap, "ComMap[" + sdn.key() + "/" + sdn.val() + "]").toString());
		}

		List<NodeLn> nodeComs = ARR.asAL();

		Boolean isAll = "*".equals(ARRi.first(tabsForms, null));

		if (isAll != null && !isAll) {

			comMap = comMap.entrySet().stream().filter(p -> ARR.as(tabsForms).contains(p.getKey().nodeName())).collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));

			if (L.isInfoEnabled()) {
				L.info(Rt.buildReport(comMap, "Filtered " + ARR.as(tabsForms) + " -> ComMap[" + sdn.key() + "/" + sdn.val() + "]").toString());
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

//		defaultAfterUpdateDragEventClb((e) -> {
////			NotesSpace.rerenderFirst();
//		});

		super.init();

		Map<NodeDir, Component> comMap = NFPageCom.buildPageComMap(sdn, this);

		if (L.isInfoEnabled()) {
			L.info(Rt.buildReport(comMap, "ComMap[" + sdn.key() + "/" + sdn.val() + "]").toString());
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
				NodeLn nodeLn = (NodeLn) com;
				boolean rel = ObjState.Position.REL == nodeLn.nodeDir.stateCom().fields().get_POSITION(ObjState.Position.ABS);
//				if (rel) {
				nodeLn.parentForDependForm(NotesSpace.this);
//				}
			} else if (com instanceof IZState) {
				X.nothing();
			}

			appendChild(com);

		});
	}

}
