package zk_com.core;

import mpc.rfl.IRfl;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.EQ;
import mpc.exception.RequiredRuntimeException;
import mpc.html.StyleMap;
import mpc.map.MAP;
import mpu.str.RANDOM;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.Tbxmy;
import zk_com.base_ext.JavascriptTAG;
import zk_com.base_ext.StyleTAG;
import zk_com.base_ctr.Menupopup0;
import zk_com.win.CWindowComposer;
import zk_form.events.DefAction;
import zk_form.head.IHeadCom;
import zk_form.notify.ZKI;
import zk_os.sec.MatrixAccess;
import zk_page.ZKC;
import zk_page.ZKCFinder;
import zk_page.ZKS;
import zk_page.ZKPage;
import zk_notes.node_state.FormState;

import java.util.List;

public interface IZCom<T extends XulElement> extends IZStyle, IZState, IRfl {
	public static final Logger L = LoggerFactory.getLogger(IZCom.class);

	default void applyState_BgColor(FormState... comState) {
		FormState.apply_BgColor(comH(), comState.length == 1 ? comState[0] : getComStateDefault());
	}

	default void draggablePersistense(String name) {
		absolute();
		ZKS.DRAG_DROP(comH(), true, true);
		FormState.apply_or_RANDOM_TOP_LEFT(comH(), getComState_JSON(name, false));
	}

	default void draggablePersistenseForm(String name) {
		absolute();
		ZKS.DRAG_DROP(comH(), true, true);
		FormState.apply_or_RANDOM_TOP_LEFT(comH(), getFormState_PROPS(name, false));
	}

	default void draggablePersistensePageCom(String name) {
		absolute();
		ZKS.DRAG_DROP(comH(), true, true);
		FormState.apply_or_RANDOM_TOP_LEFT(comH(), getPageComState_JSON(name, false));
	}

	default IZCom applyState_RandomOrTopLeft(String comname) {
		return applyState_RandomOrTopLeft(getComState_JSON(comname, false));
	}

	default IZCom applyState_RandomOrTopLeft(FormState... comState) {
		ZKS.DRAG_DROP(comH());
		FormState.apply_or_RANDOM_TOP_LEFT(comH(), comState.length == 1 ? comState[0] : getComStateDefault());
		return this;
	}

	//	default IZCom openInFirstWindow() {
	//		ZKC.getFirstWindow().appendChild(comH());
	//		return this;
	//	}
	default IZCom openInFirstWindow(Window... parent) {
		(ARG.isDefNNF(parent) ? parent[0] : ZKC.getFirstWindow()).appendChild(comH());
		return this;
	}

	default FormState getComStateDefault() {
		return getComState_JSON(getClass().getSimpleName(), false);
	}

	default <T> T findSingleParent(Class<T> clazz, T... defRq) {
		Component singleParent = ZKCFinder.findSingleParent(com(), (Class) clazz, null);
		if (singleParent != null) {
			return (T) singleParent;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Parent not found for component '%s'", getClass().getSimpleName()), defRq);
	}

	default <T> List<T> findAll(Class<T> clazz, boolean recirsive_or_first, List... defRq) {
		return ZKCFinder.findAllFromParent(com(), (Class) clazz, recirsive_or_first, defRq);
	}

	static <T> T removeMeFirst(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		T first = findFirstInPage(clazz, recursive_or_first, null);
		if (first != null) {
			ZKC.removeMeCheckWindowParentReturnParent((Component) first);
			return first;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Not found first for removeMeFirst");
	}

	static <T> T findFirstInPage(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		return ZKCFinder.findFirstIn_Page(clazz, recursive_or_first, defRq);
	}

	static <T> T findFirstInWin(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		return ZKCFinder.findFirstIn_Win(clazz, recursive_or_first, defRq);
	}
//	default T hideFirst(T... defRq) {
//		return ZKS.hideFirst((Class<T>) getClass(), false, defRq);
//	}

	default void removeMe() {
		ZKC.removeMeCheckWindowParentReturnParent(com());
	}

	default IZCom onDefaultAction(DefAction defAction, String... event) {
		comX().addEventListener(ARG.toDefOr(Events.ON_CLICK, event), (SerializableEventListener) e -> defAction.onDefAction(e));
		return this;
	}

	default XulElement onDefaultAction(String event, DefAction defAction) {
		comX().addEventListener(ARG.toDefOr(Events.ON_CLICK, event), (SerializableEventListener) e -> {
//			if (defAction.length == 0) {
//			onDefaultActionEvent(e);
//			} else {
//				defAction[0].onDefAction(e);
			defAction.onDefAction(e);
//			}
		});
		return (XulElement) this;
	}

	default void onDefaultActionEvent(Event event) {
	}

	default <T> T defaultAction(String... onEvent) {
		String event = ARG.toDefOr(Events.ON_CLICK, onEvent);
		com().addEventListener(event, (SerializableEventListener) eventHandler -> onDefaultActionEvent(eventHandler));
		return (T) this;
	}

	default <T> T defaultActionLog(String msg, String... onEvent) {
		String event = ARG.toDefOr(Events.ON_CLICK, onEvent);
		com().addEventListener(event, (SerializableEventListener) eventHandler -> {
			try {
				ZKI.log(msg);
			} catch (Exception err) {
				L.error("Happens onDefaultLogAction error", err);
			}
		});
		return (T) this;
	}

	//
	//
	//
	default StyleMap styleMap(StyleMap... defRq) {
		String style = comX().getStyle();
		return style != null ? StyleMap.of(style) : ARG.toDefThrow(() -> new RequiredRuntimeException("Property style not found from component '%s'", this), defRq);
	}


	static Menupopup0 createPopupMenu(HtmlBasedComponent hostHolderContainer, XulElement onChild, String openMenuOnEvent_orNullIfRightClick) {
		return Menupopup0.createMenupopup(hostHolderContainer, onChild, openMenuOnEvent_orNullIfRightClick);
	}

	default T onCLICK(EventListener<? extends Event> listener) {
		Component c = (Component) this;
		c.addEventListener(Events.ON_CLICK, listener);
		return (T) c;
	}

	default T onDBLCLICK(EventListener<? extends Event> listener) {
		Component c = (Component) this;
		c.addEventListener(Events.ON_DOUBLE_CLICK, listener);
		return (T) c;
	}

	default Component appendTo(Component... parent) {
		Component it = com();
		boolean visibleState = isVisibleState();
//		it.setVisible(visibleState);
//		parent.appendChild(it);
		if (visibleState) {
			boolean append = ARG.isDef(parent) ? parent[0].appendChild(it) : ZKC.getFirstWindow().appendChild(it);
		}
		if (this instanceof IHeadCom) {
			ZKPage.renderHeadRsrc((IHeadCom) this);
		}
		return it;
	}

	default Window getFirstWindow() {
		return ZKC.getFirstWindow();
	}

	default boolean isVisibleState() {
//		return true;
		return MatrixAccess.hasAccessForCurrentUser(getMA(), true);
	}

	default MatrixAccess getMA() {
		return null;
	}

	default XulElement setSTYLE(String style, Object... args) {
		return ZKS.STYLE((T) this, style, args);
	}

	default XulElement addSTYLE(String style, Object... args) {
		return ZKS.addSTYLE((T) this, style, args);
	}

	default XulElement addStyleAttr(String attr, String val) {
		return ZKS.addStyleAttr((T) this, attr, val);
	}

	default XulElement rmStyleAttr(String attr) {
		return ZKS.rmStyleAttr((T) this, attr);
	}

	default void addStyleTAG(String styleTagContent, Object... args) {
		com().appendChild(new StyleTAG(styleTagContent, args));
	}

	default void addJavascriptTAG(String styleTagContent, Object... args) {
		com().appendChild(new JavascriptTAG(styleTagContent, args));
	}

	default XulElement setCLASS(String clazz) {
		XulElement it = (XulElement) this;
		it.setClass(clazz);
		return it;
	}

	default XulElement setClassAndStyle(String clazz, String style) {
		XulElement it = (XulElement) this;
		setSTYLE(style);
		setCLASS(clazz);
		return it;
	}


	default Component set(String attr, Object val) {
		Component child = (Component) this;
		child.setAttribute(attr, val);
		return child;
	}

	default boolean isAttrEq(String key, Object val, boolean... unsafeEquals) {
		Object atrVal = com().getAttribute(key);
		return EQ.equals(val, atrVal, !ARG.isDefEqTrue(unsafeEquals));
	}

	default void append() {
		appendTo(ZKC.getFirstWindow());
	}

	default String getClasses(String... defRq) {
		String sclass = comH().getSclass();
		if (X.notEmpty(sclass)) {
			return sclass;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Class is empty"), defRq);
	}

	default XulElement show(Component... parent) {
		XulElement child = comX();
		if (ARG.isDef(parent)) {
			ARG.toDef(parent).appendChild(child);
		} else {
			ZKC.getFirstWindow().appendChild(child);
		}
		return child;
	}

	default Window _showInWindow(Component... parent) {
		return CWindowComposer._show(this, parent);
	}

	default String cn() {
		return getClass().getSimpleName();
	}

	//
	//

	default IZCom<T> attr(String key, Object newValue) {
		Object o = attr_put(key, newValue);
		return this;
	}

	default Object attr_put(String key, Object newValue) {
		return MAP.update(com().getAttributes(), key, newValue, MAP.PutGetRemove.PUT);
	}

	default Object attr_rm(String key) {
		return MAP.update(com().getAttributes(), key, null, MAP.PutGetRemove.REMOVE);
	}

	default Object attr_get(String key, Object... defRq) {
		return MAP.get(com().getAttributes(), key, defRq);
	}

	default <T> T attr_as(String key, Class<T> asType, T... defRq) {
		return MAP.getAs(com().getAttributes(), key, asType, defRq);
	}

	default Boolean attr_is(String key, Boolean... defRq) {
		return attr_as(key, Boolean.class, defRq);
	}

	default String attr_str(String key, String... defRq) {
		return attr_as(key, String.class, defRq);
	}

	//
	//
	default IZCom placeholder(String placeholder) {
		if (this instanceof InputElement) {
			InputElement inputElement = (InputElement) this;
			inputElement.setPlaceholder(placeholder);
		} else if (this instanceof Tbxmy) {
			Tbxmy inputElements = (Tbxmy) this;
			inputElements.getComsAsTbxm().stream().forEach(t -> t.placeholder(placeholder));
		}
		return this;
	}

	default Menupopup0 getOrCreateMenupopup(HtmlBasedComponent holder) {
		IT.state(holder != this, "holder==this");
		return getOrCreateMenupopup(holder, null);
	}

	default Menupopup0 getOrCreateMenupopup(HtmlBasedComponent holder, String eventForOpenMenu_orNullIfRightClick) {
		String popup = comX().getContext();
		if (X.notEmpty(popup)) {
			IT.state(popup.startsWith("uuid("));
			String uuid = STR.substrCount(comX().getContext(), 5, 1);
			Menupopup0 component = (Menupopup0) holder.getChildren().stream().filter(c -> c instanceof Menupopup0 && c.getUuid().equals(uuid)).findFirst().get();
			return component;
		}
		return Menupopup0.createMenupopup(holder, comX(), eventForOpenMenu_orNullIfRightClick);
	}

	default XulElement comX() {
		return (XulElement) this;
	}

	default IZCom randomColorWithRandomPosAbs(String... bgColors) {
		applyState_RandomOrTopLeft();
		absolute();
		randomColor(bgColors);
		return this;
	}

	default void randomColor(String[] bgColors) {
		if (ARG.isDef(bgColors)) {
			ZKS.BGCOLOR(comH(), RANDOM.ARRAY_ITEM(bgColors));
		}
	}

	default IZCom daemon() {
		attr_put("com.daemon", true);
		return this;
	}

	default Boolean isDaemon(Boolean... defRq) {
		return attr_is("com.daemon", defRq);
	}

	default IZCom replaceWith(Component newCom) {
		Component oldCom = com();
		oldCom.getParent().insertBefore(newCom, oldCom);
		oldCom.detach();
		return this;
	}

	;


//	@Override
//	default FormState getDefaultComState() {
//		return getComState(getClass().getSimpleName(), false, true);
//	}

}
