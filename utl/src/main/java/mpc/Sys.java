package mpc;

import lombok.SneakyThrows;
import mpc.core.P;
import mpc.rt.SheduledThread;
import mpe.rt.core.ExecRq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Этот класс был создан как попытка создать базовый класс без внешних зависимостей, который можно было просто использовать в любом проекте
 * Т.е. просто копируем класс и юзаем базовые методы f() p() exit().
 */
public class Sys {
	public static final Logger L = LoggerFactory.getLogger(Sys.class);

	public static boolean isOsWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}


	/**
	 * *************************************************************
	 * --------------------------- PRINT -----------------------
	 * *************************************************************
	 */

	public static void p(Object s) {
		System.out.println(s);
	}

	public static void pf(String s, Object... args) {
		System.out.println(X.f(s, args));
	}

	public static void e(String s, Object... args) {
		e(String.format(s, args));
	}

	public static void e(String m) {
		System.err.println(m);
	}

	public static void e(Object o) {
		e(o == null ? null : o.toString());
	}


	/**
	 * *************************************************************
	 * --------------------------- EXIT -----------------------
	 * *************************************************************
	 */

	public static void exit() {
		System.exit(0);
	}

	public static void exit(String message) {
		p(message);
		p("exit(0)/" + (message == null ? "-1" : message.length()));
		System.exit(0);
	}

	public static void exitf(String obj, Object... args) {
		pf(obj, args);
		p("exit(0)");
		System.exit(0);
	}

	public static void exit(Object... objs) {
		if (objs == null) {
			p(null);
			return;
		}
		p(Arrays.asList(objs));
		p("exit(0)/" + objs.length);
		System.exit(0);
	}

	/**
	 * *************************************************************
	 * ------------------------- BEEP & SAY ------------------------
	 * *************************************************************
	 */

	public static void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	@SneakyThrows
	public static void openCode(Path path) {
		runQuicklyExecSafeSpace("code", path.toString());
	}

	@SneakyThrows
	public static void openBrowser(String url) {
		runQuicklyExecSafeSpace("chromium", url);
	}

	@SneakyThrows
	public static void openNautilus(String file) {
		runQuicklyExecSafeSpace("nautilus", file);
	}

	@SneakyThrows
	public static void runQuicklyExecUnsafeSpace(String command) {
		Process exec = Runtime.getRuntime().exec(command);
		try {
			int i = exec.exitValue();
			Sys.p(i + ":" + command);
		} catch (Exception ex) {
			L.error("Exec error, command : " + command + " : " + ex.getMessage());
		}
	}

	@SneakyThrows
	public static void runQuicklyExecSafeSpace(String... command) {
		Process exec = Runtime.getRuntime().exec(command);
		try {
			int i = exec.exitValue();
			Sys.p(i + ":" + command);
		} catch (Exception ex) {
			L.error("Exec error, command : " + command + " : " + ex.getMessage());
		}
	}

	public static void exit(Object obj) {
		P.pnice(obj);
		p("exit(0)");
		System.exit(0);
	}

	public static void saySheduled(String message, int at_time) {
		new SheduledThread(at_time) {
			@Override
			protected void doWork() {
				say((message));

			}
		};
	}

	public static void say(String msg, Object... args) {
		msg = X.f_(msg, args);
		if (isOsWindows()) {
			Sayer.sayOnWindows(msg);
		} else {
			Sayer.sayOnLinux(msg);
		}
	}

	@SneakyThrows
	public static List<String> runQuicklyExecRq(String... cmds) {
		ExecRq exec = ExecRq.exec(false, cmds);
		java.util.List<String> out = exec.getOut(true);
		return out;
	}

	static class Sayer {

		public static void sayOnLinux(String message) {
			try {
				Runtime.getRuntime().exec("spd-say " + message.replaceAll("\\s+", "-"));
			} catch (IOException e) {
				p(e.getMessage());
			}
		}

		public static void sayOnWindows(String message) {
			// if WINDOWS
			String result = "";
			try {
				// File file = File.createTempFile("voicePC", ".vbs");
				File file = new File("voicePC.vbs");
				file.deleteOnExit();
				FileWriter fw = new FileWriter(file);

				String vbs = "CreateObject(\"SAPI.SpVoice\").Speak\"" + message + "\"";

				fw.write(vbs);
				fw.close();
				Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = input.readLine()) != null) {
					result += line;
				}
				input.close();

			} catch (Exception e) {
				p(e.getMessage());
			}
		}
	}

}