package zk_com.ext;


import lombok.Getter;
import mpc.rfl.RFL;
import mpc.str.ObjTo;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.core.ENUM;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import zk_com.base.Lb;
import zk_com.base_ctr.Span0;
import zk_page.ZKS;

import java.util.Collection;
import java.util.List;

//
public class Ddl<T> extends Span0 {

	public static final String DEL = SYMJ.POINT;
//	public static final String DEL = SYMJ.POINT_DBL;

	public Ddl addEventListener(EventListener<? extends Event> listener) {
		super.addEventListener(Events.ON_CLICK, listener);
		return this;
	}

	private @Getter T _value;
	private final @Getter Class genericType;

	public static Ddl<String> of(String... selectable) {
		return new Ddl(ARR.as(selectable));
	}

	public Ddl(Collection<T> choices) {
		this(null, choices);
	}

	public Ddl(T value) {
		this(value, value instanceof Enum ? ENUM.getValues((Class) value.getClass()) : ARR.EMPTY_LIST);
	}

	public Ddl(Class<? extends Enum> value) {
		this(ENUM.getValues((Class) value));
	}

	private final Collection<T> choices;

	public Ddl(T value, Collection<T> choices) {
		super();
		this._value = value;
		Class genericType1 = RFL.getGenericType(getClass(), null);
		this.genericType = genericType1 == null ? (value == null ? ARRi.first(choices).getClass() : value.getClass()) : genericType1;
		this.choices = choices;
	}

	private Object fontSize_px_pct;

	@Override
	public Ddl font_size(Object px_pct) {
//		return super.font_size(px_pct);
		this.fontSize_px_pct = px_pct;
		return this;
	}

	protected void init() {
		fillItems(choices);
	}

	public Ddl fillItems(Collection<T> choices, boolean... beforeRemoveAll) {
		if (ARG.isDefEqTrue(beforeRemoveAll)) {
			clearDdItems(true);
		}
		int i = 0;
		for (T choice : choices) {
			addDdItem(choice, choices.size() == ++i, X.equals(choice, _value));
		}
		return this;
	}

	public Ddl addDdItem(T value, boolean last, boolean... selected) {
		if (ARG.isDefEqTrue(selected)) {
			set_value(value);
		}
		Lb item = newComboitem(value, last, selected);
		appendChild(item);
		return this;
	}

	protected Lb newComboitem(T value, boolean last, boolean... selected) {
		String org = String.valueOf(value);
		String label = last ? org : value + DEL;
		Lb lb = new Lb(label);
		applyItemStyle(lb);
		if (ARG.isDefEqTrue(selected)) {
			applySelectedStyle(lb);
		}
		lb.onCLICK((e) -> {
			boolean isOk = onHappensClickItem((MouseEvent) e, value);
			if (!isOk) {
				return;
			}
			applySelectedStyle(lb);
			set_value(value);
			List<Component> children = Ddl.this.getChildren();
			children.forEach(c -> {
				ZKS.rmStyleAttr((HtmlBasedComponent) c, "font-weight");
				ZKS.rmStyleAttr((HtmlBasedComponent) c, "text-decoration");
			});
		});

//		lb.onDBLCLICK(e -> onDblClickItem(value));

		return lb;
	}

	protected void applyItemStyle(Lb lb) {
		lb.cursorOnOver();
		if (fontSize_px_pct != null) {
			lb.font_size(fontSize_px_pct);
		}
	}

	protected void applySelectedStyle(Lb lb) {
		ZKS.addStyleAttr(lb, "font-weight", "bold");
		ZKS.addStyleAttr(lb, "text-decoration", "underline");
	}

	protected T toValueAs(Object value) {
		return value instanceof String ? (T) value : (T) ObjTo.objTo(value, getGenericType());
	}

	public boolean onHappensClickItem(MouseEvent e, T value) {
		L.info("ddl value:" + value);
		return true;
	}

//	public boolean onDblClickItem(T value) {
//		L.info("ddl value:" + value);
//		return true;
//	}

	public void clearDdItems(boolean withValue) {
		if (withValue) {
			set_value(null);
		}
		getChildren().clear();
	}

	public void set_value(T _value) {
		this._value = _value;
	}
}
