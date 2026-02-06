package zk_page.index;

import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpc.net.query.QueryUrl;
import mpc.time.QDT;
import mpu.IT;
import mpu.core.ARRi;
import mpu.core.QDate;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Window;
import mpe.call_msg.core.NodeID;
import utl_rest.StatusException;
import zk_com.core.IZWin;
import zk_form.head.StdHeadLib;
import zk_form.notify.ZKI;
import zk_notes.control.NotesPSP;
import zk_notes.control.NotesSpace;
import zk_notes.factory.NFCreate;
import zk_notes.factory.NodeCom;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVT;
import zk_notes.node.core.NXT;
import zk_page.core.SpVM;
import zklogapp.logview.LogFileView;

import java.nio.file.Path;

public class IndexItemNPSP extends IndexRootNPSP {

	private final NodeID nodeID;

	public IndexItemNPSP(Window window, NodeID nodeID) {
		super(window);
		this.nodeID = nodeID;
	}

	@Override
	public void buildPage() {

		NotesPSP.initStyleWindowDefault(window);

		NotesSpace notesSpace = NotesSpace.initOnPage(window, true);

		HtmlBasedComponent parent = notesSpace == null ? window : notesSpace;

		NodeDir nodeDir = NodeDir.ofNodeId(nodeID);

		QueryUrl query = SpVM.get().getQuery();
		NVT nvt = query.getFirstAs(NVT.KEY, NVT.class, null);
		if (nvt != null) {
			StdHeadLib.initAndAddRsrcToPage(nvt);
			IZWin form = NFCreate.createForm(nodeDir, nvt);
			form._showInWindowEmbed(window);
		} else {
			NXT nxt = query.getFirstAs(NXT.KEY, NXT.class, null);
			if (nxt != null) {
				switch (nxt) {
					case LOG:
						Path firstLog = ARRi.first(EXT.LOG.lsAll(nodeDir.getSelfDir()), null);
//						IT.NN(firstLog, "file with *.log data not found");
						if (firstLog == null) {
							ZKI.alert("file with type *.log not found");
						} else {
							String f = query.getFirstAsStr("f", null);
							LogFileView.openSingly(firstLog.toString(), f);
						}
						break;
					default:
						throw new WhatIsTypeException(nxt);
				}
			}

		}

		NodeCom nodeCom = NodeCom.of(nodeDir);

		if (!nodeDir.existNode(false)) {
			throw StatusException.C400("Not found");
		}
		nodeCom.buildAndAppendChildIn(parent);

	}

}
