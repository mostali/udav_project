package zk_pages.zznsi_pages.jira_tasks.form;

import mpc.rfl.RFL;
import mpc.str.sym.SYMJ;
import mpe.cmsg.std.JqlCallMsg;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Bt;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0;
import zk_notes.node.NodeDir;
import zk_page.ZKS;

public abstract class SingleJqlForm extends Div0 {
	final Tbx tbxJql = Tbx.of("", "Jira Jql Syntax");

	{
		tbxJql.onOK(e -> doUp(e, ((Tbx) e.getTarget()).getValue()));
		tbxJql.onChangingAutoWidth(600).width(600);
//		tbxJql.width(500);
	}

	final Bt btAdd = Bt.of(SYMJ.SEARCH_FONAR + " Add ..", e -> doAdd(e, tbxJql.getValue()));
	final Bt btUp = Bt.of(SYMJ.ARROW_REPEAT_TRIANGLE_GREEN + " Up .. ", e -> doUp(e, tbxJql.getValue()));
	final Bt btReset = Bt.of(SYMJ.CLEAR + " Reset", e -> doReset(e, tbxJql.getValue()));

	protected abstract void doUp(Event e, String jqlVal);

	protected abstract void doAdd(Event e, String jqlVal);

	protected abstract void doReset(Event e, String jqlVal);

//	protected abstract void doCleanPage(Event e, String jqlVal);

	@Override
	protected void init() {
		super.init();

		NodeDir nodeDir = NodeDir.ofCurrentPage(JqlCallMsg.TYPE);
		if (nodeDir.existNode(false)) {
			JqlCallMsg iCallMsg = nodeDir.newInstanceCallMsgValid(null);
			if (iCallMsg != null) {
				tbxJql.setValue(iCallMsg.getKeyAsJql(null));
			}
		}

		ZKS.MARGIN_TOP(this, 15);
		ZKS.MARGIN_LEFT(this, 20.0);

		appendChilds(tbxJql, btUp, btAdd, btReset);

	}


	public String getJqlExpression() {
		return tbxJql.getValue();
	}

	@Override
	public String toString() {
		return RFL.scn(SingleJqlForm.class) + ":" + getJqlExpression();
	}


}
