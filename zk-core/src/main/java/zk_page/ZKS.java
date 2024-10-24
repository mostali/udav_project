package zk_page;

import lombok.RequiredArgsConstructor;
import mpc.exception.NI;
import mpc.ui.UColorTheme;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.html.CLASS;
import mpc.html.STYLE;
import mpc.json.GsonMap;
import mpc.num.UNum;
import mpc.rfl.RFL;
import mpu.core.UDbl;
import mpu.str.RANDOM;
import mpu.IT;
import mpu.X;
import mpu.str.SPLIT;
import mpu.str.UST;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.HtmlBasedComponent;
import mpc.map.UMap;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Span;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zul.impl.XulElement;
import utl_web.UWeb;
import zk_form.string_analyze.StringInfo;

import java.util.*;

//open close nojs - https://codepen.io/B_Sanchez/pen/RpKJVx
//https://developer.mozilla.org/en-US/docs/Web/CSS/transform
//STYLE
@RequiredArgsConstructor
public class ZKS {
	public static final int DEFAULT_TEXT_SIZE_AUTO_DIMS = 16;
	public static final int MAX_INDEX = 9999;

	//https://html-color.codes/green
//	public static final String[] BLUE = AR.of("", "", "", "", "", "", "", "");

	final HtmlBasedComponent com;

	public static ZKS of(HtmlBasedComponent com) {
		return new ZKS(com);
	}

	public static String classRnd(HtmlBasedComponent com, String pfx, int len) {
		String str = RANDOM.ALPHA(pfx, len);
		com.setClass(str);
		return str;
	}

	public static void bgcolor_green(XulElement com) {
		BGCOLOR(com, "green");
	}

//	public static void border_raduis(XulElement com) {
//		BORDER_RADIUS(com, "green");
//	}

	public static void PADDING0(Window window) {
		PADDING_WIN(window, 0, 0);
	}

	public static void PADDING_WIN(Window window, Object win_px_pct, Object content_px_pct) {
		ZKS.addSTYLE(window, "padding:" + toPxPct(win_px_pct));
		window.setContentStyle("padding:" + toPxPct(content_px_pct));
	}

	public static Integer px(String width, Integer... defRq) {
		if (width != null && width.endsWith("px")) {
			Integer vl = UST.INT(width.substring(0, width.length() - 2), null);
			if (vl != null) {
				return vl;
			}
		}
		return ARG.toDefRq(defRq);
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

	public static <C extends HtmlBasedComponent> C PADDING(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "padding", px_pct);
	}

	public static <C extends HtmlBasedComponent> C MARGIN(C com, Object px_pct) {
		return applyStylePropertyPxPct(com, "margin", px_pct);
	}

	public static <C extends HtmlBasedComponent> C applyStylePropertyPxPct(C com, String name, Object px_pct) {
		return px_pct == null ? rmStyleAttr(com, name) : addSTYLE(com, name + ":" + toPxPct(px_pct));
	}


	private static String toPxPct(Object value) {
		if (!(value instanceof Number)) {
			return value.toString();
		} else if (value instanceof Double) {
			return ((Double) value).doubleValue() + "%";
		} else {
			return ((Number) value).intValue() + "px";
		}
	}

	public static <C extends HtmlBasedComponent> C VFLEX_MIN(C com) {
		com.setVflex("min");
		return com;
	}

	public static <C extends HtmlBasedComponent> C addStyle(C com, GsonMap style) {
		String width = style.getAsStr("width", null);
		if (width != null) {
			addStyleAttr(com, "width", width);
		}
		String height = style.getAsStr("height", null);
		if (height != null) {
			addStyleAttr(com, "height", height);
		}
		return com;
	}

	public static class AutoDims {

		public static <C extends InputElement> C initAutoDims(C com) {
			String text = com.getText();
			initAutoDims(com, text);
			return com;
		}

		public static <C extends HtmlBasedComponent> C initAutoDims(C com, String content) {
			return initAutoDims(com, content, DEFAULT_TEXT_SIZE_AUTO_DIMS);
		}

		public static <C extends HtmlBasedComponent> C initAutoDims(C com, String content, int text_size) {

			NI.stop("check - what is not used. RMM");
			int[] dims = getAutoDims(content, text_size);

			com.setWidth(dims[0] + "px");
			com.setHeight(dims[1] + "px");

	//		ZKS.addStyleAttr(com, "width", dims[0] + "px");
	//		ZKS.addStyleAttr(com, "height", dims[0] + "px");

			return com;
		}

		public static int[] getAutoDims(String content, int text_size) {
			StringInfo si = StringInfo.of(content);
			double letHeight = (text_size * 1.8);
			double letWidth = (text_size * 0.5);
			double offsetW = 3 * letWidth;
			double offsetH = 1.3 * letHeight;
			int width = (int) (si.getMaxlineLength() * letWidth + offsetW);//+ 3 * tWidth
			int height = (int) (si.getLines().size() * letHeight + offsetH);//+ 2 * tHeight

			width = UNum.minLE(width, 100);
			height = UNum.minLE(height, 50);

			width = UNum.maxGE(width, 600);
			height = UNum.maxGE(height, 400);

	//		int width = BtWidth.getWidth(content);
	//		int lines = StringInfo.getLines(content);
	//		int height = BtHeight.getHeight(lines);
	//		if (lines <= 3) {
	//			height = 50;
	//		}
	//		if (content.length() <= 30) {
	//			width = 100;
	//		}

			return new int[]{width, height};

		}

		public static String getAutoWidth_50_100_200_300_400(String value, double k) {
			if (value == null) {
				return null;
			}
			int len;
			if (value.length() < 4) {
				len = 60;
			} else if (value.length() <= 8) {
				len = 80;
			} else if (value.length() <= 12) {
				len = 120;
			} else if (value.length() <= 18) {
				len = 200;
			} else if (value.length() <= 24) {
				len = 300;
			} else {
				len = 400;
			}
			return toPxPct((int) (len * k));
		}

		public static String getAutoWidth_50_100_200(String value, double k) {
			if (value == null) {
				return null;
			}
			int len;
			if (value.length() < 4) {
				len = 60;
			} else if (value.length() <= 8) {
				len = 80;
			} else if (value.length() <= 12) {
				len = 120;
			} else {
				len = 200;
			}
			return toPxPct((int) (len * k));
		}
	}

	//	public static int[] getAutoDims(String content) {
//		return getAutoDims(content, DEFAULT_TEXT_SIZE_AUTO_DIMS);
//	}

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

	public static <C extends HtmlBasedComponent> C BORDER_RED(C com) {
		return BORDER(com, "1px solid gray");
	}

	public static <C extends HtmlBasedComponent> C BORDER(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "border") : ZKS.addStyleAttr(com, "border", value);
	}

	public static <C extends HtmlBasedComponent> C BORDER_TOP(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "border-top") : ZKS.addStyleAttr(com, "border-top", value);
	}

	public static <C extends HtmlBasedComponent> C BORDER_BOTTOM(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "border-bottom") : ZKS.addStyleAttr(com, "border-bottom", value);
	}

	public static <C extends HtmlBasedComponent> C TRANSFORM_SCALE(C com, Double value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "transform") : ZKS.addStyleAttr(com, "transform", "scale(" + UDbl.scale(value, 1) + ")");
	}

	public static <C extends HtmlBasedComponent> C BORDER_RADIUS(C com, Object px_or) {
		return px_or == null ? ZKS.rmStyleAttr(com, "border-radius") : ZKS.addStyleAttr(com, "border-radius", px_or instanceof Integer ? px_or + "px" : px_or.toString());

	}

	public static <C extends HtmlBasedComponent> C FONT_FAMILY0(C com) {
		return FONT_FAMILY(com, "system-ui");
	}

	public static <C extends HtmlBasedComponent> C FONT_FAMILY1(C com) {
		return FONT_FAMILY(com, "monospace");
	}

	public static <C extends HtmlBasedComponent> C FONT_FAMILY(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "font-family") : ZKS.addStyleAttr(com, "font-family", value);
	}

	public static <C extends HtmlBasedComponent> C POSITION(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "position") : ZKS.addStyleAttr(com, "position", value);
	}

	public static <C extends HtmlBasedComponent> C TEXT_DECARATION_NONE(C com) {
		return TEXT_DECARATION_NONE(com, "none");
	}

	public static <C extends HtmlBasedComponent> C TEXT_DECARATION_NONE(C com, String value) {
		return X.empty(value) ? ZKS.rmStyleAttr(com, "text-decoration") : ZKS.addStyleAttr(com, "text-decoration", value);
	}

	public static <C extends HtmlBasedComponent> String getStyleAttrValue(C com, String attrName, String... defRq) {
		return UMap.getAsString(STYLE.toMap(com.getStyle()), attrName, defRq);
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

	public static boolean applyProp(HtmlBasedComponent com, Map<String, Object> props, String prop) {
		if (!props.containsKey(prop)) {
			return false;
		}
		switch (prop) {
			case "top":
				TOP(com, props.get("top"));
				return true;
			case "left":
				LEFT(com, props.get("left"));
				return true;
			case "width":
				WIDTH(com, props.get("width"));
				return true;
			case "height":
				HEIGHT(com, props.get("height"));
				return true;
//			case "open":
//				HEIGHT(com, props.get("height"));
//				return true;
			default:
				throw new WhatIsTypeException(prop);
		}
//		return false;
	}

	public static <C extends HtmlBasedComponent> C DRAG_DROP(C com, Boolean... drag_drop) {
		com.setDraggable(ARRi.item(drag_drop, 0, true) + "");
		com.setDroppable(ARRi.item(drag_drop, 1, true) + "");
		return com;
	}

	public static boolean isPx(String vl) {
		return vl != null && StringUtils.endsWithIgnoreCase(vl, "px");
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

	public static void APPLY_RANDOM_TOPLEFT(HtmlBasedComponent ln, boolean... absolute) {
		ZKS.TOP(ln, RANDOM.RANGE(260, 720) + "px");
		ZKS.LEFT(ln, RANDOM.RANGE(360, 1200) + "px");
	}

	public static void WIDTH_HEIGHT100(HtmlBasedComponent com) {
		WIDTH_HEIGHT(com, 100.0, 100.0);
	}

	public static void enableDarkTheme(HtmlBasedComponent com) {

		ZKS.BGCOLOR(com, UColorTheme.BLACK[1]);
		ZKS.COLOR(com, UColorTheme.WHITE[1]);

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
		T first = ZKCFinder.findFirstIn_Page(clazz, recursive_or_first, null);
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
		T first = ZKCFinder.findFirstIn_Page(clazz, recursive_or_first, null);
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
		T first = ZKCFinder.findFirstIn_Page(clazz, recursive_or_first, null);
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

	public static <C extends HtmlBasedComponent> C ZINDEX(C com, int zindex) {
		if (zindex <= 0) {
			addStyle(com, "z-index:" + zindex);
		}
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
			addStyleAttr(com, "font-size", ARG.toDef(font_size) + "px");
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

	public static <C extends HtmlBasedComponent> C TOP(C com, Object px_pct) {
		com.setTop(px_pct == null ? null : toPxPct(px_pct));
		return com;
	}

	public static <C extends HtmlBasedComponent> C FONT_SIZE(C com, Object px_pct) {
		return px_pct == null ? rmStyleAttr(com, "font-size") : addStyleAttr(com, "font-size", toPxPct(px_pct));
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

	public static <C extends HtmlBasedComponent> C OVERFLOW(C com, Integer val) {
		if (val == null) {
			return rmStyleAttr(com, "overflow");
		} else if (!(val instanceof Number)) {
			return addStyle(com, "overflow:" + val.toString());
		}
		int valInt = ((Number) val).intValue();
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
		return addStyle(com, "zoom:" + zoom);
	}

	public static <C extends HtmlBasedComponent> C OPACITY(C com, Double pattern) {
		return addStyle(com, "opacity:" + pattern);
	}

	public static <C extends HtmlBasedComponent> C BGCOLOR(C com, String color) {
		return addStyle(com, "background-color:" + color);
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

	public static <C extends HtmlBasedComponent> C HEIGHT_MIN(C com, String height) {
		addStyleAttr(com, "min-height", height);
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
