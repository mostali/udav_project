package zk_notes.node_srv.types;

import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.exception.FIllegalStateException;
import mpc.log.LogTailReader;
import mpc.map.MAP;
import mpc.rfl.RFL;
import mpe.NT;
import mpe.call_msg.SendCallMsg;
import mpe.call_msg.core.INode;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare3;
import mpu.str.SPLIT;
import nett.appb.TgApp;
import nett.ats.SendMessage0;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import zk_notes.node.NodeDir;
import mpe.call_msg.injector.TrackMap;
import zk_notes.node_srv.NodeEvalType;
import zk_os.AppZos;
import zk_os.db.WebUsrService;
import zk_os.db.net.WebUsr;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SendMsgECS {

	public static final Logger L = LoggerFactory.getLogger(SendMsgECS.class);

	@SneakyThrows
	public static String doSendMsg_AsyncLog_Simple(NodeDir node) {

		NodeEvalType nodeEvalType = node.evalType(false, null);
		switch (nodeEvalType) {
			case SENDMSG:
				break;
			default:
				throw new FIllegalStateException("SendMessage not support '%s'", nodeEvalType);
		}

		Pare3<Object, Throwable, String> rsp = doSendMsg_AsyncLog(node, null);

		if (rsp.hasVal()) {
			throw rsp.val();
		}
		if (L.isInfoEnabled()) {
			L.info("Rslt Object doSendMsg_AsyncLog:" + rsp);
		}
//		return X.blank(rsp.ext()) ? rsp.key() + "" : rsp.ext();
		return X.blank(rsp.ext()) ? rsp.key() + "" : rsp.ext();
	}

	public static Pare3<Object, Throwable, String> doSendMsg_AsyncLog(INode<NodeDir> inject, TrackMap.TrackId track) {

//		NodeDir nodeDir = inject.nodeDir;

//		String nodeDataVal = inject.newInjected(track).nodeDataStr;

		String nodeDataVal = inject.inject(track).nodeDataStr;

		SendCallMsg sendCallMsg = SendCallMsg.of(nodeDataVal);

		sendCallMsg.updateMsgWithBodyIfEmpty();

		switch (sendCallMsg.type()) {
			case EMAIL:
				return LogTailReader.doAsyncLogOperation(() -> EMAIL_send(inject.toNodeImpl(), SendCallMsg.of(nodeDataVal)));
			case TG:
				return TG_send(sendCallMsg);
			default:
				throw new FIllegalStateException("SendMessage not support '%s'", sendCallMsg.type());
		}

	}

	private static @NotNull Pare3 TG_send(SendCallMsg sendCallMsg) {
		String toTg = sendCallMsg.getToTg(null);
		if ("0".equals(toTg)) {
			toTg = WebUsr.get().getId() + "";
		}

		List<String> toChats = SPLIT.allByComma(toTg);

		Supplier tgSenderFunc = () -> {
			List l = new LinkedList();
			for (String toChat : toChats) {
				try {
					Map headersMap = sendCallMsg.getHeaders_MAP();
					String parseMode = MAP.getAsString(headersMap, "parse", null);
					Integer msgId = tg_sendMsgHtmlFromBeaUser(toChat, sendCallMsg.getMsgOrBody(), parseMode);
					l.add(msgId);
				} catch (Exception ex) {
					L.error("tg_sendMsgFromBeaUser:" + toChat, ex);
				}
			}
			return l;
		};

		return LogTailReader.doAsyncLogOperation(tgSenderFunc);
	}

	@SneakyThrows
	public static Object EMAIL_send(NodeDir node, SendCallMsg sqlCallMsgPrepared) {
		RFL.JarCall jarCall = RFL.JCT.MAIL.newJarCall();
		String[] headersSeqargs = sqlCallMsgPrepared.getHeaders_SEQARGS();
		sqlCallMsgPrepared.updateMsgWithBodyIfEmpty();
		headersSeqargs = sqlCallMsgPrepared.getHeaders_SEQARGS();
		Object rspInvoke = jarCall.invokeArgs(headersSeqargs);
		return rspInvoke;
	}

//	public static Integer tg_sendMsgFromBeaUser(String msgVal, Integer... defRq) {
//		WebUsr webUsr = WebUsr.get();
//		return tg_sendMsgFromBeaUser(webUsr.getId(), msgVal, defRq);
//	}

	public static Integer tg_sendMsgHtmlFromBeaUser(Long userId, String msgVal, Integer... defRq) {

		WebUsr webUsr = WebUsrService.get().loadUserById(userId);
//		WebUsr webUsr = WebUsrService.get().loadUserById(userId);

		String chatId = webUsr.isMainRole_OWNER() ? APP.getTgBotOwnerId() : webUsr.getNidByNet(NT.TG) + "";

		TgApp appBot = AppZos.TgApp;
		if (appBot != null) {
			Message msg = AppZos.TgApp.getRootRoute().sendMessage(new SendMessage(chatId, msgVal));
			L.info("TgMessage#{}  sended to chatId#{}:{}:", msg.getMessageId(), webUsr.getFirst_name(), chatId);
			return msg.getMessageId();
		}
		//app is null
		L.warn("TgMessage NOT sended (Tg Bot Off) to chatId#{}:{}:", webUsr.getFirst_name(), chatId);
		return ARG.toDefThrowMsg(() -> X.f("Set Tg App"), defRq);
//		Sys.say("tgsenddd");

	}

	public static Integer tg_sendMsgHtmlFromBeaUser(String chatId, String msgVal, Integer... defRq) {
		return tg_sendMsgHtmlFromBeaUser(chatId, msgVal, "HTML", defRq);
	}

	public static Integer tg_sendMsgHtmlFromBeaUser(String chatId, String msgVal, String parseMode, Integer... defRq) {
		TgApp appBot = AppZos.TgApp;
		if (appBot != null) {
			SendMessage0 simpleSendMessage = SendMessage0.of(chatId, msgVal, parseMode);
			Message msg = AppZos.TgApp.getRootRoute().sendMessage(simpleSendMessage);
			return msg.getMessageId();
		}
		return ARG.toDefThrowMsg(() -> X.f("Set Tg App"), defRq);
	}
}
