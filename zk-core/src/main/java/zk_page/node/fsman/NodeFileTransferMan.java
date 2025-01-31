package zk_page.node.fsman;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpc.log.L;
import mpc.ui.UColorTheme;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import mpu.str.STR;
import org.jetbrains.annotations.Nullable;
import org.zkoss.zul.Window;
import zk_form.notify.ZKI;
import zk_notes.AppNotes;
import zk_notes.control.NodeFactory;
import zk_notes.control.NotesSpace;
import zk_os.AFC;
import zk_os.AFCC;
import zk_os.db.net.WebUsr;
import zk_os.sec.Sec;
import zk_page.ZKR;
import zk_page.core.SpVM;
import zk_page.node.NodeDir;
import zk_page.node_state.FileState;
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

	/// /		ZKL.alert("Move %s to %s >> %s  >> %s ", pagename, dstSd3, srcPage, rpaSd3Page);
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
		NodeDir n2 = node.cloneWithItem(newName);
		if (UFS.exist(n2.getPathFc())) {
			throw new FIllegalStateException("Target node '%s' already exist", newName);
		}
		moveItemNote(n1, n2);
	}

	public static void clearPageFromEmptyNotes(Pare<String, String> page) {
		List<Path> formPaths = AFC.DIR_FORMS_LS_CLEAN(page.key(), page.val());
		for (Path formPath : formPaths) {
			FormState formState = FormState.ofFormDir(page, formPath);
			if (!formState.emptyData()) {
				continue;
			}
			List<Path> files = formState.fLs(EFT.DIR);
			if (X.notEmpty(files)) {
				continue;
			}
			files = formState.fLs(EFT.FILE);
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
			deleteItem(NodeDir.ofNodeName(formState.formName(), page));
			L.info("Deleted clearPageFromEmptyNotes:" + formState.pathFc());
		}
		ZKR.restartPage();
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
		return moveItemSd3(nodeDir, nodeDir.cloneWithSd3(sd3));
	}

	public static NodeDir moveItemSd3(NodeDir nodeDir, NodeDir nodeDst) {

		//
		//.forms

		Path _moved1 = null;
		{
			Path src_parent = nodeDir.getPathFc();
			Path dst_forms = nodeDst.getPathFc();
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
			Path src_parent = nodeDir.getPathFc();
			Path dst_forms = nodeDst.getPathFc();
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
			Path src_parent = nodeItem.getPathFc();
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
	public static Object addNewRandomForm(Pare sdn, NodeFileTransferMan.AddNewForm.OptsAdd... opts) {
		Path image = AppNotes.getRpaForms_BlankDir(sdn, "note-", 3);
		return addNewFormAndOpenUX("note-" + STR.randAlpha(3), "", opts);
	}

	public static Object addNewFormAndOpenUX(String newNotesName, String content, AddNewForm.OptsAdd... opts) {
		IT.isFilename(newNotesName);
		Path newNotesPath = AppNotes.getPathOfFormNote_PPI(newNotesName);
		if (UFS.exist(newNotesPath)) {
			ZKI.alert("Form '%s' already exist", newNotesName);
		} else {
			return AddNewForm.addNewFormAndOpen(newNotesName, content, opts);
		}
		return null;
	}

	public static class AddNewForm {

		@Data
		public static class OptsAdd {
			public static OptsAdd DEF = new OptsAdd();
			public boolean doubleView = false;
			public boolean wysiwygView = false;
			public boolean httpCallForm = false;
			public boolean isKafkaCallForm = false;
			public boolean isSqlCallForm = false;
		}

		public static Window addNewFormAndOpen(String newNotesName, String data, OptsAdd... opts) {

			OptsAdd opts0 = ARG.toDefOr(OptsAdd.DEF, opts);

			String content = X.toString(data, "" + RANDOM.UUID(3));
			FormState props = FormState.ofFormName(newNotesName, SpVM.get().ppi().sdn(), content, true);

			props.update("top", RANDOM.RANGE(260, 520) + "px");
			props.update("left", RANDOM.RANGE(260, 520) + "px");

			props.update("width", RANDOM.RANGE(260, 520) + "px");
			props.update("height", RANDOM.RANGE(260, 520) + "px");

			props.update("height", RANDOM.RANGE(260, 520) + "px");
			//		props.updatePropSingle(BG, RANDOM.RANGE(260, 520) + "px");

			props.update(FormState.OPEN, true);

			{//UPDATE VIEW
				NodeDir.NVT viewType = NodeDir.NVT.TEXT;
				int size = 1;
				if (opts0.isWysiwygView()) {
					viewType = NodeDir.NVT.WYSIWYG;
				} else if (opts0.isHttpCallForm()) {
					if (props.emptyData()) {
						props.writeFcData("GET http://github.com\n--#header comment\n--#Authorization:Bearer");
					}
				} else if (opts0.isDoubleView()) {
					if (props.emptyData()) {
						size = 2;
					}
				} else if (opts0.isKafkaCallForm()) {
					if (props.emptyData()) {
//						size = 3;
						props.writeFcData("KPUT http://localhost:9092/\n--#header comment\n--topic:topicName\n--key:msgkey\nmsgvalue");
					}
				} else if (opts0.isSqlCallForm()) {
					if (props.emptyData()) {
//						size = 3;
						props.writeFcData("jdbc:postgresql://localhost:5432/schema_name?currentSchema=cur_schema&searchpath=cur_schema" + //
								"\n--login:login" +  //
								"\n--password:password" + //
								"\nSELECT * FROM pg_catalog.pg_tables;");
					}
				}
				if (size != 1) {
					props.update(FormState.PK_SIZE, size);
				}
				props.update(FormState.PK_VIEW, viewType);
			}

			WebUsr user = Sec.getUser();
			props.update(SecFileState.PK_USER, user.getAliasOrLogin());
			props.update(SecFileState.SECE, "");
			props.update(SecFileState.SECV, "");

			props.update(FormState.BG_COLOR, RANDOM.ARRAY_ITEM(UColorTheme.WHITE));

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

}
