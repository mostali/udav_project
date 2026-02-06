package zk_page;

import lombok.RequiredArgsConstructor;
import mpc.ui.ColorTheme;
import mpc.arr.QUEUE;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.html.CLASS;
import mpc.html.STYLE;
import mpc.json.GsonMap;
import mpc.rfl.RFL;
import mpu.core.UDbl;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import mpu.IT;
import mpu.X;
import mpu.str.SPLIT;
import mpu.str.UST;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.HtmlBasedComponent;
import mpc.map.MAP;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Div;
import org.zkoss.zul.Span;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;
import utl_web.UWeb;

import java.util.*;

//open close nojs - https://codepen.io/B_Sanchez/pen/RpKJVx
//https://developer.mozilla.org/en-US/docs/Web/CSS/transform
//https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-width
//STYLE
@RequiredArgsConstructor
public class ZKS {
	public static final String PX = "px";

	//https://html-color.codes/green
//	public static final String[] BLUE = AR.of("", "", "", "", "", "", "", "");

	final HtmlBasedComponent com;

	public static ZKS of(HtmlBasedComponent com) {
		return new ZKS(com);
	}

	public static String classRnd(HtmlBasedComponent com, String pfx, int len) {
		String str = RANDOM.alpha(pfx, len);
		com.setClass(str);
		return str;
	}

	public static void PADDING0(Window window) {
		WC_PADDING(window, 0, 0);
	}

	public static void WC_WIDTH(Window window, Object win_px_pct) {
//		ZKS.addStyleAttr(window, "width", win_px_pct == null ? null : toPxPct(win_px_pct));
		String style = STYLE.addStyleAttr(window.getContentStyle(), "width", (win_px_pct == null ? null : toPxPct(win_px_pct)));
		window.setContentStyle(style);
	}

	public static void WC_DISPLAY(Window window, String value) {
		String style = STYLE.addStyleAttr(window.getContentStyle(), "display", (value == null ? null : toPxPct(value)));
		window.setContentStyle(style);
	}

	public static void WC_PADDING(Window window, Object win_px_pct, Object content_px_pct) {
		ZKS.addStyleAttr(window, "padding", win_px_pct == null ? null : toPxPct(win_px_pct));
		String style = STYLE.addStyleAttr(window.getContentStyle(), "padding", (content_px_pct == null ? null : toPxPct(content_px_pct)));
		window.setContentStyle(style);
	}

	public static void WC_PADDING_TOP(Window window, Object win_px_pct, Object content_px_pct) {
		ZKS.addStyleAttr(window, "padding-top", win_px_pct == null ? null : toPxPct(win_px_pct));
		String style = STYLE.addStyleAttr(window.getContentStyle(), "padding-top", (content_px_pct == null ? null : toPxPct(content_px_pct)));
		window.setContentStyle(style);
	}

	public static void WC_MARGIN_TOP(Window window, Object win_px_pct, Object content_px_pct) {
		ZKS.addStyleAttr(window, "margin-top", win_px_pct == null ? null : toPxPct(win_px_pct));
		String style = STYLE.addStyleAttr(window.getContentStyle(), "margin-top", (content_px_pct == null ? null : toPxPct(content_px_pct)));
		window.setContentStyle(style);
	}


	public static void BGCOLOR_WIN(Window window, String win_bgColor, String content_bgColor) {
		ZKS.addStyleAttr(window, "background", win_bgColor == null ? null : win_bgColor);
		String style = STYLE.addStyleAttr(window.getContentStyle(), "background", content_bgColor == null ? null : content_bgColor);
		window.setContentStyle(style);
	}

	public static String px(Integer px) {
		return px + PX;
	}

	public static Integer px(String vlPx, Integer... defRq) {
		if (vlPx != null && vlPx.endsWith(PX)) {
			Integer vl = UST.INT(vlPx.substring(0, vlPx.length() - 2), null);
			if (vl != null) {
				return vl;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Except pattern with px '%s'", vlPx), defRq);
	}

	public static <C extends HtmlBasedComponent> C onVisibleOnMove(C com, boolean startIsVisible) {
		if (!startIsVisible) {
			ZKS.addSTYLE(com, "opacity:0.0");
		}
		com.addEventListener(Events.ON_MOUSE_OVER, (SerializableEventListener<Event>) event -> ZKS.addSTYLE(com, "opacity:1.0"));
		com.addEventListener(Events.ON_MOUSE_OUT, (SerializableEventListener<Event>) event -> ZKS.addSTYLE(com, "opacity:0.0"));
		return com;
	}

	public static <C extends HtmlBasedComponent> C MARGIN_CENTER(C com, boolean displayBlock_orAbsolute) {
		addSTYLE(com, displayBlock_orAbsolute ? "margin:0 auto;display:block" : "margin:0 auto;display:block;left:0;right:0;position:absolute");
		//border:1px solid green;\nwidth:70%;margin:0 auto
		return com;
	}

	public static <C extends HtmlBasedComponent> C CONTENT_EDITABLE(HtmlBasedComponent com) {
		com.setWidgetAttribute("contenteditable", "");
		return (C) com;
	}

	public static <C extends HtmlBasedComponent> C PADDING(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "padding", px_pct);
	}

	public static <C extends HtmlBasedComponent> C PADDING_TOP(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "padding-top", px_pct);
	}

	public static <C extends HtmlBasedComponent> C PADDING_BOTTOM(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "padding-bottom", px_pct);
	}

	public static <C extends HtmlBasedComponent> C PADDING_RIGHT(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "padding-right", px_pct);
	}

	public static <C extends HtmlBasedComponent> C PADDING_LEFT(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "padding-left", px_pct);
	}

	public static <C extends HtmlBasedComponent> C MARGIN(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "margin", px_pct);
	}

	public static <C extends HtmlBasedComponent> C MARGIN(C com, Object t_px_pct, Object r_px_pct, Object b_px_pct, Object l_px_pct) {
		return applyStylePropertyPxPct(com, "margin", X.f("%s %s %s %s", toPxPct0(t_px_pct), toPxPct0(r_px_pct), toPxPct0(b_px_pct), toPxPct0(l_px_pct)));
	}

	public static <C extends HtmlBasedComponent> C MARGIN_LEFT(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "margin-left", px_pct);
	}

	public static <C extends HtmlBasedComponent> C MARGIN_RIGHT(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "margin-RIGHT", px_pct);
	}

	public static <C extends HtmlBasedComponent> C MARGIN_TOP(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "margin-top", px_pct);
	}

	public static <C extends HtmlBasedComponent> C MARGIN_BOTTOM(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "margin-bottom", px_pct);
	}

	public static <C extends HtmlBasedComponent> C applyStylePropertyPxPct(C com, String name, Object px_pct) {
		return px_pct == null ? rmStyleAttr(com, name) : addSTYLE(com, name + ":" + toPxPct(px_pct));
	}


	public static String toPxPct0(Object value) {
		return value == null ? "0" : toPxPct(value);
	}

	public static String toPxPct(Object value) {
		if (!(value instanceof Number)) {
			return value.toString();
		} else if (value instanceof Double) {
			return ((Double) value).doubleValue() + "%";
		} else {
			return ((Number) value).intValue() + PX;
		}
	}

	public static <C extends HtmlBasedComponent> C VFLEX_MIN(C com) {
		com.setVflex("min");
		return com;
	}

	public static <C extends HtmlBasedComponent> C addStyle(C com, GsonMap style) {
		String width = style.getAsString("width", null);
		if (width != null) {
			addStyleAttr(com, "width", width);
		}
		String height = style.getAsString("height", null);
		if (height != null) {
			addStyleAttr(com, "height", height);
		}
		return com;
	}

	public static <C extends HtmlBasedComponent> C CENTER(C com, Integer... mode) {
		Integer mode0 = ARG.toDefOr(0, mode);
		String style;
		switch (mode0) {
			case 0:
				style = "margin:0 auto;display:block;text-align:center;";
				break;
			case 1:
				style = "margin:0 auto;display:block;text-align:right;";
				break;
			case -1:
				style = "margin:0 auto;display:block;text-align:left;";
				break;
			case 10:
				style = "margin:0 auto;display:block;left:0;right:0;position:absolute";
				break;
			default:
				throw new WhatIsTypeException(mode0);
		}
		addSTYLE(com, style);
		return com;

	}

	public static <C extends HtmlBasedComponent> C BORDER_BOTTOM(C com) {
		return BORDER(com, "1px solid gray");
	}

	public static <C extends HtmlBasedComponent> C BORDER_GRAY(C com) {
		return BORDER(com, "1px solid gray");
	}

	public static <C extends HtmlBasedComponent> C BORDER(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "border") : ZKS.addStyleAttr(com, "border", value);
	}

	public static <C extends HtmlBasedComponent> C BORDER_TOP(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "border-top") : ZKS.addStyleAttr(com, "border-top", value);
	}

	public static <C extends HtmlBasedComponent> C BORDER_RIGHT(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "border-right") : ZKS.addStyleAttr(com, "border-right", value);
	}

	public static <C extends HtmlBasedComponent> C BORDER_BOTTOM(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "border-bottom") : ZKS.addStyleAttr(com, "border-bottom", value);
	}

	public static <C extends HtmlBasedComponent> C TRANSFORM_SCALE(C com, Double value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "transform") : ZKS.addStyleAttr(com, "transform", "scale(" + UDbl.scale(value, 1) + ")");
	}

	public static <C extends HtmlBasedComponent> C BORDER_RADIUS(C com, Object px_or) {
		return px_or == null ? ZKS.rmStyleAttr(com, "border-radius") : ZKS.addStyleAttr(com, "border-radius", px_or instanceof Integer ? px_or + PX : px_or.toString());

	}

	public static <C extends HtmlBasedComponent> C FONT_FAMILY0(C com) {
		return FONT_FAMILY(com, "system-ui");
	}

	public static <C extends HtmlBasedComponent> C FONT_FAMILY_MONOSPACE(C com) {
		return FONT_FAMILY(com, "monospace");
	}

	public static <C extends HtmlBasedComponent> C FONT_FAMILY(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "font-family") : ZKS.addStyleAttr(com, "font-family", value);
	}

	public static <C extends HtmlBasedComponent> C POSITION(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "position") : ZKS.addStyleAttr(com, "position", value);
	}

	public static <C extends HtmlBasedComponent> C TEXT_DECARATION_NONE(C com) {
		return TEXT_DECARATION(com, "none");
	}

	public static <C extends HtmlBasedComponent> C TEXT_DECARATION(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "text-decoration") : ZKS.addStyleAttr(com, "text-decoration", value);
	}


	public static <C extends HtmlBasedComponent> String getStyleAttrValue(C com, String attrName, String... defRq) {
		return MAP.getAsString(STYLE.toMap(com.getStyle()), attrName, defRq);
	}

	public static String toStringPosDims(HtmlBasedComponent w) {
		return "top:" + w.getTop() + ";left:" + w.getLeft() + ";width:" + w.getWidth() + ";height:" + w.getHeight();
	}

	public static boolean putProp(HtmlBasedComponent com, Map<String, Object> props, String prop) {
		switch (prop) {
			case "top":
				props.put("top", com.getTop());
				return true;
			case "left":
				props.put("left", com.getLeft());
				return true;
			case "width":
				props.put("width", com.getWidth());
				return true;
			case "height":
				props.put("height", com.getHeight());
				return true;
		}
		return false;
	}

	private static Map<Integer, Long> mapTitlexMsAgo = QUEUE.cache_map_FILO(100);

	public static void applyProp_Titlex_WithTimeout(HtmlBasedComponent com, Object titlex, int minShowMs) {
		String titleX_content = titlex.toString();
		com.addEventListener(Events.ON_MOUSE_OVER, (e) -> {
			Long msAgo = mapTitlexMsAgo.get(titleX_content.hashCode());
			if (msAgo == null || System.currentTimeMillis() - msAgo > minShowMs) {
				Notification.show(titleX_content, com);
				mapTitlexMsAgo.put(titleX_content.hashCode(), System.currentTimeMillis());
			}
		});
	}


	public static <C extends HtmlBasedComponent> C DRAG_DROP(C com, Boolean... drag1_drop2) {
		com.setDraggable(ARRi.item(drag1_drop2, 0, true) + "");
		com.setDroppable(ARRi.item(drag1_drop2, 1, true) + "");
		return com;
	}

	public static boolean isPx(String vl) {
		return vl != null && StringUtils.endsWithIgnoreCase(vl, PX);
	}

	public static <C extends HtmlBasedComponent> C BGIMAGE(C com, String bgUrl, String bgSize, String bgPos, String bgRepeat) {
		addStyleAttr(com, "background-image", IT.NE(bgUrl));
		if (bgSize != null) {
			addStyleAttr(com, "background-size", bgSize);
		}
		if (bgPos != null) {
			addStyleAttr(com, "background-position", bgPos);
		}
		if (bgRepeat != null) {
			addStyleAttr(com, "background-repeat", bgRepeat);
		}
		return com;
	}

	public static void APPLY_RANDOM_TOPLEFT(HtmlBasedComponent ln) {
		ZKS.TOP(ln, RANDOM.range(260, 720) + PX);
		ZKS.LEFT(ln, RANDOM.range(360, 1200) + PX);
	}

	public static void APPLY_RANDOM_TOPLEFT(HtmlBasedComponent com, int offsetY) {

		ZKS.TOP(com, RANDOM.range(offsetY + 260, offsetY + 720) + PX);
		ZKS.LEFT(com, RANDOM.range(360, 1200) + PX);
	}

	public static void WIDTH_HEIGHT100(HtmlBasedComponent com) {
		WIDTH_HEIGHT(com, 100.0, 100.0);
	}

	public static void enableDarkTheme(HtmlBasedComponent com) {

		ZKS.BGCOLOR(com, ColorTheme.BLACK[1]);
		ZKS.COLOR(com, ColorTheme.WHITE[1]);

	}

	public static String getAppClassName(Class clazz) {
		return "Zkn" + clazz.getSimpleName();
	}

	public static void applyNiceBg(HtmlBasedComponent com, Pare bgColor) {
		String first = bgColor.key() instanceof ZKColor ? ((ZKColor) bgColor.key()).nextColor() : (String) bgColor.key();
		String sec = bgColor.val() == null ? null : (bgColor.val() instanceof ZKColor ? ((ZKColor) bgColor.val()).nextColor() : (String) bgColor.val());

		if (sec == null) {
			BGCOLOR(com, first);
		} else {
			applyNiceBg(com, first, sec);
		}

	}

	public static void applyNiceBg(HtmlBasedComponent com, String fromColor, String toColor) {
		ZKS.BOX_SHADOW(com, "0 4px 12px rgba(0,0,0,0.15)");
		ZKS.BG(com, X.f_("linear-gradient(135deg, %s, %s)", fromColor, toColor));
	}

	public ZKS left(String val) {
		LEFT(com, val);
		return this;
	}

	public ZKS top(String val) {
		TOP(com, val);
		return this;
	}

	public ZKS width(String val) {
		WIDTH(com, val);
		return this;
	}


	public ZKS width(int val) {
		WIDTH(com, val + "%");
		return this;
	}

	public ZKS height(String val) {
		HEIGHT(com, val);
		return this;
	}

	public ZKS abs() {
		ABSOLUTE(com);
		return this;
	}

	public ZKS bottom(Object px_pct) {
		BOTTOM(com, px_pct);
		return this;
	}

	public ZKS right(Object px_pct) {
		RIGHT(com, px_pct);
		return this;
	}

	public ZKS zindex(int zindex) {
		ZINDEX(com, zindex);
		return this;
	}

	/**
	 * *************************************************************
	 * ---------------------------- SHOW --------------------------
	 * *************************************************************
	 */

	public static <T extends XulElement> T toggleDnoneFirst(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		T first = ZKCFinderExt.findFirst_inPage0(clazz, recursive_or_first, null);
		return first != null ? toggleDnone(first) : ARG.toDefThrow(() -> new RequiredRuntimeException("Recursive(%s) com '%s' not found", recursive_or_first, clazz.getSimpleName()), defRq);
	}

	public static <T extends XulElement> T toggleDnone(T com) {
		String valCur = ZKS.getStyleAttrValue(com, "display", null);
		if (valCur == null) {
			ZKS.addStyleAttr(com, "display", "none");
			return com;
		}
		//String valPrev0 = ZKS.getStyleAttrValue(com, "display0", null);
		//String valPrev0 = (String) com.getAttribute("display0");
		switch (valCur) {
			case "none":
				//return valPrev0 != null ? ZKS.addStyleAttr(com, "display", valPrev0) : ZKS.addStyleAttr(com, "display", defineDisplayStyleAttrValue(com));
				return ZKS.addStyleAttr(com, "display", defineDisplayStyleAttrValue(com));
			default:
				ZKS.addStyleAttr(com, "display", "none");
				//com.setAttribute("display0", valCur);
				return com;
		}
	}

	private static <T extends HtmlBasedComponent> String defineDisplayStyleAttrValue(T first) {
		if (first instanceof Div) {
			return "block";
		} else if (first instanceof Span) {
			return "inline";
		} else {
			return "inline-block";
		}
	}

	public static <T extends XulElement> T showFirst(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		T first = ZKCFinderExt.findFirst_inPage0(clazz, recursive_or_first, null);
		if (first != null) {
			ZKS.DISPLAY(first, -1);
//			first.setVisible(true);
//			Sys.say("show");
//			ZKJS.setAction_ShowEffect(first, 1000);
			return first;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Recursive (%s) component '%s' for hide operation  not found", recursive_or_first, RFL.scn(clazz)), defRq);
	}

	public static <T extends XulElement> T hideFirst(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		T first = ZKCFinderExt.findFirst_inPage0(clazz, recursive_or_first, null);
		if (first != null) {
			ZKS.DISPLAY(first, 1);

//			first.setVisible(false);
//			Sys.say("hide");
//			ZKJS.setAction_HideEffect(first, 1000);
			return first;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Recursive (%s) component '%s' for hide operation  not found", recursive_or_first, RFL.scn(clazz)), defRq);
	}

	/**
	 * *************************************************************
	 * ---------------------------- STATIC --------------------------
	 * *************************************************************
	 */

	public static <C extends HtmlBasedComponent> C ZINDEX(C com, Integer zindex) {
//		if (zindex != null && zindex <= 0) {
//			addStyle(com, "z-index:" + zindex);
//		}
		com.setZindex(zindex);
		return com;
	}

	public static <C extends HtmlBasedComponent> C RELATIVE(C com) {
		return POSITION(com, "relative");
	}

	public static <C extends HtmlBasedComponent> C ABSOLUTE(C com) {
		addStyleAttr(com, "position", "absolute");
		return com;
	}

	public static <C extends HtmlBasedComponent> C FIXED(C com) {
		addStyleAttr(com, "position", "fixed");
		return com;
	}

	public static <C extends HtmlBasedComponent> C BOLD_NICE(C com, Integer... font_size) {
		addStyleAttr(com, "font-family", "arial,sans-serif");
		if (ARG.isDef(font_size)) {
			addStyleAttr(com, "font-size", ARG.toDef(font_size) + PX);
		}
		addStyleAttr(com, "font-weight", "bold");
		return com;
	}

	public static <C extends HtmlBasedComponent> C RIGHT(C com, Object px_pct) {
		return px_pct == null ? rmStyleAttr(com, "right") : addStyleAttr(com, "right", toPxPct(px_pct));
	}

	public static <C extends HtmlBasedComponent> C BOTTOM(C com, Object px_pct) {
		return px_pct == null ? rmStyleAttr(com, "bottom") : addStyleAttr(com, "bottom", toPxPct(px_pct));
	}

	public static <C extends HtmlBasedComponent> C LEFT(C com, Object px_pct) {
		com.setLeft(px_pct == null ? null : toPxPct(px_pct));
		return com;
	}

	public static <C extends HtmlBasedComponent> C TOP_LEFT(C com, Object top_px_pct, Object left_px_pct) {
		return TOP(LEFT(com, left_px_pct), top_px_pct);
	}

	public static <C extends HtmlBasedComponent> C BOTTOM_RIGHT(C com, Object bottom_px_pct, Object right_px_pct) {
		return BOTTOM(RIGHT(com, right_px_pct), bottom_px_pct);
	}

	public static <C extends HtmlBasedComponent> C TOP_RIGHT(C com, Object bottom_px_pct, Object right_px_pct) {
		return TOP(RIGHT(com, right_px_pct), bottom_px_pct);
	}

	public static <C extends HtmlBasedComponent> C TOP(C com, Object px_pct) {
		com.setTop(px_pct == null ? null : toPxPct(px_pct));
		return com;
	}

	public static <C extends HtmlBasedComponent> C FONT_SIZE(C com, Object px_pct) {
		return px_pct == null ? rmStyleAttr(com, "font-size") : addStyleAttr(com, "font-size", toPxPct(px_pct));
	}

	public static <C extends HtmlBasedComponent> C VERT_ALIGN(C com, String val) {
		return X.empty(val) ? rmStyleAttr(com, "vertical-align") : addStyleAttr(com, "vertical-align", val);
	}

	public static <C extends HtmlBasedComponent> C TEXT_ALIGN(C com, Integer val) {
		if (val == null) {
			return rmStyleAttr(com, "text-align");
		} else if (!(val instanceof Number)) {
			return addStyle(com, "text-align:" + val.toString());
		}
		int valInt = ((Number) val).intValue();
		String value;
		switch (valInt) {
			case 0:
				value = "center";
				break;
			case -1:
				value = "left";
				break;
			case 1:
				value = "right";
				break;
			default:
				throw new WhatIsTypeException(valInt);
		}
		return addStyle(com, "text-align:" + value);
	}

	public static <C extends HtmlBasedComponent> C OVERFLOW(C com, Integer auto0_hidden1_overlay2_scroll3) {
		if (auto0_hidden1_overlay2_scroll3 == null) {
			return rmStyleAttr(com, "overflow");
		} else if (!(auto0_hidden1_overlay2_scroll3 instanceof Number)) {
			return addStyle(com, "overflow:" + auto0_hidden1_overlay2_scroll3.toString());
		}
		int valInt = ((Number) auto0_hidden1_overlay2_scroll3).intValue();
		String value;
		switch (valInt) {
			case 0:
				value = "auto";
				break;
			case 1:
				value = "hidden";
				break;
			case 2:
				value = "overlay";
				break;
			case 3:
				value = "scroll";
				break;
			default:
				throw new WhatIsTypeException(valInt);
		}
		return addStyle(com, "overflow:" + value);
	}

	public static <C extends HtmlBasedComponent> C FLEX(C com, String flex_direction) {
		DISPLAY(com, "flex");
		return addStyleAttr(com, "flex-direction", flex_direction);
	}

	public static <C extends HtmlBasedComponent> C DISPLAY(C com, Object val) {
		if (val == null) {
			return rmStyleAttr(com, "display");
		} else if (!(val instanceof Number)) {
			return addStyle(com, "display:" + val);
		}
		int valInt = ((Number) val).intValue();
		String value;
		switch (valInt) {
			case -1:
				value = "none";
				break;
			case 0:
				value = "inline";
				break;
			case 1:
				value = "inline-block";
				break;
			case 2:
				value = "block";
				break;
			default:
				throw new WhatIsTypeException(valInt);
		}
		return addStyle(com, "display:" + value);
	}

	public static <C extends HtmlBasedComponent> C ZOOM(C com, Double zoom) {
		return zoom == null ? rmStyleAttr(com, "zoom") : addStyle(com, "zoom:" + zoom);
	}

	public static <C extends HtmlBasedComponent> C OPACITY(C com, Double pattern) {
		return pattern == null ? rmStyleAttr(com, "opacity") : addStyle(com, "opacity:" + pattern);
	}

	public static <C extends HtmlBasedComponent> C BOX_SHADOW(C com, String shadowVal) {
		return shadowVal == null ? rmStyleAttr(com, "box-shadow") : addStyle(com, "box-shadow:" + shadowVal);
	}

	public static <C extends HtmlBasedComponent> C BG(C com, String bgStr) {
		return bgStr == null ? rmStyleAttr(com, "background") : addStyle(com, "background:" + bgStr);
	}

	public static <C extends HtmlBasedComponent> C BGCOLOR(C com, String color) {
		return color == null ? rmStyleAttr(com, "background-color") : addStyle(com, "background-color:" + color);
	}

	public static <C extends HtmlBasedComponent> C COLOR(C com, String color) {
		return addStyle(com, "color:" + color);
	}

	public static <C extends HtmlBasedComponent> C FLOAT(C com, Boolean left_right_rm) {
		return left_right_rm == null ? rmStyleAttr(com, "float") : addStyle(com, "float:" + (left_right_rm ? "left" : "right"));
	}

	public static <C extends HtmlBasedComponent> C addSTYLE(C com, String style, Object... args) {
		return addStyle(com, style, args);
	}

	public static <C extends HtmlBasedComponent> C STYLE(C com, String style, Object... args) {
		com.setStyle(X.f(style, args));
		return com;
	}

	public static <C extends HtmlBasedComponent> C WIDTH_MOBILE(C com, String width, String widthMobile) {
		com.setWidth(UWeb.isMobile() ? widthMobile : width);
		return com;
	}

	public static <C extends HtmlBasedComponent> C HEIGHT_MOBILE(C com, String height, String heightMobile) {
		com.setHeight(UWeb.isMobile() ? heightMobile : height);
		return com;
	}


	public static <C extends HtmlBasedComponent> C WIDTH_HEIGHT(C com, Object w_px_pct, Object h_px_pct) {
		WIDTH(com, w_px_pct);
		return HEIGHT(com, h_px_pct);
	}

	public static <C extends HtmlBasedComponent> C WIDTH(C com, Object px_pct, Object... height_px_pct) {
		com.setWidth(px_pct == null ? null : toPxPct(px_pct));
		if (ARG.isDef(height_px_pct)) {
			HEIGHT(com, ARG.toDef(height_px_pct));
		}
		return com;
	}

	public static <C extends HtmlBasedComponent> C HEIGHT(C com, Object px_pct) {
		com.setHeight(px_pct == null ? null : toPxPct(px_pct));
		return com;
	}

	public static <C extends HtmlBasedComponent> C HEIGHT_MIN(C com, Object px_pct) {
		addStyleAttr(com, "min-height", toPxPct(px_pct));
		return com;
	}

	public static <C extends HtmlBasedComponent> C HEIGHT_MAX(C com, Object px_pct) {
		addStyleAttr(com, "max-height", toPxPct(px_pct));
		return com;
	}

	public static <C extends HtmlBasedComponent> C WIDTH_MIN(C com, Object px_pct) {
		addStyleAttr(com, "min-width", toPxPct(px_pct));
		return com;
	}

	public static <C extends HtmlBasedComponent> C WIDTH_MAX(C com, Object px_pct) {
		addStyleAttr(com, "max-width", toPxPct(px_pct));
		return com;
	}

	public static <C extends HtmlBasedComponent> C HEIGHT_ADAPTIVE_0(C com) {
		com.setHeight("100%");
		com.setVflex("min");
		return com;
	}

//	public static <C extends HtmlBasedComponent> C HEIGHT_ADAPTIVE_1(C com) {
//		com.setHeight("100%");
//		com.setVflex("1");
//		return com;
//	}

	public static <C extends HtmlBasedComponent> C BLOCK(C com) {
		addStyleAttr(com, "display", "block");
		return com;
	}

	public static <C extends HtmlBasedComponent> C INLINE_BLOCK(C com) {
		addStyleAttr(com, "display", "inline-block");
		return com;
	}

	public static <C extends HtmlBasedComponent> C addStyleAttr(C com, String key, String val) {
		com.setStyle(STYLE.addStyleAttr(com.getStyle(), key, val));
		return com;
	}

	public static <C extends HtmlBasedComponent> C addStyle(C com, String style, Object... args) {
		com.setStyle(STYLE.addStyle(com.getStyle(), X.f(style, args)));
		return com;
	}

	public static <C extends HtmlBasedComponent> C rmStyleAttr(C com, String style) {
		com.setStyle(STYLE.rmStyle(com.getStyle(), style));
		return com;
	}

	//https://forum.zkoss.org/question/6113/how-add-sclass-in-java/
	public static <C extends HtmlBasedComponent> C addClassAttr(C com, String singleOrManyClass, boolean... checkUniq) {
		return addClassAttr(com, SPLIT.argsBy(singleOrManyClass, " "), checkUniq);
	}

	public static <C extends HtmlBasedComponent> C addClassAttr(C com, String[] singleClass, boolean... checkUniq) {
		com.setSclass(CLASS.addClass(com.getSclass(), singleClass, checkUniq));
		return com;
	}

	public static <C extends HtmlBasedComponent> C rmClassAttr(C com, String singleOrManyClass) {
		return rmClassAttr(com, SPLIT.argsBy(singleOrManyClass, " "));
	}

	public static <C extends HtmlBasedComponent> C rmClassAttr(C com, String[] singleClass) {
		com.setSclass(CLASS.rmClass(com.getSclass(), singleClass));
		return com;
	}

	//https://www.javatips.net/api/carewebframework-core-master/org.carewebframework.plugin-parent/org.carewebframework.plugin.infopanel/src/main/java/org/carewebframework/plugin/infopanel/service/InfoPanelService.java
	//https://www.zkoss.org/zkdemo/effects/image_zoomer
	//https://stackoverflow.com/questions/11118358/jquery-scale-and-fade-at-the-same-time
	static <C extends HtmlBasedComponent> C addStyleAttrRMM(C parent) {
		return addStyle(parent, "transform:scale(1,1);transition:transform:400ms ease 0s");
	}

	private static <C extends HtmlBasedComponent> C addClassOn(C parent) {
		return addClassAttr(parent, "transform:scale(1,1);transition:transform:400ms ease 0s");
	}

	public static void setStyleAsCData() {
//		https://forum.zkoss.org/question/64488/problem-in-styling-window-title/

//		<zk>
//<style><![CDATA[
//.z-window-embedded-header { color:white; font-size:50px}
//]]> </style>
//<window id="winCbos" title="CBOS" style="text-align:center;" width="100%" border="normal" >
//</window>
//</zk>
	}


	ZKS float0(Boolean left_right_remove) {
		ZKS.FLOAT(com, left_right_remove);
		return this;
	}
}
