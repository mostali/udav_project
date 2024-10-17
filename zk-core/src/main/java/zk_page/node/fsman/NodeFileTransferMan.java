package zk_page.node.fsman;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.EXT;
import mpc.log.L;
import mpc.ui.UColorTheme;
import mpu.IT;
import mpu.X;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import mpu.str.STR;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.zkoss.zul.Window;
import zk_form.notify.ZKI;
import zk_notes.AppNotes;
import zk_notes.control.NodeFactory;
import zk_notes.control.NotesSpace;
import zk_os.AFC;
import zk_os.db.net.WebUsr;
import zk_os.sec.Sec;
import zk_page.core.SpVM;
import zk_page.node.NodeDir;
import zk_page.node_state.FormState;
import zk_page.node_state.SecFileState;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class NodeFileTransferMan implements ITransportMan {
	final NodeDir node;

//	public static void movePageOnlyName(String sd3Src, String pagenameSrc, String pagenameDst) {
//		IT.isNotEq(pagenameSrc, pagenameDst, "except difference page's");
//		Path srcPage = AFC.getRpaPlane(sd3Src, pagenameSrc);
//		Path rpaSd3Page = AFC.getRpaPlane(sd3Src, pagenameDst);
//		IT.isFileNotExist(rpaSd3Page, "Dst exist*");
//		IT.isDirNotExist(rpaSd3Page, "Dst exist");
//		Path dstParent = rpaSd3Page.getParent();
//		UFS_BASE.MV.moveIn(srcPage, dstParent);
////		ZKL.alert("Move %s to %s >> %s  >> %s ", pagename, dstSd3, srcPage, rpaSd3Page);
//	}

	public static void movePageToSd3(String sd3Src, String pagename, String dstSd3) {
		IT.isNotEq(sd3Src, dstSd3, "except difference plane's");
		Path srcPage = AFC.getRpaPageDir(sd3Src, pagename);
		Path rpaSd3Page = AFC.getRpaPageDir(dstSd3, pagename);
		IT.isFileNotExist(rpaSd3Page, "Dst exist*");
		IT.isDirNotExist(rpaSd3Page, "Dst exist");
		Path dstParent = rpaSd3Page.getParent();
		UFS_BASE.MV.moveIn(srcPage, dstParent);

//		Path pageStatePath_Src = FormState.ofPageState(Sdn.of(sd3Src, pagename)).pathFc();
//		Path pageStatePath_Dst = FormState.ofPageState(Sdn.of(dstSd3, pagename)).pathFc();
//		if (UFS.existFile(pageStatePath_Src)) {
//			UFS_BASE.MV.moveIn(pageStatePath_Src, pageStatePath_Dst.getParent());
//		}

//		ZKL.alert("Move %s to %s >> %s  >> %s ", pagename, dstSd3, srcPage, rpaSd3Page);
	}

	@SneakyThrows
	public static void movePlane(String srcSd3, String dstSd3) {
		Path src = AFC.getRpaPlaneDir(srcSd3);
		Path dst = AFC.getRpaPlaneDir(dstSd3);
		IT.isDirOrFileNotExist(dst, "target plane '%s' already exist", dstSd3);
		Files.move(src, dst);
	}

	public static void deletePage(Pare<String, String> sdn) {
		RW.deleteDir(AFC.getRpaPageDir(sdn), true);
	}

	public static void rename(NodeDir node, String newName) {
		NodeDir n1 = node;
		NodeDir n2 = node.cloneRenameName(newName);
		if (UFS.exist(n2.getTargetPathDataFc())) {
			throw new FIllegalStateException("Target node '%s' already exist", newName);
		}
		moveItemNote(n1, n2);
	}

	public static void clearPageFromEmptyNotes(Pare<String, String> page) {
		List<Path> formPaths = AFC.DIR_FORMS_LS_CLEAN(page.key(), page.val());
		for (Path formPath : formPaths) {
			FormState formState = FormState.ofFormDir(page, formPath);
			if (X.notEmpty(formState.readFcData(null))) {
				deleteItem(NodeDir.ofNodeName(formState.formName(), page));
				L.info("Deleted clearPageFromEmptyNotes:" + formState.pathFc());
			}
		}
	}

	@Override
	public NodeDir moveItemToSd3(String sd3) {
		return moveItemSd3(node, sd3);
	}

	@Override
	public NodeDir deleteItem() {
		return deleteItem(node);
	}

	//
	//
	//

	public static NodeDir moveItemSd3(NodeDir nodeDir, String sd3) {
		return moveItemSd3(nodeDir, nodeDir.cloneRenameSd3(sd3));
	}

	public static NodeDir moveItemSd3(NodeDir nodeDir, NodeDir nodeDst) {

		//
		//.forms

		Path _moved1 = null;
		{
			Path src_parent = nodeDir.getTargetPathDataFc();
			Path dst_forms = nodeDst.getTargetPathDataFc();
			src_parent = src_parent.getParent();
			dst_forms = dst_forms.getParent().getParent();
			_moved1 = UFS_BASE.MV.moveIn(src_parent, dst_forms, true);
		}

		//
		//.coms

		Path _moved2 = null;
		Path srcCom = nodeDir.toComsPath(EXT.PROPS);
		if (UFS.existFile(srcCom, true)) {
			Path dstCom = nodeDst.toComsPath(EXT.PROPS);
			Path srcComParent = srcCom.getParent();
			Path dstComParent = dstCom.getParent().getParent();
			IT.isFalse(UFS.existFile(dstCom), "exist com entity '%s'", nodeDst.nodeName());
			_moved2 = UFS_BASE.MV.moveIn(srcComParent, dstComParent, true);
		}
		return nodeDst;
	}

	public static NodeDir moveItemNote(NodeDir nodeDir, NodeDir nodeDst) {

		//
		//.forms

		Path _moved1 = null;
		{
			Path src_parent = nodeDir.getTargetPathDataFc();
			Path dst_forms = nodeDst.getTargetPathDataFc();
			src_parent = src_parent.getParent();
			dst_forms = dst_forms.getParent().getParent().resolve(nodeDst.nodeName());
//			dst_forms = dst_forms.getParent().getParent();
//			_moved1 = UFS_BASE.MV.moveIn(src_parent, dst_forms, true);
			_moved1 = UFS_BASE.MV.move(src_parent, dst_forms, true);
		}

		//
		//.coms

		Path _moved2 = null;
		Path srcCom = nodeDir.toComsPath(EXT.PROPS);
		if (UFS.existFile(srcCom, true)) {
			Path dstCom = nodeDst.toComsPath(EXT.PROPS);
			Path srcComParent = srcCom.getParent();
			Path dstComParent = dstCom.getParent().getParent();
			IT.isFalse(UFS.existFile(dstCom), "exist com entity '%s'", nodeDst.nodeName());
			_moved2 = UFS_BASE.MV.moveIn(srcComParent, dstComParent, true);
		}
		return nodeDst;
	}

	public static NodeDir deleteItem(NodeDir nodeItem) {
		{
			Path src_parent = nodeItem.getTargetPathDataFc();
			UFS.RM.deleteDir(src_parent.getParent());
		}

		Path srcCom = nodeItem.toComsPath(EXT.PROPS);
		if (UFS.existFile(srcCom)) {
			UFS.RM.deleteDir(srcCom.getParent());
		}

		srcCom = nodeItem.toComsPath(EXT.JSON);
		if (UFS.existFile(srcCom)) {
			UFS.RM.deleteDir(srcCom.getParent());
		}

		if (L.isInfoEnabled()) {
			L.info("Item deleted:" + nodeItem.nodeName());
		}
		return nodeItem;
	}

	//
	//
	// ADD NEW FORM

	@Nullable
	public static Object addNewRandomForm(Pare sdn) {
		Path image = AppNotes.getRpaForms_BlankDir(sdn, "note-", 3);
		return addNewFormAndOpenUX("note-" + STR.randAlpha(3), "");
	}

	public static Object addNewFormAndOpenUX(String newNotesName, String content) {
		IT.isFilename(newNotesName);
		Path newNotesPath = AppNotes.getPathOfFormNote_PPI(newNotesName);
		if (UFS.exist(newNotesPath)) {
			ZKI.alert("Form '%s' already exist", newNotesName);
		} else {
			return addNewFormAndOpen(newNotesName, content);
		}
		return null;
	}

	public static Window addNewFormAndOpen(String newNotesName, String data) {

		String content = X.toString(data, "" + RANDOM.UUID(3));
		FormState formPropsApply = FormState.ofFormName(newNotesName, SpVM.get().ppi().sdn(), content, true);

		formPropsApply.updatePropSingle("top", RANDOM.RANGE(260, 520) + "px");
		formPropsApply.updatePropSingle("left", RANDOM.RANGE(260, 520) + "px");

		formPropsApply.updatePropSingle("width", RANDOM.RANGE(260, 520) + "px");
		formPropsApply.updatePropSingle("height", RANDOM.RANGE(260, 520) + "px");

		formPropsApply.updatePropSingle("height", RANDOM.RANGE(260, 520) + "px");
//		formPropsApply.updatePropSingle(BG, RANDOM.RANGE(260, 520) + "px");

		formPropsApply.updatePropSingle(FormState.OPEN, true);
		formPropsApply.updatePropSingle(FormState.PK_STATE, NodeDir.NVT.TEXT_WIN);

		WebUsr user = Sec.getUser();
		formPropsApply.updatePropSingle(SecFileState.PK_USER, user.getAliasOrLogin());
		formPropsApply.updatePropSingle(SecFileState.SECE, "");
		formPropsApply.updatePropSingle(SecFileState.SECV, "");

		formPropsApply.updatePropSingle(FormState.BG_COLOR, RANDOM.ARRAY_ITEM(UColorTheme.WHITE));

		NotesSpace.rerenderFirst();

		{//try open new

			NodeDir nodeDir = NodeDir.ofNodeName(newNotesName, SpVM.get().sdn());
			Window window = NodeFactory.openFormIdentitySinglyAsWin0_Opened(nodeDir);

//		NotesSpace.f
//			Window firstWindow = ZKC.getFirstWindow();
//			Pare<NodeDir, Component> nodeDirComponentPare = nodeDir.buildSingleCom(firstWindow);
//			if (nodeDirComponentPare.val() instanceof NodeLn) {
//				((NodeLn) nodeDirComponentPare.val()).checkAndOpenIfStateOpened(false);
//			}

			return window;
		}
	}

}
