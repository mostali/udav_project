package zk_notes.node_state;

import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.EXT;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.pare.Pare;
import mpu.str.TKN;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.Window;
import zk_form.notify.ZKI;
import zk_os.AFC;
import zk_os.AFCC;
import zk_page.ZKS;
import zk_notes.AppNotes;
import zk_notes.node.NodeDir;
import zk_page.events.ZKE;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;


public class FormState<P> extends SecFileState<P> {
	public static final String[] TOP_LEFT = {"top", "left"};
	public static final String[] WIDTH_HEIGHT = {"width", "height"};
	public static final String BG_COLOR = "bg-color";
	public static final String OPEN = "open";


	//
	//
	// PlaneState

	public static FormState ofPlaneState_orCreate(String sd3) {
		Path dstStateFile = AFC.PLANES.getRpaPlaneStatePath(sd3);
		return FormState.ofPathComFile_orCreate(Pare.of(sd3), dstStateFile, false);
	}

	//
	//
	//PageState

	public static FormState ofPageState_orCreate(Pare<String, String> sdn) {
		return ofPageState_orCreate(sdn, false);
	}

	public static FormState ofPageState_orCreate(Pare<String, String> sdn, boolean create) {
		Path dstStateFile = AFC.PAGES.getRpaPageStatePath(sdn);
		return ofPageState_orCreate(sdn, dstStateFile, create);
	}

	public static FormState ofPageState_orCreate(Pare<String, String> sdn, Path pageState, boolean create) {
		return FormState.ofPathComFile_orCreate(sdn, pageState, create);
	}

	//
	//Page Com
	public static FormState ofPathPageComFile_OrCreate(Pare<String, String> sdn, String comname, EXT ext, boolean... create) {
		Path comState = AFC.PAGECOMS.getRpaPageComStatePath(sdn.key(), sdn.val(), comname, ext);
		return FormState.ofPathPageComFile_OrCreate(sdn, comState, create);
	}

	//
	//
	//Form

	public static FormState ofFormName(Pare sdn, String nodeName, String content, boolean... create) {
		Path pathOfFormNote = sdn != null ? AppNotes.getPathOfFormNote_SDN(sdn, nodeName) : AppNotes.getPathOfFormNote_PPI(nodeName);
		if (ARG.isDefEqTrue(create)) {
			boolean created = UFS_BASE.MKFILE.createFileIfNotExistWithContentMkdirs(pathOfFormNote, content);
			if (!created) {
				UFS_BASE.RM.deleteDir(pathOfFormNote.getParent());
				IT.state(UFS_BASE.MKFILE.createFileIfNotExistWithContentMkdirs(AppNotes.getPathOfFormNote_PPI(nodeName), content));
			}
		}
		return ofPathFormFile_orCreate(sdn, pathOfFormNote);
	}

	public static FormState ofFormName_OrCreate(Pare sdn, String noteName) {
		Path pathOfFormNotePpi = AppNotes.getPathOfFormNote_SDN(sdn, noteName);
		return ofPathFormFile_orCreate(sdn, pathOfFormNotePpi);
	}

	public static FormState ofFormDirOrCreate(Pare sdn, Path nodeDir) {
		String nodeName = nodeDir.getFileName().toString();
		Path formpathFile = AFC.FORMS.getRpaFormStatePath(ARG.toDef(sdn), nodeName);
		return ofPathFormFile_orCreate(sdn, formpathFile);
	}

	public static FormState ofPathFormFile_orCreate(Pare<String, String> sdn, String formname, EXT ext, boolean... create) {
		Path formState = AFC.FORMS.getRpaFormStatePath(sdn.key(), sdn.val(), formname, ext);
		return FormState.ofPathFormFile_orCreate(sdn, formState, create);
	}

	public static FormState ofPathFormFile_orCreate(Pare sdn, Path pathCom, boolean... create) {
		FormState propApply = new FormState(sdn, pathCom.toString(), true);
		propApply.pathFc = pathCom;
		if (ARG.isDefEqTrue(create)) {
			propApply.getPropsOrCreate();
		}
		return propApply;
	}

	//
	//
	//Com

	public static FormState ofPathComFile_orCreate(Pare<String, String> sdn, String comname, EXT ext, boolean... create) {
		Path comState = AFC.COMS.getRpaComStatePath(sdn.key(), sdn.val(), comname, ext);
		return FormState.ofPathComFile_orCreate(sdn, comState, create);
	}

	public static FormState ofPathPageComFile_OrCreate(Pare sdn, Path pathCom, boolean... create) {
		return ofPathComFile_orCreate(sdn, pathCom, create);
	}

	public static FormState ofPathComFile_orCreate(Pare sdn, Path pathCom, boolean... create) {
		FormState propApply = new FormState(sdn, pathCom.toString(), false);
		propApply.pathFc = pathCom;//transient
		if (ARG.isDefEqTrue(create)) {
			propApply.getPropsOrCreate();
		}
		return propApply;
	}

	public static FormState ofState_OrCreate(Pare<String, String> sdn, String state, String nodeName, FormState... defRq) {

		FormState formState;
		switch (state) {
			case AFCC.DIR_FORMS:
				formState = FormState.ofFormName_OrCreate(sdn, nodeName);
				break;
			case AFCC.DIR_COMS:
				formState = FormState.ofPathComFile_orCreate(sdn, nodeName, EXT.JSON);
				break;
			case AFCC.DIR_PLANES:
				formState = FormState.ofPlaneState_orCreate(sdn.key());
				break;
			case AFCC.DIR_PAGES:
				formState = FormState.ofPageState_orCreate(sdn);
				break;
			default:
				return ARG.toDefThrowMsg(() -> X.f("illegal state for update : %s", state), defRq);

		}
		return formState;
	}

	//
	//
	//

	public String formName() {
		return pathProps().getParent().getFileName().toString();
	}

	public FormState(Pare sdn, String pathComStr, boolean isForm) {
		super(sdn, pathComStr, isForm);
	}

	public <E extends Enum> E stateTypeFrom(Class<E>... types) {
		String propState = get_STATE(null);
		if (propState == null) {
			return null;
		}
		Map<String, Enum> valuesAsMap = ENUM.getValuesAsMap((Class[]) types);
		return (E) valuesAsMap.getOrDefault(propState, null);
	}

	public Fields fields() {
		return new Fields();
	}


	public class Fields {

		public int[] get_TOP_LEFT(int[]... defRq) {
			Integer top = TKN.first(get(CN.TOP, null), ZKS.PX, Integer.class, null);
			Integer left = TKN.first(get(CN.LEFT, null), ZKS.PX, Integer.class, null);
			return top != null && left != null ? new int[]{top, left} : ARG.toDefThrow(() -> new RequiredRuntimeException("Except top(%s) left(%s) args ", top, left), defRq);
		}

		public Integer get_TOP(Integer... defRq) {
			return ZKS.px(get(CN.TOP, ""), defRq);
		}

		public void set_TOP(int px) {
			set(CN.TOP, px + ZKS.PX);
		}

		public Integer get_LEFT(Integer... defRq) {
			return ZKS.px(get(CN.LEFT, ""), defRq);
		}

		public void set_LEFT(int px) {
			set(CN.LEFT, px + ZKS.PX);
		}

		public void set_TOP_LEFT(int... topLeft) {
			IT.state(topLeft.length == 2);
			set(CN.TOP, topLeft[0] + ZKS.PX);
			set(CN.LEFT, topLeft[1] + ZKS.PX);
		}

		public void set_VIEW(NodeDir.NVT noteViewType) {
			set(PK_VIEW, noteViewType.name());
		}

		public void set_STATE(NodeDir.NVT noteViewType) {
			set(PK_STATE, noteViewType.name());
		}

		public void set_SIZE(Integer size) {
			set(PK_SIZE, size);
		}

		public Integer get_SIZE(Integer... defRq) {
			return getAs(PK_SIZE, Integer.class, defRq);
		}

		public void set_SECE(String vl) {
			set(ISecState.SECE, vl);
		}

		public String get_SECE(String... defRq) {
			return get(ISecState.SECE, defRq);
		}

		public String get_SECV(String... defRq) {
			return get(ISecState.SECV, defRq);
		}

		public String get_EXE(String... defRq) {
			return get(CN.EXE, defRq);
		}

		public boolean isSizable() {
			return get_SIZE(null) != null;
		}

	}

	public NodeDir.NVT viewType(NodeDir.NVT... defRq) {
		NodeDir.NVT view = getAs(PK_VIEW, NodeDir.NVT.class, defRq);
		if (view != null) {
			return view;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("NodeViewType not found from '%s'", formName()), defRq);
	}

	//
	//
	//

	public boolean apply_TITLE(HtmlBasedComponent com) {
		return apply(com, PK_TITLE);
	}

	public boolean apply_TITLEX(HtmlBasedComponent com) {
		return apply(com, PK_TITLEX);
	}

	public boolean apply_TOP_LEFT(HtmlBasedComponent com) {
		return apply(com, TOP_LEFT);
	}

	public boolean apply_BG_COLOR(HtmlBasedComponent com) {
		return apply(com, BG_COLOR);
	}

	public boolean apply_WIDTH_HEIGHT(HtmlBasedComponent com) {
		return apply(com, WIDTH_HEIGHT);
	}

	public boolean apply(HtmlBasedComponent com, String... props) {
		return apply(com, pathFc(), isForm, props);
	}


	public void setFromCom_TOP_LEFT(HtmlBasedComponent com) {
		setFromCom(com, TOP_LEFT);
	}
	//
	//
	//

	public static void updateResizableEvent(Event e, FormState fCom) {

		String w;
		String h;

		if (e instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) e;


			boolean isCtrl = me.getKeys() == ZKE.ZKE_ALT_CODE;
			if (!isCtrl) {
				return;
			}

			//			int oldX = fCom.getPX("width", 0);
			//			int oldY = fCom.getPX("height", 0);

			int newX = me.getX();
			int newY = me.getY();

			//			boolean isBigDiff = oldX > 10 && oldX > 10 && Math.abs(oldX - newX) > 30 || Math.abs(oldY - newY) > 30;
			//			if (isBigDiff) {
			//				return;
			//			}
			w = newX + ZKS.PX;
			h = newY + ZKS.PX;
		} else if (e instanceof SizeEvent) {
			SizeEvent se = (SizeEvent) e;
//			boolean isCtrl = se.getKeys() == 258;
//			if (!isCtrl) {
//				return;
//			}
			w = se.getWidth();
			h = se.getHeight();
		} else {
			throw new WhatIsTypeException(e.getClass());
		}
		fCom.set("width", w);
		fCom.set("height", h);
	}

	public static void updateMoveEvent(Event e, FormState fCom, Component... parent) {
//		MoveEvent me = (MoveEvent) e;
		//					if (me.getKeys() != 258) {
		//						return;
		//					}
		fCom.setFromCom_TOP_LEFT((HtmlBasedComponent) ARG.toDefOr(e.getTarget(), parent));
	}

	public static void addEventListenerMoveAndResize(HtmlBasedComponent comTextWin, HtmlBasedComponent resizableCom_comTextNoteTbxm_OrComTextWin, FormState state) {
		Class aClass = resizableCom_comTextNoteTbxm_OrComTextWin.getClass();
		boolean resizableIsWin = resizableCom_comTextNoteTbxm_OrComTextWin instanceof Window;

		EventListener updateEvent = e -> {
			if (e instanceof MoveEvent) {
				FormState.updateMoveEvent(e, state, comTextWin);
			} else if (
//					!Events.ON_CLICK.equals(e.getName()) && //
					(resizableIsWin ?//
							e instanceof SizeEvent ://
							e instanceof MouseEvent && aClass.isAssignableFrom(e.getTarget().getClass())))//
			{
				FormState.updateResizableEvent(e, state);
				ZKI.infoBottomCenter("Updated " + state.formName());
			}
		};
		resizableCom_comTextNoteTbxm_OrComTextWin.addEventListener(resizableIsWin ? Events.ON_SIZE : Events.ON_DOUBLE_CLICK, updateEvent);
		comTextWin.addEventListener(Events.ON_MOVE, updateEvent);
	}

	public static void apply_TopLeft_WidthHeigth_Bgc_Titles(HtmlBasedComponent comTextWin, //
															HtmlBasedComponent comTextNoteTbxm_OrComTextWin, //
															FormState stateForm, //
															boolean skipAbsolutePositionProps) { //
		if (!stateForm.existPropsFile()) {
			return;
		}
		if (!skipAbsolutePositionProps && !stateForm.apply_TOP_LEFT(comTextWin)) {
			ZKS.TOP_LEFT(comTextWin, 30.0, 30.0);
		}
		if (!stateForm.apply_WIDTH_HEIGHT(comTextNoteTbxm_OrComTextWin)) {
			//nothing, already init
		}

		stateForm.apply_TITLE(comTextNoteTbxm_OrComTextWin);
		stateForm.apply_TITLEX(comTextNoteTbxm_OrComTextWin);

		FormState.apply_BgColor(comTextWin, stateForm);

	}

	public static void apply_BgColor(HtmlBasedComponent com, FormState formState) {
		String bgColor = formState.get(BG_COLOR, null);
		if (bgColor != null) {
			ZKS.BGCOLOR(com, bgColor);
		}
	}

	public static void apply_or_RANDOM_TOP_LEFT(HtmlBasedComponent com, FormState comState) {
		if (!comState.apply_TOP_LEFT(com)) {
			ZKS.APPLY_RANDOM_TOPLEFT(com);
		}
	}

	@Override
	public String toString() {
		return "FormPropsApply:" + pathFcStr + ":isForm=" + isForm;
	}

	public Boolean is_STATE_CLOSED() {
		return getAs_STATE(String.class, null) == null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (fdEquals(o)) {
			return true;
		}
		if (!(o instanceof FormState)) {
			return false;
		}
		FormState<?> formState = (FormState<?>) o;
		return Objects.equals(pathFcStr, formState.pathFcStr);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pathFcStr);
	}


	public Path pathPropsCom(boolean... create) {
		return isForm ? stateCom(create).pathProps() : pathProps();
	}

	public FormState stateCom(boolean... create) {
		IT.state(isForm, "it must be form");
		return ofPathComFile_orCreate(sdn, formName(), EXT.JSON, create);
	}

	public FormState stateForm(boolean... create) {
		IT.state(!isForm, "it must be com");
		return ofPathFormFile_orCreate(sdn, formName(), EXT.JSON, create);
	}

	public NodeDir nodeDir() {
		return NodeDir.ofNodeName(sdn, formName());
	}


//	public enum State {
//		CLOSED
//	}

}
