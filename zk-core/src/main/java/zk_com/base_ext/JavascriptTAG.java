package zk_com.base_ext;

import mpu.X;
import org.zkoss.zul.Html;
import zk_com.core.IZCom;

public class JavascriptTAG extends Html implements IZCom {

	public JavascriptTAG(String tagContent, Object... args) {
		super("<script>" + X.f(tagContent, args) + "</script>");
	}

	public static JavascriptTAG of(String tagContent, Object... args) {
		return new JavascriptTAG(tagContent, args);
	}

}
