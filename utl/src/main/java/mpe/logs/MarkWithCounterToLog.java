package mpe.logs;

import mpc.log.LoggerToSystemOut;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class MarkWithCounterToLog implements Logger {

	private Logger log;
	//
	public final MarkWithCounter markWithCounter;
	//

	public MarkWithCounterToLog activateWriteToSystemOut() {
		this.log = new LoggerToSystemOut();
		return this;
	}

	public static MarkWithCounterToLog create(Logger logger, String prefixId) {
		return new MarkWithCounterToLog(logger, prefixId);
	}

	public static MarkWithCounterToLog createSimple(Logger logger, String prefixId) {
		return new MarkWithCounterToLog(logger, prefixId, true);
	}

	public MarkWithCounterToLog(Logger log, String prefixId) {
		this.log = log;
		this.markWithCounter = MarkWithCounter.create(prefixId);
	}

	public MarkWithCounterToLog(Logger log, String prefixId, boolean simple) {
		this.log = log;
		this.markWithCounter = MarkWithCounter.createSimple(prefixId);
	}


	@Override
	public String getName() {
		return log.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public void trace(String s) {
		log.trace(markWithCounter.next(s));
	}

	@Override
	public void trace(String s, Object o) {
		log.trace(markWithCounter.next(s), o);
	}

	@Override
	public void trace(String s, Object o, Object o1) {
		log.trace(markWithCounter.next(s), o, o1);
	}

	@Override
	public void trace(String s, Object... objects) {
		log.trace(markWithCounter.next(s), objects);
	}

	@Override
	public void trace(String s, Throwable throwable) {
		log.trace(markWithCounter.next(s), throwable);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return log.isTraceEnabled(marker);
	}

	@Override
	public void trace(Marker marker, String s) {
		log.trace(marker, markWithCounter.next(s));
	}

	@Override
	public void trace(Marker marker, String s, Object o) {
		log.trace(marker, markWithCounter.next(s), o);
	}

	@Override
	public void trace(Marker marker, String s, Object o, Object o1) {
		log.trace(marker, markWithCounter.next(s), o, o1);
	}

	@Override
	public void trace(Marker marker, String s, Object... objects) {
		log.trace(marker, markWithCounter.next(s), objects);
	}

	@Override
	public void trace(Marker marker, String s, Throwable throwable) {
		log.trace(marker, markWithCounter.next(s), throwable);
	}

	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public void debug(String s) {
		log.debug(markWithCounter.next(s));
	}

	@Override
	public void debug(String s, Object o) {
		log.debug(markWithCounter.next(s), o);
	}

	@Override
	public void debug(String s, Object o, Object o1) {
		log.debug(markWithCounter.next(s), o, o1);
	}

	public void debug(String message, Object... args) {
		message = markWithCounter.next(message);
		log.debug(message, args);
	}

	@Override
	public void debug(String s, Throwable throwable) {
		log.debug(markWithCounter.next(s), throwable);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return log.isDebugEnabled(marker);
	}

	@Override
	public void debug(Marker marker, String s) {
		log.debug(marker, markWithCounter.next(s));
	}

	@Override
	public void debug(Marker marker, String s, Object o) {
		log.debug(marker, markWithCounter.next(s), o);
	}

	@Override
	public void debug(Marker marker, String s, Object o, Object o1) {
		log.debug(marker, markWithCounter.next(s), o, o1);
	}

	@Override
	public void debug(Marker marker, String s, Object... objects) {
		log.debug(marker, markWithCounter.next(s), objects);
	}

	@Override
	public void debug(Marker marker, String s, Throwable throwable) {
		log.debug(marker, markWithCounter.next(s), throwable);
	}

	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	@Override
	public void info(String s) {
		log.info(markWithCounter.next(s));
	}

	@Override
	public void info(String s, Object o) {
		log.info(markWithCounter.next(s), o);
	}

	@Override
	public void info(String s, Object o, Object o1) {
		log.info(markWithCounter.next(s), o, o1);
	}

	public void info(String message, Object... args) {
		message = markWithCounter.next(message);
		log.info(message, args);
	}

	@Override
	public void info(String s, Throwable throwable) {
		log.info(markWithCounter.next(s), throwable);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return log.isInfoEnabled(marker);
	}

	@Override
	public void info(Marker marker, String s) {
		log.info(marker, markWithCounter.next(s));
	}

	@Override
	public void info(Marker marker, String s, Object o) {
		log.info(marker, markWithCounter.next(s), o);
	}

	@Override
	public void info(Marker marker, String s, Object o, Object o1) {
		log.info(marker, markWithCounter.next(s), o, o1);
	}

	@Override
	public void info(Marker marker, String s, Object... objects) {
		log.info(marker, markWithCounter.next(s), objects);
	}

	@Override
	public void info(Marker marker, String s, Throwable throwable) {
		log.info(marker, markWithCounter.next(s), throwable);
	}

	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	@Override
	public void error(String s) {
		log.error(markWithCounter.next(s));
	}

	@Override
	public void error(String s, Object o) {
		log.error(markWithCounter.next(s), o);
	}

	@Override
	public void error(String s, Object o, Object o1) {
		log.error(markWithCounter.next(s), o, o1);
	}

	public void error(String message, Object... args) {
		message = markWithCounter.next(message);
		log.error(message, args);
	}

	@Override
	public void error(String s, Throwable throwable) {
		log.error(markWithCounter.next(s), throwable);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return log.isErrorEnabled(marker);
	}

	@Override
	public void error(Marker marker, String s) {
		log.error(marker, markWithCounter.next(s));

	}

	@Override
	public void error(Marker marker, String s, Object o) {
		log.error(marker, markWithCounter.next(s), o);
	}

	@Override
	public void error(Marker marker, String s, Object o, Object o1) {
		log.error(marker, markWithCounter.next(s), o, o1);
	}

	@Override
	public void error(Marker marker, String s, Object... objects) {
		log.error(marker, markWithCounter.next(s), objects);
	}

	@Override
	public void error(Marker marker, String s, Throwable throwable) {
		log.error(marker, markWithCounter.next(s), throwable);
	}

	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	public void warn(String message) {
		message = markWithCounter.next(message);
		log.warn(message);
	}

	@Override
	public void warn(String s, Object o) {
		log.warn(markWithCounter.next(s), o);

	}

	@Override
	public void warn(String s, Object... objects) {
		log.warn(markWithCounter.next(s), objects);
	}

	@Override
	public void warn(String s, Object o, Object o1) {
		log.warn(markWithCounter.next(s), o1);
	}

	public void warn(String message, Throwable throwable) {
		message = markWithCounter.next(message);
		log.warn(message, throwable);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return log.isWarnEnabled(marker);
	}

	@Override
	public void warn(Marker marker, String s) {
		log.warn(marker, markWithCounter.next(s));

	}

	@Override
	public void warn(Marker marker, String s, Object o) {
		log.warn(marker, markWithCounter.next(s), o);
	}

	@Override
	public void warn(Marker marker, String s, Object o, Object o1) {
		log.warn(marker, markWithCounter.next(s), o, o1);
	}

	@Override
	public void warn(Marker marker, String s, Object... objects) {
		log.warn(marker, markWithCounter.next(s), objects);
	}

	@Override
	public void warn(Marker marker, String s, Throwable throwable) {
		log.warn(marker, markWithCounter.next(s), throwable);
	}

}