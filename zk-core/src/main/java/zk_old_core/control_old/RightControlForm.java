package zk_old_core.control_old;

import mpc.str.sym.SYMJ;
import mpu.pare.Pare;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Ln;
import zk_com.win.Win0;
import zk_notes.AppNotes;
import zk_page.ZKCF;
import zk_page.ZKColor;
import zk_page.ZKS;
import zk_page.behaviours.UploaderFileOrPhotoEvent;
import zk_page.core.ISpCom;
import zk_page.core.SpVM;
import zk_page.node.fsman.NodeFileTransferMan;

import java.nio.file.Path;

public class RightControlForm extends Win0 implements ISpCom {

	public static final String TM_POS = "pos";

	public static RightControlForm findFirst(RightControlForm... defRq) {
		return ZKCF.findFirstIn_Page(RightControlForm.class, true, defRq);
	}

	@Override
	protected void init() {
		super.init();

		ZKS.PADDING_WIN(this, 5, 0);
		fixed();
		width(40);

		bottom_rigth(5.0, 0.5);

//		String bgColor = ZKColor.ORANGE.nextColor();
		String bgColor = ZKColor.ORANGE.variants[6];
//		bgcolor(ZKColor.GREENS.nextColor());
		String bgColorControl = "#f3efe9";
		bgcolor(bgColorControl);
		ZKS.BORDER_RADIUS(this, "10px");

		Pare<String, String> sdn = SpVM.get().sdn();

		{
			SerializableEventListener event = (Event e) -> {
				NodeFileTransferMan.addNewRandomForm(sdn);
			};
			Ln addTextLn = appendLn(event, SYMJ.FILE3);
			addTextLn.decoration_none().relative().block().padding(5).bgcolor(bgColor).width(31);
			ZKS.BORDER_RADIUS(addTextLn, 5);
			ZKS.BORDER_BOTTOM(addTextLn, "2px solid " + bgColorControl);
		}

		{
			Path imgBlankDir = AppNotes.getRpaForms_BlankDir(sdn, "img", 3);
			SerializableEventListener hrefAction = new UploaderFileOrPhotoEvent(imgBlankDir.toString(), true, null);
			Ln addImgNode = appendLn(hrefAction, SYMJ.FILE_IMG3);
			addImgNode.decoration_none().relative().block().padding(5).bgcolor(bgColor).width(31);
			ZKS.BORDER_RADIUS(addImgNode, 5);

		}

	}

	//	public static void resetState() {
//		WinPos defPos = WinPos.TC;
//		Pare<String, String> sdn = SpVM.get().sdn();
//		AFC.getRpaComStatePath(sdn.key(),sdn.val(),RightFormControl.class.getSimpleName()).write(TM_POS, defPos.name());
//		List<RightFormControl> com = ZKCF.rootsByClass(RightFormControl.class, true);
//		if (!X.empty(com)) {
//			applyStylePos(com.get(0), defPos, false);//reset
//		}
//	}

//	@Override
//	public MatrixAccess getMA() {
//		return MatrixAccess.EDITOR_FULL;
//	}
//
//	static IMapStateRw getState() {
//		return AppCoreStateOld.getStateGlobal(RightFormControl.class, true);
//	}
//
//	WinPos getCurrentPosition() {
//		return getState().readAs(TM_POS, WinPos.class, WinPos.TC);
//	}
//
//	private static void applyStylePos(RightFormControl ctrlMenu, WinPos tmPos, boolean... writeState) {
//		if (ARG.isDefEqTrue(writeState)) {
//			getState().write(TM_POS, tmPos.name());
//		}
//		if (ctrlMenu instanceof Win0) {
//			ctrlMenu.setPosition(tmPos.getPattern());
//			ctrlMenu.invalidate();
//			return;
//		}
//	}
//
//	private Menupopup0 simpleMenupopup;
//
//	public Menupopup0 getContextMenu() {
//		return simpleMenupopup;
//	}
//
//	@SneakyThrows
//	protected void init() {
//
//		doOverlapped();
//
//		ZKS.PADDING0(this);
//
//		applyStylePos(this, getCurrentPosition());
//
//		setCLASS(scn());
//
//		addStyleTAG(".%s{position:fixed !important}", scn());
//
//		EnumSwitcher<WinPos> child = new EnumSwitcher<WinPos>(WinPos.class, null, this, false) {
//			@Override
//			protected void applyPosition(WinPos typeValue) {
//				applyStylePos(RightFormControl.this, typeValue, true);
//			}
//
//			@Override
//			public String getIcon() {
//				return super.getIcon();
//			}
//		};
//
//		appendChild(child);
//
//		simpleMenupopup = child.addToMenu;
//
//		getNameComOrCreate();//init
//	}
//
//	private Menuitem nameCom;
//
//	public Menuitem getNameComOrCreate() {
//		if (nameCom != null) {
//			return nameCom;
//		}
//		String sd3 = getModelSP().ppi().subdomain3();
//		this.nameCom = simpleMenupopup.addMenuitem(SYMJ.ARROW_RIGHT_SPEC + " " + sd3);
//		nameCom.setHref(sd3);
//		return nameCom;
//	}
}
