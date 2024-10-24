package zk_com.base_ctr;

import mpu.core.ARG;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.event.DropEvent;
import zk_com.core.IZDropDiv;
import zk_page.ZKR;

public class Div0D extends Div0 implements IZDropDiv {
	@Override
	protected void init() {
		super.init();
		initDND();
	}

	protected void initDND() {
		IZDropDiv.initDND(this, ARG.ofNN(afterUpdateClb));
	}

	protected FunctionV1<DropEvent> afterUpdateClb = (e) -> ZKR.restartPage();

	public Div0D afterUpdateDragEventClb(FunctionV1<DropEvent> afterUpdateClb) {
		this.afterUpdateClb = afterUpdateClb;
		return this;
	}

}
