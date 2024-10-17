package zk_notes;

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
import zk_os.sec.Sec;
import zk_page.ZKR;
import zk_page.node.NodeDir;
import zk_com.core.IZWin;
import zk_page.node.fsman.NodeFileTransferMan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//AppNotesMenuCaption
public class ANMC {

	public static void applyWinTbxCap(Pare<String, String> sd3pn, IZWin izWin, Window seWinNote, NodeDir node) {

//		boolean isEditView = formIdg.formState().nodeViewType(null) == NodeDir.NVT.SE;
//		XulElement captionLnViewEditMode = new Ln(isEditView ? ANI.VIEW_MODE : ANI.EDIT_MODE).onCLICK(e -> {
//			formIdg.formState().updateProp_NVT(!isEditView ? NodeDir.NVT.SE : NodeDir.NVT.XML);
//			openFormSingly(formname, closeAction, isEditView ? SeTbxWin.class : Xml.class, showFilename);
//		});

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
//		Caption caption = new Caption();
//		caption.appendChild(Span0.of(links));
//		izWin._caption(caption);
//		izWin._caption(Span0.of(links));

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

	}

}
