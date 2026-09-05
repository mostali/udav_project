package zk_form.notify;

import mpe.core.ERR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class ZkLogger implements Logger {
	public static final Logger L = LoggerFactory.getLogger(ZkLogger.class);

	@Override
	public String getName() {
		return ZkLogger.class.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	@Override
	public void trace(String s) {
		ZKI.log(s);
	}

	@Override
	public void trace(String s, Object o) {
		ZKI.log(s, o);
	}

	@Override
	public void trace(String s, Object o, Object o1) {
		ZKI.log(s, o, o1);
	}

	@Override
	public void trace(String s, Object... objects) {

		ZKI.log(s);
	}

	@Override
	public void trace(String s, Throwable throwable) {
		L.trace(s, throwable);
		ZKI.log("TRACE:" + ERR.getMessageWithType(throwable) + ":" + s);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return true;
	}

	@Override
	public void trace(Marker marker, String s) {
		ZKI.log(s);
	}

	@Override
	public void trace(Marker marker, String s, Object o) {
		ZKI.log(s, o);
	}

	@Override
	public void trace(Marker marker, String s, Object o, Object o1) {
		ZKI.log(s, o, o1);
	}

	@Override
	public void trace(Marker marker, String s, Object... objects) {
		ZKI.log(s, objects);
	}

	@Override
	public void trace(Marker marker, String s, Throwable throwable) {
		L.trace(s, throwable);
		ZKI.log("TRACE:" + ERR.getMessageWithType(throwable) + ":" + s);
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public void debug(String s) {

		ZKI.log(s);
	}

	@Override
	public void debug(String s, Object o) {
		ZKI.log(s, o);
	}

	@Override
	public void debug(String s, Object o, Object o1) {
		ZKI.log(s, o, o1);
	}

	@Override
	public void debug(String s, Object... objects) {
		ZKI.log(s, objects);
	}

	@Override
	public void debug(String s, Throwable throwable) {
		L.debug(s, throwable);
		ZKI.log("DEBUG:" + ERR.getMessageWithType(throwable) + ":" + s);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return true;
	}

	@Override
	public void debug(Marker marker, String s) {

		ZKI.log(s);
	}

	@Override
	public void debug(Marker marker, String s, Object o) {
		ZKI.log(s, o);
	}

	@Override
	public void debug(Marker marker, String s, Object o, Object o1) {
		ZKI.log(s, o, o1);
	}

	@Override
	public void debug(Marker marker, String s, Object... objects) {
		ZKI.log(s, objects);
	}

	@Override
	public void debug(Marker marker, String s, Throwable throwable) {
		L.debug(s, throwable);
		ZKI.log("DEBUG:" + ERR.getMessageWithType(throwable) + ":" + s);
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void info(String s) {

		ZKI.log(s);
	}

	@Override
	public void info(String s, Object o) {
		ZKI.log(s, o);
	}

	@Override
	public void info(String s, Object o, Object o1) {
		ZKI.log(s, o, o1);
	}

	@Override
	public void info(String s, Object... objects) {
		ZKI.log(s, objects);
	}

	@Override
	public void info(String s, Throwable throwable) {
		L.info(s, throwable);
		ZKI.log("INFO:" + ERR.getMessageWithType(throwable) + ":" + s);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return true;
	}

	@Override
	public void info(Marker marker, String s) {

		ZKI.log(s);
	}

	@Override
	public void info(Marker marker, String s, Object o) {
		ZKI.log(s, o);
	}

	@Override
	public void info(Marker marker, String s, Object o, Object o1) {
		ZKI.log(s, o, o1);
	}

	@Override
	public void info(Marker marker, String s, Object... objects) {
		ZKI.log(s, objects);

	}

	@Override
	public void info(Marker marker, String s, Throwable throwable) {
		L.info(s, throwable);
		ZKI.log("INFO:" + ERR.getMessageWithType(throwable) + ":" + s);
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void warn(String s) {

		ZKI.log(s);
	}

	@Override
	public void warn(String s, Object o) {
		ZKI.log(s, o);
	}

	@Override
	public void warn(String s, Object... objects) {
		ZKI.log(s, objects);
	}

	@Override
	public void warn(String s, Object o, Object o1) {
		ZKI.log(s, o, o1);
	}

	@Override
	public void warn(String s, Throwable throwable) {
		L.warn(s, throwable);
		ZKI.log("WARN:" + ERR.getMessageWithType(throwable) + ":" + s);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return true;
	}

	@Override
	public void warn(Marker marker, String s) {

		ZKI.log(s);
	}

	@Override
	public void warn(Marker marker, String s, Object o) {
		ZKI.log(s, o);
	}

	@Override
	public void warn(Marker marker, String s, Object o, Object o1) {
		ZKI.log(s, o, o1);
	}

	@Override
	public void warn(Marker marker, String s, Object... objects) {
		ZKI.log(s, objects);
	}

	@Override
	public void warn(Marker marker, String s, Throwable throwable) {
		L.warn(s, throwable);
		ZKI.log("WARN:" + ERR.getMessageWithType(throwable) + ":" + s);
	}

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public void error(String s) {

		ZKI.log(s);
	}

	@Override
	public void error(String s, Object o) {
		ZKI.log(s, o);
	}

	@Override
	public void error(String s, Object o, Object o1) {
		ZKI.log(s, o, o1);
	}

	@Override
	public void error(String s, Object... objects) {
		ZKI.log(s, objects);
	}

	@Override
	public void error(String s, Throwable throwable) {
		L.error(s, throwable);
		ZKI.log("ERROR:" + ERR.getMessageWithType(throwable) + ":" + s);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return true;
	}

	@Override
	public void error(Marker marker, String s) {
		ZKI.log(s);
	}

	@Override
	public void error(Marker marker, String s, Object o) {
		ZKI.log(s, o);
	}

	@Override
	public void error(Marker marker, String s, Object o, Object o1) {
		ZKI.log(s, o, o1);
	}

	@Override
	public void error(Marker marker, String s, Object... objects) {
		ZKI.log(s, objects);
	}

	@Override
	public void error(Marker marker, String s, Throwable throwable) {
		L.error(s, throwable);
		ZKI.log("ERROR:" + ERR.getMessageWithType(throwable) + ":" + s);
	}
}
