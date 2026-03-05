package zk_pages.zznsi_pages.jira_tasks.core;

import mpe.cmsg.std.JqlCallMsg;
import mpu.IT;
import mpu.X;
import mpu.str.JOIN;
import udav_net.bincall.JiraBin;
import udav_net.bincall.jira.IssueContract;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
import zk_notes.factory.NFTrans;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.types.jqlMsg.JqlZSrv;
import zk_notes.node_state.proxy.StateProxyRW;
import zk_os.core.Sdn;
import zk_os.walkers.NoteWalker;
import zk_page.ZKR;
import zk_pages.zznsi_pages.jira_tasks.JtApp;

import java.util.*;
import java.util.function.Predicate;

public class IssuesViewBuilder {

	public final Sdn sdn;

	public final JiraBin.JqlLoader jqlLoader;

	public IssuesViewBuilder(Sdn sdn, JiraBin.JqlLoader jqlLoader) {
		this.sdn = sdn;
		this.jqlLoader = jqlLoader;
	}

	public static void newBuilderAndAdd(String jqlExpression) {
		String[] hlp = JqlZSrv.UserContext.get().hlpArgs;
		JqlCallMsg jqlCallMsg = JqlCallMsg.of(JqlCallMsg.buildMsgByHlp(hlp, IT.NB(jqlExpression, "set jqlExpression")));
		newBuilderAndAdd(Sdn.get(), jqlCallMsg);
	}

	public static IssuesViewBuilder newBuilderAndAdd(Sdn sdn, JqlCallMsg jqlMsg) {

		IssuesViewBuilder issuesViewBuilder = newBuilder(sdn, jqlMsg);

		issuesViewBuilder.doCycle_Add();

		JtApp.showInfo(X.f("Apply filter from '%s'", jqlMsg.toObjMsgId(null)));
		return issuesViewBuilder;
	}

	public static IssuesViewBuilder newBuilder(Sdn sdn, JqlCallMsg jqlMsg) {
		String keyAsJql = jqlMsg.getKeyAsJql();
		String[] hlp = JqlZSrv.UserContext.get().hlpArgs;
		JiraBin.JqlLoader jqlLoader = new JiraBin.JqlLoader(hlp, new JiraBin.JqlLoader.JqlFilter(hlp, keyAsJql));
		IssuesViewBuilder viewIssuesBuilder = new IssuesViewBuilder(sdn, jqlLoader);
		return viewIssuesBuilder;
	}

	public static void doUp(Sdn sdn, String[] hlp, String jqlExpression) {

//		ZKI_Quest.showMessageBoxErrorYN("Refresh", "Up?", yn -> {
//
//			if (!yn) {
//				return null;
//			}

		//skip jql

		Predicate<NodeDir> isJql = n -> {
			String s = n.nodeName();
			return s.equalsIgnoreCase(JqlCallMsg.KEY) || s.startsWith(JqlCallMsg.KEY + "@");
		};

		List<NodeDir> allNodes = NoteWalker.toList(Sdn.get(), isJql.negate());

		{
			//clear page ALL & serialize
			allNodes.forEach(n -> {
				new RecoveryStateSerialize(n.state()).serialize();
				NFTrans.deleteItem(n);
			});
		}

		// load by filter - NEW issues
		Map<String, IssueContract> newIssues = JiraBin.apiV3().getIssuesAsMap(jqlExpression, JqlZSrv.UserContext.get().hlpArgs);

		List add = new LinkedList();

		newIssues.forEach((key, issueContract) -> {
			Optional<NodeDir> nodeExisted = allNodes.stream().filter(n -> n.nodeName().equalsIgnoreCase(key)).findFirst();
			if (nodeExisted.isPresent()) {
				add.add("KNOWN:" + key);
//					new RecoveryState(nodeExisted.get().state()).applyNewOrPrev();
			} else {
				add.add("NEW:" + key);
//					NewNoteIssueBuilder newNoteIssueBuilder = NewNoteIssueBuilder.of(hlp, sdn, issueContract);
//					String data = newNoteIssueBuilder.buildDataNote();
//					newNoteIssueBuilder.openNew();
			}
		});

		doCycle(hlp, sdn, newIssues.values(), jqlExpression, true);

		ZKI_Quest.showMessageBox("Refreshed", JOIN.allByNL(add), ZKI.Level.INFO + "");

		ZKR.restartPage();
//			NotesSpace.rerenderFirst();

//			return null;
//		});

	}

	public void doCycle_Add() {
		doCycleAdd(jqlLoader.loadIssues());
	}

	private void doCycleAdd(List<IssueContract> issues) {
		doCycle(jqlLoader.hlp, sdn, issues, jqlLoader.jqlExpression(null), false);
	}

	public static void doCycle(String[] hlp, Sdn sdn, Collection<IssueContract> issues, String jqlExpression, boolean isUp) {

		List<NewNote> total = new LinkedList<>();

		for (IssueContract issue : issues) {
			NewNoteIssue newNoteIssue = NewNoteIssue.of(hlp, sdn, issue);
			newNoteIssue.build(isUp);
			total.add(newNoteIssue);
		}

		NewNoteJql newNoteJql = NewNoteJql.of(hlp, sdn, JqlCallMsg.TYPE);
		newNoteJql.openNew(isUp, jqlExpression);
		total.add(newNoteJql);


	}


}
