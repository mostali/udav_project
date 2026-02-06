package botcore;

import mpu.X;
import mpu.core.ARG;
import mpc.str.sym.SYMJ;

public class FSA {

	public static final String ICON_FAIL = SYMJ.DOG_DEATH;
	public static final String ICON_ERR = SYMJ.FAIL_RED_THINK;
	public static final String ICON_WARN = SYMJ.WARN;
	public static final String ICON_NOTE = SYMJ.LAMP;
	public static final String ICON_HAND_THIS = SYMJ.HAND_R;
	public static final String ICON_PLUS = SYMJ.PLUS;
	public static final String ICON_LINK = SYMJ.ARROW_LR;
	public static final String ICON_MINUS = SYMJ.MINUS;
	public static final String ICON_INPUT = SYMJ.ARROW_DOWN;
	public static final String ICON_OK_THINK = SYMJ.OK_GREEN;
	public static final String ICON_OK_BOLD = SYMJ.OK_GREEN_BORD;

	public static final String CHOICE_ACTION = SYMJ.GEAR + " Выбери действие..";

	public static final String ERROR_SOMETHING = ICON_FAIL + "Что-то пошло не так";
	public static final String ERROR_SOMETHING_TOOLONG = ERROR_SOMETHING + "\n Сервер слишком долго думал";
	public static final String ERROR_SURPRISE_SERVER_ERROR = "Произошла неожиданная ошибка. Пожалуйста, повторите Ваш запрос.";
	public static final String WARN_UNDEFINED_REQUEST = ICON_WARN + "Неизвестный запрос";

	//
	//

	public static final String ICON_HELP = SYMJ.BOOK;
	public static final String BM_LOADING = SYMJ.TIME_SANDGLASS + " Loading..";
	public static final String ICON_EYE = SYMJ.EYE;
	public static final String SCREAMER = "❗";

	//
	//
	//

	public static String ICON_FAIL(String msg, Object... args) {
		return ICON_FAIL + " " + X.f(msg, args);
	}

	public static String ICON_OK(String msg, Object... args) {
		return ICON_OK_THINK + " " + X.f(msg, args);
	}

	public static String ICON_ERR(String msg, Object... args) {
		return ICON_ERR + " " + X.f(msg, args);
	}

	public static String ICON_INFO_SIMPLE(String msg, Object... args) {
		return ICON_OK_THINK + " " + X.f(msg, args);
	}

	public static String ICON_INPUT(String msg, Object... args) {
		return X.f(msg, args) + ICON_INPUT;
	}

	public static String ICON_CONFIRM_OK(String msg, Object... args) {
		return FSA.ICON_OK_BOLD + " " + X.f(msg, args);
	}

	public static String ICON_WARN(String msg, Object... args) {
		return ICON_WARN + " " + X.f(msg, args);
	}

	public static String ICON_NOTE(String msg, Object... args) {
		return ICON_NOTE + " " + X.f(msg, args);
	}

	public static String ICON_HELP(String msg, Object... args) {
		return ICON_HELP + " " + X.f(msg, args);
	}

	public static String ICON_PLUS(String msg, Object... args) {
		return ICON_PLUS + " " + X.f(msg, args);
	}

	public static String ICON_LINK(String msg, Object... args) {
		return ICON_LINK + " " + X.f(msg, args);
	}

	public static String ICON_MINUS(String msg, Object... args) {
		return ICON_MINUS + " " + X.f(msg, args);
	}

	public static String ICON_ON_OFF(boolean isOnOff, String... val) {
		String sfx = (ARG.isDef(val) ? val[0] : val[1]) + " ";
		return isOnOff ? SYMJ.ONOFF_ON_PLAY + " " + sfx : SYMJ.ONOFF_ON_PAUSE + " " + sfx;
	}

	public static String 	ICON_ONOFF_PLAYRED(boolean isOnOff) {
		return isOnOff ? SYMJ.ONOFF_ON_PLAY : SYMJ.ONOFF_OFF_RED;
	}

}
