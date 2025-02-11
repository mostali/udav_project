package mpc.log;

import lombok.Getter;
import lombok.Setter;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.pare.Pare;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LogTailReader {

	public static final String DEF_LOCATION_SERVER_LOG = "./logs/server.log";

	public static final int DEF_COUNT_LINES = 1000;

	public final String fileServerLog;

	public LogTailReader() {
		this(DEF_LOCATION_SERVER_LOG);
	}

	public LogTailReader(String fileServerLog) {
		this.fileServerLog = fileServerLog;
	}

	private @Getter Pare<Long, List<String>> lastPart = Pare.of(0L, ARR.EMPTY_LIST);

	private @Setter boolean skipFirstPart = true;

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
			return skipFirstPart ? ARR.EMPTY_LIST : tailLines;
		}
		//next part
		List<String> show = new ArrayList<>(tailLines);
		show.removeAll(prevLines);
		return show;

	}
}
