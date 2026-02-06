package zk_com.base_ext;

import lombok.Getter;
import mpu.core.ENUM;
import mpu.X;
import mpc.str.sym.SYMJ;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Dd;
import zk_com.base.Lb;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.SpanSwitch;
import zk_com.core.IZComExt;
import zk_form.events.ITouchEvent;
import zk_page.ZKS;

public abstract class EnumSwitcherIcon<T extends Enum> extends SpanSwitch implements ITouchEvent {

	final Class<T> ddType;

	public EnumSwitcherIcon(Class<T> ddType) {
		this(ddType, null, null, false);
	}

	public Menupopup0 addToMenu;

	public void onClickInfo(Event event) {//EventListener
		Info info = ITouchEvent.getDataInfo(event);
		if (info.ms > 1000) {
			onSwitchEvent();
		}
	}

	private @Getter Dd dd;

	public EnumSwitcherIcon(Class<T> ddType, String eventToShowDd, IZComExt holderMenu, boolean visibleOnMove) {
		super();

		this.ddType = ddType;

		SpanSwitch _THIS = this;

		Lb switcherLb = getSensCom();

		if (holderMenu != null) {
			addToMenu = holderMenu.appendMenupopup(switcherLb);
			if (X.empty(eventToShowDd)) {
				addToMenu.addMI(getIcon(), (SerializableEventListener) -> onSwitchEvent());
			} else {
				addToMenu.addMI(getIcon(), eventToShowDd, (SerializableEventListener) -> onSwitchEvent());
			}
		} else {
			if (X.notEmpty(eventToShowDd)) {
				switcherLb.addEventListener(eventToShowDd, (SerializableEventListener<Event>) event -> {
					onSwitchEvent();
				});
			} else {
				ITouchEvent.initEvent(this, true);
			}
		}

		if (visibleOnMove) {
			ZKS.onVisibleOnMove(switcherLb, false);
		}

		dd = new Dd(this.ddType);
		dd.onSELECTION((SerializableEventListener) event1 -> {
			_THIS.switchToNext();
			T ddTypeValue = (T) ENUM.valueOf(dd.getValue(), this.ddType);
			applyPosition(ddTypeValue);
		});
		_THIS.appendChild(switcherLb);
		_THIS.appendChild(dd);

	}

	public String getIcon() {
		return SYMJ.ARROW_MOVABLE;
	}

	private Lb lbSensCom;

	public Lb getSensCom() {
		return lbSensCom == null ? (lbSensCom = (Lb) new Lb(SYMJ.EYE)) : lbSensCom;
	}

	public void onSwitchEvent() {
		switchToNext();
	}

	protected abstract void applyPosition(T typeValue);

}
