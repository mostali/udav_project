package zk_old_core.events;

import mpe.core.UBool;
import mpc.map.UMap;

import java.util.Map;

@Deprecated
public class PageRootProps {

	public static final String PK_LAYOUT = "layout";

//	public static final String BEHAVIOURS = "behaviours";
//	public static final String HIGHLIGHT_ON_OVER_OUT = "highlightOnOverOut";
//	public static final String OPEN_FORM_ON_DBL_CLICK = "openFormOnDblClick";

//	public static List<String> getAllPageBehaviours() {
////		return URefl.fieldValuesSt(FsWin.class, String.class, StringConditionType.STARTS.buildCondition("VIEW_"), false);
//		return AR.as(HIGHLIGHT_ON_OVER_OUT, OPEN_FORM_ON_DBL_CLICK);
//	}
//
//	public static Map getBehavioursProps(Map<String, Object> rootProps) {
//		return UMap.getAs(rootProps, PageRootProps.BEHAVIOURS, Map.class, Collections.EMPTY_MAP);
//	}

	public static boolean isEnable_Def0(Map<String, Object> rootProps, String prop) {
		return UBool.isTrue_Bool_12_YesNo_PlusMinus(getPageProp(rootProps, prop, "0"));
	}

	public static String getPageProp(Map<String, Object> rootProps, String key, String... defRq) {
		return UMap.get((Map) rootProps, key, defRq);
	}

	public static <T> T getPagePropAs(Map<String, Object> rootProps, String key, Class<T> asType, T... defRq) {
		return (T) UMap.getAs(rootProps, key, asType, defRq);
	}
}
