package zk_com.base;


import mpu.X;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import zk_com.core.IZCom;
import zk_com.core.IZComExt;
import zk_page.ZKS;

public class Lb extends Label implements IZComExt {

	public Lb(String label) {
		this(label, null, null);
	}

	public Lb(String label, Object... args) {
		this(X.f(label, args), null, null);
	}

	public static Lb of(String label, Object... args) {
		return new Lb(X.f(label, args), null, null);
	}

	public static Lb line(String label) {
		Lb lb = new Lb(label, null, null);
		lb.setStyle("display:block");
		return lb;
	}

	public Lb(String label, String bgColor, String color) {
		super(label);

		if (bgColor != null) {
			ZKS.BGCOLOR(this, bgColor);
		}
		if (color != null) {
			ZKS.COLOR(this, color);
		}
	}

	public Lb bold(Integer... font_size) {
		return ZKS.BOLD_NICE(this, font_size);
	}

//	public void doReplace(FileMan.DirView dirView, boolean b, boolean b1, boolean b2) {
//		replace(dirView, b, b1, b2);
//	}
}
