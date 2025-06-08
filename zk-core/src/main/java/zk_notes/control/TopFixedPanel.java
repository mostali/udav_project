package zk_notes.control;

import mpc.html.STYLE;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.func.Function2;
import mpu.func.Function3;
import mpu.func.FunctionV1;
import mpu.func.FunctionV2;
import mpu.pare.Pare;
import mpu.str.STR;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Popup;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base_ext.Popup0;
import zk_com.win.Win0;
import zk_form.notify.NotifyRef;
import zk_form.notify.ZKI_Messagebox;
import zk_notes.node_srv.core.NodeEvalType;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.FormState;
import zk_notes.node_state.libs.PageState;
import zk_os.AppZos;
import zk_os.core.Sdn;
import zk_os.walkers.PagesWalker;
import zk_os.walkers.PlaneWalker;
import zk_page.*;
import zk_page.core.ISpCom;
import zk_page.core.SpVM;
import zk_page.index.RSPath;

import java.nio.file.Path;

public class TopFixedPanel extends Win0 implements ISpCom {

	public static final int MARGIN_TOP_NOTETYPE = 10;
	public static final int MARGIN_TOP_CONTROL = 25;

	public static TopFixedPanel findFirst(TopFixedPanel... defRq) {
		return ZKCFinder.findFirstIn_Page(TopFixedPanel.class, true, defRq);
	}

	@Override
//	@Init
	protected void init() {
		super.init();

		ZKS.PADDING_WIN(this, 5, 0);
		fixed();
//		width(60.0);
		height(40);

		addSTYLE("background-color: rgba(255, 255, 255, 0.5)");

		setContentStyle(STYLE.addStyle(getContentStyle(), "background-color: rgba(255, 255, 255, 0.3)"));

		top_left(5, 5);

		ZKS.BORDER_RADIUS(this, "10px");

		SpVM spVM = SpVM.get();
		PageState pageState = spVM.pageState();
		Pare<String, String> sdn = spVM.sdn0();

		ZKColor iconCol = ZKColor.BLUE;
//		String orangeColDev = ZKColor.YELLOW.variants[1];
		String orangeColDev = ZKColor.YELLOW.nextColor();

		{
			IconLn iconLn = new IconLn(SYMJ.FILE3, (Event e) -> NodeFileTransferMan.addNewRandomForm(sdn), "Add note", ZKColor.WHITE.nextColor(), MARGIN_TOP_CONTROL);
			appendChild(iconLn);
		}
		{
			if (AppZos.isDevEnable()) {
				IconLn iconLn = new IconLn(SYMJ.GENDER_SYM, Pare.of(Events.ON_MOUSE_OVER, openPopupEvent(NodeEvalType.values())), "Add special note", orangeColDev, MARGIN_TOP_NOTETYPE);
				appendChild(iconLn);
			}
		}
//		{//
//			Ln lnClean = ((Ln) (new Ln(SYMJ.CLEAR + " Clean").title("Clean page from empty notes.."))).addEventListener(e -> {
//				ZKI_Messagebox.showMessageBoxBlueYN("Cleaning empty notes", "Clean page from empty notes?", y -> {
//					if (y) {
//						NodeFileTransferMan.clearPageFromEmptyNotes(sdn());
//					}
//				});
//			});
//			Pare<String, SerializableEventListener> eventShowPopup = Pare.of(Events.ON_MOUSE_OVER, Popup0.openPopupEventWith(lnClean));
//			IconLn iconShowPP_Other = new IconLn(SYMJ.TOOLS, eventShowPopup, "Choice action", MARGIN_TOP_CONTROL);
//			appendChild(iconShowPP_Other);
//		}

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
				if (state.fields().get_FIXED()) {
					newIcon.apply(sdn.key(), pagename);
				}
				return null;
			};

			PagesWalker.doWalkFuncAllPlanes(applierPage);

		}
	}


	private static @NotNull SerializableEventListener openPopupEvent(NodeEvalType[] values) {
		Sdn sdn = SpVM.get().sdn0();
		Popup popup = new Popup();
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
