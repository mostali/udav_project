package zk_com.base_ctr;

import mpu.core.ARGn;
import mpu.core.ARRi;
import mpu.IT;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;

public class SpanSwitch extends Span0 {

	private Integer currentPos = -1;

	public SpanSwitch(Component... coms) {
		super(coms);
	}

	public SpanSwitch switchToNext(int... nextPos) {
		Pare<Integer, Component> next = ARRi.next(IT.NE(getComs()), ARGn.toDefOr(currentPos, nextPos));
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
