package mp.utl_odb.tree;

import mpc.rfl.RFL;
import mpu.IT;
import mpu.core.ARR;
import mpu.pare.Pare;

import java.util.List;
import java.util.stream.Collectors;

public interface IAppProps {

	//	public static List<AppPropDef> getAllProps(UTree treeProps) {
	//		List<AppPropDef> appLogProps = RFL.fieldValuesSt(AppZosProps.class, AppPropDef.class, true, (List<AppPropDef>) ARR.EMPTY_LIST);
	//		appLogProps.forEach(p -> p.setTreeProps(treeProps));
	//		return appLogProps;
	//	}

	static <P extends Class, T extends UTree> List<AppPropDef> getAllPropsWithTreeProps(Pare<P, T> pareTreeProps) {
		List<AppPropDef> props = RFL.fieldValuesSt(pareTreeProps.key(), AppPropDef.class, true, (List<AppPropDef>) ARR.EMPTY_LIST);
		props.forEach(p -> p.setTreeProps(pareTreeProps.val()));
		return props;
	}

	static List<AppPropDef> getAllProps(Object... treeProps) {
		IT.isEven2(treeProps.length);
		return (List) Pare.ofKeyValues(treeProps).stream().flatMap(p -> getAllPropsWithTreeProps((Pare) p).stream()).collect(Collectors.toList());
	}


}
