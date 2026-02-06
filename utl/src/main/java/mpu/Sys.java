package mpu;

import lombok.SneakyThrows;
import mpc.env.AP;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.map.BootContext;
import mpe.core.P;
import mpe.rt.SheduledThread;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.SPLIT;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Алиас для System + функции для взамодействия с системой
 */
public class Sys {

	public static void main(String[] args) {
		X.exit(SysExec.execFileSh(new File("/home/dav/pjnsi/insi/_cicd/nifi/"), "/home/dav/pjnsi/insi/_cicd/nifi/export/export-flow.sh", SPLIT.argsBySpace("-b bucket_DEV -f flnPcg1")));
		System.setProperty("jm.task", "*");
		System.setProperty("jm.projects", "EXP");
		X.exit(getEnvSysWoPfxMap("jm."));
		String envOrPropWoPfx = getEnvSysWoPfxFirst("jm.", "task");
		X.exit(envOrPropWoPfx);
//		X.exit(execRq);
//		Sys.exec_UnsafeSpace("ssh HOST tail -100 /opt/tomcat-9/logs/catalina.out");
	}

	public static final Logger L = LoggerFactory.getLogger(Sys.class);
	public static final String NULL0 = "0";

	public static boolean isOsWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}


	/**
	 * *************************************************************
	 * --------------------------- PRINT -----------------------
	 * *************************************************************
	 */
	public static void p(Throwable s) {
		s.printStackTrace(System.out);
	}

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
	 * ------------------------- EXEC ------------------------
	 * *************************************************************
	 */

	@SneakyThrows
	public static void exec_UnsafeSpace(String command) {
		Process exec = Runtime.getRuntime().exec(command);
		try {
			int i = exec.exitValue();
			Sys.p(i + ":" + command);
		} catch (Exception ex) {
			L.error("Exec error, command : " + command + " : " + ex.getMessage());
		}
	}

	public enum ExecDestroyMode {
		DESTROY_WAIT, DESTROY_NOWAIT, //
		FORCEDESTROY, //
		HYBRID_WAIT_DESTROY, HYBRID_NOWAIT_DESTROY //
		;

		@SneakyThrows
		public void destroy(Process process) {
			switch (this) {
				case DESTROY_WAIT:
				case DESTROY_NOWAIT:
					process.destroy();
					if (this == DESTROY_WAIT) {
						int status = process.waitFor();// ждём гарантированного завершения
					}
					break;
				case FORCEDESTROY:
					Process process0 = process.destroyForcibly();// убиваем насильно
					break;
				case HYBRID_WAIT_DESTROY:
				case HYBRID_NOWAIT_DESTROY:
					process.destroy(); // попросим вежливо завершиться
					if (!process.waitFor(5, TimeUnit.SECONDS)) {
						process.destroyForcibly(); // убиваем насильно
						if (this == HYBRID_WAIT_DESTROY) {
							int status = process.waitFor();// ждём гарантированного завершения
						}
					}
					break;
				default:
					throw new WhatIsTypeException(this);
			}
		}
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
		for (Object obj : objs) {
			X.p(obj);
		}
		p("exit(0)/" + objs.length);
		System.exit(0);
	}

	public static void exit(Object obj) {
		P.pnice(obj);
		p("exit(0)");
		System.exit(0);
	}

	/**
	 * *************************************************************
	 * ------------------------- OPEN LINUX APP's ------------------------
	 * *************************************************************
	 */

	@SneakyThrows
	public static void open_Code(Path path) {
		SysExec.exec_SafeSpace("code", path.toString());
	}

	@SneakyThrows
	public static void open_FileInFirefox(Path file) {
		SysExec.exec_SafeSpace("firefox" , file.toAbsolutePath().toString());
	}

	@SneakyThrows
	public static void open_FileInChrome(Path file) {
		SysExec.exec_SafeSpace("chromium" , file.toAbsolutePath().toString());
	}

	@SneakyThrows
	public static void open_Chrome(String url) {
		SysExec.exec_SafeSpace("chromium", url);
	}

	@SneakyThrows
	public static void open_Nautilus(String file) {
		open_Nautilus(Paths.get(file));
	}

	@SneakyThrows
	public static void open_Nautilus(Path file) {
		SysExec.exec_SafeSpace("nautilus", file.toString());
	}

	@SneakyThrows
	public static void open_Terminal(Path file) {
		SysExec.exec_SafeSpace("gnome-terminal", file.toString());
	}

	/**
	 * *************************************************************
	 * -------------------------  SAY ------------------------
	 * *************************************************************
	 */

	public static void beep() {
		Toolkit.getDefaultToolkit().beep();
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

	public static TimerTask RUN_TIMER(int beforeDelay, int period, TimerTask runnable) {
		new Timer().schedule(runnable, beforeDelay, period);
		return runnable;
	}


	public static void setClipboard(String writeMe, boolean... required) {
		try {
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable tText = new StringSelection(writeMe);
			clip.setContents(tText, null);
		} catch (Throwable ex) {
			if (ARG.isDefEqTrue(required)) {
				X.throwException(ex);
			}
		}
	}


	@Deprecated //see BootContext
	public enum PKS {
		ENV, SYS, APP, BOOT_CONTEXT;

		public static Pare<PKS, String> getValueFirst(String key, Pare<PKS, String>... defRq) {
			Optional<Pare<PKS, String>> opt = Arrays.stream(values()).map(t -> {
				String value = t.getValue(key, null);
				if (value != null) {
					return Pare.of(t, value);
				}
				return null;
			}).filter(X::NN).findFirst();
			return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("Except pks key '%s'", key), opt, defRq);
		}

		public <T> T getValueAs(String key, Class<T> asType, T... defRq) {
			String value = getValue(key, null);
			return value != null ? UST.strTo(value, asType, defRq) : ARG.toDefThrowMsg(() -> X.f("Except not null value by key '%s'", key), defRq);
		}

		public String getValue(String key, String... defRq) {
			switch (this) {
				case ENV:
					return getEnvProp(key, defRq);
				case SYS:
					return getSysProp(key, defRq);
				case APP:
					return AP.get(key, defRq);
				case BOOT_CONTEXT:
					BootContext bootContext = BootContext.get();
					return bootContext != null ? bootContext.get(key, defRq) : ARG.toDefThrowMsg(() -> X.f("Set boot context"), defRq);
				default:
					throw new WhatIsTypeException(this);
			}
		}

	}

	public static String getSysProp(String syskey, String... defRq) {
		String s = System.getProperty(syskey, null);
		return s != null ? s : ARG.toDefThrowMsg(() -> X.f("Except value by sysKey '%s'", syskey), defRq);
	}

	public static String getEnvProp(String envkey, String... defRq) {
		String s = System.getenv(envkey);
		return s != null ? s : ARG.toDefThrowMsg(() -> X.f("Except value by envKey '%s'", envkey), defRq);
	}

//	public static String getEnvOrSysProp(String key, String... defRq) {
//		String val = System.getenv(key);
//		if (val == null) {
//			val = System.getProperty(key);
//		}
//		return val != null ? val : ARG.toDefThrowMsg(() -> X.f("Except env&sys prop '%s'", key), defRq);
//	}

	public static String getEnvSysWoPfxFirst(String pfx, String key, String... defRq) {
		Function<String, String> pfxSbstr = (in) -> in.substring(pfx.length());
		Optional<String> first = System.getenv().entrySet().stream().filter(i -> i.getKey().startsWith(pfx)).map(e -> pfxSbstr.apply(e.getValue())).findFirst();
		if (first.isEmpty()) {
			first = System.getProperties().entrySet().stream().filter(i -> i.getKey().toString().startsWith(pfx)).map(e -> pfxSbstr.apply(e.getValue().toString())).findFirst();
		}
		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException(X.f("Except env&sys prop '%s'", key)), first, defRq);
	}

	public static Map<String, String> getEnvSysWoPfxMap(String pfx) {
		Function<String, String> pfxSbstr = (in) -> in.substring(pfx.length());
		HashMap<String, String> collect = System.getenv().entrySet().stream().filter(i -> i.getKey().startsWith(pfx)).collect(Collectors.toMap(e -> pfxSbstr.apply(e.getKey()), e -> e.getValue(), (k, k2) -> pfxSbstr.apply(k2), HashMap::new));
		HashMap<String, String> collect2 = System.getProperties().entrySet().stream().filter(i -> i.getKey().toString().startsWith(pfx)).collect(Collectors.toMap(e -> pfxSbstr.apply(e.getKey().toString()), e -> e.getValue().toString(), (k, k2) -> pfxSbstr.apply(k2), HashMap::new));
		return ARR.merge(true, collect, collect2);
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

	public static Subject subject() {
		return Subject.getSubject(AccessController.getContext());
	}

}