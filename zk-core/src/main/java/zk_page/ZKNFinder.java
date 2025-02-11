package zk_page;


import lombok.RequiredArgsConstructor;
import mpc.arr.STREAM;
import mpu.core.ARG;
import mpu.core.ARR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import zk_notes.coms.NoteTbxm;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.control.NodeLn;
import zk_notes.node.NodeDir;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ZKNFinder {

	private static Collection<Class<Component>> comClasses = (Collection) ARR.asAD(NodeLn.class);
	private static Collection<Class<Component>> formClasses = (Collection) ARR.asAD(NoteTbxm.class, SeNoteTbxm.class);

	public static List<Component> findAllNodeCom(boolean isForm, boolean unwrapIfInWindow) {
		return findAllNodeCom(null, isForm, unwrapIfInWindow);
	}

	public static List<Component> findAllNodeCom(NodeDir nodeDir, boolean isForm, boolean unwrapIfInWindow) {
		Collection<Class<Component>> findClasses = isForm ? formClasses : comClasses;
		List<Component> allInRootsByClasses = ZKCFinder.findAllInRootsByClasses(findClasses, true, ARR.EMPTY_LIST);
		allInRootsByClasses = STREAM.toList(allInRootsByClasses, n -> {
			if (isForm) {
				if (n instanceof NoteTbxm && (nodeDir == null || ((NoteTbxm) n).getFormName().equals(nodeDir.nodeName()))) {
					return true;
				}
			} else {
				if (n instanceof NodeLn && (nodeDir == null || ((NodeLn) n).getFormName().equals(nodeDir.nodeName()))) {
					return true;
				}
			}
			return false;
		});
		if (unwrapIfInWindow) {
			allInRootsByClasses = STREAM.mapToList(allInRootsByClasses, c -> c.getParent() instanceof Window ? c.getParent() : c);
		}
		return allInRootsByClasses;
	}

	//
	//

	public static <C extends Component> List<C> findAllWindowComInRoots_andRemove(Class<C> comClass, Predicate<C> test, boolean... recursive) {
		List<C> allInPage = (List<C>) ZKCFinder.findAllInRootsByClass(comClass, ARG.isDefEqTrue(recursive), ARR.EMPTY_LIST).stream().filter(test).collect(Collectors.toList());
		allInPage.forEach(ZKC::removeParentWindowForChild);
		return allInPage;
	}

}
