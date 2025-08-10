package mpc.env;

import lombok.Getter;
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

public class AbstractAppPropDef<T> {

	private @Getter Supplier<T> defValueSupplier;

	public final AbsType<T> defValueHolder;

	private @Getter String desc;

	public AbstractAppPropDef desc(String desc) {
		this.desc = desc;
		return this;
	}


	public AbstractAppPropDef setDefValueSupplier(Supplier<T> defValueSupplier) {
		this.defValueSupplier = defValueSupplier;
		return this;
	}

	private Optional<T> _cachedValue;

	public AbstractAppPropDef(String key, T defVal) {
		this(key, defVal, (Class<T>) IT.NN(defVal).getClass());
	}

	public AbstractAppPropDef(String key, T defVal, Class<T> asType) {
		this(key, defVal, asType, key);
	}

	public AbstractAppPropDef(Object[] pare) {
		this((String) pare[0], (T) pare[1], (Class) pare[2], (String) pare[3]);
	}

	public Class<T> getDefaultType() {
		return defValueHolder.type();
	}

	public AbstractAppPropDef(String key, T defVal, Class<T> asType, String desc) {
		this.desc = desc;
		this.defValueHolder = new AbsType<>(key, defVal, asType);
	}

	public void update(Object val) {
		T val0 = doValidatedPropValue(val);
//		if (treeProps != null) {
//			treeProps.putAppend(getPropName(), val0);
//		}
		set_cachedValue(val0);
	}


	public T doValidatedPropValue() {
		return doValidatedPropValue(defValueHolder.getValue());
	}

	public T doValidatedPropValue(Object val) {
		T val0 = ObjTo.objTo(IT.NN(val), defValueHolder.type(), null);
		IT.NN(val0, "Illegal value '%s' for type '%s'", val, defValueHolder.type().getSimpleName());
		return val0;
	}

	public T getValueOrDefault(T... defRq) {
		return findValueFrom_Store_Or_Default(true, true, true, defRq);
	}

	public T getValueFromStoreOrDefault() {
		T val = findValueFrom_Store_Or_Default(true, true, true, null);
		if (val != null) {
			return val;
		}
		val = defValueHolder.getValueOr(null);
		if (val != null) {
			return val;
		} else if (defValueSupplier != null) {
			return defValueSupplier.get();
		}
		return null;
	}

	private T findValueFrom_Store_Or_Default(boolean checkTree, boolean checkSys, boolean checkAP, T... defRq) {
		Optional<T> lazyLoaded = _cachedValue;
		if (X.notEmpty(lazyLoaded)) {
			return lazyLoaded.get();
		}
		T vl = findValueFrom_STOREs(checkTree, checkSys, checkAP, null);
		if (vl != null) {
			return vl;
		}
		vl = getDefaultValueFromHolder_or_Supplier(defRq);
		set_cachedValue(vl);
		return vl;
	}

	public void set_cachedValue(T vl) {
		this._cachedValue = Optional.ofNullable(vl);
		doAutoInit(vl);
	}

	public void doAutoInit(T vl) {
		if (autoInitPropHolder != null && vl != null) {
			AutoInitClassProperty.setValueObject(autoInitPropHolder, getPropName(), vl);
		}
	}

	private T findValueFrom_STOREs(boolean checkTree, boolean checkSys, boolean checkAP, T... defRq) {

		Class<T> asType = defValueHolder.type();

		T targetVal;

		if (checkTree) {
			targetVal = getAs(asType, null);
			if (targetVal != null) {
				return targetVal;
			}
		}

		if (checkSys) {
			String spVal = System.getProperty(getPropName(), null);
			if (X.notEmpty(spVal)) {
				return UST.strTo(spVal, asType);
			}
		}

		if (checkAP) {
			targetVal = AP.getAs(getPropName(), asType, null);
			if (targetVal != null) {
				return targetVal;
			}
		}

		return ARG.toDefThrowMsg(() -> X.f("Value '%s' from STORE's not found, checkTree:%s,  checkSys:%s,  checkAP:%s ", getPropName(), checkTree, checkSys, checkAP), defRq);
	}

	public T getDefaultValueFromHolder_or_Supplier(T... defRq) {

		T targetVal = defValueHolder.val();

		if (X.notEmptyObj_Str_Cll(targetVal)) {
			return targetVal;
		}

		boolean hasDefaultSupplier = defValueSupplier != null;
		if (hasDefaultSupplier) {
			targetVal = defValueSupplier.get();
			if (X.notEmptyObj_Str_Cll(targetVal)) {
				return targetVal;
			}
		}

		return ARG.toDefThrowMsg(() -> X.f("Value '%s' from DEFAULT not found, checkSupplier:%s ", getPropName(), hasDefaultSupplier), defRq);
	}


	public <T> T getAs(Class<T> asType, T... defRq) {
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set tree or not call this method, key=%s", getPropName()), defRq);
	}

	public <T> String[] getValueSplited(String del) {
		return SPLIT.argsBy((String) getValueOrDefault(), del);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
				"name='" + getPropName() + '\'' +
				", value=" + (defValueHolder.getValueAsObject() instanceof CharSequence ? "'" + defValueHolder.getValueAsObject() + "'" : defValueHolder.getValueAsObject()) +
				", type=" + defValueHolder.type().getSimpleName() +
				", desc=" + desc +
				'}';
	}

	public String getPropName() {
		return defValueHolder.name();
	}

	public String toTitle() {
		return X.equals(getDesc(), getPropName()) ? getDesc() : (getPropName() + ":" + getDesc());
	}

	//
	//
	private Class autoInitPropHolder;

	public AbstractAppPropDef autoInitHolder(Class autoInitPropHolder) {
		this.autoInitPropHolder = autoInitPropHolder;
		return this;
	}

	public boolean isAutoInit() {
		return autoInitPropHolder != null;
	}
}
