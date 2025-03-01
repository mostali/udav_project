package mp.utl_odb.tree;

import lombok.Getter;
import lombok.Setter;
import mpc.env.AP;
import mpc.env.AutoInitClassProperty;
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


	private @Setter UTree treeProps;

	private @Getter Supplier<T> defValueSupplier;

	public final AbsType<T> defValueHolder;

	private @Getter String desc;

	public AppPropDef desc(String desc) {
		this.desc = desc;
		return this;
	}


	public AppPropDef setDefValueSupplier(Supplier<T> defValueSupplier) {
		this.defValueSupplier = defValueSupplier;
		return this;
	}

	private Optional<T> _cachedValue;

	public AppPropDef(String key, T defVal) {
		this(key, defVal, (Class<T>) IT.NN(defVal).getClass());
	}

	public AppPropDef(String key, T defVal, Class<T> asType) {
		this(key, defVal, asType, key);
	}

	public AppPropDef(Object[] pare) {
		this((String) pare[0], (T) pare[1], (Class) pare[2], (String) pare[3]);
	}

	public Class<T> getDefaultType() {
		return defValueHolder.type();
	}

	public AppPropDef(String key, T defVal, Class<T> asType, String desc) {
		this.desc = desc;
		this.defValueHolder = new AbsType<>(key, defVal, asType);
	}

	public void update(Object val) {
		T val0 = doValidatedPropValue(val);
		if (treeProps != null) {
			treeProps.put(getPropName(), val0);
		}
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
		return findValueFrom_Store_Or_Default(true, true, true, defValueSupplier != null, defRq);
	}

	public T findValueFrom_Store_Or_Default(boolean checkTree, boolean checkSys, boolean checkAP, boolean checkSupplier, T... defRq) {
		Optional<T> lazyLoaded = _cachedValue;
		if (X.notEmpty(lazyLoaded)) {
			return lazyLoaded.get();
		}
		T vl = findValueFrom_STOREs(checkTree, checkSys, checkAP, null);
		if (vl != null) {
			return vl;
		}
		vl = findValueFrom_Default(checkSupplier, defRq);
		set_cachedValue(vl);
		return vl;
	}


//	public AppPropDef<T> setValue(T value) {

	/// /		defValueHolder.setValue(value);
	/// /		_cachedValue = Optional.of(value);
//		set_cachedValue(value);
//		return this;
//	}
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

	private T findValueFrom_Default(boolean checkSupplier, T... defRq) {

//		Class<T> asType = defValueHolder.type();

		T targetVal = defValueHolder.val();

		if (X.notEmptyObj_Str_Cll(targetVal)) {
			return targetVal;
		}

		if (checkSupplier) {
			targetVal = defValueSupplier.get();
			if (X.notEmptyObj_Str_Cll(targetVal)) {
				return targetVal;
			}
		}

		return ARG.toDefThrowMsg(() -> X.f("Value '%s' from DEFAULT not found, checkSupplier:%s ", getPropName(), checkSupplier), defRq);
	}


	public <T> T getAs(Class<T> asType, T... defRq) {
		if (treeProps != null) {
			return treeProps.getAs(getPropName(), asType, defRq);
		}
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

	public AppPropDef autoInitHolder(Class autoInitPropHolder) {
		this.autoInitPropHolder = autoInitPropHolder;
		return this;
	}

	public boolean isAutoInit() {
		return autoInitPropHolder != null;
	}
}
