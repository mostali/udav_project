package zk_page;

import lombok.RequiredArgsConstructor;
import mpc.exception.NI;
import mpc.num.UNum;
import mpe.core.ERR;
import mpu.core.ARG;
import mpu.str.SPLIT;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.impl.InputElement;
import zk_notes.AxnTheme;

import java.util.List;

public class ZKS_AutoDims {

	public static <C extends InputElement> C initAutoDims(C com, boolean... skipCheck) {
		String text = com.getText();
		initAutoDims(com, text, skipCheck);
		return com;
	}

	public static <C extends HtmlBasedComponent> C initAutoDims(C com, String content, boolean... skipCheck) {
		return initAutoDims(com, content, AxnTheme.DEFAULT_FONT_SIZE_AUTO_DIMS, skipCheck);
	}

	public static <C extends HtmlBasedComponent> C initAutoDims(C com, String content, int text_size, boolean... skipCheck) {

		if (ARG.isDefNotEqTrue(skipCheck)) {
			System.err.println(ERR.getStackTraceShort(new NI("check - what is not used. RMM"), 10));
		}

//			NI.stop("check - what is not used. RMM");

		int[] dims = StringInfo.getAutoDims(content, text_size);

		com.setWidth(dims[0] + ZKS.PX);
		com.setHeight(dims[1] + ZKS.PX);

		//		ZKS.addStyleAttr(com, "width", dims[0] + "px");
		//		ZKS.addStyleAttr(com, "height", dims[0] + "px");

		return com;
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
		return ZKS.toPxPct((int) (len * k));
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
		return ZKS.toPxPct((int) (len * k));
	}

	@RequiredArgsConstructor
	public static class StringInfo {

		public final String org;

		private List<String> lines;

		public static int[] getAutoDims(String content, int text_size) {
			StringInfo si = of(content);
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

			return new int[]{width, height};

		}

		public List<String> getLines() {
			return lines != null ? lines : (lines = SPLIT.allByNL(org));
		}

		public int getMaxlineLength() {
			List<String> lines = getLines();
			return lines.size() == 0 ? 0 : lines.stream().mapToInt(l -> l.length()).max().getAsInt();
		}

		public static StringInfo of(String content) {
			return new StringInfo(content);
		}
	}
}
