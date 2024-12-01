package zk_page;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.trees.UTreeNext;
import mpu.core.ARR;
import mpc.ui.UColorTheme;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ZKColor {
	BLACK(UColorTheme.BLACK), GREENS(UColorTheme.GREEN), GRAYS(UColorTheme.GRAY), BLUES(UColorTheme.BLUE), REDS(UColorTheme.RED), YELLOWS(UColorTheme.YELLOW), ORANGE(UColorTheme.ORANGE);

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

	public String nextColor() {
		String color = UTreeNext.nextIndex0("zkc-ac", name(), ARR.as(variants));
		return color;
	}

	public String nextPropBgColor() {
		return "background-color:" + nextColor();
	}

	public String nextPropFontColor() {
		return "color:" + nextColor();
	}
}
