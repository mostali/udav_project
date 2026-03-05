package zk_form.control;

import org.zkoss.zk.ui.event.Event;
import zk_com.base.Tbxm;
import zk_notes.node_state.ObjState;

public class StatePropTbxm extends Tbxm {
	final ObjState pageState;
	final String prop;

	public StatePropTbxm(ObjState state, String prop) {
		super(state.get(prop, ""), DIMS.BYCONTENT);
		this.pageState = state;
		this.prop = prop;
		this.placeholder(prop);
	}

	@Override
	protected void onSubmitTextValue(Event e) {
		super.onSubmitTextValue(e);
		pageState.set(prop, getValue());

	}
}
