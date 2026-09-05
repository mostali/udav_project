package mpe.restapp;

import mpc.console.QuestAnswer;
import mpu.core.RW;

import java.nio.file.Paths;

public class RestApp {

	public static final String fromFile = "t";

	public static void main(String[] args) {
		RestRoute.of(8000, "/ping").on(ex0 -> {
			return Rsp.ok("ponk");
		});
	}

	public static String readToken(String... defRq) {
		String s = RW.readString(Paths.get(fromFile), defRq);
		return s != null ? s.trim() : null;

	}

	public static void initFirstToken() {
		String tk = RestApp.readToken(null);
		if (tk == null && QuestAnswer.CONTINUE_YN("Set token?")) {
			String token = QuestAnswer.QUEST("Set token..");
			RW.write(Paths.get(fromFile), token);
		}
	}
}
