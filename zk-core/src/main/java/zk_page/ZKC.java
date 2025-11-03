package zk_page;

import mpc.log.L;
import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import mpe.core.U;
import mpu.X;
import mpc.exception.RequiredRuntimeException;
import mpc.rfl.RFL;
import mpu.core.ARR;
import mpu.str.STR;
import org.zkoss.sound.AAudio;
import org.zkoss.video.AVideo;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;
import zk_com.base.Ln;
import zk_com.ext.video.AdvVideo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

//https://jsfiddle.net/solona/s6youz83/
//https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Composing/Macro_Component/Implement_Custom_Java_Class
//Component
public class ZKC {

	public static PageCtrl getFirstPageCtrl() {
		return (PageCtrl) getFirstPage();
	}

	public static Page getFirstPage() {
		return Executions.getCurrent().getDesktop().getFirstPage();
	}

	public static Window getFirstWindow(Window... defRq) {
		Window firstPageRoot = (Window) getFirstPageRoot();
		if (firstPageRoot != null) {
			return firstPageRoot;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("First window not found"), defRq);
	}

	public static Window getFirstWindow(Desktop desktop) {
		return (Window) desktop.getFirstPage().getFirstRoot();
	}

	public static Component getFirstPageRoot() {
		if (Executions.getCurrent() == null || Executions.getCurrent().getDesktop() == null || Executions.getCurrent().getDesktop().getFirstPage() == null) {
			return null;
		}
		return Executions.getCurrent().getDesktop().getFirstPage().getFirstRoot();
	}

	public static Collection<Component> getPageRoots() {
		return Executions.getCurrent().getDesktop().getFirstPage().getRoots();
	}

	public static Component loadComponentOrErrComponent(Component parent, String content) {
		return loadComponentOrErrComponent(parent, null, content, new String[0]);
	}

	public static Component loadComponentOrErrComponent(Component parent, String content, Object... formatArgs) {
		return loadComponentOrErrComponent(parent, null, content, formatArgs);
	}

	public static Component loadComponentOrErrComponent(Component parent, String content, Map context, Object... formatArgs) {
		content = X.f(content, formatArgs);
		try {
			return Executions.createComponentsDirectly(content, null, parent, context);
		} catch (Exception e) {
			e.printStackTrace();
			return Executions.createComponentsDirectly("stuff/error-simple.zul", null, parent, null);
		}
	}

	public static Component loadComponentOrErrComponent(Component parent, Map context, String content, Object... formatArgs) {
		content = X.f(content, formatArgs);
		try {
			return Executions.createComponentsDirectly(content, null, parent, context);
		} catch (Exception e) {
			e.printStackTrace();
			return Executions.createComponentsDirectly("stuff/error-simple.zul", null, parent, null);
		}
	}

	public static Component loadComponentFromRsrcOrErrComponent(Component parent, String resourceFileName) {
		try {
			return Executions.createComponentsDirectly(new InputStreamReader(ZKC.class.getClassLoader().getResourceAsStream(resourceFileName)), null, parent, null);
		} catch (IOException e) {
			e.printStackTrace();
			return Executions.createComponentsDirectly("stuff/error-simple.zul", null, parent, null);

		}
	}


	public static <T> List<T> findComponent(Collection<Component> components, Class<T> componentClass, int offset, int limit, boolean reqired) {
		List<T> findedComponents = new ArrayList();
		if (limit == 0) {
			return findedComponents;
		}
		if (offset < 0) {
			offset = 0;
		}
		Iterator it = components.iterator();

		while (it.hasNext()) {
			if (offset-- == 0) {
				continue;
			}
			if (limit == 0) {
				return findedComponents;
			}
			Object checked = it.next();
			if (checked != null && componentClass.isAssignableFrom(checked.getClass())) {
				findedComponents.add(componentClass.cast(checked));
				limit--;
			}
		}
		if (reqired && findedComponents.isEmpty()) {
			throw new NullPointerException("Component not found:::" + componentClass + "\n" + U.multiMessageMerge("\n", components.toArray()));
		}
		return findedComponents;
	}

	public static void detachFirstParentWindow(HtmlBasedComponent com) {
		Component par = com.getParent();
		while (par != null) {
			if (par instanceof Window) {
				par.detach();
				return;
			}
			par = par.getParent();
		}
	}

	public static void detachAll(HtmlBasedComponent com) {
		List<Component> children = com.getChildren();
		if (children == null) {
			return;
		}
		if (true) {
			for (Component c : children) {
				c.detach();
			}
		} else {
			Iterator<Component> iterator = children.iterator();
			while (iterator.hasNext()) {
				Component c = iterator.next();
				c.detach();
//			iterator.remove();
			}
		}
		children.clear();
	}

	public static Div createDiv(List<Component> zkComs) {
		Div center = new Div();
		for (Component c : zkComs) {
			center.appendChild(c);
		}
		return center;
	}

	public static AdvVideo newVideo(Path fileVideo, String... id) {
		AVideo cont = new AVideo(fileVideo.toFile());
		AdvVideo audio = new AdvVideo() {
		};
		audio.setControls(true);
		audio.setContent(cont);
		if (ARG.isDef(id)) {
			audio.setId(ARG.toDef(id));
		}
		return audio;
	}

	public static Component newLabel(String label, String... id) {
		Label l = new Label(label);
		if (ARG.isDef(id)) {
			l.setId(ARG.toDef(id));
		}
		return l;
	}

	public static Audio newAudio(Path fileMp3, String... id) {
		AAudio cont = new AAudio(fileMp3.toFile());
		Audio audio = new Audio() {
			@Override
			public String getWidgetClass() {
				return super.getWidgetClass();
			}
		};
		audio.setControls(true);
		audio.setContent(cont);
		if (ARG.isDef(id)) {
			audio.setId(ARG.toDef(id));
		}
		return audio;
	}

	public static Label newLabelBlock(String label, String... id) {
		Label l = new Label(label);
		if (ARG.isDef(id)) {
			l.setId(ARG.toDef(id));
		}
		l.setStyle("display:block");
		return l;
	}

	public static Component newCaption(String label, String... id) {
		Caption l = new Caption(label);
		if (ARG.isDef(id)) {
			l.setId(ARG.toDef(id));
		}
		return l;
	}

	public static Div newDiv(Component child) {
		Div _div = new Div();
		_div.appendChild(child);
		return _div;
	}

	public static Div newDiv(List<Component> coms, String... id) {
		Div _div = new Div();
		if (ARG.isDef(id)) {
			_div.setId(ARG.toDef(id));
		}
		if (X.notEmpty(coms)) {
			for (Component c : coms) {
				_div.appendChild(c);
			}
		}
		return _div;
	}

	public static void newParent(Component com, Component newParent) {
		removeMeCheckWindowParentReturnParent(com);
		newParent.appendChild(com);
	}

	public static Component removeParentWindowForChild(Event event, boolean... required) {
		return removeTargetParentComponent(event.getTarget(), Window.class, required);
	}

	public static Component removeParentWindowForChild(Component comChild, boolean... required) {
		return removeTargetParentComponent(comChild, Window.class, required);
	}

	public static Component removeTargetParentComponent(Component removeMe, Class parentClass, boolean... required) {
		Component parent = null;
		do {
			parent = parent == null ? removeMe.getParent() : parent.getParent();
		} while (parent != null && !parentClass.isAssignableFrom(parent.getClass()));
		if (parent != null) {
			return removeMeCheckWindowParentReturnParent(parent);
		} else if (ARG.isDefEqTrue(required)) {
			throw new RequiredRuntimeException("Parent '%s' not found for component '%s'", RFL.scn(removeMe), RFL.scn(removeMe));
		}
		return parent;
	}

	public static Component removeMeReturnParentExt(Component removeMe) {
		removeMe.detach();
		return removeMeCheckWindowParentReturnParent(removeMe);
	}

	public static Component removeMeCheckWindowParentReturnParent(Component removeMe) {
		Component parent = removeMe.getParent();
		if (parent == null && removeMe instanceof Window) {
			removeMe.detach();
			((Window) removeMe).onClose();
		} else {
			parent.removeChild(removeMe);
		}
		return parent;
	}

	public static Component removeMeReturnParentWithEffect(XulElement removeMe) {
		Component parent = removeMe.getParent();
		parent.removeChild(removeMe);
		ZKS.addStyleAttrRMM(removeMe);
		return parent;
	}

	public static void detachAllSafety(Component sourceView) {
		try {
			List<Component> children = sourceView.getChildren();
			for (Component component : children) {
				component.detach();
			}
		} catch (Exception ex) {
			L.warn("cleanSafety " + sourceView, ex);
		}
	}

	public static Component appendChild(Component child) {
		Component firstRoot = Executions.getCurrent().getDesktop().getFirstPage().getFirstRoot();
		firstRoot.appendChild(child);
		return firstRoot;
	}


	public static void setAttributeToCom(Component com, String key, Serializable attribute) {
		com.setAttribute(key, attribute);
	}

	public static String getAttributeFromCom(Component com, String key, String... defRq) {
		String attribute = (String) com.getAttribute(key);
		if (attribute != null) {
			return attribute;
		}
		return ARG.toDefRq(defRq);
	}


	public static <C extends Component> void removeAllInPage(Class<C> scriptViewClass) {
		List<Component> allInPage = ZKCFinderExt.findAll_inPage0(scriptViewClass, true, ARR.EMPTY_LIST);
		allInPage.forEach(ZKC::removeMeCheckWindowParentReturnParent);
	}

	public static <C extends Component> void removeAllInWindow(Class<C> scriptViewClass) {
		List<Component> allInPage = ZKCFinderExt.findAllInFirstWin0(scriptViewClass, true, ARR.EMPTY_LIST);
		allInPage.forEach(ZKC::removeMeCheckWindowParentReturnParent);
	}


	public static String toStringLog(Component c) {
		return c == null ? "ComIsNull" : c.getClass().getSimpleName() + "#" + c.getUuid();
	}

	static Set prevTotal;

	public static void printAll() {
		Set<Component> total = new HashSet<>(0);
		ZKC.getPageRoots().forEach(c -> printNext(c, 0, total));
		X.p("Total com on page:" + total.size());
		if (prevTotal == null) {
			prevTotal = total;
			return;
		}
		HashSet<Component> cloneTotal = new HashSet(total);
		boolean b = cloneTotal.removeAll(prevTotal);
		X.p("-------------------------------Clone---------------------------:" + cloneTotal.size());
		cloneTotal.forEach(c -> printNext(c, 0, null));
		X.p("Total com on page:" + total.size());
		X.p("Diff:" + cloneTotal.size());

	}

	public static void printNext(Component com, int level, Set total) {
//		total.incrementAndGet();
		if (total != null) {
			total.add(com);
		}
		X.p(STR.TAB(level) + RFL.scn(com) + "*" + X.sizeOf(com.getChildren()));
		for (Component innerCom : com.getChildren()) {
			printNext(innerCom, level + 1, total);
		}
	}

	public static Desktop getFirstDesktop(Desktop... defRq) {
		Execution current = Executions.getCurrent();
		if (current != null) {
			Desktop desktop = current.getDesktop();
			if (desktop != null) {
				return desktop;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("First Page not found"), defRq);
	}

	public static boolean appendCloseLinkTo(HtmlBasedComponent com, Supplier<Component> closeThisOrNull) {
		return com.appendChild(new Ln(SYMJ.FAIL_NICE_BOLD).onCLICK(e -> {
			if (closeThisOrNull != null) {
				closeThisOrNull.get().detach();
			} else {
				com.detach();
			}
		}));
	}

	public static <T> T removeMeFirst(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		T first = findMeFirst(clazz, recursive_or_first, null);
		if (first != null) {
			removeMeCheckWindowParentReturnParent((Component) first);
			return first;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except first component for removeMeFirst"), defRq);
	}

	public static <T> T findMeFirst(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		return ZKCFinderExt.findFirst_InPage(clazz, recursive_or_first, defRq);
	}
}
