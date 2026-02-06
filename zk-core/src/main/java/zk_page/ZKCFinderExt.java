package zk_page;

import mpc.exception.RequiredRuntimeException;
import mpc.fs.path.IPath;
import mpc.rfl.RFL;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import zk_com.core.IReRender;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ZKCFinderExt {

	//
	// ----------------------- FIND BY PARENT or SIBLING --------------------------
	//

	public static <C extends Component> List<C> findByParent(Component com, Class<C> comClazz, List<C>... defRq) {
		List totalComs = new ArrayList();
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
		return !totalComs.isEmpty() ? totalComs : ARG.toDefThrow(() -> new RequiredRuntimeException("Component Next Parent '%s' not found from component '%s'", comClazz, com), defRq);
	}

	public static <C extends Component> List<C> findBySibling(Component com, Class<C> comClazz, boolean nextOrPrev, boolean onlyFirst, List<C>... defRq) {
		List totalComs = new ArrayList();
		Component next = com;
		while (true) {
			if (comClazz.isAssignableFrom(next.getClass())) {
				totalComs.add(next);
			}
			if (onlyFirst) {
				return totalComs;
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

	//
	// ----------------------- FIND ALL IN --------------------------
	//

	public static <C extends Component> List<C> find_inDesktop(Desktop desktopZK, Class<C> comClazz, boolean onlyFirstPage_or_All, boolean recursive_or_first) {
		if (onlyFirstPage_or_All) {
			Page page = desktopZK.getFirstPage();
			return findAll_inPage0(page, comClazz, recursive_or_first);
		}
		List<C> coms = new ArrayList();
		for (Page page : desktopZK.getPages()) {
			coms.addAll(findAll_inPage0(page, comClazz, recursive_or_first));
		}
		return coms;
	}

	public static <T> T findFirst_inPage0(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		return findFirst_inPage0_or_Win0(true, clazz, recursive_or_first, defRq);
	}

	public static <T> T findFirst_inWin0(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		return findFirst_inPage0_or_Win0(false, clazz, recursive_or_first, defRq);
	}

	public static <T> T findFirst_inPage0_or_Win0(boolean inPage_or_inWin, Class<T> clazz, boolean recursive_or_first, T... defRq) {
		List<T> all;
		if (inPage_or_inWin) {
			all = findAll_inPage0((Class) clazz, recursive_or_first, (List) null);
		} else {
			all = findAllInFirstWin0((Class) clazz, recursive_or_first, (List) null);
		}
		if (X.notEmpty(all)) {
			return all.get(0);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("First component '%s' (recursive=%s) not found", clazz, recursive_or_first), defRq);
	}

	public static <C extends Component> List<C> findAll_inPage0(Class<C> comClazz, boolean recursive_or_first, List<C>... defRq) {
		return findAll_inPage0(ARR.as(comClazz), recursive_or_first, defRq);
	}

	public static <C extends Component> List<C> findAll_inPage0(Collection<Class<C>> comClasses, boolean recursive_or_first, List<C>... defRq) {
		return findAll_inPage0(ZKC.getFirstPage(), comClasses, recursive_or_first, defRq);
	}

	public static <C extends Component> List<C> findAllInFirstWin0(Class<C> comClazz, boolean recursive_or_first, List<C>... defRq) {
		return ZKCFinder.find_inChilds(ZKC.getFirstWindow(), comClazz, recursive_or_first, defRq);
	}

	public static <C extends Component> List<C> findAll_inPage0(Page src, Class<C> comClazz, boolean recursive_or_first, List<C>... defRq) {
		return findAll_inPage0(src, ARR.asAL(comClazz), recursive_or_first, defRq);
	}

	public static <C extends Component> List<C> findAll_inPage0(Page src, Collection<Class<C>> comClasses, boolean recursive_or_first, List<C>... defRq) {
		Iterator<Component> itRoots = src.getRoots().stream().iterator();
		List<C> totalComs = new ArrayList();
		while (itRoots.hasNext()) {
			Component component = itRoots.next();
			for (Class<C> comClazz : comClasses) {
				if (comClazz.isAssignableFrom(component.getClass())) {
					totalComs.add((C) component);
				} else if (recursive_or_first) {
					List<C> coms = ZKCFinder.find_inChilds(component, comClazz, true, Collections.EMPTY_LIST);
					totalComs.addAll(coms);
				}
			}
		}
		if (!totalComs.isEmpty()) {
			return totalComs;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Component '%s' (recursive=%s) not found from src '%s'", comClasses, recursive_or_first, src), defRq);
	}

	//
	// ----------------------- RERENDER --------------------------
	//

	public static <C extends IReRender> C rerenderFirst(Class<C> comClass, boolean... recursive) {
		C c = (C) findFirst_inWin0((Class) comClass, ARG.isDefEqTrue(recursive), null);
		if (c != null) {
			c.rerender();
		}
		return null;
	}

	public static <T> T findFirst_InPage(Class<T> clazz, boolean recursive_or_first, T... defRq) {
		return findFirst_inPage0(clazz, recursive_or_first, defRq);
	}

	//
	//
	//

	public static class RMM_EXPERIMENTAL {

		public static <C extends Component> C removeMeFirst(Class<C> comClass, boolean... recursive) {
			C c = findFirst_inWin0(comClass, ARG.isDefEqTrue(recursive), null);
			if (c != null) {
				ZKC.removeMeReturnParentExt(c);
			}
			return null;
		}

		public static <C extends Component> List<C> removeMeAllByFilenameInWin(Class<C> comClass, IPath path, boolean... recursive) {
			List allByFilename = findAllByFilename((Class) comClass, path, ARR.EMPTY_LIST);
			allByFilename.forEach(i -> {
				ZKC.removeMeReturnParentExt((Component) i);
			});
			return allByFilename;
		}

		public static <C extends Component> List<C> removeMeAllInWin(Class<C> comClass, boolean checkWraperWin, boolean... recursive) {
			List<C> all = findAllInFirstWin0(comClass, ARG.isDefEqTrue(recursive), ARR.EMPTY_LIST);
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
			List<V> all = findAll_inPage0((Class) openViewClass, true, null);
			if (all != null) {
				return all.stream().filter(i -> fname.equals(i.toPath())).collect(Collectors.toList());
			}
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Component's excepted by filename '%s'", fname), defRq);
		}

		public static <V extends IPath> V findByFilename(Class<V> openViewClass, Predicate<IPath> filter, V... defRq) {
			List<V> all = findAll_inPage0((Class) openViewClass, true, ARR.EMPTY_LIST);
			Optional<V> first = all.stream().filter(filter).findFirst();
			return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("IPath '%s' not found", RFL.scn(openViewClass)), first, defRq);
		}
	}

}
