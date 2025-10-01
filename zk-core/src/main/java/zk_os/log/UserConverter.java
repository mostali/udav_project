package zk_os.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserConverter extends ClassicConverter {

	@Override
	public String convert(ILoggingEvent event) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			String userName = authentication.getName();
			if (StringUtils.isNotBlank(userName)) {
				return userName;
			}
		}
		return "NO_USER";
	}
}
