package mp.utl_odb.tree;

import lombok.Setter;
import mpc.ERR;
import mpc.args.ARG;
import mpc.arr.Arr;
import mpc.rfl.RFL;
import mpc.types.pare.Pare;

import java.util.ArrayList;
import java.util.List;

public class AppPropDef<T> extends Pare<String, T> {

	@Setter
	public UTree treeProps;

	public AppPropDef(String key, T defVal) {
		super(key, defVal);
	}

	public static List<AppPropDef> getAllProps(Object... treeProps) {
		ERR.isEven2(treeProps.length);
		List<Pare<Class, UTree>> evens = Pare.ofKeyValues(treeProps);
		List allProps = new ArrayList();
		for (Pare<Class, UTree> even : evens) {
			allProps.addAll(getAllPropsFromTreeProps(even));
		}
		return allProps;
	}

	public static <P extends Class, T extends UTree> List<AppPropDef> getAllPropsFromTreeProps(Pare<P, T> treeProps) {
		List<AppPropDef> props = RFL.fieldValuesSt(treeProps.key(), AppPropDef.class, true, (List<AppPropDef>) Arr.EMPTY_LIST);
		props.forEach(p -> p.setTreeProps(treeProps.val()));
		return props;
	}


	public void update(Object val) {
		treeProps.put(key(), val);
	}

	public <T> T getValueOrDefault(T... def) {
		T defVal = (T) val();
		T as = getAs(key(), (Class<T>) defVal.getClass(), null);
		if (as != null) {
			return as;
		}
		if (ARG.isDef(def)) {
			return ARG.toDef(def);
		}
		return defVal;
	}

	public Integer getAsInt(String key, Integer... defRq) {
		Integer val = treeProps.getAs(key, Integer.class, defRq);
		return val;
	}

	public String getAsStr(String key, String... defRq) {
		String val = treeProps.getAs(key, String.class, defRq);
		return val;
	}

	public <T> T getAs(String key, Class<T> asType, T... defRq) {
		return treeProps.getAs(key, asType, defRq);
	}

}
