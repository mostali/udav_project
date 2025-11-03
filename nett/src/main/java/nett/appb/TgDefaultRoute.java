package nett.appb;

import lombok.Setter;
import lombok.SneakyThrows;
import mp.utl_odb.query_core.QP;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.IT;
import mpu.core.ENUM;
import mpe.str.CN;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mpc.types.abstype.AbsType;
import mpc.env.boot.BootRunUtils;
import mpc.fs.fd.RES;
import mpc.rfl.RFL;
import mpu.str.UST;
import mpu.str.TKN;
import mpu.core.QDate;
import mp.utl_odb.netapp.mdl.NetActivityModel;
import mp.utl_odb.mdl.AModel;
import nett.Tgc;
import nett.Tgh;
import botcore.BotRoute;
import botcore.RouteAno;
import nett.TgDefaultFileDownloader;
import nett.msg.TgMsg;
import nett.msg.TgMsgBuilder;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RouteAno
public class TgDefaultRoute extends TgRoute {

	public static final String FILE_START_MD = "bot.start.md";
	public static final String FILE_HELP_MD = "bot.help.md";

	@Setter
	private TgDefaultFileDownloader fileDownloader = new TgDefaultFileDownloader(this);

	protected TgDefaultRoute() {
		super();
	}

	protected TgDefaultRoute(TgRoute route) {
		super(route);
	}

	@Override
	protected boolean isDefaultRoute() {
		return true;
	}

	public String loadFileContent(String fileFromRunLoctionOrResources, boolean... copyToRL) {
		return RES.loadFileFromRunLocationOrResources(getTgApp().getClass(), fileFromRunLoctionOrResources, ARG.isDefEqTrue(copyToRL), null);
	}

	public ReplyKeyboardMarkup getMainMenu(String chatId) {
		return null;
	}

	@Override
	public Object doUpdateMessage(String msgIn) {
		TgMsg tgMsg = null;
		Document document = Tgc.getDocument(getUpdate().update);
		if (document != null) {
			return doDownoadDocument(document);
		}
		String[] two = Tgc.getMessageAndCallback(getUpdate().update);
		if (two[1] != null) {
			tgMsg = getTgMsg_CallbackNotFound(two[1]);
		} else {

			tgMsg = beforeUpdateMessage(two[0]);
			if (tgMsg != null) {
				return tgMsg;
			}

			switch (two[0]) {
				case "/":
					tgMsg = getTgMsg_ROUTES();
					break;
				case "/start":
					tgMsg = getTgMsg_START();
					break;
				case "/help":
				case "Help..":
					tgMsg = getTgMsg_HELP();
					break;
				default:

					if (!isAdmin()) {
						return getTgMsg_RouteNotFound();
					}

					if (two[0].startsWith("///")) {
						String key = two[0].substring(3);
						tgMsg = choiceAdminTgMsg(key);
					}

					if (tgMsg != null) {
						return tgMsg;
					}

					tgMsg = afterUpdateMessage(two[0]);

					if (tgMsg == null) {
						tgMsg = getTgMsg_RouteNotFound(two[0]);
					}
					break;
			}
		}
		return sendMsg(tgMsg);
	}

	protected TgMsg beforeUpdateMessage(String s) {
		return null;
	}

	protected TgMsg afterUpdateMessage(String s) {
		return null;
	}

	@SneakyThrows
	private Object doDownoadDocument(Document document) {
		return fileDownloader.doDownalod(document);
	}

	protected String getValidationErrorForDownloadDocument(Document document) {
		return null;//"default app - download is deprecated";
	}

	public TgMsg getMessage_ADMIN() {
		return new TgMsgBuilder() {
			@Override
			protected TgMsgBuilder buildMessages() {
				String version = BootRunUtils.checkAndRunGetVersion(getTgApp().getClass(), ARR.as("--v"), true);
				addMessageFmt(Tgh.b("Version:") + version);
				addMessageFmt(Tgh.b("Prom:") + getTgApp().isProm());
				addMessageFmt(Tgh.b("Store:") + getTgApp().getStoreRoot());
				return this;
			}
		}.getMsgOrCreate();
	}

	public TgMsg getTgMsg_ROUTES() {
		StringBuilder sb = new StringBuilder();
		for (Collection<BotRoute> allRoutes : getRootRoute().getMapRoutes().values()) {
			for (BotRoute tgRoute : allRoutes) {
				if (!tgRoute.isAlias()) {
					sb.append(tgRoute.getKeyRoute()).append("\n");
				}
			}
		}
		TgMsg tgMsg = new TgMsg();
		if (sb.length() == 0) {
			tgMsg.setText("TgRoutes is empty");
		} else {
			tgMsg.setText(toString());
			tgMsg.setParseMode(ParseMode.MARKDOWNV2);
		}
		return tgMsg;
	}

	public TgMsg getTgMsg_HELP() {
		String helpMsg = loadFileContent(FILE_HELP_MD);
		if (helpMsg != null) {
			return TgMsg.of(helpMsg).setParseMode(ParseMode.MARKDOWNV2);
		}
		return getTgMsg_START();
	}

	public TgMsg getTgMsg_START() {
		User usr = Tgc.getUser(getUpdate().update);
		String head = null;
		switch (usr.getLanguageCode()) {
			case "ru":
				head = "Добро пожаловать, " + usr.getFirstName() + "!";// + ", your id " + usr.getId() + ", language" + usr.getLanguageCode();
				break;
			default:
				head = "Welcome, " + usr.getFirstName() + "!";//+ ", your id " + usr.getId() + ", language" + usr.getLanguageCode();
				break;
		}
		TgMsg tgMsg = new TgMsg();
		String startMsg = loadFileContent(FILE_START_MD);
		if (startMsg == null) {
			tgMsg.setParseMode(ParseMode.HTML).setText(Tgh.b(head));
		} else {
			tgMsg.setText(head + "\n" + startMsg);
			tgMsg.setParseMode(ParseMode.MARKDOWNV2);
		}
		ReplyKeyboard keyboard = getMainMenu(getChatIdAnyStr());
		if (keyboard != null) {
			tgMsg.setKeyboard(keyboard);
		}
		return tgMsg;
	}

	public TgMsg getTgMsg_CallbackNotFound(String clb) {
		return TgMsg.of("ERROR: callback '" + clb + "' not found ");
	}

	public TgMsg choiceAdminTgMsg(String key) {
		switch (key) {
			case "":
				return getMessage_ADMIN();

			case "a": {
				List<NetActivityModel> activity = NetActivityModel.loadActivitys(getTgApp().getActivityDb(), 10, null);
				String msg = activity.stream().map(NetActivityModel::toLine).collect(Collectors.joining("\n"));
				return TgMsg.of(msg);
			}
			case "u": {
				List<NetActivityModel> activity = getTgApp().getActivityDb().getModels(QP.distinct(CN.USER_UID));
				String msg = activity.stream().map(a -> String.valueOf(a.getUser_uid())).collect(Collectors.joining(","));
				return TgMsg.of(msg);
			}
			default: {

				if (key.startsWith("sql ")) {
					return runSql(key.substring(4).trim());
				}

				TgApp.AppMode appMode = ENUM.valueOf(key, TgApp.AppMode.class, null);
				if (appMode != null) {
					getTgApp().setAppMode(appMode);
					return TgMsg.of("Set app mode: " + appMode);
				}
				if (key.matches("a .+")) {
					String[] args = key.substring(2).split("\\s+");
					List<QP> qps = new ArrayList<>();
					Integer limit = null;
					for (String arg : args) {
						if (arg.startsWith("u")) {
							qps.add(QP.p(CN.USER_UID, UST.INT(arg.substring(1))));
						} else if (arg.startsWith("l")) {
							limit = UST.INT(arg.substring(1));
						} else if (arg.startsWith("d")) {
							qps.add(QP.pLE(CN.DT, QDate.ofMono14(arg.substring(1)).toSqlDate()));
						} else if (arg.startsWith("m")) {
							qps.add(QP.pLE(CN.DT, QDate.now().addMinutes(-1 * IT.isInt0(arg.substring(1))).toSqlDate()));
						}
					}
					if (limit == null) {
						limit = 10;
					}
					qps.add(QP.limit(limit));
					List<NetActivityModel> activity = NetActivityModel.loadActivitys(getTgApp().getActivityDb(), qps.toArray(new QP[0]));
					String msg = activity.stream().map(NetActivityModel::toLine).collect(Collectors.joining("\n"));
					return TgMsg.of(X.empty(msg) ? "empty result, key '" + key + "'" : msg);
				} else {
					return null;
				}
			}
		}
	}

	@SneakyThrows
	private TgMsg runSql(String key) {
		String clazzName = TKN.first(key, ' ');
		Class clazz = RFL.clazz(clazzName);
		IT.isClassOf(clazz, AModel.class);
		TypeDb db = TypeDbEE.getDbEE(clazz);
		String sql = IT.notEmpty(key.substring(clazzName.length() + 1).trim());
		if (sql.startsWith("select")) {
			List<List<AbsType>> rows = db.sql_query_(sql);
			if (rows.isEmpty()) {
				return TgMsg.of("empty rows");
			}
			AtomicInteger i = new AtomicInteger();
			String head = X.f("<b>Found '%s' rows</b>\n", rows.size());
			rows = ARR.sublist(rows, 0, 9, rows);
			String msg = rows.stream().map(m -> (i.incrementAndGet()) + m.toString()).collect(Collectors.joining("\n"));
			return TgMsg.ofHtml(head + msg);
		} else {
			boolean execute = db.sql_execute_(sql);
			return TgMsg.of("result:" + execute);
		}
	}
}
