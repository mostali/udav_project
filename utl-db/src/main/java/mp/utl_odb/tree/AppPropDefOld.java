//package mp.utl_odb.tree;
//
//import lombok.Setter;
//import mpc.env.AP;
//import mpc.exception.RequiredRuntimeException;
//import mpu.IT;
//import mpu.X;
//import mpu.core.ARG;
//import mpu.core.ARR;
//import mpc.rfl.RFL;
//import mpu.pare.Pare;
//import mpu.str.UST;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
////public class AppPropDef<T> extends Pare<String, T> {
//public class AppPropDef<T> extends Pare<String, T> {
//
//	@Setter
//	public UTree treeProps;
//
//	final Class<T> asDefType;
//
//	public AppPropDef(String key, T defVal) {
//		this(key, defVal, (Class<T>) IT.NN(defVal).getClass());
//	}
//
//	public AppPropDef(String key, T defVal, Class<T> asType) {
//		super(key, defVal);
//		if (defVal == null) {
//			this.asDefType = IT.NN(asType);
//		} else {
//			if (asType != null) {
//				asDefType = asType;
//			} else {
//				asDefType = (Class<T>) defVal.getClass();
//			}
//		}
//	}
//
//	public static List<AppPropDef> getAllProps(Object... treeProps) {
//		IT.isEven2(treeProps.length);
//		return (List) Pare.ofKeyValues(treeProps).stream().flatMap(t -> getAllPropsWithTreeProps((Pare) t).stream()).collect(Collectors.toList());
////		List<Pare<Class, UTree>> evens = Pare.ofKeyValues(treeProps);
////		List allProps = new ArrayList();
////		for (Pare<Class, UTree> even : evens) {
////			allProps.addAll(getAllPropsWithTreeProps(even)?);
////		}
////		return allProps;
//	}
//
//	public static <P extends Class, T extends UTree> List<AppPropDef> getAllPropsWithTreeProps(Pare<P, T> treeProps) {
//		List<AppPropDef> props = RFL.fieldValuesSt(treeProps.key(), AppPropDef.class, true, (List<AppPropDef>) ARR.EMPTY_LIST);
//		props.forEach(p -> p.setTreeProps(treeProps.val()));
//		return props;
//	}
//
//
//	public void update(Object val) {
//		treeProps.put(key(), val);
//	}
//
//	//	public <T> T getValueFromTreeOrDefault(T... defRq) {
////		T defVal = (T) val();
////		T as = getAs(key(), (Class<T>) defVal.getClass(), null);
////		if (as != null) {
////			return as;
////		}
////		return ARG.toDefOr(defVal, defRq);
////	}
//	public <T> T getValueFromAllOrDefault(T... defRq) {
//		return getValueFromOrDefault(true, true, true, defRq);
//	}
//
//	public <T> T getValueFromOrDefault(boolean checkTree, boolean checkSys, boolean checkAP, T... defRq) {
//		Class<T> asType = (Class<T>) asDefType;
//		T targetVal;
//		if (checkTree) {
//			targetVal = getAs(asType, null);
//			if (targetVal != null) {
//				return targetVal;
//			}
//		}
//		if (checkSys) {
//			String spVal = System.getProperty(key(), null);
//			if (X.notEmpty(spVal)) {
//				return UST.strTo(spVal, asType);
//			}
//		}
//		if (checkAP) {
//			targetVal = AP.getAs(key(), asType, null);
//			if (targetVal != null) {
//				return targetVal;
//			}
//		}
//		T defVal = (T) val();
//		if (X.notEmptyObj(defVal)) {
//			return defVal;
//		}
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("props not found '%s', checkTree:%s,  checkSys:%s,  checkAP:%s ", key(), checkTree, checkSys, checkAP), defRq);
//	}
//
////	private <T> Class<T> getDefValType() {
////		T defVal = (T) val();
////		return (Class<T>) defVal.getClass();
////	}
//
////	public Integer getAsInt(Integer... defRq) {
////		return getAs(Integer.class, defRq);
////	}
//
////	public Long getAsLong(Long... defRq) {
////		return getAs(Long.class, defRq);
////	}
//
////	public String getAsStr(String... defRq) {
////		return getAs(String.class, defRq);
////	}
//
//	public Boolean getAsBool(Boolean... defRq) {
//		return getAs(Boolean.class, defRq);
//	}
//
//	public <T> T getAs(Class<T> asType, T... defRq) {
//		if (treeProps != null) {
//			return treeProps.getAs(key(), asType, defRq);
//		}
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("Set tree or not call this method, key=%s", key()), defRq);
//	}
//
//}
