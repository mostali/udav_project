package nett.appb;

import botcore.RouteAno;
import botcore.clb.BotCallback;
import botcore.clb.QuestState;
import lombok.SneakyThrows;
import mpu.str.TKN;
import mpc.str.condition.StringConditionType;
import mpc.str.sym.SYMJ;
import nett.Tgh;
import nett.msg.TgMsg;

import java.io.IOException;

@RouteAno(key = EchoRoute.CMD, eq = StringConditionType.STARTS)
public class EchoRoute extends TgRoute {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
//		Env.setAppName("tge");
		DefTgApp.startTgApp(EchoRoute.class.getPackage().getName());
	}

	public static final String CMD = "ping";

	public EchoRoute() {
		super();
	}

	public EchoRoute(TgRoute route) {
		super(route);
	}

	public static final TgCallback _CALLBACK_PINGPONG = TgCallback.of(SYMJ.ARROW_RIGHT + " Ping?");

	@Override
	public Object doUpdateMessage(String msgIn) {
		if ("ping".equals(msgIn)) {
			return TgMsg.of("pong").addKeyboardButton(_CALLBACK_PINGPONG.toKey());
		}
		String[] two = TKN.two(msgIn, " ", null);
		if (two == null) {
			int emsgId = sendLoadingAndGetEmsgId();
			return TgMsg.of("edited: pong" + msgIn.substring(CMD.length())).set_emsgId(emsgId);

		}
		return TgMsg.of("pong " + two[1]).addKeyboardButton(_CALLBACK_PINGPONG.toKey());
	}

	@SneakyThrows
	@Override
	public Object doUpdateCallback(BotCallback clb, String data2) {
		if (clb == _CALLBACK_PINGPONG) {
			setQuestState(QuestState.ofMap(_CALLBACK_PINGPONG, ""));
			return sendMsg_HTML(Tgh.i("Ready?"));
		}
		return super.doUpdateCallback(clb, data2);
	}

	@SneakyThrows
	@Override
	protected Object doUpdateQuest(QuestState questState, String questAnswer) {
		return "Pong " + questAnswer;
	}

}
