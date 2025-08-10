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
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Menupopup0;
import zk_com.editable.EditableValue;
import zk_form.events.DefAction;
import zk_form.events.Grep_CfrmSerializableEventListener;
import zk_form.SLib;
import zk_form.notify.ZKI;
import zk_page.ZKR;
import zklogapp.ALI;
import zklogapp.AppLogProps;
import zklogapp.header.BottomHistoryPanel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class DirView0 extends DirView {

	public static Function<List<String>, Path> grepSuccesCallbackWriteResult = grepLines -> {
		String first = "/tmp/" + UUID.randomUUID() + GrepExecRq.EXT_GREP;
		RW.writeLines(Paths.get(first), grepLines);
		BottomHistoryPanel.addItem(first.toString());
		return Paths.get(first);
	};

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
					UFS_BASE.MKDIR.mkdirIfNotExist(resolve);
				} else {
					UFS_BASE.MKFILE.createEmptyFileIfNotExist(resolve);
				}
				if (funcNewValue != null) {
					funcNewValue.apply(newValue);
				}
				ZKI.showMsgBottomRightFast_INFO("%s is append:%s", isDir ? "New Dir" : "New File", UF.fn(resolve, 2));
			}
		};
		newFile.setTooltip("Add file or dir/");
		return newFile;
	}

	public static void applyMenuDir(Menupopup0 menupopup, Path path) {

//		menupopup.addMenuitem(ALI.DIR_UP + "UP", e -> DirView.findFirst().upDown(true).rerender());
//		menupopup.addMenuitem(ALI.DIR_UP + "DOWN", e -> Sys.openNautilus(path.toString()));
//		menupopup.addContextMenuSeparator();
		menupopup.addMI(ALI.OS_OPEN + "Open Dir", getEventOpenSimpleMenu_OS(path));
		menupopup.addMI(SYMJ.SEARCH_LUPA_RIGHT + " Search Files (Glob)", SLib.of(path.getParent()).toEventShowInModal());

		menupopup.add_______();
		menupopup.addMI_Href(Grep_CfrmSerializableEventListener.toMenuItemComponent(path, "", grepSuccesCallbackWriteResult));
		menupopup.add_______();
		menupopup.addMI_RenameFile_Cfrm(path.toString(), null, false);
		menupopup.addMI_RenameFile_Cfrm(path.toString(), null, true);

		DefAction successCalbackAction = e -> {
			findFirst().rerender();
			ZKI.showMsgBottomRightFast_INFO("Removed");
		};
		menupopup.addMI_DeleteFile(path.toString(), successCalbackAction);


	}

	public static void applyMenuFile(Menupopup0 menupopup, Path path) {
		boolean isArc = GEXT.ARC.isPath(path);
		String pathStr = path.toString();
		menupopup.addMI(ALI.DOWNLOAD + UF.fnWithSize(path), e -> ZKR.download(path));


		menupopup.add_______();
		menupopup.addMI(ALI.OS_OPEN + " Open in Code", e -> Sys.open_Code(path));
		menupopup.addMI(ALI.OS_OPEN + " Open Dir", getEventOpenSimpleMenu_OS(path));

		menupopup.add_______();

		menupopup.addMI_Href(Grep_CfrmSerializableEventListener.toMenuItemComponent(path, "", grepSuccesCallbackWriteResult));

		menupopup.add_______();

		if (isArc) {
			String encoding = AppLogProps.APR_UNZIP_ENCODING.getValueOrDefault("");
			menupopup.addMI(ALI.UNZIP + " Unzip file (overwrite)", (e) -> {
				String msg = UnZipExecEE.unzip(pathStr, true, encoding);
				ZKI.log(msg);
				ZKI.showMsgBottomRightFast_INFO(msg);
				findFirst().rerender();
			});
			menupopup.addMI(ALI.UNZIP + " Unzip file (skip existed)", (e) -> {
				String msg = UnZipExecEE.unzip(pathStr, false, encoding);
				ZKI.log(msg);
				ZKI.showMsgBottomRightFast_INFO(msg);
				findFirst().rerender();
			});

			menupopup.add_______();

		}

		menupopup.addMI_RenameFile_Cfrm(pathStr, null);
		menupopup.addMI_DeleteFile(pathStr, null);

	}

	public static SerializableEventListener getEventOpenSimpleMenu_OS(Path path) {
		return e -> Sys.open_Nautilus(path);
	}

	public static SerializableEventListener getEventOpenSimpleMenu_Terminal(Path path) {
		return e -> Sys.open_Terminal(path);
	}

	public static SerializableEventListener getEventOpenDirViewWithSimpleMenu(Path path) {
		return e -> DirView0.openWithSimpleMenu(path)._modal();
	}

	@Override
	protected void init() {
		super.init();

		applyStyle();
	}


	protected void applyStyle() {


	}


	public static DirView openWithSimpleMenu(Path dir) {
		FunctionV2<Menupopup0, Path> apllierDirMenu = (menupopup, chDir) -> applyMenuDir(menupopup, chDir);
		FunctionV2<Menupopup0, Path> apllierFileMenu = (menupopup, chFile) -> applyMenuFile(menupopup, chFile);
		DirView dirView = open(dir, true);
		dirView.applierDirMenu(apllierDirMenu).applierFileMenu(apllierFileMenu);
		Window window = dirView._modal()._title(dir.getFileName().toString())._closable(true)._showInWindow();
		return dirView;
	}

	public static DirView openWithSimpleMenuAsForm(Path dir) {
		FunctionV2<Menupopup0, Path> apllierDirMenu = (menupopup, chDir) -> applyMenuDir(menupopup, chDir);
		FunctionV2<Menupopup0, Path> apllierFileMenu = (menupopup, chFile) -> applyMenuFile(menupopup, chFile);
		DirView dirView = open(dir, true);
		dirView.applierDirMenu(apllierDirMenu).applierFileMenu(apllierFileMenu);
		Window window = dirView._title(dir.getFileName().toString())._closable(true)._showInWindow();
		return dirView;
	}
}
