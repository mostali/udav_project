package zk_com.base_ctr;

import mpu.core.ARRi;
import mpu.IT;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;

public class DivSwitch extends Div0 {

	private int currentPos = -1;

	public DivSwitch(Component... coms) {
		super(coms);
	}

	public DivSwitch switchToNext() {
		Pare<Integer, Component> next = ARRi.next(IT.NE(getComs()), currentPos);
		currentPos = next.key();
		Component nextCom = next.val();
		getChildren().clear();
		appendChild(nextCom);
		return this;
	}


	@Override
	protected void init() {
		super.init();
		attachAll = false;
		switchToNext();
	}
}
