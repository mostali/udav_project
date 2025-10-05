package zk_notes.factory;

import mpc.exception.WhatIsTypeException;
import mpc.map.MAP;
import mpu.core.ARG;
import mpu.str.RANDOM;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.core.IZCom;
import zk_notes.AxnTheme;
import zk_notes.control.NodeLn;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FormState;
import zk_page.ZKS;

import java.util.Map;

public class NFStyle {

	public static void apply_BgColor(HtmlBasedComponent com, FormState formState) {
		String bgColor = formState.get(FormState.BG_COLOR, null);
		if (bgColor != null) {
			ZKS.BGCOLOR(com, bgColor);
		}
	}

	public static IZCom applyRandomColorWithRandomPosAbs(IZCom izCom, String... bgColors) {
//		applyState_RandomOrTopLeft();
		applyState_RandomOrTopLeft_ForNode(izCom);
		izCom.absolute();
		applyRandomColor(izCom.comH(), bgColors);
		return izCom;
	}

	public static void applyRandomColor(HtmlBasedComponent com, String[] bgColors) {
		if (ARG.isDef(bgColors)) {
			ZKS.BGCOLOR(com, RANDOM.ARRAY_ITEM(bgColors));
		}
	}

	public static void applyState_BgColor(IZCom izCom, FormState... comState) {
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
			return 0.44;
		} else if (weightKb < 100) {
			return 0.69;
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

			default:
				throw new WhatIsTypeException(prop);
		}
	}

	public static IZCom applyState_RandomOrTopLeft_ForNode(IZCom izCom, FormState... comState) {
		applyState_RandomOrTopLeft((HtmlBasedComponent) izCom, comState.length == 1 ? comState[0] : izCom.getComStateDefault());
		return izCom;
	}

	public static void applyState_RandomOrTopLeft(HtmlBasedComponent com, FormState comState) {
		if (!comState.apply(com, FormState.TOP_LEFT)) {
			ZKS.APPLY_RANDOM_TOPLEFT(com);
		}
	}

	public static void apply_TopLeft_WidthHeigth_Bgc_Titles(HtmlBasedComponent comTextWin, //
															HtmlBasedComponent comTextNoteTbxm_OrComTextWin, //
															FormState stateForm, //
															boolean skipAbsolutePositionProps) { //
		if (!stateForm.existPropsFile()) {
			return;
		}
		if (!skipAbsolutePositionProps && !stateForm.apply(comTextWin, FormState.TOP_LEFT)) {
			ZKS.TOP_LEFT(comTextWin, 30.0, 30.0);
		}

		boolean fixedOrTrue = stateForm.fields().get_FIXED(true);
		if (fixedOrTrue) {
			if (!stateForm.apply(comTextNoteTbxm_OrComTextWin, FormState.WIDTH_HEIGHT)) {
				//nothing, already init
			}
		}

		stateForm.apply(comTextNoteTbxm_OrComTextWin, FormState.PK_TITLE);
		stateForm.apply(comTextNoteTbxm_OrComTextWin, FormState.PK_TITLEX);

		apply_BgColor(comTextWin, stateForm);

	}

	public static void applyDefaultStyle(HtmlBasedComponent nodeLn, NodeDir node) {

		FormState stateCom = node.stateCom();

		if (!stateCom.apply(nodeLn, NodeCom.VISIBLE)) {
			//ok
		}

		if (!stateCom.apply(nodeLn, FormState.PK_TITLE)) {
			//ok
		}
		if (!stateCom.apply(nodeLn, FormState.PK_TITLEX)) {
			//ok
		}

		if (!stateCom.apply(nodeLn, "position")) {
			ZKS.ABSOLUTE(nodeLn);
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
	}
}
