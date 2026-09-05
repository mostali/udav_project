package zk_com.base_ctr;

import lombok.RequiredArgsConstructor;
import mpu.Sys;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Lb;
import zk_form.events.ITouchEvent;

@RequiredArgsConstructor
public class SensSpanWith extends Span0 implements ITouchEvent {
	final String label;

	@Override
	protected void init() {
		super.init();
		Lb green = appendLb(label);
		ITouchEvent.initEvent(this, false);
	}

	@Override
	public void onTouchEvent(Event event) throws Exception {
		Sys.p("E:" + event.getData());
	}
}
