package mp.utl_odb.tree;

import lombok.Getter;
import lombok.Setter;
import mpc.env.AP;
import mpc.exception.RequiredRuntimeException;
import mpc.rfl.RFL;
import mpc.str.ObjTo;
import mpc.types.abstype.AbsType;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.UST;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AppPropDef<T> extends AbsType<T> {

	@Setter
	private UTree treeProps;

	private Supplier<T> supplierDefValue;

	public AppPropDef setSupplierDefValue(Supplier<T> supplierDefValue) {
		this.supplierDefValue = supplierDefValue;
		return this;
	}

	@Setter
	private Optional<T> _cachedValue;
	@Getter
	private String _desc;


	public AppPropDef<T> cloneWithNewValue(T val) {
		AppPropDef<T> tAppPropDef = new AppPropDef<>(name(), val, type(), _desc);
		tAppPropDef.setTreeProps(treeProps);
		return tAppPropDef;
	}

	public AppPropDef(String key, T defVal) {
		this(key, defVal, (Class<T>) IT.NN(defVal).getClass());
	}

	public AppPropDef(String key, T defVal, Class<T> asType) {
		this(key, defVal, asType, key);
	}

	public AppPropDef(Object[] pare) {
		this((String) pare[0], (T) pare[1], (Class) pare[2], (String) pare[3]);
	}

	public AppPropDef(String key, T defVal, Class<T> asType, String desc) {
		super(key, defVal, asType);
		this._desc = desc;
	}

	public AppPropDef(Pare<String, Class> pare, boolean... initDefaultValue) {
		super(pare.key(), ARG.isDefEqTrue(initDefaultValue) ? (T) ObjTo.toDefaultValue(pare.val()) : null, pare.val());
	}


	public static List<AppPropDef> getAllProps(Object... treeProps) {
		IT.isEven2(treeProps.length);
		return (List) Pare.ofKeyValues(treeProps).stream().flatMap(p -> getAllPropsWithTreeProps((Pare) p).stream()).collect(Collectors.toList());
	}

	public static <P extends Class, T extends UTree> List<AppPropDef> getAllPropsWithTreeProps(Pare<P, T> pareTreeProps) {
		List<AppPropDef> props = RFL.fieldValuesSt(pareTreeProps.key(), AppPropDef.class, true, (List<AppPropDef>) ARR.EMPTY_LIST);
		props.forEach(p -> p.setTreeProps(pareTreeProps.val()));
		return props;
	}


	public void update(Object val) {
		T val0 = doValidatedPropValue(val);
		if (treeProps != null) {
			treeProps.put(name(), val0);
		}
		setValue(val0);
	}

	@Override
	public AbsType<T> setValue(T value) {
		super.setValue(value);
		_cachedValue = Optional.of(value);
		return this;
	}

	public T doValidatedPropValue() {
		return doValidatedPropValue(getValue());
	}

	public T doValidatedPropValue(Object val) {
		T val0 = ObjTo.objTo(IT.NN(val), type(), null);
		IT.NN(val0, "Illegal value '%s' for type '%s'", val, type().getSimpleName());
		return val0;
	}

	public T getValueOrDefault(T... defRq) {
		return getValueOrDefault(true, true, true, supplierDefValue != null, defRq);
	}

	public T getValueOrDefault(boolean checkTree, boolean checkSys, boolean checkAP, boolean checkSupplier, T... defRq) {
		Optional<T> lazyLoaded = _cachedValue;
		if (X.notEmpty(lazyLoaded)) {
			return lazyLoaded.get();
		}
		T vl = getValueFromOrDefaultImpl(checkTree, checkSys, checkAP, checkSupplier, defRq);
		set_cachedValue(Optional.ofNullable(vl));
		return vl;
	}

	private T getValueFromOrDefaultImpl(boolean checkTree, boolean checkSys, boolean checkAP, boolean checkSupplier, T... defRq) {

		Class<T> asType = type();

		T targetVal;

		if (checkTree) {
			targetVal = getAs(asType, null);
			if (targetVal != null) {
				return targetVal;
			}
		}

		if (checkSys) {
			String spVal = System.getProperty(name(), null);
			if (X.notEmpty(spVal)) {
				return UST.strTo(spVal, asType);
			}
		}

		if (checkAP) {
			targetVal = AP.getAs(name(), asType, null);
			if (targetVal != null) {
				return targetVal;
			}
		}

		targetVal = val();

		if (X.notEmptyObj_Str_Cll(targetVal)) {
			return targetVal;
		}

		if (checkSupplier) {
			targetVal = supplierDefValue.get();
			if (X.notEmptyObj_Str_Cll(targetVal)) {
				return targetVal;
			}
		}

		return ARG.toDefThrow(() -> new RequiredRuntimeException("AppProp not found '%s', checkTree:%s,  checkSys:%s,  checkAP:%s, checkSupplier:%s ", name(), checkTree, checkSys, checkAP, checkSupplier), defRq);
	}


	public <T> T getAs(Class<T> asType, T... defRq) {
		if (treeProps != null) {
			return treeProps.getAs(name(), asType, defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set tree or not call this method, key=%s", name()), defRq);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
				"name='" + name() + '\'' +
				", value=" + (getValueAsObject() instanceof CharSequence ? "'" + getValueAsObject() + "'" : getValueAsObject()) +
				", type=" + type().getSimpleName() +
				", desc=" + _desc +
				'}';
	}

}
