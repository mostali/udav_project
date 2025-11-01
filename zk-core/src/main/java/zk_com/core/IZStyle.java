package zk_com.core;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_page.ZKColor;
import zk_page.ZKS;

public interface IZStyle {

	default IZStyle font_size(Object px_pct) {
		HtmlBasedComponent com = (HtmlBasedComponent) this;
		ZKS.FONT_SIZE(com, px_pct);
		return this;
	}

	default IZStyle font_family(String family) {
		HtmlBasedComponent com = (HtmlBasedComponent) this;
		ZKS.addSTYLE(com, "font-family", family);
		return this;
	}

	default IZStyle width(Object px_pct) {
		return (IZCom) ZKS.WIDTH(comH(), px_pct);
	}

	default IZStyle zindex(Integer zindex) {
		return (IZCom) ZKS.ZINDEX(comH(), zindex);
	}

	default IZStyle width_height(Object width_px_pct, Object height_px_pct) {
		return (IZStyle) ZKS.WIDTH(ZKS.HEIGHT(comH(), height_px_pct), width_px_pct);
	}

	default IZStyle height(Object height) {
		return (IZCom) ZKS.HEIGHT(comH(), height);
	}

	default IZStyle block() {
		return (IZCom) ZKS.BLOCK(comH());
	}

	default IZStyle flex() {
		return (IZCom) ZKS.DISPLAY(comH(), "flex");
	}

	default IZStyle inlineBlock() {
		return (IZCom) ZKS.INLINE_BLOCK(comH());
	}

	default IZStyle border(String value) {
		ZKS.BORDER(comH(), value);
		return this;
	}

	default IZStyle border_radius(String value) {
		ZKS.BORDER_RADIUS(comH(), value);
		return this;
	}

	default Component com() {
		return ((Component) this);
	}

	default HtmlBasedComponent comH() {
		return (HtmlBasedComponent) com();
	}

	default IZStyle center(Integer... mode) {
		return (IZStyle) ZKS.CENTER(comH(), mode);
	}

	default IZStyle bgcolor(Object bgColor) {
		return (IZStyle) ZKS.BGCOLOR(comH(), ZKColor.toColorPropertyValue(bgColor));
	}

	default IZStyle color(Object bgColor) {
		return (IZStyle) ZKS.COLOR(comH(), ZKColor.toColorPropertyValue(bgColor));
	}

	default IZStyle float0(Boolean left_right_remove) {
		return (IZStyle) ZKS.FLOAT(comH(), left_right_remove);
	}

	default IZStyle relative() {
		return (IZStyle) ZKS.RELATIVE(comH());
	}

	default IZStyle padding(Object px_pct) {
		return (IZStyle) ZKS.PADDING(comH(), px_pct);
	}
	default IZStyle padding_left(Object px_pct) {
		return (IZStyle) ZKS.PADDING_LEFT(comH(), px_pct);
	}
	default IZStyle margin(Object px_pct) {
		return (IZStyle) ZKS.MARGIN(comH(), px_pct);
	}

	default IZStyle margin(Object t_px_pct, Object r_px_pct, Object b_px_pct, Object l_px_pct) {
		return (IZStyle) ZKS.MARGIN(comH(), t_px_pct, r_px_pct, b_px_pct, l_px_pct);
	}

	default IZStyle top_left(Object top_px_pct, Object left_px_pct) {
		ZKS.TOP_LEFT(comH(), top_px_pct, left_px_pct);
		return this;
	}

	default IZStyle top_rigth(Object bottom_px_pct, Object right_px_pct) {
		ZKS.TOP_RIGHT(comH(), bottom_px_pct, right_px_pct);
		return this;
	}
	default IZStyle bottom_rigth(Object bottom_px_pct, Object right_px_pct) {
		ZKS.BOTTOM_RIGHT(comH(), bottom_px_pct, right_px_pct);
		return this;
	}
	default IZStyle bottom(Object bottom_px_pct) {
		ZKS.BOTTOM(comH(), bottom_px_pct);
		return this;
	}

	default IZStyle title(String title) {
		HtmlBasedComponent it = (HtmlBasedComponent) this;
		comH().setTooltiptext(title);
		return this;
	}

	default IZStyle absolute() {
		return (IZStyle) ZKS.ABSOLUTE((HtmlBasedComponent) this);
	}

	default IZStyle fixed() {
		return (IZStyle) ZKS.FIXED((HtmlBasedComponent) this);
	}

	default IZStyle font_bold_nice(int font_size) {
		return (IZStyle) ZKS.BOLD_NICE((HtmlBasedComponent) this, font_size);
	}

	default ZKS zstyle() {
		return ZKS.of(comH());
	}

	default IZStyle borderSilver() {
		return border("2px solid silver");
	}

	default IZStyle borderRed() {
		return border("2px solid red");
	}

	default IZStyle contentEditable() {
		return ZKS.CONTENT_EDITABLE((HtmlBasedComponent) this);
	}

	default IZStyle cursorOnOver(String... cursor) {
		ZKS.addStyleAttr(comH(), "cursor", ARG.toDefOr("pointer", cursor));
		return this;
	}

	;;
}
