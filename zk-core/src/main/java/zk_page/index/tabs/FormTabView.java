package zk_page.index.tabs;

import lombok.RequiredArgsConstructor;
import mpc.fs.fd.EFT;
import mpc.str.sym.FD_ICON;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Window;
import zk_com.base.Lb;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.base_ctr.Span0;
import zk_com.core.IZCom;
import zk_com.ext.Ddl;
import zk_com.tabs.Tab0;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Sec;
import zk_notes.events.AppEventsFD;
import zk_notes.factory.NFCreate;
import zk_notes.factory.NodeCom;
import zk_notes.node.core.NVM;
import zk_notes.node.core.NVT;
import zk_page.ZKS;
import zk_page.ZKSession;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class FormTabView extends Div0 {

	final NodeDir nodeDir;
	//	final NodeDir.NVT nvt;
	final Tab0 tab0;

	public FormTabView(Tab0 tab0, NodeDir nodeDir, Component... coms) {
		super(coms);
		this.nodeDir = nodeDir;
		this.tab0 = tab0;
	}

	@Override
	protected void init() {
		super.init();

		NVT sessionNVT = getSA_NVT(nodeDir, null);

		if (sessionNVT == null) {
			NVT nvtInState = (NVT) nodeDir.state().getAs_STATE(NVT.class, null);
			if (nvtInState != null) {
				sessionNVT = nvtInState;
				setSA_NVT(nodeDir, sessionNVT);
			} else {
				setSA_NVT(nodeDir, sessionNVT = nodeDir.nvt(NVT.TEXT));
			}
		}

		NVT nvt = sessionNVT;

		NodeDirViewFiles dirViewCom = new NodeDirViewFiles(nodeDir, tab0);

		IZCom com;

		NVM nvm = nodeDir.nvm_first(null);

		if (nvm != null) {

			com = NFCreate.createForm_MEDIA(nodeDir, nvm);

			appendChilds(dirViewCom, (Component) com);

			ZKS.HEIGHT_MIN((HtmlBasedComponent) com, 1600);
			ZKS.WIDTH_MIN((HtmlBasedComponent) com, 1600);

		} else {

			NodeCom nodeWin = NodeCom.of(nodeDir);

			if (nvt.isWindowMode()) {
				nodeWin = nodeWin.mode(Window.Mode.EMBEDDED);
			}

			nodeWin.nvt(nvt).buildAndAppendChildIn(this);

			getChildren().add(0, dirViewCom);

			if (nvt.isWindowMode()) {
				setAutoHeiht(nodeWin);
			}

		}


	}

	private void setAutoHeiht(NodeCom nodeWin) {
		HtmlBasedComponent com = (HtmlBasedComponent) nodeWin.comDataText();
		setAutoHeiht(nodeWin.nodeDir, com);
	}

	private void setAutoHeiht(NodeDir nodeDir, HtmlBasedComponent com) {

		ObjState stateCom = nodeDir.state().stateCom();
		String minHeightComProp = com.getHeight();
		String minHeightComVflex = com.getVflex();
		String minHeight = stateCom.get("min-height", null);
		String height = stateCom.get("height", null);
		String minHeightCom = ZKS.getStyleAttrValue(com, "min-height", null);
		String heightCom = ZKS.getStyleAttrValue(com, "height", null);
		if (!X.emptyAll(minHeight, height, minHeightCom, heightCom)) {
			L.info("Auto height found value in com [ cH:{} , cV:{} , cF_mh:{} , cF_h:{}, cS_mh:{} , cS_h:{} ]", minHeightComProp, minHeightComVflex, minHeight, height, minHeightCom, heightCom);
			return;
		}
		String data = nodeDir.fCat("");
		long length = data.length();
		if (length < 2000) {
			ZKS.HEIGHT_MIN((HtmlBasedComponent) com, 300);
		} else if (length < 8000) {
			ZKS.HEIGHT_MIN((HtmlBasedComponent) com, 800);
		} else {
			ZKS.HEIGHT_MIN((HtmlBasedComponent) com, 1600);
		}
	}

	@RequiredArgsConstructor
	static class NodeDirViewFiles extends Div0 {
		public final NodeDir nodeDir;
		public final Tab0 tab0;

		@Override
		protected void init() {
			super.init();

			Lb nodeLb = Lb.ERR(nodeDir.nodeId() + SYMJ.ARROW_RIGHT_SPEC);
			appendChild(nodeLb);

			Ddl<NVT> ddl = new Ddl<NVT>(getSA_NVT(nodeDir)) {
				@Override
				public boolean onHappensClickItem(MouseEvent e, NVT nvt) {
					if (e.getKeys() == 258) { //isWithCtrl
						nodeDir.state().fields().set_VIEW(nvt);
						ZKI.showMsgBottomRightFast_INFO("Updated '%s' view-state '%s'", nodeDir.nodeID(), nvt);
					}
					try {
						//check NVT
						NFCreate.createForm(nodeDir, nvt);
					} catch (Exception ex) {
						ZKI_Sec.alertFileNotFound(ex);
						return false;
					}
					setSA_NVT(nodeDir, nvt);
					Events.postEvent(Events.ON_SELECT, tab0, null); //simulate a click
					return true;
				}

			};
			appendChild(ddl);

			appendChild(Xml.HR());

			List<Path> listDir = nodeDir.dLs(EFT.DIR, ARR.EMPTY_LIST);
			List<Path> listFiles = nodeDir.dLs(EFT.FILE, ARR.EMPTY_LIST);

			Menupopup0 menu = nodeLb.getOrCreateMenupopup((HtmlBasedComponent) getParent());

			AppEventsFD.applyEvent_OPENDIR_VIEW(menu, nodeDir.toPath());
			AppEventsFD.applyEvent_OPENDIR_OS(menu, nodeDir.toPath());
			AppEventsFD.applyEvent_OPENDIR_TERMINAL(menu, nodeDir.toPath());

			for (Path child : listDir) {
				appendChild(new FileOrDirSpan(nodeDir, child));
			}
			for (Path child : listFiles) {
				appendChild(new FileOrDirSpan(nodeDir, child));
			}
			appendChild(Xml.HR());

//			appendChild(Xml.BR());

		}

		@RequiredArgsConstructor
		static class FileOrDirSpan extends Span0 {
			public final NodeDir nodeDir;
			public final Path fileOrDir;

			@Override
			protected void init() {
				super.init();
				String relativePathDirOrFile = nodeDir.toPath().relativize(fileOrDir).toString();
				String symj = FD_ICON.getEmojSymbol(fileOrDir);
				Lb child = new Lb(symj + " " + relativePathDirOrFile);
				child.cursorOnOver();

				switch (EFT.of(fileOrDir)) {
					case FILE:
						AppEventsFD.applyEvent_OPENFILE(child, fileOrDir, Events.ON_DOUBLE_CLICK);
						AppEventsFD.applyEvent_OPEN_IN_CODE(child.getOrCreateMenupopup(FileOrDirSpan.this),null, fileOrDir, Events.ON_DOUBLE_CLICK);
//						child.onDblClick(FileView.getEventShowComInModal(fileOrDir));
						break;
					case DIR:
						AppEventsFD.applyEvent_OPENDIR_VIEW(child, fileOrDir, Events.ON_DOUBLE_CLICK);
//						child.onDblClick(SimpleDirView.getEventOpenSimpleMenu(fileOrDir));
						break;

				}

				appendChild(child);
			}
		}
	}


	private static NVT getSA_NVT(NodeDir nodeDir, NVT... defRq) {
		return ZKSession.getSessionAttrs().getAs(nodeDir.nodeId() + ".nvt", NVT.class, defRq);
	}

	private static void setSA_NVT(NodeDir nodeDir, NVT nvt) {
		ZKSession.getSessionAttrs().putAs(nodeDir.nodeId() + ".nvt", nvt);
	}
}
