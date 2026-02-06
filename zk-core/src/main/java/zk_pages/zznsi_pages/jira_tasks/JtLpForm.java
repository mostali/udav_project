package zk_pages.zznsi_pages.jira_tasks;

import mpc.rfl.RFL;
import mpu.IT;
import mpu.X;
import mpu.str.SPLIT;
import zk_com.base.Bt;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0;
import zk_pages.zznsi_pages.jira_tasks.core.AutoFF;
import zk_pages.zznsi_pages.jira_tasks.core.PageCtx;

import java.util.List;

public class JtLpForm extends Div0 {
	final Tbx tbxHost = Tbx.of("", "jira_url: Url to Jira").onChangingAutoWidth(100);
	final Tbx tbxLogin = Tbx.of("", "jira_login: login").onChangingAutoWidth(100);
	final Tbx tbxPassword = Tbx.of("", "jira_pass: password").onChangingAutoWidth(100);
	final Bt btSearch = Bt.of("Test Connect..", e -> doConnect());

	protected void doConnect() {
		X.p(":" + toString());
	}

	protected String getHost() {
		return tbxHost.getValue();
	}

	protected String getLogin() {
		return tbxLogin.getValue();
	}

	protected String getPassword() {
		return tbxPassword.getValue();
	}

	final PageCtx pageCtx = PageCtx.of();

	@Override
	protected void init() {
		super.init();

		IT.notEmpty(pageCtx.allowed, "set pageCtx");

		new AutoFF(pageCtx).doAutoFillField(tbxHost, "jira_url");
		new AutoFF(pageCtx).doAutoFillField(tbxLogin, "jira_login");
		new AutoFF(pageCtx).doAutoFillField(tbxPassword, "jira_pass");

		appendChilds(tbxHost, tbxLogin, tbxPassword, btSearch);

	}


	@Override
	public String toString() {
		return RFL.scn(JtLpForm.class) + ":" + getHost() + " -> " + getLogin() + " -> " + "*******";
	}
}
