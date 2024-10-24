package zk_old_core.old.mwin;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpe.core.UBool;
import mpu.X;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.*;
import mpc.fs.fd.EFT;
import mpu.str.STR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Window;
import zk_com.base.Dd;
import zk_com.base_ctr.Span0;
import zk_old_core.old.per_win.IPerWinStateRw;
import zk_old_core.old.per_win.PerWin;
import zk_old_core.std.AbsVF;
import zk_old_core.AppCoreStateOld;
import zk_os.sec.ROLE;
import zk_page.ZKC;
import zk_page.ZkPage;
import zk_page.core.ISpCom;
import zk_old_core.mdl.PageDirModel;
import zk_page.ZKCFinder;
import zk_old_core.app_ds.struct.PageDirDS;
import zk_os.sec.MatrixAccess;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class MWin extends PerWin implements ISpCom {

	public static final String VIEW_MAIN = "view.mw";
	public static final String DEFAULT_BGCOLOR = "#e0f2ff";


	public static final String DEF_WIDTH_DD = "100px";
	public static final String VIEW_HEAD = "view.head";
	public static final String VIEW_PAGE = "view.page";
	public static final String VIEW_FORM = "view.form";
	public static final String VIEW_TRM = "view.trm";
	public static final String VIEW_FB = "view.fb";
	public static final String VIEW_MW_MANY = "view.mw.many";


	private static List<String> getAllMwProps() {
		return getAllViewProps(MWin.class);
	}

	public static List<MWin> findAll(List<MWin>... defRq) {
		return findAll(MWin.class, defRq);
	}

	public static MWin findFirstOrOpen(boolean renderHeadRsrc) {
		List<MWin> all = MWin.findAll(null);
		return X.empty(all) ? MWin.open(renderHeadRsrc) : all.get(0);
	}

	public static void openOnLoadPage(PageDirModel pageDirModel) {
		if (!ROLE.hasEditorMin()) {
			return;
		}
		IPerWinStateRw iPerWinStateRw = IPerWinStateRw.loadStateRw(pageDirModel.getFileState(MWin.class, false));
		boolean has = UBool.isTrue_Bool_12_YesNo_PlusMinus(iPerWinStateRw.read(MWin.VIEW_MAIN, "false"));
		if (!has) {
			return;
		}
		MWin mWin = MWin.open(pageDirModel.path());
		mWin.showStartContent();
	}

	ChoicerForm getComChoicerForm() {
		return findChild(ChoicerForm.class).get(0);
	}

	public Path getFormComPath(Path... defRq) {
		String formComSelectedDdItem = getComChoicerForm().ddFormComs.getFormComSelectedDdItem();
		if (X.notEmpty(formComSelectedDdItem)) {
			return getPageDirModel().path().resolve(formComSelectedDdItem);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set form com"), defRq);
	}

	public Path getPageFormPath(Path... defRq) {
		String formPageFormSelectedDdItem = getComChoicerForm().ddPageForms.getPageFormSelectedDdItem();
		if (X.notEmpty(formPageFormSelectedDdItem)) {
			String pageFormFile = ChoicerForm.extractPageFormFile(formPageFormSelectedDdItem);
			return getPageDirModel().path().resolve(pageFormFile);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set page form"), defRq);
	}


	public static void openForm(AbsVF target) {
		openForm(target, true);
	}

	public static void openForm(AbsVF target, boolean renderHeadRsrc) {
		MWin mWin = MWin.findFirstOrOpen(renderHeadRsrc);

		ChoicerForm choicerForm = mWin.getComChoicerForm();

		choicerForm.ddFormComs.clearDdItems(true);

		choicerForm.ddPageForms.setForm(target);

		switch (target.ctype()) {
			case HTML:
				Path relativize = mWin.getPageDirModel().path().relativize(target.getRootChilds().get(0));
				choicerForm.ddFormComs.setFormComSelectedDdItem(relativize.toString());
				mWin.showContent(target.getRootChilds().get(0));
				break;
			default:
				mWin.showContent(target.getPathRootProps());
				break;

		}
	}

	@Override
	public MatrixAccess getMA() {
		return MatrixAccess.EDITOR_FULL;
	}

	@SneakyThrows
	public static MWin open(boolean... renderHeadRsrc) {
		return open((PageDirModel.get().path()), renderHeadRsrc);
	}

	public static MWin open(Path pageDir, boolean... renderHeadRsrc) {
		MWin child = ofPage(pageDir);
		Window w = ZKC.getFirstWindow();
		w.appendChild(child);
		if (ARG.isDefEqTrue(renderHeadRsrc)) {
			ZkPage.renderHeadRsrc_Form((PageCtrl) w.getPage(), child);
		}
		return child;
	}

	@SneakyThrows
	public static MWin ofPage() {
		return ofPage(PageDirModel.get().path());
	}

	@SneakyThrows
	public static MWin ofPage(Path pageDir) {
		Path stateFile = PageDirModel.of(pageDir).getFileState(MWin.class, false);
		UFS_BASE.MKFILE.createFileIfNotExist(stateFile, false);
		return new MWin(stateFile);
	}

	public MWin(Path fileState) {
		super(fileState);
	}


	public <T> List<T> findChild(Class<T> type, List<T>... defRq) {
//		return ZKComFinder.findCom((Component) this, (Class) type, true, defRq);
		return ZKCFinder.findAllFromParent((Component) this, (Class) type, true, (List[]) defRq);
	}

	@Override
	protected void init() {
		super.init();

//		setCLASS(MWin.class.getSimpleName());

		IPerWinStateRw stateRw = getStateRw();


		Caption headerCaption = getHeaderCaption();
		if (UBool.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_HEAD, "false"))) {
			headerCaption.appendChild(new ChoicerHead(this));
		}

		if (UBool.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_PAGE, "false"))) {
			headerCaption.appendChild(new ChoicerPage(this));
		}

		if (UBool.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_FORM, "false"))) {
			headerCaption.appendChild(new ChoicerForm(this));
		}

		if (UBool.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_TRM, "false"))) {
			headerCaption.appendChild(new MwInnerTrm(this));
		}
		if (UBool.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_FB, "false"))) {
			headerCaption.appendChild(new ChoicerFormBuilder(this));
		}
	}

	@Override
	public Component getHeaderCaptionContainer() {

		String fileStateName = AppCoreStateOld.fn(MWin.class, false);
		IPerWinStateRw stateRw = getStateRw();

		PageDirModel pageDirModel = getPageDirModel();
		Path metaDir = PageDirDS.meta.getPath(pageDirModel.path());
		Collection<Path> allMWprops = UDIR.ls(metaDir, EFT.FILE, null, path -> path.getFileName().toString().endsWith(fileStateName));
		allMWprops = allMWprops.stream().map(p -> Paths.get(UF.getNameWoExtQk(p))).collect(Collectors.toList());
//		if (allMWprops.size() < 2) {
//			return new Lb(PageDirDS.FN_MWIN_PROPS);
//		}
		SerializableEventListener onOkEvent = event -> {
			showContent(stateAsPath());
		};

		Component switcher = getDefaultReseterAndSwitcher(pageDirModel);
		getHeaderCaption().appendChild(switcher);

		if (UBool.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_MW_MANY, "false"))) {
			Dd ddMWin = new Dd(UF.getNameWoExt(fileStateName), STR.toStringList(allMWprops));
			ddMWin.setWidth(MWin.DEF_WIDTH_DD);
			ddMWin.onOK(onOkEvent);
			return Span0.of(switcher, ddMWin);
		} else {
			return Span0.of(switcher);
		}
	}

	@Override
	public void showContent(Path pathFile) {
		super.showContent(pathFile);
		getStateRw().write(IPerWinStateRw.STATE.edit_form, pathFile.toString());
	}

	public void showStartContent() {
		Path path = getLastEditFormPath(null);
		if (path != null && Files.isRegularFile(path)) {
			super.showContent(path);
		} else {
			showMainProps();
		}
	}

	public Path getLastEditFormPath(Path... defRq) {
		String path = getStateRw().read(IPerWinStateRw.STATE.edit_form, null);
		if (path != null) {
			return Paths.get(path);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Last path form not found"), defRq);
	}


//
//	public Path getLastEditFormComPath(Path... defRq) {
//		String path = getStateRw().read(IDimsStateRw.STATE.edit_form_com, null);
//		if (path != null) {
//			return Paths.get(path);
//		}
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("Last path form-com not found"), defRq);
//	}
}
