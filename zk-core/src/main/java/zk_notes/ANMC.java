package zk_notes;

import mpc.str.sym.SYMJ;
import mpe.core.ERR;
import mpe.http.HttpCallMsg;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.UST;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Window;
import zk_com.base.Ln;
import zk_com.base.Xml;
import zk_com.base_ctr.Span0;
import zk_com.editable.EditableValue;
import zk_com.win.Win0;
import zk_form.notify.ZKI;
import zk_os.sec.Sec;
import zk_page.ZKR;
import zk_page.node.NodeDir;
import zk_com.core.IZWin;
import zk_page.node.fsman.NodeFileTransferMan;
import zklogapp.header.BottomHistoryPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//AppNotesMenuCaption
public class ANMC {

	public static void applyWinTbxCap(Pare<String, String> sd3pn, IZWin izWin, Window seWinNote, NodeDir node) {

		List<String> nodeData = node.state().readFcDataAsLines(ARR.EMPTY_LIST);

		List<String> urls = nodeData.stream().filter(u -> UST.URL(u, null) != null).collect(Collectors.toList());
		ArrayList links = new ArrayList();
		for (int i = 0; i < urls.size(); i++) {
			if (i == 3) {
				break;
			}
			Ln caption = Ln.ofEmojBlank(urls.get(i), i == 0 ? "" : (i + 1) + "");
			if (i > 0) {
				links.add(Xml.NBSP());
			}
			links.add(caption);
		}

		if (Sec.isEditorAdminOwner()) {
			Caption caption = Win0.getCaptionOrCreate(seWinNote);
			caption.getChildren().clear();
			EditableValue editableValue = new EditableValue(node.nodeName()) {
				@Override
				protected void onUpdatePrimaryText(String value) {
					super.onUpdatePrimaryText(value);
					NodeFileTransferMan.rename(node, value);
					ZKR.restartPage();
				}
			};
			links.add(0, editableValue);
			caption.appendChild(Span0.of(links));
			izWin._caption(caption);
		}

		if (Sec.isEditorAdminOwner()) {

			Caption caption = Win0.getCaptionOrCreate(seWinNote);

			//do validation by one first line of msg
			String line0 = node.state().nodeLine(0, null);
			if (line0 != null) {
				HttpCallMsg httpCallMsg = HttpCallMsg.of(line0, true);
				if (httpCallMsg != null && !httpCallMsg.hasErrors()) {
					String fullVal = node.state().nodeValue();
					if (!line0.equals(fullVal)) {
						//do validation with full val
						httpCallMsg = HttpCallMsg.of(fullVal);
					}
					caption.appendChild(new PlayLn(node));
				}
			}
		}
	}

	public static class PlayLn extends Ln {

		public PlayLn(NodeDir node) {
			super(SYMJ.JET);
			title("Send http call");
			addEventListener(e -> {
				HttpCallMsg httpCallMsg;
				try {
					httpCallMsg = HttpCallMsg.of(node.state().nodeValue());
					String rsp = httpCallMsg.sendHttpCall100_or_custom400_404(true);
					BottomHistoryPanel.addItemAsData(rsp);
//					ZKI.infoEditorBw(rsp);
				} catch (Throwable ex) {
					L.error("Call error on node (mb data is change on moment send?):" + node, ex);
					ZKI.infoEditorBw(ERR.getStackTrace(ex));
				}

			});
		}
	}

}
