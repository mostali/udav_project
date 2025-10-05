package mpu;

import lombok.SneakyThrows;
import mpc.exception.CleanMessageRuntimeException;
import mpc.exception.ExecRuntimeException;
import mpc.fs.tmpfile.TmpFileOperation;
import mpe.rt.core.ExecRq;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import mpu.str.STR;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SysExec {

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
			Sys.L.error("Exec error, command : " + command + " : " + ex.getMessage());
		}
	}

	/// /			L.error("Exec error, command : " + command + " : " + ex.getMessage());
//		}
//	}
	@Deprecated //no handle sys err
	@SneakyThrows
	public static ExecRq exec(boolean returnErrorOrThrow, String... cmds) {
		return ExecRq.exec(returnErrorOrThrow, cmds);
	}

	@SneakyThrows
	public static Pare3<Integer, String, String> exec(int timeoutSeconds, Sys.ExecDestroyMode execDestroyMode, String... cmdTokens) {
		ProcessBuilder pb = new ProcessBuilder(cmdTokens);
		pb.redirectErrorStream(false); // не сливать stderr в stdout

		Process process = pb.start();

		// Используем ExecutorService для чтения потоков в фоне
		ExecutorService executor = Executors.newFixedThreadPool(2);

		// Чтение stdout
		Future<String> stdoutFuture = executor.submit(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append(STR.NL);
				}
				return sb.toString();
			}
		});

		// Чтение stderr
		Future<String> stderrFuture = executor.submit(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append(STR.NL);
				}
				return sb.toString();
			}
		});

		try {

			Integer exitCode = null;
			if (timeoutSeconds > 0) {
				// Ждем завершения процесса с таймаутом
				boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
				if (!finished) {
					execDestroyMode.destroy(process);
					return Pare3.of(null, stdoutFuture.get(), stderrFuture.get());
//				throw new TimeoutException("Процесс превысил лимит времени: " + timeoutSeconds + " сек");
				}
			} else {
				exitCode = process.waitFor();
			}


			// Получаем выводы
			String stdout = stdoutFuture.get();
			String stderr = stderrFuture.get();

			exitCode = process.exitValue();

//			System.out.println("Exit code: " + exitCode);
//			System.out.println("STDOUT:\n" + stdout);
//			System.out.println("STDERR:\n" + stderr);

//			if (exitCode != 0) {
//				throw new RuntimeException("Команда завершилась с ошибкой. Код: " + exitCode + "\n" + stderr);
//			}

			return Pare3.of(exitCode, stdout, stderr);

		} finally {
			executor.shutdownNow(); // завершаем потоки чтения
		}
	}
}
