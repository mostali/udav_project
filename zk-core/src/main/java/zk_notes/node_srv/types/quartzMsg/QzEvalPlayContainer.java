package zk_notes.node_srv.types.quartzMsg;

import mpc.str.sym.SYMJ;
import mpe.str.StringWalkBuilder;
import mpu.X;
import mpu.core.ARR;
import mpu.func.FunctionV1;
import org.quartz.JobKey;
import org.zkoss.zul.Messagebox;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.PlayContainer;
import zk_os.quartz.QzApiEE;
import zk_page.ZKME;

import java.util.Set;

public class QzEvalPlayContainer {

	public static PlayContainer toPlayContainer(PlayContainer.PlayLn playLn) {
		return new PlayContainer(playLn, Xml.NBSP(), new QzRmLn(playLn.node), Xml.NBSP(), new QzShowAll(playLn.node));
	}

	public static class QzRmLn extends Ln {
		public QzRmLn(NodeDir node) {
			super(" " + SYMJ.FAIL_RED_THINK);
			title("Remove all Quartz tasks");
			addEventListener(e -> {
				FunctionV1<Boolean> f = r -> {
					String msg;
					if (r == null) {
						return;
					} else if (r) {
						int[] deleteRslt = QzECS.deleteAll(node, true);
						msg = X.f("All(%s) taskof node '%s' was CLEAN from Scheduler", ARR.ofInt(deleteRslt), node.nodeId());
					} else {
						int[] deleteRslt = QzApiEE.deleteAllTotal(true);
						msg = X.f("All(%s) task was CLEAN from Scheduler", ARR.ofInt(deleteRslt));
					}
					ZKI.infoAfterPointer(msg, ZKI.Level.INFO);
				};
				String msg = X.f("Remove mode -> Single (YES) | ALL (NO) <- (%s/%s) ?", X.sizeOf(QzECS.findAllJobKeys(node)), X.sizeOf(QzApiEE.getAllJobKeys()));
				ZKI_Quest.showMessageBoxYNC_ofLevel("Remove quartz task's", msg, f, Messagebox.QUESTION);

			});
		}
	}

	public static class QzShowAll extends Ln {
		public QzShowAll(NodeDir node) {
			super(" " + SYMJ.QUEST_RED);
			title("Show all Quartz tasks");
			FunctionV1<Set<JobKey>> printer = jobs -> {
				String rp = StringWalkBuilder.<JobKey>of(jk -> jk.toString()).ol().buildSbAll(jobs).toString();
				ZKME.textReadonly("All founded Job's", rp);
			};
			addEventListener(e -> {
				FunctionV1<Boolean> f = r -> {
					Set<JobKey> allJobKeys;
					if (r == null) {
						return;
					} else if (r) {
						allJobKeys = QzApiEE.getAllJobKeys();
					} else {
						allJobKeys = QzECS.findAllJobKeys(node);
					}
					printer.apply(allJobKeys);
				};
				String msg = X.f("Show mode -> ALL (YES) | Single (NO) <- (%s/%s) ?", X.sizeOf(QzApiEE.getAllJobKeys()), X.sizeOf(QzECS.findAllJobKeys(node)));
				ZKI_Quest.showMessageBoxYNC_ofLevel("Show quartz task's", msg, f, Messagebox.QUESTION);

				//
//					Set<JobKey> allJobKeys = QzApiEE.getAllJobKeys();

			});
		}
	}
}
