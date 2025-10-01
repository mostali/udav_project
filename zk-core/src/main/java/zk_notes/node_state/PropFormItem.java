package zk_notes.node_state;

import mp.utl_odb.tree.AppPropDef;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
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
import zk_form.notify.ZKI;
import zk_form.notify.NotifyRef;
import zk_page.ZKR;
import zk_notes.ANI;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PropFormItem<T> extends Span0 {

	public final AppPropDef<T> appProp;

	public PropFormItem(AppPropDef<T> appProp) {
		this.appProp = appProp;
	}

	private Dd _ddProp;
	private Cb _cbProp;
	private Tbx _tbxProp;

	boolean withBt = false;
	boolean rightTop = false;
	boolean allowSetIllegalValue = true;

	public PropFormItem posRightTop(boolean... rightTop) {
		this.rightTop = ARG.isDefNotEqFalse(rightTop);
		return this;
	}

	private T getValueFromCom() {
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

		final String property = appProp.getPropName();
		final T defValueWithType = appProp.defValueHolder.getValue();

		T targetValue = appProp.getValueOrDefault(null);

		T useValue = targetValue == null ? defValueWithType : targetValue;

		AtomicBoolean isCollection = new AtomicBoolean(false);
		IZCom fCom;
		if (defValueWithType instanceof Boolean) {
			fCom = _cbProp = new Cb((Boolean) useValue).moldToggle();
		} else if (defValueWithType instanceof Enum) {
			fCom = _ddProp = new Dd((Enum) useValue, true);
		} else if (defValueWithType instanceof Collection) {
			isCollection.set(true);
			List<String> selected = targetValue != null ? (List<String>) targetValue : (List<String>) defValueWithType;
			List<String> vls = (List<String>) appProp.getDefValueSupplier().get();
			fCom = _ddProp = new Dd(selected.get(0), vls);
		} else {
			String tbxVl = targetValue == null ? X.toStringNN(useValue, "") : targetValue.toString();
			fCom = _tbxProp = new Tbx(tbxVl, property, property);
			_tbxProp.autoWidth();
		}

		SerializableEventListener updaterProp = event -> {
			try {
				Object newValue;
				if (isCollection.get()) {
					newValue = ((InputEvent) event).getValue();
					submitValue(appProp, (T) ARR.as(newValue));
				} else {
					newValue = getValueFromCom();
					submitValue(appProp, (T) newValue);
				}
//				ZKI_Log.log("Update '%s' with new value '%s'", property, newValue);
				L.info("Update '{}' with new value '{}'", property, newValue);
				ZKR.restartPage();
			} catch (Exception ex) {
				ZKI.alert(ex);
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
			fCom.placeholder(appProp.getPropName());
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
				T valueOrDefault = appProp.getValueOrDefault(appProp.defValueHolder.getValue());
				T newValue = (T) (X.equals(valueOrDefault, false) ? (Boolean) true : (Boolean) false);
				changedValue(appProp, newValue);
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
			NotifyRef.ERR(ex.getMessage()).ref(this).show();
			if (allowSetIllegalValue) {
				appProp.set_cachedValue(newValue);
			}
		}
	}

	protected void changedValue(AppPropDef<T> appProp, T newValue) {
		submitValue(appProp, newValue);
	}

}
