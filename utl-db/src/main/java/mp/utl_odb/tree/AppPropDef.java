package mp.utl_odb.tree;

import lombok.Setter;
import mpc.env.AbstractAppPropDef;
import mpc.exception.RequiredRuntimeException;
import mpu.core.ARG;

public class AppPropDef<T> extends AbstractAppPropDef<T> {

	private @Setter UTree treeProps;

	public AppPropDef(String key, T defVal) {
		super(key, defVal);
	}

	public AppPropDef(String key, T defVal, Class<T> asType) {
		super(key, defVal, asType);
	}

	@Override
	public void update(Object val) {
		T val0 = doValidatedPropValue(val);
		if (treeProps != null) {
			treeProps.putAppend(getPropName(), val0);
		}
		set_cachedValue(val0);
	}


	@Override
	public <T> T getAs(Class<T> asType, T... defRq) {
		if (treeProps != null) {
			return treeProps.getValueAs(getPropName(), asType, defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set tree or not call this method, key=%s", getPropName()), defRq);
	}

}
