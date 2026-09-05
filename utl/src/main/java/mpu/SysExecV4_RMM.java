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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.*;

public class SysExecV4_RMM {

	public static final Logger L = LoggerFactory.getLogger(SysExecV4_RMM.class);

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

		// Очереди для передачи данных в исходный поток
		BlockingQueue<String> stdoutQueue = new LinkedBlockingQueue<>();
		BlockingQueue<String> stderrQueue = new LinkedBlockingQueue<>();
		BlockingQueue<Boolean> completionSignal = new LinkedBlockingQueue<>();

		// Используем ExecutorService для чтения потоков в фоне
		ExecutorService executor = Executors.newFixedThreadPool(2);

		// Чтение stdout
		Future<?> stdoutFuture = executor.submit(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					stdoutQueue.put(line); // Передаем в очередь
				}
			} catch (IOException | InterruptedException e) {
				// Прерывание - нормальная ситуация при shutdownNow
				if (!(e instanceof InterruptedException)) {
					e.printStackTrace();
				}
			} finally {
				try {
					stdoutQueue.put("<<STDOUT_END>>"); // Маркер конца
				} catch (InterruptedException ignored) {}
			}
		});

		// Чтение stderr
		Future<?> stderrFuture = executor.submit(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					stderrQueue.put(line); // Передаем в очередь
				}
			} catch (IOException | InterruptedException e) {
				if (!(e instanceof InterruptedException)) {
					e.printStackTrace();
				}
			} finally {
				try {
					stderrQueue.put("<<STDERR_END>>"); // Маркер конца
				} catch (InterruptedException ignored) {}
			}
		});

		// Поток для мониторинга завершения чтения
		Future<?> completionFuture = executor.submit(() -> {
			try {
				stdoutFuture.get(); // Ждем завершения чтения stdout
				stderrFuture.get(); // Ждем завершения чтения stderr
				completionSignal.put(true); // Сигнализируем о завершении
			} catch (InterruptedException | ExecutionException e) {
				try {
					completionSignal.put(false);
				} catch (InterruptedException ignored) {}
			}
		});

		StringBuilder stdoutBuilder = new StringBuilder();
		StringBuilder stderrBuilder = new StringBuilder();

		try {
			Integer exitCode = null;
			boolean processFinished = false;

			if (timeoutMs > 0) {
				// Ждем завершения процесса с таймаутом
				processFinished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
				if (!processFinished) {
					execDestroyMode.destroy(process);
					// Дожимаем оставшиеся данные из потоков
					waitForStreamsCompletion(completionSignal, completionFuture);
					collectRemainingData(stdoutQueue, stderrQueue, stdoutBuilder, stderrBuilder, outErrLogger);
					return Pare3.of(null, stdoutBuilder.toString(), stderrBuilder.toString());
				}
			} else {
				exitCode = process.waitFor();
				processFinished = true;
			}

			if (processFinished) {
				// Ждем завершения чтения потоков
				waitForStreamsCompletion(completionSignal, completionFuture);

				// Собираем все данные в исходном потоке
				collectRemainingData(stdoutQueue, stderrQueue, stdoutBuilder, stderrBuilder, outErrLogger);

				exitCode = process.exitValue();
			}

			return Pare3.of(exitCode, stdoutBuilder.toString(), stderrBuilder.toString());

		} finally {
			executor.shutdownNow(); // завершаем потоки чтения
		}
	}

	@SneakyThrows
	private static void waitForStreamsCompletion(BlockingQueue<Boolean> completionSignal, Future<?> completionFuture) {
		try {
			// Ждем сигнала о завершении с таймаутом
			Boolean completed = completionSignal.poll(5, TimeUnit.SECONDS);
			if (completed == null) {
				// Таймаут - форсируем завершение
				completionFuture.cancel(true);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			completionFuture.cancel(true);
		}
	}

	@SneakyThrows
	private static void collectRemainingData(BlockingQueue<String> stdoutQueue, BlockingQueue<String> stderrQueue,
											 StringBuilder stdoutBuilder, StringBuilder stderrBuilder,
											 FunctionV2<String, String> outErrLogger) {
		// Собираем данные из очередей в исходном потоке
		boolean stdoutDone = false;
		boolean stderrDone = false;

		while (!stdoutDone || !stderrDone) {
			// Обрабатываем stdout
			if (!stdoutDone) {
				String stdoutLine = stdoutQueue.poll(100, TimeUnit.MILLISECONDS);
				if (stdoutLine != null) {
					if ("<<STDOUT_END>>".equals(stdoutLine)) {
						stdoutDone = true;
					} else {
						stdoutBuilder.append(stdoutLine).append(STR.NL);
						if (outErrLogger != null) {
							outErrLogger.apply(stdoutLine, null);
						}
					}
				}
			}

			// Обрабатываем stderr
			if (!stderrDone) {
				String stderrLine = stderrQueue.poll(100, TimeUnit.MILLISECONDS);
				if (stderrLine != null) {
					if ("<<STDERR_END>>".equals(stderrLine)) {
						stderrDone = true;
					} else {
						stderrBuilder.append(stderrLine).append(STR.NL);
						if (outErrLogger != null) {
							outErrLogger.apply(null, stderrLine);
						}
					}
				}
			}
		}
	}

}
