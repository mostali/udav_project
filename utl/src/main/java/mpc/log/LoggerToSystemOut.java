package mpc.log;

import lombok.SneakyThrows;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.str.STR;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoggerToSystemOut implements Logger {

	private final Path file;
	private final PrintStream out;

	@SneakyThrows
	public static LoggerToSystemOut fromFile(String filename, boolean... newFile) {
		if (ARG.isDefEqTrue(newFile)) {
			Files.deleteIfExists(Paths.get(filename));
		}
		return new LoggerToSystemOut(Paths.get(filename));
	}

	public LoggerToSystemOut() {
		this(System.out, null);
	}

	public LoggerToSystemOut(Path toFile) {
		this(null, toFile);
	}

	public LoggerToSystemOut(PrintStream out, Path file) {
		this.out = out;
		this.file = file;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	public void log(String message) {
		out(message);
	}

	private void out(String message) {
		out(message, null);
	}

	private void out(String message, Throwable throwable) {
		if (out != null) {
			out.println(message);
			if (throwable != null) {
				throwable.printStackTrace(out);
			}
		}
		if (file != null) {
			try {
				if (throwable != null) {
					message += System.lineSeparator() + StringUtils.join(throwable.getStackTrace(), System.lineSeparator());
				}
				RW.write_AppendOrCreateNew_(file, message + STR.NL);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
	}

	public void log(String message, Object o) {
		log(X.fl(message, o));
	}

	public void log(String message, Object... o) {
		out(X.fl(message, o));
	}

	public void log(String message, Throwable throwable) {
		out(message, throwable);
	}

	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	@Override
	public void trace(String s) {
		log(s);
	}

	@Override
	public void trace(String s, Object o) {
		log(s, o);
	}

	@Override
	public void trace(String s, Object o, Object o1) {
		log(s, o, o1);
	}

	@Override
	public void trace(String s, Object... objects) {
		log(s, objects);
	}

	@Override
	public void trace(String s, Throwable throwable) {
		log(s, throwable);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return true;
	}

	@Override
	public void trace(Marker marker, String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void trace(Marker marker, String s, Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void trace(Marker marker, String s, Object o, Object o1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void trace(Marker marker, String s, Object... objects) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void trace(Marker marker, String s, Throwable throwable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public void debug(String s) {
		log(s);
	}

	@Override
	public void debug(String s, Object o) {
		log(s, o);
	}

	@Override
	public void debug(String s, Object o, Object o1) {
		log(s, o, o1);
	}

	@Override
	public void debug(String s, Object... objects) {
		log(s, objects);
	}

	@Override
	public void debug(String s, Throwable throwable) {
		log(s, throwable);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return true;
	}

	@Override
	public void debug(Marker marker, String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void debug(Marker marker, String s, Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void debug(Marker marker, String s, Object o, Object o1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void debug(Marker marker, String s, Object... objects) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void debug(Marker marker, String s, Throwable throwable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void info(String s) {
		log(s);
	}

	@Override
	public void info(String s, Object o) {
		log(s, o);
	}

	@Override
	public void info(String s, Object o, Object o1) {
		log(s, o, o1);
	}

	@Override
	public void info(String s, Object... objects) {
		log(s, objects);
	}

	@Override
	public void info(String s, Throwable throwable) {
		log(s, throwable);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return true;
	}

	@Override
	public void info(Marker marker, String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void info(Marker marker, String s, Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void info(Marker marker, String s, Object o, Object o1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void info(Marker marker, String s, Object... objects) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void info(Marker marker, String s, Throwable throwable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void warn(String s) {
		log(s);
	}

	@Override
	public void warn(String s, Object o) {
		log(s, o);
	}

	@Override
	public void warn(String s, Object... objects) {
		log(s, objects);
	}

	@Override
	public void warn(String s, Object o, Object o1) {
		log(s, o, o1);
	}

	@Override
	public void warn(String s, Throwable throwable) {
		log(s, throwable);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return true;
	}

	@Override
	public void warn(Marker marker, String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void warn(Marker marker, String s, Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void warn(Marker marker, String s, Object o, Object o1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void warn(Marker marker, String s, Object... objects) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void warn(Marker marker, String s, Throwable throwable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public void error(String s) {
		log(s);
	}

	@Override
	public void error(String s, Object o) {
		log(s, o);
	}

	@Override
	public void error(String s, Object o, Object o1) {
		log(s, o, o1);
	}

	@Override
	public void error(String s, Object... objects) {
		log(s, objects);
	}

	@Override
	public void error(String s, Throwable throwable) {
		log(s, throwable);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return true;
	}

	@Override
	public void error(Marker marker, String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void error(Marker marker, String s, Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void error(Marker marker, String s, Object o, Object o1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void error(Marker marker, String s, Object... objects) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void error(Marker marker, String s, Throwable throwable) {
		throw new UnsupportedOperationException();
	}
}
