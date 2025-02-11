package zk_com.base_ext;

import mpu.X;
import org.zkoss.zul.Html;
import zk_com.core.IZCom;

public class StyleTAG extends Html implements IZCom {

	public StyleTAG(String tagContent, Object... args) {
		super("<style>" + X.f(tagContent, args) + "</style>");
	}

	public static StyleTAG of(String tagContent, Object... args) {
		return new StyleTAG(tagContent, args);
	}

}
