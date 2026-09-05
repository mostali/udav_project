package mpu;

import lombok.SneakyThrows;
import mpc.fs.tmpfile.TmpFileOperation;
import mpu.core.RW;
import mpu.func.FunctionV2;
import mpu.pare.Pare3;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SysExecV3_RMM {

	public static final Logger L = LoggerFactory.getLogger(SysExecV3_RMM.class);

	public static final FunctionV2<String, String> funcOutErrLoggerDefault = (i, e) -> {
		X.p("ok:" + i);
		X.p("err:" + e);
		X.p("--------------");
	};

	@SneakyThrows
	public static Pare3<Integer, String, String> execSync_ShData(Path workDir, String fileData, FunctionV2<String, String> outErrLogger) {
		return (Pare3<Integer, String, String>) new TmpFileOperation<Pare3<Integer, String, String>>() {
			@Override
			public void doOperationImpl(Path tmpFile) {
				tmpFile = tmpFile.toAbsolutePath();
				RW.write(tmpFile, fileData);
				Path workDir0 = workDir == null ? tmpFile.getParent() : workDir;
//				Path workDir0 = null;

				if (L.isInfoEnabled()) {
					if (L.isDebugEnabled()) {
						L.debug("Exec bash script with file '{}' in workDir '{}' and data\n{}", tmpFile, workDir0, fileData);
					} else {
						L.info("Exec bash script with file '{}' in workDir '{}'", tmpFile, workDir0);
					}
				}

				Pare3<Integer, String, String> execResult = execImpl(-1, workDir0, null, outErrLogger, "/bin/bash", tmpFile.toString());

				setOperationResult(Optional.of(execResult));
			}
		}.doOperation().getOperationResult().get();
	}

	@SneakyThrows
	public static Pare3<Integer, String, String> execSync_FileSh(Path workDir, Path fileSh, FunctionV2<String, String> outErrLogger) {
		return execImpl(-1, workDir, null, outErrLogger, "/bin/bash", fileSh.toString());
	}

	@SneakyThrows
	public static Pare3<Integer, String, String> execSync_AnyCmds(FunctionV2<String, String> outErrLogger, String... cmdTokens) {
		return execImpl(-1, null, null, outErrLogger, cmdTokens);
	}

	@SneakyThrows
	public static Pare3<Integer, String, String> execWaitMs(int timeoutMs, Sys.ExecDestroyMode execDestroyMode, String... cmdTokens) {
		IT.isPosNotZero(timeoutMs, "Timeout need more that 0 (vs %s), because use ExecDestroyMode", timeoutMs);
		return execImpl(timeoutMs, null, execDestroyMode, null, cmdTokens);
	}

	@SneakyThrows
	private static Pare3<Integer, String, String> execImpl(int timeoutMs, Path workDir, Sys.ExecDestroyMode execDestroyMode, FunctionV2<String, String> outErrLogger, String... cmdTokens) {
		ProcessBuilder pb = new ProcessBuilder(cmdTokens);
		if (workDir != null) {
			pb.directory(workDir.toFile());
		}
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
					if (outErrLogger != null) {
						outErrLogger.apply(line, null);
					}
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
					if (outErrLogger != null) {
						outErrLogger.apply(null, line);
					}
				}
				return sb.toString();
			}
		});

		try {

			Integer exitCode = null;
			if (timeoutMs > 0) {
				// Ждем завершения процесса с таймаутом
				boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
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
