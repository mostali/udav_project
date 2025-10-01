package zk_page;

import mpc.exception.RequiredRuntimeException;
import mpu.core.ARG;
import org.zkoss.zk.ui.Component;

import java.util.*;

public class ZKCFinder {

	public static <C extends Component> List<C> find_inChilds(Component parent, Class<C> comClazz, boolean recursive, List<C>... defRq) {
		return find_inChilds(parent, comClazz, recursive, false, defRq);
	}

	public static <C extends Component> List<C> find_inChilds(Component parent, Class<C> comClazz, boolean recursive, boolean onlyFirst, List<C>... defRq) {
		Iterator<Component> it = parent.getChildren().iterator();
		List<C> coms = new ArrayList();
		while (it.hasNext()) {
			Component component = it.next();
			if (comClazz.isAssignableFrom(component.getClass())) {
				coms.add((C) component);
			}
			if (ARG.isDefEqTrue(recursive)) {
				List<C> comsIn = find_inChilds(component, comClazz, true, onlyFirst, Collections.EMPTY_LIST);
				coms.addAll(comsIn);
			}
		}
		if (!coms.isEmpty()) {
			return coms;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Component '%s' (recursive=%s) (onlyFirst=%s) not found from '%s'", comClazz, recursive, onlyFirst, parent), defRq);
	}

}
