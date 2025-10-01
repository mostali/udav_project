package zk_page.index;

import mpc.fs.UUFS;
import org.zkoss.zul.Window;
import zk_com.base.Dd;
import zk_os.coms.AFC;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class PlaneDdChoicer extends BaseDdChoicer {

	public abstract void onChoiceSd3(String sd3);

	@Override
	protected void init() {
		super.init();

		Set<Path> alLSd3 = AFC.PLANES.DIR_PLANES_LS_CLEAN(true);

		Map<String, Path> sd3Map = UUFS.toMapByFn(alLSd3);

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
