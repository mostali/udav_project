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
import org.zkoss.zk.ui.Page;
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

	//	public static final String DEL = SYMJ.POINT;
	public static final String DEL = SYMJ.POINT_DBL;

	public Ddl addEventListener(EventListener<? extends Event> listener) {
		super.addEventListener(Events.ON_CLICK, listener);
		return this;
	}

	private @Getter T _value;
	private final @Getter Class genericType;

	public Ddl(Collection<T> choices) {
		this(ARRi.first(choices, null), choices);
	}

	public Ddl(T value) {
		this(value, value instanceof Enum ? ENUM.getValues((Class) value.getClass()) : ARR.EMPTY_LIST);
	}

	public Ddl(T value, Collection<T> choices) {
		super();
		this._value = value;
		this.genericType = RFL.getGenericType(getClass());
		fillItems(choices);
	}

//	private Class<? extends Enum> asEnum = null;
//
//	public Ddl(Enum type) {
//		this(type.getClass());
//		asEnum = type.getClass();
//		setValue(type.name());
//	}
//
//	public Ddl(Class<? extends Enum> type) {
//		this(ENUM.getValuesAsString(type));
//		asEnum = type;
//	}


//	public static Ddl ofDir(String defValue_Or_First, Collection<String> dirs, Function<String, Path> actionChoiceItem) {
//		Ddl dirFileNames = X.empty(defValue_Or_First) ? new Ddl(dirs) : new Ddl(defValue_Or_First, dirs);
//		dirFileNames.onCHANGE((e) -> {
//			if (actionChoiceItem != null) {
//				actionChoiceItem.apply(dirFileNames.getValue());
//			}
//		});
//		return dirFileNames;
//	}
//
//	public <T extends Enum> T getValueAsEnum(Class<T>... asEnum) {
//		return ARG.isDef(asEnum) ?//
//				(T) ENUM.valueOf(getValue(), IT.NN(ARG.toDef(asEnum)), false) :
//				(T) ENUM.valueOf(getValue(), IT.NN(this.asEnum, "set enum class in constructor"), false)
//				;
//	}


	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
		//override
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
		appendChild(newComboitem(value, last, selected));
		return this;
	}

	protected Lb newComboitem(T value, boolean last, boolean... selected) {
		String org = String.valueOf(value);
		String label = last ? org : value + DEL;
		Lb lb = new Lb(label);
		lb.cursorOnOver();
		if (ARG.isDefEqTrue(selected)) {
			setSelectedStyle(lb);
		}
		lb.onCLICK((e) -> {
			boolean isOk = onClickItem((MouseEvent) e, value);
			if (!isOk) {
				return;
			}
			setSelectedStyle(lb);
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

	protected void setSelectedStyle(Lb lb) {
		ZKS.addStyleAttr(lb, "font-weight", "bold");
		ZKS.addStyleAttr(lb, "text-decoration", "underline");
	}

	protected T toValueAs(Object value) {
		return value instanceof String ? (T) value : (T) ObjTo.objTo(value, getGenericType());
	}


	public boolean onClickItem(MouseEvent e, T value) {
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
