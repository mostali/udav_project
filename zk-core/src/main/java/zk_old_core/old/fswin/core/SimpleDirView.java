package zk_old_core.old.fswin.core;

import mpc.fs.UF;
import mpc.fs.UFS_BASE;
import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.func.FunctionV2;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Window;
import zk_com.base.Lb;
import zk_com.base_ctr.Menupopup0;
import zk_com.editable.EditableValue;
import zk_com.win.Win0;
import zk_form.notify.ZKI;
import zklogapp.ALM;

import java.nio.file.Path;
import java.util.function.Function;

public class SimpleDirView extends DirView {

	public SimpleDirView(Path dir, boolean open) {
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
					UFS_BASE.MKDIR.mkdirIfNotExist(resolve);
				} else {
					UFS_BASE.MKFILE.createEmptyFileIfNotExist(resolve);
				}
				if (funcNewValue != null) {
					funcNewValue.apply(newValue);
				}
				ZKI.infoBottomRightFast("%s is append:%s", isDir ? "New Dir" : "New File", UF.fn(resolve, 2));
			}
		};
		newFile.setTooltip("Add file or dir/");
		return newFile;
	}

	@Override
	protected void init() {
		super.init();

	}

	public static void openWithSimpleMenu(Path dir) {
		String title = UF.fn(dir, 2);
		FunctionV2<Menupopup0, Path> apllierDirMenu = (menupopup, chDir) -> ALM.applyLogDir(menupopup, chDir);
		FunctionV2<Menupopup0, Path> apllierFileMenu = (menupopup, chFile) -> ALM.applyLogFile(menupopup, chFile);
		DirView dirView = open(dir, true);
		dirView.applierDirMenu(apllierDirMenu).applierFileMenu(apllierFileMenu);
		Window window = dirView._popup()._sizable()._closable()._modal()._showInWindow();
		Caption caption = Win0.getCaptionOrCreate(window, true);
		caption.getChildren().clear();
		caption.appendChild(new Lb(title).bold(16));
		EditableValue newFile = newAddFileComponent(dir, (vl) -> {
			dirView.replaceWith(open(dir, true));
			return null;
		});
		caption.appendChild(newFile);
	}
}
