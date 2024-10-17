package zk_old_core.index;

import lombok.SneakyThrows;
import mpu.X;
import org.zkoss.zul.Window;
import zk_page.core.PageSP;
import zk_page.core.SpVM;

@Deprecated
public class IndexPSP extends PageSP {

	public final String sd3;

	public IndexPSP(Window window, SpVM spVM) {
		this(window,spVM,null);
	}
	public IndexPSP(Window window, SpVM spVM, String sd3) {
		super(window, spVM);
		this.sd3 = sd3;
	}

	@SneakyThrows
	public void buildPageImpl() {
		window.appendChild(X.empty(sd3) ? FormPageIndex.buildView() : FormPageIndex.buildView(sd3));
	}

}
