package zk_page.index;

import mpc.fs.UFS;
import zk_com.base.Dd;
import zk_com.base_ctr.Div0;
import zk_os.AFC;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class Sd3DdChoicer extends Div0 {

	public abstract void onChoicePath(String sd3);

	@Override
	protected void init() {
		super.init();

		List<Path> alLSd3 = AFC.DIR_PLANES_LS_CLEAN();

		Map<String, Path> sd3Map = UFS.toMap(alLSd3);

		Function<String, Path> func = (p) ->
		{
			onChoicePath(p);
			return null;
		};

		Dd dd = Dd.ofDir(SpVM.get().subdomain3(), sd3Map.keySet(), func);
		appendChild(dd);


	}
}
