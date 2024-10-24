package zk_com.sun_editor;

import mpe.core.UBool;
import mpu.X;
import mpc.fs.path.IPath;
import mpc.fs.path.PathEntity;
import mpc.fs.UFS;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.PageCtrl;
import zk_com.base.Lb;
import zk_old_core.old.per_win.IPerWinStateRw;
import zk_old_core.old.per_win.PerWin;
import zk_os.sec.MatrixAccess;
import zk_os.sec.ROLE;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.ZkPage;
import zk_page.core.ISpCom;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SeWinOLD extends PerWin implements ISpCom, IPerState {

	public static final String VIEW_MAIN = "view.sew";
	public static final String DEFAULT_BGCOLOR = "#f1f1f1";

	public static void open(PageDirModel pageDirModel, Path fileContent) {
		if (!ROLE.hasEditorMin()) {
			return;
		}
		Path fileState = pageDirModel.getFileState(SeWinOLD.class, false);
		ZKC.appendChild(new SeWinOLD(fileState, fileContent));
	}

	public static void openOnLoadPage(PageDirModel pdm) {
		if (!ROLE.hasEditorMin()) {
			return;
		}
		IPerWinStateRw state = IPerWinStateRw.loadStateRw(pdm.getFileState(SeWinOLD.class, false));
		boolean has = UBool.isTrue_Bool_12_YesNo_PlusMinus(state.read(SeWinOLD.VIEW_MAIN, "false"));
		if (!has) {
			return;
		}
		String last = state.read(IPerWinStateRw.STATE.edit_form.name(), null);
		if (X.empty(last)) {
			return;
		}
		Path file = Paths.get(last);
		if (!UFS.existFile(file)) {
			return;
		}
		SeWinOLD.open(pdm, file);
	}

	@Override
	public MatrixAccess getMA() {
		return MatrixAccess.EDITOR_FULL;
	}

	final IPath pathFileHtml;

	public SeWinOLD(Path fileState, Path fileHtml) {
		super(fileState);
		pathFileHtml = PathEntity.of(fileHtml);
	}


	@Override
	protected void init() {
		super.init();


		SeTbxm seTbx = (SeTbxm) new SeTbxm(pathFileHtml.fPath()) {
			@Override
			public void onSave(Event event) {
				super.onSave(event);
				ZKR.rebuildPage();
			}
		}.saveble();
		seTbx.setHeight("100%");
		getStateRw().write(IPerWinStateRw.STATE.edit_form.name(), pathFileHtml.fPath().toString());
		getMainViewOrCreate().appendChild(seTbx);

		Component switcher = getDefaultReseterAndSwitcher(PageDirModel.get());
		getHeaderCaption().appendChild(switcher);

		getHeaderCaption().appendChild(new Lb(pathFileHtml.fName()));
		ZkPage.renderHeadRsrc_Form((PageCtrl) getFirstWindow().getPage(), seTbx);

		appendChild(getMainViewOrCreate());

//		setContentStyle("overflow:scroll");
//		setContentStyle("height:100%");
		getMainViewOrCreate().setHeight("100%");

//		ZkPage.addStyleTag(getFirstWindow().getPage(), ".%s > .z-window-content {overflow:scroll}", className);

	}

}
