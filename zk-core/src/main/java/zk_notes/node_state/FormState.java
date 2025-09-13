package zk_notes.node_state;

import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.EXT;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ENUM;
import mpu.pare.Pare;
import mpu.str.TKN;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_notes.node.core.NVT;
import zk_notes.node_state.libs.PageState;
import zk_notes.node_state.libs.PlaneState;
import zk_os.AFC;
import zk_os.AFCC;
import zk_page.ZKS;
import zk_notes.AppNotes;
import zk_notes.node.NodeDir;

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

	public static PlaneState ofPlaneState_orCreate(String sd3) {
		Path dstStateFile = AFC.PLANES.getRpaPlaneStatePath(sd3);
		return FormState.ofPathComFile_orCreate(Pare.of(sd3), dstStateFile, AFC.Entity.PLANE, false);
	}

	//
	//
	//PageState

	public static PageState ofPageState_orCreate(Pare<String, String> sdn) {
		return ofPageState_orCreate(sdn, false);
	}

	public static PageState ofPageState_orCreate(Pare<String, String> sdn, boolean create) {
		Path dstStateFile = AFC.PAGES.getState(sdn);
		return ofPageState_orCreate(sdn, dstStateFile, create);
	}

	public static PageState ofPageState_orCreate(Pare<String, String> sdn, Path pageState, boolean create) {
		return FormState.ofPathComFile_orCreate(sdn, pageState, AFC.Entity.PAGE, create);
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
		return FormState.ofPathComFile_orCreate(sdn, comState, AFC.Entity.COM, create);
	}

	public static FormState ofPathPageComFile_OrCreate(Pare sdn, Path pathCom, boolean... create) {
		return ofPathComFile_orCreate(sdn, pathCom, AFC.Entity.PAGE, create);
	}

	public static <T extends FormState> T ofPathComFile_orCreate(Pare sdn, Path pathCom, AFC.Entity entity, boolean... create) {
		switch (entity) {

			case PLANE:
				return (T) new PlaneState(sdn, pathCom.toString(), false);

			case PAGE:
				return (T) new PageState(sdn, pathCom.toString(), false);

			default:

				FormState propApply = new FormState(sdn, pathCom.toString(), false);
				propApply.pathFc = pathCom;//transient
				if (ARG.isDefEqTrue(create)) {
					propApply.getPropsOrCreate();
				}
				return (T) propApply;
		}
	}

	public static FormState ofState_OrCreate(Pare<String, String> sdn, String afcEntityName, String nodeName, FormState... defRq) {

		FormState formState;
		switch (afcEntityName) {
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
				return ARG.toDefThrowMsg(() -> X.f("illegal state for update : %s", afcEntityName), defRq);

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
//			return ZKS.px(get(CN.TOP, ""), defRq);
			String vl = get(CN.TOP, null);
			return vl != null ? ZKS.px(vl, defRq) : (ARG.toDefThrow(() -> new RequiredRuntimeException("Except value from TOP property"), defRq));
		}

		public Integer get_WIDTH(Integer... defRq) {
			String vl = get(CN.WIDTH, null);
			return vl != null ? ZKS.px(vl, defRq) : (ARG.toDefThrow(() -> new RequiredRuntimeException("Except value from WIDTH property"), defRq));
		}

		public Integer get_HEIGHT(Integer... defRq) {
			String vl = get(CN.HEIGHT, null);
			return vl != null ? ZKS.px(vl, defRq) : (ARG.toDefThrow(() -> new RequiredRuntimeException("Except value from HEIGHT property"), defRq));
		}

		public void set_TOP(int px) {
			set(CN.TOP, px + ZKS.PX);
		}

		public void set_WIDTH(int px) {
			set(CN.WIDTH, px + ZKS.PX);
		}

		public void set_HEIGHT(int px) {
			set(CN.HEIGHT, px + ZKS.PX);
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

		public void set_VIEW(NVT noteViewType) {
			set(PK_VIEW, noteViewType.name());
		}

		public void set_STATE(NVT noteViewType) {
			set(PK_STATE, noteViewType.name());
		}

		public void set_FIXED(boolean fixed) {
			set(PK_FIXED, fixed);
		}

		public Boolean get_FIXED(Boolean... defRq) {
			return getAs(PK_FIXED, Boolean.class, defRq);
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

	public NVT viewType(NVT... defRq) {
		NVT view = getAs(PK_VIEW, NVT.class, defRq);
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
