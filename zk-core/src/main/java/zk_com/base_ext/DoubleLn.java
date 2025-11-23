package zk_com.base_ext;

import mpu.core.ARG;
import zk_com.base.Ln;
import zk_com.base_ctr.Span0;
import zk_notes.ANI;
import zk_page.ZKColor;
import zk_page.ZKS;

public class DoubleLn extends Span0 {

	public final Ln ln;

	public DoubleLn(Ln masterLink, String url, boolean... blank) {
		super();


		if (ARG.isDefNotEqTrue(blank)) {
			ln = new Ln(ANI.LINK2IN, url, false).decoration_none();
		} else {
			ln = new Ln(ANI.LINK2OUT, url, true).decoration_none();
		}

		if (false) {
			appendChild(masterLink);
			appendChild(ln);
		} else {//swap
			String l1 = masterLink.getLabel();
			String l2 = ln.getLabel();
			masterLink.setLabel(l2);
			ln.setLabel(l1);
			applyStyle();
			appendChild(ln);
//			appendChild(masterLink);
		}

	}

	private void applyStyle() {
		ln.color(ZKColor.BLACK.nextColor());
		ln.inlineBlock();
		ln.padding(5);
		ZKS.applyNiceBg(ln, ZKColor.BLUE.nextColor(), ZKColor.WHITE.nextColor());
		ZKS.OPACITY(ln, 0.96);
		ZKS.BORDER_RADIUS(ln, 5);
	}

	public DoubleLn randomAbsPosition() {
		absolute();
		ZKS.APPLY_RANDOM_TOPLEFT(this);
		return this;
	}
}
