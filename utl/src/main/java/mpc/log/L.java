package mpc.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import mpc.str.sym.SEP;
import mpu.Sys;
import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class L {

	public static final Logger L = new LoggerToSystemOut();

	public static Logger toFile() {
		return toFile("log.log");
	}

	public static Logger toFile(String filename) {
		return new LoggerToSystemOut(null, Paths.get(filename));
	}

	public static boolean isInfoEnabled() {
		return true;
	}

	public static boolean isTraceEnabled() {
		return true;
	}

	public static void debug(String m, Object... log) {
		p(m, log);
	}

	public static void trace(String m, Object... log) {
		p(m, log);
	}

	public static void info(String m, Object... log) {
		p(m, log);
	}

	public static void warn(String m, Object... log) {
		p(m, log);
	}

	public static void error(String m, Object... log) {
		p(m, log);
	}

	public static void p(String m, Object... log) {
		if (log.length == 0) {
			Sys.p(m);
		} else if (log.length == 1 && log[0] instanceof Throwable) {
			Sys.p(m);
			((Throwable) log[0]).printStackTrace(System.err);
		} else {
			m = m.replace("{}", "%s");
			m = m.contains("%s") ? m : m + "::: %s";
			m = String.format(m, log);
			Sys.p(m);
		}
	}


	public static boolean isDebugEnabled() {
		return true;
	}

	public static boolean isWarnEnabled() {
		return true;
	}

	public static boolean isErrorEnabled() {
		return true;
	}

	public static void setLogLevel(String packageName, String logLevel) {
		setLogLevel(packageName, Level.toLevel(logLevel));
	}

	//https://examples.javacodegeeks.com/enterprise-java/logback/logback-change-log-level-runtime-example/
	//https://mincong.io/en/change-log-level-at-runtime-in-logback/
	public static void setLogLevel(Class clazz, Level logLevel) {
		setLogLevel(clazz.getPackage().getName() + "." + clazz.getSimpleName(), logLevel);
	}

	public static void setLogLevel(String packageName, Level logLevel) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger logger = loggerContext.getLogger(packageName);
		System.out.println("Logger '" + packageName + "', active level BEFORE: " + logger.getLevel());
		logger.setLevel(logLevel);
		System.out.println("Logger '" + packageName + "', active level AFTER: " + logger.getLevel());
	}

	public static void warnBig(Logger logger, String msg, Object... args) {
		logger.warn(SEP.EXMARK.__str2__(X.fl(msg, args)));
	}

	public static void debugOrWarnError(Logger logger, Throwable ex, String msg) {
		if (logger.isDebugEnabled()) {
			logger.debug(msg, ex);
		} else if (logger.isWarnEnabled()) {
			logger.warn(msg + " ( " + ex.getMessage() + " )");
		}
	}
}
