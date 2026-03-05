package zk_notes.factory;

import mpc.exception.WhatIsTypeException;
import mpc.map.MAP;
import mpc.num.UNum;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.str.RANDOM;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.FormState;
import zk_page.ZKS;

import java.util.HashMap;
import java.util.Map;

public enum BeType {
	top, left,  //
	width, height,  //
	padding, margin,  //
	font_size("-"), text_decoration("-"), //
	border, visible,
	fixed, border_radius("-"),  //
	//
	title, titlex,
	link_visible("."), //
	note_size("-"), //
	bgcolor(), //
	pos(), //
	top_left("-"), //
	width_height("-"), //
	;

	public static BeType valueOfProp(String name, BeType... defRq) {
		for (BeType value : BeType.values()) {
			if (value.name0.equalsIgnoreCase(name)) {
				return value;
			}
		}
		return ARG.throwMsg(() -> X.f_("Except BeType from value '%s'", name), defRq);
	}

	public static BeType valueOf(String name, BeType... defRq) {
		return ENUM.valueOf(name, BeType.class, defRq);
	}

	public final String name0;

	BeType() {
		this(null);
	}

	BeType(String del) {
		this.name0 = del == null ? name() : name().replace("_", del);
	}

	public static void applyProps(FormState props, NFNew.OptsAdd opts0) {
		boolean rslt = opts0.applyProp(props, BeType.link_visible);
		boolean rslt2 = opts0.applyProp(props, BeType.note_size);
		boolean rslt3 = opts0.applyProp(props, BeType.bgcolor);
		boolean rslt4 = opts0.applyProp(props, BeType.pos);

		if (opts0.getOptBe().getTop_left() == null) {
			props.set("top", UNum.round10(RANDOM.range(260, 520)) + "px");
			props.set("left", UNum.round10(RANDOM.range(260, 520)) + "px");
		} else {
			NFNew.OptsBe optBe = opts0.optBe;
			props.set(top.name0, ZKS.toPxPct(optBe.top_left[0]));
			props.set(left.name0, ZKS.toPxPct(optBe.top_left[1]));
		}

		if (opts0.getOptBe().getWidth_height() == null) {
			props.set("width", UNum.round10(RANDOM.range(500, 600)) + "px");
			props.set("height", UNum.round10(RANDOM.range(400, 500)) + "px");
		} else {
			NFNew.OptsBe optBe = opts0.optBe;
			props.set(width.name0, ZKS.toPxPct(optBe.width_height[0]));
			props.set(height.name0, ZKS.toPxPct(optBe.width_height[1]));
		}
	}

	public static Map<BeType, Boolean> applyProp(HtmlBasedComponent com, Map<String, Object> props, BeType... prop) {
		Map<BeType, Boolean> rsp = new HashMap();
		for (BeType ss : prop) {
			rsp.put(ss, applyProp(com, props, ss.name0));
		}
		return rsp;
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
}
