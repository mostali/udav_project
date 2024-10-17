package nett;

import mpu.core.ARR;
import mpu.str.UST;
import nett.ats.ATS;
import org.telegram.telegrambots.meta.api.objects.Message;
import mpu.IT;
import mpc.env.Env;
import mpc.types.opts.SeqOptions;
import mpc.log.L;

import java.net.MalformedURLException;
import java.net.URL;

public class NettMod {

	public static SeqOptions runOpts = null;

	public static void main(String[] args) throws MalformedURLException {

		runOpts = SeqOptions.of(args);

		String botId = runOpts.getSingle(ARR.of("i", "id"), null);
		String botToken = runOpts.getSingle(ARR.of("t", "token"), null);
		if (botId == null && botToken == null) {
			if (L.isInfoEnabled()) {
				L.info("BotId/BotToken[-i/-t] not found from run-args. Try use home dir [{}/otr/dav]", Env.PD_ENV_TLP);
			}
			botId = Env.EDIR.FILEVAR_TLP.readStrRq("nett/bt/gts/i");
			botToken = Env.EDIR.FILEVAR_TLP.readStrRq("nett/bt/gts/t");
		} else {
			if (L.isInfoEnabled()) {
				L.info("BotId/BotToken[-i/-t] found from run-args.");
			}
			IT.notEmpty(botId, "botId");
			IT.notEmpty(botToken, "botToken");
		}

		long reciverId = IT.isLong0(runOpts.getSingle(ARR.of("r", "reciver")));
		String message = IT.notEmpty(runOpts.getSingle(ARR.of("m", "message")));
		URL photo_url = UST.URL(runOpts.getSingle(ARR.of("pu", "photo_url"), null));

		Message m = null;
		if (photo_url == null) {
			m = sendMessageAndStop(botId, botToken, reciverId, message);
		} else {
			m = sendMessageWithPhotoWithStop(botId, botToken, reciverId, message, photo_url);
		}
		if (L.isInfoEnabled()) {
			L.info("Message [{}] sended to [{}] is successful.", m.getMessageId(), m.getChatId());
		}
	}

	public static Message sendMessageWithPhotoWithStop(String botDomain, String botToken, long reciverId, String message, URL urlPhoto) {
		Message m = ATS.sendPost(botDomain, botToken, reciverId, message, urlPhoto, true, null);
		return m;
	}

	public static Message sendMessageAndStop(String botDomain, String botToken, long reciverId, String message) {
		Message m = ATS.sendMessage(botDomain, botToken, reciverId, message, true, false, null);
		return m;
	}
}
