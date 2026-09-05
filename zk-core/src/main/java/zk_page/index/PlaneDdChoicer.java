package zk_page.index;

import mpc.fs.UUFS;
import mpu.core.ARG;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;
import mpe.cmsg.ns.NodeID;
import zk_com.base.Dd;
import zk_os.coms.AFC;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class PlaneDdChoicer extends BaseDdChoicer {

	private boolean withIndex = true;

	public PlaneDdChoicer withIndex(boolean... withIndex) {
		this.withIndex = ARG.isDefNotEqFalse(withIndex);
		return this;
	}

	public abstract void onChoicePlane(Event onPlaneSubmitEvent, String plane);

	@Override
	protected void init() {
		super.init();

		Set<Path> alLSd3 = AFC.PLANES.DIR_PLANES_LS_CLEAN(true);

		Map<String, Path> sd3Map = UUFS.toMapByFn(alLSd3);

		if (!withIndex) {
			sd3Map.remove(NodeID.PLANE_INDEX_ALIAS);
		}

		Function<String, Path> func = (p) ->
		{
			return null;
		};

		Dd dd = Dd.ofDir(SpVM.get().subdomain3Rq(), sd3Map.keySet(), func);
		dd.onOK(p -> onSubmitPage(p));
		appendChild(dd);

	}

	private void onSubmitPage(Event p) {
		onChoicePlane(p, ((Dd) p.getTarget()).getValue());
	}

	public Window openDefaultModalWindow(String winTitle) {
		return _title(winTitle)._closable()._modal()._showInWindow();
	}
}
