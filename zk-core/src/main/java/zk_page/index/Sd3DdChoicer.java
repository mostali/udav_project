package zk_page.index;

import lombok.Getter;
import mpc.fs.UFS;
import mpu.X;
import mpu.pare.Pare;
import zk_com.base.Cb;
import zk_com.base.Dd;
import zk_os.AFC;
import zk_page.core.SpVM;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class Sd3DdChoicer extends BaseDdChoicer {

	public abstract void onChoiceSd3(String sd3);

	@Override
	protected void init() {
		super.init();

		Set<Path> alLSd3 = AFC.PLANES.DIR_PLANES_LS_CLEAN(true);

		Map<String, Path> sd3Map = UFS.toMapByFn(alLSd3);

		Function<String, Path> func = (p) ->
		{
			onChoiceSd3(p);
			return null;
		};

		Dd dd = Dd.ofDir(SpVM.get().subdomain3Rq(), sd3Map.keySet(), func);
		dd.onOK(p -> func.apply(dd.getValue()));
		appendChild(dd);

	}

}
