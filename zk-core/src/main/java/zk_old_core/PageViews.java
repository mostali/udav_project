//package zk_page.behaviours;
//
//import mpe.core.UBool;
//import mpc.map.UMap;
//
//import java.util.Collections;
//import java.util.Map;
//
//public class PageViews {
//
//	public static final String BEHAVIOURS = "views";
//	public static final String MWIN = "mwin";
//
////	public static List<String> getAllPageBehaviours() {
//////		return URefl.fieldValuesSt(FsWin.class, String.class, StringConditionType.STARTS.buildCondition("VIEW_"), false);
////		return AR.as(HIGHLIGHT_ON_OVER_OUT, OPEN_FORM_ON_DBL_CLICK);
////	}
//
//	public static Map getViewsProps(Map<String, Object> rootProps) {
//		return UMap.getAs(rootProps, PageViews.BEHAVIOURS, Map.class, Collections.EMPTY_MAP);
//	}
//
//	public static boolean isEnableDef0(Map<String, Object> rootProps, String prop) {
//		return UBool.isTrue_Bool_12_YesNo_PlusMinus(getViewProp(rootProps, prop, "0"));
//	}
//
//	public static String getViewProp(Map<String, Object> rootProps, String key, String... defRq) {
//		return UMap.get(getViewsProps(rootProps), key, defRq);
//	}
//
//	public static <T> T getViewPropAs(Map<String, Object> rootProps, String key, Class<T> asType, T... defRq) {
//		return (T) UMap.getAs(getViewsProps(rootProps), key, asType, defRq);
//	}
//}
