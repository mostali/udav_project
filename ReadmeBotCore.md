# Bot Java Framework
Фрэймфорк для создания ботов Telegram & Vk

## 📚 Описание
Для создания бота необходимо создать 1 класс, route, с антоацией описывающей тип принимаемых сообщений (соотвествие маске) и соотв. методы для их обработки

_Запрос к боту бывает 3-х типов:_
1. Сообщение (doUpdateMessage) - когда пользователь отправил сообщение
2. Колбэк (doUpdateCallback) - когда мы отправили пользователю сообщение c кнопками и ждем от него нажатие на кнопку (колбэк)
3. Вопрос (doUpdateQuest) - когда мы отправили пользователю сообщение и ждем от него ответ (первое пришедшее сообщение)

_Алгоритм обработки запроса:_
- Анализируем сообщение, ищем по маске нужный Route
- На каждый перехваченый роутом запрос создается отдельный экземпляр класса такого роута, который уже далее вызывает соотв. методы

## ✅ Quick Start

```javascript
//Инициируем Telegram Bot Instance по заданным кредам

//Для запуска бота должны быть заданы свойста бота ID & Token, задаются свйоствами 'tg.bt.id' & 'tg.bt.tk'
//Если задано свойство `tg.bt.owner.id` - админу будет отправлено сообщение о старте

//Запускаем бота 
//скан пакетов для регистрации нового демо-route
public static void main(String[] args) throws IOException, ClassNotFoundException {
    DefTgApp.startTgApp(EchoRoute.class.getPackage().getName(), ... );
}

//Демо бот - "ping & pong"
//отвечает на все сообщение начинающиеся со cлова ping
@RouteAno(key = EchoRoute.CMD, eq = StringConditionType.STARTS)
public class EchoRoute extends TgRoute {

	public static final String CMD = "ping";

	public EchoRoute() {
		super();
	}

	public EchoRoute(TgRoute route) {
		super(route);
	}

	public static final TgCallback _CALLBACK_PINGPONG = TgCallback.of(SYMJ.ARROW_RIGHT + " Ping?");

	@Override
	public Object doUpdateMessage(String msg) {
		if ("ping".equals(msg)) {
			return TgMsg.of("pong").addKeyboardButton(_CALLBACK_PINGPONG.toKey());
		}
		String[] two = USToken.two(msg, " ", null);
		if (two == null) {
			int emsgId = getEmsgId();
			return TgMsg.of("edited: pong" + msg.substring(CMD.length())).emsgId(emsgId);
		}
		return TgMsg.of("pong " + two[1]).addKeyboardButton(_CALLBACK_PINGPONG.toKey());

	}

	@SneakyThrows
	@Override
	public Object doUpdateCallback(BotCallback clb, String data2) {
		if (clb == _CALLBACK_PINGPONG) {
			setQuestState(QuestState.ofMap(_CALLBACK_PINGPONG, ""));
			return sendMsg(Tgh.i("Ready?"));
		}
		return super.doUpdateCallback(clb, data2);
	}

	@SneakyThrows
	@Override
	protected Object doUpdateQuest(QuestState questState, String questAnswer) {
		return "Pong " + questAnswer;
	}

}

```