package zk_notes.node_srv.publ_srv.publ.walker;

import lombok.Getter;
import mpc.env.Env;
import mpc.exception.IErrorsCollector;
import mpc.log.L;
import mpe.NT;
import mpe.cmsg.core.StdType;
import mpe.cmsg.std.PublCallMsg;
import mpe.cmsg.ns.NodeID;
import mpe.cmsg.NodeData;
import mpu.X;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.InjectSrv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class NodePbProc implements IErrorsCollector {

	private final @Getter NodeData nodeData;

	@Getter
	final LinkedHashSet<String> nodesSeq0 = new LinkedHashSet<>();

	public NodePbProc(NodeData node) {
		this.nodeData = node;

	}

	public static void main(String[] args) {
		try {

			InjectSrv.initDefaultService();

			Env.setAppName(NT.BEA);
			// Путь к директории ноды
//			String playlistNodePath = "/home/dav/Загрузки";

			// Создаем процессор и обрабатываем плейлист
//			PlaylistNodeProcessor processor = new PlaylistNodeProcessor(NodeID.of("//dueshman"));
			NodeID nodeID = NodeID.of("/ttt/publ0");
			NodeDir nodeDir = NodeDir.ofNodeId(nodeID);
//			NodeDir nodeDir = No.ofNodeId("m//");
//			if(nodeDir.nodeId().nodeName()==null)
//			Path path = nodeDir.toPath();

			NodePbProc processor = new NodePbProc(nodeDir.inject());
			List<PublCallMsg.SingleSrcLine> publist = processor.processPublist();

			// Выводим результаты
//			System.out.println("Processing node: " + processor.getNodePath());
			System.out.println("Found:\n" + publist);
			for (PublCallMsg.SingleSrcLine file : publist) {
				System.out.println("  - " + file);
			}

		} catch (IOException e) {
			System.err.println("Error processing playlist: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public List<PublCallMsg.SingleSrcLine> processPublist() throws IOException {
		List<PublCallMsg.SingleSrcLine> result = new ArrayList<>();

//		PublCallMsg publCallMsg = (PublCallMsg) StdType.newCallMsgNative(nodeData.nodeDir, StdType.PUBL, false);
		PublCallMsg publCallMsg = (PublCallMsg) nodeData.nodeDir.inject().newInstanceCallMsgValid(null);

//		String bodyString = publCallMsg.getBody_STRING();
//		List<String> lines = RW.readLinesCleanTrim(publistPath, null);
		List<String> lines = publCallMsg.getBody_ASLINES();
		if (X.empty(lines)) {
			L.warn("Except publist body from node '{}'", nodeData.nodeDir.toObjID());
			return result;
		}

		for (String line : lines) {
			processLine(line, result);
		}

		List<Throwable> errors = getErrors();
		if (X.notEmpty(errors)) {
			L.error("After processing publist '" + nodeData.nodeDir.toObjID() + "'", getMultiOrSingleErrorOrNull());
		}
		return result;
	}

	/**
	 * Обрабатывает одну строку плейлиста
	 */
	private void processLine(String line0, List<PublCallMsg.SingleSrcLine> result) throws IOException {

		String line = line0.trim();

		if (line.startsWith("#")) {
			return;
		}

		PublCallMsg.SingleSrcLine SingleSrcLine = new PublCallMsg.SingleSrcLine(line0);

		if (!SingleSrcLine.isValid()) {
			L.error("SingleSrcLine has illegal content", SingleSrcLine.getMultiOrSingleErrorOrNull());
			return;
		}

		result.add(SingleSrcLine);

	}

	private List _errors;

	@Override
	public List<Throwable> getErrors() {
		return _errors != null ? _errors : (_errors = new LinkedList());
	}

	//
	//


}
