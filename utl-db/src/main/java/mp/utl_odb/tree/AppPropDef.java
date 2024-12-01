package mp.utl_odb.tree;

import lombok.Getter;
import lombok.Setter;
import mpc.env.AP;
import mpc.exception.RequiredRuntimeException;
import mpc.str.ObjTo;
import mpc.types.abstype.AbsType;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.str.SPLIT;
import mpu.str.UST;

import java.util.Optional;
import java.util.function.Supplier;

public class AppPropDef<T> {

	@Setter
	private UTree treeProps;

	private Supplier<T> supplierDefValue;

	public final AbsType<T> seed;

	private String title;

	public AppPropDef title(String title) {
		this.title = title;
		return this;
	}

//	public AbsType<T> seed() {
//		return seed;
//	}

	public AppPropDef setSupplierDefValue(Supplier<T> supplierDefValue) {
		this.supplierDefValue = supplierDefValue;
		return this;
	}

	@Setter
	private Optional<T> _cachedValue;
	@Getter
	private String _desc;


//	public AppPropDef<T> cloneWithNewValue(T val) {
//		AppPropDef<T> tAppPropDef = new AppPropDef<>(name(), val, type(), _desc);
//		tAppPropDef.setTreeProps(treeProps);
//		return tAppPropDef;
//	}

	public AppPropDef(String key, T defVal) {
		this(key, defVal, (Class<T>) IT.NN(defVal).getClass());
	}

	public AppPropDef(String key, T defVal, Class<T> asType) {
		this(key, defVal, asType, key);
	}

	public AppPropDef(Object[] pare) {
		this((String) pare[0], (T) pare[1], (Class) pare[2], (String) pare[3]);
	}

//	final T defVal;
//
//	public T getDefaultValue() {
//		return defVal;
//	}

	public Class<T> getDefaultType() {
		return seed.type();
	}

	public AppPropDef(String key, T defVal, Class<T> asType, String desc) {
		this._desc = desc;
		this.seed = new AbsType<>(key, defVal, asType);
//		this.defVal = defVal;
	}

//	public AppPropDef(Pare<String, Class> pare, boolean... initDefaultValue) {
//		this.seed = new AbsType<>(pare.key(), ARG.isDefEqTrue(initDefaultValue) ? (T) ObjTo.toDefaultValue(pare.val()) : null, pare.val());
//	}

	public void update(Object val) {
		T val0 = doValidatedPropValue(val);
		if (treeProps != null) {
			treeProps.put(seed.name(), val0);
		}
		setValue(val0);
	}

	public AppPropDef<T> setValue(T value) {
		seed.setValue(value);
		_cachedValue = Optional.of(value);
		return this;
	}

	public T doValidatedPropValue() {
		return doValidatedPropValue(seed.getValue());
	}

	public T doValidatedPropValue(Object val) {
		T val0 = ObjTo.objTo(IT.NN(val), seed.type(), null);
		IT.NN(val0, "Illegal value '%s' for type '%s'", val, seed.type().getSimpleName());
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

		Class<T> asType = seed.type();

		T targetVal;

		if (checkTree) {
			targetVal = getAs(asType, null);
			if (targetVal != null) {
				return targetVal;
			}
		}

		if (checkSys) {
			String spVal = System.getProperty(seed.name(), null);
			if (X.notEmpty(spVal)) {
				return UST.strTo(spVal, asType);
			}
		}

		if (checkAP) {
			targetVal = AP.getAs(seed.name(), asType, null);
			if (targetVal != null) {
				return targetVal;
			}
		}

		targetVal = seed.val();

		if (X.notEmptyObj_Str_Cll(targetVal)) {
			return targetVal;
		}

		if (checkSupplier) {
			targetVal = supplierDefValue.get();
			if (X.notEmptyObj_Str_Cll(targetVal)) {
				return targetVal;
			}
		}

		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}

//		L.error();
		throw new RequiredRuntimeException("AppProp not found '%s', checkTree:%s,  checkSys:%s,  checkAP:%s, checkSupplier:%s ", seed.name(), checkTree, checkSys, checkAP, checkSupplier);
	}


	public <T> T getAs(Class<T> asType, T... defRq) {
		if (treeProps != null) {
			return treeProps.getAs(seed.name(), asType, defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set tree or not call this method, key=%s", seed.name()), defRq);
	}

	public <T> String[] getValueSplited(String del) {
		return SPLIT.argsBy((String) getValueOrDefault(), del);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
				"name='" + seed.name() + '\'' +
				", value=" + (seed.getValueAsObject() instanceof CharSequence ? "'" + seed.getValueAsObject() + "'" : seed.getValueAsObject()) +
				", type=" + seed.type().getSimpleName() +
				", desc=" + _desc +
				'}';
	}

	public String name() {
		return seed.name();
	}

}
