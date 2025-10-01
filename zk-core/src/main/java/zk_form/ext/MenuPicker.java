package zk_form.ext;

import mpc.fs.UFS;
import mpu.X;
import mpu.core.ARRi;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import udav_net.apis.zznote.ItemPath;
import zk_com.base.Bt;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0M;
import zk_com.base_ext.EscTbx;
import zk_notes.AxnTheme;
import zk_notes.leftmenu.LeftMenu;
import zk_notes.leftmenu.LmMenuPicker;
import zk_os.coms.AFC;
import zk_os.core.Sdn;
import zk_page.ZKS;

import java.util.Collection;
import java.util.List;

public class MenuPicker<T> extends ItemsPicker<T> {

	public MenuPicker(Collection<CharSequence> items) {
		super(items);
	}

	protected static void applyStyle(MenuPicker parent, Div0M menuPanel) {

//				super.applyStyle(modalCom);

		ZKS.ABSOLUTE(parent);
		ZKS.INLINE_BLOCK(menuPanel);
		ZKS.ZINDEX(parent, AxnTheme.ZI_MENU);
		ZKS.LEFT(parent, 20);
		ZKS.TOP(parent, 100);

//				ZKS.BORDER_GRAY(modalCom);

		ZKS.BGCOLOR(menuPanel, "#f8f8ff");
		ZKS.OPACITY(menuPanel, 0.96);

		ZKS.PADDING(menuPanel, "20pt");

	}

	public static LmMenuPicker ofAllForms(String sd3, String page) {
		List<String> fileNames = UFS.toFileNames(AFC.FORMS.DIR_FORMS_LS_CLEAN(sd3, page));
		return new LmMenuPicker(Sdn.of(sd3, page), ItemPath.filterNoIndexPath(fileNames), LeftMenu.SpaceType.NODES);
	}

	public static LmMenuPicker ofAllPages(String sd3) {
		List<String> fileNames = UFS.toFileNames(AFC.PAGES.DIR_PAGES_LS_CLEAN(sd3));
		return new LmMenuPicker(Sdn.of(sd3, ItemPath.PAGE_INDEX_ALIAS), ItemPath.filterNoIndexPath(fileNames), LeftMenu.SpaceType.PAGES);
	}

	public static LmMenuPicker ofAllSd3() {
		List<String> fileNames = UFS.toFileNames(AFC.PLANES.DIR_PLANES_LS_CLEAN(false));
		return new LmMenuPicker(Sdn.of(ItemPath.SD3_INDEX_ALIAS, ItemPath.PAGE_INDEX_ALIAS), ItemPath.filterNoIndexPath(fileNames), LeftMenu.SpaceType.SPACES);
	}

	@Override
	public void onHappensClickItems(Event event, Collection<T> item) {
//				super.onHappensClosePciker();
		X.p("onHappensClickItems:" + ARRi.first(item));
	}

	protected boolean withCloseButton = false;

	@Override
	protected Bt getCloseBt() {
		return withCloseButton ? super.getCloseBt() : null;
	}

//	String sclass = STR.randstr(6, 6);

	@Override
	protected void init() {
		super.init();

//		super.appendChildStyle(".%s { width:100px; border:1px solid silver; }", sclass);
	}

	@Override
	protected void applyStyle(Div0M menuPanel) {
//		menuPanel.addSclass(sclass);
		ZKS.ABSOLUTE(this);

		ZKS.INLINE_BLOCK(menuPanel);
		ZKS.ZINDEX(this, AxnTheme.ZI_MENU);
		ZKS.LEFT(this, 0);
		ZKS.TOP(this, 0);

		ZKS.BORDER_GRAY(menuPanel);
	}

	@Override
	protected void applyStyleForItem(Ln ln) {
//		ln.addSclass(sclass);
//		super.applyStyleForItem(ln);
//		ZKS.BLOCK(ln);
//		ZKS.WIDTH(ln,);
//		ZKS.BGCOLOR(ln, UColorTheme.GRAY[0]);
//		ZKS.MARGIN(ln, "100px");
//		ZKS.PADDING(ln, "50px");
	}


	@Override
	public void onHappensClosePciker() {
//				super.onHappensClosePciker();
	}

	@Override
	public EscTbx appendClosableByEsc(Component... closeIT) {
		return null;
	}

}
