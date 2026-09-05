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
import java.util.concurrent.atomic.AtomicInteger;

public class SysExecV5_RMM {

	public static final Logger L = LoggerFactory.getLogger(SysExecV5_RMM.class);

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

		// Единая очередь для сохранения порядка сообщений
		BlockingQueue<StreamLine> outputQueue = new LinkedBlockingQueue<>();
		AtomicInteger completedStreams = new AtomicInteger(0);
		int TOTAL_STREAMS = 2;

		// Используем ExecutorService для чтения потоков в фоне
		ExecutorService executor = Executors.newFixedThreadPool(2);

		// Создаем задачи заранее
		Runnable stdoutTask = () -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					outputQueue.put(new StreamLine(StreamType.STDOUT, line));
				}
			} catch (IOException | InterruptedException e) {
				if (!(e instanceof InterruptedException)) {
					e.printStackTrace();
				}
			} finally {
				if (completedStreams.incrementAndGet() >= TOTAL_STREAMS) {
					outputQueue.offer(new StreamLine(StreamType.STDOUT, "<<COMPLETION>>"));
				}
			}
		};

		Runnable stderrTask = () -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					outputQueue.put(new StreamLine(StreamType.STDERR, line));
				}
			} catch (IOException | InterruptedException e) {
				if (!(e instanceof InterruptedException)) {
					e.printStackTrace();
				}
			} finally {
				if (completedStreams.incrementAndGet() >= TOTAL_STREAMS) {
					outputQueue.offer(new StreamLine(StreamType.STDOUT, "<<COMPLETION>>"));
				}
			}
		};

		// Запускаем задачи и получаем Future
		Future<?> stdoutFuture = executor.submit(stdoutTask);
		Future<?> stderrFuture = executor.submit(stderrTask);

		StringBuilder stdoutBuilder = new StringBuilder();
		StringBuilder stderrBuilder = new StringBuilder();

		// Собираем данные в исходном потоке
		boolean allStreamsCompleted = false;
		long startTime = System.currentTimeMillis();
		long timeout = timeoutMs > 0 ? timeoutMs + 5000 : 30000; // добавляем запас времени

		while (!allStreamsCompleted && (System.currentTimeMillis() - startTime < timeout)) {
			try {
				StreamLine streamLine = outputQueue.poll(100, TimeUnit.MILLISECONDS);
				if (streamLine != null) {
					if ("<<COMPLETION>>".equals(streamLine.line)) {
						allStreamsCompleted = true;
						break;
					}

					if (streamLine.type == StreamType.STDOUT) {
						stdoutBuilder.append(streamLine.line).append(STR.NL);
						if (outErrLogger != null) {
							outErrLogger.apply(streamLine.line, null);
						}
					} else {
						stderrBuilder.append(streamLine.line).append(STR.NL);
						if (outErrLogger != null) {
							outErrLogger.apply(null, streamLine.line);
						}
					}
				}

				// Дополнительная проверка завершения
				if (stdoutFuture.isDone() && stderrFuture.isDone() && outputQueue.isEmpty()) {
					allStreamsCompleted = true;
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}

		try {
			Integer exitCode = null;
			boolean processFinished = false;

			if (timeoutMs > 0) {
				processFinished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
				if (!processFinished) {
					execDestroyMode.destroy(process);
					return Pare3.of(null, stdoutBuilder.toString(), stderrBuilder.toString());
				}
			} else {
				exitCode = process.waitFor();
				processFinished = true;
			}

			if (processFinished) {
				// Дожимаем оставшиеся данные
				collectRemainingData(outputQueue, stdoutBuilder, stderrBuilder, outErrLogger);
				exitCode = process.exitValue();
			}

			return Pare3.of(exitCode, stdoutBuilder.toString(), stderrBuilder.toString());

		} finally {
			executor.shutdownNow();
		}
	}

	@SneakyThrows
	private static void collectRemainingData(BlockingQueue<StreamLine> outputQueue,
											 StringBuilder stdoutBuilder, StringBuilder stderrBuilder,
											 FunctionV2<String, String> outErrLogger) {
		// Собираем оставшиеся данные
		StreamLine streamLine;
		while ((streamLine = outputQueue.poll(100, TimeUnit.MILLISECONDS)) != null) {
			if ("<<COMPLETION>>".equals(streamLine.line)) {
				continue;
			}

			if (streamLine.type == StreamType.STDOUT) {
				stdoutBuilder.append(streamLine.line).append(STR.NL);
				if (outErrLogger != null) {
					outErrLogger.apply(streamLine.line, null);
				}
			} else {
				stderrBuilder.append(streamLine.line).append(STR.NL);
				if (outErrLogger != null) {
					outErrLogger.apply(null, streamLine.line);
				}
			}
		}
	}

	// Вспомогательные классы
	private enum StreamType {
		STDOUT, STDERR
	}

	private static class StreamLine {
		final StreamType type;
		final String line;

		StreamLine(StreamType type, String line) {
			this.type = type;
			this.line = line;
		}
	}

}
