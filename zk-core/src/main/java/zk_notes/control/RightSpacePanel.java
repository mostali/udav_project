package zk_notes.control;

import mpc.html.STYLE;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Popup;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base.Popup0;
import zk_com.core.IZStyle;
import zk_com.uploader.DDFileUploader;
import zk_com.win.Win0;
import zk_form.ZkTheme;
import zk_form.ext.GalleryVF;
import zk_form.notify.NotifyRef;
import zk_form.notify.ZKI_Quest;
import zk_notes.node.core.NVT;
import zk_notes.node_srv.NodeEvalType;
import zk_os.AppZosProps;
import zk_os.coms.AFCC;
import zk_os.core.Sdn;
import zk_page.*;
import zk_page.behaviours.BgImg;
import zk_page.behaviours.EventHighlightForm;
import zk_page.behaviours.UploaderFileOrPhotoEvent;
import zk_page.core.ISpCom;
import zk_page.core.SpVM;
import zk_notes.fsman.NodeFileTransferMan;
import zk_page.events.ECtrl;
import zk_page.events.ZKE;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RightSpacePanel extends Win0 implements ISpCom {

	public static final int MARGIN_TOP_NOTETYPE = 10;
	public static final int MARGIN_TOP_CONTROL = 25;

	public static RightSpacePanel findFirst(RightSpacePanel... defRq) {
		return ZKCFinderExt.findFirst_inPage0(RightSpacePanel.class, true, defRq);
	}

	public static RightSpacePanel light(String msg) {
		RightSpacePanel first = findFirst(null);
		if (first == null) {
			return null;
		}
//		ZKI.infoBottomCenter(msg);
		ZKJS.eval("highlightElement('%s',1000)", first.getUuid());
		return first;
	}

	@Override
	protected void init() {
		super.init();

//		appendChild(Xml.ofJs("function lightRCP(){ highlightElement()alert ('ok:'+ %s);return %s; }", getUuid(), getUuid()));

		ZKS.WC_PADDING(this, 5, 0);
		fixed();
		width(40);

		addSTYLE("background-color: rgba(255, 255, 255, 0.5)");

		setContentStyle(STYLE.addStyle(getContentStyle(), "background-color: rgba(255, 255, 255, 0.3)"));

		bottom_rigth(5.0, 0.5);

		ZKS.BORDER_RADIUS(this, "10px");

		addTechLinks();

		Pare<String, String> sdn = SpVM.get().sdn();

		String orangeCol = ZKColor.ORANGE.variants[6];
		String orangeColDev = ZKColor.ORANGE.variants[1];//1,5

		{
			IconLn iconLn = new IconLn(SYMJ.FILE3, (Event e) -> NodeFileTransferMan.addNewRandomForm(sdn), "Add note", orangeCol, MARGIN_TOP_CONTROL);
			appendChild(iconLn);
		}

		{
			SerializableEventListener eventWYSIWIG = (Event e) -> {
				NodeFileTransferMan.AddNewForm.OptsAdd opts = new NodeFileTransferMan.AddNewForm.OptsAdd();
				opts.setNodeViewType(NVT.WYSIWYG);
				NodeFileTransferMan.addNewRandomForm(sdn, opts);
			};
			Ln lnWYSIWIG = (Ln) new Ln(SYMJ.FILE_HTML + " WYSIWYG").addEventListener(eventWYSIWIG).title("Add WYSIWYG note");

			SerializableEventListener eventAddPrettyCode = (Event e) -> {
				NodeFileTransferMan.AddNewForm.OptsAdd opts = NodeFileTransferMan.AddNewForm.OptsAdd.newOpts();
				opts.setNodeViewType(NVT.CODE);
				NodeFileTransferMan.addNewRandomForm(sdn, opts);
			};
			Ln lnPretty = (Ln) new Ln(SYMJ.FILE_MANUSCRIPT + " PrettyCode").addEventListener(eventAddPrettyCode).title("Add PRETTY CODE note");

			SerializableEventListener eventSize = (Event e) -> {
				NodeFileTransferMan.AddNewForm.OptsAdd opts = NodeFileTransferMan.AddNewForm.OptsAdd.newOpts();
				opts.setNoteSize(2);
				NodeFileTransferMan.addNewRandomForm(sdn, opts);
			};
			Ln lnSize = (Ln) new Ln(SYMJ.ABCD + " Double").addEventListener(eventSize).title("Add double note");

			//
			//POPUP OTHER NOTE'S

			Pare<String, SerializableEventListener> eventShowPopup = Pare.of(Events.ON_MOUSE_OVER, Popup0.openPopupEventWithComLn(lnSize, lnPretty, lnWYSIWIG));
			IconLn iconShowPP_Other = new IconLn(SYMJ.CONTROL, eventShowPopup, "Choice type", orangeCol);
			appendChild(iconShowPP_Other);
		}

		{//UPLOAD PHOTO / VIDEO
			Path mediaBlankDir = AFCC.getFormDirBlank(sdn, "media-", 3);
			SerializableEventListener hrefAction = new UploaderFileOrPhotoEvent(mediaBlankDir.toString(), true, p -> {
				if (AppZosProps.APP_WEB_SYNC_RESTART_PAGE.getValueOrDefault()) {
					ZKR.restartPage();
				}
				return null;
			});
			IconLn iconLn = new IconLn(SYMJ.FILE_IMG3, hrefAction, "Upload media-note from clipboard", orangeCol, MARGIN_TOP_NOTETYPE);
			appendChild(iconLn);
		}

		{//UPLOAD FiLE
//			Path mediaBlankDir = AFCC.getFormDirBlank(sdn, "file-some");
//			IconLn iconLn = new IconLn(SYMJ.FILE_IMG3, e -> NativeFileUploaderComposer.open("asd", mediaBlankDir, 2), "Upload media-note from clipboard", orangeCol, MARGIN_TOP_NOTETYPE);
			IconLn iconLn = new IconLn(SYMJ.FILE2, e -> DDFileUploader.open(), "Upload file note drag & drop", orangeCol, MARGIN_TOP_NOTETYPE);
			appendChild(iconLn);
		}

//		if (AppZos.isDevEnable()) {
		IconLn iconLn = new IconLn(SYMJ.GENDER_SYM, Pare.of(Events.ON_MOUSE_OVER, openPopupEvent(NodeEvalType.values())), "Add special note", orangeColDev, MARGIN_TOP_NOTETYPE);
		appendChild(iconLn);
//		}

		{

			Ln lnShowEmoji = ((Ln) (new Ln(SYMJ.BOT2 + " ShowEmoji").title("Choice Emoji"))).addEventListener((Event e) -> {
//				List<String> allEmojs = SYMJ.getAllEmojs();
				Map<String, String> allEmojs = SYMJ.getAllEmojsMap();
				String strings = X.f("var strings = %s;", ZKJS.getAsJsArray(allEmojs));
				//String screen = "var screen = [100, 100, 1400, 1200];";
				ZKJS.eval(strings + "distributeStringsOnScreen(strings, null, 50);");
			});

			Ln lnShowColor = ((Ln) (new Ln(SYMJ.FILE_IMG2 + " ShowColor's").title("Choice Color"))).addEventListener((Event e) -> {
				String strings = X.f("var strings = %s;", ZKJS.getAsJsArray(ZKColor.getAllColors()));
				String screen = "var screen = null;";
				ZKJS.eval(strings + screen + "distributeStringsOnScreen(strings, null, 150);");
			});
			Ln lnShowBgImgs = ((Ln) (new Ln(SYMJ.FILE_IMG2 + " Show Bg Images").title("Show all background images.."))).addEventListener((Event e) -> {
				List<String> imagesSrc = BgImg.getAllDefaultBgImages().stream().map(i -> BgImg.getBgImageRelPath(i)).collect(Collectors.toList());

				GalleryVF galleryVF = GalleryVF.ofFileImages(imagesSrc);
//				ZKPage.renderHeadRsrc(galleryVF);
				galleryVF.width(100.0);
//				galleryVF.height(100.0);
//				String js = X.f("copyToClb('curl -s ' + '" + url + "'  + ' | bash')", NodeApiChars.UP + "/" + formName);
//				galleryVF.onDBLCLICK(c->ZKJS.eval(js));
				galleryVF.openInFirstWindow();
			});

			Ln lnAutoGrid = ((Ln) (new Ln(SYMJ.GRID_MANY_H + " AutoGrid").title("Enable auto align grid"))).addEventListener((Event e) -> {
				UPageSP.doAlignPageGrid();
				NotesSpace.rerenderFirst();
			});

			Ln lnClean = ((Ln) (new Ln(SYMJ.CLEAR + " Clean").title("Clean page from empty notes.."))).addEventListener(e -> {
				ZKI_Quest.showMessageBoxBlueYN("Cleaning empty notes", "Clean page from empty notes?", y -> {
					if (y) {
						NodeFileTransferMan.clearPageFromEmptyNotes(sdnAny());
					}
				});
			});

			Ln lnOnBorder = ((Ln) (new Ln(SYMJ.SQUARE_DBL + " Show Border").title("Show Border on components"))).addEventListener(e -> {
				EventHighlightForm.applyOnOff_MouseOverOut((List) ZKCFinderExt.findAllInFirstWin0(HtmlBasedComponent.class, true));
			});

			//
			//POPUP TECH

			Pare<String, SerializableEventListener> eventShowPopup = Pare.of(Events.ON_MOUSE_OVER, Popup0.openPopupEventWithComLn(lnClean, lnAutoGrid, lnOnBorder, lnShowBgImgs, lnShowColor, lnShowEmoji));
			IconLn iconShowPP_Other = new IconLn(SYMJ.TOOLS, eventShowPopup, "Choice action", MARGIN_TOP_CONTROL);
			appendChild(iconShowPP_Other);
		}

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

	private void addTechLinks() {
		addLinkCloseAll();
	}

	private void addLinkCloseAll() {
		SerializableEventListener hrefAction = e -> {
			boolean close = ZKE.isWithCtrl(e, ECtrl.ZKE_CTRL_CODE);
//			boolean openAll = ZKE.isWithCtrl(e, ZKE.ZKE_ALT_CODE);

			List<Component> allNodeCom = ZKNFinder.findAllNodeCom(true, true);
//			List<Window> components = (List) STREAM.filterToList(allNodeCom, c -> c instanceof Window);
			allNodeCom.forEach(w -> {
				if (close) {
					Events.postEvent(Events.ON_CLOSE, w, null); //simulate a click
				} else {
					w.detach();
				}
			});
		};
		IconLn iconLn = new IconLn(SYMJ.FAIL_NICE_THINK, hrefAction, "Close all (without save note state)");
		appendChild(iconLn);
	}

	private static class IconLn extends Ln {

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

			bgcolor(ZkTheme.CTRL_BG_COLOR);

			Ln addTextLn = this;

			Object marginPat = 0;
			if (ARG.isDef(margin_top)) {
				marginPat = ARG.toDef(margin_top) + "px 0px 0px 0px";
			}
			IZStyle lnAction = addTextLn.decoration_none().relative().block().padding(5).margin(marginPat).width(31);

			ZKS.applyNiceBg((HtmlBasedComponent) lnAction, bgColor, "white");

			ZKS.BORDER_RADIUS(addTextLn, 5);
			ZKS.BORDER_BOTTOM(addTextLn, "2px solid " + ZkTheme.CTRL_BORDER_COLOR);

		}


	}

}
