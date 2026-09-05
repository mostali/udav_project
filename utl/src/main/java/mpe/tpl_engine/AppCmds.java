package mpe.tpl_engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpc.exception.FIllegalStateException;
import mpu.IT;
import mpu.X;
import mpu.core.RW;
import mpu.pare.Tuple;
import mpu.str.JOIN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class AppCmds {

	public static final Logger L = LoggerFactory.getLogger(AppCmds.class);

	public static final int CAT_MAX_BYTES = 2_000_000;

	public static final String SYM_CMD = "◼";
	public static final String SYM_STORE = "\uD83D\uDDC4";//🗄
	public static final String SYM_GROUPS = "\uD83C\uDF9B";//🎛
	public static final String SYM_EX = "➤️";

	//
	//
	// CORE API

	public static AppCmdHandler createCmdHandler(String cmd) {
		AppCmd appCmd = findCmd(cmd);
		return appCmd == null ? null : appCmd.createHandler(cmd);
	}

	public static AppCmd findCmd(String cmd) {

//		switch (cmd) {
//			case "::ls":
//				return (cmdIn) -> {
//					Map<TplEngine.TplSrc, List<TplEngine.Tpl>> tplsByExt = TplEngine.findTplsByExt(ARR.asHSET(EXT.SQL));
//					return Rt.buildReport(tplsByExt, cmdIn);
//				};
//		}

		Optional<Map.Entry<CmdTuple, AppCmd>> first = _CMDS.entrySet().stream().filter(c -> c.getValue().canHandle(cmd)).findFirst();
		if (first.isEmpty()) {
			return null;
		}
		return first.get().getValue();
	}

	//
	//

	public static class CatAppCmd extends AppCmds.RegexAppCmd<String> {

		public CatAppCmd() {
			super("cat\\s+.+", "Показать содержимое файла", "cat /tmp/file");
		}

		@Override
		public String run(String cmd) {

			cmd = cmd.substring(4).trim();

			Path path = Paths.get(cmd);

			IT.isFdExist(path);

			if (Files.isRegularFile(path)) {
				if (path.toFile().length() > CAT_MAX_BYTES) {
					throw new FIllegalStateException("File '%s' has limit size. More than [%s] bytes", path, CAT_MAX_BYTES);
				}
				return RW.readContent(path);
			}
			if (Files.isDirectory(path)) {
				File[] files = path.toFile().listFiles();
				List<File> dirs = Arrays.stream(files).filter(f -> f.isDirectory()).collect(Collectors.toList());
				List<File> filesEnt = Arrays.stream(files).filter(f -> !dirs.contains(f)).collect(Collectors.toList());
				return JOIN.argsByNL("Dirs:", JOIN.allByNL(dirs), "Files:", JOIN.allByNL(filesEnt));
			}
			throw new FIllegalStateException("What is entity '%s' ?", path);
		}
	}

	//
	//
	// CORE STRUCT


	@RequiredArgsConstructor
	public static class AppCmdHandler<R> {
		private final @Getter AppCmd<R> appCmd;
		private final String cmd;

		private R result;

		public String getCmdText() {
			return cmd;
		}

		public R getResult() {
			if (result != null) {
				return result;
			}
			R result = appCmd.run(cmd);
			L.info(":::" + result);
			return this.result = result;
		}

	}

	@FunctionalInterface
	public interface AppCmd<R> {

		R run(String cmd);

		default AppCmdHandler<R> createHandler(String cmd) {
			return new AppCmdHandler(this, cmd);
		}

		default boolean isRegex() {
			return false;
		}

		default boolean canHandle(String cmd) {
			if (isRegex()) {
				return cmd.matches(key());
			}
			return X.equalsIgnoreCase(cmd, key());
		}

		default String key() {
			return _CMDS.entrySet().stream().filter(i -> i.getValue() == this).findAny().get().getKey().key();
		}
	}

	public static class CmdTuple extends Tuple {
		public CmdTuple(Object... objs) {
			super(objs);
		}

		public String key(String... defRq) {
			return (String) super.key(defRq);
		}

		public String desc(String... defRq) {
			return super.getAsString(1, defRq);
		}

		public String example(String... defRq) {
			return super.getAsString(2, defRq);
		}
	}

	public static abstract class NamedAppCmd<R> implements AppCmd<R> {
		public final CmdTuple key;

		public NamedAppCmd(String pattern, String desc) {
			this.key = new CmdTuple(pattern, desc);
			_CMDS.put(key, this);
			TplAppCmds.L.info("reg tplcmd {}->{}", key, this);
		}

		@Override
		public String key() {
			return this.key.key();
		}

	}

	public static abstract class RegexAppCmd<R> implements AppCmd<R> {
		public final CmdTuple key;

		public RegexAppCmd(String pattern, String desc) {
			this(pattern, desc, null);
		}

		public RegexAppCmd(String pattern, String desc, String example) {
			this.key = new CmdTuple(pattern, desc, example);
			_CMDS.put(key, this);
			TplAppCmds.L.info("reg tplcmd(RX) {}->{}", key, this);
		}

		@Override
		public String key() {
			return this.key.id();
		}

		@Override
		public boolean isRegex() {
			return true;
		}


	}


	//
	//
	// STATIC AUTO INIT

	public static final Map<CmdTuple, AppCmd> _CMDS = new LinkedHashMap<>();

	static {
		registerNestedCommands(AppCmds.class);
		registerNestedCommands(TplAppCmds.class);
	}

	public static void registerNestedCommands(Class cmdsClass) {
		L.info("registerNestedCommands:" + cmdsClass);
		for (Class<?> type : cmdsClass.getDeclaredClasses()) {
			int modifiers = type.getModifiers();

			if (java.lang.reflect.Modifier.isInterface(modifiers)) {
				continue;
			}

			if (java.lang.reflect.Modifier.isAbstract(modifiers)) {
				continue;
			}

			if (!AppCmd.class.isAssignableFrom(type)) {
				continue;
			}

			if (!java.lang.reflect.Modifier.isStatic(modifiers)) {
				continue;
			}

			try {
				java.lang.reflect.Constructor<?> ctor = type.getDeclaredConstructor();
				ctor.setAccessible(true);
				ctor.newInstance();
			} catch (NoSuchMethodException e) {
				L.warn("нет конструктора без аргументов — пропускаем", e);
			} catch (Exception e) {
				throw new IllegalStateException("Cannot register TplCmd: " + type.getName(), e);
			}
		}
	}

	//
	//


}
