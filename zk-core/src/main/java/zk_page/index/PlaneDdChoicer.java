package zk_page.index;

import mpc.fs.UUFS;
import mpu.core.ARG;
import org.zkoss.zul.Window;
import mpe.call_msg.core.NodeID;
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

	public abstract void onChoiceSd3(String sd3);

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
			onChoiceSd3(p);
			return null;
		};

		Dd dd = Dd.ofDir(SpVM.get().subdomain3Rq(), sd3Map.keySet(), func);
		dd.onOK(p -> func.apply(dd.getValue()));
		appendChild(dd);

	}

	public Window openDefaultModalWindow(String winTitle) {
		return _title(winTitle)._closable()._modal()._showInWindow();
	}
}
