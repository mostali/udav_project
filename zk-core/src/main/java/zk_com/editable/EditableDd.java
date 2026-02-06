package zk_com.editable;

import lombok.Getter;
import mp.utilspoi.UMd2Html;
import mpc.arr.STREAM;
import mpc.exception.WhatIsTypeException;
import mpu.core.ARG;
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
import zk_com.base.Dd;
import zk_com.base.Lb;
import zk_com.base.Md;
import zk_com.base_ctr.Span0;
import zk_com.core.IZStyle;
import zk_form.ext.IItem;
import zk_page.ZKS_AutoDims;
import zk_page.events.ZKE;

import java.util.Collection;
import java.util.List;

public class EditableDd<T> extends Span0 implements IZStyle {

	@Getter
	protected final Dd<T> editCom;

	@Getter
	final HtmlBasedComponent viewCom;

	boolean isLabelOrEdit = true;

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
		//override
	}

	public EditableDd(String value, Collection<String> values) {
		this(value, (Collection) STREAM.mapToList(values, IItem::of), null);
	}

	public EditableDd(String value, Collection<IItem<T>> values, Boolean mainInit) {
		super();

		editCom = new Dd(value, STREAM.mapToList(values, IItem::getLabelName));
		viewCom = new Lb(editCom.getValue());

		editCom.addEventListener(Events.ON_CANCEL, (SerializableEventListener) arg0 -> onChangeStateToEditableOrLabel(false, true));
		viewCom.addEventListener(Events.ON_DOUBLE_CLICK, (SerializableEventListener) arg0 -> onChangeStateToEditableOrLabel(true));
		editCom.addEventListener(Events.ON_OK, (SerializableEventListener) arg0 -> onChangeStateToEditableOrLabel(false));

		super.appendChild(viewCom);
		super.appendChild(editCom);

		swapVisible();

	}

	@Override
	public IZStyle font_bold_nice(int font_size) {
		if (viewCom instanceof IZStyle) {
			return ((IZStyle) viewCom).font_bold_nice(font_size);
		}
		return this;
	}


	private void swapVisible() {
		viewCom.setVisible(isLabelOrEdit);
		editCom.setVisible(!isLabelOrEdit);
	}

	protected void onChangeStateToEditableOrLabel(boolean editOrLabel, boolean... isCancel) {
		this.isLabelOrEdit = editOrLabel;
		viewCom.setVisible(!editOrLabel);
		editCom.setVisible(editOrLabel);
		if (ARG.isDefEqTrue(isCancel)) {
//			ZK.showLog("no save (for save use " + IZkCom.SHORTCUT_STOR_ALT_V + " )");
		} else {
			if (editOrLabel) {
				String valueFromView = getValueFrom(viewCom);
				editCom.setValue(valueFromView);
//				editCom.setWidth(ZKS_AutoDims.getAutoWidth_50_100_200_300_400(valueFromView, 1));
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

	private EditableDd setValueFor(Component view, String content) {
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

//	public EditableDd setDefaultDims() {
//		setEditableRows(10);
//		setEditableWidth("500px");
//		return this;
//	}

//	public EditableDd block() {

	/// /				setWidthDirectly("100%");
//		setStyle("display:block");
//		return this;
//	}
	public EditableDd setEditableRows(int rows) {
		this.editCom.setRows(rows);
		return this;
	}

	public EditableDd setEditableWidth(String width) {
		this.editCom.setWidth(width);
		return this;
	}


}

