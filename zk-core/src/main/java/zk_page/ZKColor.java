package zk_page;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.trees.UTreeNext;
import mpu.core.ARR;
import mpc.ui.ColorTheme;
import mpu.core.ARRi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ZKColor {
	WHITE(ColorTheme.WHITE),  //
	BLACK(ColorTheme.BLACK),  //
	GREEN(ColorTheme.GREEN), //
	GRAY(ColorTheme.GRAY), //
	BLUE(ColorTheme.BLUE), //
	LBLUE(ColorTheme.LBLUE), //
	RED(ColorTheme.RED), //
	YELLOW(ColorTheme.YELLOW), //
	ORANGE(ColorTheme.ORANGE) //
	;

	public final String[] variants;

	public static String toColorPropertyValue(Object bgColor) {
		if (bgColor instanceof CharSequence) {
			return bgColor.toString();
		} else if (bgColor instanceof ZKColor) {
			return ((ZKColor) bgColor).nextColor();
		}
		return bgColor.toString();
	}

	public static List<String> getAllColors() {
		return Arrays.stream(values()).flatMap(v -> Stream.of(v.variants)).collect(Collectors.toList());
	}

	public String nextColorSlow() {
		String color = UTreeNext.nextIndex0("zkc-ac", name(), ARR.as(variants));
		return color;
	}

	public String nextColor() {
		return ARRi.rand(variants);
	}

	public String nextBgColor_asHtmlProp() {
		return "background-color:" + nextColor();
	}

//	public String nextProp_Color() {
//		return "color:" + nextColor();
//	}

}
