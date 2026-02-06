package mpc.log;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import mpe.rt.Thread0;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.func.FunctionV;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import mpu.str.Sb;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LogTailReader {

	public static final String DEF_LOCATION_SERVER_LOG = "./logs/server.log";

	public static final int DEF_COUNT_LINES = 1000;

	public final String fileServerLog;

	public static LogTailReader newLoggedTask() {
		LogTailReader logTailReader = new LogTailReader();
		logTailReader.setSkipFirstPart(true);
		logTailReader.readNextTailLogLines();//init first part log
		return logTailReader;
	}

	public LogTailReader() {
		this(DEF_LOCATION_SERVER_LOG);
	}

	public LogTailReader(String fileServerLog) {
		this.fileServerLog = fileServerLog;
	}

	private @Getter Pare<Long, List<String>> lastPart = Pare.of(0L, ARR.EMPTY_LIST);

	private @Setter boolean skipFirstPart = true;

	@SneakyThrows
	public static <T> Pare3<T, Throwable, String> doAsyncLogOperation(Supplier<T> nodeFunc) {


		LogTailReader logTailReader = new LogTailReader();

		logTailReader.readNextTailLogLines();//init first part log

		Sb logDataSb = new Sb();
		FunctionV readAndWriteState = () -> {
			String logData = JOIN.allByNL(logTailReader.readNextTailLogLines());
			logDataSb.NL(logData);
		};

		AtomicReference rslt = new AtomicReference();
		Thread0 thread0 = new Thread0(() -> {

			readAndWriteState.apply();

			rslt.set(nodeFunc.get());

			readAndWriteState.apply();

		}, true);

		thread0.join();

		readAndWriteState.apply();

		return Pare3.of((T) rslt.get(), thread0.getErrorsAsSingleOrMultiException(null), logDataSb.toString());

	}

	private @Getter List<String> collector = new ArrayList<>();

	public List<String> readNextTailLogLines(Integer... count) {
		int count0 = ARG.toDefOr(DEF_COUNT_LINES, count);
		Path pathServerLog = Paths.get(fileServerLog);
		long currentSize = X.sizeOf(pathServerLog);
		long prevSize = lastPart.key();
		List<String> prevLines = lastPart.val();
		if (prevSize == currentSize) {
			return ARR.EMPTY_LIST;
		}
		List<String> tailLines = RW.readLines(pathServerLog, -count0, true);
		lastPart = Pare.of(currentSize, tailLines);
		if (prevSize == 0) {

			//it first

			if (skipFirstPart) {
				return ARR.EMPTY_LIST;
			}
			collector.clear();
			collector.addAll(tailLines);
			return tailLines;
		}

		//next part

		List<String> show = new ArrayList<>(tailLines);
		show.removeAll(prevLines);

		collector.addAll(tailLines);

		return show;

	}
}
