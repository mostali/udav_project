package zk_form.control.breadcrumbs;

import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Ln;

public class BreadLn extends Ln {

	public BreadLn(String name) {
		this(name, null);
	}

	public BreadLn(String name, SerializableEventListener action) {
		super(name);
		if (action != null) {
			onCLICK(action);
		}
		decoration_none();
		padding(5);
	}



}
