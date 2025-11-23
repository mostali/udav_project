package zk_os.notify;

import lombok.SneakyThrows;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mpe.core.ERR;
import mpu.X;
import mpu.core.MDate;
import mpu.core.QDate;
import mpu.str.TKN;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;
import zk_notes.AppNotesCore;

import java.util.List;
import java.util.concurrent.Semaphore;

public class NotifySrv {

	public static void addNotify(String msg, Exception ex) {
		Ctx10Db ctx10Db = getDb();
		String key = getNextKey(ctx10Db);
		ctx10Db.put(key, msg, ERR.getStackTrace(ex));
	}

	private static @NotNull Ctx10Db getDb() {
		return AppNotesCore.TREE_NOTIFY_GLOB();
	}

	static final Semaphore LOCK = new Semaphore(1);

	@SneakyThrows
	private static String getNextKey(Ctx10Db ctx10Db) {
		LOCK.acquire();
		String strKey = null;
		try {

			do {

				if (strKey == null) {
					strKey = MDate.formatDateToStringNow();
				}
				Ctx10Db.CtxModel10 modelByKey = ctx10Db.getModelByKey(strKey);
				if (modelByKey == null) {
					return strKey;
				}
				String[] two = TKN.two(strKey, 4);
				String key = two[0];
				String version = two[1];
				if (X.empty(version)) {
					strKey = key + "1";//first
				} else {
					strKey = two[0] + (UST.INT(version) + 1);
				}

			} while (true);

		} finally {
			LOCK.release();
		}
	}

	public static List<Ctx10Db.CtxModel10> getAll() {
		return getDb().getModels();
	}
}
