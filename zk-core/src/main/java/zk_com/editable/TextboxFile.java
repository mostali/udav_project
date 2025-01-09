package zk_com.editable;

import lombok.Getter;
import mpu.IT;
import mpc.fs.path.IPath;
import mpc.fs.path.PathEntity;
import mpu.core.RW;
import mpc.exception.NotifyMessageRtException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Label;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.XulElement;
import mpu.core.ARG;
import mpc.exception.WhatIsTypeException;
import zk_com.core.IZComExt;
import zk_page.ZKC;
import zk_form.notify.ZKI_Window;
import zk_page.events.ZKE;

import java.nio.file.Path;
import java.nio.file.Paths;

//https://stackoverflow.com/questions/17398202/zk-ctrl-key-or-hot-key-creating-issue-with-diffrent-browsers
//https://forum.zkoss.org/question/24875/how-to-receive-a-ctrlenter-keyevent/
//https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Patterns/Keystroke_Handling
public class TextboxFile extends Span implements IZComExt {

	//	@Wire
	@Getter
	final Textbox editCom;

	//	@Wire
	@Getter
	final XulElement viewCom;

	boolean isLabelOrEdit = true;

	final String _file;

	private transient Path path;

	public static TextboxFile of(Path path, boolean... editView) {
		return new TextboxFile(path, editView).setDefaultDims();
	}

	public static TextboxFile of(Object labelOrComponent, Path path, boolean... editView) {
		return new TextboxFile(labelOrComponent, path, editView).setDefaultDims();
	}

	public Path file() {
		return path == null ? path = Paths.get(_file) : path;
	}


	public TextboxFile(Path file, boolean... editView) {
		this(file.getFileName().toString(), file, editView);
	}

	public TextboxFile(Object viewComOrString, Path file, boolean... editView) {

		this.path = file;
		this._file = file.toString();

		IT.notNull(viewComOrString);

		if (viewComOrString instanceof CharSequence) {
			this.viewCom = new Label(viewComOrString.toString());
		} else if (viewComOrString instanceof Component) {
			this.viewCom = (XulElement) viewComOrString;
		} else {
			throw new WhatIsTypeException(viewComOrString.getClass());
		}

		this.editCom = new Textbox();

		editCom.addEventListener(Events.ON_CANCEL, (SerializableEventListener) arg0 -> onChangeStateToEditableOrLabel(false, true));
		viewCom.addEventListener(Events.ON_DOUBLE_CLICK, (SerializableEventListener) arg0 -> onChangeStateToEditableOrLabel(true));
		editCom.addEventListener(Events.ON_OK, (SerializableEventListener) arg0 -> onChangeStateToEditableOrLabel(false));


//		editCom.setCtrlKeys("^v");
//		textbox.setCtrlKeys("^#enter");
//		textbox.addEventListener(Events.ON_CTRL_KEY, new EventListener<Event>() {
//			@Override
//			public void onEvent(Event event) throws Exception {
//			}
//		});

		{
			ZKE.addEventListenerCtrl(editCom, Events.ON_CTRL_KEY, ZKE.SHORTCUT_STORE_ALT_V, (SerializableEventListener) event -> onChangeStateToEditableOrLabel(false, ((KeyEvent) event).getKeyCode() == 66));

			//		editCom.setCtrlKeys(ZKE.SHORTCUT_STORE_ALT_V);//@b
			//		editCom.addEventListener(Events.ON_CTRL_KEY, new org.zkoss.zk.ui.event.SerializableEventListener() {
			//			@Override
			//			public void onEvent(Event arg0) throws Exception {
			//				KeyEvent ke = (KeyEvent) arg0;
			//				onChangeStateToEditableOrLabel(false, ke.getKeyCode() == 66);
			//			}
			//		});
		}

//		setMacroURI("/WEB-INF/component/editablelabel.zul");
//		setMacroURI("/zul/editablelabel.zul");

		super.appendChild(viewCom);
		super.appendChild(editCom);

		this.isLabelOrEdit = !ARG.isDefEqTrue(editView);

		viewCom.setVisible(this.isLabelOrEdit);
		editCom.setVisible(!this.isLabelOrEdit);

		if (!isLabelOrEdit) {
			readEditComValue();
		}

	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {

	}

	protected void onChangeStateToEditableOrLabel(boolean editOrLabel, boolean... isCancel) {
		this.isLabelOrEdit = editOrLabel;
		if (!editOrLabel && viewOff_removeFromTree != null) {
			viewCom.setVisible(false);
			if (viewOff_removeFromTree) {
				ZKC.removeMeReturnParent(viewCom);//!!!
			}
		} else {
			viewCom.setVisible(!editOrLabel);
		}
		editCom.setVisible(editOrLabel);
		if (ARG.isDefEqTrue(isCancel)) {
//			ZK.showLog("no save ( for save use " + SHORTCUT_STOR_ALT_V + " )");
		} else {
			if (editOrLabel) {
				readEditComValue();
			} else {
				onUpdateAndWriteText(editCom.getValue());
			}
		}
	}

	private void readEditComValue() {
		editCom.setValue(RW.readContent(file(), ""));
	}

	protected void onUpdateAndWriteText(String value) {
		if (enableWrite) {
			RW.write(file(), value);
			ZKI_Window.info(NotifyMessageRtException.LEVEL.LOG.I("File writed '" + _file + "', size=" + value.length()));
		}
	}

	public TextboxFile setDefaultDims() {
		setEditableRows(10);
		setEditableWidth("500px");
		return this;
	}

	public TextboxFile block() {
		setStyle("display:block");
		return this;
	}

	public TextboxFile setEditableRows(int rows) {
		this.editCom.setRows(rows);
		return this;
	}

	public TextboxFile setEditableWidth(String width) {
		this.editCom.setWidth(width);
		return this;
	}

	private boolean enableWrite = true;

	public TextboxFile setEnableWrite(boolean enableWrite) {
		this.enableWrite = enableWrite;
		return this;
	}

	Boolean viewOff_removeFromTree = null;

	public TextboxFile enableViewOffAfterEdit(boolean removeFromTree) {
		viewOff_removeFromTree = removeFromTree;
		return this;
	}

	public TextboxFile viewLabelOrEdit(boolean viewLabelOrEdit) {
		isLabelOrEdit = viewLabelOrEdit;
		return this;
	}

	public static class TextboxFileOpenListener implements SerializableEventListener {

		final IPath path;

		public TextboxFileOpenListener(Path path) {
			this.path = PathEntity.of(path);
		}

		@Override
		public void onEvent(Event event) throws Exception {
			TextboxFile tb = (TextboxFile) of(path.fPath(), true).enableViewOffAfterEdit(true).setSTYLE("position:absolute;left:10px;top:10px;");
			ZKC.appendChild(tb);
		}
	}
}

