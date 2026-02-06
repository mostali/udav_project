package zk_page.index.control;

import mpc.html.STYLE;
import mpc.str.sym.SYMJ;
import mpc.types.OptLazySupplier;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.func.Function3;
import mpu.func.FunctionV2;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.*;
import zk_com.win.Win0;
import zk_form.ZkTheme;
import zk_form.notify.NotifyRef;
import zk_notes.AppNotesProps;
import zk_notes.AxnTheme;
import zk_notes.leftmenu.LeftMenu;
import zk_notes.node_srv.NodeEvalType;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.PageState;
import zk_notes.search.MainSearchPanel;
import zk_os.AppZos;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecMan;
import zk_os.walkers.PagesWalker;
import zk_page.*;
import zk_page.core.ISpCom;
import zk_page.core.SpVM;
import zk_page.events.ZKE;
import zk_page.index.RSPath;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class TopSpacePanel extends Win0 implements ISpCom {

	public static final int MARGIN_TOP_NOTETYPE = 10;
	public static final int MARGIN_TOP_CONTROL = 25;

	public static TopSpacePanel findFirst(TopSpacePanel... defRq) {
		return ZKCFinderExt.findFirst_inPage0(TopSpacePanel.class, true, defRq);
	}

	public static void replaceFirst(Component topSpacePanel) {
		findFirst().replaceWith(topSpacePanel);
	}

	@Override
//	@Init
	protected void init() {
		super.init();

		setClass(ZKS.getAppClassName(TopSpacePanel.class));

		ZKS.WC_PADDING(this, 3, 0);
		fixed();
//		width(60.0);
		height(AxnTheme.HEADER_HEIGHT_FIXED_LEFT); //why in html -10px, eg 40px (if set 50)?
//		absolute();

		addSTYLE("background-color: rgba(255, 255, 255, 0.5)");

		setContentStyle(STYLE.addStyle(getContentStyle(), "background-color: rgba(255, 255, 255, 0.3)"));

		top_left(5, 5);

		ZKS.BORDER_RADIUS(this, "10px");

		SpVM spVM = SpVM.get();

		Sdn sdn = spVM.sdn();


		Supplier<Boolean> checkViewFunc = () -> (Boolean) getPageState().getAs(AppNotesProps.NAV_MENU_OPENED, Boolean.class, false);

//		SecState ss = SecState.getSecState();
		WebUsr webUsr = WebUsr.get();
		Boolean checkViewEnable = checkViewFunc.get();

		boolean isMainRoleAdminOrOwner = webUsr.isMainRole_ADMIN_OWNER();
		if (isMainRoleAdminOrOwner) {
			//SEARCH ITEM
			MainSearchPanel mainTbx = new MainSearchPanel();
			appendChild(mainTbx);
		}


		Supplier<Component> hSep = () -> (Component) Lb.of(SYMJ.POINT_MINI).padding("0 10px");

		AtomicBoolean initedNav = new AtomicBoolean(false);

		{ //leftMenu Cb
			Cb lmCb = (Cb) new Cb("NAV").moldToggle().onCLICK(e -> {

				boolean checked = ((Cb) e.getTarget()).isChecked();
				if (checked) {
//					if (ss.isPlaneHolder_Or_AdminOrOwnerRole) {
					LeftMenu.openSimple();
//					}
				} else {
					LeftMenu first = LeftMenu.findFirst(null);
					if (first != null) {
						first.detach();
					}
				}


				if (initedNav.get()) {
					if (SecMan.isAllowedEditPlane(webUsr, sdn)) {
						getPageState().set(AppNotesProps.NAV_MENU_OPENED, checked);
					}
				} else {
					initedNav.set(true);
				}
			});


			boolean hasTbf = SpVM.get().getQuery().hasParam(PageState.TabsMode.tbf.name());
			if (!hasTbf) {

//				if (checkViewEnable && ss.isPlaneHolder_Or_AdminOrOwnerRole) {
				ZKE.sendPostEventClick(lmCb);
//				}
				lmCb.setChecked(checkViewEnable);

			}

			lmCb.font_bold_nice(AxnTheme.FONT_SIZE_MENU);

//			appendChild(hSep.get());

			appendChild(lmCb);

		}

		if (!isMainRoleAdminOrOwner) {
			return;
		}

		//EDITOR
		{

			appendChild(hSep.get());
			{//add note
				IconLn iconLn = new IconLn(SYMJ.FILE3, (Event e) -> NodeFileTransferMan.addNewRandomForm(sdn), "Add note", Pare.of(ZKColor.WHITE), MARGIN_TOP_CONTROL);
				appendChild(iconLn);
			}
			{//add special note
				if (AppZos.isDevEnable()) {

					IconLn iconLn = new IconLn(SYMJ.GENDER_SYM, Pare.of(Events.ON_MOUSE_OVER, openPopupEvent(NodeEvalType.values())), "Add special note", Pare.of(ZKColor.YELLOW), MARGIN_TOP_NOTETYPE);
					appendChild(iconLn);
				}
			}
		}

		//
		//

		{

			OptLazySupplier singlyAdderSep = new OptLazySupplier(() -> appendChild(hSep.get()));

//			ZKColor iconCol = ZKColor.toColorOt inZKColor.BLUE;
//			Pare pare = ;

			FunctionV2<String, String> adderFixPageLinkIcon = (plane0, pagename0) -> {
//				IconLn iconLn = new IconLn(pagename0, (Event e) -> RSPath.toPage_Redirect(plane0, pagename0), pagename0, iconCol.nextColor(), MARGIN_TOP_CONTROL);
				IconLn iconLn = new IconLn(pagename0, RSPath.toPage(plane0, pagename0), null, pagename0, ZkTheme.TOP_PANEL_LINK_BG_COLOR, MARGIN_TOP_CONTROL);
				singlyAdderSep.get();
				iconLn.title("Link to " + plane0 + "/" + pagename0);
				appendChild(iconLn);
			};

			Set<Pare<String, String>> fixedPages = new HashSet<>();
			Function3<String, String, PageState, Object> applierPage = (plane, pagename, state) -> {
				if (state.fields().get_FIXED(false)) {
					fixedPages.add(Pare.of(plane, pagename));
				}
				return null;
			};

			PagesWalker.doWalkFuncAllPlanes(applierPage);

			fixedPages.forEach(sdnItem -> adderFixPageLinkIcon.apply(sdnItem.key(), sdnItem.val()));

		}
	}


	private static @NotNull SerializableEventListener openPopupEvent(NodeEvalType[] values) {
		Sdn sdn = SpVM.get().sdn();
		Popup0 popup = new Popup0();
		for (int i = 0; i < values.length; i++) {
			NodeEvalType value = values[i];
			Ln lnToAdd = new Ln(value.icon() + " " + value.name().toLowerCase());
			popup.appendChild(lnToAdd);
			if (!ARR.isLast(i, value)) {
				popup.appendChild(Xml.NBSP(2));
			}
			lnToAdd.onCLICK(e -> {
				NodeFileTransferMan.AddNewForm.OptsAdd opts = NodeFileTransferMan.AddNewForm.OptsAdd.newOpts();
				opts.setNodeEvalType(value);
				NodeFileTransferMan.addNewRandomForm(sdn, opts);
			});
		}
		ZKC.getFirstWindow().appendChild(popup);
		SerializableEventListener eventHandler = ev -> popup.open(ZKC.getFirstWindow(), NotifyRef.Pos.after_pointer.name());
		return eventHandler;
	}

	public static class IconLn extends Ln {

//		public IconLn(String label, SerializableEventListener listener, String title, Integer... margin_top) {
//			this(label, listener, title, null, margin_top);
//		}

		public IconLn(String label, SerializableEventListener listener, String title, Pare bgColor, Integer... margin_top) {
			this(label, Pare.of(Events.ON_CLICK, listener), title, bgColor, margin_top);
		}

//		public IconLn(String label, Pare<String, SerializableEventListener> listener, String title, Integer... margin_top) {
//			this(label, listener, title, null, margin_top);
//		}

		public IconLn(String label, Pare<String, SerializableEventListener> listener, String title, Pare bgColor, Integer... margin_top) {
			this(label, null, listener, title, bgColor, margin_top);
		}

		public IconLn(String label, String href, Pare<String, SerializableEventListener> listener, String title, Pare bgColor, Integer... margin_top) {
			super(X.toStringSE(label, 15));

			if (href != null) {
				setHref(href);
				IT.isNull(listener, "listener!=null");
			} else {
				addEventListener(listener.keyStr(), listener.val());
				IT.isEmpty(href, "href not empty");
			}

			title(title);

			ZKS.applyNiceBg(this, bgColor);
//			ZKS.applyNiceBg(this, iconCol.nextColor(), ZKColor.YELLOW.nextColor());


			bgcolor(ZkTheme.CTRL_BG_COLOR);

			Ln iconLn = this;

			Object marginPat = 0;
			if (ARG.isDef(margin_top)) {
				marginPat = ARG.toDef(margin_top) + "px 2px 0px 2px";
			}
			iconLn.decoration_none().relative().padding(5).margin(marginPat).bgcolor(bgColor).width(31);
			ZKS.BORDER_RADIUS(iconLn, 15);
			ZKS.BORDER_BOTTOM(iconLn, "2px solid " + ZkTheme.CTRL_BORDER_COLOR);

			ZKS.FONT_SIZE(iconLn, 10);
			ZKS.COLOR(iconLn, ZKColor.BLACK.nextColor());
		}


	}

}
