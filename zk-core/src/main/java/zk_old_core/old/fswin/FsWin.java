package zk_old_core.old.fswin;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpe.core.UBool;
import mpc.env.Env;
import mpc.exception.FIllegalStateException;
import mpc.exception.NotifyMessageRtException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.*;
import mpc.fs.fd.EFT;
import mpc.rfl.RFL;
import mpu.str.STR;
import mpc.str.condition.StringConditionType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Window;
import zk_com.base.*;
import zk_com.base_ctr.Span0;
import zk_page.ZKC;
import zk_page.ZkPage;
import zk_old_core.old.fswin.core.DirView;
import zk_old_core.old.fswin.core.FdView;
import zk_old_core.old.per_win.IPerWinStateRw;
import zk_old_core.old.per_win.PerWin;
import zk_old_core.old.mwin.MWin;
import zk_old_core.sd.core.SdRsrc;
import zk_os.sec.ROLE;
import zk_page.core.ISpCom;
import zk_os.sec.MatrixAccess;
import zk_old_core.app_ds.struct.PageDirDS;
import zk_page.core.PagePathInfo;
import zk_page.core.SpVM;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FsWin extends PerWin implements ISpCom {

	public static final String DEF_WIDTH_DD = "220px";
	public static final String VIEW_MAIN = "view.fsw";
	public static final String VIEW_FSW_MANY = "view.fsw.many";
	public static final String DEFAULT_BGCOLOR = "#009688";

	public static void openOnLoadPage(PageDirModel pdm) {
		if (!ROLE.hasEditorMin()) {
			return;
		}
		boolean has = UBool.isTrue_Bool_12_YesNo_PlusMinus(IPerWinStateRw.loadStateRw(pdm.getFileStateFs()).read(FsWin.VIEW_MAIN, "false"));
		if (!has) {
			return;
		}
		FsWin.openPageDir(pdm, true);
	}

	private static List<String> getAllFswProps() {
		return RFL.fieldValuesSt(FsWin.class, String.class, StringConditionType.STARTS.buildCondition("VIEW_"), false);
	}

	public static List<FsWin> findAll(List<FsWin>... defRq) {
		return findAll(FsWin.class, defRq);
	}

	public static void openStdLocation(String location, boolean renderHeadRsrc) {
		SdRsrc.LocRsrc locRsrc = SdRsrc.LocRsrc.ofShortCmd(location, null);
		if (locRsrc == null) {
			throw NotifyMessageRtException.LEVEL.RED.I("What is location '%s'?", location);
		}

		PageDirModel pdm = PageDirModel.get(null);
		if (pdm == null) {
			throw NotifyMessageRtException.LEVEL.RED.I("Page model not allowed for location '%s'", location);
		}

		switch (locRsrc) {

		}

		Path openPath = null;

		switch (locRsrc) {
			case PAGE_ASSETS:
			case PAGE_UPLOADS: {
				openPath = locRsrc.getParentOfStdLocationForPageOrSd(pdm.path());
				break;
			}
			case SD_ASSETS:
			case SD_UPLOADS:
				SpVM spVM = SpVM.get(null);
				if (spVM == null) {
					throw new FIllegalStateException("SpVM not found for page:" + PagePathInfo.current());
				}
				//XZ TODO - use ppi without spVM
				openPath = locRsrc.getParentOfStdLocation(spVM.ppi());
				break;

			case SITE_ASSETS:
			case SITE_UPLOADS:
				if (!MatrixAccess.ADMIN_FULL.hasAccess()) {
					return;
				}
				openPath = locRsrc.getParentOfStdLocation((PagePathInfo) null);
				break;
			default:
				throw new WhatIsTypeException(locRsrc);
		}

		openNoSecurity(pdm.getFileStateFs(), openPath, renderHeadRsrc);

	}

	public static boolean openPageDir(PageDirModel pageDirModel, boolean renderHeadRsrc) {
		return openNoSecurity(pageDirModel.getFileStateFs(), pageDirModel.path(), renderHeadRsrc);
	}

	public static boolean openRPA(boolean renderHeadRsrc) {
		if (!MatrixAccess.ADMIN_FULL.hasAccess()) {
			return false;
		}
		return openNoSecurity(PageDirModel.get().getFileStateFs(), Env.RPA, renderHeadRsrc);
	}

	public static boolean openNoSecurity(Path openPath) {
		return openNoSecurity(PageDirModel.get().getFileStateFs(), openPath, true);
	}

	public static boolean openNoSecurity(Path fileState, Path openPath, boolean... renderHeadRsrc) {
		FsWin fsWin = FsWin.ofState(fileState, openPath);
		Window window = ZKC.getFirstWindow();
		fsWin.appendTo(window);
		if (ARG.isDefEqTrue(renderHeadRsrc)) {
			ZkPage.renderHeadRsrc_Form((PageCtrl) window.getPage(), fsWin);
		}
		return true;
	}

//	public static void reopen() {
//		FsWin fsWin = ZKComFinder.findCom(FsWin.class, false).get(0);
//		fsWin.detach();
//		FsWin.open(false);
//	}


	@Override
	public MatrixAccess getMA() {
		return MatrixAccess.EDITOR_FULL;
	}

//	private static Path getPageFsFileState(Path pageDir) {
//		return PageDirDS.meta.getPathFile_FsWin(pageDir);
//	}

	@SneakyThrows
	public static FsWin ofState(Path stateFile, Path openDir) {
		return new FsWin(stateFile, openDir);
	}

	private final String dirPathStr;
	private transient Path dirPath;

	public Path dirPath() {
		return dirPath == null ? (dirPath = Paths.get(dirPathStr)) : dirPath;
	}

	public String dirName() {
		return dirPath().getFileName().toString();
	}

	public FsWin(Path fileState, Path openDir) {
		super(fileState);
		this.dirPathStr = openDir.toString();
		this.dirPath = openDir;
	}


	@Override
	protected void init() {
		super.init();

//		if (U.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_HEAD, "false"))) {
//			getHeaderCaption().appendChild(new ChoicerHead(this));
//		}
//
//		if (U.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_PAGE, "false"))) {
//			getHeaderCaption().appendChild(new ChoicerPage(this));
//		}
//
//		if (U.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_FORM, "false"))) {
//			getHeaderCaption().appendChild(new ChoicerForm(this));
//		}
//
//		if (U.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_TRM, "false"))) {
//			getHeaderCaption().appendChild(new InnerTrm(this));
//		}
//		if (U.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_FB, "false"))) {
//			getHeaderCaption().appendChild(new ChoicerFormBuilder(this));
//		}

		Caption headerCaption = getHeaderCaption();
		headerCaption.appendChild(getChoicedFd());
		headerCaption.appendChild(getFsCmdTrm());

		DirView dirView = new DirView(dirPath(), true) {
			@Override
			protected void onClickFdView(Event event, FdView fdView) {
				getChoicedFd().setPathFd(fdView.path());
			}
		};

		getMainViewOrCreate().appendChild(dirView);

		appendChild(getMainViewOrCreate());

		HtmlBasedComponent parent = (HtmlBasedComponent) getMainViewOrCreate().getParent();

//		String className = getClass().getSimpleName();
//		setCLASS(className);

		setContentStyle("overflow:scroll");

//		ZkPage.addStyleTag(getFirstWindow().getPage(), ".%s > .z-window-content {overflow:scroll}", className);

	}

	ChoicedFd choicedFd = null;

	public ChoicedFd getChoicedFd() {
		return choicedFd == null ? choicedFd = new ChoicedFd(this) : choicedFd;
	}

	FsInnerTrm fsInnerTrm = null;

	public FsInnerTrm getFsCmdTrm() {
		return fsInnerTrm == null ? fsInnerTrm = new FsInnerTrm(this) : fsInnerTrm;
	}


	@Override
	public Component getHeaderCaptionContainer() {

		IPerWinStateRw stateRw = getStateRw();

		PageDirModel pageDirModel = getPageDirModel(null);

		String fileStateName = pageDirModel.getFileStateFs().getFileName().toString();

		Path metaDir = PageDirDS.meta.getPath(pageDirModel == null ? stateAsPath() : pageDirModel.path());

		Collection<Path> allMWprops = UDIR.ls(metaDir, EFT.FILE, null, path -> path.getFileName().toString().endsWith(fileStateName), Collections.EMPTY_LIST);
		allMWprops = allMWprops.stream().map(p -> Paths.get(UF.getNameWoExtQk(p))).collect(Collectors.toList());
		SerializableEventListener onOkEvent = event -> {
			showContent(stateAsPath());
		};

		Component switcher = getDefaultReseterAndSwitcher(pageDirModel);
		getHeaderCaption().appendChild(switcher);

		if (UBool.isTrue_Bool_12_YesNo_PlusMinus(stateRw.read(VIEW_FSW_MANY, "false"))) {
			Dd ddFsWin = new Dd(UF.getNameWoExt(fileStateName), STR.toStringList(allMWprops));
			ddFsWin.setWidth(MWin.DEF_WIDTH_DD);
			ddFsWin.onOK(onOkEvent);
			return Span0.of(switcher, ddFsWin);
		} else {
			return Span0.of(switcher);
		}

	}

}
