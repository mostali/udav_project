package zk_notes.factory;

import lombok.Data;
import lombok.SneakyThrows;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.fs.fd.RES;
import mpe.cmsg.core.INodeType;
import mpe.cmsg.core.StdType;
import mpe.core.P;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import mpu.str.STR;
import mpu.str.TKN;
import org.jetbrains.annotations.Nullable;
import org.zkoss.zul.Window;
import zk_form.notify.ZKI;
import zk_notes.AppNotesProps;
import zk_notes.control.NotesSpace;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVT;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.AppStatePath;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.FormState;
import zk_os.coms.AFCC;
import mpe.img.EColor;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;
import zk_os.sec.Sec;
import zk_os.sec.SecApp;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.util.function.Function;

public class NFNew {

	@SneakyThrows
	private static String loadDefResData(String evalTypeName, boolean new_about, String... defRq) {
		String resName = (new_about ? "/_com/_demo_note_new/" : "/_com/_demo_note_about/") + evalTypeName + ".props";
		return RES.of(StdType.class, resName).cat_(defRq);
	}

	@Nullable
	public static Pare<NodeDir, Window> openRandomINE(Pare sdn, OptsAdd... opts) {
		Path image = AFCC.getFormDirBlank(sdn, NFTrans.NEW_NOTE_PFX, 3);
		return openNewINE_inCurrentSdn(NFTrans.NEW_NOTE_PFX + STR.randAlpha(3), "", opts);
	}

	public static Pare<NodeDir, Window> openNewINE_inCurrentSdn(String newNotesName, String content, OptsAdd... opts) {
		return openNewINE(null, newNotesName, content, opts);

	}

	public static Pare<NodeDir, Window> openNewINE(Pare<String, String> sdn_OrNull, String newNotesName, String content, OptsAdd... opts) {
		IT.isFilename(newNotesName);
		Path newNotesPath = sdn_OrNull != null ? AppStatePath.getFormDataPath(sdn_OrNull, newNotesName) : AppStatePath.getFormDataPath_PPI(newNotesName);
		if (UFS.exist(newNotesPath)) {
			ZKI.alert("Form '%s' already exist", newNotesName);
		} else {
			return openNewRewrite(sdn_OrNull, newNotesName, content, opts);
		}
		return null;
	}

	public static Window openNewWithState(Pare<String, String> sdn, String nodeName, String data, String state) {
		sdn = sdn == null ? Sdn.get() : sdn;
		FormState props = AppStateFactory.ofFormName_WithContent(sdn, nodeName, data, state, true);
		return NFForm.openFormRequired(sdn, nodeName);
	}

	public static Pare<NodeDir, Window> openNewRewrite(Pare<String, String> sdn, String newNotesName, String data, OptsAdd... opts) {

		sdn = sdn == null ? Sdn.get() : sdn;

		OptsAdd opts0 = ARG.toDef(() -> OptsAdd.newOpts(), opts);

		Function<String, String> pfxName = (name) -> {
			if (name.startsWith(NFTrans.NEW_NOTE_PFX)) {
				String last = TKN.lastGreedy(name, "-");
				return opts0.getNodeEvalType() == null ? name : opts0.getNodeEvalType().stdProps().shortName() + "-" + last;
			}
			return name;
		};

		if (opts0.isAllowedAutoPfx()) {
			newNotesName = pfxName.apply(newNotesName);
		}

		String content = X.toStringNN(data, "" + RANDOM.uuid(3));

		FormState props = AppStateFactory.ofFormName_WithContent(sdn, newNotesName, content, true);

		BeType.applyProps(props, opts0);

		props.set(ObjState.OPEN, true);

		NVT viewType = NVT.TEXT;
		{
			//UPDATE VIEW

			if (opts0.nodeViewType != null) {
				viewType = opts0.nodeViewType;
			} else if (opts0.getNodeEvalType() != null) {
				INodeType nodeEvalType = opts0.getNodeEvalType();
				String stdType = nodeEvalType.stdTypeUC();
				String defResData = loadDefResData(stdType.toLowerCase(), true, null);
				if (defResData == null) {
					P.warnBig(X.f("Need impl for com '%s'", stdType));
				} else {

					//
					props.set(ObjState.BG_COLOR, RANDOM.array_item(nodeEvalType.stdProps().toColor()));
					props.writeFcData(defResData);

				}
			}

			opts0.applyProp((FormState) props, BeType.note_size);

			props.set(ObjState.PK_VIEW, viewType);
		}

		WebUsr user = Sec.getUser();

		props.set(SecApp.USER, user.getAliasOrLogin());
		props.set(SecApp.SECE, "");
		props.set(SecApp.SECV, AppNotesProps.APR_USE_PUBLIC_MODE.getValueOrDefault() ? SecApp.SECFORALL : "");


		NotesSpace.rerenderFirst();

		{//try open new
			NodeDir nodeDir = NodeDir.ofNodeName(SpVM.get().sdn(), newNotesName);
			Window window = NFForm.openFormRequired(nodeDir);

			//		NotesSpace.f
			//			Window firstWindow = ZKC.getFirstWindow();
			//			Pare<NodeDir, Component> nodeDirComponentPare = nodeDir.buildSingleCom(firstWindow);
			//			if (nodeDirComponentPare.val() instanceof NodeLn) {
			//				((NodeLn) nodeDirComponentPare.val()).checkAndOpenIfStateOpened(false);
			//			}

			return Pare.of(nodeDir, window);
		}
	}

	@Data
	public static class OptsAdd {

		public static OptsAdd newOpts() {
			return new OptsAdd();
		}

//		public boolean doubleView = false;

		public boolean wysiwygView = false;
		public boolean isPrettyCodeView = false;

		public boolean httpCallForm = false;
		public boolean isKafkaCallForm = false;
		public boolean isSqlCallForm = false;

		public final OptsBe optBe = new OptsBe();

		private INodeType nodeEvalType;

		private NVT nodeViewType;

		public boolean isAllowedAutoPfx() {
			return httpCallForm || isKafkaCallForm || isNVT(NVT.CODE) || isSqlCallForm || (nodeEvalType != null && !StdType.NODE.stdTypeUC().equals(nodeEvalType.stdTypeUC()));
		}

		public boolean isNVT(NVT nvt) {
			return nodeViewType != null && nodeViewType == nvt;
		}


		public boolean applyProp(FormState props, BeType propKey) {

			if (optBe == null || optBe.linkIsVisible == null) {
				return false;
			}

			switch (propKey) {
				case pos:
					if (optBe.pos != null) {
						props.set(propKey.name0, optBe.pos.name());
					}
					return true;
				case bgcolor:
					if (optBe.zkColor != null) {
						props.set(propKey.name0, optBe.zkColor.nextColor());
					}
					return true;
				case link_visible:
					if (optBe.linkIsVisible != null) {
						props.stateCom(true).set(propKey.name0, optBe.linkIsVisible);
					}
					return true;

				case note_size:
					OptsBe optBe = getOptBe();
					if (optBe.getNoteSize() > 1) {
						props.set(propKey.name0, optBe.getNoteSize());
					}
					return true;
				default:
					throw new WhatIsTypeException(propKey);
			}
		}
	}

	@Data
	public static class OptsBe {
		int noteSize = 1;
		Boolean linkIsVisible = null;
		EColor zkColor = null;
		Object[] top_left;
		Object[] width_height;
		ObjState.Position pos;
	}
}