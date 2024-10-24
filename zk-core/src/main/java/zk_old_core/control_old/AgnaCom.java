package zk_old_core.control_old;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.X;
import mpc.str.sym.SYMJ;
import mpe.state_rw.IMapStateRw;
import org.zkoss.zul.Menuitem;
import zk_com.base_ext.EnumSwitcher;
import zk_com.base_ctr.Menupopup0;
import zk_com.win.Win0;
import zk_com.win.WinPos;
import zk_old_core.AppCoreStateOld;
import zk_os.sec.MatrixAccess;
import zk_page.ZKCFinder;
import zk_page.ZKS;
import zk_page.core.ISpCom;

import java.util.List;

public class AgnaCom extends Win0 implements ISpCom {

	public static final String TM_POS = "pos";

	public static AgnaCom findFirst(AgnaCom... defRq) {
		return ZKCFinder.findFirstIn_Page(AgnaCom.class, true, defRq);
	}

	public static void resetState() {
		WinPos defPos = WinPos.TC;
		getState().write(TM_POS, defPos.name());
		List<AgnaCom> com = ZKCFinder.rootsByClass(AgnaCom.class, true);
		if (!X.empty(com)) {
			applyStylePos(com.get(0), defPos, false);//reset
		}
	}

	@Override
	public MatrixAccess getMA() {
		return MatrixAccess.EDITOR_FULL;
	}

	static IMapStateRw getState() {
		return AppCoreStateOld.getStateGlobal(AgnaCom.class, true);
	}

	WinPos getCurrentPosition() {
		return getState().readAs(TM_POS, WinPos.class, WinPos.TC);
	}

	private static void applyStylePos(AgnaCom ctrlMenu, WinPos tmPos, boolean... writeState) {
		if (ARG.isDefEqTrue(writeState)) {
			getState().write(TM_POS, tmPos.name());
		}
		if (ctrlMenu instanceof Win0) {
			ctrlMenu.setPosition(tmPos.getPattern());
			ctrlMenu.invalidate();
			return;
		}
	}

	private Menupopup0 simpleMenupopup;

	public Menupopup0 getContextMenu() {
		return simpleMenupopup;
	}

	@SneakyThrows
	protected void init() {

		doOverlapped();

		ZKS.PADDING0(this);

		applyStylePos(this, getCurrentPosition());

		setCLASS(scn());

		addStyleTAG(".%s{position:fixed !important}", scn());

		EnumSwitcher<WinPos> child = new EnumSwitcher<WinPos>(WinPos.class, null, this, false) {
			@Override
			protected void applyPosition(WinPos typeValue) {
				applyStylePos(AgnaCom.this, typeValue, true);
			}

			@Override
			public String getIcon() {
				return super.getIcon();
			}
		};

		appendChild(child);

		simpleMenupopup = child.addToMenu;

		getNameComOrCreate();//init
	}

	private Menuitem nameCom;

	public Menuitem getNameComOrCreate() {
		if (nameCom != null) {
			return nameCom;
		}
		String sd3 = getModelSP().ppi().subdomain3();
		this.nameCom = simpleMenupopup.addMenuitem(SYMJ.ARROW_RIGHT_SPEC + " " + sd3);
		nameCom.setHref(sd3);
		return nameCom;
	}
}
