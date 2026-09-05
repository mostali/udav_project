package mpc.log;

import lombok.Getter;
import mpe.rt.Thread0;

public class LogTailReaderThread0 extends Thread0 {

	private final @Getter LogTailReader logTailReader;

	public LogTailReaderThread0(String name, LogTailReader logTailReader, Runnable runnable, boolean... started) {
		super(name, runnable, started);
		this.logTailReader = logTailReader;
	}

//	public LogTailReaderThread0(String name, Runnable runnable, boolean... started) {
//		super(name, runnable, started);
//		this.logTailReader = LogTailReader.newLoggedTask();
//	}
}
