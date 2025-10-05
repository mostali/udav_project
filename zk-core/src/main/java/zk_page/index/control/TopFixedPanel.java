package zk_page.index.control;

import mpc.exception.WhatIsTypeException;
import mpc.html.STYLE;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.func.Function3;
import mpu.func.FunctionV2;
import mpu.pare.Pare;
import mpu.paree.Paree;
import mpu.paree.Paree3;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Cb;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base.Popup0;
import zk_com.win.Win0;
import zk_form.notify.NotifyRef;
import zk_notes.AppNotesProps;
import zk_notes.AxnTheme;
import zk_notes.leftmenu.LeftMenu;
import zk_notes.node_srv.core.NodeEvalType;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.FormState;
import zk_notes.node_state.libs.PageState;
import zk_notes.search.NoteBandboxLogo;
import zk_os.AppZos;
import zk_os.core.Sdn;
import zk_os.walkers.PagesWalker;
import zk_page.*;
import zk_page.core.ISpCom;
import zk_page.core.SpVM;
import zk_page.events.ZKE;
import zk_page.index.RSPath;

import java.util.concurrent.atomic.AtomicBoolean;

public class TopFixedPanel extends Win0 implements ISpCom {

	public static final int MARGIN_TOP_NOTETYPE = 10;
	public static final int MARGIN_TOP_CONTROL = 25;

	public static TopFixedPanel findFirst(TopFixedPanel... defRq) {
		return ZKCFinderExt.findFirst_inPage0(TopFixedPanel.class, true, defRq);
	}

	@Override
//	@Init
	protected void init() {
		super.init();

		setClass(ZKS.getAppClassName(TopFixedPanel.class));

		ZKS.PADDING_WIN(this, 5, 0);
		fixed();
//		width(60.0);
		height(50); //why in html -10px, eg 40px (if set 50)?
//		absolute();

		addSTYLE("background-color: rgba(255, 255, 255, 0.5)");

		setContentStyle(STYLE.addStyle(getContentStyle(), "background-color: rgba(255, 255, 255, 0.3)"));

		top_left(5, 5);

		ZKS.BORDER_RADIUS(this, "10px");

		SpVM spVM = SpVM.get();
		PageState pageState = spVM.pageState();
		Pare<String, String> sdn = spVM.sdn0();

		ZKColor iconCol = ZKColor.BLUE;

		String orangeColDev = ZKColor.YELLOW.nextColor();

		{//SEARCH ITEM
			NoteBandboxLogo mainTbx = new NoteBandboxLogo();
			appendChild(mainTbx);
		}

		AtomicBoolean initedNav = new AtomicBoolean(false);

		{ //leftMenu Cb
			Cb lmCb = (Cb) new Cb("NAV").moldToggle().onCLICK(e -> {
				boolean checked = ((Cb) e.getTarget()).isChecked();
				if (checked) {
					LeftMenu child = new LeftMenu(getDefaultState());
					ZKC.getFirstWindow().appendChild(child);
				} else {
					LeftMenu first = LeftMenu.findFirst(null);
					if (first != null) {
						first.detach();
					}
				}
				if (initedNav.get()) {
					getPageState().set(AppNotesProps.MAIN_MENU_ENABLE.getPropName(), checked);
				} else {
					initedNav.set(true);
				}
			});

			Boolean isEnableMainMenu = AppNotesProps.MAIN_MENU_ENABLE.getValueOrDefault();
			if (isEnableMainMenu) {
				if (!SpVM.get().getQuery().hasParam(PageState.TabsMode.tbf.name())) {
					lmCb.setChecked(isEnableMainMenu);
					ZKE.sendPostEventClick(lmCb);
				}
			}

			lmCb.font_bold_nice(AxnTheme.FONT_SIZE_MENU);
			appendChild(lmCb);
		}

		{//add note
			IconLn iconLn = new IconLn(SYMJ.FILE3, (Event e) -> NodeFileTransferMan.addNewRandomForm(sdn), "Add note", ZKColor.WHITE.nextColor(), MARGIN_TOP_CONTROL);
			appendChild(iconLn);
		}
		{//add special note
			if (AppZos.isDevEnable()) {
				IconLn iconLn = new IconLn(SYMJ.GENDER_SYM, Pare.of(Events.ON_MOUSE_OVER, openPopupEvent(NodeEvalType.values())), "Add special note", orangeColDev, MARGIN_TOP_NOTETYPE);
				appendChild(iconLn);
			}
		}

		//
		//

		{

			FunctionV2<String, String> newIcon = (sd3, pagename) -> {
				IconLn iconLn = new IconLn(pagename, (Event e) -> {
					RSPath.toPage_Redirect(sd3, pagename);
				}, pagename, iconCol.nextColor(), MARGIN_TOP_CONTROL);
				appendChild(iconLn);
			};

			Function3<String, String, FormState, Object> applierPage = (plane, pagename, state) -> {
				if (state.fields().get_FIXED(false)) {
					newIcon.apply(sdn.key(), pagename);
				}
				return null;
			};

			PagesWalker.doWalkFuncAllPlanes(applierPage);

		}

	}

	private Paree3<Boolean, Boolean, Boolean> getDefaultState() {
		RSPath pathType = Sdn.getRq().getPathType();
		switch (pathType) {
			case ROOT:
				return Paree.of3(true, false, false);
			case PLANE:
				return Paree.of3(false, true, false);
			case PAGE:
				return Paree.of3(false, false, true);
			default:
				throw new WhatIsTypeException(pathType);
		}
	}


	private static @NotNull SerializableEventListener openPopupEvent(NodeEvalType[] values) {
		Sdn sdn = SpVM.get().sdn0();
		Popup0 popup = new Popup0();
		for (int i = 0; i < values.length; i++) {
			NodeEvalType value = values[i];
			Ln lnToAdd = new Ln(value.icon() + " " + value.name().toLowerCase());
			popup.appendChild(lnToAdd);
			if (!ARR.isLast(i, value)) {
				popup.appendChild(Xml.NBSP(2));
			}
			lnToAdd.onCLICK(e -> {
				NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
				opts.setNodeEvalType(value);
				NodeFileTransferMan.addNewRandomForm(sdn, opts);
			});
		}
		ZKC.getFirstWindow().appendChild(popup);
		SerializableEventListener eventHandler = ev -> popup.open(ZKC.getFirstWindow(), NotifyRef.Pos.after_pointer.name());
		return eventHandler;
	}

	public static class IconLn extends Ln {

		public IconLn(String label, SerializableEventListener listener, String title, Integer... margin_top) {
			this(label, listener, title, null, margin_top);
		}

		public IconLn(String label, SerializableEventListener listener, String title, String bgColor, Integer... margin_top) {
			this(label, Pare.of(Events.ON_CLICK, listener), title, bgColor, margin_top);
		}

		public IconLn(String label, Pare<String, SerializableEventListener> listener, String title, Integer... margin_top) {
			this(label, listener, title, null, margin_top);
		}

		public IconLn(String label, Pare<String, SerializableEventListener> listener, String title, String bgColor, Integer... margin_top) {
			super(X.toStringSE(label, 15));

			addEventListener(listener.keyStr(), listener.val());

			title(title);

			bgColor = bgColor == null ? ZKColor.GRAY.variants[0] : bgColor;
			String bgColorControl = "#f3efe9";
			bgcolor(bgColorControl);

			Ln iconLn = this;

			Object marginPat = 0;
			if (ARG.isDef(margin_top)) {
				marginPat = ARG.toDef(margin_top) + "px 2px 0px 2px";
			}
			iconLn.decoration_none().relative().padding(5).margin(marginPat).bgcolor(bgColor).width(31);
			ZKS.BORDER_RADIUS(iconLn, 15);
			ZKS.BORDER_BOTTOM(iconLn, "2px solid " + bgColorControl);

			ZKS.FONT_SIZE(iconLn, 10);
			ZKS.COLOR(iconLn, ZKColor.BLACK.nextColor());
		}


	}

}
