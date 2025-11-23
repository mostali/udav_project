package zk_notes.node_srv.types.quartzMsg;

import com.google.common.eventbus.Subscribe;
import mpe.eventbus.UEventBus;
import mpe.call_msg.QzEvalMsg;
import mpu.IT;
import mpu.pare.Pare3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;

import java.nio.file.Path;

public class QzTaskServiceOld {

	public static final Logger L = LoggerFactory.getLogger(QzTaskServiceOld.class);

	//
	//
	//

	public static void registerEventBus() {
		UEventBus.register(new SaveTbxListener(), SaveTbxListener.BUSEVENT_NOTE_SAVE);
	}

	public static class SaveTbxListener {

		public static final String BUSEVENT_NOTE_SAVE = "NoteSaveEvent";

		@Subscribe
		public static void onChangeTbxEvent(Object event) {
			try {
				onChangeTbxEventImpl(event);
			} catch (Exception ex) {
				L.error("Error onChangeTbxEvent", ex);
				ZKI.alert(ex);
			}
		}

	}

	//
	//
	//

	public static void onChangeTbxEventImpl(Object event) {

		Pare3<NodeDir, Path, String> eventData = IT.isType0(event, Pare3.class);
		NodeDir node = eventData.key();
		Path nodePath = eventData.val();
		String nodeData = eventData.ext();

		if (!QzEvalMsg.isValidKeyFirstLine(nodeData)) {
			return;
		}
		QzEvalMsg qzCallMsg = QzEvalMsg.ofQk(nodeData);
		if (qzCallMsg.hasErrors()) {
			qzCallMsg.getErrors().forEach(e -> L.error("QzCallMsg-FAIL", e));
			ZKI.alert(qzCallMsg.getErrsAsMsg("Errors Quartz Service:", true));
			return;
		}

//		QzEvalService.runAll(node, qzCallMsg);

	}


}
