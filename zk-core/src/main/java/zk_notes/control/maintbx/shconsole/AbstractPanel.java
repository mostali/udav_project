package zk_notes.control.maintbx.shconsole;

import mpc.log.L;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

public abstract class AbstractPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private Panelchildren panChild;

	private Textbox textboxIn;
	private Label lblboxOutType;
	private Textbox textboxOut;

	protected abstract void onClickButtonRun();

	protected abstract void onClickButtonContext();

	public AbstractPanel() {
		super();
	}

	public AbstractPanel onInitPanel() {

		this.panChild = new Panelchildren();

		this.panChild.setParent(this);

		this.onCreatePanel();

		return this;
	}

	private void onCreatePanel() {

		onConfigure();

		onAppendHead();

		onAppendBodyIn();

		onAppendBodyOut();

		onAppendFooter();

	}

	/**
	 * Append components
	 */
	private void onConfigure() {
		super.setTitle("СУФД Консоль");

		super.setBorder("normal");
		super.setDraggable("console.log(event)");
		super.setDroppable("console.log(event)");
//		super.setAction("show: slideDown({duration:1000})");
		setFloatable(true);
		setSizable(true);
		setCollapsible(true);
//		 setFramable(true);
		setMovable(true);
		super.setPopup("console.log(event)");
//		super.setCollapsible(true);
		super.setClosable(true);
//		super.setMovable(true);
		setSizable(true);
		setMinimizable(true);
		setBorder(true);

		setOpen(false);

		setAuService(new AuService() {
			@Override
			public boolean service(AuRequest auRequest, boolean b) {
				L.info("AuRequest:" + auRequest + ":" + b);
				return false;
			}
		});
	}

	private void onAppendHead() {
		Label label = new Label("Введите MVEL выражение:");
		label.setStyle("display:block");
		panChild.appendChild(label);
	}

	private void onAppendBodyIn() {
		Textbox comIn = getComIn();
		comIn.setParent(panChild);
	}

	private void onAppendBodyOut() {
		Label comOutType = getComOutType();
		comOutType.setParent(panChild);

		Textbox comOut = getComOut();
		comOut.setParent(panChild);
	}

	private void onAppendFooter() {

		/**
		 * Button "Run code"
		 */
		Button btnRunCode = new Button("Run");
		btnRunCode.setStyle("right: 0px;");

		btnRunCode.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				onClickButtonRun();
			}
		});
		btnRunCode.setParent(panChild);

		Button btnContext = new Button("Context");
//		btnContext.setStyle("display:block;");

		btnContext.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				onClickButtonContext();
			}
		});
		btnContext.setParent(panChild);

	}

	/**
	 * Get input value
	 */
	public String getInput() {
		String cmd = getComIn().getText();
		return cmd;
	}

	/**
	 * Set out values
	 */
	public void setOutputValues(String type, String out) {
		setOutputType(type);
		setOutput(out);
	}

	public void setOutput(String out) {
		getComOut().setValue(out);
	}

	public void setOutputType(String out) {
		getComOutType().setValue("TYPE:" + out);
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

	protected Label getComOutType() {
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
