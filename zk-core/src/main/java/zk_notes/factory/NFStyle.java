package zk_notes.factory;

import mpc.exception.WhatIsTypeException;
import mpc.map.MAP;
import mpu.X;
import mpu.core.ARG;
import mpu.str.RANDOM;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.core.IZCom;
import zk_notes.AppNotesProps;
import zk_notes.AxnTheme;
import zk_notes.node.NodeDir;
import zk_notes.node_state.EntityState;
import zk_notes.node_state.ObjState;
import zk_page.ZKS;

import java.util.Map;

public class NFStyle {

	public static void apply_BgColor(HtmlBasedComponent com, ObjState formState) {
		String bgColor = formState.get(ObjState.BG_COLOR, null);
		if (bgColor != null) {
			Boolean isEnableNiceView = AppNotesProps.APK_NICE_BGCOLOR_ENABLE.getValueOrDefault(true);
			if (isEnableNiceView) {
				ZKS.applyNiceBg(com, bgColor, "white");
			} else {
				ZKS.BGCOLOR(com, bgColor);
			}
		}
	}

	public static IZCom applyRandomColorWithRandomPosAbs(IZCom izCom, String... bgColors) {
		applyState_RandomOrTopLeft_ForNode(izCom);
		izCom.absolute();
		applyRandomColor(izCom.comH(), bgColors);
		return izCom;
	}

	public static void applyRandomColor(HtmlBasedComponent com, String[] bgColors) {
		if (ARG.isDef(bgColors)) {
			ZKS.BGCOLOR(com, RANDOM.array_item(bgColors));
		}
	}

	public static void applyState_BgColor(IZCom izCom, ObjState... comState) {
		apply_BgColor(izCom.comH(), comState.length == 1 ? comState[0] : izCom.getComStateDefault());
	}


	public static double getWeigthZoom(NodeDir nodeDir) {
		long weightKb = nodeDir.formSize(0L);
		if (weightKb == 0) {
			return 0.6;
		} else if (weightKb < 100) {
			return 0.9;
		}
		return 1.0;
	}

	public static double getWeigthOpacity(NodeDir nodeDir) {
		long weightKb = nodeDir.formSize(0L);
		if (weightKb == 0) {
			return 0.55;
		} else if (weightKb < 100) {
			return 0.85;
		}
		return 1.0;
	}


	public static boolean applyProp(HtmlBasedComponent com, Map<String, Object> props, String prop) {
		if (!props.containsKey(prop)) {
			return false;
		}
		switch (prop) {

			case "top":
				ZKS.TOP(com, props.get(prop));
				return true;
			case "left":
				ZKS.LEFT(com, props.get(prop));
				return true;
			case "width":
				ZKS.WIDTH(com, props.get(prop));
				return true;
			case "height":
				ZKS.HEIGHT(com, props.get(prop));
				return true;

			case "padding":
				ZKS.PADDING(com, props.get(prop));
				return true;
			case "margin":
				ZKS.MARGIN(com, props.get(prop));
				return true;

			case "text-decoration":
				ZKS.TEXT_DECARATION(com, (String) props.get(prop));
				return true;
			case "border":
				ZKS.BORDER(com, (String) props.get(prop));
				return true;
			case "border-radius":
				ZKS.BORDER_RADIUS(com, props.get(prop));
				return true;

			case "title":
				com.setTooltiptext((String) props.get(prop));
				return true;
			case "titlex":
				Object titlex = props.get(prop);
				if (titlex != null) {
					ZKS.applyProp_Titlex_WithTimeout(com, titlex, 5_000);
				}
				return true;

			case "visible":
				com.setVisible(MAP.getAsBool(props, prop, true));
				return true;

			case "font-size":
				ZKS.FONT_SIZE(com, props.get(prop));
				return true;

			case "fixed":
				if (X.notEmptyAllObj_Str_Cll_Num(props.get(prop))) {
					ZKS.FIXED(com);
					return true;
				}
				return false;

			case ObjState.LINK_VISIBLE:
				Object o = props.get(prop);
				if ("false".equals(o + "")) {
					com.setVisible(false);
					return true;
				}
				return false;

			default:
				throw new WhatIsTypeException(prop);
		}
	}

	public static IZCom applyState_RandomOrTopLeft_ForNode(IZCom izCom, ObjState... comState) {
		applyState_RandomOrTopLeft((HtmlBasedComponent) izCom, comState.length == 1 ? comState[0] : izCom.getComStateDefault());
		return izCom;
	}

	public static void applyState_RandomOrTopLeft(HtmlBasedComponent com, ObjState comState) {
		if (!comState.apply(com, ObjState.TOP_LEFT)) {
			ZKS.APPLY_RANDOM_TOPLEFT(com);
		}
	}

	public static void apply_TopLeft_WidthHeigth_Bgc_Titles(HtmlBasedComponent comTextWin, //
															HtmlBasedComponent comTextNoteTbxm_OrComTextWin, //
															ObjState stateForm, //
															boolean skipAbsolutePositionProps) { //
		if (!stateForm.existPropsFile()) {
			return;
		}
		if (!skipAbsolutePositionProps && !stateForm.apply(comTextWin, ObjState.TOP_LEFT)) {
			ZKS.TOP_LEFT(comTextWin, 30.0, 30.0);
		}

//		boolean fixedOrTrue = stateForm.fields().get_FIXED(true);//todo fixed
//		if (fixedOrTrue) {
		if (!stateForm.apply(comTextNoteTbxm_OrComTextWin, ObjState.WIDTH_HEIGHT)) {
			//nothing, already init
		}
//		}

		stateForm.apply(comTextNoteTbxm_OrComTextWin, ObjState.PK_TITLE);
		stateForm.apply(comTextNoteTbxm_OrComTextWin, ObjState.PK_TITLEX);

		ObjState.Position position = stateForm.fields().get_POSITION(ObjState.Position.ABS);
		String prop = null;
		switch (position) {
			case ABS:
				break;
			case FIX:
				stateForm.apply((HtmlBasedComponent) comTextNoteTbxm_OrComTextWin.getParent(), ObjState.PK_FIXED);
				break;
			case REL:
				break;
			default:
				throw new WhatIsTypeException(position);
		}

		apply_BgColor(comTextWin, stateForm);

	}

	public static void applyComDefaultStyle(HtmlBasedComponent nodeLn, NodeDir node) {

		ObjState stateCom = node.stateCom();

		if (!stateCom.apply(nodeLn, EntityState.LINK_VISIBLE)) {
			//ok
		}

		if (!stateCom.apply(nodeLn, ObjState.PK_TITLE)) {
			//ok
		}
		if (!stateCom.apply(nodeLn, ObjState.PK_TITLEX)) {
			//ok
		}

		if (!stateCom.apply(nodeLn, "font-size")) {
			ZKS.FONT_SIZE(nodeLn, AxnTheme.FONT_SIZE_APP_LINK);
		}

		if (!stateCom.apply(nodeLn, "padding")) {
			ZKS.PADDING(nodeLn, "9px 6px");
		}
		if (!stateCom.apply(nodeLn, "margin")) {
			ZKS.MARGIN(nodeLn, "0px");
		}

		if (!stateCom.apply(nodeLn, "text-decoration")) {
			ZKS.TEXT_DECARATION_NONE(nodeLn);
		}

		if (!stateCom.apply(nodeLn, "border-radius")) {
			ZKS.BORDER_RADIUS(nodeLn, "9px 6px 6px 6px");
		}

		if (!stateCom.apply(nodeLn, "opacity")) {
			double weigthZoom = getWeigthOpacity(node);
			if (weigthZoom != 1.0) {
				ZKS.OPACITY(nodeLn, weigthZoom);
			}
		}

		if (!stateCom.apply(nodeLn, "position")) {
			ObjState.Position position = stateCom.fields().get_POSITION(ObjState.Position.ABS);
			switch (position) {
				case ABS:
					ZKS.ABSOLUTE(nodeLn);
					break;
				case FIX:
					ZKS.FIXED(nodeLn);
					break;
				case REL:
					//nothing
					break;
				default:
					throw new WhatIsTypeException(position);
			}
		}

	}
}
