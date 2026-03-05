package zk_form.xform;

import mp.utl_odb.tree.AppPropDef;
import mpu.IT;
import mpu.X;
import mpu.func.FunctionV1;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.*;
import zk_com.base.Bt;
import zk_com.base.Cb;
import zk_com.base.Dd;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Span0;
import zk_com.core.IZCom;
import zk_form.notify.NotifyRef;
import zk_page.ZKC;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Xform extends Div0 {

	final Map<String, Object> map;

	final Bt btOk = new Bt("ok");

	public Xform(Map<String, Object> map) {
		super();
		this.map = map;
	}

	public static Xform of(Map map) {
		return new Xform(map);
	}

	FunctionV1<Event> callback;

	public Xform withCallback(FunctionV1<Event> callback) {
		this.callback = callback;
		return this;
	}

	public static Xform ofItemsCb(List<String> items) {
		Map map = items.stream().collect(Collectors.toMap(k -> k, v -> false));
		return of(map);
	}

	public void onSubmitXform(Map<String, Object> mapModel) {
		X.p("onSubmitXform:" + mapModel);

	}

	public static Xform ofMap(Map<String, Object> form, FunctionV1<Map<String, Object>> onOk) {
		Xform xform = new Xform(form) {
			@Override
			public void onSubmitXform(Map<String, Object> mapModel) {
				onOk.apply(mapModel);
			}
		};
		return xform;
	}


	@Override
	protected void init() {
		super.init();

		map.forEach((k, v) -> {
			appendChild(new XformItem<>(k, v));
		});

		appendChild(btOk);

		btOk.addEventListener(Events.ON_CLICK, e -> {
			onSubmitXform(getMapModel());
			if (callback != null) {
				callback.apply(e);
			}
		});
	}

	public Map<String, Object> getMapModel() {
		Map<String, Object> items = getXformItems().stream().collect(Collectors.toMap(k -> k.name, v -> {
			Object valueFromCom = v.getValueFromCom();
			return valueFromCom;
		}));

		return items;
	}

	public @NotNull List<XformItem> getXformItems() {
		return getChildren().stream().filter(c -> c instanceof XformItem).map(c -> (XformItem) c).collect(Collectors.toList());
	}


	public static class XformItem<T> extends Span0 {

		public final String name;
		public final Object objDef;

		public XformItem(String name, Object objDef) {
			this.name = name;
			this.objDef = objDef;
		}

		private Dd _ddProp;
		private Cb _cbProp;
		private Tbx _tbxProp;

		boolean withBt = false;
		boolean rightTop = false;
		boolean allowSetIllegalValue = true;

//		public zk_notes.node_state.PropFormItem posRightTop(boolean... rightTop) {
//			this.rightTop = ARG.isDefNotEqFalse(rightTop);
//			return this;
//		}

		private T getValueFromCom() {
			final T defValue = (T) objDef;
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

//			final String property = appProp.getPropName();
//			final T defValueWithType = appProp.defValueHolder.getValue();
//
//			T targetValue = appProp.getValueOrDefault(null);

//			T useValue = targetValue == null ? defValueWithType : targetValue;

			Object useObj = objDef;
			String useName = name;

			AtomicBoolean isCollection = new AtomicBoolean(false);
			IZCom fCom;
			if (useObj instanceof Boolean) {
				fCom = _cbProp = new Cb((Boolean) useObj).moldToggle();
				_cbProp.setLabel(useName);
			} else if (useObj instanceof Enum) {
				fCom = _ddProp = new Dd((Enum) useObj, true);
			} else if (useObj instanceof Collection) {
				isCollection.set(true);
				List<String> selected = useObj != null ? (List<String>) useObj : (List<String>) useObj;
//				List<String> vls = (List<String>) appProp.getDefValueSupplier().get();
				fCom = _ddProp = new Dd(selected.get(0), (Collection) useObj);
			} else {
				String tbxVl = useObj == null ? X.toStringNN(useObj, "") : useObj.toString();
				fCom = _tbxProp = new Tbx(tbxVl, useName, useName);
				_tbxProp.autoWidth();
			}

//			SerializableEventListener updaterProp = event -> {
//				try {
//					Object newValue;
//					if (isCollection.get()) {
//						newValue = ((InputEvent) event).getValue();
//						submitValue(appProp, (T) ARR.as(newValue));
//					} else {
//						newValue = getValueFromCom();
//						submitValue(appProp, (T) newValue);
//					}
////				ZKI_Log.log("Update '%s' with new value '%s'", property, newValue);
//					L.info("Update '{}' with new value '{}'", property, newValue);
//					ZKR.restartPage();
//				} catch (Exception ex) {
//					ZKI.alert(ex);
//				}
//			};
//
//			Bt btOk = null;
//			if (withBt) {
//				btOk = new Bt(ANI.SUBMIT_PROP);
//				btOk.title(property);
//				btOk.onCLICK(updaterProp);
//			}
//
//			fCom.title(appProp.toTitle());
//
//			if (fCom instanceof InputElement) {
//				fCom.placeholder(appProp.getPropName());
//			}
//
//			Span0 span0 = withBt ? Span0.of(fCom.com(), btOk) : Span0.of(fCom.com());
			appendChild(fCom.com());
//
//			if (rightTop) {
//				span0.setSTYLE("float:right;display:inline-block;");
//			}
//
//			if (fCom instanceof Dd) {
//				Dd dd = (Dd) fCom;
//				dd.onCHANGE(updaterProp);
//			} else if (fCom instanceof InputElement) {
//				InputElement fci = (InputElement) fCom;
//				fci.addEventListener(Events.ON_OK, updaterProp);
//			}
//
//			//
//			//
//
//			SerializableEventListener event_PropChanged = event -> {
//				if (event instanceof InputEvent) {
//					InputEvent inputEvent = (InputEvent) event;
//					changedValue(appProp, (T) inputEvent.getValue());
//				} else if (event instanceof MouseEvent) {
//					T valueOrDefault = appProp.getValueOrDefault(appProp.defValueHolder.getValue());
//					T newValue = (T) (X.equals(valueOrDefault, false) ? (Boolean) true : (Boolean) false);
//					changedValue(appProp, newValue);
//				}
//			};
//
//			if (_tbxProp != null) {
//				_tbxProp.onCHANGED(event_PropChanged);
//			} else if (_cbProp != null) {
//				_cbProp.onCLICK(event_PropChanged);
//			}

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

}
