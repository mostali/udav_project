package zk_com.editable;

import lombok.Getter;
import mp.utilspoi.UMd2Html;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import mpu.core.ARG;
import mpc.exception.WhatIsTypeException;
import zk_com.base.Md;
import zk_com.base_ctr.Span0;
import zk_com.core.IZStyle;
import zk_page.ZKS_AutoDims;
import zk_page.events.ECtrl;
import zk_page.events.ZKE;

//https://stackoverflow.com/questions/17398202/zk-ctrl-key-or-hot-key-creating-issue-with-diffrent-browsers
//https://forum.zkoss.org/question/24875/how-to-receive-a-ctrlenter-keyevent/
//https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Patterns/Keystroke_Handling
public class EditableValue extends Span0 implements IZStyle {

	//	@Wire
	@Getter
	final Textbox editCom;

	//	@Wire
	@Getter
	final HtmlBasedComponent viewCom;

	boolean isLabelOrEdit = true;

	public EditableValue isLabelVew(boolean isLabelVew) {
		this.isLabelOrEdit = isLabelVew;
		swapVisible();
		onChangeStateToEditableOrLabel(null, !isLabelVew);
		return this;
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
		//override
	}

	public EditableValue(String value) {
		this(value, Label.class);
	}

	@Override
	public IZStyle font_bold_nice(int font_size) {
		if (viewCom instanceof IZStyle) {
			return ((IZStyle) viewCom).font_bold_nice(font_size);
		}
		return this;
	}

	public EditableValue(String value, Class<? extends Component> classCom) {

		if (classCom == Label.class) {
			this.viewCom = new Label(value);
		} else if (classCom == Html.class) {
			this.viewCom = new Html(value);
		} else if (classCom == Textbox.class) {
			this.viewCom = new Textbox(value);
		} else if (classCom == Md.class) {
			this.viewCom = new Md(value);
		} else {
			throw new WhatIsTypeException(classCom);
		}

		this.editCom = new Textbox();

		editCom.addEventListener(Events.ON_OK, (SerializableEventListener) arg0 -> {
//			if (ECtrl.of(arg0) == ECtrl.CTRL_ALT_SHIFT) {
//				onChangeStateToEditableOrLabel(true);
//			}
			onChangeStateToEditableOrLabel(arg0, false);
		});
		editCom.addEventListener(Events.ON_CANCEL, (SerializableEventListener) arg0 -> onChangeStateToEditableOrLabel(arg0, false, true));
		viewCom.addEventListener(Events.ON_DOUBLE_CLICK, (SerializableEventListener) arg0 -> {
			if (ECtrl.ofAsCtrl(arg0) == ECtrl.CTRL) {
				onChangeStateToEditableOrLabel(arg0, true);
			}
		});

		ZKE.addEventListenerCtrl(editCom, Events.ON_CTRL_KEY, ZKE.SHORTCUT_STORE_ALT_V, (SerializableEventListener) event -> onChangeStateToEditableOrLabel(event, false, ((KeyEvent) event).getKeyCode() == .66));

		super.appendChild(viewCom);
		super.appendChild(editCom);

		swapVisible();
	}

	private void swapVisible() {
		viewCom.setVisible(isLabelOrEdit);
		editCom.setVisible(!isLabelOrEdit);
	}

	protected void onChangeStateToEditableOrLabel(Event e, boolean editOrLabel, boolean... isCancel) {
		this.isLabelOrEdit = editOrLabel;
		viewCom.setVisible(!editOrLabel);
		editCom.setVisible(editOrLabel);
		if (ARG.isDefEqTrue(isCancel)) {
//			ZK.showLog("no save (for save use " + IZkCom.SHORTCUT_STOR_ALT_V + " )");
		} else {
			if (editOrLabel) {
				String valueFromView = getValueFrom(viewCom);
				editCom.setValue(valueFromView);
				editCom.setWidth(ZKS_AutoDims.getAutoWidth_50_100_200_300_400(valueFromView, 1));
			} else {
				onUpdatePrimaryText(editCom.getValue());
			}
		}
	}

	protected String getValueFrom(Component view) {
		if (view instanceof Label) {
			return ((Label) view).getValue();
		} else if (view instanceof Md) {
			return (((Md) view).data_md);
		} else if ((view instanceof Html)) {
			return ((Html) view).getContent();
		}
		throw new WhatIsTypeException(view.getClass());
	}

	private EditableValue setValueFor(Component view, String content) {
		if (view instanceof Label) {
			((Label) view).setValue(content);
			return this;
		} else if ((view instanceof Md)) {
			((Md) view).data_md = content;
			((Md) view).setContent(UMd2Html.buildHtml(content));
			return this;
		} else if ((view instanceof Html)) {
			((Html) view).setContent(content);
			return this;
		}
		throw new WhatIsTypeException(view.getClass());
	}

	protected void onUpdatePrimaryText(String value) {
		setValueFor(viewCom, editCom.getValue());
	}

	public EditableValue setDefaultDims() {
		setEditableRows(10);
		setEditableWidth("500px");
		return this;
	}

	public EditableValue block() {
		//		setStyle();
		//		setWidthDirectly("100%");
		setStyle("display:block");
		return this;
	}

	public EditableValue setEditableRows(int rows) {
		this.editCom.setRows(rows);
		return this;
	}

	public EditableValue setEditableWidth(String width) {
		this.editCom.setWidth(width);
		return this;
	}


//	public String getValue() {
//		return textbox.getValue();
//	}
//
//	public void setValue(String value) {
//		textbox.setValue(value);
//		label.setValue(value);
//	}

//	@Listen("onDoubleClick=#label")
//	public void doEditing() {
//		textbox.setVisible(true);
//		label.setVisible(false);
//		textbox.focus();
//	}
//
//	@Listen("onBlur=#textbox")
//	public void doEdited() {
//		label.setValue(textbox.getValue());
//		textbox.setVisible(false);
//		label.setVisible(true);
//		Events.postEvent("onEdited", this, null);
//	}

}

