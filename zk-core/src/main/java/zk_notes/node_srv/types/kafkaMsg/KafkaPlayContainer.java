package zk_notes.node_srv.types.kafkaMsg;

import mpc.str.sym.SYMJ;
import mpe.call_msg.KafkaCallMsg;
import mpu.SysThreads;
import mpu.X;
import org.zkoss.zk.ui.Component;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.PlayContainer;

import java.util.ArrayList;
import java.util.List;

public class KafkaPlayContainer {

	public static PlayContainer toPlayContainer(PlayContainer.PlayLn playLn) {
		List<Component> l = new ArrayList();
		KafkaCallMsg kafkaCallMsg = KafkaCallMsg.ofQk(playLn.node);
		l.add(playLn);
		l.add(Xml.NBSP());
		if (kafkaCallMsg.type() == KafkaCallMsg.KafkaMethodType.KGET) {
			l.add(new KafkaRmLn(playLn.node));
		}
		return new PlayContainer(l.toArray(new Component[l.size()]));
	}

	public static class KafkaRmLn extends Ln {
		public KafkaRmLn(NodeDir node) {
			super(" " + SYMJ.FAIL_RED_THINK);
			title("Remove all Kafka consumer's");
			addEventListener(e -> {
				List<Thread> threads = SysThreads.getThreads(KafkaECS.getKafkaConsumerThreadName(node), null);
				if (threads == null) {
					ZKI.showMsgBottomRightFast_INFO("Not found thread's '%s'", node.nodeId());
				} else {
					threads.stream().filter(Thread::isAlive).forEach(Thread::interrupt);
					ZKI.showMsgBottomRightFast_INFO("All '%s' thread's of node '%s' was INTERRUPT", X.sizeOf(threads), node.nodeId());
				}
			});
		}
	}
}
