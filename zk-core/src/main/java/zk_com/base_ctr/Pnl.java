package zk_com.base_ctr;

import lombok.Setter;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.*;
import zk_page.ZKJS;

public class Pnl extends Panel {
	private static final long serialVersionUID = 1L;

	private Panelchildren panChild;

	public Pnl() {
		super();
		this.panChild = new Panelchildren();
		this.panChild.setParent(this);
	}


	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
//		preinit();
		init();
		afterinit();
		super.onPageAttached(newpage, oldpage);
	}

	private void afterinit() {
		if (showEffect_ms > 0) {
			ZKJS.setAction_ShowEffect(this, showEffect_ms);
		}
		if (border != null) {
			super.setBorder("normal");
		}

	}

	@Setter
	int showEffect_ms = -1;

	String border = "normal";

	public Pnl init() {
		return this;
	}

	/**
	 * Append components
	 */
//	private void onConfigure() {
//		super.setTitle("Консоль");
//		super.setBorder("normal");
//		super.setDraggable("true");
//		setFloatable(true);
//		setDroppable("true");
//		setSizable(true);
//		// setFramable(true);
//		// super.setPopup("asdasdasd");
//		super.setCollapsible(true);
//		super.setClosable(true);
//		super.setMovable(true);
//
//	}

//	private void onAppendHead() {
//		Label label = new Label("Input Command:");
//		label.setStyle("display:block");
//		panChild.appendChild(label);
//	}


}
