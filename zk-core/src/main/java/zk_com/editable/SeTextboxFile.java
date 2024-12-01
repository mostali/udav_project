//package zk_com.editable;
//
//import lombok.Getter;
//import mpc.core.ARG;
//import mpc.UC;
//import mpc.exception.NotifyMessage;
//import mpc.exception.WhatIsTypeException;
//import mpc.fs.path.PathEntity;
//import mpu.core.RW;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Page;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zk.ui.event.KeyEvent;
//import org.zkoss.zk.ui.event.SerializableEventListener;
//import org.zkoss.zul.Label;
//import org.zkoss.zul.Span;
//import org.zkoss.zul.Textbox;
//import org.zkoss.zul.impl.XulElement;
//import zk_com.core.IZkComExt;
//import zk_form.std_core.head.IHeadCom;
//import zk_form.std_core.head.IHeadRsrc;
//import zk_form.std_core.head.RsrcName;
//import zk_os.events.ZKE;
//import zk_os.notify.ZKNotify;
//import zk_page.ZKC;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
////https://stackoverflow.com/questions/17398202/zk-ctrl-key-or-hot-key-creating-issue-with-diffrent-browsers
////https://forum.zkoss.org/question/24875/how-to-receive-a-ctrlenter-keyevent/
////https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Patterns/Keystroke_Handling
//public class SeTextboxFile extends Span implements IZkComExt, IHeadCom {
//
//	//	@Wire
//	@Getter
//	final Textbox editCom;
//
//	//	@Wire
//	@Getter
//	final XulElement viewCom;
//
//	boolean isLabelOrEdit = true;
//
//	final String _file;
//
//	private transient Path path;
//
//	public static SeTextboxFile of(Path path, boolean... editView) {
//		return new SeTextboxFile(path, editView).setDefaultDims();
//	}
//
//	public static SeTextboxFile of(Object labelOrComponent, Path path, boolean... editView) {
//		return new SeTextboxFile(labelOrComponent, path, editView).setDefaultDims();
//	}
//
//	public Path file() {
//		return path == null ? path = Paths.get(_file) : path;
//	}
//
//
//	public SeTextboxFile(Path file, boolean... editView) {
//		this(file.getFileName().toString(), file, editView);
//	}
//
//	public SeTextboxFile(Object viewComOrString, Path file, boolean... editView) {
//
//		this.path = file;
//		this._file = file.toString();
//
//		UC.notNull(viewComOrString);
//
//		if (viewComOrString instanceof CharSequence) {
//			this.viewCom = new Label(viewComOrString.toString());
//		} else if (viewComOrString instanceof Component) {
//			this.viewCom = (XulElement) viewComOrString;
//		} else {
//			throw new WhatIsTypeException(viewComOrString.getClass());
//		}
//
//		this.editCom = new Textbox();
//
//		editCom.addEventListener(Events.ON_CANCEL, (SerializableEventListener) arg0 -> onChangeStateToEditableOrLabel(false, true));
//		viewCom.addEventListener(Events.ON_DOUBLE_CLICK, (SerializableEventListener) arg0 -> onChangeStateToEditableOrLabel(true));
//		editCom.addEventListener(Events.ON_OK, (SerializableEventListener) arg0 -> onChangeStateToEditableOrLabel(false));
//
//
////		editCom.setCtrlKeys("^v");
////		textbox.setCtrlKeys("^#enter");
////		textbox.addEventListener(Events.ON_CTRL_KEY, new EventListener<Event>() {
////			@Override
////			public void onEvent(Event event) throws Exception {
////			}
////		});
//
//		{
//			ZKE.addEventListenerCtrl(editCom, Events.ON_CTRL_KEY, ZKE.SHORTCUT_STORE_ALT_V, (SerializableEventListener) event -> onChangeStateToEditableOrLabel(false, ((KeyEvent) event).getKeyCode() == 66));
//
//			//		editCom.setCtrlKeys(ZKE.SHORTCUT_STORE_ALT_V);//@b
//			//		editCom.addEventListener(Events.ON_CTRL_KEY, new org.zkoss.zk.ui.event.SerializableEventListener() {
//			//			@Override
//			//			public void onEvent(Event arg0) throws Exception {
//			//				KeyEvent ke = (KeyEvent) arg0;
//			//				onChangeStateToEditableOrLabel(false, ke.getKeyCode() == 66);
//			//			}
//			//		});
//		}
//
////		setMacroURI("/WEB-INF/component/editablelabel.zul");
////		setMacroURI("/zul/editablelabel.zul");
//
//		super.appendChild(viewCom);
//		super.appendChild(editCom);
//
//		this.isLabelOrEdit = !ARG.isDefEqTrue(editView);
//
//		viewCom.setVisible(this.isLabelOrEdit);
//		editCom.setVisible(!this.isLabelOrEdit);
//
//		if (!isLabelOrEdit) {
//			readEditComValue();
//		}
//
//	}
//
//	@Override
//	public void onPageAttached(Page newpage, Page oldpage) {
//		init();
//		super.onPageAttached(newpage, oldpage);
//	}
//
//	protected void init() {
//
//	}
//
//	protected void onChangeStateToEditableOrLabel(boolean editOrLabel, boolean... isCancel) {
//		this.isLabelOrEdit = editOrLabel;
//		if (!editOrLabel && viewOff_removeFromTree != null) {
//			viewCom.setVisible(false);
//			if (viewOff_removeFromTree) {
//				ZKC.removeMeReturnParent(viewCom);//!!!
//			}
//		} else {
//			viewCom.setVisible(!editOrLabel);
//		}
//		editCom.setVisible(editOrLabel);
//		if (ARG.isDefEqTrue(isCancel)) {
////			ZK.showLog("no save ( for save use " + SHORTCUT_STOR_ALT_V + " )");
//		} else {
//			if (editOrLabel) {
//				readEditComValue();
//			} else {
//				onUpdateAndWriteText(editCom.getValue());
//			}
//		}
//	}
//
//	private void readEditComValue() {
//		editCom.setValue(RW.readContent(file(), ""));
//	}
//
//	protected void onUpdateAndWriteText(String value) {
//		if (enableWrite) {
//			RW.write(file(), value);
//			ZKNotify.showInfo(NotifyMessage.LEVEL.LOG.I("File writed '" + _file + "', size=" + value.length()));
//		}
//	}
//
//	public SeTextboxFile setDefaultDims() {
//		setEditableRows(10);
//		setEditableWidth("500px");
//		return this;
//	}
//
//	public SeTextboxFile BLOCK() {
//		setStyle("display:block");
//		return this;
//	}
//
//	public SeTextboxFile setEditableRows(int rows) {
//		this.editCom.setRows(rows);
//		return this;
//	}
//
//	public SeTextboxFile setEditableWidth(String width) {
//		this.editCom.setWidth(width);
//		return this;
//	}
//
//	private boolean enableWrite = true;
//
//	public SeTextboxFile setEnableWrite(boolean enableWrite) {
//		this.enableWrite = enableWrite;
//		return this;
//	}
//
//	Boolean viewOff_removeFromTree = null;
//
//	public SeTextboxFile enableViewOffAfterEdit(boolean removeFromTree) {
//		viewOff_removeFromTree = removeFromTree;
//		return this;
//	}
//
//	public SeTextboxFile viewLabelOrEdit(boolean viewLabelOrEdit) {
//		isLabelOrEdit = viewLabelOrEdit;
//		return this;
//	}
//
//	public static final IHeadRsrc[] HEAD_RSCS = {RsrcName.SUNEDITOR_CSS, RsrcName.SUNEDITOR_JS};//, RsrcName.SUNEDITOR_EN_JS
//
//	@Override
//	public IHeadRsrc[] getHeadRsrc() {
//		return HEAD_RSCS;
//	}
//
//	public static class TextboxFileOpenListener extends PathEntity implements SerializableEventListener {
//
//		public TextboxFileOpenListener(Path path) {
//			super(path);
//		}
//
//		@Override
//		public void onEvent(Event event) throws Exception {
//			SeTextboxFile tb = (SeTextboxFile) of(path(), true).enableViewOffAfterEdit(true).setSTYLE("position:absolute;left:10px;top:10px;");
//			ZKC.appendChild(tb);
//		}
//	}
//}
//
