package zk_notes.control;

import mpc.arr.STREAM;
import mpc.html.STYLE;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base_ext.Popup0;
import zk_com.win.Win0;
import zk_form.notify.NotifyRef;
import zk_form.notify.ZKI_Messagebox;
import zk_notes.AppNotes;
import zk_notes.node_srv.core.NodeEvalType;
import zk_os.AppZos;
import zk_os.AppZosProps;
import zk_os.core.Sdn;
import zk_page.*;
import zk_page.behaviours.UploaderFileOrPhotoEvent;
import zk_page.core.ISpCom;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_page.events.ZKE;

import java.nio.file.Path;
import java.util.List;

public class RightControlForm extends Win0 implements ISpCom {

	public static RightControlForm findFirst(RightControlForm... defRq) {
		return ZKCFinder.findFirstIn_Page(RightControlForm.class, true, defRq);
	}

	@Override
	protected void init() {
		super.init();

		ZKS.PADDING_WIN(this, 5, 0);
		fixed();
		width(40);

		addSTYLE("background-color: rgba(255, 255, 255, 0.5)");

		String newStyle = STYLE.addStyle(getContentStyle(), "background-color: rgba(255, 255, 255, 0.3)");
		setContentStyle(newStyle);

		bottom_rigth(5.0, 0.5);
		ZKS.BORDER_RADIUS(this, "10px");

		addTechLinks();

		Pare<String, String> sdn = SpVM.get().sdn0();

		{
			SerializableEventListener event = (Event e) -> {
				NodeFileTransferMan.addNewRandomForm(sdn);
			};
			IconLn iconLn = new IconLn(SYMJ.FILE3, event, "Add note", 20);
			appendChild(iconLn);

		}
		{
			SerializableEventListener event = (Event e) -> {
				NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
				opts.setWysiwygView(true);
				NodeFileTransferMan.addNewRandomForm(sdn, opts);
			};
			IconLn iconLn = new IconLn(SYMJ.FILE_HTML, event, "Add WYSIWYG note");
			appendChild(iconLn);

		}

		{

			{
				//
				// Inner forms
				SerializableEventListener eventAddPrettyCode = (Event e) -> {
					NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
					opts.setPrettyCodeView(true);
					NodeFileTransferMan.addNewRandomForm(sdn, opts);
				};
				Ln lnPretty = (Ln) new Ln(SYMJ.FILE_HTML).addEventListener(eventAddPrettyCode).title("Add PRETTY CODE note");

				//
				//
				SerializableEventListener eventSize = (Event e) -> {
					NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
					opts.setNoteSize(2);
					NodeFileTransferMan.addNewRandomForm(sdn, opts);
				};
				//					IconLn iconLn = new IconLn(SYMJ.ABCD, event, "Add double note");
				//					appendChild(iconLn);
				Ln lnSize = (Ln) new Ln(SYMJ.ABCD).addEventListener(eventSize).title("Add double note");

				//
				//

				SerializableEventListener popup = Popup0.openPopupEventOther(lnPretty,lnSize);

				Pare<String, SerializableEventListener> event = Pare.of(Events.ON_MOUSE_OVER, popup);
				appendChild(new IconLn(SYMJ.CONTROL, event, "Choice type"));
			}

		}

		{
			Path mediaBlankDir = AppNotes.getFormBlankDir(sdn, "media-", 3);
			SerializableEventListener hrefAction = new UploaderFileOrPhotoEvent(mediaBlankDir.toString(), true, p -> {
				if (AppZosProps.APP_WEB_SYNC.getValueOrDefault()) {
					ZKR.restartPage();
				}
				return null;
			});
			IconLn iconLn = new IconLn(SYMJ.FILE_IMG3, hrefAction, "Add media note from clipboard");
			appendChild(iconLn);

		}

		{
			if (AppZos.isDevEnable()) {
				{
					NodeEvalType[] values = NodeEvalType.values();
					SerializableEventListener eventHandler = openPopupEvent(values);
					IconLn iconLn = new IconLn(SYMJ.GENDER_SYM, Pare.of(Events.ON_MOUSE_OVER, eventHandler), "Add special note", 30);
					appendChild(iconLn);
				}

				{
					SerializableEventListener event = (Event e) -> {
						List<String> allEmojs = SYMJ.getAllEmojs();
						String strings = X.f("var strings = %s;", ZKJS.getAsJsArray(allEmojs));
//				String screen = "var screen = [100, 100, 1400, 1200];";
						ZKJS.eval(strings + "distributeStringsOnScreen(strings, null, 50);");
					};
					IconLn iconLn = new IconLn(SYMJ.BOT2, event, "Choice Emoj", 50);
					appendChild(iconLn);
				}
				{
					SerializableEventListener event = (Event e) -> {
						String strings = X.f("var strings = %s;", ZKJS.getAsJsArray(ZKColor.getAllColors()));
						String screen = "var screen = null;";
						ZKJS.eval(strings + screen + "distributeStringsOnScreen(strings, null, 150);");
					};
					IconLn iconLn = new IconLn(SYMJ.FILE_IMG2, event, "Choice Color");
					appendChild(iconLn);
				}
				{
					SerializableEventListener event = (Event e) -> {
						PageSP.doAlignPageGrid();
						NotesSpace.rerenderFirst();
					};
					IconLn iconLn = new IconLn(SYMJ.GEAR, event, "Enable auto align grid");
					appendChild(iconLn);
				}

				addLinkCleanEmpty();

			}
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

	private void addTechLinks() {
		addLinkCloseAll();
	}

	private void addLinkCleanEmpty() {
		SerializableEventListener hrefAction = e -> {
			ZKI_Messagebox.showMessageBoxBlueYN("Cleaning empty notes", "Clean page from empty notes?", y -> {
				if (y) {
					NodeFileTransferMan.clearPageFromEmptyNotes(sdn());
				}
			});
		};
		IconLn iconLn = new IconLn(SYMJ.CLEAR, hrefAction, "Clean page from empty notes..");
		appendChild(iconLn);
	}

	private void addLinkCloseAll() {
		SerializableEventListener hrefAction = e -> {
			boolean close = ZKE.isWithCtrl(e);

			List<Component> allNodeCom = ZKNFinder.findAllNodeCom(true, true);
			List<Window> components = (List) STREAM.filterToList(allNodeCom, c -> c instanceof Window);
			components.forEach(w -> {
				if (close) {
					Events.postEvent(Events.ON_CLOSE, w, null); //simulate a click
//					w.onClose();
				} else {
					w.detach();
				}
			});
		};
		IconLn iconLn = new IconLn(SYMJ.FAIL_NICE_THINK, hrefAction, "Close all");
		appendChild(iconLn);
	}

	public static class IconLn extends Ln {


		public IconLn(String label, SerializableEventListener listener, String title, Integer... margin_top) {
			this(label, Pare.of(Events.ON_CLICK, listener), title, margin_top);
		}

		public IconLn(String label, Pare<String, SerializableEventListener> listener, String title, Integer... margin_top) {
			super(label);

			addEventListener(listener.keyStr(), listener.val());

			title(title);

			String bgColor = ZKColor.ORANGE.variants[6];
			String bgColorControl = "#f3efe9";
			bgcolor(bgColorControl);

			Ln addTextLn = this;

			Object marginPat = 0;
			if (ARG.isDef(margin_top)) {
				marginPat = ARG.toDef(margin_top) + "px 0px 0px 0px";
			}
			addTextLn.decoration_none().relative().block().padding(5).margin(marginPat).bgcolor(bgColor).width(31);
			ZKS.BORDER_RADIUS(addTextLn, 5);
			ZKS.BORDER_BOTTOM(addTextLn, "2px solid " + bgColorControl);

		}


	}

}
