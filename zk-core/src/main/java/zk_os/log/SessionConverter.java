package zk_os.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.zkoss.zk.ui.sys.SessionsCtrl;

import java.lang.reflect.Method;

public class SessionConverter extends ClassicConverter {

	@Override
	public String convert(ILoggingEvent event) {
		if (SessionsCtrl.getCurrent() != null) {
			try {
				Object nativeSession = SessionsCtrl.getCurrent().getNativeSession();
				Method getIdMethod = nativeSession.getClass().getMethod("getId");
				return (String) getIdMethod.invoke(nativeSession);

			} catch (Exception e) {
				return "anonim*";
			}
		}
		return "anonim";
	}
}