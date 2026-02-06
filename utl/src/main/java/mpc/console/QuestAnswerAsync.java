package mpc.console;


import mpu.X;
import mpu.core.EQ;
import mpu.str.STR;
import mpc.str.condition.StringConditionPattern;

//QuickAnswer Async
public class QuestAnswerAsync {

	public static String CONTINUE_ANSWER(int wait_ms, StringConditionPattern scp, String message, Object... args) {
		message = args.length == 0 ? message : X.f(message, args);
		String answer = ConsoleInput.waitInputOrStartSync(wait_ms, message);
		return answer == null || !scp.matches(answer) ? null : answer;
	}

	public static String CONTINUE_ANSWER(int wait_ms, String message, Object... args) {
		message = args.length == 0 ? message : X.f(message, args);
		String answer = ConsoleInput.waitInputOrStartSync(wait_ms, message);
		return answer;
	}

	public static boolean CONTINUE_YN_12(int wait_ms, String message, Object... args) {
		message = args.length == 0 ? message : X.f(message, args);
		String answer = ConsoleInput.waitInputOrStartSync(wait_ms, message);
		return answer == null ? false : isYes(answer, "y", "yes", "da");
	}

	public static boolean isYes(String answer, String... any) {
		return EQ.equalsAny(answer, true, any);
	}

	public static boolean CONTINUE_OR_ABORT(int wait_ms, String message, Object... args) {
		message = args.length == 0 ? message : X.f(message, args);
		String answer = ConsoleInput.waitInputOrStartSync(wait_ms, message);
		return answer == null;
	}
}
