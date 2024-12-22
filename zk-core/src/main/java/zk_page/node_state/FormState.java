package zk_page.node_state;

import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.EXT;
import mpe.str.CN;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.pare.Pare;
import mpu.str.USToken;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.Window;
import zk_os.AFC;
import zk_page.ZKS;
import zk_notes.AppNotes;
import zk_page.node.NodeDir;

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

	public static FormState ofPlaneState(String sd3) {
		Path dstStateFile = AFC.getRpaPlaneStatePath(sd3);
		return FormState.ofPathComFile_OrCreate(dstStateFile, Pare.of(sd3), false);
	}

	//
	//
	//PageState

	public static FormState ofPageState(Pare<String, String> sdn) {
		return ofPageState(sdn, false);
	}

	public static FormState ofPageState(Pare<String, String> sdn, boolean create) {
		Path dstStateFile = AFC.getRpaPageStatePath(sdn);
		return ofPageState(dstStateFile, sdn, create);
	}

	public static FormState ofPageState(Path pageState, Pare<String, String> sdn, boolean create) {
		return FormState.ofPathComFile_OrCreate(pageState, sdn, create);
	}

	//
	//Page Com
	public static FormState ofPathPageComFile_OrCreate(Pare<String, String> sdn, String comname, EXT ext, boolean... create) {
		Path comState = AFC.getRpaPageComStatePath(sdn.key(), sdn.val(), comname, ext);
		return FormState.ofPathPageComFile_OrCreate(comState, sdn, create);
	}

	//
	//
	//Form

	public static FormState ofFormName(String nodeName, Pare sdn, String content, boolean... create) {
		Path pathOfFormNote = AppNotes.getPathOfFormNote_PPI(nodeName);
		if (ARG.isDefEqTrue(create)) {
			boolean created = UFS_BASE.MKFILE.createFileIfNotExistWithContentMkdirs(pathOfFormNote, content);
			if (!created) {
				UFS_BASE.RM.deleteDir(pathOfFormNote.getParent());
				IT.state(UFS_BASE.MKFILE.createFileIfNotExistWithContentMkdirs(AppNotes.getPathOfFormNote_PPI(nodeName), content));
			}
		}
		return ofPathFormFile_orCreate(pathOfFormNote, sdn);
	}

	public static FormState ofFormName(String noteName, Pare sdn) {
		Path pathOfFormNotePpi = AppNotes.getPathOfFormNote_NOPPI(sdn, noteName);
		return ofPathFormFile_orCreate(pathOfFormNotePpi, sdn);
	}

	public static FormState ofFormDir(Pare sdn, Path nodeDir) {
		String nodeName = nodeDir.getFileName().toString();
		Path formpathFile = AFC.getRpaFormStatePath(ARG.toDef(sdn), nodeName);
		return ofPathFormFile_orCreate(formpathFile, sdn);
	}

	public static FormState ofPathFormFile_orCreate(Pare<String, String> sdn, String formname, EXT ext, boolean... create) {
		Path formState = AFC.getRpaFormStatePath(sdn.key(), sdn.val(), formname, ext);
		return FormState.ofPathFormFile_orCreate(formState, sdn, create);
	}

	public static FormState ofPathFormFile_orCreate(Path pathCom, Pare sdn, boolean... create) {
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

	public static FormState ofPathComFile_OrCreate(Pare<String, String> sdn, String comname, EXT ext, boolean... create) {
		Path comState = AFC.getRpaComStatePath(sdn.key(), sdn.val(), comname, ext);
		return FormState.ofPathComFile_OrCreate(comState, sdn, create);
	}

	public static FormState ofPathPageComFile_OrCreate(Path pathCom, Pare sdn, boolean... create) {
		return ofPathComFile_OrCreate(pathCom, sdn, create);
	}

	public static FormState ofPathComFile_OrCreate(Path pathCom, Pare sdn, boolean... create) {
		FormState propApply = new FormState(sdn, pathCom.toString(), false);
		propApply.pathFc = pathCom;//transient
		if (ARG.isDefEqTrue(create)) {
			propApply.getPropsOrCreate();
		}
		return propApply;
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
		String propState = getProp_STATE(null);
		if (propState == null) {
			return null;
		}
		Map<String, Enum> valuesAsMap = ENUM.getValuesAsMap((Class[]) types);
		return (E) valuesAsMap.getOrDefault(propState, null);
	}

	public void updatePropSingle_TopLeft(int[] topLeft) {
		update(CN.TOP, topLeft[0] + "px");
		update(CN.LEFT, topLeft[1] + "px");
	}


	//
	// PROP -> VIEW

	public void updateProp_VIEW(NodeDir.NVT noteViewType) {
		update(PK_VIEW, noteViewType.name());
	}

	public class Upd {
		public void updateSize(Integer size) {
			update(PK_SIZE, size);
		}

		public Integer getSize(Integer... defRq) {
			return getAs(PK_SIZE, Integer.class, defRq);
		}

		public void update_SECE(String vl) {
			update(ISecState.SECE, vl);
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
			return upd().getSize(null) != null;
		}


	}

	public Upd upd() {
		return new Upd();
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


	public void updatePropsFromCom_TOP_LEFT(HtmlBasedComponent com) {
		updatePropsFromCom(com, TOP_LEFT);
	}
	//
	//
	//

	public static void updateResizableEvent(Event e, FormState fCom) {

		String w;
		String h;

		if (e instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) e;

			boolean isCtrl = me.getKeys() == 258;
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
			w = newX + "px";
			h = newY + "px";
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
		fCom.update("width", w);
		fCom.update("height", h);
	}

	public static void updateMoveEvent(Event e, FormState fCom, Component... parent) {
//		MoveEvent me = (MoveEvent) e;
		//					if (me.getKeys() != 258) {
		//						return;
		//					}
		fCom.updatePropsFromCom_TOP_LEFT((HtmlBasedComponent) ARG.toDefOr(e.getTarget(), parent));
	}

	public static void addEventListenerMoveAndResize(HtmlBasedComponent movableParentCom, HtmlBasedComponent resizableChildCom, FormState props) {
		Class aClass = resizableChildCom.getClass();
		boolean resizableIsWin = resizableChildCom instanceof Window;

		EventListener updateEvent = e -> {
			if (e instanceof MoveEvent) {
				FormState.updateMoveEvent(e, props, movableParentCom);
			} else if (
//					!Events.ON_CLICK.equals(e.getName()) && //
					(resizableIsWin ?//
							e instanceof SizeEvent ://
							e instanceof MouseEvent && aClass.isAssignableFrom(e.getTarget().getClass())))//
			{
				FormState.updateResizableEvent(e, props);
			}
		};
		resizableChildCom.addEventListener(resizableIsWin ? Events.ON_SIZE : Events.ON_DOUBLE_CLICK, updateEvent);
		movableParentCom.addEventListener(Events.ON_MOVE, updateEvent);
	}

	public static void apply_TopLeft_WidthHeigth_Bg(HtmlBasedComponent parentCom, HtmlBasedComponent com, FormState props) {
		if (!props.existPropsFile()) {
			return;
		}
		if (!props.apply_TOP_LEFT(parentCom)) {
			ZKS.TOP_LEFT(parentCom, 30.0, 30.0);
		}
		if (!props.apply_WIDTH_HEIGHT(com)) {
			//nothing, already init
		}

		FormState.apply_BgColor(parentCom, props);

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
		return getPropAs_STATE(String.class, null) == null;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (fEquals(o)) {
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

	public int[] getTopLeftArgs(int[]... defRq) {
		Integer top = USToken.first(get(CN.TOP, null), "px", Integer.class, null);
		Integer left = USToken.first(get(CN.LEFT, null), "px", Integer.class, null);
		return top != null && left != null ? new int[]{top, left} : ARG.toDefThrow(() -> new RequiredRuntimeException("Except top(%s) left(%s) args ", top, left), defRq);
	}

	public FormState comState(boolean... create) {
		return ofPathComFile_OrCreate(sdn, formName(), EXT.JSON, create);
	}

	public NodeDir nodeDir() {
		return NodeDir.ofNodeName(formName(), sdn);
	}


//	public enum State {
//		CLOSED
//	}

}
