package zk_page;

import mpc.fs.path.IPath;
import mpc.rfl.RFL;
import mpu.core.ARG;
import mpu.X;
import mpc.exception.RequiredRuntimeException;
import mpu.core.ARR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import zk_com.core.IReRender;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ZKCFinder {

	public static Predicate<IPath> BYPATH(Path path) {
		return (i -> i.toPath().equals(path));
	}

	/// /	public static AbstractDocumentAttributes findDoc(UUID guid, boolean onlyCurrentDesktop_or_All, boolean onlyFirstPage_or_All, boolean onlyRootCom_or_All) {
	/// /		List<AbstractBrowsePane> sfs = findSF(onlyCurrentDesktop_or_All, onlyFirstPage_or_All, onlyRootCom_or_All);
	/// /		if (X.empty(sfs)) {
	/// /			return null;
	/// /		}
	/// /		for (AbstractBrowsePane sf : sfs) {
	/// /			List docs = sf.getDocTableModel().getDataList();
	/// /			for (Object oDoc : docs) {
	/// /				if (!(oDoc instanceof AbstractDocumentAttributes)) {
	/// /					continue;
	/// /				}
	/// /				AbstractDocumentAttributes doc = (AbstractDocumentAttributes) oDoc;
	/// /				if (guid.equals(doc.getGlobalDocId())) {
	/// /					return doc;
	/// /				}
	/// /			}
	/// /		}
	/// /		return null;
	/// /	}
//
//	public static List<AbstractBrowsePane> findSF(DocTypeInfo docTypeInfo, boolean onlyCurrentDesktop_or_All, boolean onlyFirstPage_or_All, boolean onlyRootCom_or_All) {
//		List<AbstractBrowsePane> sfs = findSF(onlyCurrentDesktop_or_All, onlyFirstPage_or_All, onlyRootCom_or_All);
//		if (X.empty(sfs)) {
//			return sfs;
//		}
//		Iterator<AbstractBrowsePane> itSfs = sfs.iterator();
//		while (itSfs.hasNext()) {
//			AbstractBrowsePane sf = itSfs.next();
//			if (!docTypeInfo.equals(sf.getCurrentDocType())) {
//				itSfs.remove();
//			}
//		}
//		return sfs;
//	}

//	public static List<AbstractBrowsePane> findSF(boolean onlyCurrentDesktop_or_All, boolean onlyFirstPage_or_All, boolean onlyRootCom_or_All) {
//		return findCom(AbstractBrowsePane.class, onlyCurrentDesktop_or_All, onlyFirstPage_or_All, onlyRootCom_or_All);
//	}

	//	public static <C extends Component> List<C> findCom(Class<C> comClazz, boolean onlyCurrentDesktop_or_All, boolean onlyFirstPage_or_All, boolean onlyRootCom_or_All) {
//		if (onlyCurrentDesktop_or_All) {
//			Desktop desktop = Executions.getCurrent().getDesktop();
//			return findDesktopCom(desktop, comClazz, onlyFirstPage_or_All, onlyRootCom_or_All);
//		} else {
//			List<C> coms = new ArrayList<>();
//			//Executions.getCurrent().getDesktop().getSession().getNativeSession()
//			//((WebAppCtrl) WebApps.getCurrent()).getDesktopCache(Sessions.getCurrent());
//			for (Desktop desktop : GlobalDesktopCacheProvider.getDesktops().values()) {
//				coms.addAll(findDesktopCom(desktop, comClazz, onlyFirstPage_or_All, onlyRootCom_or_All));
//			}
//			return coms;
//		}
//	}
	public static <C extends Component> C findSingleParent(Component com, Class<C> comClazz, C... defRq) {
		List<C> parent = findParent(com, comClazz, null);
		if (parent != null && parent.size() == 1) {
			return parent.get(0);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Component Single Parent '%s' not found from component '%s' (size:%s)", comClazz, com, X.sizeOf(parent)), defRq);
	}

	public static <C extends Component> List<C> findParent(Component com, Class<C> comClazz, List<C>... defRq) {
		List totalComs = new ArrayList();
//		List rootComs = new ArrayList();
		Component next = com;
		while (true) {
			if (comClazz.isAssignableFrom(next.getClass())) {
				totalComs.add(next);
			}
			next = next.getParent();
			if (next == null) {
				break;
			}
		}
//		List<Component> childs = next != com ? next.getChildren() : null;
//		if (childs != null) {
//			childs.forEach((i) -> totalComs.addAll(findParent(i, comClazz, Collections.EMPTY_LIST)));
//		}

		if (!totalComs.isEmpty()) {
			return totalComs;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Component Next Parent '%s' not found from component '%s'", comClazz, com), defRq);
	}

	public static <C extends Component> List<C> findSibling(Component com, Class<C> comClazz, boolean nextOrPrev, List<C>... defRq) {
		List totalComs = new ArrayList();
		Component next = com;
		while (true) {
			if (comClazz.isAssignableFrom(next.getClass())) {
				totalComs.add(next);
			}
			next = nextOrPrev ? next.getNextSibling() : next.getPreviousSibling();
			if (next == null) {
				break;
			}
		}
		if (!totalComs.isEmpty()) {
			return totalComs;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Component Next Sibling '%s' not found from component '%s'", comClazz, com), defRq);
	}

	public static <C extends Component> List<C> findDesktopCom(Desktop desktopZK, Class<C> comClazz, boolean onlyFirstPage_or_All, boolean recursive_or_first) {
		if (onlyFirstPage_or_All) {
			Page page = desktopZK.getFirstPage();
			return findAll(page, comClazz, recursive_or_first);
		}
		List<C> coms = new ArrayList();
		for (Page page : desktopZK.getPages()) {
			coms.addAll(findAll(page, comClazz, recursive_or_first));
		}
		return coms;
	}

	public static <T> T findFirstIn_Page(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		return findFirstIn(true, clazz, recursive_or_first, defRq);
	}

	public static <T> T findFirstIn_Win(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		return findFirstIn(false, clazz, recursive_or_first, defRq);
	}

	public static <T> T findFirstIn(boolean inPage_or_inWin, Class<T> clazz, boolean recursive_or_first, T... defRq) {
		List<T> all;
		if (inPage_or_inWin) {
			all = findAllInRootsByClass((Class) clazz, recursive_or_first, (List) null);
		} else {
			all = findAllInWin((Class) clazz, recursive_or_first, (List) null);
		}
		if (X.notEmpty(all)) {
			return all.get(0);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("First component '%s' (recursive=%s) not found", clazz, recursive_or_first), defRq);
	}

	public static <C extends Component> List<C> findAllInRootsByClass(Class<C> comClazz, boolean recursive_or_first, List<C>... defRq) {
		return findAll(ZKC.getFirstPage(), comClazz, recursive_or_first, defRq);
	}

	public static <C extends Component> List<C> findAllInRootsByClasses(Collection<Class<C>> comClasses, boolean recursive_or_first, List<C>... defRq) {
		return findAll(ZKC.getFirstPage(), comClasses, recursive_or_first, defRq);
	}

	public static <C extends Component> List<C> findAllInWin(Class<C> comClazz, boolean recursive_or_first, List<C>... defRq) {
		return findAllFromParent(ZKC.getFirstWindow(), comClazz, recursive_or_first, defRq);
	}

	public static <C extends Component> List<C> findAll(Page src, Class<C> comClazz, boolean recursive_or_first, List<C>... defRq) {
		return findAll(src, ARR.asAD(comClazz), recursive_or_first, defRq);
	}

	public static <C extends Component> List<C> findAll(Page src, Collection<Class<C>> comClasses, boolean recursive_or_first, List<C>... defRq) {
		Iterator<Component> itRoots = src.getRoots().stream().iterator();
		List<C> totalComs = new ArrayList();
		while (itRoots.hasNext()) {
			Component component = itRoots.next();
			for (Class<C> comClazz : comClasses) {
				if (comClazz.isAssignableFrom(component.getClass())) {
					totalComs.add((C) component);
				} else if (recursive_or_first) {
					List<C> coms = findAllFromParent(component, comClazz, true, Collections.EMPTY_LIST);
					totalComs.addAll(coms);
				}
			}
		}
		if (!totalComs.isEmpty()) {
			return totalComs;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Component '%s' (recursive=%s) not found from src '%s'", comClasses, recursive_or_first, src), defRq);
	}

	public static <C extends Component> List<C> findAllFromParent(Component parent, Class<C> comClazz, boolean recursive, List<C>... defRq) {
		Iterator<Component> it = parent.getChildren().iterator();
		List<C> coms = new ArrayList();
		while (it.hasNext()) {
			Component component = it.next();
			if (comClazz.isAssignableFrom(component.getClass())) {
				coms.add((C) component);
			}
			if (ARG.isDefEqTrue(recursive)) {
				List<C> com = findAllFromParent(component, comClazz, true, Collections.EMPTY_LIST);
				coms.addAll(com);
			}
		}
		if (!coms.isEmpty()) {
			return coms;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Component '%s' (recursive=%s) not found from '%s'", comClazz, recursive, parent), defRq);
	}

	public static <C extends Component> C removeMeFirst(Class<C> comClass, boolean... recursive) {
		C c = ZKCFinder.findFirstIn_Win(comClass, ARG.isDefEqTrue(recursive), null);
		if (c != null) {
			ZKC.removeMeReturnParentExt(c);
		}
		return null;
	}

	public static <C extends IReRender> C rerenderFirst(Class<C> comClass, boolean... recursive) {
		C c = ZKCFinder.findFirstIn_Win(comClass, ARG.isDefEqTrue(recursive), null);
		if (c != null) {
			c.rerender();
		}
		return null;
	}

	public static <C extends Component> List<C> removeMeAllByFilenameInWin(Class<C> comClass, IPath path, boolean... recursive) {
		List allByFilename = ZKCFinder.findAllByFilename((Class) comClass, path, ARR.EMPTY_LIST);
		allByFilename.forEach(i -> {
			ZKC.removeMeReturnParentExt((Component) i);
		});
//		ZKComFinder.findAllInPage(Window.class,true).stream().flatMap(l->l.getChildren().stream()).collect(Collectors.toList())
		return allByFilename;
	}


	public static <C extends Component> List<C> removeMeAllInWin(Class<C> comClass, boolean checkWraperWin, boolean... recursive) {
		List<C> all = ZKCFinder.findAllInWin(comClass, ARG.isDefEqTrue(recursive), ARR.EMPTY_LIST);
		all.forEach(i -> {
			if (checkWraperWin) {
				ZKC.removeParentWindowForChild(i);
			} else {
				ZKC.removeMeReturnParentExt(i);
			}
		});
		return all;
	}


	public static <V extends IPath> List<V> findAllByFilename(Class<V> openViewClass, IPath fname, List<V>... defRq) {
		List<V> all = ZKCFinder.findAllInRootsByClass((Class) openViewClass, true, null);
		if (all != null) {
			return all.stream().filter(i -> fname.equals(i.toPath())).collect(Collectors.toList());
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Component's excepted by filename '%s'", fname), defRq);
	}

	public static <V extends IPath> V findByFilename(Class<V> openViewClass, Predicate<IPath> filter, V... defRq) {
		List<V> all = ZKCFinder.findAllInRootsByClass((Class) openViewClass, true, ARR.EMPTY_LIST);
		Optional<V> first = all.stream().filter(filter).findFirst();
		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("IPath '%s' not found", RFL.scn(openViewClass)), first, defRq);
	}

	//	public static <V extends IPath> V findByFormname_PPI(Pare sdn,Class<V> openViewClass, String formname, V... defRq) {
//		List<V> all = ZKComFinder.findAllInPage((Class) openViewClass, true, ARR.EMPTY_LIST);
//		Path pathOfFormNotePpi = AppNotes.getPathOfFormNote_PPI(formname);
//		Optional<V> first = all.stream().filter(c -> c.fPath().equals(pathOfFormNotePpi)).findFirst();
//		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("SeWin '%s' not found", formname), first, defRq);
//	}
//	public static void refresh(Object source) {
//		final RefreshScrollerEvent refreshScrollerEvent = new RefreshScrollerEvent(source);
//		EventUtils.postEvent(refreshScrollerEvent);
//	}

//	public static abstract class ZkWalker<C extends Component> {
//		boolean onlyCurrentDesktop_or_All = false;
//		boolean onlyFirstPage_or_All = false;
//		boolean onlyRootCom_or_All = false;
//
////		public ZkWalker setOnlyCurrentDesktop_or_All(boolean onlyCurrentDesktop_or_All) {
////			this.onlyCurrentDesktop_or_All = onlyCurrentDesktop_or_All;
////			return this;
////		}
////		public ZkWalker setOnlyFirstPage_or_All(boolean onlyFirstPage_or_All) {
////			this.onlyFirstPage_or_All = onlyFirstPage_or_All;
////			return this;
////		}
////		public ZkWalker setOnlyRootCom_or_All(boolean onlyRootCom_or_All) {
////			this.onlyRootCom_or_All = onlyRootCom_or_All;
////			return this;
////		}
//
//		public ZkWalker setOnlyRootCom_or_All(boolean onlyCurrentDesktop_or_All, boolean onlyFirstPage_or_All, boolean onlyRootCom_or_All) {
//			this.onlyCurrentDesktop_or_All = onlyCurrentDesktop_or_All;
//			this.onlyFirstPage_or_All = onlyFirstPage_or_All;
//			this.onlyRootCom_or_All = onlyRootCom_or_All;
//			return this;
//		}
//
//		public void walk(Class<C> clazz) {
////			if(onlyCurrentDesktop_or_All){
////				walk(Executions.getCurrent());
////			}
////			for (org.zkoss.zk.ui.Desktop desktop : GlobalDesktopCacheProvider.getDesktops().values()) {
////				walk(desktop, clazz);
////			}
//		}
//
//		public void walk(Desktop desktop, Class<C> clazz) {
////			List<C> l = findRootCom(desktop, clazz);
////			if (U.empty(l)) {
////				return;
////			}
////			for (C c : l) {
////				nextComponent(desktop, c);
////			}
//		}
//
//		public abstract void nextComponent(Desktop desctop, C com);
//	}

}
