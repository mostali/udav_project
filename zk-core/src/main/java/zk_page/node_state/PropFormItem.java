package zk_page.node_state;

import mp.utl_odb.tree.AppPropDef;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.impl.InputElement;
import zk_com.base.Bt;
import zk_com.base.Cb;
import zk_com.base.Dd;
import zk_com.base.Tbx;
import zk_com.base_ctr.Span0;
import zk_com.core.IZCom;
import zk_form.notify.ZKI_Log;
import zk_form.notify.Cnn;
import zk_page.ZKR;
import zk_notes.ANI;

public class PropFormItem<T> extends Span0 {

	public final AppPropDef<T> appProp;

	public PropFormItem(AppPropDef<T> appProp) {
		this.appProp = appProp;
	}

	private Dd _ddProp;
	private Cb _cbProp;
	private Tbx _tbxProp;

	//
	//
//	public void replace(AppPropDef prop) {
//		super.replace(new PropFormItem(prop), false, false, false);
//	}

//	@Override
//	protected void replace(Component comp, boolean bFellow, boolean bListener, boolean bChildren) {
//		super.replace(comp, bFellow, bListener, bChildren);
//	}

	boolean withBt = false;
	boolean rightTop = false;
	boolean allowSetIllegalValue = true;

	public PropFormItem posRightTop(boolean... rightTop) {
		this.rightTop = ARG.isDefNotEqFalse(rightTop);
		return this;
	}

	private T getValue() {
		final T defValue = appProp.getValueOrDefault();
//		if (defValue == null) {
//			return null;
//		} else
		if (defValue instanceof Boolean) {
			return (T) (Boolean) _cbProp.isChecked();
		} else if (defValue instanceof Enum) {
			return (T) _ddProp.getValueAsEnum();
		} else {
			return (T) _tbxProp.getValue();
		}
	}


	@Override
	protected void init() {
		super.init();

		final String property = appProp.key();
		final T defValue = appProp.seed.getValue();

		T value = appProp.getValueOrDefault(null);

		T useValue = value == null ? defValue : value;

		IZCom fCom;
		if (defValue instanceof Boolean) {
			fCom = _cbProp = new Cb((Boolean) useValue).moldToggle();
		} else if (defValue instanceof Enum) {
			fCom = _ddProp = new Dd((Enum) useValue);
		} else {
			String tbxVl = value == null ? X.toString(useValue, "") : value.toString();
			fCom = _tbxProp = new Tbx(tbxVl, property, property);
			_tbxProp.autoWidth();
		}

		SerializableEventListener updaterProp = event -> {
			try {
				submitValue(appProp, getValue());
				ZKI_Log.log("Update '%s' value '%s'", property, appProp.getValueOrDefault());
				ZKR.restartPage();
			} catch (Exception ex) {
				ZKI_Log.alert(ex, "ERR:" + ex.getMessage());
			}
		};

		Bt btOk = null;
		if (withBt) {
			btOk = new Bt(ANI.SUBMIT_PROP);
			btOk.title(property);
			btOk.onCLICK(updaterProp);
		}

		fCom.title(appProp.toTitle());

		if (fCom instanceof InputElement) {
			fCom.placeholder(appProp.key());
		}

		Span0 span0 = withBt ? Span0.of(fCom.com(), btOk) : Span0.of(fCom.com());
		appendChild(span0);

		if (rightTop) {
			span0.setSTYLE("float:right;display:inline-block;");
		}

		if (fCom instanceof Dd) {
			Dd dd = (Dd) fCom;
			dd.onCHANGE(updaterProp);
		} else if (fCom instanceof InputElement) {
			InputElement fci = (InputElement) fCom;
			fci.addEventListener(Events.ON_OK, updaterProp);
		}

		//
		//

		SerializableEventListener event_PropChanged = event -> {
			if (event instanceof InputEvent) {
				InputEvent inputEvent = (InputEvent) event;
				changedValue(appProp, (T) inputEvent.getValue());
			} else if (event instanceof MouseEvent) {
				changedValue(appProp, (T) (X.equals(appProp.getValueOrDefault(), false) ? (Boolean) true : (Boolean) false));
			}
		};

		if (_tbxProp != null) {
			_tbxProp.onCHANGED(event_PropChanged);
		} else if (_cbProp != null) {
			_cbProp.onCLICK(event_PropChanged);
		}

	}


	protected void submitValue(AppPropDef<T> appProp, T newValue) {
		try {
			appProp.update(newValue);
		} catch (IT.CheckException ex) {
			L.error("updateValue", ex);
			Cnn.ERR(ex.getMessage()).ref(this).show();
			if (allowSetIllegalValue) {
				appProp.setValue(newValue);
			}
		}
	}

//	protected void changingValue(AppPropDef<T> appProp, T newValue) {
//
//	}

	protected void changedValue(AppPropDef<T> appProp, T newValue) {
		submitValue(appProp, newValue);
	}


}
