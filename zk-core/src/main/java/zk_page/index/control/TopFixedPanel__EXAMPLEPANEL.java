package zk_page.index.control;

import mpc.html.STYLE;
import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Popup;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base.Popup0;
import zk_com.win.Win0;
import zk_form.notify.NotifyRef;
import zk_form.notify.ZKI_Quest;
import zk_notes.node_srv.NodeEvalType;
import zk_notes.fsman.NodeFileTransferMan;
import zk_os.AppZos;
import zk_os.core.Sdn;
import zk_page.ZKC;
import zk_page.ZKCFinderExt;
import zk_page.ZKColor;
import zk_page.ZKS;
import zk_page.core.ISpCom;
import zk_page.core.SpVM;

public class TopFixedPanel__EXAMPLEPANEL extends Win0 implements ISpCom {

	public static final int MARGIN_TOP_NOTETYPE = 10;
	public static final int MARGIN_TOP_CONTROL = 25;

	public static TopFixedPanel__EXAMPLEPANEL findFirst(TopFixedPanel__EXAMPLEPANEL... defRq) {
		return ZKCFinderExt.findFirst_inPage0(TopFixedPanel__EXAMPLEPANEL.class, true, defRq);
	}

	@Override
//	@Init
	protected void init() {
		super.init();

		ZKS.WC_PADDING(this, 5, 0);
		fixed();
		width(60.0);
		height(40);

		addSTYLE("background-color: rgba(255, 255, 255, 0.5)");

		setContentStyle(STYLE.addStyle(getContentStyle(), "background-color: rgba(255, 255, 255, 0.3)"));

		top_left(5, 5);

		ZKS.BORDER_RADIUS(this, "10px");

		Pare<String, String> sdn = SpVM.get().sdn();

		String orangeCol = ZKColor.ORANGE.variants[6];
		String orangeColDev = ZKColor.ORANGE.variants[1];//1,5

		{
			IconLn iconLn = new IconLn(SYMJ.FILE3, (Event e) -> NodeFileTransferMan.addNewRandomForm(sdn), "Add note", orangeCol, MARGIN_TOP_CONTROL);
			appendChild(iconLn);
		}
		if (AppZos.isDevEnable()) {
			IconLn iconLn = new IconLn(SYMJ.GENDER_SYM, Pare.of(Events.ON_MOUSE_OVER, openPopupEvent(NodeEvalType.values())), "Add special note", orangeColDev, MARGIN_TOP_NOTETYPE);
			appendChild(iconLn);
		}
		//
		//POPUP TECH

		Ln lnClean = ((Ln) (new Ln(SYMJ.CLEAR + " Clean").title("Clean page from empty notes.."))).addEventListener(e -> {
			ZKI_Quest.showMessageBoxBlueYN("Cleaning empty notes", "Clean page from empty notes?", y -> {
				if (y) {
					NodeFileTransferMan.clearPageFromEmptyNotes(sdnAny());
				}
			});
		});

		Pare<String, SerializableEventListener> eventShowPopup = Pare.of(Events.ON_MOUSE_OVER, Popup0.openPopupEventWithComLn(lnClean));
		IconLn iconShowPP_Other = new IconLn(SYMJ.TOOLS, eventShowPopup, "Choice action", MARGIN_TOP_CONTROL);
		appendChild(iconShowPP_Other);

	}


	private static @NotNull SerializableEventListener openPopupEvent(NodeEvalType[] values) {
		Sdn sdn = SpVM.get().sdn();
		Popup popup = new Popup();
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
			super(label);

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

		}


	}

}
