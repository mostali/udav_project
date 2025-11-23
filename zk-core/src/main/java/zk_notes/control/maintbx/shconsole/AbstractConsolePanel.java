package zk_notes.control.maintbx.shconsole;

import mpc.str.sym.SYMJ;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import zk_com.base.Bt;
import zk_com.base_ctr.Menupopup0;
import zk_page.ZKS;
import zk_page.events.ZKE;

public abstract class AbstractConsolePanel extends Panel {
	private static final long serialVersionUID = 1L;

	private Panelchildren panChild;

	private Textbox textboxIn;
	private Label lblboxOutType;
	private Textbox textboxOut;

	protected abstract void onClickButton_RUN();

	protected abstract void onClickButton_HISTORY();

	protected void fillHistoryMenu(Menupopup0 menu) {

	}

	public AbstractConsolePanel() {
		super();
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		onInitPanel();
		super.onPageAttached(newpage, oldpage);
	}

	private AbstractConsolePanel onInitPanel() {

		this.panChild = new Panelchildren();

		this.panChild.setParent(this);

		this.onCreatePanel();

		return this;

	}

	private void onCreatePanel() {

		ZKS.applyNiceBg(this, "#f0f8ff", "#f5f5f5");

		ZKE.addEventListenerCtrl(this, Events.ON_CTRL_KEY, ZKE.SHORTCUT_STORE_ALT_Q, e -> onClickButton_RUN());

		onConfigure();

//		setStyle("color:lightyellow");

		setTitle(title());

//		onAppendInHead();

		onAppendBodyIn();

		onAppendBodyOut();

		onAppendFooter();

	}

	/**
	 * Append components
	 */
	private void onConfigure() {

		super.setBorder("normal");
		super.setDraggable("true");
		super.setAction("show: slideDown({duration:1000})");
		setFloatable(true);
		setDroppable("true");
		setSizable(true);
		// setFramable(true);
		// super.setPopup("asdasdasd");
		super.setCollapsible(true);
		super.setClosable(true);
		super.setMovable(true);

	}

	protected String title() {
		return SYMJ.TERMINAL + " Shell / Alt+q ";
	}

	protected String titleLabel() {
		return "Command";
	}

	private void onAppendInHead() {
		Label label = new Label(titleLabel());
		label.setStyle("display:block;");
		panChild.appendChild(label);
	}

	private void onAppendBodyIn() {
		Textbox comIn = getComIn();
		comIn.setParent(panChild);
	}

	private void onAppendBodyOut() {
		Label comOutType = getComOutHeader();
		comOutType.setParent(panChild);

		Textbox comOut = getComOut();
		comOut.setParent(panChild);
	}

	private void onAppendFooter() {

		Button btnRunCode = new Button("Run");
		btnRunCode.setStyle("right: 0px;");

		btnRunCode.addEventListener(Events.ON_CLICK, event -> onClickButton_RUN());
		btnRunCode.setParent(panChild);

		Bt btnContext = new Bt(btName());
		btnContext.addEventListener(Events.ON_CLICK, event -> onClickButton_HISTORY());
		btnContext.setParent(panChild);

		Menupopup0 menu = btnContext.getOrCreateMenupopup((HtmlBasedComponent) this.getParent());
		fillHistoryMenu(menu);

	}

	protected String btName() {
		return "History";
	}

	//
	//

	public void setOut(String header, String out) {
		setOutHeader(header);
		setOut(out);
	}

	public void setOut(String out) {
		getComOut().setValue(out);
	}

	public void setOutHeader(String out) {
		getComOutHeader().setValue("Out:" + out);
	}

	/**
	 * Get main components
	 */
	protected Textbox getComIn() {
		if (textboxIn == null) {
			textboxIn = new Textbox();
			textboxIn.setRows(12);
			textboxIn.setCols(150);
			textboxIn.setStyle("display:block");
		}
		return textboxIn;
	}

	protected Label getComOutHeader() {
		if (lblboxOutType == null) {
			lblboxOutType = new Label();
			lblboxOutType.setStyle("right:0px");
		}
		return lblboxOutType;
	}

	protected Textbox getComOut() {
		if (textboxOut == null) {
			textboxOut = new Textbox();
			textboxOut.setRows(12);
			textboxOut.setCols(150);
			textboxOut.setStyle("display:block");
			textboxOut.setDisabled(true);
		}
		return textboxOut;
	}

}
