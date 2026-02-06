package zk_com.base;


import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpc.json.UGson;
import mpu.str.UST;
import mpe.ftypes.core.FDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Textbox;
import zk_com.core.IZCom;
import zk_form.notify.NotifyRef;
import zk_form.notify.ZKI;
import zk_notes.node_state.ProxyRW;
import zk_os.AppZos;
import zk_page.ZKS_AutoDims;
import zk_page.events.ZKE;
import zk_form.notify.ZKI_Custom;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

public class Tbx extends Textbox implements IZCom {

	public static final Logger L = LoggerFactory.getLogger(Tbx.class);

	protected DIMS dimsMode = null;

	protected Boolean multiline = null;

	protected boolean saveble = false, saveOnShortCut = true;

	protected boolean json, prettyjson = false;
	protected boolean securityOn = true;

	protected boolean notification_write = true;
	protected boolean checkFileExist = false;

	private @Getter String pathStr;

	public static Tbx of(String placeholder) {
		Tbx tbx = new Tbx();
		tbx.setPlaceholder(placeholder);
		return tbx;
	}

	public static Tbx of(String value, String placeholder) {
		Tbx tbx = new Tbx(value);
		tbx.setPlaceholder(placeholder);
		return tbx;
	}

	public boolean isPersist() {
		return pathStr != null;
	}

	private transient Path path0;

	public Path toPath() {
		return path0 != null ? path0 : (path0 = Paths.get(pathStr));
	}

	public String fName() {
		return toPath().getFileName().toString();
	}

	public Tbx(boolean focus) {
		setFocus(true);
	}

	public Tbx(DIMS... dimsMode) {
		super();
		dims(dimsMode);
	}

	public Tbx(String value, DIMS... dims) {
		this(value, null, null, dims);
	}

	public Tbx(String value, String placeholder, String tooltip, DIMS... dims) {
		super(value);
		if (X.notEmpty(placeholder)) {
			setPlaceholder(placeholder);
		}
		if (X.notEmpty(tooltip)) {
			setTooltip(tooltip);
		}
		dims(dims);
	}

	public Tbx(Path file, DIMS... dims) {
		super();
		this.pathStr = file.toString();
		this.json = EXT.JSON == EXT.of(toPath());
		dims(dims);
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	public Tbx saveOnShortCut(boolean... saveOnShortCut) {
		this.saveOnShortCut = ARG.isDefNotEqFalse(saveOnShortCut);
		saveble();
		return this;
	}

	public Tbx saveble(boolean... savable) {
		this.saveble = ARG.isDefNotEqFalse(savable);
		return this;
	}

	public Tbx json(boolean... json) {
		this.json = ARG.isDefNotEqFalse(json);
		return this;
	}

	public Tbx prettyjson(boolean... prettyjson) {
		this.prettyjson = ARG.isDefNotEqFalse(prettyjson);
		if (this.prettyjson) {
			this.json = true;
		}
		return this;
	}

	public Tbx dims(DIMS... dims) {
		this.dimsMode = ARG.toDefOrNull(dims);
		return this;
	}

	public Tbx multiline(boolean... multiline) {
		this.multiline = ARG.isDefNotEqFalse(multiline);
		return this;
	}

	public Tbx onCHANGED(SerializableEventListener<Event> event) {
		addEventListener(Events.ON_CHANGE, event);
		return this;
	}

	public Tbx onOK(SerializableEventListener<Event> event) {
		addEventListener(Events.ON_OK, event);
		return this;
	}

	public void onChangingUpdateValue() {
		onCHANGING((SerializableEventListener<Event>) event -> {
			String org = ((InputEvent) event).getValue();
			setValue(org);
		});
	}

	public Tbx onCHANGING(SerializableEventListener<Event> event) {
		addEventListener(Events.ON_CHANGING, event);
		return this;
	}

	public Tbx onChangingAutoWidth(int defWidth) {
		onCHANGING((Event e) -> {
			InputEvent iEvent = (InputEvent) e;
			Tbx target = (Tbx) e.getTarget();
			String vl = iEvent.getValue();
			if (vl.length() > 100) {
				target.width(80.0);
			} else if (vl.length() > 60) {
				target.width(60.0);
			} else if (vl.length() > 20) {
				target.width(40.0);
			} else if (vl.length() > 6) {
				target.width(20.0);
			} else {
				target.width(defWidth);
			}
		});
		return this;
	}

	public void autoDims() {
		ZKS_AutoDims.initAutoDims(this, this.getValue());
	}

	public Tbx autoWidth() {
		width(ZKS_AutoDims.getAutoWidth_50_100_200(this.getValue(), 1));
		return this;
	}

	@Getter
	private String[] autoDateFormat;

	public Tbx autoDateFormat(String... autoDateFormat) {
		if (X.notEmpty(autoDateFormat)) {
			this.autoDateFormat = autoDateFormat;
		} else {
			this.autoDateFormat = new String[]{FDate.APP_SLDF_UFOS, FDate.APP_SLDF_UFOS, FDate.MONO_YYYYMMDDmmhhss, FDate.MONO_YYYYMMDDmmhh, FDate.MONO_YYYYMMDDmm, FDate.MONO_YYYYMMDD};
		}
		onCHANGED((SerializableEventListener<Event>) event -> {
			if (getDate(null) == null) {
				ZKI.alert("Wrong date '%s'", getValue());
			}
		});
		return this;
	}

	public Date getDate(Date... defRq) {
		return DATE(getValue(), getAutoDateFormat(), defRq);
	}

	public static Date DATE(String str, String[] formats, Date... defRq) {
		for (String format : formats) {
			if (FDate.APP_SLDF_UFOS.equals(format)) {
				Date dateFrom = AppZos.getLogGetterDate().getDateFrom(str, null);
				if (dateFrom != null) {
					return dateFrom;
				}
			}
			Date date = UST.DATE(str, format, null);
			if (date != null) {
				return date;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Wrong date '%s'. Checked format's '%s'.", str, ARR.of(formats));
	}

	public Tbx onCANCEL(SerializableEventListener eventListener) {
		addEventListener(Events.ON_CANCEL, eventListener);
		return this;
	}

	public class SubmitEvent implements SerializableEventListener {

		@Override
		public void onEvent(Event event) throws Exception {
			onSubmitTextValue(event);
		}
	}

	private boolean hasSaveCallback = false;

	public SerializableEventListener<Event> getEventSave(Function<String, Boolean>... saveCallback) {
		if (ARG.isDef(saveCallback)) {
			hasSaveCallback = true;
		}
		return event -> {
			save();
			if (hasSaveCallback) {
				boolean rslt = ARG.toDef(saveCallback).apply(getValue());
				if (notification_write) {
					onNotificationWrite(rslt, null);
				}
			}
		};
	}

	private SubmitEvent submitEvent = null;

	protected void init() {

		Optional<String> contentFromFile = null;

		String cachedPrettyJson = null;
		if (isPersist()) {
			onCheckFileExist();
			cachedPrettyJson = updateSetValueWithCheck(readFileContent());
			contentFromFile = Optional.of(cachedPrettyJson);
		}

		if (submitEvent == null || saveOnShortCut) {
			ZKE.addEventListenerCtrl(this, Events.ON_CTRL_KEY, ZKE.SHORTCUT_STORE_CTRL_S, submitEvent = new SubmitEvent());
		}

//		if (shortCut != null) {
//			ZKE.addEventListenerCtrl(this, Events.ON_CTRL_KEY, shortCut, e -> shortCutEvent(e));
//		}

//		Boolean is6 = ((KeyEvent) event).getKeyCode() == 66);
//		ZKE.addEventListenerCtrl(this, Events.ON_CTRL_KEY, ZKE.SHORTCUT_STORE_ALT_V, (SerializableEventListener) event -> onSubmitTextValue(event));
//		ZKE.addEventListenerCtrl(this, Events.ON_CTRL_KEY, ZKE.SHORTCUT_STORE_ALT_V, new SubmitEvent());
//		ZKE.addEventListenerCtrl(this, Events.ON_OK, erializableEventListener) event -> onSubmitTextValue());

		if (dimsMode != null) {
			String value;
			if (contentFromFile != null) {
				value = contentFromFile.get();
			} else {
				value = cachedPrettyJson != null ? cachedPrettyJson : updateSetValueWithCheck(getValue());
			}
			dimsMode.init(this, value);
		}

		setMultiline(multiline != null ? multiline : this instanceof Tbxm);

	}

	private String updateSetValueWithCheck(String value) {
		String value0 = prettyjson ? UGson.toStringPretty_orEmpty(value) : value;
		setValue(value0);
		return value0;
	}

	protected String readFileContent() {
		return ProxyRW.of(this).readContent("");
	}

	public Tbx onCheckFileExist() {
		if (checkFileExist) {
			IT.isFileExist(pathStr);
		}
		return this;
	}

	protected void onSubmitTextValue(Event e) {
		if (isPersist() || saveble) {
			save();
		}
	}

	public void save() {
		String value = getValue();
		if (json || prettyjson) {
			try {//valid with errors
				Object jo = UGson.JO_(value);
				value = updateSetValueWithCheck(value);
			} catch (JsonSyntaxException ex) {
				ZKI.alert("This json is invalid!\n---\n" + (ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage()));
				return;
			}
		}

		onCheckFileExist();

		boolean doWrite = saveble && isPersist();
		boolean securityAllow = false;
		if (doWrite) {
			securityAllow = !securityOn || isAllowSecurity();
			if (securityAllow) {
				setValue(value);
				ProxyRW.of(this).writeContent(value);
			}
		}
		if (notification_write && !hasSaveCallback) {
			onNotificationWrite(doWrite, securityAllow);
		}
		if (L.isDebugEnabled()) {
			String msg = toNotificationMessage(doWrite, securityAllow);

			msg += X.f(". Path '%s', length '%s'", ProxyRW.of(this).getTargetAnyPath(), X.sizeOf(value));
			L.debug(msg);
		}
	}

	private boolean isAllowSecurity() {
		return getFormState().isAllowedAccess_EDIT();
	}

	private void onNotificationWrite(boolean doWrite, Boolean securityAllow) {
		if (L.isInfoEnabled()) {
			L.info("Security '{}'", securityAllow == null ? "off" : securityAllow);
		}
		String notificationMessage = toNotificationMessage(doWrite, securityAllow);
		if (securityAllow == null || securityAllow) {
			ZKI.infoAfterPointer(notificationMessage, ZKI.Level.WARN);
			NotifyRef.INFO(notificationMessage, NotifyRef.Pos.after_pointer).show();
		} else {
			ZKI_Custom.warnBottomRightFast(notificationMessage);
		}
	}

	private String toNotificationMessage(boolean doWrite, Boolean securityAllow) {
		String rslt = doWrite ? "updated" : "NOT updated";
		if (!(securityAllow == null || securityAllow)) {
			return "Access denied";
		}
		String vlType = json || prettyjson ? ("Note with JSON" + (prettyjson ? "*" : "")) : "Note with TEXT";
		String msg = X.f("%s is %s", vlType, rslt);
		return msg;
	}

	public Tbx(Path filePath, DIMS dims) {
		super();
		this.pathStr = filePath.toString();
		this.path0 = filePath;
		dims(dims);
	}

	public enum DIMS {
		BYCONTENT, WH100, W80;

		public void init(HtmlBasedComponent com, String value) {
			switch (this) {
				case BYCONTENT:
					ZKS_AutoDims.initAutoDims(com, IT.NN(value));
					break;
				case WH100:
					com.setHeight("100%");
					com.setWidth("100%");
					break;
				case W80:
					com.setWidth("80%");
					break;
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

}
