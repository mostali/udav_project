package utl_spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProps implements InitializingBean {

	public static final Logger L = LoggerFactory.getLogger(AppProps.class);

	protected static AppProps instance;

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = this;
	}

	public static AppProps get() {
		return instance != null ? null : (instance = new AppProps());
	}

	//	@PostConstruct
	//	public void init() {
	//	}

	@Value("${app.name:defa}")
	public String app_name;

	@Value("${server.port:8080}")
	public int server_port;

	@Override
	public String toString() {
		return "AppProps{" +
				"simpleName='" + app_name + '\'' +
				", serverPort=" + server_port +
				'}';
	}

}
