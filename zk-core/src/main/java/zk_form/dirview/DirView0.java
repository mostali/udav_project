package zk_form.dirview;

import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.str.sym.FD_ICON;
import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.core.ARR;
import mpu.func.FunctionV2;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Menupopup0;
import zk_com.editable.EditableValue;
import zk_form.notify.ZKI;
import zk_page.ZKCFinderExt;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DirView0 extends DirView {

	public DirView0(Path dir, boolean open) {
		super(dir, open);
	}

	public static @NotNull EditableValue newAddFileComponent(Path dir, Function<String, String> funcNewValue) {
		String value = SYMJ.PLUS;
		EditableValue newFile = new EditableValue(value) {
			@Override
			protected void onUpdatePrimaryText(String newValue) {
				if (value.equals(newValue)) {
					return;
				}
				super.onUpdatePrimaryText(SYMJ.PLUS);
				boolean isDir = newValue.endsWith("/");
				Path resolve = dir.resolve(IT.isDirname(newValue));
				IT.isDirOrFileNotExist(resolve);
				if (isDir) {
					UFS.MKDIR.mkdirIfNotExist(resolve);
				} else {
					UFS.MKFILE.createEmptyFileIfNotExist(resolve);
				}
				if (funcNewValue != null) {
					funcNewValue.apply(newValue);
				}
				ZKI.showMsgBottomRightFast_INFO("%s is append:%s", isDir ? "New Dir" : "New File", UF.fn(resolve, 2));
			}
		};
		newFile.setTooltip("Add 'filename.txt' or 'dirname/'");
		return newFile;
	}

	public static SerializableEventListener getEventOpenDirViewWithSimpleMenu(Path path) {
		return e -> DirView0.openWithSimpleMenu(path)._modal();
	}

	public static List<DirView0> findAll(Path path) {
		List<DirView0> allInPage0 = ZKCFinderExt.findAll_inPage0(DirView0.class, true, ARR.EMPTY_LIST);
		return allInPage0.stream().filter(d -> d.path().equals(path)).collect(Collectors.toList());
	}

	@Override
	protected void init() {
		super.init();

		applyStyle();
	}


	protected void applyStyle() {


	}


	public static DirView openWithSimpleMenu(Path dir, boolean... mkdirsIfNotExist) {
		UFS.MKDIR.createDirsOrSingleDirOrCheckExist(dir, mkdirsIfNotExist);
		FunctionV2<Menupopup0, Path> apllierDirMenu = (menupopup, chDir) -> DirViewMenu.applyMenuDir(menupopup, chDir);
		FunctionV2<Menupopup0, Path> apllierFileMenu = (menupopup, chFile) -> DirViewMenu.applyMenuFile(menupopup, chFile);
		DirView dirView = createDirView(dir, true);
		dirView.applierDirMenu(apllierDirMenu).applierFileMenu(apllierFileMenu);
//		Window window = dirView._modal()._title(dir.getFileName().toString())._closable(true)._showInWindow();
		Window window = dirView._modal()._title(FD_ICON.getFilename_or_Root_Home_Tmp_Icon(dir))._closable(true)._showInWindow();
		return dirView;
	}


	public static DirView createWithSimpleMenuAsForm(Path dir) {
		DirView dirView = createDirView(dir, true);
		return applyDefaultMenu(dirView);
	}

	public static @NotNull DirView applyDefaultMenu(DirView dirView) {
		FunctionV2<Menupopup0, Path> apllierDirMenu = (menupopup, chDir) -> DirViewMenu.applyMenuDir(menupopup, chDir);
		FunctionV2<Menupopup0, Path> apllierFileMenu = (menupopup, chFile) -> DirViewMenu.applyMenuFile(menupopup, chFile);
		dirView.applierDirMenu(apllierDirMenu).applierFileMenu(apllierFileMenu);
		return dirView;
	}

	public static DirView openWithSimpleMenuAsForm_asModal(Path dir) {
		IT.isDirExist(dir);
		DirView dirView = createWithSimpleMenuAsForm(dir);
		Window window = dirView._title(dir.getFileName().toString())._closable(true)._showInWindow();
		return dirView;
	}

	public static DirView createDirView(Path dir, boolean open) {
		DirView dirView = new DirView(dir, open) {
			@Override
			protected void onClickFdView(Event event, FdView fdView) {
				L.info("choiced:" + fdView.path());
			}
		};
		return dirView;
	}
}
