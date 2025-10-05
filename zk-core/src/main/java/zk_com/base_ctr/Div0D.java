package zk_com.base_ctr;

import mpu.core.ARG;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.event.DropEvent;
import zk_com.core.IZDnd;
import zk_page.ZKR;

public class Div0D extends Div0 implements IZDnd {
	@Override
	protected void init() {
		super.init();
		initDND();
	}

	protected void initDND() {
		IZDnd.initDND(this, ARG.ofNN(defaultAfterUpdateClb));
	}

	protected FunctionV1<DropEvent> defaultAfterUpdateClb = (e) -> ZKR.restartPage();

	public Div0D defaultAfterUpdateDragEventClb(FunctionV1<DropEvent> afterUpdateClb) {
		this.defaultAfterUpdateClb = afterUpdateClb;
		return this;
	}

}
