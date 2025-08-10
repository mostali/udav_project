package zk_com.charts;

import lombok.Setter;
import mpu.str.RANDOM;
import mpu.str.STR;
import zk_com.base.Xml;
import zk_page.ZKS;

public class Chart extends Xml {

	private @Setter String id;

	public Chart(String html, Object... args) {
		super(html, args);
	}

	@Override
	protected void init() {
//		ZKS.BLOCK(this);
		super.init();
	}

	public static String wrapLabel(String label) {
		return STR.wrapIfNot(STR.unwrap(label, "'").replace("'", "\'"), "'");
	}


	public static String getRandomdId(String lc) {
		return lc + "_" + RANDOM.ALPHA(5);
	}

}
