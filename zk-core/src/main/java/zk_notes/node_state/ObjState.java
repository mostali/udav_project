package zk_notes.node_state;

import mpc.exception.RequiredRuntimeException;
import mpe.str.CN;
import mpu.IT;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.TKN;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_notes.node.core.NVT;
import zk_notes.node_state.impl.ComState;
import zk_os.sec.SecApp;
import zk_page.ZKS;
import zk_notes.node.NodeDir;

import java.nio.file.Path;
import java.util.Objects;


public abstract class ObjState<P> extends SecFileState<P> {

	public static final String[] TOP_LEFT = {"top", "left"};
	public static final String[] WIDTH_HEIGHT = {"width", "height"};
	public static final String BG_COLOR = "bg-color";
	public static final String OPEN = "open";

	//
	//
	//

	public String objName() {
		return pathProps().getParent().getFileName().toString();
	}

	public ObjState(Pare sdn, String pathComStr, boolean isForm) {
		super(sdn, pathComStr, isForm);
	}

//	public <E extends Enum> E stateTypeFrom(Class<E>... types) {
//		String propState = get_STATE(null);
//		if (propState == null) {
//			return null;
//		}
//		Map<String, Enum> valuesAsMap = ENUM.getValuesAsMap((Class[]) types);
//		return (E) valuesAsMap.getOrDefault(propState, null);
//	}

	public Fields fields() {
		return new Fields();
	}

	public Boolean isVisibleBody() {
		return getAs(ObjState.BODY_VISIBLE, Boolean.class, true);
	}

	public Boolean isNeedOpenIfHide() {
		return getAs(ObjState.BODY_OPENIFHIDE, Boolean.class, false);
	}

	public Boolean isToggleBodyBehaviour() {
		return getAs(ObjState.BODY_TOGGLE, Boolean.class, false);
	}

	@Override
	public String spaceName() {
		return sdn.key();
	}

	public enum Position {
		ABS, FIX, REL
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


		public void set_POSITION(Position position) {
			set(PK_POS, position);
		}

		public Position get_POSITION(Position... defRq) {
			return getAs(PK_POS, Position.class, defRq);
		}

		public void set_FIXED(boolean fixed) {
			set(PK_FIXED, fixed);
		}

		public Boolean get_FIXED(Boolean... defRq) {
			return getAs(PK_FIXED, Boolean.class, defRq);
		}

//		public void set_RELATIVE(boolean relative) {
//			set(PK_RELATIVE, relative);
//		}
//
//		public Boolean get_RELATIVE(Boolean... defRq) {
//			return getAs(PK_RELATIVE, Boolean.class, defRq);
//		}

		public void set_SIZE(Integer size) {
			set(PK_SIZE, size);
		}

		public Integer get_SIZE(Integer... defRq) {
			return getAs(PK_SIZE, Integer.class, defRq);
		}

		public void set_SECE(String vl) {
			set(SecApp.SECE, vl);
		}

		public String get_SECE(String... defRq) {
			return get(SecApp.SECE, defRq);
		}

		public String get_SECV(String... defRq) {
			return get(SecApp.SECV, defRq);
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
		return ARG.toDefThrow(() -> new RequiredRuntimeException("NodeViewType not found from '%s'", objName()), defRq);
	}

	//
	//
	//

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
		if (!(o instanceof ObjState)) {
			return false;
		}
		ObjState<?> formState = (ObjState<?>) o;
		return Objects.equals(pathFcStr, formState.pathFcStr);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pathFcStr);
	}


	public Path pathPropsCom(boolean... create) {
		return isForm ? stateCom(create).pathProps() : pathProps();
	}

	public ComState stateCom(boolean... create) {
		IT.state(isForm, "it must be form");
		return AppStateFactory.forCom(sdn, objName(), create);
	}

	public NodeDir nodeDir() {
		return NodeDir.ofNodeName(sdn, objName());
	}

}
