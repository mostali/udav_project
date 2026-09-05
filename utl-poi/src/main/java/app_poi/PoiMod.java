package app_poi;

import lombok.SneakyThrows;
import mpc.exception.WhatIsTypeException;
import mpe.NT;
import mpf.zbin.ZBin;
import mpe.cmsg.std_ext.XlsCallMsg;
import mpe.str.table.Matrix;
import mpf.zcall.ZType;
import mpu.IT;
import mpu.X;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ZType.ZTypeAno(app = "poi", version = "1")
public class PoiMod {

	public static final Logger L = LoggerFactory.getLogger(PoiMod.class);

	public static void main(String[] args) {
		NT.BEA.set();
		List<List> list = (List<List>) ZBin.POI.invokeMsg("TREAD_AT /home/dav/pjm/utl-poi/src/main/java/app_poi/db.xlsx");

//		XlsCallMsg.of()
//		List<List> list = (List<List>) ZBin.POI.invokeMsg("tread:/home/dav/pjm/utl-poi/src/main/java/utl_poi/db.xlsx");

		List<List> lists = Matrix.trimAll(list, false);

		X.exit(lists);

	}

	@SneakyThrows
	public static <T> T invokeArgs(Object... args) {

		IT.hasLength(args, 3, "set msg+table+sheetname");

		XlsCallMsg xlsCallMsg = XlsCallMsg.ofAny(args[0], true);

		List<List> table = (List<List>) args[1];

		return invokeMsgImpl(xlsCallMsg, table, (String) args[2]);

	}

	@SneakyThrows
	public static <T> T invokeMsg(Object msg) {
		XlsCallMsg msvCallMsg = XlsCallMsg.ofAny(msg, true);
		return invokeMsgImpl(msvCallMsg, null, null);

	}

	private static <T> @NotNull T invokeMsgImpl(XlsCallMsg xlsCallMsg, List<List> table, String sheetName) {

		L.info("invokeMsg\n{} ", xlsCallMsg.getMsg());

		XlsCallMsg.Method method = xlsCallMsg.method;

		switch (method) {

			case TREAD: {
//				IT.isInt(sheetName,"except sheetIndex");
				List<List> ll = MatrixWb.toListList_Complete_String(xlsCallMsg.getPath(), UST.INT(sheetName,0));
				return (T) ll;
			}


			case TREAD_AT: {
//				IT.isInt(sheetName,"except sheetIndex");
				List<List> ll = MatrixWb.toListList_Complete_AT(xlsCallMsg.getPath(), UST.INT(sheetName,0));
				return (T) ll;
			}

			case TWRITE: {

				Wb0.writeSheetToFile(xlsCallMsg.getPath(), IT.NN(table), "");

				return (T) "OK";
			}

			default:
				throw new WhatIsTypeException(method);

		}
	}


}
