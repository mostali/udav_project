package zk_old_core.events;

import mpu.core.ARR;
import mpe.core.UBool;
import mpc.map.UMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Deprecated
public class PageBehaviours {

	public static final String BEHAVIOURS = "behaviours";
	public static final String HIGHLIGHT_ON_OVER_OUT = "highlightOnOverOut";
	public static final String OPEN_FORM_ON_DBL_CLICK = "openFormOnDblClick";
	public static final String SHOW_CONTEXT_MENU = "showContextMenu";

	public static List<String> getAllPageBehaviours() {
//		return URefl.fieldValuesSt(FsWin.class, String.class, StringConditionType.STARTS.buildCondition("VIEW_"), false);
		return ARR.as(HIGHLIGHT_ON_OVER_OUT, OPEN_FORM_ON_DBL_CLICK, SHOW_CONTEXT_MENU);
	}

	public static Map getBehavioursProps(Map<String, Object> rootProps) {
		return UMap.getAs(rootProps, PageBehaviours.BEHAVIOURS, Map.class, Collections.EMPTY_MAP);
	}

	public static boolean isEnable_Def0(Map<String, Object> rootProps, String prop) {
		return UBool.isTrue_Bool_12_YesNo_PlusMinus(getBeahaviourProp(rootProps, prop, "0"));
	}

	public static String getBeahaviourProp(Map<String, Object> rootProps, String key, String... defRq) {
		return UMap.get(getBehavioursProps(rootProps), key, defRq);
	}

	public static <T> T getBeahaviourPropAs(Map<String, Object> rootProps, String key, Class<T> asType, T... defRq) {
		return (T) UMap.getAs(getBehavioursProps(rootProps), key, asType, defRq);
	}
}
