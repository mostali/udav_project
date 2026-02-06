package zk_notes.fsman;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpc.log.L;
import mpc.num.UNum;
import mpe.core.P;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import mpu.str.STR;
import mpu.str.TKN;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.zkoss.zul.Window;
import zk_form.notify.ZKI;
import zk_notes.node_state.AppStatePath;
import zk_notes.AppNotesProps;
import zk_notes.factory.NFOpen;
import zk_notes.control.NotesSpace;
import zk_notes.node.core.NVT;
import zk_notes.node_srv.NodeEvalType;
import zk_notes.node_state.AppStateFactory;
import zk_os.coms.AFC;
import zk_os.coms.AFCC;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;
import zk_os.sec.Sec;
import zk_os.sec.SecApp;
import zk_page.ZKR;
import zk_page.core.SpVM;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FileState;
import zk_notes.node_state.ObjState;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
public class NodeFileTransferMan implements ITransportMan {
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

	@Override
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

	@Override
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

	//
	//
	// ADD NEW FORM

	@Nullable
	public static Pare<NodeDir, Window> addNewRandomForm(Pare sdn, NodeFileTransferMan.AddNewForm.OptsAdd... opts) {
		Path image = AFCC.getFormDirBlank(sdn, NEW_NOTE_PFX, 3);
		return addNewFormAndOpenUX(NEW_NOTE_PFX + STR.randAlpha(3), "", opts);
	}

	public static Pare<NodeDir, Window> addNewFormAndOpenUX(String newNotesName, String content, AddNewForm.OptsAdd... opts) {
		return addNewFormAndOpenUX(null, newNotesName, content, opts);

	}

	public static Pare<NodeDir, Window> addNewFormAndOpenUX(Pare<String, String> sdnOrNullIfCurrent, String newNotesName, String content, AddNewForm.OptsAdd... opts) {
		IT.isFilename(newNotesName);
		Path newNotesPath = sdnOrNullIfCurrent != null ? AppStatePath.getFormDataPath(sdnOrNullIfCurrent, newNotesName) : AppStatePath.getFormDataPath_PPI(newNotesName);
		if (UFS.exist(newNotesPath)) {
			ZKI.alert("Form '%s' already exist", newNotesName);
		} else {
			return AddNewForm.addNewFormAndOpen(sdnOrNullIfCurrent, newNotesName, content, opts);
		}
		return null;
	}

	public static class AddNewForm {

		@Data
		public static class OptsAdd {

//			public static OptsAdd DEF = new OptsAdd();

			public static OptsAdd newOpts() {
				return new OptsAdd();
			}

			public boolean doubleView = false;
			private int noteSize = 1;
			public boolean wysiwygView = false;
			public boolean isPrettyCodeView = false;
			public boolean httpCallForm = false;
			public boolean isKafkaCallForm = false;
			public boolean isSqlCallForm = false;

			private NodeEvalType nodeEvalType;

			private NVT nodeViewType;

			public boolean isAllowedAutoPfx() {
				return httpCallForm || isKafkaCallForm || isNVT(NVT.CODE) || isSqlCallForm || nodeEvalType != NodeEvalType.NODE;
			}

			public boolean isNVT(NVT nvt) {
				return nodeViewType != null && nodeViewType == nvt;
			}
		}

		public static Pare<NodeDir, Window> addNewFormAndOpen(String newNotesName, String data, OptsAdd... opts) {
			Pare<String, String> sdn = SpVM.get().ppi().sdnAny();
			return addNewFormAndOpen(sdn, newNotesName, data, opts);
		}

		public static Pare<NodeDir, Window> addNewFormAndOpen(Pare<String, String> sdn, String newNotesName, String data, OptsAdd... opts) {

			sdn = sdn == null ? Sdn.get() : sdn;

			OptsAdd opts0 = ARG.toDefOr(OptsAdd.newOpts(), opts);

			Function<String, String> pfxName = (name) -> {
				if (name.startsWith(NEW_NOTE_PFX)) {
					String last = TKN.lastGreedy(name, "-");
					return opts0.getNodeEvalType() == null ? name : opts0.getNodeEvalType().shortName() + "-" + last;
				}
				return name;
			};

			if (opts0.isAllowedAutoPfx()) {
				newNotesName = pfxName.apply(newNotesName);
			}

			String content = X.toStringNN(data, "" + RANDOM.uuid(3));

			ObjState props = AppStateFactory.ofFormName_WithContent(sdn, newNotesName, content, true);

			props.set("top", UNum.round10(RANDOM.range(260, 520)) + "px");
			props.set("left", UNum.round10(RANDOM.range(260, 520)) + "px");

			props.set("width", UNum.round10(RANDOM.range(500, 600)) + "px");
			props.set("height", UNum.round10(RANDOM.range(400, 500)) + "px");


			//			props.set("height", RANDOM.RANGE(260, 520) + "px");
			//		props.updatePropSingle(BG, RANDOM.RANGE(260, 520) + "px");

			props.set(ObjState.OPEN, true);

			NVT viewType = NVT.TEXT;
			{//UPDATE VIEW

				if (opts0.nodeViewType != null) {
					viewType = opts0.nodeViewType;
				} else if (opts0.getNodeEvalType() != null) {
					NodeEvalType nodeEvalType = opts0.getNodeEvalType();
					String defResData = nodeEvalType.loadDefResData(true, null);
					if (defResData == null) {
						P.warnBig(X.f("Need impl for com '%s'", nodeEvalType));
					} else {

						//
						props.set(ObjState.BG_COLOR, RANDOM.array_item(nodeEvalType.toColor()));
						props.writeFcData(defResData);

					}
				}
				if (opts0.getNoteSize() > 1) {
					props.set(ObjState.PK_SIZE, opts0.getNoteSize());
				}
				props.set(ObjState.PK_VIEW, viewType);
			}

			WebUsr user = Sec.getUser();
			props.set(SecApp.USER, user.getAliasOrLogin());
			props.set(SecApp.SECE, "");
			props.set(SecApp.SECV, AppNotesProps.APR_USE_PUBLIC_MODE.getValueOrDefault() ? SecApp.SECFORALL : "");


			NotesSpace.rerenderFirst();

			{//try open new
				NodeDir nodeDir = NodeDir.ofNodeName(SpVM.get().sdn(), newNotesName);
				Window window = NFOpen.openFormRequired(nodeDir);

				//		NotesSpace.f
				//			Window firstWindow = ZKC.getFirstWindow();
				//			Pare<NodeDir, Component> nodeDirComponentPare = nodeDir.buildSingleCom(firstWindow);
				//			if (nodeDirComponentPare.val() instanceof NodeLn) {
				//				((NodeLn) nodeDirComponentPare.val()).checkAndOpenIfStateOpened(false);
				//			}

				return Pare.of(nodeDir, window);
			}
		}
	}

}
