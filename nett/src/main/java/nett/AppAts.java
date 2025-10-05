package nett;

import lombok.SneakyThrows;
import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import nett.appb.DefTgApp;
import nett.ats.ATS;
import org.telegram.telegrambots.meta.api.objects.Message;

public class AppAts {
	public static final String MSG_ERR_ILLEGAL_MSG = SYMJ.POINT_DBL + "Что-то иллегальное";

	@SneakyThrows
	public static Message sendAppMessage(long reciverId, String message, boolean... shutdown) {
		DefTgApp tgApp = DefTgApp.startTgApp();
		return ATS.sendMessage(tgApp.getRootRoute().getBotId(), null, reciverId, message, ARG.isDefEqTrue(shutdown), DefTgApp.USE_FREE_PROXY, tgApp.getRootRoute());
	}

}
