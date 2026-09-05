package zk_com.editable;

import mpc.fs.ext.EXT;
import mpu.core.RW;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Html;
import zk_com.base_ctr.Div0;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class EditableValueFile extends EditableValue {

	final String _file;

	private transient Path path;


//	public static Div0 buildComs_EditView_ForDir(Path path) {
//		return buildComs_EditView(DirModel.getAllPaths(path));
//	}

	public static Div0 buildComs_EditView(List<Path> paths) {
		Div0 divView = new Div0();
		for (Path f : paths) {
			switch (EXT.of(f)) {
				case AVI:
				case JS:
				case CSS:
				case MD:
				case LOG:
				case TXT:
				case XML:
				case ZUL:
				case HTML:
				case JSON:
				case PROPS:
				case PROPERTIES:
					EditableValueFile editValue = buildCom_EditView(f);
					divView.appendChild(editValue);
					break;
				default:
					continue;
			}
		}
		return divView;
	}

	@NotNull
	private static EditableValueFile buildCom_EditView(Path f) {
		EditableValueFile editValue = (EditableValueFile) new EditableValueFile(f).isLabelVew(false).setDefaultDims();
//			SimplePopupMenu popup = new SimplePopupMenu(editValue);
//			popup.appendChild(new Menuitem("sd"));
//			popup.addContextMenuSeparator();
//			poppMenu.addContextMenuItem(new );
//			editValue.setTooltip(popup);
		editValue.setTooltiptext(f.getFileName().toString());
		return editValue;
	}

//	public static Div0 buildCom_EditViewWithRename_ForDir(Path path) {
//		return buildComs_EditViewWithRename(DirModel.getAllPaths(path));
//	}

	@NotNull
	private static Div0 buildCom_EditViewWithRename(Path f) {
		RenameFileTextbox renameFileCom = RenameFileTextbox.buildCom(f);
		EditableValueFile editValueCom = buildCom_EditView(f);
		return Div0.of(renameFileCom, editValueCom);
	}

	public static Div0 buildComs_EditViewWithRename(List<Path> paths) {
		Div0 divView = new Div0();
		for (Path f : paths) {
			Div0 child = buildCom_EditViewWithRename(f);
			divView.appendChild(child);
		}
		return divView;
	}

	public Path file() {
		return path == null ? path = Paths.get(_file) : path;
	}

	public EditableValueFile(String file) {
		this(Paths.get(file));
	}

	public EditableValueFile(Path file) {
		this(file, Html.class);
	}

	public EditableValueFile(String file, Class<? extends Component> classCom) {
		this(Paths.get(file), classCom);
	}

	public EditableValueFile(Path file, Class<? extends Component> classCom) {
		super(RW.readString(file), classCom);
		this.path = file;
		this._file = file.toString();
	}

	@Override
	protected void onUpdatePrimaryText(String text) {
		super.onUpdatePrimaryText(text);
		RW.write(file(), text);
	}
}

