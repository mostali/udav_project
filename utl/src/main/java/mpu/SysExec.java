package mpu;

import lombok.SneakyThrows;
import mpc.exception.CleanMessageRuntimeException;
import mpc.exception.ExecRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.tmpfile.TmpFileOperation;
import mpc.str.sym.SEP;
import mpe.rt.core.ExecRq;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.func.Function2;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import mpu.str.Sb;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class SysExec {

	public static final String APK_BASH_MODE = "bash.mode";


	public static final Function2<String, Pare3<Integer, String, String>, String> showResultMsg = (d, r) -> {
		Sb sb = new Sb();
		sb.NL(d);
		sb.NL(SEP.DASH.__str1__("OUT"));
		sb.NL(r.val());
		sb.NL(SEP.DASH.__str1__("ERR"));
		sb.NL(r.ext());
		return sb.toString();
	};


	//	@SneakyThrows
//	public static void exec_SafeSpace(FunctionV1 ok, Function err, FunctionV1<Exception> fail, String... command) {
//		Process exec = Runtime.getRuntime().exec(command);
//		try {
//			int i = exec.exitValue();
//			if(i==0){
//				ok.apply(X.toString0(exec.getOutputStream()));
//			}
//			switch (i) {
//				case 0:
//
//			}

	/// /			Sys.p(i + ":" + command);
//		} catch (Exception ex) {
//			fail.apply(ex);
	@SneakyThrows
	public static Pare<Integer, List<String>> exec_filetmp(String cmdKey, String data, Path workDir, boolean throwIfHasError, Object... args) {
		final Pare[] rslt = new Pare[1];
		new TmpFileOperation() {
			@Override
			public void doOperationImpl(Path executableFile) {
				executableFile = executableFile.toAbsolutePath();
				RW.write(executableFile, data);
				rslt[0] = exec_file(cmdKey, executableFile, workDir == null ? executableFile.getParent() : workDir, throwIfHasError, args);
			}
		}.doOperation();
		return rslt[0];
	}

	public static Object[] execFileSh(File workDir, String fileSh, String... args) {
		List<String> outLines = new ArrayList<>();
		List<String> errorLines = new ArrayList<>();
		Integer status = null;

		String[] args0 = {"/bin/bash", "-c", fileSh};
		args0 = ARR.merge(args0, args);
		ProcessBuilder processBuilder = new ProcessBuilder(args0);
		processBuilder.directory(workDir);

		try {
			Process process = processBuilder.start();

			// Чтение стандартного вывода
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					outLines.add(line);
				}
			}

			// Чтение стандартного вывода ошибок
			try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String errorLine;
				while ((errorLine = errorReader.readLine()) != null) {
					errorLines.add(errorLine);
				}
			}

			// Ожидание завершения процесса и получение кода возврата
			status = process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			status = -1; // Устанавливаем статус в -1 в случае ошибки
		}

		return new Object[]{status, outLines, errorLines};
	}

	@SneakyThrows
	public static Pare<Integer, List<String>> exec_file(String cmdKey, Path executableFile, Path workDir, boolean throwIfHasError, Object... args) {
		List<String> argsRun = ARR.asAL(cmdKey, executableFile.toString());
		argsRun.addAll((Collection) ARR.as(args));
		ProcessBuilder processBuilder = new ProcessBuilder(argsRun);
		processBuilder.redirectErrorStream(true);

//		processBuilder.inheritIO();
		if (workDir != null) {
			processBuilder.directory(workDir.toFile());
		}
		Process process = processBuilder.start();
		List<String> results = RW.readLines(process.getInputStream());
		int exitCode = process.waitFor();
		String msg = X.fl("exec_file/{} >> {}", cmdKey, executableFile);
		if (Sys.L.isInfoEnabled()) {
			if (Sys.L.isDebugEnabled()) {
				Sys.L.debug(msg + " :::" + exitCode + "\n" + JOIN.allByNL(results));
			} else {
				Sys.L.info(msg + " :::" + exitCode + "*" + X.sizeOf(results));
			}
		}
		if (throwIfHasError && exitCode != 0) {
//			throw  new CleanMessageRuntimeException(JOIN.allByNL(results));
			throw new ExecRuntimeException(new CleanMessageRuntimeException(JOIN.allByNL(results)), msg);
		}
		return Pare.of(exitCode, results);
	}

	public static Pare<Integer, List<String>> exePython3(String pyData) {
		return exePython3(null, pyData);
	}

	public static Pare<Integer, List<String>> exePython3(Path workDir, String pyData) {
		return exec_filetmp("python3", pyData, workDir, false);
	}

	@SneakyThrows
	public static void exec_SafeSpace(String... command) {
		Process exec = Runtime.getRuntime().exec(command);
		try {
			int i = exec.exitValue();
			Sys.p(i + ":" + command);
		} catch (Exception ex) {
			Sys.L.error("Exec error, command : " + ARR.as(command), ex);
		}
	}

	@Deprecated //no handle sys err
	@SneakyThrows
	public static ExecRq exec(boolean returnErrorOrThrow, String... cmds) {
		return ExecRq.exec(returnErrorOrThrow, cmds);
	}

	public static final int MODE0 = 0;
	public static final int MODE1 = 1;

	public static @NotNull Pare3<Integer, String, String> execByMode(String bashData, Integer bashMode) {
		Pare3<Integer, String, String> execRq;

		switch (bashMode) {
			case 0:
				execRq = SysExecV3_RMM.execWaitMs(30_000, Sys.ExecDestroyMode.HYBRID_NOWAIT_DESTROY, SPLIT.argsBySpace(bashData));
				break;
			case 1:
				execRq = SysExecAsync.execSync_ShData(null, bashData, null);
				break;

			default:
				throw new WhatIsTypeException(bashMode);

		}
		return execRq;
	}
}
