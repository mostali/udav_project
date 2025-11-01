package zk_com.base;


import mpu.pare.Pare;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import zk_com.core.IZCom;
import zk_page.ZKS;

public class Cb extends Checkbox implements IZCom {

	public Cb(Pare<String, Boolean> opts) {
		this(opts.key(), opts.val());
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	public void init() {
	}

	public static boolean isChecked(Cb cbSensFilter, boolean checkDisable, boolean checkVisible) {
		if (cbSensFilter == null) {
			return false;
		} else if (checkDisable && cbSensFilter.isDisabled()) {
			return false;
		} else if (checkVisible && !cbSensFilter.isVisible()) {
			return false;
		}
		return cbSensFilter.isChecked();
	}

	public Cb addEventListener(EventListener<? extends Event> listener) {
		super.addEventListener(Events.ON_CLICK, listener);
		return this;
	}

	public Cb(boolean isChecked) {
		this(null, null, null);
		setChecked(isChecked);
	}

	public Cb(String label) {
		this(label, null, null);
	}

	public Cb() {
		super();
	}

	public Cb(String label, String bgColor) {
		this(label, bgColor, null);
	}

	public Cb(String label, Boolean checked) {
		this(label);
		setChecked(checked);
	}

	public Cb(String label, String bgColor, String color) {
		super(label);

		if (bgColor != null) {
			ZKS.BGCOLOR(this, bgColor);
		}
		if (color != null) {
			ZKS.COLOR(this, color);
		}
	}

	public Cb moldSwitch() {
		setMold("switch");
		return this;
	}

	public Cb moldToggle() {
		setMold("toggle");
		return this;
	}

	public Cb checked(boolean checked) {
		setChecked(checked);
		return this;
	}

	public Cb setValue0(boolean val) {
		setValue(val);
		return this;
	}
}
