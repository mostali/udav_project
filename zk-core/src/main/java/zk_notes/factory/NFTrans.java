package zk_notes.factory;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpc.log.L;
import mpu.IT;
import mpu.X;
import mpu.core.RW;
import mpu.pare.Pare;
import org.apache.commons.io.FileUtils;
import zk_form.notify.ZKI;
import zk_notes.node_state.AppStatePath;
import zk_notes.node_state.AppStateFactory;
import zk_os.coms.AFC;
import zk_os.coms.AFCC;
import zk_os.core.Sdn;
import zk_page.ZKR;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FileState;
import zk_notes.node_state.ObjState;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class NFTrans {//implements ITransportMan
	public static final String NEW_NOTE_PFX = "note-";
	final NodeDir node;

//	public static void movePageOnlyName(String sd3Src, String pagenameSrc, String pagenameDst) {
//		IT.isNotEq(pagenameSrc, pagenameDst, "except difference page's");
//		Path srcPage = AFC.getRpaPlane(sd3Src, pagenameSrc);
//		Path rpaSd3Page = AFC.getRpaPlane(sd3Src, pagenameDst);
//		IT.isFileNotExist(rpaSd3Page, "Dst exist*");
//		IT.isDirNotExist(rpaSd3Page, "Dst exist");
//		Path dstParent = rpaSd3Page.getParent();
//		UFS_BASE.MV.moveIn(srcPage, dstParent);

	/// /		ZKL.alert("Move %s to %s >> %s  >> %s ", pagename, dstSd3, srcPage, rpaSd3Page);
//	}
	public static Sdn movePageToSd3(Pare<String, String> sdn, String dstSd3) {
		return movePageToSd3(sdn.key(), sdn.val(), dstSd3);
	}

	public static Sdn movePageToSd3(String sd3Src, String pagename, String dstSd3) {
		IT.isNotEq(sd3Src, dstSd3, "except difference plane's");
		Path srcPage = AFC.PAGES.getDir(sd3Src, pagename);
		Path rpaSd3Page = AFC.PAGES.getDir(dstSd3, pagename);
		IT.isFileNotExist(rpaSd3Page, "Dst exist*");
		IT.isDirNotExist(rpaSd3Page, "Dst exist");
		Path dstParent = rpaSd3Page.getParent();
		UFS.MV.moveIn(srcPage, dstParent);
		return Sdn.of(dstSd3, pagename);

	}

	@SneakyThrows
	public static void movePlane(String srcSd3, String dstSd3) {
		Path src = AFC.PLANES.getPlaneDir(srcSd3);
		Path dst = AFC.PLANES.getPlaneDir(dstSd3);
		IT.isDirOrFileNotExist(dst, "target plane '%s' already exist", dstSd3);
		Files.move(src, dst);
	}

	public static void rename(NodeDir node, String newName, boolean throwErrors) {
		NodeDir n1 = node;
		NodeDir n2 = node.cloneWithItem(newName);
		if (UFS.exist(n2.getPathFc())) {
			if (throwErrors) {
				throw new FIllegalStateException("Target node '%s' already exist", newName);
			} else {
				return;
			}
		}
		moveItemNote(n1, n2);
	}

	public static void clearPageFromEmptyNotes(Pare<String, String> page) {
		Set<Path> formPaths = AFC.FORMS.DIR_FORMS_LS_CLEAN(page.key(), page.val());
		for (Path formPath : formPaths) {
			ObjState formState = AppStateFactory.ofFormDir_orCreate(page, formPath);
			if (!formState.emptyData()) {
				continue;
			}
			List<Path> files = formState.dLs(EFT.DIR);
			if (X.notEmpty(files)) {
				continue;
			}
			files = formState.dLs(EFT.FILE);
			switch (files.size()) {
				case 0:
					continue;
				case 1:
					continue;//need
				case 2:

					String fileNameDefault = AFC.toFileName_Default(EXT.PROPS);
					boolean is1 = UF.fn(files.get(0)).equals(fileNameDefault) || UF.fn(files.get(1)).equals(fileNameDefault);

					String fnProps = fileNameDefault + AFCC.PROPS_FILE_EXT;
					boolean is2 = UF.fn(files.get(0)).equals(fnProps) || UF.fn(files.get(1)).equals(fnProps);

					if (is1 && is2) {
						break; //delete
					} else {
						continue;//need
					}

				default:
					continue;//need

			}

			Path file0 = files.get(0);

			Path path2 = FileState.toPathPropsFromPathFc(file0, false);
			boolean isDefaultNoteProps = file0.equals(path2);
			if (!isDefaultNoteProps) {
				continue;
			}
			deleteItem(NodeDir.ofNodeName(page, formState.objName()));
			L.info("Deleted clearPageFromEmptyNotes:" + formState.pathFc());
		}
		ZKR.restartPage();
	}

	public static class COPY {

		public static boolean doCopyItem(String newNotesName, NodeDir nodeDir) {

			ObjState formState = nodeDir.state();
			ObjState formStateCom = nodeDir.stateCom();

			Path newNotesFormPath = AppStatePath.getFormDataPathCurrent(newNotesName);
			if (UFS.exist(newNotesFormPath)) {
				ZKI.alert("Node FORM '%s' already exist", newNotesName);
				return false;
			}
			Path newNotesComPath = AppStatePath.getComPropsPathCurrent(newNotesName);
			if (UFS.exist(newNotesComPath)) {
				ZKI.alert("Node LINK '%s' already exist", newNotesName);
				return false;
			}
			//				UFS_BASE.COPY.copyDirectory(nodeDirPath, newNotesFormPath.getParent());


			Path formDir = formState.toPathDir();
			if (UFS.existDir(formDir)) {
				UFS.COPY.copyDirectory(formDir, newNotesFormPath.getParent());
			}

			Path stateComDir = formStateCom.toPathDir();
			if (UFS.existDir(stateComDir)) {
				UFS.COPY.copyDirectory(stateComDir, newNotesComPath.getParent());
			}
			return true;
		}
	}

	public NodeDir moveItemToSd3(String sd3) {
		return moveItemNoteToSd3(node, sd3);
	}


	//
	//
	//

	public static NodeDir moveItemNoteToSd3(NodeDir nodeDir, String sd3) {
		return moveItemNote(nodeDir, nodeDir.cloneWithSd3(sd3));
	}

	public static NodeDir moveItemNoteToPage(NodeDir nodeDir, String pagename) {
		return moveItemNote(nodeDir, nodeDir.cloneWithPage(pagename));
	}

	public static NodeDir moveItemNote(NodeDir srcNode, NodeDir dstNode) {
		{ //.forms
			if (UFS.existFile(srcNode.getPath_FormFc_Data())) {
				Path src = srcNode.getSelfDir();
				Path dst = dstNode.getSelfDir();
				Path _moved = UFS.MV.move(src, dst, true);
			}
		}
		{//.coms
			if (UFS.existFile(srcNode.getPath_ComFc())) {
				Path src = srcNode.getPath_ComFc().getParent();
				Path dst = dstNode.getPath_ComFc().getParent();
				Path _moved = UFS.MV.move(src, dst, true);
			}
		}
		{
			checkEmptyDir_Sd3_Page_andRemove(srcNode);
		}
		return dstNode;
	}

	public static void checkEmptyDir_Sd3_Page_andRemove(NodeDir srcNode) {
		checkEmptyDir_Sd3_Page_andRemove(AFCC.getPageOfNode(srcNode));
	}

	public static void checkEmptyDir_Sd3_Page_andRemove(Path pageDir) {
		if (UFS.exist(pageDir) && UFS.isDirWoContent(pageDir, true)) {//page
			if (FileUtils.deleteQuietly(pageDir.toFile())) {
				L.info("Deleted blank page directory {}", pageDir);
				checkEmptyDir_Sd3_andRemove(pageDir);
			}
		}
	}

	public static void checkEmptyDir_Sd3_andRemove(Path pageDir) {
		if (UFS.exist(pageDir) && UFS.isDirWoContent(pageDir.getParent(), true)) {
			if (FileUtils.deleteQuietly(pageDir.getParent().toFile())) {
				L.info("Deleted blank plane directory {}", pageDir.getParent().toFile());
			}
		}
	}


	//
	//
	// DELETE

	public NodeDir deleteItem() {
		return deleteItem(node);
	}

	public static NodeDir deleteItem(NodeDir nodeItem) {
		{
			Path src_parent = nodeItem.getPathFc();
			UFS.RM.deleteDir(src_parent.getParent());
		}

		Path srcCom;
//		Path srcCom = nodeItem.getPath_ComFc(EXT.PROPS);
//		if (UFS.existFile(srcCom)) {
//			UFS.RM.deleteDir(srcCom.getParent());
//		}

		srcCom = nodeItem.getPath_ComFc();
		if (UFS.existFile(srcCom)) {
			UFS.RM.deleteDir(srcCom.getParent());
		}

		if (L.isInfoEnabled()) {
			L.info("Item deleted:" + nodeItem.nodeName());
		}

		checkEmptyDir_Sd3_Page_andRemove(nodeItem);

		return nodeItem;
	}

	public static void deletePage(Pare<String, String> sdn) {
		deletePage(AFC.PAGES.getDir(sdn));
	}

	public static void deletePage(Path pageDir) {
		RW.deleteDir(pageDir, true);
		checkEmptyDir_Sd3_andRemove(pageDir);
	}

	public static void deleteSd3(Path pageSd3) {
		RW.deleteDir(pageSd3, true);
	}


}
