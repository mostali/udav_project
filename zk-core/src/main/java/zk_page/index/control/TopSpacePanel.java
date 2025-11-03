package zk_page.index.control;

import mpc.html.STYLE;
import mpc.str.sym.SYMJ;
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
import zk_com.base.Cb;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base.Popup0;
import zk_com.win.Win0;
import zk_form.notify.NotifyRef;
import zk_notes.AppNotesProps;
import zk_notes.AxnTheme;
import zk_notes.leftmenu.LeftMenu;
import zk_notes.node_srv.NodeEvalType;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.FormState;
import zk_notes.node_state.libs.PageState;
import zk_notes.search.MainSearchPanel;
import zk_os.AppZos;
import zk_os.core.Sdn;
import zk_os.sec.SecMan;
import zk_os.walkers.PagesWalker;
import zk_page.*;
import zk_page.core.ISpCom;
import zk_page.core.SpVM;
import zk_page.events.ZKE;
import zk_page.index.RSPath;

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

		Pare<String, String> sdn = spVM.sdn();

		ZKColor iconCol = ZKColor.BLUE;

		String orangeColDev = ZKColor.YELLOW.nextColor();

		Supplier<Boolean> checkViewFunc = () -> (Boolean) getPageState().getAs(AppNotesProps.NAV_MENU_OPENED, Boolean.class, false);

		boolean isPlaneOwnerOrAdmin = SecMan.isPlaneOwnerOrAdmin();
		Boolean checkViewEnable = checkViewFunc.get();

		if (checkViewEnable && !isPlaneOwnerOrAdmin) {
			LeftMenu.openSimple();

		}


		if (isPlaneOwnerOrAdmin) {
			//SEARCH ITEM
			MainSearchPanel mainTbx = new MainSearchPanel();
			appendChild(mainTbx);
		}


		AtomicBoolean initedNav = new AtomicBoolean(false);

		{ //leftMenu Cb
			Cb lmCb = (Cb) new Cb("NAV").moldToggle().onCLICK(e -> {

				boolean checked = ((Cb) e.getTarget()).isChecked();
				if (checked) {
					LeftMenu.openSimple();
				} else {
					LeftMenu first = LeftMenu.findFirst(null);
					if (first != null) {
						first.detach();
					}
				}


				if (initedNav.get()) {
					if (isPlaneOwnerOrAdmin) {
						getPageState().set(AppNotesProps.NAV_MENU_OPENED, checked);
					}
				} else {
					initedNav.set(true);
				}
			});


			boolean hasTbf = SpVM.get().getQuery().hasParam(PageState.TabsMode.tbf.name());
			if (!hasTbf) {

				if (checkViewEnable && isPlaneOwnerOrAdmin) {
					ZKE.sendPostEventClick(lmCb);
				}
				lmCb.setChecked(checkViewEnable);

			}

			lmCb.font_bold_nice(AxnTheme.FONT_SIZE_MENU);

//			lmCb.setVisible(adminOrOwnerRole);

			appendChild(lmCb);
		}

		if (!isPlaneOwnerOrAdmin) {
			return;
		}

		//EDITOR

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
