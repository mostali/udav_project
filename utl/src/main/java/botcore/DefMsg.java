package botcore;

import mpc.env.Env;
import mpu.core.ARR;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.X;
import mpc.exception.ICleanMessage;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.RES;
import mpc.str.sym.SYMJ;
import mpu.core.QDate;
import mpu.str.STR;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public enum DefMsg {
	START, HELP;
	public static final String filePath = "/bot/msg/";
	public static final String filePathDef = filePath + "def/";
	public static final String[] ANSWER_WTH_WITH_ICON = ARR.of(SYMJ.ML_NO, SYMJ.WM_NO, SYMJ.ML_THINK, SYMJ.WM_THINK, SYMJ.ML_WTH, SYMJ.WM_WTH,//
			SYMJ.ZZZ, SYMJ.EYES, SYMJ.EYE,//
			SYMJ.DOG_LIFE, SYMJ.DOG_DEATH, SYMJ.BEAR);
	public static final String[] ANSWER_WTH = {"Не понял..", "Не поняла..", "Не понятно..",//
			"Что это значит?", "В смысле?", "Что ты хочешь?", "Вы не ошиблись?",//
			"Я не знаю что тебе ответить(", "Хмм, это очень странно", "Мб ещё разочек?",//
			"Я очень старался, но ничего не понял",
			"Што❓", "What⁉",//
			//
	};
	public static final String MSG_ERR_ILLEGAL_MSG = SYMJ.WM_SAD + " Неизвестная ошибка. Пожалуйста, обратитесь к администратору.";
	public static final String PFX_MSG_ERROR = "ERROR:";

	public static String randWth() {
		return ARRi.rand(DefMsg.ANSWER_WTH, DefMsg.ANSWER_WTH_WITH_ICON);
	}

	public static String read(String rsrc, String... defRq) {
		return RES.readString(filePath + rsrc, defRq);
	}

	public static String readDef(String rsrc, String... defRq) {
		return RES.readString(filePathDef + rsrc, defRq);
	}

	public static String buildAppsMsgFull(DefMsg type, String nt, Enum[] apps, String... defRq) {
		String total = type == DefMsg.HELP ? DefMsg.readDefMain() : "";
		String msg = buildAppsMsg(type, nt, apps);
		if (msg != null) {
			total += "\n\n" + msg;
		}
		return total;
	}

	public static String buildAppsMsg(DefMsg type, String nt, Enum[] apps, String... defRq) {
		String msg = null;
		for (Enum app : apps) {
			String rsrs = readFirstDefMsg(type, nt, app.name().toLowerCase(), null);
			if (rsrs != null) {
				String bname = app instanceof ICleanMessage ? ((ICleanMessage) app).getCleanMessage() : app.name();
				String bot_head = SYMJ.ZZZ + " Бот - " + bname + STR.HR;
				msg = msg == null ? "" : msg;
				msg += "\n\n" + bot_head + "\n" + rsrs;
			}
		}
		if (msg != null) {
			return msg;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("DefMsg '%s', nt(%s), apps(%s) is empty", type, nt, ARR.as(apps)), defRq);
	}

	public static String readDefMain() {
		return readDef("bot.main.txt");
	}

	@NotNull
	public static String buildOwnerStartMessage() {
		return Env.getAppName().toUpperCase() + " Bot started " + QDate.now().mono6_h2s2();
	}

	public enum DefMsgVar {
		NT_APP, NT, APP, COMMON
	}

	public static String readFirstDefMsg(DefMsg defMsg, String nt, String app, String... defRq) {
		app = app == null ? null : app.toLowerCase();
		nt = nt == null ? null : nt.toLowerCase();
		String cmd = defMsg.name().toLowerCase();
		List<DefMsgVar> vars = defineAllVariants(nt, app);
		for (DefMsgVar var : vars) {
			String filename;
			switch (var) {
				case NT_APP: //nt.app.DefMsg.txt
					filename = nt + "." + app + "." + cmd;
					break;
				case NT://nt.DefMsg.txt
					filename = nt + "." + cmd;
					break;
				case APP://app.DefMsg.txt
					filename = app + "." + cmd;
					break;
				case COMMON://DefMsg.txt
					filename = cmd;
					break;
				default:
					throw new WhatIsTypeException(var);
			}
			filename += ".txt";
			String rsrc = readDef(filename, null);
			if (rsrc != null) {
				return rsrc;
			}
		}
		String finalNt = nt;
		String finalApp = app;
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Rsrc '%s', nt(%s), app(%s) not found", finalNt, finalApp, defMsg), defRq);
	}

	private static List<DefMsgVar> defineAllVariants(String nt, String app) {
		List<DefMsgVar> vars = new LinkedList();
		if (X.notEmpty(nt) && X.notEmpty(app)) {
			vars.add(DefMsgVar.NT_APP);
			vars.add(DefMsgVar.NT);
			vars.add(DefMsgVar.APP);
		} else if (X.notEmpty(nt)) {
			vars.add(DefMsgVar.NT);
		} else if (X.notEmpty(app)) {
			vars.add(DefMsgVar.APP);
		}
		vars.add(DefMsgVar.COMMON);
		return vars;
	}

	public static DefMsg ofMsg(String msg, DefMsg... defRq) {
		switch (msg.toLowerCase()) {
			case "/":
			case "/start":
			case "начать"://mb has payload {"command":"start"}
				return START;
			case "?":
			case "/help":
				return HELP;
			default:
				return ARG.toDefThrow(() -> new WhatIsTypeException(msg), defRq);
		}
	}

}
