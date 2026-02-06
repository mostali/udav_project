package zk_os.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import mpe.str.CN;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.SessionsCtrl;

import java.lang.reflect.Method;

public class SessionConverter extends ClassicConverter {

	@Override
	public String convert(ILoggingEvent event) {
		Session current = SessionsCtrl.getCurrent();
		if (current != null) {
			try {
				Object nativeSession = SessionsCtrl.getCurrent().getNativeSession();
				String idSess = (String) nativeSession.getClass().getMethod("getId").invoke(nativeSession);
				String ipSess = current.getRemoteAddr();
				return ipSess + "-" + idSess;
			} catch (Exception e) {
				return CN.ANONIM + ":E";
			}
		}
		return CN.ANONIM;
	}
}