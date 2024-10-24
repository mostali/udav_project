package zk_page;

import mpu.core.ARG;
import mpe.core.U;
import mpu.X;
import mpc.exception.RequiredRuntimeException;
import mpc.rfl.RFL;
import mpu.core.ARR;
import org.zkoss.sound.AAudio;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

//https://jsfiddle.net/solona/s6youz83/
//https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Composing/Macro_Component/Implement_Custom_Java_Class
//Component
public class ZKC {

	//		editCom.setCtrlKeys("@v@b");

//	public static final Component DIV1 = new TCom("<div>div1</div>").toComHtml();
//	public static final Component DIV2 = new TCom("<div>div2</div>").toComHtml();
//	public static final Component CDM1 = new TCom("<div>div1m</div><div>div2m</div><div>div3m</div><div>div4m</div>").toComHtml();
//	public static final Component CI1 = new TCom("<i>cursive1</i>").toComHtml();
//	public static final Component CS1 = new TCom("<span>span1</span>").toComHtml();
//	public static final Component HR = new TCom("<hr/>").toComHtml();

	public static PageCtrl getFirstPageCtrl() {
		return (PageCtrl) getFirstPage();
	}

	public static <T> T getFirstPageAs(Class<T> asType) {
		return asType.cast(getFirstPage());
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

	public static Component getFirstPageRoot() {
		return Executions.getCurrent().getDesktop().getFirstPage().getFirstRoot();
	}

	public static Collection getPageRoots() {
		return Executions.getCurrent().getDesktop().getFirstPage().getRoots();
	}

	public static Collection getPageRootsFellows() {
		return Executions.getCurrent().getDesktop().getFirstPage().getFellows();
	}

	public static Component contentQ(Component parent, String content) {
		return contentQ(parent, null, content, new String[0]);
	}

	public static Component contentQ(Component parent, String content, Object... formatArgs) {
		return contentQ(parent, null, content, formatArgs);
	}

	public static Component contentQ(Component parent, String content, Map context, Object... formatArgs) {
		content = X.f(content, formatArgs);
		try {
			return Executions.createComponentsDirectly(content, null, parent, context);
		} catch (Exception e) {
			e.printStackTrace();
			return Executions.createComponentsDirectly("stuff/error-simple.zul", null, parent, null);
		}
	}

	public static Component contentQ(Component parent, Map context, String content, Object... formatArgs) {
		content = X.f(content, formatArgs);
		try {
			return Executions.createComponentsDirectly(content, null, parent, context);
		} catch (Exception e) {
			e.printStackTrace();
			return Executions.createComponentsDirectly("stuff/error-simple.zul", null, parent, null);
		}
	}

	public static Component resourceQ(Component parent, String resourceFileName) {
		try {
			return Executions.createComponentsDirectly(new InputStreamReader(ZKC.class.getClassLoader().getResourceAsStream(resourceFileName)), null, parent, null);
		} catch (IOException e) {
			e.printStackTrace();
			return Executions.createComponentsDirectly("stuff/error-simple.zul", null, parent, null);

		}
	}

//	public static <T> T findComponent(Component com, Class<T> componentClass, int level, T... defRq) {
//		List<Component> coms = com.getChildren();
//		for (Component component : coms) {
//			if (componentClass.isAssignableFrom(component.getClass())) {
//				return (T) component;
//			}
//		}
//		while (level-- > 0) {
//			for (Component component : coms) {
//				Component
//			}
//		}
//
//	}

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

	public static void detachAll(HtmlBasedComponent com) {
		List<Component> children = com.getChildren();
		if (children == null) {
			return;
		}
		Iterator<Component> iterator = children.iterator();
		while (iterator.hasNext()) {
			Component c = iterator.next();
			c.detach();
			iterator.remove();
		}
	}

	public static Div createDiv(List<Component> zkComs) {
		Div center = new Div();
		for (Component c : zkComs) {
			center.appendChild(c);
		}
		return center;
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
		Audio audio = new Audio(){
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
		removeMeReturnParent(com);
		newParent.appendChild(com);
	}

	//	public static Component removeTargetParentComponent(Component removeMe, Class parentClass, boolean... required) {
//
//	}
	public static Component removeParentWindowForChild(Event event, boolean... required) {
		return removeTargetParentComponent(event.getTarget(), Window.class, required);
	}

	public static Component removeParentWindowForChild(Component comChild, boolean... required) {
		return removeTargetParentComponent(comChild, Window.class, required);
	}

	public static Component removeTargetParentComponent(Component removeMe, Class parentClass, boolean... required) {
		Component parent = null;
//		if(parentClass.isAssignableFrom(removeMe.getClass())){
//			return removeMeReturnParent(removeMe);
//		}
		do {
			parent = parent == null ? removeMe.getParent() : parent.getParent();
		} while (parent != null && !parentClass.isAssignableFrom(parent.getClass()));
		if (parent != null) {
			return removeMeReturnParent(parent);
		} else if (ARG.isDefEqTrue(required)) {
			throw new RequiredRuntimeException("Parent '%s' not found for component '%s'", RFL.scn(removeMe), RFL.scn(removeMe));
		}
		return parent;
	}

	public static Component removeMeReturnParentExt(Component removeMe) {
		removeMe.detach();
		return removeMeReturnParent(removeMe);
	}

	public static Component removeMeReturnParent(Component removeMe) {
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
		List<Component> allInPage = ZKCFinder.rootsByClass(scriptViewClass, true, ARR.EMPTY_LIST);
		allInPage.forEach(ZKC::removeMeReturnParent);
	}

	public static <C extends Component> void removeAllInWindow(Class<C> scriptViewClass) {
		List<Component> allInPage = ZKCFinder.findAllInWin(scriptViewClass, true, ARR.EMPTY_LIST);
		allInPage.forEach(ZKC::removeMeReturnParent);
	}
}
