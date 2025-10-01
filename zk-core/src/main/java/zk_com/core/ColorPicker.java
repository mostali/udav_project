package zk_com.core;

import lombok.RequiredArgsConstructor;
import mpc.rfl.RFL;
import mpc.ui.ColorTheme;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Bt;
import zk_com.base.Lb;
import zk_com.base_ctr.Div0;
import zk_page.ZKS;

import java.lang.reflect.Field;
import java.util.List;

public class ColorPicker extends Div0 {

	static Integer totalColors = null;

	@Override
	protected void init() {
		super.init();

		appendChild(newCloseButton());

		setZindex(99999);
		ZKS.ABSOLUTE(this);
		ZKS.WIDTH_HEIGHT100(this);
		ZKS.BGCOLOR(this, "white");

		appendChild(new ThemeColorPickerContainer() {
			@Override
			public void onCLICK_COLOR(Event event, String parentName, String colorCode) {
				ColorPicker.this.onCLICK_COLOR(event, parentName, colorCode);
			}
		});

		appendChild(newCloseButton());

	}

	private Component newCloseButton() {
		return (Component) new Bt("Close Color Picker").onCLICK(e -> removeMe()).width_height(100.0, 5.0);
	}

	public void onCLICK_COLOR(Event event, String parentName, String colorCode) {
		//override
	}


	public static abstract class ThemeColorPickerContainer extends Div0 {

		public abstract void onCLICK_COLOR(Event event, String parentName, String colorCode);

		@Override
		protected void init() {
			super.init();

			ZKS.FLEX(this, "row");

			List<Field> colorsNames = RFL.fields(ColorTheme.class, String[].class);
			List<String[]> colors = RFL.fieldValuesSt(ColorTheme.class, String[].class, false);
			if (totalColors == null) {
				totalColors = colorsNames.size();
			}
			for (int i = 0; i < colorsNames.size(); i++) {
				for (int i1 = 0; i1 < colors.size(); i1++) {
					if (i != i1) {
						continue;
					}

					String colorName = colorsNames.get(i).getName();
					String[] values = colors.get(i);
					ColorSpace colorSpace = new ColorSpace(colorName, values) {
						@Override
						public void onCLICK_COLOR(Event event, String parentName, String colorCode) {
							ThemeColorPickerContainer.this.onCLICK_COLOR(event, parentName, colorCode);
						}
					};

					appendChild(colorSpace);
				}
			}
		}


		@RequiredArgsConstructor
		public abstract static class ColorSpace extends Div0 {
			final String colorName;
			final String[] colorValues;

			@Override
			protected void init() {
				super.init();

				ZKS.WIDTH(this, 100.0 / totalColors);

				appendChild((Component) new Lb(colorName).block().center());

				Div0 row = new Div0();
				appendChild(row);

				ZKS.FLEX(row, "column");


				for (String value : colorValues) {

					Lb singleColor = row.appendLb(value);
////				Ln singleColor = row.appendLn(null, value);

////				singleColor.block();
////				EventHighlightForm.applyOnOff_MouseOverOut(singleColor);
////				ZKS.MARGIN(lb, "50px");

					ZKS.BGCOLOR(singleColor, value);
					ZKS.PADDING(singleColor, "50px");
					singleColor.onCLICK((Event e) -> onCLICK_COLOR(e, colorName, value));
				}

			}

			public abstract void onCLICK_COLOR(Event event, String parentName, String colorCode);
		}

	}


}
