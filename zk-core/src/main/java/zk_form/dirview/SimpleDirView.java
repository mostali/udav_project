package zk_form.dirview;

import mpc.fs.UF;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import mpe.rt_exec.GrepExecRq;
import mpe.rt_exec.UnZipExecEE;
import mpu.IT;
import mpu.Sys;
import mpu.core.RW;
import mpu.func.FunctionV2;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Menupopup0;
import zk_com.editable.EditableValue;
import zk_form.events.DefAction;
import zk_form.events.Grep_CfrmSerializableEventListener;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Log;
import zk_page.ZKR;
import zklogapp.ALI;
import zklogapp.AppLogProps;
import zklogapp.header.BottomHistoryPanel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class SimpleDirView extends DirView {

	public static Function<List<String>, Path> grepSuccesCallbackWriteResult = grepLines -> {
		String first = "/tmp/" + UUID.randomUUID() + GrepExecRq.EXT_GREP;
		RW.writeLines(Paths.get(first), grepLines);
		BottomHistoryPanel.addItem(first.toString());
		return Paths.get(first);
	};

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

	public static void applyMenuDir(Menupopup0 menupopup, Path path) {

//		menupopup.addMenuitem(ALI.DIR_UP + "UP", e -> DirView.findFirst().upDown(true).rerender());
//		menupopup.addMenuitem(ALI.DIR_UP + "DOWN", e -> Sys.openNautilus(path.toString()));
//		menupopup.addContextMenuSeparator();
		menupopup.addMenuitem(ALI.OS_OPEN + "Open Dir", e -> Sys.open_Nautilus(path.toString()));
		menupopup.add_______();
		menupopup.addMenuitem(Grep_CfrmSerializableEventListener.toMenuItemComponent(path, "", grepSuccesCallbackWriteResult));
		menupopup.add_______();
		menupopup.addMenuitem_RenameFile_Cfrm(path.toString(), null, false);
		menupopup.addMenuitem_RenameFile_Cfrm(path.toString(), null, true);

		DefAction successCalbackAction = e -> {
			findFirst().rerender();
			ZKI.infoBottomRightFast("Removed");
		};
		menupopup.addMenuitem_RemoveFile(path.toString(), successCalbackAction);


	}

	public static void applyMenuFile(Menupopup0 menupopup, Path path) {
		boolean isArc = GEXT.ARC.isPath(path);
		String pathStr = path.toString();
		menupopup.addMenuitem(ALI.DOWNLOAD + UF.fnWithSize(path), e -> ZKR.download(path));


		menupopup.add_______();
		menupopup.addMenuitem(ALI.OS_OPEN + "Open in Code", e -> Sys.open_Code(path));
		menupopup.addMenuitem(ALI.OS_OPEN + "Open Dir", e -> Sys.open_Nautilus(UF.fPr(path)));

		menupopup.add_______();

		menupopup.addMenuitem(Grep_CfrmSerializableEventListener.toMenuItemComponent(path, "", grepSuccesCallbackWriteResult));

		menupopup.add_______();

		if (isArc) {
			String encoding = AppLogProps.APR_UNZIP_ENCODING.getValueOrDefault("");
			menupopup.addMenuitem(ALI.UNZIP + "Unzip file (overwrite)", (e) -> {
				String msg = UnZipExecEE.unzip(pathStr, true, encoding);
				ZKI_Log.log(msg);
				ZKI.infoBottomRightFast(msg);
				findFirst().rerender();
			});
			menupopup.addMenuitem(ALI.UNZIP + "Unzip file (skip existed)", (e) -> {
				String msg = UnZipExecEE.unzip(pathStr, false, encoding);
				ZKI_Log.log(msg);
				ZKI.infoBottomRightFast(msg);
				findFirst().rerender();
			});

			menupopup.add_______();

		}

		menupopup.addMenuitem_RenameFile_Cfrm(pathStr, null);
		menupopup.addMenuitem_RemoveFile(pathStr, null);

	}

	@Override
	protected void init() {
		super.init();

	}

	public static DirView openWithSimpleMenu(Path dir) {
		String title = UF.fn(dir, 2);
		FunctionV2<Menupopup0, Path> apllierDirMenu = (menupopup, chDir) -> applyMenuDir(menupopup, chDir);
		FunctionV2<Menupopup0, Path> apllierFileMenu = (menupopup, chFile) -> applyMenuFile(menupopup, chFile);
		DirView dirView = open(dir, true);
		dirView.applierDirMenu(apllierDirMenu).applierFileMenu(apllierFileMenu);
//		Window window = dirView._popup()._sizable()._closable()._modal()._showInWindow();
		Window window = dirView._modal(true)._title(dir.getFileName().toString())._popup()._closable(true)._showInWindow();
//		Caption caption = Win0.getCap0OrCreate(window, true);
//		caption.getChildren().clear();
//		caption.appendChild((Component) new Lb(title).font_bold_nice(AppNotesTheme.FONT_SIZE_WIDGET_HEADER));
//		EditableValue newFile = newAddFileComponent(dir, (vl) -> {
//			dirView.replaceWith(open(dir, true));
//			return null;
//		});
//		caption.appendChild(newFile);
		return dirView;
	}
}