package zk_page;

import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZKF {
	int levelMax = 0;
	int levelCur = 0;

	//

	public static Collection<Component> roots(Integer... page) {
		Desktop desktop = Executions.getCurrent().getDesktop();
		if (ARG.isNotDef(page)) {
			return desktop.getFirstPage().getRoots();
		}
		return ARRi.item(desktop.getPages(), ARG.toDefOr(0, page)).getRoots();
	}

	//
	//

	public static List<Component> roots0(Predicate<Component> test) {
		return new ZKF().findAll(roots(), test);
	}

	public static <C extends Component> List<C> roots0c(Class<C> comClass, Predicate<C> test, boolean recursive, List<C>... defRq) {
		return ZKCFinder.rootsByClass(comClass, ARG.isDefEqTrue(recursive), defRq).stream().filter(test).collect(Collectors.toList());
	}

	//
	// REMOVE

	public static <C extends Component> List<C> roots0c_remove(Class<C> comClass, Predicate<C> test, boolean... recursive) {
		List<C> allInPage = roots0c(comClass, test, ARG.isDefEqTrue(recursive), ARR.EMPTY_LIST);
		allInPage.forEach(ZKC::removeMeReturnParent);
		return allInPage;
	}

	public static <C extends Component> List<C> roots0c_remove_wc(Class<C> comClass, Predicate<C> test, boolean... recursive) {
		List<C> allInPage = roots0c(comClass, test, ARG.isDefEqTrue(recursive), ARR.EMPTY_LIST);
		allInPage.forEach(ZKC::removeParentWindowForChild);
		return allInPage;
	}

	public static List<Component> roots0_remove(Predicate<Component> test) {
		List<Component> all = roots0(test);
		all.forEach(ZKC::removeMeReturnParent);
		return all;
	}

	//
	//


	public List<Component> findAll(Collection<Component> from, Predicate<Component> test) {
		return from.stream().filter(test).collect(Collectors.toList());
	}

	public List<Component> findAllChilds(Collection<Component> from, Predicate<Component> test) {
		Stream<Component> stream = from.stream().flatMap(c -> c.getChildren().stream());
		if (test != null) {
			stream = stream.filter(test);
		}
		return stream.collect(Collectors.toList());
	}
}
