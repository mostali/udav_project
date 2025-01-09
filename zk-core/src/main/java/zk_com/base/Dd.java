package zk_com.base;


import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.fd.EFT;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.core.ENUM;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import zk_com.core.IZCom;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Dd extends Combobox implements IZCom {

	public Dd addEventListener(EventListener<? extends Event> listener) {
		super.addEventListener(Events.ON_CLICK, listener);
		return this;
	}

	public Dd(String value) {
		super(value);
	}

	public Dd(String value, Collection<String> choices) {
		super(value);
		fillItems(choices);
	}

	public static Dd ofDir(String defValue_Or_First, Collection<String> dirs, Function<String, Path> actionChoiceItem) {
		Dd dirFileNames = X.empty(defValue_Or_First) ? new Dd(dirs) : new Dd(defValue_Or_First, dirs);
		dirFileNames.onCHANGE((e) -> {
			if (actionChoiceItem != null) {
				actionChoiceItem.apply(dirFileNames.getValue());
			}
		});
		return dirFileNames;
	}

	private Class<? extends Enum> asEnum = null;

	public Dd(Enum type) {
		this(type.getClass());
		asEnum = type.getClass();
		setValue(type.name());
	}

	public Dd(Class<? extends Enum> type) {
		this(ENUM.getValuesAsString(type));
		asEnum = type;
	}

	public <T extends Enum> T getValueAsEnum(Class<T>... asEnum) {
		return ARG.isDef(asEnum) ?//
				(T) ENUM.valueOf(getValue(), IT.NN(ARG.toDef(asEnum)), false) :
				(T) ENUM.valueOf(getValue(), IT.NN(this.asEnum, "set enum class in constructor"), false)
				;
	}


	public Dd(Collection<String> choices) {
		super(ARRi.first(choices, null));
		fillItems(choices);
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
		//override
	}

	public Dd fillItems(Collection<String> choices, boolean... beforeRemoveAll) {
		if (ARG.isDefEqTrue(beforeRemoveAll)) {
			clearDdItems(true);
		}
		choices.forEach(this::addDdItem);
		return this;
	}

	public Dd addDdItem(String value, boolean... main) {
		appendChild(newComboitem(value));
		if (ARG.isDefEqTrue(main)) {
			setValue(value);
		}
		return this;
	}

	protected Component newComboitem(String value) {
		return new Comboitem(value);
	}

	public Dd onCHANGE(SerializableEventListener serializableEventListener) {
		addEventListener(Events.ON_CHANGE, serializableEventListener);
		return this;
	}

	public Dd onCHANGING(SerializableEventListener serializableEventListener) {
		addEventListener(Events.ON_CHANGING, serializableEventListener);
		return this;
	}

	public Dd onOK(SerializableEventListener serializableEventListener) {
		addEventListener(Events.ON_OK, serializableEventListener);
		return this;
	}

	public Dd onSELECTION(SerializableEventListener serializableEventListener) {
		addEventListener(Events.ON_SELECTION, serializableEventListener);
		return this;
	}


	public void clearDdItems(boolean withValue) {
		if (withValue) {
			setValue(null);
		}
		getChildren().clear();
	}


}
