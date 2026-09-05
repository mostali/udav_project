package mpf.ngrid;

import lombok.RequiredArgsConstructor;
import mpc.env.APP;
import mpc.log.L;
import mpf.zbin.ZBin;
import mpu.core.ARG;
import mpu.core.ARR;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class SheetModel {

	public final String sheetGuid;

	public final String sheetName;

	private List<List> readedRows;

	public Function<List<List>, Object> writer() {
		return (ll) -> ZBin.XD.invokeArgs(APP.getPathGdKey(), sheetGuid, sheetName, ll);
	}

	public Supplier<List<List>> reader(boolean... fresh) {
		return () -> {
			if (ARG.isDefEqTrue(fresh)) {
				readedRows = null;
			}
			return readedRows != null ? readedRows : (readedRows = (List) ZBin.XD.invokeArgs(APP.getPathGdKey(), sheetGuid, sheetName));
		};
	}

	public Object clear() {
		L.info("Clear sheet [ {} -> {} ]", sheetGuid, sheetName);
		return writer().apply((List) ARR.as2());
	}

}
