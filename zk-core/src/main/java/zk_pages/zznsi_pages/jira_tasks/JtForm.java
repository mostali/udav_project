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

public class JtForm extends Div0 {
	final Tbx tbxProject = Tbx.of("", "project: Project1,Project2").onChangingAutoWidth(100);
	final Tbx tbxStatus = Tbx.of("", "status: Status1,Status2").onChangingAutoWidth(100);
	final Tbx tbxIssuetype = Tbx.of("", "issuetype: Issuetype1,Issuetype2").onChangingAutoWidth(100);
	final Bt btSearch = Bt.of("Search..", e -> doSearch());

	protected void doSearch() {
		X.p(":" + toString());
	}

	protected List<String> getProject() {
		return SPLIT.allByComma(tbxProject.getValue());
	}

	protected List<String> getStatus() {
		return SPLIT.allByComma(tbxStatus.getValue());
	}

	protected List<String> getIssuetype() {
		return SPLIT.allByComma(tbxIssuetype.getValue());
	}


	final PageCtx pageCtx = PageCtx.of();

	@Override
	protected void init() {
		super.init();

		IT.notEmpty(pageCtx.allowed, "set pageCtx");

		new AutoFF(pageCtx).doAutoFillField(tbxProject, "project");
		new AutoFF(pageCtx).doAutoFillField(tbxStatus, "status");
		new AutoFF(pageCtx).doAutoFillField(tbxIssuetype, "issuetype");

		appendChilds(tbxProject, tbxStatus, tbxIssuetype, btSearch);

	}


	@Override
	public String toString() {
		return RFL.scn(JtForm.class) + ":" + getProject() + " -> " + getStatus() + " -> " + getIssuetype();
	}
}
