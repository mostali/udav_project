package zk_com.editable;

import org.zkoss.zk.ui.Component;
import zk_page.ZKJS;

import java.nio.file.Path;


public class EditableValueFilePrettyPrint extends EditableValueFile {


	public EditableValueFilePrettyPrint(String file) {
		super(file);
	}

	public EditableValueFilePrettyPrint(Path file) {
		super(file);
	}

	public EditableValueFilePrettyPrint(String file, Class<? extends Component> classCom) {
		super(file, classCom);
	}

	public EditableValueFilePrettyPrint(Path file, Class<? extends Component> classCom) {
		super(file, classCom);
	}

	@Override
	protected void onUpdatePrimaryText(String text) {
		super.onUpdatePrimaryText(text);
		ZKJS.evalPrettyPrint();
	}

	public static EditableValueFilePrettyPrint build(Path file) {
		return (EditableValueFilePrettyPrint) new EditableValueFilePrettyPrint(file).setDefaultDims();
	}

	public static EditableValueFilePrettyPrint build(Path file, Class form) {
		return (EditableValueFilePrettyPrint) new EditableValueFilePrettyPrint(file, form).setDefaultDims();
	}
}

