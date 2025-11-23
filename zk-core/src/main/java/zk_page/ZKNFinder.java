package zk_page;


import lombok.RequiredArgsConstructor;
import mpc.arr.STREAM;
import mpu.core.ARR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_com.base.XmlHtml;
import zk_com.base.XmlMd;
import zk_com.core.IZState;
import zk_form.dirview.DirViewNode;
import zk_form.dirview.TreeViewNode;
import zk_notes.coms.*;
import zk_notes.factory.NFCreate;
import zk_notes.control.NodeLn;
import zk_notes.node.NodeDir;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class ZKNFinder {

	private static Collection<Class<Component>> comClasses = (Collection) ARR.asAD(
			NodeLn.class
	);
	private static Collection<Class<Component>> formClasses = (Collection) ARR.asAD( //
			NoteTbxm.class, //
			SeNoteTbxm.class, //

			NFCreate.HtmlWinCom.class, //
			XmlMd.class,
			XmlHtml.class,

			TreeViewNode.class,//
			DirViewNode.class,//
			PrettyCodeXml.class,//
			SingleNodeImg.class,//
			SingleNodeVideo.class,//
			SingleNodeAudio.class,//
			SingleNodeAudioMulti.class

	);

	public static class FORMS {
		public static List<Component> findAll() {
			return ZKNFinder.findAllNodeCom(true, false);
		}
	}

	public static class NODELN {
		public static List<Component> findAll() {
			return ZKNFinder.findAllNodeCom(false, false);
		}
	}

	public static List<Component> findAllNodeCom(boolean isForm, boolean unwrapIfInWindow) {
		return findAllNodeCom(null, isForm, unwrapIfInWindow);
	}

	public static List<Component> findAllNodeCom(NodeDir nodeDir, boolean isForm, boolean unwrapIfInWindow) {
		Collection<Class<Component>> findClasses = isForm ? formClasses : comClasses;
		List<Component> finded = ZKCFinderExt.findAll_inPage0(findClasses, true, ARR.EMPTY_LIST);
		finded = STREAM.filterToList(finded, n -> {
			if (isForm) {
				return isNodeFormComponent(n, nodeDir);
			} else {
				return isNodeComComponent(n, nodeDir);
			}
		});
		if (unwrapIfInWindow) {
			Window firstWindow = ZKC.getFirstWindow();
			finded = STREAM.mapToList(finded, c -> (c.getParent() instanceof Window && c.getParent() != firstWindow) ? c.getParent() : c);
		}
		return finded;
	}

	public static boolean isNodeComComponent(Component checkCom, NodeDir nodeDir) {
		boolean isFormType = isFormComType(checkCom, false);
		if (isFormType) {
			if (nodeDir == null || !(checkCom instanceof IZState)) {
				return true;
			}
			return ((IZState) checkCom).getComName().equals(nodeDir.nodeName());
		}
		return false;
	}

	public static boolean isNodeFormComponent(Component checkCom, NodeDir nodeDir) {
		boolean isFormType = isFormComType(checkCom, true);
		if (isFormType) {
			if (nodeDir == null || !(checkCom instanceof IZState)) {
				return true;
			}
			return ((IZState) checkCom).getFormName().equals(nodeDir.nodeName());
		} else if (checkCom instanceof NFCreate.HtmlWinCom) {
			return true;
		}
		return false;
	}

	private static boolean isFormComType(Component checkCom, boolean isForm) {
		return (isForm ? formClasses : comClasses).stream().anyMatch(c -> c.isAssignableFrom(checkCom.getClass()));
	}

}
