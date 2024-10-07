package zk_old_core.control_old;

import lombok.SneakyThrows;
import mpc.env.Env;
import mpc.exception.NI;
import mpc.fs.UF;
import mpc.str.sym.FD_ICON;
import mpc.str.sym.SYMJ;
import mpe.core.UBool;
import mpe.state_rw.IMapStateRw;
import mpu.X;
import mpu.core.ARG;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import utl_web.UWeb;
import zk_com.base.Bt;
import zk_com.base_ext.EnumSwitcher;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.elements.Pos12TRBL;
import zk_com.uploader.FileUploaderComposer;
import zk_com.win.EventShowModalEditor;
import zk_form.WithLogo;
import zk_old_core.old.mwin.MWin;
import zk_old_core.std.AbsVF;
import zk_old_core.std_core.FrmEE;
import zk_old_core.AppCoreStateOld;
import zk_old_core.sd.core.SdRsrc;
import zk_os.sec.MatrixAccess;
import zk_os.sec.Sec;
import zk_page.ZKCF;
import zk_page.ZKR;
import zk_old_core.events.PageBehaviours;
import zk_page.core.SpVM;
import zk_old_core.mdl.FormDirModel;
import zk_old_core.mdl.PageDirModel;
import zk_old_core.mdl.pageset.HeadFileModel;
import zk_old_core.mdl.pageset.IFormModel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TopAdminMenu extends Div0 {

	public static final String TM_POS = "pos";

	public static final String _WIDTH_V = "width:3.5rem;";
	public static final String _WIDTH_H = "width:30%;";
	public static final String _TOP0 = "top:0.2rem;";
	public static final String _BOTTOM0 = "bottom:3rem;";
	public static final String _TOP1 = "top:3rem;";
	public static final String _BOTTOM1 = "bottom:0.2rem;";

	public static final String _MARGIN_V_CENTER = "margin-top:35%;";
//	public static final String _MARGIN_V_CENTER = "margin:0 auto;left:0;right:0;";

	//	public static final String _MARGIN_H_CENTER = "margin-left:35%;";
	public static final String _MARGIN_H_CENTER = "margin:0 auto;left:0;right:0;";


	public static void resetState() {
		Pos12TRBL topRight = Pos12TRBL.TR;
		getState().write(TM_POS, topRight.name());
		List<TopAdminMenu> com = ZKCF.rootsByClass(TopAdminMenu.class, true);
		if (!X.empty(com)) {
			applyStyle(com.get(0), topRight, false);
		}
	}


	@Override
	public MatrixAccess getMA() {
		return MatrixAccess.EDITOR_FULL;
	}

	static IMapStateRw getState() {
		return AppCoreStateOld.getStateGlobal(TopAdminMenu.class, true);
	}

	Pos12TRBL getCurrentPosition() {
		return getState().readAs(TM_POS, Pos12TRBL.class, Pos12TRBL.RT);
	}


	private static void applyStyle(TopAdminMenu ctrlMenu, Pos12TRBL tmPos, boolean... writeState) {
		if (ARG.isDefEqTrue(writeState)) {
			getState().write(TM_POS, tmPos.name());
		}

		String _WIDTH_H = UWeb.isMobile() ? "width:100%" : TopAdminMenu._WIDTH_H;
		String baseStyle = "position:fixed;z-index:1000;";
		switch (tmPos) {
			case TL:
				ctrlMenu.setStyle(baseStyle + _TOP0 + "left:2rem;" + _WIDTH_H);
				break;
			case TC:
				ctrlMenu.setStyle(baseStyle + _TOP0 + _MARGIN_H_CENTER + _WIDTH_H);
				break;
			case TR:
				ctrlMenu.setStyle(baseStyle + _TOP0 + "right:2rem" + _WIDTH_H);
				break;
			case RT:
				ctrlMenu.setStyle(baseStyle + "right:2rem;" + _WIDTH_V);
				break;
			case RC:
				ctrlMenu.setStyle(baseStyle + "right:2rem;" + _WIDTH_V + _MARGIN_V_CENTER);
				break;
			case RB:
				ctrlMenu.setStyle(baseStyle + "right:2rem;bottom:2rem;" + _WIDTH_V);
				break;
			case BR:
				ctrlMenu.setStyle(baseStyle + TopAdminMenu._BOTTOM1 + "right:2rem;" + _WIDTH_H);
				break;
			case BC:
				ctrlMenu.setStyle(baseStyle + TopAdminMenu._BOTTOM1 + _MARGIN_H_CENTER + _WIDTH_H);
				break;
			case BL:
				ctrlMenu.setStyle(baseStyle + TopAdminMenu._BOTTOM1 + "left:2rem;" + _WIDTH_H);//!!
				break;
			case LB:
				ctrlMenu.setStyle(baseStyle + "left:2rem;bottom:2rem;" + _WIDTH_V);
				break;
			case LC:
				ctrlMenu.setStyle(baseStyle + "left:2rem;" + _WIDTH_V + _MARGIN_V_CENTER);
				break;
			case LT:
				ctrlMenu.setStyle(baseStyle + "left:2rem;" + _WIDTH_V);
				break;

			default:
				throw new NI(tmPos);
		}
	}

	@SneakyThrows
	protected void init() {

//		SpVM spVM = ZkQ.Q_SPVM.get();
		SpVM spVM = SpVM.get();
		SpVM finalSpVM = spVM;

		PageDirModel pageDirModel = spVM.findPageDirModel(null);

		Div0 mainMenu = this;

		applyStyle(this, getCurrentPosition());

		appendChild(new EnumSwitcher<Pos12TRBL>(Pos12TRBL.class) {
			@Override
			protected void applyPosition(Pos12TRBL typeValue) {
				applyStyle(TopAdminMenu.this, typeValue, true);
			}
		});

		if (MatrixAccess.hasAccessForCurrentUser(MatrixAccess.ADMIN_FULL, false)) {

			/**
			 * *************************************************************
			 * *************************************************************
			 * *************************************************************
			 * --------------------- SYSTEM ENTITIES -----------------------
			 * *************************************************************
			 * *************************************************************
			 * *************************************************************
			 */

			if (MatrixAccess.EDITOR_FULL.hasAccess()) {
				Bt bt = new Bt(SYMJ.PLUS);
				mainMenu.appendChild(bt);
				Menupopup0 simpleMenupopup = mainMenu.appendMenupopup(bt);
				SpVM finalSpVM1 = spVM;
				simpleMenupopup.addMenuitem("HTML", (SerializableEventListener<Event>) event -> {
					FormDirModel.ADD.addHtmlComponent(finalSpVM1.findPageDirModel(), FrmEE.createBlankForm());
					ZKR.restartPage();
				});
			}

//			new AddNewButton() {
//				@Override
//				public SpVM getModelSP(SpVM... defRq) {
//					return finalSpVM;
//				}
//			}.appendTo(mainMenu);


			if (MatrixAccess.ADMIN_FULL.hasAccess()) {
				Bt openerAppProps = new Bt(SYMJ.JET);
				Menupopup0 simplePopupMenu = super.appendMenupopup(openerAppProps, Events.ON_CLICK);

				{//AppProps
					Path pageRootProps = Paths.get(Env.FILE_APPLICATION_PROPERTIES);
					simplePopupMenu.addMenuitem(SYMJ.EDIT_PENCIL + UF.fn(pageRootProps), new EventShowModalEditor(pageRootProps));
				}
				{//AppPropsProd
					Path pageRootProps = Paths.get(Env.FILE_APPLICATION_PROPERTIES_PROD);
					simplePopupMenu.addMenuitem(SYMJ.EDIT_PENCIL + UF.fn(pageRootProps), new EventShowModalEditor(pageRootProps));
				}
				{
					simplePopupMenu.addMenuitem(SYMJ.LINK + "Go To Admin", "/admin", (String) null);
				}

			}
		}

		/**
		 * *************************************************************
		 * *************************************************************
		 * *************************************************************
		 * --------------------------- PAGE FILES -----------------------
		 * *************************************************************
		 * *************************************************************
		 * *************************************************************
		 */


		if (pageDirModel != null && MatrixAccess.EDITOR_FULL.hasAccess()) {

			Bt openerPageFiles = new Bt(SYMJ.FILE_WITH_COLOR);
			Menupopup0 simplePopupMenu = super.appendMenupopup(openerPageFiles, Events.ON_CLICK);

			{//PAGE_JSON
				{
					Path pageRootProps = pageDirModel.getFileRootProps();
					simplePopupMenu.addMenuitem(FD_ICON.FILE_PROPS + UF.fn(pageRootProps), new EventShowModalEditor(pageRootProps));
				}
			}

			{//PAGE_HEAD
				{ //FORM FILES
					Menupopup menuInner = simplePopupMenu.addInnerMenu("Form Files");
					List<IFormModel> iForms = pageDirModel.getPageSet().getIForms();
					for (IFormModel iForm : iForms) {
						Menuitem mi = new Menuitem(iForm.fd().name());
//					mi.addEventListener(Events.ON_CLICK, new EventShowModalEditor(iForm.fd().getFileRootProps()));
						mi.addEventListener(Events.ON_CLICK, new SerializableEventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								MWin.openForm(AbsVF.build(iForm, AbsVF.ViewMode.view));
							}
						});
						menuInner.appendChild(mi);
					}
				}

			}

			{ //HEAD FILES
				Menupopup menuInner = simplePopupMenu.addInnerMenu("Head Files");
				for (HeadFileModel iStaticHead : pageDirModel.getPageSet().getIStaticHeads()) {
					Path iStaticHeadPath = iStaticHead.path();
					String icon;
					switch (iStaticHead.getHeadType()) {
						case HTML:
							icon = FD_ICON.FILE_HTML;
							break;
						case CSS:
							icon = FD_ICON.FILE_CSS;
							break;
						case JS:
							icon = FD_ICON.FILE_SCRIPT;
							break;
						case UNDEFINED:
						default:
							icon = FD_ICON.FILE_PROPS;
							break;
					}
					Menuitem mi = new Menuitem(icon + UF.fn(iStaticHeadPath));
					mi.addEventListener(Events.ON_CLICK, new EventShowModalEditor(iStaticHeadPath));
					menuInner.appendChild(mi);
				}
			}

		}

		/**
		 * *************************************************************
		 * *************************************************************
		 * *************************************************************
		 * --------------------------- UPLOAD SIMPLE FILE's -----------------------
		 * *************************************************************
		 * *************************************************************
		 * *************************************************************
		 */

		{
			Bt openerUploadFiles = (Bt) new Bt(SYMJ.ARROW_DOWN).title("Upload File's");
			Menupopup0 simplePopupMenu = super.appendMenupopup(openerUploadFiles, Events.ON_CLICK);

			{

				Menupopup menuInner = simplePopupMenu.addInnerMenu(SYMJ.FILE4 + "Upload file");

				for (SdRsrc.LocRsrc value : SdRsrc.LocRsrc.values()) {
					Path uploadFileTo = null;
					switch (value) {
						case PAGE_ASSETS:
						case PAGE_UPLOADS:
						case SD_ASSETS:
						case SD_UPLOADS:
							if (pageDirModel == null) {
								continue;
							}
							uploadFileTo = value.getParentOfStdLocationForPageOrSd(pageDirModel.path());
							break;
						case SITE_UPLOADS:
						case SITE_ASSETS:
							uploadFileTo = value.getParentOfStdLocationForPageOrSd(null);
							break;
					}
					String pathUploadTo = uploadFileTo.toString();
					String label = SYMJ.FILE4 + "To '" + value.nameru() + "'";
					//simplePopupMenu.addMenuitem(label, event -> Fileupload.get(1, FileUploaderComposer.getEventUpload(pathUploadTo)));
					Menuitem mi = new Menuitem(label);
					mi.addEventListener(Events.ON_CLICK, event -> Fileupload.get(1, FileUploaderComposer.getEventUpload(pathUploadTo)));
					menuInner.appendChild(mi);
				}

			}
			{

				Menupopup0 menuInner = simplePopupMenu.addInnerMenu(SYMJ.FILE_IMG + "Upload image");

				for (SdRsrc.LocRsrc value : SdRsrc.LocRsrc.values()) {
					Path uploadFileTo = null;
					switch (value) {
						case PAGE_ASSETS:
						case PAGE_UPLOADS:
						case SD_ASSETS:
						case SD_UPLOADS:
							if (pageDirModel == null) {
								continue;
							}
							uploadFileTo = value.getParentOfStdLocationForPageOrSd(pageDirModel.path());
							break;
						case SITE_UPLOADS:
						case SITE_ASSETS:
							uploadFileTo = value.getParentOfStdLocationForPageOrSd(null);
							break;
					}
					String pathUploadTo = uploadFileTo.toString();
					String title = SYMJ.FILE_IMG + "To '" + value.nameru() + "'";
//					Menuitem mi = new Menuitem(title);
//					mi.addEventListener(Events.ON_CLICK, (SerializableEventListener) event -> ClipboardLoaderComposer.loadComponent(title, Paths.get(pathUploadTo)));
//					menuInner.appendChild(mi);
					menuInner.addMenuitem_UploadTo(title, Paths.get(pathUploadTo), true);

				}

			}

		}

		/**
		 * *************************************************************
		 * *************************************************************
		 * *************************************************************
		 * ------------------------- PAGE MODE'S -----------------------
		 * *************************************************************
		 * *************************************************************
		 * *************************************************************
		 */
		{
			Bt openerPageModes = (Bt) new Bt(SYMJ.GEAR).title("Page Behaviour's");
			Menupopup0 simplePopupMenu = super.appendMenupopup(openerPageModes, Events.ON_CLICK);

			{ //INNER MENU - PAGE MODE
//				Menupopup menuInner = simplePopupMenu.createInnerMenu(SYMJ.GEAR + "Page Mode");
//				{
				for (String keyBehavoiur : PageBehaviours.getAllPageBehaviours()) {
					String currentProp = pageDirModel.getRootPropertyIn(PageBehaviours.BEHAVIOURS, keyBehavoiur, "0");
					String icon = UBool.isTrue_Bool_12_YesNo_PlusMinus(currentProp) ? SYMJ.OK_GREEN + " " : "";
					Menuitem mi = new Menuitem(icon + "" + keyBehavoiur);
					mi.addEventListener(Events.ON_CLICK, event -> {
						//String currentProp = pageDirModel.getRootPropertyIn(PageBehaviours.BEHAVIOURS, keyBehavoiur, "0");
						String newProp = UBool.isTrue_Bool_12_YesNo_PlusMinus_SWAP10(currentProp);
						pageDirModel.setRootPropertyIn(PageBehaviours.BEHAVIOURS, keyBehavoiur, newProp, true);
						ZKR.rebuildPage(pageDirModel);
					});
					simplePopupMenu.appendChild(mi);
				}
//				}
			}

		}


		/**
		 * *************************************************************
		 * *************************************************************
		 * *************************************************************
		 * ---------------- OTHER ----------------
		 * *************************************************************
		 * *************************************************************
		 * *************************************************************
		 */


//		new ConsoleButton() {
//			@Override
//			public SpVM getModelSP() {
//				return spVM;
//			}
//		}.appendTo(ctrlMenu);


//		new HomeButton() {
//			@Override
//			public SpVM getModelSP(SpVM... defRq) {
//				return finalSpVM;
//			}
//		}.appendTo(mainMenu);

//		new AdminButton() {
//			@Override
//			public SpVM getModelSP(SpVM... defRq) {
//				return finalSpVM;
//			}
//		}.appendTo(ctrlMenu);

//		new RefreshPageButton() {
//			@Override
//			public SpVM getModelSP(SpVM... defRq) {
//				return finalSpVM;
//			}
//		}.appendTo(mainMenu);

//		new LogoutButton() {
//			@Override
//			public SpVM getModelSP(SpVM... defRq) {
//				return finalSpVM;
//			}
//		}.appendTo(mainMenu);

		if (!Sec.isAnonim()) {
			WithLogo.LogoCom.findFirst().getContextMenu().addMenuitem(SYMJ.LOGOUT).setHref("/logout");
		}
	}

}
