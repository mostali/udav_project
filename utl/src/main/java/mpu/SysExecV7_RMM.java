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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SysExecV7_RMM {

	public static final Logger L = LoggerFactory.getLogger(SysExecV7_RMM.class);

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
		pb.redirectErrorStream(false);

		Process process = pb.start();

		// Используем ExecutorService для чтения потоков в фоне
		ExecutorService executor = Executors.newFixedThreadPool(2);

		// Накопители для батчинга
		BlockingQueue<String> stdoutQueue = new LinkedBlockingQueue<>();
		BlockingQueue<String> stderrQueue = new LinkedBlockingQueue<>();
		AtomicBoolean streamsCompleted = new AtomicBoolean(false);

		// Запускаем сборщик данных, который будет агрегировать сообщения раз в секунду
		Future<?> batchCollector = executor.submit(() -> {
			while (!streamsCompleted.get() || !stdoutQueue.isEmpty() || !stderrQueue.isEmpty()) {
				try {
					// Ждем 1 секунду или пока не накопится много данных
					Thread.sleep(1000);

					// Собираем все доступные stdout сообщения
					List<String> stdoutBatch = new ArrayList<>();
					stdoutQueue.drainTo(stdoutBatch);

					// Собираем все доступные stderr сообщения
					List<String> stderrBatch = new ArrayList<>();
					stderrQueue.drainTo(stderrBatch);

					// Если есть данные - передаем пачкой
					if (!stdoutBatch.isEmpty() || !stderrBatch.isEmpty()) {
						String stdoutBatchText = String.join(STR.NL, stdoutBatch);
						String stderrBatchText = String.join(STR.NL, stderrBatch);

						if (outErrLogger != null) {
							outErrLogger.apply(
									stdoutBatchText.isEmpty() ? null : stdoutBatchText,
									stderrBatchText.isEmpty() ? null : stderrBatchText
							);
						}
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});

		// Чтение stdout
		Future<String> stdoutFuture = executor.submit(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append(STR.NL);
					stdoutQueue.put(line); // Добавляем в очередь для батчинга
				}
				return sb.toString();
			} finally {
				// Проверяем завершение всех потоков
				checkCompletion(stdoutQueue, stderrQueue, streamsCompleted);
			}
		});

		// Чтение stderr
		Future<String> stderrFuture = executor.submit(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append(STR.NL);
					stderrQueue.put(line); // Добавляем в очередь для батчинга
				}
				return sb.toString();
			} finally {
				// Проверяем завершение всех потоков
				checkCompletion(stdoutQueue, stderrQueue, streamsCompleted);
			}
		});

		try {
			Integer exitCode = null;
			if (timeoutMs > 0) {
				boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
				if (!finished) {
					execDestroyMode.destroy(process);
					streamsCompleted.set(true);
					batchCollector.get(2, TimeUnit.SECONDS); // Ждем завершения сборщика
					return Pare3.of(null, stdoutFuture.get(1, TimeUnit.SECONDS), stderrFuture.get(1, TimeUnit.SECONDS));
				}
			} else {
				exitCode = process.waitFor();
			}

			// Помечаем потоки как завершенные и ждем сборщика
			streamsCompleted.set(true);
			batchCollector.get(5, TimeUnit.SECONDS); // Ждем завершения сборщика

			// Получаем финальные выводы
			String stdout = stdoutFuture.get();
			String stderr = stderrFuture.get();
			exitCode = process.exitValue();

			return Pare3.of(exitCode, stdout, stderr);

		} finally {
			streamsCompleted.set(true);
			executor.shutdownNow();
		}
	}

	@SneakyThrows
	private static void checkCompletion(BlockingQueue<String> stdoutQueue, BlockingQueue<String> stderrQueue, AtomicBoolean completed) {
		// Если обе очереди пусты и процесс завершен, помечаем как завершенное
		new Thread(() -> {
			try {
				Thread.sleep(100); // Даем время другим потокам завершиться
				if (stdoutQueue.isEmpty() && stderrQueue.isEmpty()) {
					completed.set(true);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}).start();
	}

}
